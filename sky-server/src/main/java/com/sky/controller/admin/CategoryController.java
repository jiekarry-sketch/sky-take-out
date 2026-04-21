package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import com.sky.service.EmployeeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜品管理
 */
@RestController
@Slf4j
@RequestMapping("/admin/category")
@Api(tags="分类相关接口")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 根据类型查询菜品
     * @param type
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据类型查询菜品")
    public Result<List<Category>> list( Integer type) {
        log.info("根据类型查询菜品");
        List<Category> list= categoryService.list(type);
        return Result.success(list);
    }

    /**
     * 修改菜品分类
     * @param categoryDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改分类")
    public Result<String> update(@RequestBody CategoryDTO categoryDTO) {
        log.info("修改菜品分类:{}",categoryDTO);
        categoryService.update(categoryDTO);
        return Result.success();
    }

    /**
     * 分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分页查询")
    public Result<PageResult> page(CategoryPageQueryDTO categoryPageQueryDTO) {
        log.info("分页查询:参数为{}",categoryPageQueryDTO);
        PageResult pageresult = categoryService.pageQuery(categoryPageQueryDTO);
        return Result.success(pageresult);
    }

    /**
     * 启用、禁用分类
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("启用、禁用分类")
    public Result StartOrStop(@PathVariable("status") Integer status,Long id) {
        log.info("启用禁用:{},{}",status,id);
        categoryService.StartOrStop(status,id);
        return Result.success();
    }

    /**
     * 新增分类
     * @param categoryDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增分类")
    public Result save(@RequestBody CategoryDTO categoryDTO) {
        log.info("新增分类:{}",categoryDTO);
        categoryService.save(categoryDTO);
        return Result.success();
    }

    /**
     * 根据id删除分类
     * @param name
     * @param id
     * @return
     */
    @DeleteMapping
    @ApiOperation("根据id删除分类")
    public Result delete(String name,Long id){
       log.info("删除菜品分类:name:{},id:{}",name,id);
       categoryService.delete(id);
       return Result.success();
    }
}
