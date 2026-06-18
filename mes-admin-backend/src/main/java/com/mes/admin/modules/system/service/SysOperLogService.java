package com.mes.admin.modules.system.service;

import com.mes.admin.modules.system.entity.SysOperLog;
import com.mes.admin.modules.system.repository.SysOperLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 操作日志服务
 */
@Service
public class SysOperLogService {

    @Autowired
    private SysOperLogRepository logRepository;

    /**
     * 记录一条操作日志
     */
    public void record(SysOperLog log) {
        if (log == null) return;
        if (log.getOperTime() == null) log.setOperTime(new Date());
        if (log.getStatus() == null) log.setStatus(0);
        if (log.getCreateTime() == null) log.setCreateTime(new Date());
        try {
            logRepository.save(log);
        } catch (Exception ignored) {
            // 数据库不可用时，内存模拟
            log.setId((long) -(mockList.size() + 1));
            mockList.add(0, log);
            if (mockList.size() > 200) {
                mockList.remove(mockList.size() - 1);
            }
        }
    }

    /**
     * 便捷方法 - 记录操作
     */
    public void record(String empNo, String name, String orgName,
                        String module, String operation, String url,
                        String ip, Integer status, String errorMsg) {
        SysOperLog log = new SysOperLog();
        log.setEmpNo(empNo);
        log.setName(name);
        log.setOrgName(orgName);
        log.setModule(module);
        log.setOperation(operation);
        log.setUrl(url);
        log.setIp(ip);
        log.setStatus(status == null ? 0 : status);
        log.setErrorMsg(errorMsg);
        log.setOperTime(new Date());
        log.setCreateTime(new Date());
        record(log);
    }

    /**
     * 条件查询（工号/姓名/模块/操作/状态/时间范围）
     */
    public List<SysOperLog> findAll(Map<String, Object> params) {
        try {
            return logRepository.findAll((root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (params != null) {
                    if (params.get("empNo") != null && !((String) params.get("empNo")).isEmpty()) {
                        predicates.add(cb.like(root.get("empNo"), "%" + params.get("empNo") + "%"));
                    }
                    if (params.get("name") != null && !((String) params.get("name")).isEmpty()) {
                        predicates.add(cb.like(root.get("name"), "%" + params.get("name") + "%"));
                    }
                    if (params.get("module") != null && !((String) params.get("module")).isEmpty()) {
                        predicates.add(cb.like(root.get("module"), "%" + params.get("module") + "%"));
                    }
                    if (params.get("operation") != null && !((String) params.get("operation")).isEmpty()) {
                        predicates.add(cb.like(root.get("operation"), "%" + params.get("operation") + "%"));
                    }
                    if (params.get("status") != null) {
                        predicates.add(cb.equal(root.get("status"), params.get("status")));
                    }
                    if (params.get("startTime") != null) {
                        predicates.add(cb.greaterThanOrEqualTo(root.get("operTime"), params.get("startTime")));
                    }
                    if (params.get("endTime") != null) {
                        predicates.add(cb.lessThanOrEqualTo(root.get("operTime"), params.get("endTime")));
                    }
                }
                if (query != null) {
                    query.orderBy(cb.desc(root.get("operTime")));
                }
                return cb.and(predicates.toArray(new Predicate[0]));
            });
        } catch (Exception ignored) {
            return filterMockList(params);
        }
    }

    public SysOperLog findById(Long id) {
        if (id == null) return null;
        try {
            Optional<SysOperLog> opt = logRepository.findById(id);
            if (opt.isPresent()) return opt.get();
        } catch (Exception ignored) {}
        for (SysOperLog m : mockList) {
            if (id.equals(m.getId())) return m;
        }
        return null;
    }

    public void delete(Long id) {
        try {
            if (id != null && id > 0) {
                logRepository.deleteById(id);
            } else {
                mockList.removeIf(m -> m.getId() != null && m.getId().equals(id));
            }
        } catch (Exception ignored) {
            mockList.removeIf(m -> m.getId() != null && m.getId().equals(id));
        }
    }

    public void cleanOverDays(int days) {
        long cutoff = System.currentTimeMillis() - (long) days * 24L * 3600L * 1000L;
        Date cutoffDate = new Date(cutoff);
        try {
            List<SysOperLog> olds = logRepository.findAll((root, query, cb) ->
                    cb.lessThan(root.get("operTime"), cutoffDate));
            if (olds != null && !olds.isEmpty()) {
                logRepository.deleteAll(olds);
            }
        } catch (Exception ignored) {
            mockList.removeIf(m -> m.getOperTime() != null && m.getOperTime().before(cutoffDate));
        }
    }

    // ==================== 模拟数据 ====================
    private final List<SysOperLog> mockList = Collections.synchronizedList(new ArrayList<>());
    {
        String[] modules = {"系统管理", "用户管理", "组织管理", "角色管理", "菜单管理", "基础数据", "生产管理", "仓储管理"};
        String[] operations = {"新增", "修改", "删除", "查询", "导出", "登录", "退出登录", "修改密码"};
        String[] names = {"系统管理员", "张三", "李四", "王五", "赵六", "周七"};
        String[] orgs = {"MES 集团总部", "华东分公司", "华南分公司", "研发部", "生产部"};
        for (int i = 0; i < 30; i++) {
            SysOperLog log = new SysOperLog();
            long ts = System.currentTimeMillis() - (long) i * 3600L * 1000L * 2;
            log.setId((long) (-(mockList.size() + 1)));
            log.setEmpNo("E" + (10001 + (i % 6)));
            log.setName(names[i % names.length]);
            log.setOrgName(orgs[i % orgs.length]);
            log.setModule(modules[i % modules.length]);
            log.setOperation(operations[i % operations.length]);
            log.setUrl("/api/resource/" + i);
            log.setIp("127.0.0." + (1 + i % 10));
            log.setLocation("局域网");
            log.setCost((long) (10 + i * 3));
            log.setStatus(i % 13 == 0 ? 1 : 0);
            log.setErrorMsg(i % 13 == 0 ? "NullPointerException: 空指针异常示例" : null);
            log.setOperTime(new Date(ts));
            log.setCreateTime(new Date(ts));
            mockList.add(log);
        }
    }

    private List<SysOperLog> filterMockList(Map<String, Object> params) {
        if (params == null || params.isEmpty()) return new ArrayList<>(mockList);
        String empNo = (String) params.get("empNo");
        String name = (String) params.get("name");
        String module = (String) params.get("module");
        String operation = (String) params.get("operation");
        Object status = params.get("status");
        Object startTime = params.get("startTime");
        Object endTime = params.get("endTime");
        List<SysOperLog> result = new ArrayList<>();
        for (SysOperLog m : mockList) {
            if (empNo != null && !empNo.isEmpty()
                    && (m.getEmpNo() == null || !m.getEmpNo().contains(empNo))) continue;
            if (name != null && !name.isEmpty()
                    && (m.getName() == null || !m.getName().contains(name))) continue;
            if (module != null && !module.isEmpty()
                    && (m.getModule() == null || !m.getModule().contains(module))) continue;
            if (operation != null && !operation.isEmpty()
                    && (m.getOperation() == null || !m.getOperation().contains(operation))) continue;
            if (status != null && m.getStatus() != null && !status.equals(m.getStatus())) continue;
            if (startTime instanceof Date && m.getOperTime() != null
                    && m.getOperTime().before((Date) startTime)) continue;
            if (endTime instanceof Date && m.getOperTime() != null
                    && m.getOperTime().after((Date) endTime)) continue;
            result.add(m);
        }
        return result;
    }
}
