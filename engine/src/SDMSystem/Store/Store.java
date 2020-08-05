package SDMSystem.Store;

import SDMSystem.Feedback;
import SDMSystem.Location.Locationable;
import SDMSystem.Product.Product;
import SDMSystem.Product.ProductInStore;
import SDMSystem.Exceptions.*;
import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Store implements Locationable {

    private static int generatedSerialNumber = 1000;
    private Map<Integer,ProductInStore> productsInStore;
    private Point storeLocation;
    private float ppk;
    private final int storeSerialNumber;
    private String storeName;
    private Collection<Order> ordersFromStore;
    private Collection<Feedback> storeFeedbacks;

    public Store(Point storeLocation, float ppk, String storeName) { //ctor
        this.productsInStore = new HashMap<>();
        this.storeLocation = storeLocation;
        this.ppk = ppk;
        this.storeSerialNumber = generatedSerialNumber++;
        this.storeName = storeName;
        this.ordersFromStore = null;
        this.storeFeedbacks = null;
    }

    public void addNewProductToStore(Product newProduct, float price){
//        if(productsInStore.containsKey(newProduct.getProductSerialNumber())){
//            throw new ProductAlreadyExistsException(newProduct.getProductSerialNumber());
//        }
        //if already in store throws exception
        if(productsInStore.putIfAbsent(newProduct.getProductSerialNumber(),
                                        new ProductInStore(newProduct,price))
                                        != null){
            throw new ProductAlreadyExistsException(newProduct.getProductSerialNumber());
        }
    }

    public Point getStoreLocation() {
        return storeLocation;
    }

    public float getPpk() {
        return ppk;
    }

    public int getStoreSerialNumber() {
        return storeSerialNumber;
    }

    public String getStoreName() {
        return storeName;
    }

    @Override
    public Point getLocation() {
        return storeLocation;
    }

    @Override
    public double getDistanceFrom(Point target) {
        int a = Math.abs(target.x - this.storeLocation.x);
        int b = Math.abs(target.y - this.storeLocation.y);
        double aPower2 = Math.pow(a, 2);
        double bPower2 = Math.pow(b, 2);
        return Math.sqrt(aPower2 + bPower2);
    }
}
