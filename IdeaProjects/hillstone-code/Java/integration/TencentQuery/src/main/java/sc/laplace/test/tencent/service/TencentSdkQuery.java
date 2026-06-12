package sc.laplace.test.tencent.service;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import sc.laplace.test.tencent.constant.SdkPackageMod;
import sc.laplace.test.tencent.constant.SdkQueryMod;
import sc.laplace.test.tencent.model.SdkPackageDownloadParam;
import sc.laplace.test.tencent.model.TxLicenseInfo;
import sc.laplace.test.tencent.model.TxSdkData;
import sc.laplace.test.tencent.util.JsonUtil;
import sc.laplace.test.tencent.util.TxSdkUtil;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author jxwu
 */
@Slf4j
@Service
public class TencentSdkQuery {

    private TxLicenseInfo txLicenseInfo;
    @Resource
    private RestTemplate restTemplate;
    private boolean initialized = false;

    @PostConstruct
    public void sdkInit() {
        authorizeDownload(
                Paths.get("sdk_pro/data/authorize"),
                TxSdkUtil.INSTANCE.TxGetDeviceFingerprint(),
                txLicenseInfo = getTxLicenseInfo(
                        Paths.get("conf/sdk_pro").resolve("license").resolve("PcMgrLicense-NP22-1R33-2XTY-T18O.qcer")));
        initSdk();
        initMetaAndInfo();
    }

    public void upGrade(String mod) {
        // 目前只有全量更新未提供增量更新包
        sdkUpGrade(
                Paths.get("sdk_pro/data/download")
                        .resolve(new SimpleDateFormat("yyyyMMddHHmmss").format(Date.from(Instant.now()))),
                txLicenseInfo,
                SdkPackageMod.valueOf(mod));
        // 防止第一次启动时无数据初始化失败，判断是否需要再次调用初始化meta和info
        initMetaAndInfo();
    }

    public void initSdk() {
        int sdkInit = TxSdkUtil.INSTANCE.TxSdkInit(
                Paths.get("conf/sdk_pro").resolve("license").resolve("PcMgrLicense-NP22-1R33-2XTY-T18O.qcer")
                        .toString(),
                Paths.get("conf/sdk_pro").resolve("config").resolve("config.yaml").toString());
        log.info("init skd: {}", sdkInit);
    }

    public void initMetaAndInfo() {
        if (initialized) {
            log.info("sdk already initialized.");
            return;
        }
        int initMetaData = TxSdkUtil.INSTANCE.TxInitMetaData();
        int initInfoData = TxSdkUtil.INSTANCE.TxInitInfoData();
        if (initMetaData == 0 && initInfoData == 0) {
            initialized = true;
        }
        log.info("init meta: {}, init info: {}, sdk version: {}", initMetaData, initInfoData,
                TxSdkUtil.INSTANCE.TxSdkVersion());
    }

    public Object query(String key, String type) {
        List<TxSdkData> txSdkDataList = TxSdkUtil.IOC_TYPE_2_QUERY_TYPE.get(type)
                .parallelStream()
                .map(queryType -> {
                    // 推荐2048以上
                    byte[] dRes = new byte[4096];
                    TxSdkUtil.INSTANCE.TxSdkDetector(
                            TxSdkUtil.QUERY_TYPE_MAP.get(queryType),
                            key,
                            String.valueOf(SdkQueryMod.ANALYTICAL.ordinal()),
                            dRes,
                            dRes.length);
                    TxSdkData txSdkData = JsonUtil.toObject(dRes, new TypeReference<TxSdkData>() {
                    });
                    if (null != txSdkData) {
                        txSdkData.setKey(key);
                        txSdkData.setQueryType(queryType);
                    }
                    log.info("ioc: {}, type: {}, queryType: {}, result: {}", key, type, queryType,
                            JsonUtil.toJson(txSdkData));
                    return txSdkData;
                })
                .filter(txSdkData -> null != txSdkData && txSdkData.getCode() == 0)
                .collect(Collectors.toList());
        return txSdkDataList.isEmpty() ? null : txSdkDataList.get(0);
    }

    public TxLicenseInfo getTxLicenseInfo(Path licensePath) {
        String licenseInfo = TxSdkUtil.INSTANCE.TxLicenseInfo(licensePath.toString());
        log.info("license info: {}", licenseInfo);
        return JsonUtil.toObject(licenseInfo, new TypeReference<TxLicenseInfo>() {
        });
    }

    public void authorizeDownload(Path authorizeDirPath, String equipId, TxLicenseInfo txLicenseInfo) {
        if (Files.exists(authorizeDirPath.resolve(equipId))) {
            log.info("authorize file exists, skip download.");
            return;
        }
        restTemplate.execute(
                UriComponentsBuilder
                        .fromUri(URI.create("https://eti.qq.com/sdk/authorize/activationCode"))
                        .queryParam("equip_id", equipId)
                        .queryParam("appkey", txLicenseInfo.getAppkey())
                        .queryParam("expire_time ", txLicenseInfo.getExprieTime())
                        .build()
                        .toUri(),
                HttpMethod.GET,
                request -> request.getHeaders()
                        .setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM)),
                clientHttpResponse -> {
                    String filename = clientHttpResponse.getHeaders().getContentDisposition().getFilename();
                    if (null == filename) {
                        log.error("authorize download failed. response: {}", JsonUtil.toJson(JsonUtil
                                .toObject(clientHttpResponse.getBody(), new TypeReference<Map<Object, Object>>() {
                                })));
                        return null;
                    }
                    FileUtils.forceMkdir(authorizeDirPath.toFile());
                    FileUtils.deleteQuietly(authorizeDirPath.resolve(filename).toFile());
                    Files.copy(clientHttpResponse.getBody(), authorizeDirPath.resolve(filename));
                    log.info("authorize download success. path: {}", authorizeDirPath.resolve(filename));
                    return authorizeDirPath.resolve(filename);
                });
    }

    @SuppressWarnings("unchecked")
    public void sdkUpGrade(Path dbDownloadDirPath, TxLicenseInfo txLicenseInfo, SdkPackageMod packageMod) {
        log.info("sdk up grade start, sdk version: {}", TxSdkUtil.INSTANCE.TxSdkVersion());
        TencentSdkQuery tencentSdkQuery = (TencentSdkQuery) AopContext.currentProxy();
        CompletableFuture<Path>[] array = txLicenseInfo.getThreatPackageType()
                .stream()
                .filter(TxSdkUtil.PACKAGE_TYPE_MAP::containsKey)
                .map(packageType -> SdkPackageDownloadParam.builder()
                        .appkey(txLicenseInfo.getAppkey())
                        .packageMod(packageMod)
                        .packageTypeString(packageType)
                        .packageType(TxSdkUtil.PACKAGE_TYPE_MAP.get(packageType))
                        .reqType("api")
                        .isRocksdb(packageType.startsWith("META") ? 0 : 1)
                        .build())
                .map(param -> CompletableFuture.supplyAsync(() -> tencentSdkQuery.sdkDownload(dbDownloadDirPath, param))
                        .thenAccept(path -> {
                            if (null == path) {
                                return;
                            }
                            int code = TxSdkUtil.INSTANCE.TxDataUpGrade(path.toString(), param.getPackageMod().name());
                            log.info("sdk data upgrade. path: {}, code: {}", path, code);
                        })
                        .exceptionally(throwable -> {
                            log.error("sdk data upgrade failed. param: {}, error: {}", JsonUtil.toJson(param),
                                    throwable.getMessage(), throwable);
                            return null;
                        }))
                .toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(array).join();
        FileUtils.deleteQuietly(dbDownloadDirPath.toFile());
        log.info("sdk up grade finished, sdk version: {}", TxSdkUtil.INSTANCE.TxSdkVersion());
    }

    @Retryable(value = Exception.class, // 需重试的异常
            maxAttempts = 3, // 最大重试次数
            backoff = @Backoff(delay = 1000, multiplier = 2) // 退避策略：初始延迟1秒，倍数递增
    )
    public Path sdkDownload(Path dbDownloadDirPath, SdkPackageDownloadParam param) {
        return restTemplate.execute(
                UriComponentsBuilder
                        .fromUri(URI.create("https://eti.qq.com/sdk/package/download"))
                        .queryParam("appkey", param.getAppkey())
                        .queryParam("package_mod", param.getPackageMod().ordinal())
                        .queryParam("package_type", param.getPackageType())
                        .queryParam("req_type ", param.getReqType())
                        .queryParam("is_rocksdb ", param.getIsRocksdb())
                        .build()
                        .toUri(),
                HttpMethod.GET,
                request -> request.getHeaders()
                        .setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM)),
                clientHttpResponse -> {
                    String filename = clientHttpResponse.getHeaders().getContentDisposition().getFilename();
                    if (null == filename) {
                        log.error("sdk download failed. param: {}, response: {}", JsonUtil.toJson(param),
                                JsonUtil.toJson(JsonUtil.toObject(clientHttpResponse.getBody(),
                                        new TypeReference<Map<Object, Object>>() {
                                        })));
                        return null;
                    }
                    FileUtils.forceMkdir(dbDownloadDirPath.toFile());
                    FileUtils.deleteQuietly(dbDownloadDirPath.resolve(filename).toFile());
                    Files.copy(clientHttpResponse.getBody(), dbDownloadDirPath.resolve(filename));
                    log.info("sdk download success. param: {}, path: {}", JsonUtil.toJson(param),
                            dbDownloadDirPath.resolve(filename));
                    return dbDownloadDirPath.resolve(filename);
                });
    }
}
