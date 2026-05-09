package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import net.sf.jsqlparser.statement.select.Join;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ReportService reportService;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private WorkspaceService workspaceService;
    /**
     * 营业额统计
     * @param begin
     * @param end
     * @return
     */
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        //当前集合存放从begin到end日期的所有日期
        List<LocalDate> dateList = new ArrayList<LocalDate>();
        LocalDate current = begin;
        while (!current.isAfter(end)) {
            dateList.add(current);
            current = current.plusDays(1);
        }

        //营业额集合。
        List<Double> turnoverList = new ArrayList<Double>();
        for(LocalDate date : dateList){
            LocalDateTime beginTime = LocalDateTime.of(date,LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date,LocalTime.MAX);
            // select sum(amount) from orders where order_time > beginTime AND order_time < endTime AND status = 5
 /*SQL 语句（sumAmountByMap）需要三个不同的变量：beginTime（开始时间）、endTime（结束时间）和 status（订单状态）。
MyBatis 的 Mapper 接口方法通常只接收一个参数。如果你想一次性把这三个“零散”的变量传给 XML 里的 SQL，你有两种主流选择：
方案 A：专门写一个 Java 类（DTO/POJO）。方案 B：使用一个 Map。*/
            Map map =new HashMap();
            map.put("begin",beginTime);
            map.put("end",endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumAmountByMap(map);
            turnover = turnover == null ? 0.0:turnover;//判断turnover是否为空，为空的话就设置为0.0
            turnoverList.add(turnover);
        }

        return TurnoverReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ",")) //集合对象取出，用，分割，就成string
                .turnoverList(StringUtils.join(turnoverList,","))
                .build();
    }

    /**
     * 统计指定区间内用户数据,用户统计
     * @param begin
     * @param end
     * @return
     */
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        LocalDate current = begin;
        while (!current.isAfter(end)) {
            dateList.add(current);
            current = current.plusDays(1);
        }
        List<Integer> totalUserList = new ArrayList<>();
        List<Integer> newUserList = new ArrayList<>();

        for(LocalDate date:dateList){
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Integer totalUser = userMapper.sumUsers(endTime);

            Map map = new HashMap();
            map.put("beginTime",beginTime);
            map.put("endTime",endTime);
            Integer newUser = userMapper.countByMap(map);
            if(newUser == null){
                newUser = 0;
            }
            newUserList.add(newUser);
            totalUserList.add(totalUser);
        }
        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .newUserList(StringUtils.join(newUserList,","))
                .totalUserList(StringUtils.join(totalUserList,","))
                .build();
    }

    /**
     * 订单统计
     * @param begin
     * @param end
     * @return
     */
    public OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        LocalDate current = begin;
        while(!current.isAfter(end)){
            dateList.add(current);
            current = current.plusDays(1);
        }

        List<Integer> ordrerCountList = new ArrayList<Integer>();
        List<Integer> validOrderCountList = new ArrayList<Integer>();
        for(LocalDate date : dateList){//如果 date 是 2026-05-08，那么这行代码执行后得到的就是 2026-05-08T00:00:00
            LocalDateTime beginTime = LocalDateTime.of(date,LocalTime.MIN);//将一个“纯日期”转换成当天的“起始时间”（即 00:00:00）
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            //每天订单总数
            Integer orderCount = getOrderCount(beginTime,endTime,null);
            if(orderCount == null){
                orderCount = 0;
            }
            ordrerCountList.add(orderCount);
            //每天有效订单总数
            Integer validOrderCount = getOrderCount(beginTime,endTime,Orders.COMPLETED);
            if(validOrderCount == null){
                validOrderCount = 0;
            }
            validOrderCountList.add(validOrderCount);
        }
        //计算时间区内的订单总数量
        Integer totalOrderCount = ordrerCountList.stream().reduce(Integer::sum).get();
        //计算时间区间内的有效订单数量
        Integer sumValidOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();
        //计算订单完成率
        Double orderCompletionRate = 0.0;
        if(totalOrderCount != 0){
            orderCompletionRate = (sumValidOrderCount.doubleValue()/totalOrderCount.doubleValue());
        }

        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .validOrderCountList(StringUtils.join(validOrderCountList,","))
                .orderCountList(StringUtils.join(ordrerCountList,","))
                .validOrderCount(sumValidOrderCount)
                .totalOrderCount(totalOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    /**
     * 根据条件统计订单数量,复用代码
     * @param begin
     * @param end
     * @param status
     * @return
     */
    private Integer getOrderCount(LocalDateTime begin,LocalDateTime end,Integer status){
        Map map = new HashMap();
        map.put("beginTime",begin);
        map.put("endTime",end);
        map.put("status",status);
        Integer orderCount = orderMapper.countOrdersByMap(map);
        return orderCount;
    }

    /**
     * 查询销量排名
     * @param begin
     * @param end
     * @return
     */
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> salesTop10 = orderMapper.getSalesTop10(beginTime,endTime);
//map映射操作：对流中的每一个 GoodsSalesDTO 对象，调用它的 getName() 方法，
// 取出菜品的名字。:: 是方法引用，等价于 dto -> dto.getName()。
        List<String> nameList = salesTop10.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        List<Integer> numberList = salesTop10.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
//collect将流中所有映射后的结果（菜品名字）收集成一个新的 List<String>
        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(nameList,","))
                .numberList(StringUtils.join(numberList,","))
                .build();
    }

    /**
     * 导出运营数据报表
     * @param response
     */
    public void exportBusinessData(HttpServletResponse response) {
        //1.查询数据库(查询最近30天的数据)，获取营业数据
        //概览数据:营业额，订单完成率，新增用户数，有效订单，平均客单价
        LocalDate dateBegin = LocalDate.now().minusDays(30);
        LocalDate dateEnd = LocalDate.now().minusDays(1);
        LocalDateTime beginTime = LocalDateTime.of(dateBegin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(dateEnd, LocalTime.MAX);
        BusinessDataVO businessDataVO = workspaceService.getBusinessData(beginTime, endTime);

        //2.查询到的数据写入excel文件里(通过POI)
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
        //基于模版文件创建一个新的excel文件
        try {
            XSSFWorkbook excel = new XSSFWorkbook(in);
            //填充数据--时间
            XSSFSheet sheet = excel.getSheet("Sheet1");
            sheet.getRow(1).getCell(1).setCellValue("时间:" + dateBegin + "至" + dateEnd);
            //概览数据：最近30天
            //获取第四行
            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(businessDataVO.getTurnover());
            row.getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessDataVO.getNewUsers());
            //获取第五行
            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessDataVO.getValidOrderCount());
            row.getCell(4).setCellValue(businessDataVO.getUnitPrice());
            //填充明细数据：具体到每一天
            for (int i = 0; i < 30; i++) {
                LocalDate date = dateBegin.plusDays(i);
                //查询某一天的营业数据
                BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));
                XSSFRow row1 = sheet.getRow(7 + i);
                row1.getCell(1).setCellValue(date.toString());
                row1.getCell(2).setCellValue(businessData.getTurnover());
                row1.getCell(3).setCellValue(businessData.getValidOrderCount());
                row1.getCell(4).setCellValue(businessData.getOrderCompletionRate());
                row1.getCell(5).setCellValue(businessData.getUnitPrice());
                row1.getCell(6).setCellValue(businessData.getNewUsers());
            }
            //3.通过输出流将excel文件下载到客户端浏览器
            ServletOutputStream outputStream = response.getOutputStream();
            excel.write(outputStream);
            //关闭资源
            outputStream.close();
            excel.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
