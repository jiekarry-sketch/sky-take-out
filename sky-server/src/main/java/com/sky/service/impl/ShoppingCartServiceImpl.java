package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        // 校验商品是否在售
        Long dishId = shoppingCartDTO.getDishId();
        Long setmealId = shoppingCartDTO.getSetmealId();

        if (dishId != null) {
            // 菜品状态校验
            Dish dish = dishMapper.getById(dishId);
            if (dish == null || !StatusConstant.ENABLE.equals(dish.getStatus())) {
                throw new ShoppingCartBusinessException("该菜品已停售，无法添加到购物车");
            }
        } else if (setmealId != null) {
            // 套餐状态校验
            Setmeal setmeal = setmealMapper.getById(setmealId);
            if (setmeal == null || !StatusConstant.ENABLE.equals(setmeal.getStatus())) {
                throw new ShoppingCartBusinessException("该套餐已停售，无法添加到购物车");
            }
        }

        //判断当前加入购物车的商品是否已经存在
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);

        //如果已经存在，将数目number+1
        if (shoppingCartList != null && shoppingCartList.size() > 0) {
            ShoppingCart cart = shoppingCartList.get(0);
            cart.setNumber(cart.getNumber() + 1);
            shoppingCartMapper.updateNumberById(cart);
        } else {
            //如果不存在，需要插入一条购物车数据
            if (dishId != null) {
                Dish dish = dishMapper.getById(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
            } else {
                Setmeal setmeal = setmealMapper.getById(setmealId);
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);
        }
    }

    /**
     * 查看购物车
     * @param id
     * @return
     */
    public List<ShoppingCart> listShoppingCart(Long id) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(id);
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);
        return shoppingCartList;
    }

    /**
     * 清空购物车
     */
    public void cleanShoppingCart() {
        Long currentId = BaseContext.getCurrentId();
//        ShoppingCart shoppingCart = ShoppingCart.builder()
//                .userId(currentId)
//                .build();
        shoppingCartMapper.deleteByUserId(currentId);
    }
}
