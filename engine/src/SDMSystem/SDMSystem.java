package SDMSystem;

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
    private static final int MAX_COORDINATE = 50;
    private static final int MIN_COORDINATE = 1;
    private Store[][] storesInSystem;
    private Map<Integer,Product> productsInSystem;

    public SDMSystem() {
        storesInSystem = new Store[MAX_COORDINATE][MAX_COORDINATE];
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
        if (storesInSystem[newStoreLocation.x-1][newStoreLocation.y-1] == null) {
            storesInSystem[newStoreLocation.x-1][newStoreLocation.y-1] = newStore;
        } else {
            throw new ExistenceException(true,newStore.getSerialNumber(), "Store", "System");
        }
    }


    public void loadSystem(SuperDuperMarketDescriptor superDuperMarketDescriptor) {
        loadProducts(superDuperMarketDescriptor.getSDMItems());
        loadStores(superDuperMarketDescriptor.getSDMStores());
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
                res = Product.WayOfBuying.BYQUANTITY;
                break;
            case ("weight")     :
                res = Product.WayOfBuying.BYWEIGHT;
                break;
            default:
                throw new EnumConstantNotPresentException(Product.WayOfBuying.class,purchaseCategory);
        }
        
        return res;
    }
}
