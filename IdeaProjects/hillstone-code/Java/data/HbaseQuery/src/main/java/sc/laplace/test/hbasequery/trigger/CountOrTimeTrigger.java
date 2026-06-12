package sc.laplace.test.hbasequery.trigger;

import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.triggers.TriggerResult;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

/**
 * @author jxwu
 */
public class CountOrTimeTrigger extends Trigger<String, TimeWindow> {
    private final long maxCount;
    private final long intervalMillis;
    private long count = 0;
    private Long nextFireTime;

    public CountOrTimeTrigger(long maxCount, long intervalMillis) {
        this.maxCount = maxCount;
        this.intervalMillis = intervalMillis;
    }

    @Override
    public TriggerResult onElement(String element, long timestamp, TimeWindow window, TriggerContext ctx) throws Exception {
        count++;
        if (count >= maxCount) {
            count = 0;
            return TriggerResult.FIRE_AND_PURGE;
        }
        long currentProcessingTime = ctx.getCurrentProcessingTime();
        if (nextFireTime == null || nextFireTime <= currentProcessingTime) {
            nextFireTime = currentProcessingTime + intervalMillis;
            ctx.registerProcessingTimeTimer(nextFireTime);
        }
        return TriggerResult.CONTINUE;
    }

    @Override
    public TriggerResult onProcessingTime(long time, TimeWindow window, TriggerContext ctx) throws Exception {
        count = 0;
        nextFireTime = null;
        return TriggerResult.FIRE_AND_PURGE;
    }

    @Override
    public TriggerResult onEventTime(long time, TimeWindow window, TriggerContext ctx) throws Exception {
        return TriggerResult.CONTINUE;
    }

    @Override
    public void clear(TimeWindow window, TriggerContext ctx) throws Exception {
        nextFireTime = null;
    }
}
