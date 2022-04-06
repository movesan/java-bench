package flink1_14.cep.beans;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @description:
 *
 * @author yangbin216
 * @date 2022/4/2 10:12
 * @version 1.0
 */
@AllArgsConstructor
@Data
public class OrderDetail {

    private String orderId;
    private String status;
    private String orderCreateTime;
    private Double price;

}