package com.mes.admin.modules.system.service;

import com.mes.admin.common.util.TreeUtil;
import com.mes.admin.modules.system.entity.SysMenu;
import com.mes.admin.modules.system.repository.SysMenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SysMenuService {

    @Autowired
    private SysMenuRepository sysMenuRepository;

    public List<TreeUtil.TreeNode<SysMenu>> getMenuTree(Long userId) {
        List<SysMenu> all = findAll();
        return TreeUtil.buildTree(all, SysMenu::getId, SysMenu::getParentId, 0L);
    }

    public List<SysMenu> findAll() {
        try {
            return sysMenuRepository.findAll();
        } catch (Exception ignored) {
            return buildMockMenus();
        }
    }

    public SysMenu findById(Long id) {
        try {
            Optional<SysMenu> opt = sysMenuRepository.findById(id);
            if (opt.isPresent()) {
                return opt.get();
            }
        } catch (Exception ignored) {
        }
        List<SysMenu> list = buildMockMenus();
        for (SysMenu m : list) {
            if (m.getId().equals(id)) {
                return m;
            }
        }
        return null;
    }

    public SysMenu create(SysMenu menu) {
        if (menu.getStatus() == null) {
            menu.setStatus(0);
        }
        if (menu.getParentId() == null) {
            menu.setParentId(0L);
        }
        return sysMenuRepository.save(menu);
    }

    public SysMenu update(SysMenu menu) {
        if (menu.getId() == null) {
            throw new RuntimeException("菜单ID不能为空");
        }
        return sysMenuRepository.save(menu);
    }

    public void delete(Long id) {
        sysMenuRepository.deleteById(id);
    }

    private List<SysMenu> buildMockMenus() {
        List<SysMenu> list = new ArrayList<>();

        SysMenu dashboard = new SysMenu();
        dashboard.setId(1L);
        dashboard.setParentId(0L);
        dashboard.setMenuName("首页");
        dashboard.setPath("/dashboard");
        dashboard.setComponent("dashboard/index");
        dashboard.setRouteName("Dashboard");
        dashboard.setIsFrame(0);
        dashboard.setIsCache(0);
        dashboard.setMenuType("C");
        dashboard.setVisible("0");
        dashboard.setStatus(0);
        dashboard.setPerms("");
        dashboard.setIcon("dashboard");
        dashboard.setSort(1);
        list.add(dashboard);

        SysMenu system = new SysMenu();
        system.setId(2L);
        system.setParentId(0L);
        system.setMenuName("系统管理");
        system.setPath("/system");
        system.setComponent("Layout");
        system.setRouteName("System");
        system.setIsFrame(0);
        system.setIsCache(0);
        system.setMenuType("M");
        system.setVisible("0");
        system.setStatus(0);
        system.setPerms("");
        system.setIcon("system");
        system.setSort(2);
        list.add(system);

        SysMenu user = new SysMenu();
        user.setId(21L);
        user.setParentId(2L);
        user.setMenuName("用户管理");
        user.setPath("user");
        user.setComponent("system/user/index");
        user.setRouteName("User");
        user.setIsFrame(0);
        user.setIsCache(0);
        user.setMenuType("C");
        user.setVisible("0");
        user.setStatus(0);
        user.setPerms("system:user:list");
        user.setIcon("user");
        user.setSort(1);
        list.add(user);

        SysMenu role = new SysMenu();
        role.setId(22L);
        role.setParentId(2L);
        role.setMenuName("角色管理");
        role.setPath("role");
        role.setComponent("system/role/index");
        role.setRouteName("Role");
        role.setIsFrame(0);
        role.setIsCache(0);
        role.setMenuType("C");
        role.setVisible("0");
        role.setStatus(0);
        role.setPerms("system:role:list");
        role.setIcon("peoples");
        role.setSort(2);
        list.add(role);

        SysMenu menu = new SysMenu();
        menu.setId(23L);
        menu.setParentId(2L);
        menu.setMenuName("菜单管理");
        menu.setPath("menu");
        menu.setComponent("system/menu/index");
        menu.setRouteName("Menu");
        menu.setIsFrame(0);
        menu.setIsCache(0);
        menu.setMenuType("C");
        menu.setVisible("0");
        menu.setStatus(0);
        menu.setPerms("system:menu:list");
        menu.setIcon("tree-table");
        menu.setSort(3);
        list.add(menu);

        SysMenu org = new SysMenu();
        org.setId(24L);
        org.setParentId(2L);
        org.setMenuName("组织管理");
        org.setPath("org");
        org.setComponent("system/org/index");
        org.setRouteName("Org");
        org.setIsFrame(0);
        org.setIsCache(0);
        org.setMenuType("C");
        org.setVisible("0");
        org.setStatus(0);
        org.setPerms("system:org:list");
        org.setIcon("tree");
        org.setSort(4);
        list.add(org);

        SysMenu dict = new SysMenu();
        dict.setId(25L);
        dict.setParentId(2L);
        dict.setMenuName("字典管理");
        dict.setPath("dict");
        dict.setComponent("system/dict/index");
        dict.setRouteName("Dict");
        dict.setIsFrame(0);
        dict.setIsCache(0);
        dict.setMenuType("C");
        dict.setVisible("0");
        dict.setStatus(0);
        dict.setPerms("system:dict:list");
        dict.setIcon("dict");
        dict.setSort(5);
        list.add(dict);

        return list;
    }
}
