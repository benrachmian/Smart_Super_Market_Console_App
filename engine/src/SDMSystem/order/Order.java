package SDMSystem.order;

import SDMSystem.HasSerialNumber;
import SDMSystem.product.ProductInStore;
import SDMSystemDTO.product.DTOProductInStore;
import SDMSystemDTO.product.WayOfBuying;
import SDMSystemDTO.order.DTOOrder;
import javafx.util.Pair;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

public abstract class Order implements Serializable, HasSerialNumber<Integer> {
    private static int generatedSerialNumber = 1000;
    private Date orderDate;
    private Collection<Pair<Float,ProductInStore>> productsInOrder;
    ///private Collection<Order> subOrders;
    //private Customer whoOrdered;
    private float productsCost;
    private float deliveryCost;
    private int orderSerialNumber;
    ///private Map<Integer, Store> storesFromWhomTheOrderWasMade;
    private int amountOfProducts;
    private int amountOfProductsKinds;


    public Order(Date orderDate,
                 Collection<Pair<Float,ProductInStore>> productsInOrder,
                 float productsCost,
                 float deliveryCost,
                 //Map<Integer,Store> storesFromWhomTheOrderWasMade,
                 int amountOfProducts,
                 int amountOfProductsKinds){
                 //Collection<Order> subOrders) {
        this.orderSerialNumber = generatedSerialNumber++;
        this.orderDate = orderDate;
        this.productsInOrder = productsInOrder;
        this.productsCost = productsCost;
        this.deliveryCost = deliveryCost;
        //this.storesFromWhomTheOrderWasMade = storesFromWhomTheOrderWasMade;
        this.amountOfProducts = amountOfProducts;
        this.amountOfProductsKinds = amountOfProductsKinds;
        //this.subOrders = subOrders;

    }

    public void generateNewSerialNumber(){
        this.orderSerialNumber = generatedSerialNumber++;
    }

//    public Map<Integer, Store> getStoresFromWhomTheOrderWasMade() {
//        return storesFromWhomTheOrderWasMade;
//    }
//
//    public Collection<Order> getSubOrders() {
//        return subOrders;
//    }




    public abstract DTOOrder createDTOOrderFromOrder();
//        DTOOrder dtoOrder = new DTOOrder(getOrderDate(),
//                getDTOProductsInOrder(),
//                getProductsCost(),
//                getDeliveryCost(),
//                getOrderSerialNumber(),
//                createDTOStoresTheProductBelongsTo(),
//                getAmountOfProducts(),
//                getAmountOfProductsKinds());
//
//        return dtoOrder;
//    }

//    private Collection<DTOStore> createDTOStoresTheProductBelongsTo() {
//        Collection<DTOStore> dtoStores = new LinkedList<>();
//        for(Store store : storesFromWhomTheOrderWasMade.values()){
//            dtoStores.add(store.createDTOStore());
//        }
//
//        return dtoStores;
//    }

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

    public int getAmountOfProducts() {
        return amountOfProducts;
    }

    public int getAmountOfProductsKinds() {
        return amountOfProductsKinds;
    }

    @Override
    public Integer getSerialNumber() {
        return orderSerialNumber;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public void addProductToOrder(ProductInStore product, float amount){
        Pair<Float,ProductInStore> newProduct = new Pair<>(amount,product);
        productsInOrder.add(newProduct);
        productsCost += product.getPrice() * amount;
        amountOfProductsKinds++;
        if(product.getWayOfBuying() == WayOfBuying.BY_QUANTITY){
            amountOfProducts += amount;
        }
        else{
            amountOfProducts++;
        }
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

    public Collection<Pair<Float,ProductInStore>> getProductsInOrder() {
        return productsInOrder;
    }

    public Collection<Pair<Float, DTOProductInStore>> getDTOProductsInOrder() {
        Collection<Pair<Float, DTOProductInStore>> dtoProductsInOrder = new LinkedList<>();
        for(Pair<Float,ProductInStore> productsInOrder : productsInOrder){
            dtoProductsInOrder.add(new Pair<>(productsInOrder.getKey(),productsInOrder.getValue().createDTOProductInStore()));
        }

        return dtoProductsInOrder;
    }
}
