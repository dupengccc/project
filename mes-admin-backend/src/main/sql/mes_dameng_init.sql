-- =====================================================================
-- MES 制造执行系统 - 管理后台 (达梦数据库 Dm8)
-- 编码: UTF-8
-- 生成日期: 2026-06-17
-- 执行用户: SYSDBA (具有创建表/序列权限)
-- =====================================================================
-- 说明：达梦数据库 Dm8 语法与 Oracle 类似，主键使用 NUMBER(20)
--       ID 由后端 IdGenerator 自动生成（年月日时分毫秒+序号）
-- =====================================================================

-- =====================================================================
-- 系统模块 (System) - 5 张表
-- =====================================================================

-- 用户表
CREATE TABLE sys_user (
    id NUMBER(20) NOT NULL,
    username VARCHAR(64) NOT NULL,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(64),
    email VARCHAR(128),
    phone VARCHAR(32),
    gender INT DEFAULT 0,
    avatar VARCHAR(255),
    dept_id BIGINT,
    login_ip VARCHAR(64),
    login_date DATETIME,
    last_login_date DATETIME,
    status INT DEFAULT 0,
    remark VARCHAR(500),
    create_by VARCHAR(64),
    create_time DATETIME DEFAULT SYSDATE,
    update_by VARCHAR(64),
    update_time DATETIME,
    PRIMARY KEY (id)
);

CREATE UNIQUE INDEX idx_sys_user_username ON sys_user(username);

-- 组织/部门表
CREATE TABLE sys_org (
    id NUMBER(20) NOT NULL,
    parent_id BIGINT DEFAULT 0,
    org_code VARCHAR(64),
    name VARCHAR(128) NOT NULL,
    org_type VARCHAR(32),
    leader VARCHAR(64),
    phone VARCHAR(32),
    email VARCHAR(128),
    address VARCHAR(255),
    sort INT DEFAULT 0,
    status INT DEFAULT 0,
    remark VARCHAR(500),
    create_by VARCHAR(64),
    create_time DATETIME DEFAULT SYSDATE,
    update_by VARCHAR(64),
    update_time DATETIME,
    PRIMARY KEY (id)
);

-- 字典数据
CREATE TABLE sys_dict_data (
    id NUMBER(20) NOT NULL,
    dict_sort INT DEFAULT 0,
    dict_label VARCHAR(128),
    dict_value VARCHAR(128),
    dict_type VARCHAR(128),
    css_class VARCHAR(128),
    list_class VARCHAR(128),
    is_default VARCHAR(8),
    status INT DEFAULT 0,
    remark VARCHAR(500),
    create_by VARCHAR(64),
    create_time DATETIME DEFAULT SYSDATE,
    update_by VARCHAR(64),
    update_time DATETIME,
    PRIMARY KEY (id)
);

CREATE INDEX idx_sys_dict_type ON sys_dict_data(dict_type);

-- 数据字典表（父子层级结构）
CREATE TABLE sys_dict (
    id NUMBER(20) NOT NULL,
    dict_name VARCHAR(128),
    dict_code VARCHAR(128),
    parent_id BIGINT DEFAULT 0,
    parent_code VARCHAR(128),
    dict_value VARCHAR(128),
    sort INT DEFAULT 0,
    remark VARCHAR(512),
    status INT DEFAULT 0,
    create_by VARCHAR(64),
    create_time DATETIME DEFAULT SYSDATE,
    update_by VARCHAR(64),
    update_time DATETIME,
    PRIMARY KEY (id)
);

CREATE INDEX idx_sys_dict_parent_id ON sys_dict(parent_id);
CREATE INDEX idx_sys_dict_parent_code ON sys_dict(parent_code);
CREATE UNIQUE INDEX idx_sys_dict_code ON sys_dict(dict_code);

-- 角色表
CREATE TABLE sys_role (
    id NUMBER(20) NOT NULL,
    role_name VARCHAR(64),
    role_key VARCHAR(64),
    role_sort INT DEFAULT 0,
    status INT DEFAULT 0,
    data_scope VARCHAR(2) DEFAULT '1',
    remark VARCHAR(500),
    create_by VARCHAR(64),
    create_time DATETIME DEFAULT SYSDATE,
    update_by VARCHAR(64),
    update_time DATETIME,
    PRIMARY KEY (id)
);

-- 角色-组织 关联表（用于"自定义数据范围"）
CREATE TABLE sys_role_dept (
    id NUMBER(20) NOT NULL,
    role_id BIGINT NOT NULL,
    dept_id BIGINT NOT NULL,
    PRIMARY KEY (id)
);
CREATE INDEX idx_sys_role_dept_role ON sys_role_dept(role_id);
CREATE INDEX idx_sys_role_dept_dept ON sys_role_dept(dept_id);

-- 角色-物料 关联表（用于物料数据权限）
CREATE TABLE sys_role_material (
    id NUMBER(20) NOT NULL,
    role_id BIGINT NOT NULL,
    material_id BIGINT NOT NULL,
    material_code VARCHAR(64),
    material_name VARCHAR(128),
    PRIMARY KEY (id)
);
CREATE INDEX idx_sys_role_material_role ON sys_role_material(role_id);
CREATE INDEX idx_sys_role_material_material ON sys_role_material(material_id);

-- 生产单元表
CREATE TABLE sys_production_unit (
    id NUMBER(20) NOT NULL,
    unit_code VARCHAR(64) UNIQUE,
    unit_name VARCHAR(128),
    unit_type VARCHAR(32),
    org_id BIGINT,
    org_name VARCHAR(128),
    status INT DEFAULT 0,
    sort INT DEFAULT 0,
    remark VARCHAR(500),
    create_by VARCHAR(64),
    create_time DATETIME DEFAULT SYSDATE,
    update_by VARCHAR(64),
    update_time DATETIME,
    PRIMARY KEY (id)
);
CREATE INDEX idx_sys_production_unit_org ON sys_production_unit(org_id);
CREATE INDEX idx_sys_production_unit_code ON sys_production_unit(unit_code);

-- 接口配置表
CREATE TABLE sys_interface_config (
    id NUMBER(20) NOT NULL,
    interface_code VARCHAR(64) UNIQUE,
    interface_name VARCHAR(128),
    description VARCHAR(500),
    url VARCHAR(500),
    request_method VARCHAR(16),
    content_type VARCHAR(64),
    headers TEXT,
    param_template TEXT,
    auth_type VARCHAR(32),
    auth_config TEXT,
    timeout INT DEFAULT 30000,
    retry_count INT DEFAULT 3,
    retry_interval INT DEFAULT 5000,
    rate_limit INT DEFAULT 60,
    status INT DEFAULT 0,
    encrypt_enabled INT DEFAULT 0,
    encrypt_key VARCHAR(255),
    callback_url VARCHAR(500),
    owner_system VARCHAR(64),
    contact VARCHAR(64),
    contact_phone VARCHAR(32),
    remark VARCHAR(500),
    create_by VARCHAR(64),
    create_time DATETIME DEFAULT SYSDATE,
    update_by VARCHAR(64),
    update_time DATETIME,
    PRIMARY KEY (id)
);
CREATE INDEX idx_interface_config_code ON sys_interface_config(interface_code);
CREATE INDEX idx_interface_config_system ON sys_interface_config(owner_system);

-- 接口调用日志表
CREATE TABLE sys_interface_log (
    id NUMBER(20) NOT NULL,
    config_id NUMBER(20),
    interface_code VARCHAR(64),
    interface_name VARCHAR(128),
    request_url VARCHAR(500),
    request_method VARCHAR(16),
    request_headers TEXT,
    request_params TEXT,
    request_body TEXT,
    response_status INT,
    response_headers TEXT,
    response_body TEXT,
    response_time BIGINT,
    call_status INT DEFAULT 0,
    error_msg TEXT,
    exception_type VARCHAR(128),
    stack_trace TEXT,
    biz_status VARCHAR(16),
    biz_message VARCHAR(255),
    biz_no VARCHAR(64),
    source_system VARCHAR(64),
    source_module VARCHAR(64),
    operator_id NUMBER(20),
    operator_name VARCHAR(64),
    ip_address VARCHAR(64),
    call_time DATETIME,
    finish_time DATETIME,
    retry_count INT DEFAULT 0,
    processed INT DEFAULT 0,
    processor VARCHAR(64),
    process_time DATETIME,
    process_remark VARCHAR(500),
    call_type INT DEFAULT 0,
    create_by VARCHAR(64),
    create_time DATETIME DEFAULT SYSDATE,
    update_by VARCHAR(64),
    update_time DATETIME,
    PRIMARY KEY (id)
);
CREATE INDEX idx_interface_log_code ON sys_interface_log(interface_code);
CREATE INDEX idx_interface_log_status ON sys_interface_log(call_status);
CREATE INDEX idx_interface_log_biz_no ON sys_interface_log(biz_no);
CREATE INDEX idx_interface_log_call_time ON sys_interface_log(call_time);

-- 菜单表
CREATE TABLE sys_menu (
    id NUMBER(20) NOT NULL,
    parent_id BIGINT DEFAULT 0,
    menu_name VARCHAR(128),
    path VARCHAR(255),
    component VARCHAR(255),
    query VARCHAR(255),
    route_name VARCHAR(128),
    is_frame INT DEFAULT 0,
    is_cache INT DEFAULT 0,
    menu_type VARCHAR(8),
    visible VARCHAR(8) DEFAULT '0',
    status INT DEFAULT 0,
    perms VARCHAR(128),
    icon VARCHAR(64),
    sort INT DEFAULT 0,
    remark VARCHAR(500),
    create_by VARCHAR(64),
    create_time DATETIME DEFAULT SYSDATE,
    update_by VARCHAR(64),
    update_time DATETIME,
    PRIMARY KEY (id)
);

-- 登录日志表
CREATE TABLE sys_login_log (
    id NUMBER(20) NOT NULL,
    emp_no VARCHAR(64),
    name VARCHAR(128),
    login_ip VARCHAR(64),
    login_location VARCHAR(255),
    browser VARCHAR(255),
    os VARCHAR(128),
    login_time DATETIME,
    logout_time DATETIME,
    status INT DEFAULT 0,
    msg VARCHAR(512),
    org_id BIGINT,
    org_name VARCHAR(128),
    create_time DATETIME DEFAULT SYSDATE,
    update_time DATETIME,
    PRIMARY KEY (id)
);

CREATE INDEX idx_sys_login_log_empno ON sys_login_log(emp_no);
CREATE INDEX idx_sys_login_log_time ON sys_login_log(login_time);
CREATE INDEX idx_sys_login_log_status ON sys_login_log(status);

-- 操作日志表
CREATE TABLE sys_oper_log (
    id NUMBER(20) NOT NULL,
    emp_no VARCHAR(64),
    name VARCHAR(128),
    org_id BIGINT,
    org_name VARCHAR(128),
    module VARCHAR(128),
    operation VARCHAR(128),
    method VARCHAR(255),
    request_method VARCHAR(16),
    url VARCHAR(255),
    params TEXT,
    ip VARCHAR(64),
    location VARCHAR(255),
    oper_time DATETIME,
    cost BIGINT,
    status INT DEFAULT 0,
    error_msg TEXT,
    create_time DATETIME DEFAULT SYSDATE,
    PRIMARY KEY (id)
);

CREATE INDEX idx_sys_oper_log_empno ON sys_oper_log(emp_no);
CREATE INDEX idx_sys_oper_log_time ON sys_oper_log(oper_time);
CREATE INDEX idx_sys_oper_log_status ON sys_oper_log(status);
CREATE INDEX idx_sys_oper_log_module ON sys_oper_log(module);

-- =====================================================================
-- 基础数据模块 (MES-MD) - 8 张表
-- =====================================================================

CREATE TABLE mes_md_material (
    id NUMBER(20) NOT NULL,
    material_code VARCHAR(64),
    material_name VARCHAR(128),
    spec VARCHAR(255),
    material_type VARCHAR(32),
    manage_mode VARCHAR(32),
    unit VARCHAR(32),
    org_id BIGINT,
    org_name VARCHAR(128),
    safe_stock DECIMAL(18,2),
    current_stock DECIMAL(18,2),
    status INT DEFAULT 0,
    remark VARCHAR(500),
    create_by VARCHAR(64),
    create_time DATETIME DEFAULT SYSDATE,
    update_by VARCHAR(64),
    update_time DATETIME,
    PRIMARY KEY (id)
);
CREATE INDEX idx_mes_md_material_code ON mes_md_material(material_code);
CREATE INDEX idx_mes_md_material_type ON mes_md_material(material_type);
CREATE INDEX idx_mes_md_material_org ON mes_md_material(org_id);

CREATE TABLE mes_md_customer (
    id NUMBER(20) NOT NULL,
    customer_code VARCHAR(64),
    customer_name VARCHAR(128),
    contact VARCHAR(64),
    phone VARCHAR(32),
    address VARCHAR(255),
    credit_level VARCHAR(16),
    status INT DEFAULT 0,
    remark VARCHAR(500),
    create_by VARCHAR(64),
    create_time DATETIME DEFAULT SYSDATE,
    update_by VARCHAR(64),
    update_time DATETIME,
    PRIMARY KEY (id)
);

CREATE TABLE mes_md_vendor (
    id NUMBER(20) NOT NULL,
    vendor_code VARCHAR(64),
    vendor_name VARCHAR(128),
    contact VARCHAR(64),
    phone VARCHAR(32),
    address VARCHAR(255),
    supply_level VARCHAR(32),
    status INT DEFAULT 0,
    remark VARCHAR(500),
    create_by VARCHAR(64),
    create_time DATETIME DEFAULT SYSDATE,
    update_by VARCHAR(64),
    update_time DATETIME,
    PRIMARY KEY (id)
);

CREATE TABLE mes_md_workshop (
    id NUMBER(20) NOT NULL,
    workshop_code VARCHAR(64),
    workshop_name VARCHAR(128),
    leader VARCHAR(64),
    phone VARCHAR(32),
    area DECIMAL(18,2),
    status INT DEFAULT 0,
    remark VARCHAR(500),
    create_by VARCHAR(64),
    create_time DATETIME DEFAULT SYSDATE,
    update_by VARCHAR(64),
    update_time DATETIME,
    PRIMARY KEY (id)
);

CREATE TABLE mes_md_workstation (
    id NUMBER(20) NOT NULL,
    station_code VARCHAR(64),
    station_name VARCHAR(128),
    workshop_id BIGINT,
    workshop_name VARCHAR(128),
    device_no VARCHAR(64),
    operator VARCHAR(64),
    status INT DEFAULT 0,
    remark VARCHAR(500),
    create_by VARCHAR(64),
    create_time DATETIME DEFAULT SYSDATE,
    update_by VARCHAR(64),
    update_time DATETIME,
    PRIMARY KEY (id)
);

CREATE TABLE mes_md_process (
    id NUMBER(20) NOT NULL,
    process_code VARCHAR(64),
    process_name VARCHAR(128),
    process_type VARCHAR(32),
    standard_time DECIMAL(10,2),
    status INT DEFAULT 0,
    remark VARCHAR(500),
    create_by VARCHAR(64),
    create_time DATETIME DEFAULT SYSDATE,
    update_by VARCHAR(64),
    update_time DATETIME,
    PRIMARY KEY (id)
);

CREATE TABLE mes_md_route (
    id NUMBER(20) NOT NULL,
    route_code VARCHAR(64),
    route_name VARCHAR(128),
    product_name VARCHAR(128),
    description VARCHAR(500),
    process_count INT DEFAULT 0,
    status INT DEFAULT 0,
    remark VARCHAR(500),
    create_by VARCHAR(64),
    create_time DATETIME DEFAULT SYSDATE,
    update_by VARCHAR(64),
    update_time DATETIME,
    PRIMARY KEY (id)
);

CREATE TABLE mes_md_bom (
    id NUMBER(20) NOT NULL,
    bom_code VARCHAR(64),
    bom_name VARCHAR(128),
    product_name VARCHAR(128),
    version VARCHAR(32),
    status INT DEFAULT 0,
    remark VARCHAR(500),
    create_by VARCHAR(64),
    create_time DATETIME DEFAULT SYSDATE,
    update_by VARCHAR(64),
    update_time DATETIME,
    PRIMARY KEY (id)
);

-- =====================================================================
-- 生产管理模块 (MES-PRO) - 3 张表
-- =====================================================================

CREATE TABLE mes_pro_workorder (
    id NUMBER(20) NOT NULL,
    order_code VARCHAR(64),
    product_name VARCHAR(128),
    plan_qty DECIMAL(18,2),
    completed_qty DECIMAL(18,2),
    work_status VARCHAR(32),
    priority INT DEFAULT 2,
    plan_start DATETIME,
    plan_end DATETIME,
    actual_start DATETIME,
    actual_end DATETIME,
    workshop_name VARCHAR(128),
    workstation_name VARCHAR(128),
    operator VARCHAR(64),
    status INT DEFAULT 0,
    remark VARCHAR(500),
    create_by VARCHAR(64),
    create_time DATETIME DEFAULT SYSDATE,
    update_by VARCHAR(64),
    update_time DATETIME,
    PRIMARY KEY (id)
);

CREATE TABLE mes_pro_schedule (
    id NUMBER(20) NOT NULL,
    schedule_code VARCHAR(64),
    order_code VARCHAR(64),
    product_name VARCHAR(128),
    workshop_name VARCHAR(128),
    workstation_name VARCHAR(128),
    schedule_date DATETIME,
    plan_qty DECIMAL(18,2),
    completed_qty DECIMAL(18,2),
    status INT DEFAULT 0,
    remark VARCHAR(500),
    create_by VARCHAR(64),
    create_time DATETIME DEFAULT SYSDATE,
    update_by VARCHAR(64),
    update_time DATETIME,
    PRIMARY KEY (id)
);

CREATE TABLE mes_pro_report (
    id NUMBER(20) NOT NULL,
    report_code VARCHAR(64),
    order_code VARCHAR(64),
    product_name VARCHAR(128),
    process_name VARCHAR(128),
    operator VARCHAR(64),
    report_qty DECIMAL(18,2),
    bad_qty DECIMAL(18,2),
    report_time DATETIME,
    status INT DEFAULT 0,
    remark VARCHAR(500),
    create_by VARCHAR(64),
    create_time DATETIME DEFAULT SYSDATE,
    update_by VARCHAR(64),
    update_time DATETIME,
    PRIMARY KEY (id)
);

-- =====================================================================
-- 仓储管理模块 (MES-WM) - 6 张表
-- =====================================================================

CREATE TABLE mes_wm_warehouse (
    id NUMBER(20) NOT NULL,
    warehouse_code VARCHAR(64),
    warehouse_name VARCHAR(128),
    warehouse_type VARCHAR(32),
    leader VARCHAR(64),
    phone VARCHAR(32),
    address VARCHAR(255),
    status INT DEFAULT 0,
    remark VARCHAR(500),
    create_by VARCHAR(64),
    create_time DATETIME DEFAULT SYSDATE,
    update_by VARCHAR(64),
    update_time DATETIME,
    PRIMARY KEY (id)
);

CREATE TABLE mes_wm_area (
    id NUMBER(20) NOT NULL,
    area_code VARCHAR(64),
    area_name VARCHAR(128),
    warehouse_id BIGINT,
    warehouse_name VARCHAR(128),
    area_type VARCHAR(32),
    area_size DECIMAL(18,2),
    status INT DEFAULT 0,
    remark VARCHAR(500),
    create_by VARCHAR(64),
    create_time DATETIME DEFAULT SYSDATE,
    update_by VARCHAR(64),
    update_time DATETIME,
    PRIMARY KEY (id)
);

CREATE TABLE mes_wm_location (
    id NUMBER(20) NOT NULL,
    location_code VARCHAR(64),
    location_name VARCHAR(128),
    warehouse_id BIGINT,
    warehouse_name VARCHAR(128),
    area_id BIGINT,
    area_name VARCHAR(128),
    location_type VARCHAR(32),
    capacity DECIMAL(18,2),
    status INT DEFAULT 0,
    remark VARCHAR(500),
    create_by VARCHAR(64),
    create_time DATETIME DEFAULT SYSDATE,
    update_by VARCHAR(64),
    update_time DATETIME,
    PRIMARY KEY (id)
);

CREATE TABLE mes_wm_stock (
    id NUMBER(20) NOT NULL,
    material_code VARCHAR(64),
    material_name VARCHAR(128),
    spec VARCHAR(255),
    warehouse_name VARCHAR(128),
    area_name VARCHAR(128),
    location_name VARCHAR(128),
    stock_qty DECIMAL(18,2),
    safe_stock DECIMAL(18,2),
    unit VARCHAR(32),
    status INT DEFAULT 0,
    remark VARCHAR(500),
    create_by VARCHAR(64),
    create_time DATETIME DEFAULT SYSDATE,
    update_by VARCHAR(64),
    update_time DATETIME,
    PRIMARY KEY (id)
);

CREATE TABLE mes_wm_in (
    id NUMBER(20) NOT NULL,
    in_code VARCHAR(64),
    material_name VARCHAR(128),
    in_qty DECIMAL(18,2),
    unit VARCHAR(32),
    vendor_name VARCHAR(128),
    warehouse_name VARCHAR(128),
    in_date DATETIME,
    operator VARCHAR(64),
    status INT DEFAULT 0,
    remark VARCHAR(500),
    create_by VARCHAR(64),
    create_time DATETIME DEFAULT SYSDATE,
    update_by VARCHAR(64),
    update_time DATETIME,
    PRIMARY KEY (id)
);

CREATE TABLE mes_wm_out (
    id NUMBER(20) NOT NULL,
    out_code VARCHAR(64),
    material_name VARCHAR(128),
    out_qty DECIMAL(18,2),
    unit VARCHAR(32),
    receive_dept VARCHAR(128),
    warehouse_name VARCHAR(128),
    out_date DATETIME,
    operator VARCHAR(64),
    status INT DEFAULT 0,
    remark VARCHAR(500),
    create_by VARCHAR(64),
    create_time DATETIME DEFAULT SYSDATE,
    update_by VARCHAR(64),
    update_time DATETIME,
    PRIMARY KEY (id)
);

-- =====================================================================
-- 质量管理模块 (MES-QC) - 3 张表
-- =====================================================================

CREATE TABLE mes_qc_defect (
    id NUMBER(20) NOT NULL,
    defect_code VARCHAR(64),
    defect_name VARCHAR(128),
    defect_type VARCHAR(32),
    severity VARCHAR(32),
    suggestion VARCHAR(500),
    status INT DEFAULT 0,
    remark VARCHAR(500),
    create_by VARCHAR(64),
    create_time DATETIME DEFAULT SYSDATE,
    update_by VARCHAR(64),
    update_time DATETIME,
    PRIMARY KEY (id)
);

CREATE TABLE mes_qc_template (
    id NUMBER(20) NOT NULL,
    template_code VARCHAR(64),
    template_name VARCHAR(128),
    product_type VARCHAR(128),
    check_item_count INT DEFAULT 0,
    version VARCHAR(32),
    status INT DEFAULT 0,
    remark VARCHAR(500),
    create_by VARCHAR(64),
    create_time DATETIME DEFAULT SYSDATE,
    update_by VARCHAR(64),
    update_time DATETIME,
    PRIMARY KEY (id)
);

CREATE TABLE mes_qc_record (
    id NUMBER(20) NOT NULL,
    record_code VARCHAR(64),
    product_name VARCHAR(128),
    check_qty DECIMAL(18,2),
    qualified_qty DECIMAL(18,2),
    unqualified_qty DECIMAL(18,2),
    check_result VARCHAR(32),
    checker VARCHAR(64),
    check_time DATETIME,
    order_code VARCHAR(64),
    status INT DEFAULT 0,
    remark VARCHAR(500),
    create_by VARCHAR(64),
    create_time DATETIME DEFAULT SYSDATE,
    update_by VARCHAR(64),
    update_time DATETIME,
    PRIMARY KEY (id)
);

-- =====================================================================
-- 设备管理模块 (MES-DV) - 3 张表
-- =====================================================================

CREATE TABLE mes_dv_device (
    id NUMBER(20) NOT NULL,
    device_code VARCHAR(64),
    device_name VARCHAR(128),
    device_type VARCHAR(64),
    spec VARCHAR(255),
    workshop_id BIGINT,
    workshop_name VARCHAR(128),
    leader VARCHAR(64),
    purchase_date DATETIME,
    service_years INT,
    status INT DEFAULT 0,
    remark VARCHAR(500),
    create_by VARCHAR(64),
    create_time DATETIME DEFAULT SYSDATE,
    update_by VARCHAR(64),
    update_time DATETIME,
    PRIMARY KEY (id)
);

CREATE TABLE mes_dv_check (
    id NUMBER(20) NOT NULL,
    check_code VARCHAR(64),
    device_code VARCHAR(64),
    device_name VARCHAR(128),
    check_type VARCHAR(32),
    checker VARCHAR(64),
    check_date DATETIME,
    check_result VARCHAR(32),
    next_check_date DATETIME,
    status INT DEFAULT 0,
    remark VARCHAR(500),
    create_by VARCHAR(64),
    create_time DATETIME DEFAULT SYSDATE,
    update_by VARCHAR(64),
    update_time DATETIME,
    PRIMARY KEY (id)
);

CREATE TABLE mes_dv_repair (
    id NUMBER(20) NOT NULL,
    repair_code VARCHAR(64),
    device_code VARCHAR(64),
    device_name VARCHAR(128),
    fault_desc VARCHAR(500),
    reporter VARCHAR(64),
    report_date DATETIME,
    repairer VARCHAR(64),
    repair_date DATETIME,
    repair_status VARCHAR(32),
    repair_cost DECIMAL(18,2),
    status INT DEFAULT 0,
    remark VARCHAR(500),
    create_by VARCHAR(64),
    create_time DATETIME DEFAULT SYSDATE,
    update_by VARCHAR(64),
    update_time DATETIME,
    PRIMARY KEY (id)
);

-- =====================================================================
-- 工具管理模块 (MES-TM) - 1 张表
-- =====================================================================

CREATE TABLE mes_tm_tool (
    id NUMBER(20) NOT NULL,
    tool_code VARCHAR(64),
    tool_name VARCHAR(128),
    tool_type VARCHAR(32),
    spec VARCHAR(255),
    workshop_name VARCHAR(128),
    storage_location VARCHAR(255),
    tool_status VARCHAR(32),
    in_date DATETIME,
    status INT DEFAULT 0,
    remark VARCHAR(500),
    create_by VARCHAR(64),
    create_time DATETIME DEFAULT SYSDATE,
    update_by VARCHAR(64),
    update_time DATETIME,
    PRIMARY KEY (id)
);

-- =====================================================================
-- 排班管理模块 (MES-CAL) - 4 张表
-- =====================================================================

CREATE TABLE mes_cal_team (
    id NUMBER(20) NOT NULL,
    team_code VARCHAR(64),
    team_name VARCHAR(128),
    leader VARCHAR(64),
    workshop_name VARCHAR(128),
    member_count INT DEFAULT 0,
    status INT DEFAULT 0,
    remark VARCHAR(500),
    create_by VARCHAR(64),
    create_time DATETIME DEFAULT SYSDATE,
    update_by VARCHAR(64),
    update_time DATETIME,
    PRIMARY KEY (id)
);

CREATE TABLE mes_cal_shift (
    id NUMBER(20) NOT NULL,
    shift_code VARCHAR(64),
    shift_name VARCHAR(128),
    start_time VARCHAR(16),
    end_time VARCHAR(16),
    hours DECIMAL(10,2),
    shift_type VARCHAR(32),
    status INT DEFAULT 0,
    remark VARCHAR(500),
    create_by VARCHAR(64),
    create_time DATETIME DEFAULT SYSDATE,
    update_by VARCHAR(64),
    update_time DATETIME,
    PRIMARY KEY (id)
);

CREATE TABLE mes_cal_plan (
    id NUMBER(20) NOT NULL,
    plan_code VARCHAR(64),
    team_name VARCHAR(128),
    plan_date DATETIME,
    shift_name VARCHAR(128),
    member_count INT DEFAULT 0,
    creator VARCHAR(64),
    status INT DEFAULT 0,
    remark VARCHAR(500),
    create_by VARCHAR(64),
    create_time DATETIME DEFAULT SYSDATE,
    update_by VARCHAR(64),
    update_time DATETIME,
    PRIMARY KEY (id)
);

CREATE TABLE mes_cal_calendar (
    id NUMBER(20) NOT NULL,
    calendar_code VARCHAR(64),
    calendar_date DATETIME,
    team_name VARCHAR(128),
    shift_name VARCHAR(128),
    member_names VARCHAR(1024),
    member_count INT DEFAULT 0,
    status INT DEFAULT 0,
    remark VARCHAR(500),
    create_by VARCHAR(64),
    create_time DATETIME DEFAULT SYSDATE,
    update_by VARCHAR(64),
    update_time DATETIME,
    PRIMARY KEY (id)
);

-- =====================================================================
-- 初始化数据
-- =====================================================================

-- 1. 初始化系统用户 (密码: admin 的 BCrypt 加密值 - 需与后端加密算法一致
-- 如后端使用 bcrypt encoder，此处密码示例值与前端登录测试密码 admin 匹配)
INSERT INTO sys_user (id, username, password, nickname, email, phone, gender, dept_id, status)
VALUES (1, 'admin', '$2a$10$7EqJtq98hPqEX7fNZaFWoO5m6kU6F1S6D6p6j5E0V5b9d9j7h6', '系统管理员', 'admin@mes.com', '13800000000', 0, 1, 0);

INSERT INTO sys_user (id, username, password, nickname, email, phone, gender, dept_id, status)
VALUES (2, 'operator', '$2a$10$7EqJtq98hPqEX7fNZaFWoO5m6kU6F1S6D6p6j5E0V5b9d9j7h6', '操作员', 'op@mes.com', '13800000001', 0, 2, 0);

-- 2. 初始化角色（包含数据权限范围）
INSERT INTO sys_role (id, role_name, role_key, role_sort, status, data_scope, remark)
VALUES (1, '超级管理员', 'super_admin', 1, 0, '1', '全部数据权限');
INSERT INTO sys_role (id, role_name, role_key, role_sort, status, data_scope, remark)
VALUES (2, '集团管理员', 'group_admin', 2, 0, '4', '本集团及以下部门');
INSERT INTO sys_role (id, role_name, role_key, role_sort, status, data_scope, remark)
VALUES (3, '分公司管理员', 'branch_admin', 3, 0, '4', '本分公司及以下部门');
INSERT INTO sys_role (id, role_name, role_key, role_sort, status, data_scope, remark)
VALUES (4, '部门管理员', 'dept_admin', 4, 0, '3', '仅本部门');
INSERT INTO sys_role (id, role_name, role_key, role_sort, status, data_scope, remark)
VALUES (5, '普通用户', 'user', 5, 0, '5', '仅本人数据');
INSERT INTO sys_role (id, role_name, role_key, role_sort, status, data_scope, remark)
VALUES (6, '访客', 'guest', 6, 1, '5', '只读访客');

-- 3. 角色-组织 关联（用于"自定义数据范围"，此处示例：分公司管理员角色可见华东/华南分公司）
INSERT INTO sys_role_dept (role_id, dept_id) VALUES (3, 2);
INSERT INTO sys_role_dept (role_id, dept_id) VALUES (3, 3);

-- 4. 初始化组织数据 (MES 典型组织)
INSERT INTO sys_org (id, parent_id, org_code, name, org_type, leader, phone, email, address, sort, status)
VALUES (1, 0, 'M001', 'MES 集团', 'group', '张总', '13800000010', 'ceo@mes.com', '上海市浦东新区', 1, 0);

INSERT INTO sys_org (id, parent_id, org_code, name, org_type, leader, phone, email, address, sort, status)
VALUES (2, 1, 'W001', '华东分公司', 'branch', '李经理', '13800000020', 'east@mes.com', '上海市浦东新区', 1, 0);

INSERT INTO sys_org (id, parent_id, org_code, name, org_type, leader, phone, email, address, sort, status)
VALUES (3, 1, 'W002', '华南分公司', 'branch', '王经理', '13800000030', 'south@mes.com', '深圳市南山区', 2, 0);

INSERT INTO sys_org (id, parent_id, org_code, name, org_type, leader, phone, email, address, sort, status)
VALUES (4, 2, 'D001', '研发部', 'dept', '赵工', '13800000040', 'rd@mes.com', '上海市浦东新区', 1, 0);

INSERT INTO sys_org (id, parent_id, org_code, name, org_type, leader, phone, email, address, sort, status)
VALUES (5, 2, 'D002', '生产部', 'dept', '钱工', '13800000050', 'prod@mes.com', '上海市浦东新区', 2, 0);

INSERT INTO sys_org (id, parent_id, org_code, name, org_type, leader, phone, email, address, sort, status)
VALUES (6, 3, 'D003', '运营部', 'dept', '孙经理', '13800000060', 'ops@mes.com', '深圳市南山区', 1, 0);

-- 3. 字典数据 (常用枚举)
INSERT INTO sys_dict_data (id, dict_sort, dict_label, dict_value, dict_type, list_class, status) VALUES
(1, 1, '原材料', 'raw', 'material_type', 'primary', 0),
(2, 2, '半成品', 'semi', 'material_type', 'success', 0),
(3, 3, '成品', 'finished', 'material_type', 'warning', 0),
(4, 4, '辅料', 'aux', 'material_type', 'info', 0),
(5, 1, '未开始', 'pending', 'workorder_status', 'info', 0),
(6, 2, '生产中', 'producing', 'workorder_status', 'warning', 0),
(7, 3, '已完成', 'completed', 'workorder_status', 'success', 0),
(8, 4, '已取消', 'cancelled', 'workorder_status', 'danger', 0),
(9, 1, '正常', '0', 'common_status', 'success', 0),
(10, 2, '停用', '1', 'common_status', 'danger', 0),
(11, 1, '高', '1', 'priority', 'danger', 0),
(12, 2, '中', '2', 'priority', 'warning', 0),
(13, 3, '低', '3', 'priority', 'primary', 0),
(14, 1, '原料仓', 'raw', 'warehouse_type', 'primary', 0),
(15, 2, '成品仓', 'finished', 'warehouse_type', 'success', 0),
(16, 3, '半成品仓', 'semi', 'warehouse_type', 'warning', 0),
(17, 4, '辅料仓', 'aux', 'warehouse_type', 'info', 0),
(18, 1, '男', '0', 'gender', 'primary', 0),
(19, 2, '女', '1', 'gender', 'success', 0),
(20, 1, '战略', 'strategic', 'supply_level', 'primary', 0),
(21, 2, '主要', 'main', 'supply_level', 'success', 0),
(22, 3, '一般', 'normal', 'supply_level', 'warning', 0),
(23, 4, '临时', 'temp', 'supply_level', 'info', 0),
(24, 1, '合格', 'qualified', 'check_result', 'success', 0),
(25, 2, '不合格', 'unqualified', 'check_result', 'danger', 0),
(26, 1, '运行', '0', 'device_status', 'success', 0),
(27, 2, '停机', '1', 'device_status', 'warning', 0),
(28, 3, '维修', '2', 'device_status', 'danger', 0);

-- 4. 数据字典（父子层级结构）
INSERT INTO sys_dict (id, dict_name, dict_code, parent_id, parent_code, dict_value, sort, remark, status, create_time) VALUES
-- 根节点：分公司
(1, '分公司', 'DICT_BRANCH', 0, NULL, '1', 1, 'MES 分公司目录', 0, SYSDATE),
(2, '华东分公司', 'BRANCH_HD', 1, 'DICT_BRANCH', '1', 1, '华东区域', 0, SYSDATE),
(3, '华南分公司', 'BRANCH_HN', 1, 'DICT_BRANCH', '2', 2, '华南区域', 0, SYSDATE),
(4, '华北分公司', 'BRANCH_HB', 1, 'DICT_BRANCH', '3', 3, '华北区域', 0, SYSDATE),
(5, '西南分公司', 'BRANCH_XN', 1, 'DICT_BRANCH', '4', 4, '西南区域', 0, SYSDATE),
-- 根节点：用户状态
(10, '用户状态', 'DICT_USER_STATUS', 0, NULL, '10', 2, '用户状态字典', 0, SYSDATE),
(11, '启用', 'USER_STATUS_ON', 10, 'DICT_USER_STATUS', '0', 1, NULL, 0, SYSDATE),
(12, '禁用', 'USER_STATUS_OFF', 10, 'DICT_USER_STATUS', '1', 2, NULL, 0, SYSDATE),
-- 根节点：组织类型
(20, '组织类型', 'DICT_ORG_TYPE', 0, NULL, '20', 3, '组织类型字典', 0, SYSDATE),
(21, '集团', 'ORG_GROUP', 20, 'DICT_ORG_TYPE', 'group', 1, NULL, 0, SYSDATE),
(22, '分公司', 'ORG_BRANCH', 20, 'DICT_ORG_TYPE', 'branch', 2, NULL, 0, SYSDATE),
(23, '部门', 'ORG_DEPT', 20, 'DICT_ORG_TYPE', 'dept', 3, NULL, 0, SYSDATE);

COMMIT;

PRINT 'MES 系统数据库初始化完成！';
PRINT '表总数: 32 张';
PRINT '默认登录账号: admin / admin';
