package com.mes.admin.modules.system.service;

import com.mes.admin.common.util.IdGenerator;
import com.mes.admin.modules.system.entity.SysJob;
import com.mes.admin.modules.system.entity.SysJobLog;
import com.mes.admin.modules.system.repository.SysJobLogRepository;
import com.mes.admin.modules.system.repository.SysJobRepository;
import com.mes.admin.modules.system.scheduling.CronTaskRegistrar;
import com.mes.admin.modules.system.scheduling.JobExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 定时任务调度服务
 */
@Service
public class SysJobService {

    private static final Logger log = LoggerFactory.getLogger(SysJobService.class);

    @Autowired
    private SysJobRepository jobRepository;

    @Autowired
    private SysJobLogRepository jobLogRepository;

    @Autowired
    private CronTaskRegistrar cronTaskRegistrar;

    @Autowired
    private JobExecution jobExecution;

    /** 查询任务列表 */
    public List<SysJob> list(Map<String, Object> params) {
        try {
            return jobRepository.findAll((Specification<SysJob>) (root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (params != null) {
                    if (params.get("jobName") != null && !"".equals(params.get("jobName"))) {
                        predicates.add(cb.like(root.get("jobName"), "%" + params.get("jobName") + "%"));
                    }
                    if (params.get("jobGroup") != null && !"".equals(params.get("jobGroup"))) {
                        predicates.add(cb.equal(root.get("jobGroup"), params.get("jobGroup")));
                    }
                    if (params.get("status") != null) {
                        predicates.add(cb.equal(root.get("status"), params.get("status")));
                    }
                }
                return cb.and(predicates.toArray(new Predicate[0]));
            });
        } catch (Exception e) {
            log.warn("查询任务列表失败，返回Mock数据：{}", e.getMessage());
            return buildMockJobs(params);
        }
    }

    public SysJob getById(Long id) {
        try {
            return jobRepository.findById(id).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    /** 创建任务 */
    @Transactional
    public SysJob create(SysJob job) {
        // 校验cron表达式
        validateCron(job.getCronExpression());
        // 计算下次执行时间
        Date nextTime = cronTaskRegistrar.getNextExecutionTime(job.getCronExpression());
        job.setNextTime(nextTime);
        if (job.getStatus() == null) {
            job.setStatus(0);
        }
        if (job.getMisfirePolicy() == null) {
            job.setMisfirePolicy(3);
        }
        if (job.getConcurrent() == null) {
            job.setConcurrent(1);
        }
        SysJob saved = jobRepository.save(job);
        // 注册到调度器
        cronTaskRegistrar.addTask(saved);
        log.info("创建定时任务[{}]，id={}", saved.getJobName(), saved.getId());
        return saved;
    }

    /** 更新任务 */
    @Transactional
    public SysJob update(SysJob job) {
        validateCron(job.getCronExpression());
        Date nextTime = cronTaskRegistrar.getNextExecutionTime(job.getCronExpression());
        job.setNextTime(nextTime);
        SysJob saved = jobRepository.save(job);
        // 刷新调度器
        cronTaskRegistrar.updateTask(saved);
        log.info("更新定时任务[{}]，id={}", saved.getJobName(), saved.getId());
        return saved;
    }

    /** 删除任务 */
    @Transactional
    public void delete(Long id) {
        cronTaskRegistrar.removeTask(id);
        jobRepository.deleteById(id);
        log.info("删除定时任务，id={}", id);
    }

    /** 批量删除任务 */
    @Transactional
    public void deleteBatch(List<Long> ids) {
        for (Long id : ids) {
            cronTaskRegistrar.removeTask(id);
        }
        jobRepository.deleteAllById(ids);
    }

    /** 修改任务状态（启动/暂停） */
    @Transactional
    public void changeStatus(Long id, Integer status) {
        SysJob job = jobRepository.findById(id).orElse(null);
        if (job == null) {
            throw new RuntimeException("任务不存在");
        }
        job.setStatus(status);
        if (status == 0) {
            // 启用：重新计算下次执行时间并注册
            Date nextTime = cronTaskRegistrar.getNextExecutionTime(job.getCronExpression());
            job.setNextTime(nextTime);
            cronTaskRegistrar.addTask(job);
        } else {
            // 暂停：移除调度
            cronTaskRegistrar.removeTask(id);
        }
        jobRepository.save(job);
        log.info("修改任务[{}]状态为{}", job.getJobName(), status == 0 ? "启用" : "暂停");
    }

    /** 立即执行一次任务 */
    public void run(Long id) {
        SysJob job = jobRepository.findById(id).orElse(null);
        if (job == null) {
            throw new RuntimeException("任务不存在");
        }
        jobExecution.execute(job);
        log.info("手动执行任务[{}]", job.getJobName());
    }

    // ==================== 任务日志 ====================

    /** 查询任务日志列表 */
    public List<SysJobLog> listLog(Map<String, Object> params) {
        try {
            return jobLogRepository.findAll((Specification<SysJobLog>) (root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (params != null) {
                    if (params.get("jobName") != null && !"".equals(params.get("jobName"))) {
                        predicates.add(cb.like(root.get("jobName"), "%" + params.get("jobName") + "%"));
                    }
                    if (params.get("status") != null) {
                        predicates.add(cb.equal(root.get("status"), params.get("status")));
                    }
                }
                if (query != null) {
                    query.orderBy(cb.desc(root.get("startTime")));
                }
                return cb.and(predicates.toArray(new Predicate[0]));
            });
        } catch (Exception e) {
            log.warn("查询任务日志失败，返回Mock数据：{}", e.getMessage());
            return buildMockJobLogs(params);
        }
    }

    /** 删除任务日志 */
    @Transactional
    public void deleteLog(Long id) {
        jobLogRepository.deleteById(id);
    }

    /** 清空任务日志 */
    @Transactional
    public void cleanLog() {
        jobLogRepository.deleteAll();
    }

    // ==================== 启动加载 ====================

    /**
     * 应用启动时加载所有正常状态的任务到调度器
     */
    public void initOnStartup() {
        try {
            List<SysJob> jobs = jobRepository.findByStatus(0);
            for (SysJob job : jobs) {
                cronTaskRegistrar.addTask(job);
            }
            log.info("启动加载定时任务{}个", jobs.size());
        } catch (Exception e) {
            log.warn("启动加载定时任务失败：{}", e.getMessage());
        }
    }

    // ==================== 校验 ====================

    private void validateCron(String cron) {
        if (cron == null || cron.trim().isEmpty()) {
            throw new RuntimeException("cron表达式不能为空");
        }
        Date nextTime = cronTaskRegistrar.getNextExecutionTime(cron);
        if (nextTime == null) {
            throw new RuntimeException("cron表达式非法：" + cron);
        }
    }

    // ==================== Mock 数据 ====================

    private List<SysJob> buildMockJobs(Map<String, Object> params) {
        List<SysJob> list = new ArrayList<>();
        SysJob j1 = new SysJob();
        j1.setId(1001L);
        j1.setJobName("系统通知");
        j1.setJobGroup("SYSTEM");
        j1.setInvokeTarget("sampleTask.noParams");
        j1.setCronExpression("0/10 * * * * ?");
        j1.setMisfirePolicy(3);
        j1.setConcurrent(1);
        j1.setStatus(0);
        j1.setNextTime(new Date(System.currentTimeMillis() + 10000));
        j1.setCreateTime(new Date());
        list.add(j1);

        SysJob j2 = new SysJob();
        j2.setId(1002L);
        j2.setJobName("带参任务");
        j2.setJobGroup("SYSTEM");
        j2.setInvokeTarget("sampleTask.withParams('测试', 100)");
        j2.setCronExpression("0/30 * * * * ?");
        j2.setMisfirePolicy(3);
        j2.setConcurrent(1);
        j2.setStatus(1);
        j2.setCreateTime(new Date());
        list.add(j2);
        return list;
    }

    private List<SysJobLog> buildMockJobLogs(Map<String, Object> params) {
        List<SysJobLog> list = new ArrayList<>();
        SysJobLog l1 = new SysJobLog();
        l1.setId(IdGenerator.generateId());
        l1.setJobName("系统通知");
        l1.setJobGroup("SYSTEM");
        l1.setInvokeTarget("sampleTask.noParams");
        l1.setJobMessage("任务执行成功");
        l1.setStatus(0);
        l1.setStartTime(new Date(System.currentTimeMillis() - 60000));
        l1.setEndTime(new Date(System.currentTimeMillis() - 59950));
        l1.setCostTime(50L);
        list.add(l1);
        return list;
    }
}
