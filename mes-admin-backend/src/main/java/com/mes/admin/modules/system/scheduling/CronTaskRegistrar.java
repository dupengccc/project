package com.mes.admin.modules.system.scheduling;

import com.mes.admin.modules.system.entity.SysJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * 定时任务调度管理器
 * <p>
 * 基于线程池动态注册/取消 Cron 任务，支持运行时增删改。
 */
@Component
public class CronTaskRegistrar {

    private static final Logger log = LoggerFactory.getLogger(CronTaskRegistrar.class);

    /** 任务调度线程池 */
    private final ThreadPoolTaskScheduler taskScheduler;

    /** 已注册任务缓存：key = 任务ID */
    private final Map<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    @Autowired
    private JobExecution jobExecution;

    public CronTaskRegistrar() {
        this.taskScheduler = new ThreadPoolTaskScheduler();
        this.taskScheduler.setPoolSize(10);
        this.taskScheduler.setThreadNamePrefix("job-scheduler-");
        this.taskScheduler.setWaitForTasksToCompleteOnShutdown(true);
        this.taskScheduler.setAwaitTerminationSeconds(30);
        this.taskScheduler.initialize();
    }

    /** 添加任务 */
    public void addTask(SysJob job) {
        if (job == null || job.getId() == null) {
            return;
        }
        if (scheduledTasks.containsKey(job.getId())) {
            log.warn("任务[{}]已存在，先移除旧任务", job.getJobName());
            removeTask(job.getId());
        }
        scheduleTask(job);
    }

    /** 移除任务 */
    public void removeTask(Long jobId) {
        ScheduledFuture<?> future = scheduledTasks.remove(jobId);
        if (future != null) {
            future.cancel(false);
            log.info("移除定时任务：jobId={}", jobId);
        }
    }

    /** 更新任务（先移除后添加） */
    public void updateTask(SysJob job) {
        removeTask(job.getId());
        addTask(job);
    }

    /** 获取下次执行时间（根据cron表达式） */
    public Date getNextExecutionTime(String cronExpression) {
        try {
            CronTrigger trigger = new CronTrigger(cronExpression);
            SimpleTriggerContext context = new SimpleTriggerContext();
            return trigger.nextExecutionTime(context);
        } catch (IllegalArgumentException e) {
            log.error("cron表达式非法：{}", cronExpression, e);
            return null;
        }
    }

    /** 注册任务（仅状态为正常的任务才会调度） */
    private void scheduleTask(SysJob job) {
        if (job.getStatus() != null && job.getStatus() == 1) {
            // 暂停状态不调度
            log.info("任务[{}]处于暂停状态，跳过调度", job.getJobName());
            return;
        }
        try {
            CronTrigger trigger = new CronTrigger(job.getCronExpression());
            ScheduledFuture<?> future = taskScheduler.schedule(() -> {
                try {
                    jobExecution.execute(job);
                } catch (Exception e) {
                    log.error("任务[{}]调度异常", job.getJobName(), e);
                }
            }, trigger);
            scheduledTasks.put(job.getId(), future);
            log.info("注册定时任务[{}]，cron={}", job.getJobName(), job.getCronExpression());
        } catch (IllegalArgumentException e) {
            log.error("cron表达式非法，任务[{}]注册失败：{}", job.getJobName(), job.getCronExpression(), e);
        }
    }

    /** 销毁时关闭线程池 */
    @PreDestroy
    public void destroy() {
        for (ScheduledFuture<?> future : scheduledTasks.values()) {
            future.cancel(false);
        }
        scheduledTasks.clear();
        if (taskScheduler != null) {
            taskScheduler.shutdown();
        }
    }
}
