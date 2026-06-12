package com.hillstone.simulator.utils;

import com.hillstone.simulator.config.TrustAnyTrustManagerConfig;
import com.hillstone.simulator.config.YamlConfig;

import javax.net.ssl.*;
import java.io.InputStream;
import java.net.Socket;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @author: bohuachen
 * @date: 2023/6/19 18:06
 * @description: some desc
 */
public class HttpUtils {

    private static KeyStore getKeyStore(String password, String type)
            throws Exception {
        InputStream is = HttpUtils.class.getResourceAsStream("/certs/client.p12");
        KeyStore ks = KeyStore.getInstance(type);
        ks.load(is, password.toCharArray());
        is.close();
        return ks;

    }

    /**
     * 云平台模拟器
     *
     * @return
     */
    private static SSLContext getCloudviewSSLContent() {
        //配置，发送https请求时，忽略ssl证书认证（否则会报错没有证书）
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("TLS");

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            KeyStore keyStore = getKeyStore("wangguan", "PKCS12");
            keyManagerFactory.init(keyStore, "wangguan".toCharArray());
            sslContext.init(keyManagerFactory.getKeyManagers(),
                    new TrustManager[]{new TrustAnyTrustManagerConfig()},
                    new SecureRandom());
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return sslContext;
    }

    public static SSLContext getSSLContent() {
        Integer registerType = (Integer) YamlConfig.getProp(YamlConfig.REGISTER_TYPE);
        if (registerType == 1) {
            return getCloudviewSSLContent();
        } else {
            return getLingyanSSLContext();
        }
    }


    public static void main(String[] args) {
        getSSLContent();
    }


    private static SSLContext getLingyanSSLContext() {
        String clientPath = "/certs/lingyan/ai_client.p12";
        String serverPath = "/certs/lingyan/ai_server.p12";
        String password = "bhchen";

        try (InputStream keyStream = HttpUtils.class.getResourceAsStream(clientPath);
             InputStream caStream = HttpUtils.class.getResourceAsStream(serverPath)) {
            KeyStore keyStore = KeyStore.getInstance("pkcs12");
            keyStore.load(keyStream, password.toCharArray());
            KeyStore trustStore = KeyStore.getInstance("pkcs12");
            trustStore.load(caStream, password.toCharArray());

            KeyManagerFactory kmfactory = KeyManagerFactory
                    .getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmfactory.init(keyStore, password.toCharArray());
            final KeyManager[] kms = kmfactory.getKeyManagers();

            final TrustManagerFactory tmfactory = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmfactory.init(trustStore);
            final TrustManager[] tms = tmfactory.getTrustManagers();
            disableDomainValidation(tms);

            SSLContext n = SSLContext.getInstance("TLS");
            n.init(kms, tms, null);
            return n;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 云平台证书中没有域名，所以不关闭域名检测无法正常连接。
     * 仍使用证书，只是不校验域名/IP。
     */
    static void disableDomainValidation(TrustManager[] tms) {
        for (int i = 0; i < tms.length; i++) {
            TrustManager tm = tms[i];
            if (tm instanceof X509TrustManager) {
                tms[i] = new X509ExtendedTrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket) throws CertificateException {
                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine) throws CertificateException {
                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return ((X509TrustManager) tm).getAcceptedIssuers();
                    }
                };
            }
        }
    }

}
