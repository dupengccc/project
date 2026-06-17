package com.mes.admin.modules.system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDTO {

    private Long id;

    private String username;

    private String nickname;

    private String avatar;

    private List<String> roles;

    private List<String> permissions;

    private Long orgId;

    private String orgName;
}
