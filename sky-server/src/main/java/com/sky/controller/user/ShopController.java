package com.sky.controller.user;


import com.sky.result.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("userShopController")
@RequestMapping("/user/shop")
@Slf4j
@Tag(name="店铺相关接口")
public class ShopController {
    public static final String KEY = "SHOP_STATUS";
    @Autowired
    private RedisTemplate redisTemplate;
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
