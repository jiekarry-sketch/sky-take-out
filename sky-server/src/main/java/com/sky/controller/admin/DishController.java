package com.sky.controller.admin;

import com.github.pagehelper.Page;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜品相关接口
 */
@RestController
@Slf4j
@RequestMapping("/admin/dish")
@Tag(name="菜品相关接口")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 新增菜品和对应口味
     * @param dishDTO
     * @return
     */
    @PostMapping
    @Operation(summary ="新增菜品")
    public Result<String> save(@Valid @RequestBody DishDTO dishDTO) {
        log.info("新增菜品:{}",dishDTO);
        dishService.saveWithFlavor(dishDTO);

        //清理受影响的缓存数据，找到对应的key清理
        String key = "dish_"+dishDTO.getCategoryId();
        cleanCache(key);
        return Result.success();
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @Operation(summary = "菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品分页查询，参数为:{}",dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 批量删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    @Operation(summary ="批量删除菜品")
    public Result delete(@RequestParam List<Long> ids){
        log.info("批量删除菜品 ids = {}",ids);
        dishService.batchDelete(ids);
        cleanCache("dish_*");
        return Result.success();
    }

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @Operation(summary ="根据id查询菜品")
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("根据id查询菜品:{}",id);
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    /**
     * 修改菜品
     * @param dishDTO
     * @return
     */
    @PutMapping
    @Operation(summary ="修改菜品")
    public Result update(@Valid @RequestBody DishDTO dishDTO){
        log.info("修改菜品:{}",dishDTO);
        dishService.dishUpdayeWithFlavor(dishDTO);
        cleanCache("dish_*");
        return Result.success();
    }

    /**
     * 菜品起售、停售
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @Operation(summary ="菜品起售、停售")
    public Result startOrStop(@PathVariable("status") Integer status,Long id) {
        log.info("菜品起售停售:status={},id={}",status,id);
        dishService.startOrStop(status,id);
        cleanCache("dish_*");
        return Result.success();
    }
    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @Operation(summary ="根据分类id查询菜品")
    public Result<List<Dish>> getByCategoryId(Long categoryId){
        log.info("根据分类id查询菜品:{}",categoryId);
        List<Dish> list = dishService.list(categoryId);
        return Result.success(list);
    }

    /**
     * 清理缓存数据（使用SCAN替代KEYS，避免阻塞Redis）
     * @param pattern
     */
    private void cleanCache(String pattern){
        List<Object> keys = new ArrayList<>();
        ScanOptions options = ScanOptions.scanOptions().match(pattern).count(100).build();
        try (var cursor = redisTemplate.scan(options)) {
            while (cursor.hasNext()) {
                keys.add(cursor.next());
            }
        }
        if (!keys.isEmpty()) {
            redisTemplate.delete(keys);
            log.debug("清理缓存: pattern={}, 命中{}个key", pattern, keys.size());
        }
    }
}
