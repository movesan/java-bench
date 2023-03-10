package flink.common.source.mysql;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MysqlSource extends RichSourceFunction<Employee> {

    PreparedStatement ps;
    private Connection connection;

    /**
     * open() 方法中建立连接，这样不用每次 invoke 的时候都要建立连接和释放连接。
     *
     * @param parameters
     * @throws Exception
     */
    @Override
    public void open(Configuration parameters) throws Exception {

        super.open(parameters);
        connection = getConnection();
        String sql = "select * from employees;"; // 编写具体逻辑代码
        ps = this.connection.prepareStatement(sql);
    }

    /**
     * 程序执行完毕就可以进行，关闭连接和释放资源的动作了
     *
     * @throws Exception
     */
    @Override
    public void close() throws Exception {

        super.close();
        if (connection != null) {
            //关闭连接和释放资源
            connection.close();
        }
        if (ps != null) {

            ps.close();
        }
    }

    @Override
    public void run(SourceContext<Employee> ctx) throws Exception {

        ResultSet resultSet = ps.executeQuery(); // 执行SQL语句返回结果集
        while (resultSet.next()) {

            Employee Employee = new Employee(
                    resultSet.getInt("emp_no"),
                    resultSet.getDate("birth_date"),
                    resultSet.getString("first_name"),
                    resultSet.getString("last_name"),
                    resultSet.getDate("hire_date"));
            ctx.collect(Employee);
        }
    }

    @Override
    public void cancel() {

    }

    private static Connection getConnection() {

        Connection con = null;
        try {

            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/employees?useUnicode=true&characterEncoding=UTF-8", "root", "Ms19920625");
        } catch (Exception e) {
            System.out.println("-----------mysql get connection has exception , msg = " + e.getMessage());
        }
        return con;
    }
}
