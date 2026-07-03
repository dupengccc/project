package com.mes.admin.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 全局ID生成器
 * 生成规则：14位时间戳(yyyyMMddHHmmss) + 4位自增序号 = 共18位
 * 例如：202606181530120001
 *
 * 保证：
 * 1. 同一秒内不同实体不会冲突（通过 AtomicInteger 自增）
 * 2. 自增序号超过 9999 时自动重置，等待下一秒
 * 3. 多线程安全（AtomicInteger + synchronized 双重保障）
 * 4. 18位数字不超过 Long.MAX_VALUE（9223372036854775807，19位）
 */
public class IdGenerator {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMddHHmmss");
    private static final AtomicInteger SEQ = new AtomicInteger(0);
    private static final int SEQ_MAX = 9999;
    private static long lastSecond = 0;

    /**
     * 生成18位数字ID
     * @return Long 类型的ID
     */
    public static synchronized Long generateId() {
        long currentTime = System.currentTimeMillis();
        long currentSecond = currentTime / 1000;

        // 时间回拨，强制使用最新时间
        if (currentSecond < lastSecond) {
            currentSecond = lastSecond;
        }

        // 同一秒内自增
        if (currentSecond == lastSecond) {
            int seq = SEQ.incrementAndGet();
            if (seq > SEQ_MAX) {
                // 序号用尽，等待下一秒
                while (System.currentTimeMillis() / 1000 <= lastSecond) {
                    // busy wait
                }
                currentSecond = System.currentTimeMillis() / 1000;
                SEQ.set(0);
            }
        } else {
            // 新的秒，重置序号
            SEQ.set(0);
        }
        lastSecond = currentSecond;

        String ts = SDF.format(new Date(currentSecond * 1000)); // 14位
        String seqStr = String.format("%04d", SEQ.get()); // 4位
        return Long.parseLong(ts + seqStr);
    }
}
