package com.sky.dto;

import com.sky.entity.DishFlavor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class DishDTO implements Serializable {

    private Long id;

    @NotBlank(message = "菜品名称不能为空")
    @Size(max = 32, message = "菜品名称长度不能超过32个字符")
    private String name;

    @NotNull(message = "菜品分类不能为空")
    private Long categoryId;

    @NotNull(message = "菜品价格不能为空")
    private BigDecimal price;

    @NotBlank(message = "菜品图片不能为空")
    private String image;

    @Size(max = 255, message = "描述信息长度不能超过255个字符")
    private String description;

    private Integer status;

    private List<DishFlavor> flavors = new ArrayList<>();

}
