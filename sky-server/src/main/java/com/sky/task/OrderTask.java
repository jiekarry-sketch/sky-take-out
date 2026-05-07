package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务类、定时处理订单状态
 */
@Component
@Slf4j
public class OrderTask {
    /**
     * 处理超时订单方法
     * 无返回值
     */
    @Autowired
    private OrderMapper orderMapper;
    @Scheduled(cron = "0 * * * * ?") //spring task 定时任务，每分钟触发一次
    public void processTimeoutOrder(){
        log.info("定时处理超时订单:{}", LocalDateTime.now());
        //select * from orders where status = ? AND order_time< LocalDateTime.now()-15min
        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT ,time);
        if(ordersList != null && !ordersList.isEmpty()){
            for(Orders order : ordersList){
                order.setStatus(Orders.CANCELLED);
                order.setCancelReason("订单超时，自动取消");
                order.setCancelTime(LocalDateTime.now());
                orderMapper.update(order);
            }
        }
    }

    /**
     * 处理一直处于派送中的订单
     */
    @Scheduled(cron ="0 0 1 * * ? ")     //每天凌晨1点执行
    public void processDeliveryOrder(){  //处理一直处于配送中的订单
        log.info("处理一直处于配送中的订单:{}", LocalDateTime.now());
        //select * from ordets where status = ? AND ...
        //select * from orders where status = ? and
        LocalDateTime time = LocalDateTime.now().plusHours(-1);
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS, time);
        if(ordersList != null && !ordersList.isEmpty()){
            for(Orders orders : ordersList){
                orders.setStatus(Orders.COMPLETED);
                orders.setDeliveryTime(LocalDateTime.now());
                orderMapper.update(orders);
            }
        }
    }
}

//Spring Task 定时任务框架
/**
 * cron 字符串 定义任务触发的时间。
 *[秒] [分] [时] [日] [月] [周] [年(可选)]
 * AI生成就行
 */
