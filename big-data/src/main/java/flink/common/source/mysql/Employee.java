package flink.common.source.mysql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {

    private int emp_no;
    private Date birth_date;
    private String first_name;
    private String last_name;
//    private Enum gender;
    private Date hire_date;

}
