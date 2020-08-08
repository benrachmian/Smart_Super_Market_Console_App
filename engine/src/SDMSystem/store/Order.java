package SDMSystem.store;

import SDMSystem.customer.Customer;
import SDMSystem.product.ProductInStore;

import java.util.Collection;
import java.util.Date;

public class Order {
    private Date orderDate;
    private Collection<ProductInStore> productsInOrder;
    private Customer whoOrdered;
    private float productsCost;
    private float delivaryCost;

    public Order(Date orderDate, Customer whoOrdered, float delivaryCost) {
        this.orderDate = orderDate;
        this.productsInOrder = null;
        this.whoOrdered = whoOrdered;
        this.productsCost = 0;
        this.delivaryCost = delivaryCost;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void addProductToOrder(ProductInStore product){
        productsInOrder.add(product);
        productsCost += product.getPrice();
    }

    @Override
    public String toString() {
        return "Order Date=" + orderDate.toString() +
                "\nNumber of products: " + productsInOrder.size() +
                "\nProducts cost: " + productsCost +
                "\nDelivery cost: " + delivaryCost +
                "\nOrder cost: " + (productsCost + delivaryCost);
    }
}
