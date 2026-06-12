package sc.laplace.test.completablefuture.util;


import lombok.experimental.UtilityClass;

import java.util.concurrent.*;
import java.util.function.Function;

/**
 * CompletableFuture 扩展工具
 *
 * @author jxwu
 */
@UtilityClass
public class CompletableFutureExpandUtil {

    /**
     * 如果在给定超时之前未完成，则异常完成此 CompletableFuture 并抛出 {@link TimeoutException} 。
     *
     * @param timeout 在出现 TimeoutException 异常完成之前等待多长时间，以 {@code unit} 为单位
     * @param unit    一个 {@link TimeUnit}，结合 {@code timeout} 参数，表示给定粒度单位的持续时间
     * @return 入参的 CompletableFuture
     */
    public static <T> CompletableFuture<T> orTimeout(CompletableFuture<T> future, long timeout, TimeUnit unit) {
        CompletableFuture<T> timeoutFuture = new CompletableFuture<>();
        Delayer.delay(() -> timeoutFuture.completeExceptionally(new TimeoutException()), timeout, unit);
        return future.applyToEither(timeoutFuture, Function.identity());
    }

    public static <T> CompletableFuture<T> orTimeout(CompletableFuture<T> completableFuture, Future<T> future, long timeout, TimeUnit unit) {
        Delayer.delay(() -> {
            future.cancel(true);
            completableFuture.completeExceptionally(new TimeoutException());
        }, timeout, unit);
        return completableFuture;
    }

    /**
     * 单例延迟调度器，仅用于启动和取消任务，一个线程就足够
     */
    @UtilityClass
    static final class Delayer {
        static final ScheduledThreadPoolExecutor EXECUTOR;

        static {
            EXECUTOR = new ScheduledThreadPoolExecutor(1, new DaemonThreadFactory());
            EXECUTOR.setRemoveOnCancelPolicy(true);
        }

        static ScheduledFuture<?> delay(Runnable command, long delay, TimeUnit unit) {
            return EXECUTOR.schedule(command, delay, unit);
        }

        static final class DaemonThreadFactory implements ThreadFactory {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setDaemon(true);
                t.setName("CompletableFutureExpandUtilsDelayScheduler");
                return t;
            }
        }
    }
}
