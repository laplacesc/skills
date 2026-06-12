package sc.laplace.test.completablefuture;

import lombok.extern.slf4j.Slf4j;
import sc.laplace.test.completablefuture.util.CompletableFutureExpandUtil;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Hello world!
 *
 * @author jxwu
 */
@Slf4j
public class App {


    public static void main(String[] args) {
        run();
        run2();
    }

    public static void run() {
        long start = System.currentTimeMillis();
        CompletableFutureExpandUtil.orTimeout(
                CompletableFuture.supplyAsync(() -> {
                    try {
                        log.info("start");
                        Thread.sleep(2000);
                        log.info("end");
                        return "ok";
                    } catch (InterruptedException e) {
                        log.error("InterruptedException", e);
                        return "error";
                    }
                }), 1, TimeUnit.SECONDS
        ).exceptionally(throwable -> {
            log.error(throwable.getMessage(), throwable);
            return null;
        }).join();
        log.info("cost: {}", System.currentTimeMillis() - start);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }

    public static void run2() {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CompletableFuture<String> future = new CompletableFuture<>();
        long start = System.currentTimeMillis();
        CompletableFutureExpandUtil.orTimeout(future,
                executorService.submit(() -> {
                    try {
                        log.info("start");
                        Thread.sleep(2000);
                        log.info("end");
                        future.complete("ok");
                        return "ok";
                    } catch (InterruptedException e) {
                        log.error("InterruptedException", e);
                        future.complete("error");
                        return "error";
                    }
                }), 1, TimeUnit.SECONDS
        ).exceptionally(throwable -> {
            log.error(throwable.getMessage(), throwable);
            return null;
        }).join();
        log.info("cost: {}", System.currentTimeMillis() - start);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }
}
