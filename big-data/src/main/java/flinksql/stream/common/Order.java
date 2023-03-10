package flinksql.stream.common;

public class Order {

    public Long user;
    public String product;
    public int amount;

    // for POJO detection in DataStream API
    public Order() {
    }

    // for structured type detection in Table API
    public Order(Long user, String product, int amount) {
        this.user = user;
        this.product = product;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Order{"
                + "user="
                + user
                + ", product='"
                + product
                + '\''
                + ", amount="
                + amount
                + '}';
    }
}
