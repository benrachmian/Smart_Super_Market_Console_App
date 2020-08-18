package SDMSystem.store;

import SDMSystem.Feedback;
import SDMSystem.HasSerialNumber;
import SDMSystem.location.Locationable;
import SDMSystem.product.Product;
import SDMSystem.product.ProductInStore;
import SDMSystem.exceptions.*;
import SDMSystemDTO.product.DTOProductInStore;
import SDMSystemDTO.product.WayOfBuying;
import SDMSystemDTO.store.DTOOrder;
import SDMSystemDTO.store.DTOStore;
import javafx.util.Pair;

import java.awt.*;
import java.util.*;

public class Store implements Locationable, HasSerialNumber<Integer> {

    private static int generatedSerialNumber = 1000;
    private Map<Integer,ProductInStore> productsInStore;
    private Point storeLocation;
    private float ppk;
    private final int storeSerialNumber;
    private String storeName;
    private Collection<Order> ordersFromStore;
    private Collection<Feedback> storeFeedbacks;
    private float totalProfitFromDelivery;

    public Store(Point storeLocation, float ppk, String storeName) { //ctor
        this.productsInStore = new HashMap<>();
        this.storeLocation = storeLocation;
        this.ppk = ppk;
        this.storeSerialNumber = generatedSerialNumber++;
        this.storeName = storeName;
        this.ordersFromStore = new HashSet<>();
        this.storeFeedbacks = null;
        this.totalProfitFromDelivery = 0;
    }

    public Store(int storeSerialNumber, Point storeLocation, float ppk, String storeName) { //ctor
        this.productsInStore = new HashMap<>();
        this.storeLocation = storeLocation;
        this.ppk = ppk;
        this.storeSerialNumber = storeSerialNumber;
        this.storeName = storeName;
        this.ordersFromStore = new HashSet<>();
        this.storeFeedbacks = null;
    }

    public void addNewProductToStore(Product newProduct, float price){
        //if already in store throws exception
        if(productsInStore.putIfAbsent(newProduct.getSerialNumber(),
                                        new ProductInStore(newProduct,price))
                                        != null){
            throw new ExistenceException(true,newProduct.getSerialNumber(),"Product", "Store");
        }
        newProduct.addStore(this);
    }

    public Point getStoreLocation() {
        return storeLocation;
    }

    public float getPpk() {
        return ppk;
    }

    public ProductInStore getProductInStore(int productSerialNumber){
        ProductInStore askedProduct = productsInStore.get(productSerialNumber);
        if(askedProduct == null){
            throw new ExistenceException(false,productSerialNumber,"Product","Store");
        }
       return askedProduct;
    }

    public boolean isAvailableInStore(int productSerialNumber){
        boolean isAvailable = false;
        if(productsInStore.get(productSerialNumber) != null){
            isAvailable = true;
        }
        return isAvailable;
    }

    public String getStoreName() {
        return storeName;
    }

    @Override
    public Point getLocation() {
        return storeLocation;
    }

    @Override
    public float getDistanceFrom(Point target) {
        int a = Math.abs(target.x - this.storeLocation.x);
        int b = Math.abs(target.y - this.storeLocation.y);
        double aPower2 = Math.pow(a, 2);
        double bPower2 = Math.pow(b, 2);
        return (float) Math.sqrt(aPower2 + bPower2);
    }

    @Override
    public Integer getSerialNumber() {
        return storeSerialNumber;
    }

//    @Override
//    public String toString() {
//        return "Store ID: " + storeSerialNumber +
//                "\nStore name: " + storeName +
//                "\nProducts in store:\n\n" + productsInStoreToSting() +
//                "\nOrders history: " + ordersToString() +
//                "\nPPK: " + ppk +
//                "\nTotal profit from delivery: " + totalProfitFromDelivery;
//    }

//    private String ordersToString() {
//        String res = "";
//        if (ordersFromStore.size() != 0) {
//            for (Order order : ordersFromStore) {
//                res = res.concat(order.toString());
//            }
//        }
//        else{
//            res = res.concat("There are no any orders yet!");
//        }
//
//        return res;
//    }

//    private String productsInStoreToSting() {
//        String res = "";
//        for(ProductInStore productInStore : productsInStore.values()){
//            res = res.concat(productInStore.toString() + "\n");
//        }
//
//        return res;
//    }

    public Map<Integer, DTOProductInStore> getDTOProductsInStore() {
        Map<Integer, DTOProductInStore> DTOProductsInStore = new HashMap<>();
        for(ProductInStore productInStore : this.productsInStore.values()){
            DTOProductInStore newProduct = productInStore.createDTOProductInStore();
            DTOProductsInStore.put(newProduct.getProductSerialNumber(),newProduct);
        }
        return DTOProductsInStore;
    }

//    private DTOProductInStore createDTOProductInStoreFromProductInStore(ProductInStore productInStore) {
//        DTOProductInStore newDTOProductInStore = new DTOProductInStore(
//                productInStore.getSerialNumber(),
//                productInStore.getProductName(),
//                productInStore.getWayOfBuying(),
//                productInStore.getAmountSoldInAllStores(),
//                productInStore.getPrice(),
//                productInStore.getAmountSoldInStore());
//        return newDTOProductInStore;
//    }

    public DTOStore createDTOStore() {
        DTOStore newDTOStore = new DTOStore(
                getDTOProductsInStore(),
                storeLocation,
                ppk,
                storeSerialNumber,
                storeName,
                //getDTOOrdersFromStore(),
                totalProfitFromDelivery);
        return newDTOStore;
    }



    public float getTotalProfitFromDelivery() {
        return totalProfitFromDelivery;
    }

    public Collection<DTOOrder> getDTOOrdersFromStore() {
        Collection<DTOOrder> dtoOrders = new LinkedList<>();
        for(Order order : ordersFromStore){
            DTOOrder newDTOOrder = new DTOOrder(
                    order.getOrderDate(),
                    order.getDTOProductsInOrder(),
                    order.getProductsCost(),
                    order.getDeliveryCost(),
                    order.getOrderSerialNumber(),
                    this.createDTOStore(),
                    order.getAmountOfProducts(),
                    order.getAmountOfProductsKinds());
            dtoOrders.add(newDTOOrder);
        }

        return dtoOrders;
    }

    private Collection<DTOProductInStore> createDTOProductInStoreCollection(Collection<ProductInStore> productsCollection) {
        Collection<DTOProductInStore> dtoProductInStoreCollection = new LinkedList();
        for (ProductInStore productInStore : productsCollection){
            DTOProductInStore dtoProductInStore = productInStore.createDTOProductInStore();
            dtoProductInStoreCollection.add(dtoProductInStore);
        }

        return dtoProductInStoreCollection;
    }

    public void makeNewOrder(Date orderDate, float  deliveryCost, Collection<Pair<Float,DTOProductInStore>> dtoProductsInOrder) {
        Collection<Pair<Float,ProductInStore>> productsInOrder = new LinkedList<>();
        int amountOfProducts = 0, amountKindsOfProducts = 0;
        for(Pair<Float, DTOProductInStore> dtoProductInOrder : dtoProductsInOrder){
            ProductInStore productInStore = productsInStore.get(dtoProductInOrder.getValue().getProductSerialNumber());
            productInStore.increaseAmountSoldInStore(dtoProductInOrder.getKey());
            Pair<Float,ProductInStore> newProductInOrder = new Pair<>(dtoProductInOrder.getKey(),productInStore);
            productsInOrder.add(newProductInOrder);
            amountKindsOfProducts++;
            if(dtoProductInOrder.getValue().getWayOfBuying() == WayOfBuying.BY_QUANTITY){
                amountOfProducts += dtoProductInOrder.getKey();
            }
            else{
                amountOfProducts++;
            }
        }
        float productsCost = calcProductsCost(productsInOrder);
        //float deliveryCost = getDeliveryCost()
        ordersFromStore.add(new Order(orderDate,
                productsInOrder,
                productsCost,
                deliveryCost,
                this,
                amountOfProducts,
                amountKindsOfProducts));
    }

    private float calcProductsCost(Collection<Pair<Float, ProductInStore>> productsInOrder) {
        float totalCost = 0;
        for(Pair<Float,ProductInStore> productInOrder : productsInOrder){
            totalCost += productInOrder.getKey() * productInOrder.getValue().getPrice();
        }

        return totalCost;
    }

    public float getDeliveryCost(Point locationFromTheUser) {
        return this.getDistanceFrom(locationFromTheUser) * ppk;
    }

    public void increaseTotalProfitFromDelivery(float deliveryCost) {
        totalProfitFromDelivery += deliveryCost;
    }
}
