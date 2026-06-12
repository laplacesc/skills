package sc.laplace.test.source;

import sc.laplace.test.model.Record;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class PhaseRunner {
    private final int parallelism;

    public PhaseRunner(int parallelism) {
        this.parallelism = parallelism;
    }

    /**
     * 执行一个 phase 内的全部 collection。
     * phase 内允许多 collection 并行，phase 之间的依赖顺序由调用方控制。
     */
    public void run(List<String> collections, String phaseName, CollectionProcessor processor,
                    ArangoSource.RunningFlag runningFlag, org.apache.flink.streaming.api.functions.source.SourceFunction.SourceContext<Record> ctx) {
        if (!runningFlag.isRunning() || collections == null || collections.isEmpty()) {
            return;
        }
        // worker 数量不超过 collection 数量，避免为一次性迁移任务创建无意义线程。
        int workerCount = Math.max(1, Math.min(parallelism, collections.size()));
        ExecutorService executor = Executors.newFixedThreadPool(workerCount);
        Object checkpointLock = ctx.getCheckpointLock();
        List<Future<Void>> futures = new ArrayList<Future<Void>>(collections.size());
        try {
            for (String collection : collections) {
                if (!runningFlag.isRunning()) {
                    break;
                }
                // 每个 collection 独占一个任务，内部分页顺序由 queryExecutor 自己维护。
                futures.add(executor.submit(new CollectionTask(collection, processor, ctx, checkpointLock)));
            }
            waitForPhase(phaseName, futures, runningFlag);
        } finally {
            executor.shutdownNow();
        }
    }

    private void waitForPhase(String phaseName, List<Future<Void>> futures, ArangoSource.RunningFlag runningFlag) {
        for (Future<Void> future : futures) {
            if (!runningFlag.isRunning()) {
                return;
            }
            try {
                // 任一 collection 失败时直接终止整个 phase，让上层统一走重试/恢复逻辑。
                future.get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while waiting for " + phaseName + " tasks", e);
            } catch (ExecutionException e) {
                throw new RuntimeException("Failed during " + phaseName + " phase", e.getCause());
            }
        }
    }

    /**
     * 单个 collection 的执行单元，由调用方决定如何查询和如何向 SourceContext 发射数据。
     */
    public interface CollectionProcessor {
        void process(String collection, org.apache.flink.streaming.api.functions.source.SourceFunction.SourceContext<Record> ctx, Object checkpointLock);
    }

    /**
     * collection 级任务壳，负责把并发调度和真正业务处理分离开。
     */
    private static class CollectionTask implements Callable<Void> {
        private final String collection;
        private final CollectionProcessor processor;
        private final org.apache.flink.streaming.api.functions.source.SourceFunction.SourceContext<Record> ctx;
        private final Object checkpointLock;

        private CollectionTask(String collection, CollectionProcessor processor,
                               org.apache.flink.streaming.api.functions.source.SourceFunction.SourceContext<Record> ctx,
                               Object checkpointLock) {
            this.collection = collection;
            this.processor = processor;
            this.ctx = ctx;
            this.checkpointLock = checkpointLock;
        }

        @Override
        public Void call() {
            // processor 内部会在 checkpointLock 上同步发射数据，这里只负责隔离 collection 级并发。
            processor.process(collection, ctx, checkpointLock);
            return null;
        }
    }
}
