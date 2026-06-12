package sc.laplace.test.completablefuture;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * @author jxwu
 */
@Slf4j
public class ExecutorServiceTest {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = new ThreadPoolExecutor(
                0, 20, 1L, TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(10),
                new ThreadFactoryBuilder().setNameFormat("hq-data-output-%d").build(),
                (r, e) -> {
                    // 自定义拒绝策略：阻塞等待
                    try {
                        e.getQueue().put(r);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                });

        Runnable runnable = () -> {
            try {
                log.info("name: {}", Thread.currentThread().getName());
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
                Thread.currentThread().interrupt();
            }
        };

        for (int i = 0; i < 50; i++) {
            log.info("id: {}", i);
            CompletableFuture.runAsync(runnable, executorService);
        }
    }
}
