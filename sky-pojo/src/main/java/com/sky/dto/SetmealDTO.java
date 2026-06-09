package com.sky.dto;

import com.sky.entity.SetmealDish;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class SetmealDTO implements Serializable {

    private Long id;

    @NotNull(message = "套餐分类不能为空")
    private Long categoryId;

    @NotBlank(message = "套餐名称不能为空")
    @Size(max = 32, message = "套餐名称长度不能超过32个字符")
    private String name;

    @NotNull(message = "套餐价格不能为空")
    private BigDecimal price;

    private Integer status;

    @Size(max = 255, message = "描述信息长度不能超过255个字符")
    private String description;

    @NotBlank(message = "套餐图片不能为空")
    private String image;

    private List<SetmealDish> setmealDishes = new ArrayList<>();

}
