package SDMSystem.system;

import SDMSystem.order.DynamicOrder;
import SDMSystem.order.StaticOrder;
import SDMSystem.product.Product;
import SDMSystem.product.ProductInStore;
import SDMSystem.order.Order;
import SDMSystem.store.Store;
import SDMSystem.exceptions.*;
import SDMSystem.validation.*;
import SDMSystemDTO.product.DTOProduct;
import SDMSystemDTO.product.DTOProductInStore;
import SDMSystemDTO.order.DTOOrder;
import SDMSystemDTO.store.DTOStore;
import SDMSystemDTO.product.WayOfBuying;
import javafx.util.Pair;
import xml.generated.*;

import java.awt.*;
import java.io.*;
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


    private void addProductToSystem(Product newProduct){
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
        Map<Integer, Order> oldOrdersInSystem = this.ordersInSystem;
        storesInSystem = new StoresInSystem();
        productsInSystem = new HashMap<>();
        ordersInSystem = new HashMap<>();
        try {
            loadProducts(superDuperMarketDescriptor.getSDMItems());
            loadStores(superDuperMarketDescriptor.getSDMStores());
            scanForProductsWithoutStore();
        }
        catch (Exception e){
            storesInSystem = oldStoresInSystem;
            productsInSystem = oldProductsInSystem;
            ordersInSystem = oldOrdersInSystem;
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
        //Order newOrder = makeOrderAndAddToStore(chosenStore.getStoreSerialNumber(), orderDate, deliveryCost, productsInOrder);
        Store storeTheProductBelong = storesInSystem.getStoreInSystem(chosenStore.getStoreSerialNumber());
        Order newOrder = createdNewStaticOrderObjectAndUpdateAmountSoldInStore(orderDate,deliveryCost,productsInOrder,storeTheProductBelong);
        updateAmountSoldInSystemForEveryProductInOrder(productsInOrder);
        storeTheProductBelong.addOrder(newOrder,deliveryCost);
        ordersInSystem.put(newOrder.getSerialNumber(),newOrder);
        //addOrderWithoutSubOrdersToSystem(newOrder,chosenStore.getStoreSerialNumber());
    }

//    private void addOrderWithoutSubOrdersToSystem(Order newOrder, int storeSerialNumber) {
//        addOrderToStore(storeSerialNumber,newOrder.getDeliveryCost(),newOrder);
//        //updateAmountsSoldOfProduct(newOrder.getProductsInOrder());
//        ordersInSystem.put(newOrder.getOrderSerialNumber(), newOrder);
//    }

//    private Order makeOrderAndAddToStore(int storeSerialNumber, Date orderDate, float deliveryCost, Collection<Pair<Float, ProductInStore>> productsInOrder) {
//        Order newOrder = createdNewOrderObject(orderDate, deliveryCost,productsInOrder);
//        addOrderToStore(storeSerialNumber, deliveryCost, newOrder);
//        return newOrder;
//    }

//    private void addOrderToStore(int storeSerialNumber, float deliveryCost, Order newOrder) {
//        Store storeWithNewOrder = storesInSystem.getStoreInSystem(storeSerialNumber);
//        storeWithNewOrder.addOrder(newOrder, deliveryCost);
//    }

    public void makeNewDynamicOrder(Date orderDate,
                                    Point userLocation,
                                    Map<Integer, Collection<Pair<Float, DTOProductInStore>>> cheapestBasketDTO) {
        Collection<StaticOrder> subOrders = new LinkedList<>();
        float totalDeliveryCost;
        //int[0] = amount of products
        //int[1] = amount of products kinds
        int[] amountOfProductsAndKinds = new int[2];
        makeSubOrderFromEachStore(orderDate, userLocation, cheapestBasketDTO, subOrders);
        totalDeliveryCost = calcTotalDeliveryCostInDynamicOrder(subOrders);
        Collection<Pair<Float,ProductInStore>> allProductsInOrder = getAllProductsFromSubOrdersAndAmountOfProductsAndKinds(subOrders,amountOfProductsAndKinds);
        //Map<Integer,Store> storesSellingTheProducts = getStoresSellingTheProductsFromBasket(cheapestBasketDTO);
        Order dynamicOrder = new DynamicOrder(orderDate,
                allProductsInOrder,
                calcProductsCost(allProductsInOrder),
                totalDeliveryCost,
                amountOfProductsAndKinds[0],
                amountOfProductsAndKinds[1],
                subOrders);
        //updateAmountsSoldOfProduct(allProductsInOrder);
        updateAmountSoldInSystemForEveryProductInOrder(allProductsInOrder);
        ordersInSystem.put(dynamicOrder.getSerialNumber(),dynamicOrder);
    }

//    private Map<Integer, Store> getStoresSellingTheProductsFromBasket(Map<Integer, Collection<Pair<Float, DTOProductInStore>>> cheapestBasketDTO) {
//        Map<Integer,Store> storesSellingTheProducts = new HashMap<>();
//        for(Integer storeSerialNumber : cheapestBasketDTO.keySet()){
//            storesSellingTheProducts.put(storeSerialNumber,storesInSystem.getStoreInSystem(storeSerialNumber));
//        }
//
//        return storesSellingTheProducts;
//    }

    private Collection<Pair<Float,ProductInStore>> getAllProductsFromSubOrdersAndAmountOfProductsAndKinds(Collection<StaticOrder> subOrders, int[] amountOfProductsAndKinds) {
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

    public float calcProductsInOrderCost(Collection<Pair<Float, DTOProductInStore>> productsInOrder) {
        float res = 0;
        for(Pair<Float, DTOProductInStore> dtoProductInorder : productsInOrder){
            res += (dtoProductInorder.getValue().getPrice() * dtoProductInorder.getKey());
        }

        return res;
    }

    private float calcTotalDeliveryCostInDynamicOrder(Collection<StaticOrder> subOrders) {
        float totalDeliveryCost = 0;
        for(Order order : subOrders) {
            totalDeliveryCost += order.getDeliveryCost();
        }

        return totalDeliveryCost;
    }

    private void makeSubOrderFromEachStore(Date orderDate,
                                           Point userLocation,
                                           Map<Integer, Collection<Pair<Float, DTOProductInStore>>> cheapestBasketDTO,
                                           Collection<StaticOrder> subOrders) {
        StaticOrder subOrder;
        float deliveryCost;
        for(Integer storeSerialNumber : cheapestBasketDTO.keySet()){
            Store storeSellingTheProducts = storesInSystem.getStoreInSystem(storeSerialNumber);
            deliveryCost = getDeliveryCost(storeSerialNumber, userLocation);
//            subOrder = makeOrderAndAddToStore(storeSerialNumber,
//                    orderDate,
//                    deliveryCost,
//                    createProductsInOrderCollectionFromDTO(cheapestBasketDTO.get(storeSerialNumber)));
            subOrder = createdNewStaticOrderObjectAndUpdateAmountSoldInStore(orderDate,deliveryCost,createProductsInOrderCollectionFromDTO(cheapestBasketDTO.get(storeSerialNumber)), storeSellingTheProducts);
            storeSellingTheProducts.addOrder(subOrder,deliveryCost);
            subOrders.add(subOrder);
        }
    }

    private StaticOrder createdNewStaticOrderObjectAndUpdateAmountSoldInStore(Date orderDate,
                                                                              float deliveryCost,
                                                                              Collection<Pair<Float, ProductInStore>> productsInOrder,
                                                                              Store storeTheProductBelong) {
        int amountOfProducts = 0, amountKindsOfProducts = 0;
        for(Pair<Float, ProductInStore> productInOrderAndAmount : productsInOrder){
            ProductInStore currProduct = productInOrderAndAmount.getValue();
            currProduct.increaseAmountSoldInStore(productInOrderAndAmount.getKey());
            //currProduct.increaseAmountSoldInAllStores(productInOrderAndAmount.getKey());
            amountKindsOfProducts++;
            if(productInOrderAndAmount.getValue().getWayOfBuying() == WayOfBuying.BY_QUANTITY){
                amountOfProducts += productInOrderAndAmount.getKey();
            }
            else{
                amountOfProducts++;
            }
        }
        float productsCost = calcProductsCost(productsInOrder);

        return new StaticOrder(orderDate,
                productsInOrder,
                productsCost,
                deliveryCost,
                amountOfProducts,
                amountKindsOfProducts,
                storeTheProductBelong);
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


    private void updateAmountSoldInSystemForEveryProductInOrder(Collection<Pair<Float, ProductInStore>> productsInOrder) {
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
//        Collection<Point> storesLocation = createStoresLocationCollection();
//        return LocationValidation.checkIfUniqueLocation(userLocation,storesLocation);
        return !storesInSystem.getStoresInSystemByLocation().containsKey(userLocation);
    }

    private Collection<Point> createStoresLocationCollection() {

        return new LinkedList<>(storesInSystem.getStoresInSystemByLocation().keySet());
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


    public boolean deleteProductFromStore(DTOProductInStore chosenProductToDeleteDTO) {
        boolean deletedSuccessfully = false;
        Store storeSellingTheProduct = storesInSystem.getStoreInSystem(chosenProductToDeleteDTO.getStoreTheProductBelongsID());
        ProductInStore productToDelete = storeSellingTheProduct.getProductInStore(chosenProductToDeleteDTO.getProductSerialNumber());
        //if more then one store selling the product
        if(productToDelete.getStoresSellingTheProduct().size() > 1){
            storeSellingTheProduct.deleteProduct(productToDelete);
            deletedSuccessfully = true;
        }

        return deletedSuccessfully;
    }

    public Map<Integer, DTOProduct> getProductsTheStoreDoesntSell(DTOStore storeToUpdate) {
        Store targetStore = storesInSystem.getStoreInSystem(storeToUpdate.getStoreSerialNumber());
        Map<Integer, DTOProduct> productsTheStoreDoesntSell = new HashMap<>();
        for(Product product : productsInSystem.values()){
            if(!targetStore.isAvailableInStore(product.getSerialNumber())){
                productsTheStoreDoesntSell.put(product.getSerialNumber(),product.createDTOProduct());
            }
        }
        if(productsTheStoreDoesntSell.isEmpty()){
            throw new RuntimeException("The store already selling every product possible!");
        }

        return productsTheStoreDoesntSell;
    }

    public void addProductToStore(DTOStore storeToUpdateDTO, DTOProduct productToAddDTO, float productPrice) {
        Store storeToUpdate = storesInSystem.getStoreInSystem(storeToUpdateDTO.getStoreSerialNumber());
        Product productToAdd = productsInSystem.get(productToAddDTO.getProductSerialNumber());
        storeToUpdate.addNewProductToStore(productToAdd,productPrice);
    }

    public void updateProductPrice(DTOStore storeToUpdateDTO, DTOProductInStore chosenProductToUpdateDTO, float newPrice) {
        Store storeToUpdate = storesInSystem.getStoreInSystem(storeToUpdateDTO.getStoreSerialNumber());
        storeToUpdate.updateProductPrice(chosenProductToUpdateDTO.getProductSerialNumber(),newPrice);
    }

    public void saveOrdersToFile(String filePath) throws IOException {
        try (ObjectOutputStream out =
                     new ObjectOutputStream(
                             new FileOutputStream(filePath))) {
            out.writeObject(ordersInSystem);
            out.flush();
        }
    }

    public void loadOrdersFromFile(String filePath) throws IOException, ClassNotFoundException {
        Map<Integer, Order> orders = getOrdersFromFile(filePath);
        addLoadedOrdersToSystem(orders);
    }

    private void addLoadedOrdersToSystem(Map<Integer, Order> orders) {
        for(Order order : orders.values()){
            order.generateNewSerialNumber();
            if(order instanceof StaticOrder){
                addLoadedStaticOrderToSystem((StaticOrder)order);
            }
            //else = instanceof Dynamic order. need to add every sub order to its store
            else{
                addLoadedDynamicOrderToSystem((DynamicOrder)order);
            }
            //ordersInSystem.put(order.getOrderSerialNumber(),order);
            //addOrderToSystem
            //makeNewStaticOrder();
//            addOrderWithoutSubOrdersToSystem(order,
//                    order.getStoresFromWhomTheOrderWasMade().g);
        }
    }

    private void addLoadedDynamicOrderToSystem(DynamicOrder order) {
        for(StaticOrder staticOrder : order.getSubOrders()){
            staticOrder.generateNewSerialNumber();
            addLoadedStaticOrderToItsStoreAndUpdateAmounts(staticOrder);
        }
        ordersInSystem.put(order.getSerialNumber(),order);
    }

    private void addLoadedStaticOrderToSystem(StaticOrder order) {
        addLoadedStaticOrderToItsStoreAndUpdateAmounts(order);
        ordersInSystem.put(order.getSerialNumber(),order);
    }

    private void addLoadedStaticOrderToItsStoreAndUpdateAmounts(StaticOrder order) {
        increaseProductsInLoadedOrderAmountSoldInStore(order.getStoreFromWhomTheOrderWasMade().getSerialNumber(),
                order.getProductsInOrder());
        updateAmountSoldInSystemForEveryProductInOrder(order.getProductsInOrder());
        storesInSystem.getStoreInSystem(order.getStoreFromWhomTheOrderWasMade().getSerialNumber()).addOrder(order, order.getDeliveryCost());
    }

    private void increaseProductsInLoadedOrderAmountSoldInStore(Integer storeSerialNumber, Collection<Pair<Float, ProductInStore>> productsInOrder) {
        Store storeSellingTheProduct = storesInSystem.getStoreInSystem(storeSerialNumber);
        for (Pair<Float, ProductInStore> loadedProductAndAmount : productsInOrder) {
            ProductInStore currProduct = storeSellingTheProduct.getProductInStore(loadedProductAndAmount.getValue().getSerialNumber());
            currProduct.increaseAmountSoldInStore(loadedProductAndAmount.getKey());
        }
    }

    private  Map<Integer,Order> getOrdersFromFile(String filePath) throws IOException, ClassNotFoundException {
        Map<Integer, Order> orders;
        try (ObjectInputStream in =
                     new ObjectInputStream(
                             new FileInputStream(filePath))) {
            orders = (Map<Integer, Order>) in.readObject();
        }

        return orders;
    }

    public int getNumOfOrders() {
        return ordersInSystem.size();
    }
}
