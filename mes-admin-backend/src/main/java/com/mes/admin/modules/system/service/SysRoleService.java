package com.mes.admin.modules.system.service;

import com.mes.admin.modules.system.entity.SysRole;
import com.mes.admin.modules.system.repository.SysRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SysRoleService {

    @Autowired
    private SysRoleRepository sysRoleRepository;

    public List<SysRole> findAll() {
        try {
            return sysRoleRepository.findAll();
        } catch (Exception ignored) {
            return buildMockRoles();
        }
    }

    public SysRole findById(Long id) {
        try {
            Optional<SysRole> opt = sysRoleRepository.findById(id);
            if (opt.isPresent()) {
                return opt.get();
            }
        } catch (Exception ignored) {
        }
        List<SysRole> list = buildMockRoles();
        for (SysRole r : list) {
            if (r.getId().equals(id)) {
                return r;
            }
        }
        return null;
    }

    public SysRole create(SysRole role) {
        if (role.getStatus() == null) {
            role.setStatus(0);
        }
        return sysRoleRepository.save(role);
    }

    public SysRole update(SysRole role) {
        if (role.getId() == null) {
            throw new RuntimeException("角色ID不能为空");
        }
        return sysRoleRepository.save(role);
    }

    public void delete(Long id) {
        sysRoleRepository.deleteById(id);
    }

    private List<SysRole> buildMockRoles() {
        List<SysRole> list = new ArrayList<>();

        SysRole admin = new SysRole();
        admin.setId(1L);
        admin.setRoleName("超级管理员");
        admin.setRoleKey("admin");
        admin.setRoleSort(1);
        admin.setStatus(0);
        list.add(admin);

        SysRole user = new SysRole();
        user.setId(2L);
        user.setRoleName("普通用户");
        user.setRoleKey("user");
        user.setRoleSort(2);
        user.setStatus(0);
        list.add(user);

        return list;
    }
}
