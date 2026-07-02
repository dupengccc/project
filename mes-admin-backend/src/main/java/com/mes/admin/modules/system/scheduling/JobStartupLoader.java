package com.mes.admin.modules.system.scheduling;

import com.mes.admin.modules.system.service.SysJobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 应用就绪后加载定时任务到调度器
 */
@Component
public class JobStartupLoader {

    private static final Logger log = LoggerFactory.getLogger(JobStartupLoader.class);

    @Autowired
    private SysJobService jobService;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        try {
            jobService.initOnStartup();
        } catch (Exception e) {
            log.warn("启动加载定时任务失败：{}", e.getMessage());
        }
    }
}
