package com.sky.controller.user;

import com.sky.entity.Category;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userCategoryController")
@Slf4j
@Tag(name="C端-分类接口")
@RequestMapping("/user/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    /**
     * 条件查询(1.菜品分类;2.套餐分类)
     * @return
     */
    @GetMapping("/list")
    @Operation(summary ="条件查询")
    public Result<List<Category>> list(Integer type) {
        log.info("根据类型查询菜品,参数:{}",type);
        List<Category> list = categoryService.list(type);
        return Result.success(list);
    }

}
