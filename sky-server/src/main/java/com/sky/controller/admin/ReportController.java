package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

/**
 * 数据统计相关接口
 */
@RestController
@RequestMapping("/admin/report")
@Slf4j
@Tag(name="数据统计相关接口")
public class ReportController {
    @Autowired
    private ReportService reportService;

    /**
     * 营业额统计接口
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/turnoverStatistics")
    @Operation(summary ="营业额统计接口")
    public Result<TurnoverReportVO> turnoverStatistic(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("营业额数据统计:日期从{}到{}",begin,end);
        TurnoverReportVO  turnoverReportVO = reportService.getTurnoverStatistics(begin,end);
        return Result.success(turnoverReportVO);
    }

    /**
     * 用户统计接口
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/userStatistics")
    @Operation(summary ="用户统计接口")
    public Result<UserReportVO> userStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("用户活跃数据统计:日期从{}到{}",begin,end);
        UserReportVO userReportVO = reportService.getUserStatistics(begin,end);
        return  Result.success(userReportVO);
    }

    /**
     * 订单统计接口
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/ordersStatistics")
    @Operation(summary ="订单统计接口")
    public Result<OrderReportVO> ordersStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("订单统计数据,日期：{}到{}",begin,end);
        OrderReportVO  orderReportVO = reportService.getOrdersStatistics(begin,end);
        return   Result.success(orderReportVO);
    }

    /**
     * 查询销量排名前10的商品
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/top10")
    @Operation(summary ="查询销量排序top10接口")
    public Result<SalesTop10ReportVO>  top10(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("销量排名top10，时间从{}到{}",begin,end);
        SalesTop10ReportVO salesTop10ReportVO = reportService.getSalesTop10(begin,end);
        return  Result.success(salesTop10ReportVO);
    }
    /**
     * 导出运营数据报表
     * @param response
     * @throws IOException
     */
    @GetMapping("/export")
    @Operation(summary ="导出运营数据报表")
    public void export(HttpServletResponse response) throws IOException {
        reportService.exportBusinessData(response);
    }
}
