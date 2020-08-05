package SDMSystem.Store;

import SDMSystem.Customer.Customer;
import SDMSystem.Product.Product;

import java.util.Collection;
import java.util.Date;

public class Order {
    private Date orderDate;
    private Collection<Product> productsInOrder;
    private Customer whoOrdered;

    public Order(Date orderDate, Customer whoOrdered) {
        this.orderDate = orderDate;
        this.productsInOrder = null;
        this.whoOrdered = whoOrdered;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void addProductToOrder(Product product){
        productsInOrder.add(product);
    }
}
