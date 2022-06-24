package flink.project.cep.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description:
 *
 * @author yangbin216
 * @date 2022/4/2 11:23
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {

    private String orderId;
    private String status;
    private long orderCreateTime;
    private Double price;
}