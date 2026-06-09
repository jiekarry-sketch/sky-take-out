package com.sky.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

@Data
public class CategoryDTO implements Serializable {

    private Long id;

    @NotNull(message = "分类类型不能为空")
    private Integer type;

    @NotBlank(message = "分类名称不能为空")
    @Size(max = 32, message = "分类名称长度不能超过32个字符")
    private String name;

    private Integer sort;

}
