package SDMSystem;

import SDMSystem.Product.ProductInStore;
import SDMSystem.Store.Store;
import SDMSystem.Exceptions.*;
import com.sun.scenario.effect.impl.sw.java.JSWPhongLighting_SPOTPeer;

import java.awt.*;
import java.util.Collection;
import java.util.Map;

public class SDMSystem {
    private static final int MAX_COORDINATE = 50;
    private static final int MIN_COORDINATE = 1;
    private Store[][] storesInSystem;
    private Map<Integer,ProductInStore> productsInSystem;

    public SDMSystem() {
        storesInSystem = new Store[MAX_COORDINATE][MAX_COORDINATE];
    }


    public void addProductToSystem(ProductInStore newProduct){
        //if already in system throws exception
        if(productsInSystem.putIfAbsent(newProduct.getProductSerialNumber(),newProduct) != null) {
            throw new ProductAlreadyExistsException(newProduct.getProductSerialNumber());
        }
    }

    public void addStoreToSystem(Store newStore){
        Point newStoreLocation = newStore.getStoreLocation();
        if(storesInSystem[newStoreLocation.x][newStoreLocation.y] == null){
           (storesInSystem[newStoreLocation.x][newStoreLocation.y] = newStore;
        }
        else{

        }
    }




}
