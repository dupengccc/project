package com.mes.admin.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 全局ID生成器
 * 生成规则：17位时间戳(yyyyMMddHHmmssSSS) + 3位自增序号 = 共20位
 * 例如：20260618153012123001
 *
 * 保证：
 * 1. 同一毫秒内不同实体不会冲突（通过 AtomicInteger 自增）
 * 2. 自增序号超过 999 时自动重置，等待下一毫秒
 * 3. 多线程安全（AtomicInteger + synchronized 双重保障）
 */
public class IdGenerator {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    private static final AtomicInteger SEQ = new AtomicInteger(0);
    private static final int SEQ_MAX = 999;
    private static long lastTimestamp = 0;

    /**
     * 生成20位数字ID
     * @return Long 类型的ID
     */
    public static synchronized Long generateId() {
        long timestamp = System.currentTimeMillis();
        // 时间戳回拨了，强制使用最新时间
        if (timestamp < lastTimestamp) {
            timestamp = lastTimestamp;
        }
        // 同一毫秒内自增
        if (timestamp == lastTimestamp) {
            int seq = SEQ.incrementAndGet();
            if (seq > SEQ_MAX) {
                // 序号用尽，等待下一毫秒
                while (System.currentTimeMillis() <= lastTimestamp) {
                    // busy wait
                }
                timestamp = System.currentTimeMillis();
                SEQ.set(0);
            }
        } else {
            // 新的毫秒，重置序号
            SEQ.set(0);
        }
        lastTimestamp = timestamp;

        String ts = SDF.format(new Date(timestamp)); // 17位
        String seqStr = String.format("%03d", SEQ.get()); // 3位
        return Long.parseLong(ts + seqStr);
    }
}
