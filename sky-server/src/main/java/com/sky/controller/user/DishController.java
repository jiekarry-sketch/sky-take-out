package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userDishController")
@Tag(name="C端-浏览菜品接口")
@RequestMapping("/user/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @Operation(summary ="根据分类id查询菜品")
    @GetMapping("/list")
    public Result<List<DishVO>> getByCategoryId(Long categoryId){
        //添加判断:缓存是否存在{缓存:每个分类做一个缓存}
        //构造redis中key,规则:dish_分类id
        String key = "dish_"+categoryId;
        List<DishVO> list= (List<DishVO>)redisTemplate.opsForValue().get(key);
        if(list!=null &&  list.size()>0){
            //如果存在，直接返回结果，无需查询数据库
            return Result.success(list);
        }

        //查询数据库
        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);//查询起售中的菜品
        //如果不存在，查询数据库，将查询到的数据放入redis中
        list = dishService.listWithFlavor(dish);
        redisTemplate.opsForValue().set(key,list);
        return Result.success(list);
    }

}
