package com.mes.admin.modules.system.service;

import com.mes.admin.modules.system.entity.SysDict;
import com.mes.admin.modules.system.repository.SysDictRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据字典服务（父子层级）
 */
@Service
public class SysDictService {

    @Autowired
    private SysDictRepository dictRepository;

    /**
     * 查询全部（条件过滤）
     */
    public List<SysDict> findAll(Map<String, Object> params) {
        try {
            return dictRepository.findAll((root, query, cb) -> {
                List<Predicate> ps = new ArrayList<>();
                if (params != null) {
                    if (params.get("dictName") != null && !"".equals(params.get("dictName"))) {
                        ps.add(cb.like(root.get("dictName"), "%" + params.get("dictName") + "%"));
                    }
                    if (params.get("dictCode") != null && !"".equals(params.get("dictCode"))) {
                        ps.add(cb.like(root.get("dictCode"), "%" + params.get("dictCode") + "%"));
                    }
                    if (params.get("parentId") != null) {
                        ps.add(cb.equal(root.get("parentId"), params.get("parentId")));
                    }
                    if (params.get("parentCode") != null && !"".equals(params.get("parentCode"))) {
                        ps.add(cb.equal(root.get("parentCode"), params.get("parentCode")));
                    }
                    if (params.get("status") != null) {
                        ps.add(cb.equal(root.get("status"), params.get("status")));
                    }
                }
                if (query != null) {
                    query.orderBy(cb.asc(root.get("sort")), cb.desc(root.get("id")));
                }
                return cb.and(ps.toArray(new Predicate[0]));
            });
        } catch (Exception ignored) {
            return filterMockList(params);
        }
    }

    /**
     * 获取树形结构（用于字典管理页面左侧树）
     */
    public List<Map<String, Object>> getTree() {
        List<SysDict> all = findAll(null);
        return buildTree(all, 0L);
    }

    /**
     * 根据父编码查询子节点列表（用于登录下拉等场景）
     * 返回 { id, dictCode, dictName } 结构
     */
    public List<Map<String, Object>> findByParentCode(String parentCode) {
        try {
            List<SysDict> list;
            if (parentCode == null || parentCode.isEmpty()) {
                // 查根节点
                list = dictRepository.findByParentIdOrderBySort(0L);
            } else {
                list = dictRepository.findByParentCodeOrderBySort(parentCode);
            }
            return list.stream().map(d -> {
                Map<String, Object> m = new HashMap<>();
                m.put("id", d.getId());
                m.put("dictCode", d.getDictCode());
                m.put("dictName", d.getDictName());
                m.put("dictValue", d.getDictValue());
                m.put("status", d.getStatus());
                return m;
            }).collect(Collectors.toList());
        } catch (Exception ignored) {
            return filterMockByParentCode(parentCode);
        }
    }

    public SysDict findById(Long id) {
        if (id == null) return null;
        try {
            return dictRepository.findById(id).orElse(null);
        } catch (Exception ignored) {
            for (SysDict m : mockList) {
                if (id.equals(m.getId())) return m;
            }
            return null;
        }
    }

    @Transactional
    public SysDict create(SysDict dict) {
        if (dict.getParentId() == null) dict.setParentId(0L);
        if (dict.getSort() == null) dict.setSort(0);
        if (dict.getStatus() == null) dict.setStatus(0);
        if (dict.getCreateTime() == null) dict.setCreateTime(new Date());
        try {
            return dictRepository.save(dict);
        } catch (Exception ignored) {
            dict.setId((long) -(mockList.size() + 1));
            mockList.add(0, dict);
            return dict;
        }
    }

    @Transactional
    public SysDict update(SysDict dict) {
        if (dict.getId() == null) throw new RuntimeException("字典ID不能为空");
        dict.setUpdateTime(new Date());
        try {
            return dictRepository.save(dict);
        } catch (Exception ignored) {
            for (int i = 0; i < mockList.size(); i++) {
                if (dict.getId().equals(mockList.get(i).getId())) {
                    mockList.set(i, dict);
                    return dict;
                }
            }
            return dict;
        }
    }

    @Transactional
    public void delete(Long id) {
        try {
            // 删除自己
            dictRepository.deleteById(id);
            // 删除所有子节点
            List<SysDict> children = dictRepository.findByParentIdOrderBySort(id);
            if (children != null && !children.isEmpty()) {
                dictRepository.deleteAll(children);
            }
        } catch (Exception ignored) {
            mockList.removeIf(d -> id.equals(d.getId()));
            mockList.removeIf(d -> id.equals(d.getParentId()));
        }
    }

    // ==================== 树构建 ====================
    private List<Map<String, Object>> buildTree(List<SysDict> all, Long parentId) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (SysDict d : all) {
            if (parentId.equals(d.getParentId())) {
                Map<String, Object> node = new HashMap<>();
                node.put("id", d.getId());
                node.put("dictCode", d.getDictCode());
                node.put("dictName", d.getDictName());
                node.put("dictValue", d.getDictValue());
                node.put("parentId", d.getParentId());
                node.put("parentCode", d.getParentCode());
                node.put("sort", d.getSort());
                node.put("remark", d.getRemark());
                node.put("status", d.getStatus());
                node.put("children", buildTree(all, d.getId()));
                result.add(node);
            }
        }
        return result;
    }

    // ==================== 模拟数据 ====================
    private final List<SysDict> mockList = new ArrayList<>();
    {
        // 根节点：分公司
        SysDict root1 = make(1L, "分公司", "DICT_BRANCH", 0L, null, "1", 1, " MES 分公司目录", 0);
        mockList.add(root1);
        // 分公司子节点
        mockList.add(make(2L, "华东分公司", "BRANCH_HD", 1L, "DICT_BRANCH", "1", 1, "华东区域", 0));
        mockList.add(make(3L, "华南分公司", "BRANCH_HN", 1L, "DICT_BRANCH", "2", 2, "华南区域", 0));
        mockList.add(make(4L, "华北分公司", "BRANCH_HB", 1L, "DICT_BRANCH", "3", 3, "华北区域", 0));
        mockList.add(make(5L, "西南分公司", "BRANCH_XN", 1L, "DICT_BRANCH", "4", 4, "西南区域", 0));

        // 根节点：用户状态
        SysDict root2 = make(10L, "用户状态", "DICT_USER_STATUS", 0L, null, "10", 2, "用户状态字典", 0);
        mockList.add(root2);
        mockList.add(make(11L, "启用", "USER_STATUS_ON", 10L, "DICT_USER_STATUS", "0", 1, null, 0));
        mockList.add(make(12L, "禁用", "USER_STATUS_OFF", 10L, "DICT_USER_STATUS", "1", 2, null, 0));

        // 根节点：组织类型
        SysDict root3 = make(20L, "组织类型", "DICT_ORG_TYPE", 0L, null, "20", 3, "组织类型字典", 0);
        mockList.add(root3);
        mockList.add(make(21L, "集团", "ORG_GROUP", 20L, "DICT_ORG_TYPE", "group", 1, null, 0));
        mockList.add(make(22L, "分公司", "ORG_BRANCH", 20L, "DICT_ORG_TYPE", "branch", 2, null, 0));
        mockList.add(make(23L, "部门", "ORG_DEPT", 20L, "DICT_ORG_TYPE", "dept", 3, null, 0));

        // 根节点：性别
        SysDict root4 = make(30L, "性别", "DICT_GENDER", 0L, null, "30", 4, "性别字典", 0);
        mockList.add(root4);
        mockList.add(make(31L, "男", "GENDER_MALE", 30L, "DICT_GENDER", "0", 1, null, 0));
        mockList.add(make(32L, "女", "GENDER_FEMALE", 30L, "DICT_GENDER", "1", 2, null, 0));
    }

    private SysDict make(Long id, String dictName, String dictCode, Long parentId,
                          String parentCode, String dictValue, Integer sort, String remark, Integer status) {
        SysDict d = new SysDict();
        d.setId(id);
        d.setDictName(dictName);
        d.setDictCode(dictCode);
        d.setParentId(parentId);
        d.setParentCode(parentCode);
        d.setDictValue(dictValue);
        d.setSort(sort);
        d.setRemark(remark);
        d.setStatus(status);
        d.setCreateTime(new Date());
        d.setUpdateTime(new Date());
        return d;
    }

    private List<SysDict> filterMockList(Map<String, Object> params) {
        if (params == null || params.isEmpty()) return new ArrayList<>(mockList);
        String name = (String) params.get("dictName");
        String code = (String) params.get("dictCode");
        Long parentId = params.get("parentId") != null ? (Long) params.get("parentId") : null;
        Integer status = params.get("status") != null ? (Integer) params.get("status") : null;
        List<SysDict> result = new ArrayList<>();
        for (SysDict m : mockList) {
            if (name != null && !name.isEmpty() && !m.getDictName().contains(name)) continue;
            if (code != null && !code.isEmpty() && !m.getDictCode().contains(code)) continue;
            if (parentId != null && !parentId.equals(m.getParentId())) continue;
            if (status != null && !status.equals(m.getStatus())) continue;
            result.add(m);
        }
        return result;
    }

    private List<Map<String, Object>> filterMockByParentCode(String parentCode) {
        List<SysDict> list;
        if (parentCode == null || parentCode.isEmpty()) {
            list = mockList.stream().filter(d -> 0L.equals(d.getParentId())).collect(Collectors.toList());
        } else {
            list = mockList.stream().filter(d -> parentCode.equals(d.getParentCode())).collect(Collectors.toList());
        }
        return list.stream().map(d -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", d.getId());
            m.put("dictCode", d.getDictCode());
            m.put("dictName", d.getDictName());
            m.put("dictValue", d.getDictValue());
            m.put("status", d.getStatus());
            return m;
        }).collect(Collectors.toList());
    }
}
