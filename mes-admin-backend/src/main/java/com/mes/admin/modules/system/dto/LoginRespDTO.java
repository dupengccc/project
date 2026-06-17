package com.mes.admin.modules.system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRespDTO {

    private String token;

    @Builder.Default
    private String tokenType = "Bearer";

    private Long expiresIn;
}
