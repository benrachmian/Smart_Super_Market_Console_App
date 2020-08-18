package SDMSystem.store;

import SDMSystem.customer.Customer;
import SDMSystem.product.ProductInStore;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

public class Order {
    private static int generatedSerialNumber = 1000;
    private Date orderDate;
    private Collection<ProductInStore> productsInOrder;
    //private Customer whoOrdered;
    private float productsCost;
    private float deliveryCost;
    private final int orderSerialNumber;


    public Order(Date orderDate, Collection<ProductInStore> productsInOrder, float productsCost, float deliveryCost) {
        this.orderSerialNumber = generatedSerialNumber++;
        this.orderDate = orderDate;
        this.productsInOrder = productsInOrder;
        this.productsCost = productsCost;
        this.deliveryCost = deliveryCost;

    }

//    public Order(){
//        this.orderDate = null;
//        this.productsInOrder = new LinkedList<>();
//        //this.whoOrdered = null;
//        this.productsCost = 0;
//        this.deliveryCost = 0;
//    }

//    public Order(Date orderDate, /*Customer whoOrdered*/ float deliveryCost) {
//        this.orderSerialNumber = generatedSerialNumber++;
//        this.orderDate = orderDate;
//        this.productsInOrder = null;
//        //this.whoOrdered = whoOrdered;
//        this.productsCost = 0;
//        this.deliveryCost = deliveryCost;
//    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public void addProductToOrder(ProductInStore product){
        productsInOrder.add(product);
        productsCost += product.getPrice();
    }



//    @Override
//    public String toString() {
//        return "Order Date=" + orderDate.toString() +
//                "\nNumber of products: " + productsInOrder.size() +
//                "\nProducts cost: " + productsCost +
//                "\nDelivery cost: " + deliveryCost +
//                "\nOrder cost: " + (productsCost + deliveryCost);
//    }

    public float getDeliveryCost() {
        return deliveryCost;
    }

    public float getProductsCost() {
        return productsCost;
    }

    public Collection<ProductInStore> getProductsInOrder() {
        return productsInOrder;
    }
}
