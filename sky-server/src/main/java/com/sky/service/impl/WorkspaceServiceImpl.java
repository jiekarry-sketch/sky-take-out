package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.entity.Orders;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class WorkspaceServiceImpl implements WorkspaceService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    /**
     * 查询今日运营数据
     * @return
     */
    public BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end) {
//        LocalDate todayDate = LocalDate.now();
//        LocalDateTime todayDateTimeMIN = LocalDateTime.of(todayDate, LocalTime.MIN);
//        LocalDateTime todayDateTimeMAX = LocalDateTime.of(todayDate, LocalTime.MAX);
        Map map = new HashMap();
        map.put("beginTime",begin);
        map.put("endTime",end);
        Integer newUser = userMapper.countByMap(map);
        if(newUser == null){
            newUser = 0;
        }
        Integer totalOrderCount = orderMapper.countOrdersByMap(map);
        map.put("status", Orders.COMPLETED);
        Integer validOrderCount = orderMapper.countOrdersByMap(map);
        Double orderCompletionRate = 0.0;
        if(totalOrderCount != 0){
            orderCompletionRate =  (double)validOrderCount/totalOrderCount;
        }
        Double turnover = orderMapper.sumAmountByMap(map);
        if(turnover == null){
            turnover = 0.0;
        }
        Double unitPrice = 0.0;
        if(validOrderCount != 0) {
            unitPrice = turnover / validOrderCount;
        }
        return BusinessDataVO.builder()
                .newUsers(newUser)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .turnover(turnover)
                .unitPrice(unitPrice)
                .build();
    }

    /**
     * 查询菜品总览
     * @return
     */
    public DishOverViewVO overviewDishes() {
        Integer discontinued = dishMapper.countByStatus(StatusConstant.DISABLE);
        Integer sold = dishMapper.countByStatus(StatusConstant.ENABLE);
        return DishOverViewVO.builder()
                .discontinued(discontinued)
                .sold(sold)
                .build();
    }

    /**
     * 查询套餐总览
     * @return
     */
    public SetmealOverViewVO overviewSetmeals() {
        Integer discontinued = setmealMapper.countByStatus(StatusConstant.DISABLE);
        Integer sold = setmealMapper.countByStatus(StatusConstant.ENABLE);
        return SetmealOverViewVO.builder()
                .discontinued(discontinued)
                .sold(sold)
                .build();
    }

    /**
     * 查询订单管理数据
     * @return
     */
    public OrderOverViewVO overviewOrders() {
        LocalDate todayDate = LocalDate.now();
        LocalDateTime todayDateTimeMAX = LocalDateTime.of(todayDate, LocalTime.MAX);
        Map map = new HashMap();
        map.put("endTime",todayDateTimeMAX);
        Integer allOrders = orderMapper.countOrdersByMap(map);
        Integer cancelledOrders = orderMapper.countStatus(Orders.CANCELLED);
        Integer completedOrders = orderMapper.countStatus(Orders.COMPLETED);
        Integer deliveredOrders = orderMapper.countStatus(Orders.CONFIRMED);
        Integer waitingOrders = orderMapper.countStatus(Orders.TO_BE_CONFIRMED);
        return OrderOverViewVO.builder()
                .allOrders(allOrders)
                .cancelledOrders(cancelledOrders)
                .completedOrders(completedOrders)
                .deliveredOrders(deliveredOrders)
                .waitingOrders(waitingOrders)
                .build();
    }
}
