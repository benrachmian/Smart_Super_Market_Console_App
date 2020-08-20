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
            else if(sdmSell.getPrice() <=0){
                throw new RuntimeException(String.format("The price of products %d must be a positive number!",sdmSell.getItemId()));
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
        Collection<Pair<Float,ProductInStore>> productsInOrder = createProductsInOrderCollectionFromDTO(dtoProductsInOrder);
        Order newOrder = makeOrderAndAddToStore(chosenStore.getStoreSerialNumber(), orderDate, deliveryCost, productsInOrder);
        updateAmountsSoldOfProduct(productsInOrder);
        ordersInSystem.put(newOrder.getOrderSerialNumber(),newOrder);
    }

    private Order makeOrderAndAddToStore(int storeSerialNumber, Date orderDate, float deliveryCost, Collection<Pair<Float, ProductInStore>> productsInOrder) {
        Store storeWithNewOrder = storesInSystem.getStoreInSystem(storeSerialNumber);
        Order newOrder = createdNewOrderObject(orderDate, deliveryCost,productsInOrder);
        storeWithNewOrder.addOrder(newOrder, deliveryCost);
        return newOrder;
    }

    public void makeNewDynamicOrder(Date orderDate,
                                    Point userLocation,
                                    Map<Integer, Collection<Pair<Float, DTOProductInStore>>> cheapestBasketDTO) {
        Collection<Order> subOrders = new LinkedList<>();
        float totalDeliveryCost;
        //int[0] = amount of products
        //int[1] = amount of products kinds
        int[] amountOfProductsAndKinds = new int[2];
        makeSubOrderToEachStore(orderDate, userLocation, cheapestBasketDTO, subOrders);
        totalDeliveryCost = calcTotalDeliveryCostInDynamicOrder(subOrders);
        Collection<Pair<Float,ProductInStore>> allProductsInOrder = getAllProductsFromSubOrdersAndAmountOfProductsAndKinds(subOrders,amountOfProductsAndKinds);
        Map<Integer,Store> storesSellingTheProducts = getStoresSellingTheProductsFromBasket(cheapestBasketDTO);
        Order dynamicOrder = new Order(orderDate,
                allProductsInOrder,
                calcProductsCost(allProductsInOrder),
                totalDeliveryCost,
                storesSellingTheProducts,
                amountOfProductsAndKinds[0],
                amountOfProductsAndKinds[1],
                subOrders);
        updateAmountsSoldOfProduct(allProductsInOrder);
        ordersInSystem.put(dynamicOrder.getOrderSerialNumber(),dynamicOrder);
    }

    private Map<Integer, Store> getStoresSellingTheProductsFromBasket(Map<Integer, Collection<Pair<Float, DTOProductInStore>>> cheapestBasketDTO) {
        Map<Integer,Store> storesSellingTheProducts = new HashMap<>();
        for(Integer storeSerialNumber : cheapestBasketDTO.keySet()){
            storesSellingTheProducts.put(storeSerialNumber,storesInSystem.getStoreInSystem(storeSerialNumber));
        }

        return storesSellingTheProducts;
    }

    private Collection<Pair<Float,ProductInStore>> getAllProductsFromSubOrdersAndAmountOfProductsAndKinds(Collection<Order> subOrders, int[] amountOfProductsAndKinds) {
        Collection<Pair<Float,ProductInStore>> allProductsInOrder = new LinkedList<>();
        amountOfProductsAndKinds[0] = amountOfProductsAndKinds[1] = 0;
        for(Order order : subOrders) {
            for(Pair<Float,ProductInStore> productInOrder : order.getProductsInOrder()){
                //update amount of products
                if(productInOrder.getValue().getWayOfBuying() == WayOfBuying.BY_QUANTITY){
                    amountOfProductsAndKinds[0] += productInOrder.getKey();
                }
                else{
                    amountOfProductsAndKinds[0]++;
                }
                amountOfProductsAndKinds[1]++; //update amount of products kinds
                allProductsInOrder.add(productInOrder);
            }
        }

        return allProductsInOrder;
    }

    private float calcTotalDeliveryCostInDynamicOrder(Collection<Order> subOrders) {
        float totalDeliveryCost = 0;
        for(Order order : subOrders) {
            totalDeliveryCost += order.getDeliveryCost();
        }

        return totalDeliveryCost;
    }

    private void makeSubOrderToEachStore(Date orderDate,
                                         Point userLocation,
                                         Map<Integer, Collection<Pair<Float, DTOProductInStore>>> cheapestBasketDTO,
                                         Collection<Order> subOrders) {
        Order subOrder;
        float deliveryCost;
        for(Integer storeSerialNumber : cheapestBasketDTO.keySet()){
            //Store storeSellingTheProducts = storesInSystem.getStoreInSystem(storeSerialNumber);
            deliveryCost = getDeliveryCost(storeSerialNumber, userLocation);
            subOrder = makeOrderAndAddToStore(storeSerialNumber,
                    orderDate,
                    deliveryCost,
                    createProductsInOrderCollectionFromDTO(cheapestBasketDTO.get(storeSerialNumber)));
            subOrders.add(subOrder);
        }
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

    public float getDeliveryCost(int storeSerialNumber, Point locationFromTheUser) {
        Store store = storesInSystem.getStoreInSystem(storeSerialNumber);
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

    public DTOProduct getProductFromSystem(int chosenProductSerialNumber) {
        DTOProduct dtoProduct = null;
        Product chosenProduct = productsInSystem.get(chosenProductSerialNumber);
        if(chosenProduct != null){
            dtoProduct = chosenProduct.createDTOProduct();
        }
        return dtoProduct;
    }

    public boolean checkIfLocationIsUnique(Point userLocation) {
        Collection<Point> storesLocation = createStoresLocationCollection();
        return LocationValidation.checkIfUniqueLocation(userLocation,storesLocation);
    }

    private Collection<Point> createStoresLocationCollection() {
        Collection<Point> storesLocation = new LinkedList<>();
        storesLocation.addAll(storesInSystem.getStoresInSystemByLocation().keySet());

        return storesLocation;
    }

    public Map<Integer, Collection<Pair<Float,DTOProductInStore>>> getCheapestBasket(Collection<Pair<Float, DTOProduct>> productsInOrder) {
        Map<Integer, Collection<Pair<Float,DTOProductInStore>>> cheapestBasket = new HashMap<>();
        Collection <Pair<Float,DTOProductInStore>> productsFromSameStore;
        int storeWithCheapestProductSerialNumber;
        for(Pair<Float, DTOProduct> dtoProductInOrder : productsInOrder){
            ProductInStore cheapestProduct = findCheapestProduct(dtoProductInOrder.getValue().getProductSerialNumber());
            DTOProductInStore cheapestProductAsDTO = cheapestProduct.createDTOProductInStore();
            storeWithCheapestProductSerialNumber = cheapestProduct.getStoreTheProductBelongs().getSerialNumber();
            //If there is already a product from this store in the basket
            if(cheapestBasket.containsKey(storeWithCheapestProductSerialNumber)){
                productsFromSameStore = cheapestBasket.get(storeWithCheapestProductSerialNumber);
                productsFromSameStore.add(new Pair(dtoProductInOrder.getKey(),cheapestProductAsDTO));
            }
            else{
                productsFromSameStore = new LinkedList<>();
                productsFromSameStore.add(new Pair(dtoProductInOrder.getKey(),cheapestProductAsDTO));
                cheapestBasket.put(storeWithCheapestProductSerialNumber,productsFromSameStore);
            }
        }

        return cheapestBasket;
    }

    private ProductInStore findCheapestProduct(int productSerialNumber) {
        ProductInStore currCheapestProduct = null;
        for(Store store : storesInSystem.getStoresInSystemBySerialNumber().values()){
            if(store.isAvailableInStore(productSerialNumber)) {
                ProductInStore productInStore = store.getProductInStore(productSerialNumber);
                if(currCheapestProduct == null){
                    currCheapestProduct = productInStore;
                }
                else{
                    if(productInStore.getPrice() < currCheapestProduct.getPrice()){
                        currCheapestProduct = productInStore;
                    }
                }
            }
        }

        return currCheapestProduct;
    }


}
