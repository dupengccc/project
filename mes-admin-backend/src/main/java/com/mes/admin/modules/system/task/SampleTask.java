package com.mes.admin.modules.system.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 示例定时任务（演示调用目标的写法）
 * <p>
 * 在页面配置调用目标时使用：
 * <ul>
 *   <li>sampleTask.noParams</li>
 *   <li>sampleTask.withParams('内容', 100)</li>
 * </ul>
 */
@Component("sampleTask")
public class SampleTask {

    private static final Logger log = LoggerFactory.getLogger(SampleTask.class);

    /** 无参任务 */
    public void noParams() {
        log.info("执行无参定时任务：系统巡检完成");
    }

    /** 带参任务 */
    public void withParams(String content, Integer count) {
        log.info("执行带参定时任务：content={}, count={}", content, count);
    }

    /** 字符串单参任务 */
    public void singleParam(String msg) {
        log.info("执行单参定时任务：msg={}", msg);
    }
}
