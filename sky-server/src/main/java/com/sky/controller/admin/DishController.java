package com.sky.controller.admin;

import com.sky.dto.CategoryPageQueryDTO;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 菜品相关接口
 */
@RestController
@Slf4j
@RequestMapping("/admin/dish")
@Api(tags="菜品相关接口")
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
    @ApiOperation("新增菜品")
    public Result<String> save(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品:{}",dishDTO);
        dishService.saveWithFlavor(dishDTO);
        //清理受影响的缓存数据，找到对应的key清理
        String key = "dish_"+dishDTO.getCategoryId();
        //redisTemplate.delete(key);
        cleanCache(key);
        return Result.success();
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
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
    @ApiOperation("批量删除菜品")
    public Result delete(@RequestParam List<Long> ids) {
        log.info("根据id批量删除菜品:参数为{}",ids);
        dishService.batchDelete(ids);
        //这里可能会影响多个key;我们采用简单的方法:删除菜品后，把所有的菜品缓存都删掉
        //不能直接redisTemplate.delete("dish_*"),因为删除的时候是不能识别通配符的
        // 先获取所有以dish_开头的key
        //Set keys = redisTemplate.keys("dish_*");
        //redisTemplate.delete(keys);//获取后直接放入一个collection集合keys删除
        cleanCache("dish_*");
        return Result.success();
    }

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("根据id查询菜品:{}",id);
        DishVO dishVO =  dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    /**
     *
     * 修改菜品
     * @param dishDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改菜品")
    public Result update(@RequestBody DishDTO dishDTO) {
        log.info("修改菜品:参数为:{}",dishDTO);
        dishService.dishUpdayeWithFlavor(dishDTO);
        //修改操作包含多个对象:名称、价格，这些影响较小
        //修改分类的话就相当于新增，所以得把这个分类的也得清除了，相当于涉及到的分类我们都要清缓存
        //综合下来看，我们不如统一清理所有缓存数据
        //Set keys = redisTemplate.keys("dish_*");
        //redisTemplate.delete(keys);
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
    @ApiOperation("菜品起售、停售")
    public Result startOrStop(@PathVariable("status") Integer status,Long id) {
        log.info("菜品待售状态:{}",status,id);
        dishService.startOrStop(status,id);
        //Set keys = redisTemplate.keys("dish_*");
        //redisTemplate.delete(keys);
        cleanCache("dish_*");
        return Result.success();
    }
    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> getByCategoryId(Long categoryId){
        log.info("根据分类id查询菜品:{}",categoryId);
        List<Dish> list = dishService.list(categoryId);
        return Result.success(list);
    }

    /**
     *
     * 清理缓存数据
     * @param pattern
     */
    private void cleanCache(String pattern){
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }
}
