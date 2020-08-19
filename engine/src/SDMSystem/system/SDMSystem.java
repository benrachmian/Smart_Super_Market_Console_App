package SDMSystem.system;

import SDMSystem.product.Product;
import SDMSystem.product.ProductInStore;
import SDMSystem.store.Order;
import SDMSystem.store.Store;
import SDMSystem.exceptions.*;
import SDMSystem.validation.*;
import SDMSystemDTO.product.DTOProduct;
import SDMSystemDTO.product.DTOProductInStore;
import SDMSystemDTO.store.DTOOrder;
import SDMSystemDTO.store.DTOStore;
import SDMSystemDTO.product.WayOfBuying;
import javafx.util.Pair;
import xml.generated.*;

import java.awt.*;
import java.util.*;
import java.util.List;

public class SDMSystem {
    public static final int MAX_COORDINATE = 50;
    public static final int MIN_COORDINATE = 1;
    //In order to create two different way to find a store - by serial number and by location
    private StoresInSystem storesInSystem;
    private Map<Integer,Product> productsInSystem;
    private Map<Integer, Order> ordersInSystem;

    public SDMSystem() {
        storesInSystem = new StoresInSystem();
        productsInSystem = new HashMap<>();
        ordersInSystem = new HashMap<>();
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
            WayOfBuying loadedProductWayOfBuying = getLoadedProductWayOfBuying(sdmItem.getPurchaseCategory());
            loadedProduct = new Product(sdmItem.getId(),sdmItem.getName(), loadedProductWayOfBuying);
            addProductToSystem(loadedProduct);
        }
    }

    private WayOfBuying getLoadedProductWayOfBuying(String purchaseCategory) {
        WayOfBuying res;
        switch (purchaseCategory.toLowerCase()){
            case ("quantity")   :
                res = WayOfBuying.BY_QUANTITY;
                break;
            case ("weight")     :
                res = WayOfBuying.BY_WEIGHT;
                break;
            default:
                throw new EnumConstantNotPresentException(WayOfBuying.class,purchaseCategory);
        }
        
        return res;
    }

    public Map<Integer, DTOStore> getStoresInSystemBySerialNumber() {
        return storesInSystem.getDTOStoresInSystemBySerialNumber();
    }

    public DTOStore getStoreFromStores(int storeSerialNumber){
        DTOStore res = null;
        Store store = storesInSystem.getStoresInSystemBySerialNumber().get(storeSerialNumber);
        if(store != null)
        {
            res = store.createDTOStore();
        }
        return res;
    }

    public Map<Point, DTOStore> getStoresInSystemByLocation(){
        return storesInSystem.getDTOStoresInSystemByLocation();
    }

    public Map<Integer, DTOProduct> getProductsInSystem() {
        Map<Integer, DTOProduct> dtoProductsInSystem = new HashMap<>();
        for(Product product : this.productsInSystem.values()){
            DTOProduct newDTOProduct = product.createDTOProduct();
            dtoProductsInSystem.put(newDTOProduct.getProductSerialNumber(),newDTOProduct);
        }
        return dtoProductsInSystem;
    }


    public DTOProductInStore getProductFromStore(int chosenProductSerialNumber, int chosenStoreSerialNumber) {
        DTOProductInStore dtoProductInStore = null;
        Store chosenStore = storesInSystem.getStoresInSystemBySerialNumber().get(chosenStoreSerialNumber);
        if(chosenStore!= null){
            ProductInStore chosenProductInStore = chosenStore.getProductInStore(chosenProductSerialNumber);
            dtoProductInStore = chosenProductInStore.createDTOProductInStore();
        }
        return dtoProductInStore;
    }

    public void makeNewStaticOrder(DTOStore chosenStore,
                                   Date orderDate,
                                   float deliveryCost,
                                   Collection<Pair<Float,DTOProductInStore>> dtoProductsInOrder) {
        Store storeWithNewOrder = storesInSystem.getStoreInSystem(chosenStore.getStoreSerialNumber());
        Collection<Pair<Float,ProductInStore>> productsInOrder = createProductsInOrderCollectionFromDTO(dtoProductsInOrder);
        //storeWithNewOrder.makeNewOrderAndAddToOrdersIsSystem(orderDate,deliveryCost,productsInOrder,ordersInSystem);
        Order newOrder = createdNewOrderObject(orderDate,deliveryCost,productsInOrder);
        storeWithNewOrder.addOrder(newOrder,deliveryCost);
        updateAmountsSoldOfProduct(productsInOrder);
        ordersInSystem.put(newOrder.getOrderSerialNumber(),newOrder);
        //storeWithNewOrder.increaseTotalProfitFromDelivery(deliveryCost);
    }

    private Order createdNewOrderObject(Date orderDate,
                                        float deliveryCost,
                                        Collection<Pair<Float, ProductInStore>> productsInOrder) {

        Map<Integer,Store> storesFromWhomTheOrderWasMade = new HashMap<>();
        int amountOfProducts = 0, amountKindsOfProducts = 0;
        for(Pair<Float, ProductInStore> productInOrderAndAmount : productsInOrder){
            productInOrderAndAmount.getValue().increaseAmountSoldInStore(productInOrderAndAmount.getKey());
            Store storeTheProductBelongs = productInOrderAndAmount.getValue().getStoreTheProductBelongs();
            storesFromWhomTheOrderWasMade.putIfAbsent(storeTheProductBelongs.getSerialNumber(),storeTheProductBelongs);
            amountKindsOfProducts++;
            if(productInOrderAndAmount.getValue().getWayOfBuying() == WayOfBuying.BY_QUANTITY){
                amountOfProducts += productInOrderAndAmount.getKey();
            }
            else{
                amountOfProducts++;
            }
        }
        float productsCost = calcProductsCost(productsInOrder);

        return new Order(orderDate,
                productsInOrder,
                productsCost,
                deliveryCost,
                storesFromWhomTheOrderWasMade,
                amountOfProducts,
                amountKindsOfProducts,
                null);
    }

    private float calcProductsCost(Collection<Pair<Float, ProductInStore>> productsInOrder) {
        float totalCost = 0;
        for(Pair<Float,ProductInStore> productInOrder : productsInOrder){
            totalCost += productInOrder.getKey() * productInOrder.getValue().getPrice();
        }

        return totalCost;
    }

    private Collection<Pair<Float, ProductInStore>> createProductsInOrderCollectionFromDTO(Collection<Pair<Float, DTOProductInStore>> dtoProductsInOrder) {
        Collection<Pair<Float,ProductInStore>> productsInOrder = new LinkedList<>();
        for(Pair<Float, DTOProductInStore> dtoProductInOrder : dtoProductsInOrder){
            Store storeWithTheProduct = storesInSystem.getStoreInSystem(dtoProductInOrder.getValue().getStoreTheProductBelongsID());
            ProductInStore productInStore = storeWithTheProduct.getProductInStore(dtoProductInOrder.getValue().getProductSerialNumber());
            Pair<Float,ProductInStore> newProductInOrder = new Pair<>(dtoProductInOrder.getKey(),productInStore);
            productsInOrder.add(newProductInOrder);
        }

        return productsInOrder;
    }


    private void updateAmountsSoldOfProduct(Collection<Pair<Float, ProductInStore>> productsInOrder) {
        for(Pair<Float, ProductInStore> productInOrder : productsInOrder)
        {
            Product productInSystem = productsInSystem.get(productInOrder.getValue().getSerialNumber());
            productInSystem.increaseAmountSoldInAllStores(productInOrder.getKey());
        }
    }

    public boolean isAvailableInStore(int storeSerialNumber, int productSerialNumber) {
        Store store = storesInSystem.getStoreInSystem(storeSerialNumber);
        return store.isAvailableInStore(productSerialNumber);
    }

    public float getProductPrice(int storeSerialNumber, int productSerialNumber) {
        Store store = storesInSystem.getStoreInSystem(storeSerialNumber);
        return store.getProductInStore(productSerialNumber).getPrice();
    }

    public float getAveragePriceOfProduct(int productSerialNumber) {
        return productsInSystem.get(productSerialNumber).averagePriceOfProduct();
    }

    public int getNumberOfStoresSellingProduct(int productSerialNumber) {
        return productsInSystem.get(productSerialNumber).numberOfStoresSellingTheProduct();
    }

    public float getDeliveryCost(DTOStore chosenStore, Point locationFromTheUser) {
        Store store = storesInSystem.getStoreInSystem(chosenStore.getStoreSerialNumber());
        return store.getDeliveryCost(locationFromTheUser);
    }

    public float getDistanceFromStore(DTOStore chosenStore, Point userLocation) {
        Store store = storesInSystem.getStoreInSystem(chosenStore.getStoreSerialNumber());
        return store.getDistanceFrom(userLocation);
    }

    public Collection<DTOOrder> getAllOrders() {
        Collection<DTOOrder> dtoOrders = new LinkedList<>();
//        for(Store store : storesInSystem.getStoresInSystemBySerialNumber().values()){
//            for(DTOOrder order : store.getDTOOrdersFromStore()){
//                dtoOrders.add(order);
//            }
//        }
        for(Order order : ordersInSystem.values()){
            dtoOrders.add(order.createDTOOrderFromOrder());
        }

        return dtoOrders;
    }

    public Collection<DTOOrder> getOrdersFromStore(int storeSerialNumber) {
        Store store = storesInSystem.getStoreInSystem(storeSerialNumber);
        return store.getDTOOrdersFromStore();
    }
}
