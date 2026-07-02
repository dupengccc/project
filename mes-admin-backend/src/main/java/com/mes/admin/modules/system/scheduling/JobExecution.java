package com.mes.admin.modules.system.scheduling;

import com.mes.admin.common.util.IdGenerator;
import com.mes.admin.modules.system.entity.SysJob;
import com.mes.admin.modules.system.entity.SysJobLog;
import com.mes.admin.modules.system.repository.SysJobLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 定时任务执行入口（被 CronTaskRegistrar 通过反射调度）
 * <p>
 * 由调度线程触发，完成「调用业务 Bean 方法 + 记录执行日志」。
 */
@Component("jobExecution")
public class JobExecution {

    private static final Logger log = LoggerFactory.getLogger(JobExecution.class);

    @Autowired
    private SysJobLogRepository jobLogRepository;

    /**
     * 执行任务
     *
     * @param sysJob 任务配置
     */
    public void execute(SysJob sysJob) {
        if (sysJob == null) {
            return;
        }
        Date startTime = new Date();
        long start = System.currentTimeMillis();
        SysJobLog jobLog = new SysJobLog();
        jobLog.setId(IdGenerator.generateId());
        jobLog.setJobName(sysJob.getJobName());
        jobLog.setJobGroup(sysJob.getJobGroup());
        jobLog.setInvokeTarget(sysJob.getInvokeTarget());
        jobLog.setStartTime(startTime);

        try {
            // 反射调用目标方法
            JobInvokeUtil.invokeMethod(sysJob.getInvokeTarget());
            long cost = System.currentTimeMillis() - start;
            jobLog.setJobMessage("任务执行成功");
            jobLog.setStatus(0);
            jobLog.setCostTime(cost);
            jobLog.setEndTime(new Date());
            log.info("定时任务[{}]执行成功，耗时{}ms", sysJob.getJobName(), cost);
        } catch (Exception e) {
            long cost = System.currentTimeMillis() - start;
            log.error("定时任务[{}]执行失败", sysJob.getJobName(), e);
            jobLog.setJobMessage("任务执行失败：" + e.getMessage());
            jobLog.setStatus(1);
            jobLog.setExceptionInfo(getStackTrace(e));
            jobLog.setCostTime(cost);
            jobLog.setEndTime(new Date());
        }

        // 保存日志（数据库不可用时忽略，不影响调度主流程）
        try {
            jobLogRepository.save(jobLog);
        } catch (Exception ex) {
            log.warn("保存任务日志失败：{}", ex.getMessage());
        }
    }

    /** 获取异常堆栈字符串 */
    private String getStackTrace(Throwable e) {
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.PrintWriter pw = new java.io.PrintWriter(sw);
        e.printStackTrace(pw);
        String result = sw.toString();
        // 限制长度，避免日志字段过长
        if (result.length() > 2000) {
            return result.substring(0, 2000);
        }
        return result;
    }
}
