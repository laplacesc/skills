package sc.laplace.test.hillstone.quartz;

import org.quartz.*;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
class QuartzTaskRegistrarController {

    @Resource
    QuartzTaskRegistrar quartzTaskRegistrar;

    @PostMapping("quartz-task/add")
    void addQuartzTask() throws SchedulerException {
        JobDetail jobDetail = JobBuilder.newJob(TestJob.class)
                .usingJobData("name", "isource")
                .usingJobData("type", "black")
                .usingJobData("version", "new")
                .withIdentity("isource-black-new")
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("isource-black-new")
                .withSchedule(CronScheduleBuilder.cronSchedule("0/5 * * * * ?"))
                .startNow()
                .build();

        quartzTaskRegistrar.addQuartzTask(jobDetail, trigger);
    }

    @DeleteMapping("quartz-task/remove")
    void removeQuartzTask(String name) throws SchedulerException {
        quartzTaskRegistrar.removeQuartzTask(name);
    }

    @PutMapping("quartz-task/pause")
    void pauseQuartzTask(String name) throws SchedulerException {
        quartzTaskRegistrar.pauseQuartzTask(name);
    }

    @PutMapping("quartz-task/resume")
    void resumeQuartzTask(String name) throws SchedulerException {
        quartzTaskRegistrar.resumeQuartzTask(name);
    }
}