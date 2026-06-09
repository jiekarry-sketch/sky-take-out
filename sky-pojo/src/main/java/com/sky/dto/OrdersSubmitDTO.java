package com.sky.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrdersSubmitDTO implements Serializable {

    @NotNull(message = "地址簿id不能为空")
    private Long addressBookId;

    @Min(value = 1, message = "支付方式不合法")
    private int payMethod;

    private String remark;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime estimatedDeliveryTime;

    private Integer deliveryStatus;
    private Integer tablewareNumber;
    private Integer tablewareStatus;
    private Integer packAmount;

    @NotNull(message = "订单金额不能为空")
    @Min(value = 1, message = "订单金额必须大于0")
    private BigDecimal amount;
}
