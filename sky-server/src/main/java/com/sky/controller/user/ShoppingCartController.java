package com.sky.controller.user;

import com.sky.context.BaseContext;
import com.sky.dto.DishDTO;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/shoppingCart")
@Slf4j
@Tag(name="C端-购物车相关接口")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;
    /**
     * 添加购物车
     * @param shoppingCartDTO
     * @return
     */
    @PostMapping("/add")
    @Operation(summary ="添加购物车")
    public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info("添加购物车商品信息为:{}",shoppingCartDTO);
        shoppingCartService.addShoppingCart(shoppingCartDTO);
        return Result.success();
    }
    /**
     *
     * 查看购物车
     * @return
     */
    @GetMapping("/list")
    @Operation(summary ="查看购物车")
    public Result<List<ShoppingCart>> list(Long id) {
        id = BaseContext.getCurrentId();
        log.info("用户id:{}正在查看购物车",id);
        List<ShoppingCart> list = shoppingCartService.listShoppingCart(id);
        return Result.success(list);
    }

    @DeleteMapping("/clean")
    @Operation(summary ="清空购物车")
    public Result clean(){
        log.info("正在清空购物车");
        shoppingCartService.cleanShoppingCart();
        return Result.success();
    }
}
