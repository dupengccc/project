package com.mes.admin.modules.system.service;

import com.mes.admin.modules.system.entity.SysLoginLog;
import com.mes.admin.modules.system.repository.SysLoginLogRepository;
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
 * 登录日志服务
 */
@Service
public class SysLoginLogService {

    @Autowired
    private SysLoginLogRepository logRepository;

    /**
     * 新增一条登录日志（登录成功时调用）
     * @return 返回 id，便于登出时更新
     */
    public Long logLogin(String empNo, String name, String loginIp,
                        Long orgId, String orgName, Integer status, String msg) {
        SysLoginLog log = new SysLoginLog();
        log.setEmpNo(empNo);
        log.setName(name);
        log.setLoginIp(loginIp);
        log.setLoginTime(new Date());
        log.setStatus(status == null ? 0 : status);
        log.setMsg(msg);
        log.setOrgId(orgId);
        log.setOrgName(orgName);
        log.setCreateTime(new Date());
        log.setUpdateTime(new Date());
        try {
            log = logRepository.save(log);
            return log.getId();
        } catch (Exception ignored) {
            // 数据库不可用时，走模拟：仍返回 id=0，内存中兜底
            mockList.add(0, log);
            if (mockList.size() > 200) {
                mockList.remove(mockList.size() - 1);
            }
            log.setId((long) (-mockList.size()));
            return log.getId();
        }
    }

    /**
     * 更新登录日志为"已退出"
     * @param logId 可为 null（按 empNo 取最近一条）
     */
    public void logLogout(Long logId, String empNo) {
        SysLoginLog log = null;
        try {
            if (logId != null) {
                Optional<SysLoginLog> opt = logRepository.findById(logId);
                if (opt.isPresent()) {
                    log = opt.get();
                }
            }
            if (log == null && empNo != null) {
                Optional<SysLoginLog> opt = logRepository.findTopByEmpNoOrderByIdDesc(empNo);
                if (opt.isPresent()) {
                    log = opt.get();
                }
            }
            if (log != null) {
                log.setLogoutTime(new Date());
                log.setMsg("退出成功");
                log.setUpdateTime(new Date());
                logRepository.save(log);
                return;
            }
        } catch (Exception ignored) {}
        // 模拟数据处理
        if (empNo != null) {
            for (SysLoginLog m : mockList) {
                if (empNo.equals(m.getEmpNo()) && m.getLogoutTime() == null) {
                    m.setLogoutTime(new Date());
                    m.setMsg("退出成功");
                    m.setUpdateTime(new Date());
                    return;
                }
            }
        }
    }

    /**
     * 条件查询（支持按工号、姓名、IP、状态、时间范围）
     */
    public List<SysLoginLog> findAll(Map<String, Object> params) {
        try {
            return logRepository.findAll((root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (params != null) {
                    // 工号模糊
                    if (params.get("empNo") != null && !((String) params.get("empNo")).isEmpty()) {
                        predicates.add(cb.like(root.get("empNo"), "%" + params.get("empNo") + "%"));
                    }
                    // 姓名模糊
                    if (params.get("name") != null && !((String) params.get("name")).isEmpty()) {
                        predicates.add(cb.like(root.get("name"), "%" + params.get("name") + "%"));
                    }
                    // IP 模糊
                    if (params.get("loginIp") != null && !((String) params.get("loginIp")).isEmpty()) {
                        predicates.add(cb.like(root.get("loginIp"), "%" + params.get("loginIp") + "%"));
                    }
                    // 状态
                    if (params.get("status") != null) {
                        predicates.add(cb.equal(root.get("status"), params.get("status")));
                    }
                    // 登录时间范围
                    if (params.get("startTime") != null) {
                        predicates.add(cb.greaterThanOrEqualTo(root.get("loginTime"), params.get("startTime")));
                    }
                    if (params.get("endTime") != null) {
                        predicates.add(cb.lessThanOrEqualTo(root.get("loginTime"), params.get("endTime")));
                    }
                }
                if (query != null) {
                    query.orderBy(cb.desc(root.get("loginTime")));
                }
                return cb.and(predicates.toArray(new Predicate[0]));
            });
        } catch (Exception ignored) {
            // 数据库不可用：返回模拟数据
            return filterMockList(params);
        }
    }

    public SysLoginLog findById(Long id) {
        if (id == null) return null;
        try {
            Optional<SysLoginLog> opt = logRepository.findById(id);
            if (opt.isPresent()) return opt.get();
        } catch (Exception ignored) {}
        for (SysLoginLog m : mockList) {
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

    /**
     * 清理指定天数前的日志
     */
    public void cleanOverDays(int days) {
        long cutoff = System.currentTimeMillis() - (long) days * 24L * 3600L * 1000L;
        Date cutoffDate = new Date(cutoff);
        try {
            List<SysLoginLog> olds = logRepository.findAll((root, query, cb) ->
                    cb.lessThan(root.get("loginTime"), cutoffDate));
            if (olds != null && !olds.isEmpty()) {
                logRepository.deleteAll(olds);
            }
        } catch (Exception ignored) {
            mockList.removeIf(m -> m.getLoginTime() != null && m.getLoginTime().before(cutoffDate));
        }
    }

    // ==================== 模拟数据 ====================
    private final List<SysLoginLog> mockList = Collections.synchronizedList(new ArrayList<>());
    {
        // 初始化一些模拟登录日志
        for (int i = 0; i < 10; i++) {
            SysLoginLog log = new SysLoginLog();
            long ts = System.currentTimeMillis() - (long) i * 3600L * 1000L;
            log.setId((long) (-(mockList.size() + 1)));
            log.setEmpNo("admin");
            log.setName("系统管理员");
            log.setLoginIp("127.0.0." + (1 + i % 10));
            log.setLoginLocation("局域网");
            log.setLoginTime(new Date(ts));
            log.setLogoutTime(i % 3 == 0 ? null : new Date(ts + (long) (60 + i * 10) * 1000L));
            log.setStatus(0);
            log.setMsg("登录成功");
            log.setOrgId(1L);
            log.setOrgName("MES 集团总部");
            log.setCreateTime(new Date(ts));
            log.setUpdateTime(new Date(ts));
            mockList.add(log);
        }
    }

    private List<SysLoginLog> filterMockList(Map<String, Object> params) {
        if (params == null || params.isEmpty()) return new ArrayList<>(mockList);
        String empNo = (String) params.get("empNo");
        String name = (String) params.get("name");
        String loginIp = (String) params.get("loginIp");
        Object status = params.get("status");
        Object startTime = params.get("startTime");
        Object endTime = params.get("endTime");
        List<SysLoginLog> result = new ArrayList<>();
        for (SysLoginLog m : mockList) {
            if (empNo != null && !empNo.isEmpty()
                    && (m.getEmpNo() == null || !m.getEmpNo().contains(empNo))) continue;
            if (name != null && !name.isEmpty()
                    && (m.getName() == null || !m.getName().contains(name))) continue;
            if (loginIp != null && !loginIp.isEmpty()
                    && (m.getLoginIp() == null || !m.getLoginIp().contains(loginIp))) continue;
            if (status != null && m.getStatus() != null && !status.equals(m.getStatus())) continue;
            if (startTime instanceof Date && m.getLoginTime() != null
                    && m.getLoginTime().before((Date) startTime)) continue;
            if (endTime instanceof Date && m.getLoginTime() != null
                    && m.getLoginTime().after((Date) endTime)) continue;
            result.add(m);
        }
        return result;
    }
}
