package com.sky.controller.admin;


import com.sky.result.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;


@RestController("adminShopController") //给容器创建bean的名字区分，用户和客户端
@RequestMapping("/admin/shop")
@Slf4j
@Tag(name="店铺营业状态相关接口")
public class ShopController {
    public static final String KEY = "SHOP_STATUS";

    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 设置店铺营业状态
     * @param status
     * @return
     */
    @PutMapping("/{status}")
    @Operation(summary = "设置店铺营业状态")
    public Result setStatus(@PathVariable Integer status){
        log.info("设置店铺营业状态,status={}",status==1?"营业中":"打烊中");
        redisTemplate.opsForValue().set(KEY,status);
        return Result.success();
    }

    /**
     * 获取店铺营业状态
     * @return
     */
    @GetMapping("/status")
    @Operation(summary ="查询店铺营业状态")
    public Result<Integer> getStatus(){
        Integer shopStatus = (Integer)redisTemplate.opsForValue().get(KEY);
        log.info("查询到店铺营业状态为:{}",shopStatus==1?"营业中":"打烊中");
        return Result.success(shopStatus);
    }
}
