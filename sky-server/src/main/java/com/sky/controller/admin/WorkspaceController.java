package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
@RequestMapping("/admin/workspace")
@Slf4j
@Tag(name="工作台接口")
public class WorkspaceController {
    @Autowired
    private WorkspaceService workspaceService;

    /**
     * 查询今日运营数据
     * @return
     */
    @GetMapping("/businessData")
    @Operation(summary ="查询今日运营数据")
    public Result<BusinessDataVO> businessData(){
        log.info("正在查询今日运营数据...");
        //获得当天的开始时间
        LocalDateTime begin = LocalDateTime.now().with(LocalTime.MIN);
        //获得当天的结束时间
        LocalDateTime end = LocalDateTime.now().with(LocalTime.MAX);
        BusinessDataVO businessDataVO = workspaceService.getBusinessData(begin,end);
        return  Result.success(businessDataVO);
    }

    /**
     * 查询菜品总览
     * @return
     */
    @GetMapping("/overviewDishes")
    @Operation(summary ="查询菜品总览")
    public Result<DishOverViewVO> overviewDishes(){
        log.info("正在查询菜品总览...");
        DishOverViewVO dishOverViewVO = workspaceService.overviewDishes();
        return   Result.success(dishOverViewVO);
    }

    /**
     * 查询套餐总览
     * @return
     */
    @GetMapping("/overviewSetmeals")
    @Operation(summary ="查询套餐总览")
    public Result<SetmealOverViewVO> overviewSetmeals(){
        log.info("正在查询套餐总览...");
        SetmealOverViewVO setmealOverViewVO = workspaceService.overviewSetmeals();
        return   Result.success(setmealOverViewVO);
    }
    @GetMapping("/overviewOrders")
    @Operation(summary ="查询订单管理数据")
    public Result<OrderOverViewVO> overviewOrders(){
        log.info("正在查询订单管理数据...");
        OrderOverViewVO orderOverViewVO = workspaceService.overviewOrders();
        return   Result.success(orderOverViewVO);
    }
}
