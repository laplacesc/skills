package sc.laplace.test.hillstone.shell;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * ShellUtil
 * <p>
 * 2023/03/21 17:14:07
 *
 * @author jxwu
 */
@SuppressWarnings("Duplicated")
public class ShellUtil {
    private static final Logger log = LoggerFactory.getLogger(ShellUtil.class);

    private ShellUtil() {
        // no-op
    }

    /**
     * 同时支持调用shell文件和shell命令
     *
     * @param shellPath shell path
     * @param args      args
     * @return output
     */
    public static List<String> executeShell(String shellPath, String... args) {
        return executeShell(true, shellPath, args);
    }

    /**
     * shell脚本或命令执行
     *
     * @param needResult 需要结果则不打印中间输出日志，否则返回空结果，打印中间日志
     * @param shellPath  文件绝对路径或shell命令
     * @param args       参数列表
     * @return 执行结果
     */
    public static List<String> executeShell(boolean needResult, String shellPath, String... args) {
        try {
            // 构造command list
            List<String> list = Arrays.stream(args).collect(Collectors.toList());
            list.add(0, shellPath);
            // 如果是文件则将文件权限设为可执行，同时加上/bin/sh
            Optional.of(new File(shellPath)).filter(f -> f.exists() && f.isFile()).ifPresent(f -> {
                log.info("set shell file {} executable {}", shellPath, f.setExecutable(true));
                list.add(0, "/bin/sh");
            });
            // 构造ProcessBuilder，开始执行
            ProcessBuilder pb = new ProcessBuilder(list);
            pb.redirectErrorStream(true);
            log.info("execute shell, command: {}", pb.command());
            Process process = pb.start();
            // 读取控制台输出并存入列表
            List<String> result = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (needResult) {
                        result.add(line);
                    } else {
                        log.info(line);
                    }
                }
            }
            // 等待执行完成，执行失败则报错
            Optional.of(process.waitFor()).filter(code -> code != 0).ifPresent(code -> {
                log.error("execute shell failed, command: {}, code: {}, error: {}", pb.command(), code, result);
                throw new RuntimeException();
            });
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread.interrupted(): {}", Thread.interrupted());
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
