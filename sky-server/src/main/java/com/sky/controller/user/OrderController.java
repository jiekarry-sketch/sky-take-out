package com.sky.controller.user;

import com.sky.context.BaseContext;
import com.sky.dto.OrdersDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.Orders;
import com.sky.exception.OrderBusinessException;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("userOrderController")
@RequestMapping("/user/order")
@Slf4j
@Tag(name="C端-订单相关接口")
public class OrderController {
    @Autowired
    private OrderService orderService;

    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    @PostMapping("/submit")
    @Operation(summary ="用户下单")
    public Result<OrderSubmitVO> sumbitOrder(@RequestBody OrdersSubmitDTO ordersSubmitDTO){
        log.info("用户下单，参数为:{}",ordersSubmitDTO);
        OrderSubmitVO orderSubmitVO = orderService.sumbitOrder(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }
    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    @PutMapping("/payment")
    @Operation(summary ="订单支付")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("订单支付：{}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
        log.info("生成预支付交易单：{}", orderPaymentVO);
        orderService.paySuccess(ordersPaymentDTO.getOrderNumber());
        return Result.success(orderPaymentVO);
    }

    /**
     * 查询订单详情
     * @param id
     * @return
     */
    @GetMapping("/orderDetail/{id}")
    @Operation(summary ="查询订单详情")
    public Result<OrderVO> getByOrderId(@PathVariable("id") Long id){
        log.info("查询订单id为{}的信息...",id);
        OrderVO orderVO = orderService.details(id);
        return Result.success(orderVO);
    }

    /**
     * 查询历史订单
     * @param page
     * @param pageSize
     * @param status 订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消
     * @return
     */
    @GetMapping("/historyOrders")
    @Operation(summary ="历史订单查询")
    public Result<PageResult> page(int page,int pageSize,Integer status){
        log.info("正在查询历史订单:共{}页，每页{}条,订单状态码{}",page,pageSize,status);
        PageResult pageResult = orderService.pageQuery(page,pageSize,status);
        return Result.success(pageResult);
    }

    @PutMapping("/cancel/{id}")
    @Operation(summary ="取消订单")
    public Result cancelById(@PathVariable("id") Long id){
        log.info("正在取消订单id为{}的订单...",id);
        //先查询当前订单是否完成，如果未完成则可以取消
        Orders order = orderService.getById(id);
        if(order!=null && order.getStatus()!=5){
            //更改status为6
            orderService.cancelOrder(id);
        }
        else{
            throw new OrderBusinessException("该订单已经完成，无法取消！");
        }
        return Result.success();
    }

    /**
     * 再来一单
     * @param id
     */
    @PostMapping("/repetition/{id}")
    @Operation(summary ="再来一单")
    public Result repetition(@PathVariable("id") Long id){
        log.info("用户发起请求：再来一单，对应订单id{}",id);
        orderService.repetition(id);
        return Result.success();
    }

    /**
     * TODO
     * @param id
     * @return
     */
    @GetMapping("/reminder/{id}")
    @Operation(summary ="催单")
    public Result reminder(@PathVariable("id") Long id){
        log.info("用户正在催单，订单id为{}...",id);
        orderService.reminder(id);
        return Result.success();
    }
}
