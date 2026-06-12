package sc.laplace.test.hillstone.quartz;

import org.quartz.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author jxwu
 */
@Component
public class QuartzTaskRegistrar {

    @Resource
    private Scheduler quartzScheduler;

    public void addQuartzTask(JobDetail jobDetail, Trigger trigger) throws SchedulerException {
        quartzScheduler.scheduleJob(jobDetail, trigger);
    }

    public void removeQuartzTask(String name) throws SchedulerException {
        quartzScheduler.pauseTrigger(TriggerKey.triggerKey(name));
        quartzScheduler.unscheduleJob(TriggerKey.triggerKey(name));
        quartzScheduler.deleteJob(JobKey.jobKey(name));
    }

    public void pauseQuartzTask(String name) throws SchedulerException {
        quartzScheduler.pauseJob(JobKey.jobKey(name));
    }

    public void resumeQuartzTask(String name) throws SchedulerException {
        quartzScheduler.resumeJob(JobKey.jobKey(name));
    }
}
