package com.mes.admin.modules.system.service;

import com.mes.admin.common.util.TreeUtil;
import com.mes.admin.modules.system.entity.SysOrg;
import com.mes.admin.modules.system.repository.SysOrgRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class SysOrgService {

    @Autowired
    private SysOrgRepository sysOrgRepository;

    public List<TreeUtil.TreeNode<SysOrg>> getTree() {
        List<SysOrg> all = findAll();
        return TreeUtil.buildTree(all, SysOrg::getId, SysOrg::getParentId, 0L);
    }

    public List<SysOrg> findAll() {
        try {
            return sysOrgRepository.findAll();
        } catch (Exception ignored) {
            return buildMockOrgs();
        }
    }

    public SysOrg findById(Long id) {
        try {
            Optional<SysOrg> opt = sysOrgRepository.findById(id);
            if (opt.isPresent()) {
                return opt.get();
            }
        } catch (Exception ignored) {
        }
        List<SysOrg> list = buildMockOrgs();
        for (SysOrg org : list) {
            if (org.getId().equals(id)) {
                return org;
            }
        }
        return null;
    }

    public String findNameById(Long id) {
        SysOrg org = findById(id);
        return org != null ? org.getName() : null;
    }

    public SysOrg create(SysOrg org) {
        if (org.getStatus() == null) {
            org.setStatus(0);
        }
        if (org.getParentId() == null) {
            org.setParentId(0L);
        }
        return sysOrgRepository.save(org);
    }

    public SysOrg update(SysOrg org) {
        if (org.getId() == null) {
            throw new RuntimeException("组织ID不能为空");
        }
        return sysOrgRepository.save(org);
    }

    public void delete(Long id) {
        sysOrgRepository.deleteById(id);
    }

    public List<SysOrg> findByParentId(Long parentId) {
        try {
            return sysOrgRepository.findByParentId(parentId);
        } catch (Exception ignored) {
            List<SysOrg> result = new ArrayList<>();
            for (SysOrg org : buildMockOrgs()) {
                if (org.getParentId() != null && org.getParentId().equals(parentId)) {
                    result.add(org);
                }
            }
            return result;
        }
    }

    private List<SysOrg> buildMockOrgs() {
        List<SysOrg> list = new ArrayList<>();

        SysOrg group = new SysOrg();
        group.setId(1L);
        group.setParentId(0L);
        group.setOrgCode("MES-GROUP");
        group.setName("MES 集团总部");
        group.setOrgType("group");
        group.setLeader("张总");
        group.setPhone("010-00000000");
        group.setEmail("group@mes.com");
        group.setAddress("北京市朝阳区");
        group.setSort(1);
        group.setStatus(0);
        list.add(group);

        SysOrg branch = new SysOrg();
        branch.setId(2L);
        branch.setParentId(1L);
        branch.setOrgCode("MES-SH");
        branch.setName("上海分公司");
        branch.setOrgType("branch");
        branch.setLeader("李经理");
        branch.setPhone("021-00000000");
        branch.setEmail("sh@mes.com");
        branch.setAddress("上海市浦东新区");
        branch.setSort(1);
        branch.setStatus(0);
        list.add(branch);

        SysOrg dept = new SysOrg();
        dept.setId(3L);
        dept.setParentId(2L);
        dept.setOrgCode("MES-SH-IT");
        dept.setName("信息技术部");
        dept.setOrgType("dept");
        dept.setLeader("王主管");
        dept.setPhone("021-11111111");
        dept.setEmail("it@mes.com");
        dept.setAddress("上海市浦东新区张江");
        dept.setSort(1);
        dept.setStatus(0);
        list.add(dept);

        return list;
    }
}
