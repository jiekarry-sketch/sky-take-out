package com.sky.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "员工登录时传递的数据模型") //提取 description 属性的值，并在生成的 API 文档中展示出来
public class EmployeeLoginDTO implements Serializable {

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码")
    private String password;

}