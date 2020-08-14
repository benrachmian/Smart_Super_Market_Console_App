package SDMSystem.system;

import SDMSystem.product.Product;
import SDMSystem.store.Store;
import SDMSystem.exceptions.*;
import SDMSystem.validation.*;
import xml.generated.*;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SDMSystem {
    public static final int MAX_COORDINATE = 50;
    public static final int MIN_COORDINATE = 1;
    //private Map<Integer,Store> storesInSystem;
    //In order to create two different way to find a store - by serial number and by location
    private StoresInSystem storesInSystem;
    private Map<Integer,Product> productsInSystem;

    public SDMSystem() {
        storesInSystem = new StoresInSystem();
        productsInSystem = new HashMap<>();
    }


    public void addProductToSystem(Product newProduct){
        //if already in system throws exception
        if(productsInSystem.putIfAbsent(newProduct.getSerialNumber(),newProduct) != null) {
            throw new ExistenceException(true,newProduct.getSerialNumber(),"Product","System");
        }
    }

    public void addStoreToSystem(Store newStore) {
        Point newStoreLocation = newStore.getStoreLocation();
        //if the store doesn't exist
        storesInSystem.addStoreToSystem(newStore,newStoreLocation);

    }


    public void loadSystem(SuperDuperMarketDescriptor superDuperMarketDescriptor) {
        StoresInSystem oldStoresInSystem = this.storesInSystem;
        Map<Integer, Product> oldProductsInSystem = this.productsInSystem;
        storesInSystem = new StoresInSystem();
        productsInSystem = new HashMap<>();
        try {
            loadProducts(superDuperMarketDescriptor.getSDMItems());
            loadStores(superDuperMarketDescriptor.getSDMStores());
            scanForProductsWithoutStore();
        }
        catch (Exception e){
            storesInSystem = oldStoresInSystem;
            productsInSystem = oldProductsInSystem;
            throw e;
        }
    }

    private void scanForProductsWithoutStore() {
        for(Product product : productsInSystem.values()){
            if(product.numberOfStoresSellingTheProduct() == 0){
                throw new RuntimeException("There are no stores selling product " + product.getSerialNumber());
            }
        }
    }

    private void loadStores(SDMStores sdmStores) {
        Store loadedStore;
        List<SDMStore> sdmStoreList = sdmStores.getSDMStore();
        for (SDMStore sdmStore : sdmStoreList) {
            Point loadedStoreLocation = getLoadedStoreLocation(sdmStore.getLocation());
            loadedStore = new Store(sdmStore.getId(),loadedStoreLocation,sdmStore.getDeliveryPpk(),sdmStore.getName());
            addStoreToSystem(loadedStore);
            List<SDMSell> sdmSellList = sdmStore.getSDMPrices().getSDMSell();
            loadProductsToStore(sdmSellList,loadedStore);
        }
    }

    private void loadProductsToStore(List<SDMSell> sdmSellList, Store loadedStore) {
        for(SDMSell sdmSell : sdmSellList){
            Product productToLoad = productsInSystem.get(sdmSell.getItemId());
            if(productToLoad == null){
                throw new ExistenceException(false,sdmSell.getItemId(),"Product", "System");
            }
            loadedStore.addNewProductToStore(productToLoad,sdmSell.getPrice());
        }
    }

    private Point getLoadedStoreLocation(Location location) {
        Point loadedStoreLocation = new Point(location.getX(),location.getY());
        LocationValidation.checkLocationValidation2D(
                loadedStoreLocation,
                MIN_COORDINATE,
                MAX_COORDINATE,
                MIN_COORDINATE,
                MAX_COORDINATE);
        return loadedStoreLocation;
    }

    private void loadProducts(SDMItems sdmItems) {
        Product loadedProduct;
        List<SDMItem> sdmItemsList = sdmItems.getSDMItem();
        for(SDMItem sdmItem : sdmItemsList){
            Product.WayOfBuying loadedProductWayOfBuying = getLoadedProductWayOfBuying(sdmItem.getPurchaseCategory());
            loadedProduct = new Product(sdmItem.getId(),sdmItem.getName(), loadedProductWayOfBuying);
            addProductToSystem(loadedProduct);
        }
    }

    private Product.WayOfBuying getLoadedProductWayOfBuying(String purchaseCategory) {
        Product.WayOfBuying res;
        switch (purchaseCategory.toLowerCase()){
            case ("quantity")   :
                res = Product.WayOfBuying.BY_QUANTITY;
                break;
            case ("weight")     :
                res = Product.WayOfBuying.BY_WEIGHT;
                break;
            default:
                throw new EnumConstantNotPresentException(Product.WayOfBuying.class,purchaseCategory);
        }
        
        return res;
    }

    public Map<Integer, Store> getStoresInSystemBySerialNumber() {
        return storesInSystem.getStoresInSystemBySerialNumber();
    }

    public Store getStoreFromStores(int storeSerialNumber){
        return storesInSystem.getStoresInSystemBySerialNumber().get(storeSerialNumber);
    }

    public Map<Point, Store> getStoresInSystemByLocation(){
        return storesInSystem.getStoresInSystemByLocation();
    }

    public Map<Integer, Product> getProductsInSystem() {
        return productsInSystem;
    }
}
