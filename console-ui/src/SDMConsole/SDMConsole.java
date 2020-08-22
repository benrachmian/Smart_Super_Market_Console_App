package SDMConsole;

import SDMSystem.exceptions.ExistenceException;
import SDMSystem.system.SDMSystem;
import SDMSystemDTO.product.DTOProduct;
import SDMSystemDTO.product.DTOProductInStore;
import SDMSystemDTO.product.WayOfBuying;
import SDMSystemDTO.store.DTOOrder;
import SDMSystemDTO.store.DTOStore;
import javafx.util.Pair;
import xml.XMLHelper;

import javax.xml.bind.JAXBException;
import java.awt.*;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class SDMConsole {
    private SDMSystem sdmSystem;
    private static final int MIN_CHOOSE = 1;
    private static final int MAX_CHOOSE = 6;
    private static final int EXIT = 6;
    private boolean fileLoaded = false;

    public SDMConsole() {
        sdmSystem = new SDMSystem();
    }

    private void printOpeningMenu() {
        System.out.println(
                "Welcome to Super Duper Market!\n" +
                        "What would you like to do?\n" +
                        "Please insert the option's number.\n" +
                        "1.Read the system details from an XML file\n" +
                        "2.Show stores information\n" +
                        "3.Show all products in the system\n" +
                        "4.Make order\n" +
                        "5.Show order history\n" +
                        "6.Exit");
    }

    public void startApp() {
        printOpeningMenu();
        int choose = Validation.getValidChoice(MIN_CHOOSE, MAX_CHOOSE);

        while (choose != EXIT) {
            if (choose != 1) {
                if (!fileLoaded) {
                    System.out.println("You must load a file first before making that action!");
                } else {
                    switch (choose) {
                        case 2:
                            printAllStoresAndTheirProducts();
                            break;
                        case 3:
                            printAllProducts();
                            break;
                        case 4:
                            makeOrder();
                            break;
                        case 5:
                            showOrdersHistoryInSystem();
                            break;
                    }
                }
            } else {
                if (loadFileToSystem()) { //option 1
                    fileLoaded = true;
                    System.out.println("File loaded successfully!");
                }
            }
            printOpeningMenu();
            choose = Validation.getValidChoice(MIN_CHOOSE, MAX_CHOOSE);
        }
    }

    private void showOrdersHistoryInSystem() {
        Collection<DTOOrder> ordersInSystem = sdmSystem.getAllOrders();
        if(ordersInSystem.size() >0) {
            System.out.println("Orders history in system:");
            for (DTOOrder order : ordersInSystem) {
                printOrder(order);
            }
        }
        else{
            System.out.println("There are no any orders yet!");
        }
    }

    private void printOrder(DTOOrder order) {
        System.out.println("-------------------------------------------------------------------");
        System.out.println("Order serial number: " + order.getOrderSerialNumber());
        System.out.println("Order date: " + order.getOrderDate());
        printStoresFromWhomTheOrderWasMade(order);
        System.out.println("Kinds of products in the order: " + order.getAmountOfProductsKinds());
        System.out.println("Total number of products in order: " + order.getAmountOfProducts());
        System.out.printf("Total cost of all products: %.2f\n",order.getProductsCost());
        System.out.printf("Delivery cost: %.2f\n",order.getDeliveryCost());
        System.out.printf("Total order cost: %.2f\n", order.getOrderCost());
        System.out.println("-------------------------------------------------------------------");
    }

    private void printStoresFromWhomTheOrderWasMade(DTOOrder order) {
        int countStores = 1;
        System.out.println("Stores from whom the order was made: ");
        for(DTOStore store : order.getStoreFromWhomTheOrderWasMade()){
            System.out.printf("%d. Store serial number: %d\n   Store name: %s\n",
                    countStores++,
                    store.getStoreSerialNumber(),
                    store.getStoreName());
        }
    }

    private void makeOrder(){
        System.out.println("Would you like to make a static order or dynamic order?");
        System.out.println("1. Static order");
        System.out.println("2. Dynamic order");
        int choose = Validation.getValidChoice(1, 2);
        if(choose ==1){
            makeStaticOrder();
        }
        else{
            makeDynamicOrder();
        }

    }

    private void makeDynamicOrder() {
        //Pair: amount,product
        Collection<Pair<Float, DTOProduct>> productsInOrder = new LinkedList<>();
        Date orderDate = getOrderDateFromUser();
        Point userLocation = getLocationDifferentFromStores();
        System.out.println("Please choose the products you would like to order:");
        chooseProducts(productsInOrder,true,null);
        //key in map: store id. value in map:a collection of products from the same store
        //key in pair:amount bought. value in pair: the product itself
        Map<Integer, Collection<Pair<Float,DTOProductInStore>>> cheapestBasket = sdmSystem.getCheapestBasket(productsInOrder);
        if(cheapestBasket.size() >= 1) {
            showSummeryOfDynamicOrder(cheapestBasket, userLocation);
            if (askIfConfirmOrder()) {
                sdmSystem.makeNewDynamicOrder(orderDate, userLocation, cheapestBasket);
                System.out.println("The order was made successfully!");
            }
        }
    }

    private void showSummeryOfDynamicOrder(Map<Integer, Collection<Pair<Float, DTOProductInStore>>> cheapestBasket, Point userLocation) {
        float deliveryCost;
        float totalDeliveryCost = 0;
        float costOfProductsInStore = 0;
        float totalCostOfProductsInStore = 0;
        System.out.println("Stores you are buying from:");
        for (Integer storeSerialNumber : cheapestBasket.keySet()) {
            DTOStore storeSellingTheProducts = sdmSystem.getStoreFromStores(storeSerialNumber);
            System.out.println("-------------------------------------------------------------------");
            printStoreIdAndName(storeSerialNumber, storeSellingTheProducts.getStoreName());
            printStoreCoordinate(storeSellingTheProducts.getStoreLocation());
            deliveryCost = sdmSystem.getDeliveryCost(storeSellingTheProducts.getStoreSerialNumber(), userLocation);
            totalDeliveryCost += deliveryCost;
            printDistanceFromStoreAndPpkAndDeliveryCost(storeSellingTheProducts, userLocation, deliveryCost);
            //the size of the collection is the number of kinds
            System.out.println("Number of products kinds bought from store: " + cheapestBasket.get(storeSerialNumber).size());
            costOfProductsInStore = sdmSystem.calcProductsInOrderCost(cheapestBasket.get(storeSerialNumber));
            totalCostOfProductsInStore += costOfProductsInStore;
            System.out.println("The cost of the products bought from store: " + costOfProductsInStore);
            System.out.println("-------------------------------------------------------------------");
        }
        System.out.printf("Total delivery cost: %.2f\n", (totalDeliveryCost));
        System.out.printf("Total products cost: %.2f\n", (totalCostOfProductsInStore));
        System.out.printf("Total order cost: %.2f\n", (totalDeliveryCost + totalCostOfProductsInStore));
    }

    private void printStoreCoordinate(Point storeLocation) {
        System.out.println("The store coordinates are:");
        System.out.println("X: " + storeLocation.x);
        System.out.println("Y: " + storeLocation.y);
    }

    private void printAllProductsForDynamicOrder() {
        for(DTOProduct product : sdmSystem.getProductsInSystem().values()){
            System.out.println("-------------------------------------------------------------------");
            printProduct(product);
            System.out.println("-------------------------------------------------------------------");
        }
    }


    private void makeStaticOrder() {
        Scanner s = new Scanner(System.in);
        //Pair: amount,product
        Collection<Pair<Float, DTOProduct>> productsInOrder = new LinkedList<>();
        boolean succeeded = false;
        float deliveryCost = 0;
        printAllStoresIdNamePpk();
        do {
            try {
                System.out.println("Please choose a store by entering its serial number: ");
                int chosenStoreSerialNumber = s.nextInt();
                DTOStore chosenStore = sdmSystem.getStoreFromStores(chosenStoreSerialNumber);
                if (chosenStore != null) {
                    Date orderDate = getOrderDateFromUser();
                    Point userLocation = getLocationDifferentFromStores();
                    chooseProducts(productsInOrder,false,chosenStore);
                    deliveryCost = sdmSystem.getDeliveryCost(chosenStore.getStoreSerialNumber(), userLocation);
                    if (productsInOrder.size() >= 1) {
                        Collection<Pair<Float, DTOProductInStore>> productsInOrderAsProductInStoreObj = createProductInStoreCollection(chosenStore,productsInOrder);
                        showSummeryOfStaticOrder(chosenStore, productsInOrderAsProductInStoreObj, deliveryCost, userLocation);
                        if (askIfConfirmOrder()) {
                            sdmSystem.makeNewStaticOrder(chosenStore, orderDate, deliveryCost, productsInOrderAsProductInStoreObj);
                            System.out.println("The order was made successfully!");
                        }
                    }
                    succeeded = true;

                    // }
                } else {
                    System.out.println("No such store in the system! Please try again!");
                }
            } catch (InputMismatchException e) {
                System.out.println("You must enter an integer!");
                s.nextLine();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                s.nextLine();
            }
        }
        while (!succeeded);
    }

    private Point getLocationDifferentFromStores() {
        Point userLocation = getLocationFromTheUser();
        while (!sdmSystem.checkIfLocationIsUnique(userLocation)) {
            System.out.println("The location must be different from all stores location!");
            userLocation = getLocationFromTheUser();
        }

        return userLocation;
    }

    private Collection<Pair<Float, DTOProductInStore>> createProductInStoreCollection(DTOStore chosenStore, Collection<Pair<Float, DTOProduct>> productsInOrder) {
        Collection<Pair<Float, DTOProductInStore>> productsInStore = new LinkedList<>();
        for(Pair<Float, DTOProduct> dtoProduct : productsInOrder){
            productsInStore.add(new Pair(dtoProduct.getKey(),chosenStore.getProductFromStore(dtoProduct.getValue().getProductSerialNumber())));
        }

        return productsInStore;
    }

    private boolean askIfConfirmOrder() {
        System.out.println("Do you confirm the order? insert Y\\N");
        return Validation.getValidYesOrNoAnswer();
    }

    private void showSummeryOfStaticOrder(DTOStore chosenStore,
                                          Collection<Pair<Float, DTOProductInStore>> productsInOrder,
                                          float deliveryCost,
                                          Point userLocation) {
        System.out.println("-------------------------------------------------------------------");
        System.out.println("Summery of order:" );
        for(Pair<Float, DTOProductInStore> productInOrder : productsInOrder){
            printProduct(productInOrder.getValue());
            System.out.println("Price: " + productInOrder.getValue().getPrice());
            System.out.println("Amount in order: " + productInOrder.getKey());
            System.out.println("Total amount of product: " + productInOrder.getValue().getPrice() * productInOrder.getKey());
            System.out.println("-------------------------------------------------------------------");
        }
        printDistanceFromStoreAndPpkAndDeliveryCost(chosenStore,userLocation,deliveryCost);
        System.out.printf("Total order cost: %.2f\n" ,(deliveryCost + sdmSystem.calcProductsInOrderCost(productsInOrder)));
    }

    private void printDistanceFromStoreAndPpkAndDeliveryCost(DTOStore store, Point userLocation, float deliveryCost){
        System.out.printf("Distance from store: %.2f\n",sdmSystem.getDistanceFromStore(store,userLocation));
        System.out.println("Store ppk: " + store.getPpk());
        System.out.printf("Delivery cost: %.2f\n", deliveryCost );
    }

//    private float calcProductsInOrderCost(Collection<Pair<Float, DTOProductInStore>> productsInOrder) {
//        float res = 0;
//        for(Pair<Float, DTOProductInStore> dtoProductInorder : productsInOrder){
//            res += (dtoProductInorder.getValue().getPrice() * dtoProductInorder.getKey());
//        }
//
//        return res;
//    }

    private void chooseProducts(Collection<Pair<Float,DTOProduct>> productsInOrder, boolean isDynamic, DTOStore chosenStore) {
        Scanner s = new Scanner(System.in);
        float amountToBuy;
        DTOProduct chosenProduct;
        boolean finished = false;
        while (!finished) {
            try {
                if(isDynamic){
                    printAllProductsForDynamicOrder();
                }
                else{
                    printAllProductsForOrderFromStore(chosenStore);
                }
                System.out.println("Choose a product by entering its serial number. insert 'q' if finished");
                String answer = s.nextLine();
                if(!Validation.isQ(answer)) {
                    int chosenProductSerialNumber = Integer.parseInt(answer);
                    if(isDynamic){
                        chosenProduct = sdmSystem.getProductFromSystem(chosenProductSerialNumber);
                    }
                    else {
                        chosenProduct = sdmSystem.getProductFromStore(chosenProductSerialNumber, chosenStore.getStoreSerialNumber());
                    }
                    if(chosenProduct != null) {
                        amountToBuy = getAmountToBuy(chosenProduct);
                        productsInOrder.add(new Pair<>(amountToBuy, chosenProduct));
                    }
                    else{
                        System.out.println("There are no such product! Please try again!");
                    }
                }
                else{
                    finished = true;
                }
            } catch (ExistenceException ex) {
                System.out.println(ex.getMessage());
            } catch (InputMismatchException | NumberFormatException | StringIndexOutOfBoundsException ex) {
                System.out.println("You must enter an integer or Q!");
            }
        }

    }

    private float getAmountToBuy(DTOProduct chosenProduct) {
        float amountToBuy;
        if (chosenProduct.getWayOfBuying() == WayOfBuying.BY_QUANTITY) {
            System.out.println("Please enter the number of units you would like to buy:");
            amountToBuy = Validation.getValidPositiveInteger();
        } else { //by weight
            System.out.println("Please enter how many kilos you would like to buy:");
            amountToBuy = Validation.getValidPositiveNumber();
        }

        return amountToBuy;
    }

    private void printAllProductsForOrderFromStore(DTOStore chosenStore) {
        System.out.println("The products from store " + chosenStore.getStoreSerialNumber() + ":");
        for (DTOProduct product : sdmSystem.getProductsInSystem().values()) {
            System.out.println("-------------------------------------------------------------------");
            System.out.println("Product serial number: " + product.getProductSerialNumber());
            System.out.println("Product name: " + product.getProductName());
            System.out.println("Way of buying: " + product.getWayOfBuying());
            System.out.print("Price: ");
            if (!sdmSystem.isAvailableInStore(chosenStore.getStoreSerialNumber(), product.getProductSerialNumber())) {
                System.out.println("The product is not available in that store!");
            } else {
                System.out.println(sdmSystem.getProductPrice(chosenStore.getStoreSerialNumber(),
                        product.getProductSerialNumber()));
            }
            System.out.println("-------------------------------------------------------------------");
        }

    }

    private Point getLocationFromTheUser() {
        Scanner s = new Scanner(System.in);
        int x,y;
        Point userLocation = null;
        boolean succeeded = false;
        do {
            try {
                System.out.println("Please enter your location using coordinates x,y.");
                System.out.print("x: ");
                x = s.nextInt();
                System.out.print("y: ");
                y = s.nextInt();
                if (Validation.checkIfLocationInRange(x, y, SDMSystem.MIN_COORDINATE, SDMSystem.MAX_COORDINATE)) {
//                    if (!(x == storeLocation.x && y == storeLocation.y)) {
                        userLocation = new Point(x, y);
                        succeeded = true;
//                    } else {
//                        System.out.println("The location can't be the same as the store location!");
//                    }
                }
            } catch (InputMismatchException ex) {
                System.out.println("You must enter in integer!");
                s.nextLine();
            }
        }
        while(!succeeded);

        return userLocation;
    }

    private Date getOrderDateFromUser()  {
        boolean succeeded = false;
        Date orderDate = null;
        Scanner s = new Scanner(System.in);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM-HH:mm");
        dateFormat.setLenient(false);
        do {
            try {
                System.out.println("Please enter the order date in this format: dd/MM-hh:mm");
                String dateInput = s.nextLine();
                orderDate = dateFormat.parse(dateInput);
                orderDate.setYear(120);
                succeeded = true;
            } catch (ParseException ex) {
                System.out.println("You must enter the date in the correct format!");
            }
        }
        while(!succeeded);

        return orderDate;
    }

    private void printAllStoresIdNamePpk() {
        Map<Integer, DTOStore> storesInSystem = sdmSystem.getStoresInSystemBySerialNumber();
        System.out.println("The stores in the system are:");
        for(DTOStore store : storesInSystem.values()){
            System.out.println("-------------------------------------------------------------------");
            printStoreIdAndName(store.getStoreSerialNumber(),store.getStoreName());
            System.out.println("Store PPK: " + store.getPpk());
        }
    }

    private void printStoreIdAndName(int storeSerialNumber,String storeName) {
        System.out.println("Store serial number: " + storeSerialNumber);
        System.out.println("Store name: " + storeName);
    }

    private boolean loadFileToSystem() {
        Scanner scanPath = new Scanner(System.in);
        boolean succeeded = false;
        System.out.print("Please insert the path of the xml file: ");
        String filePath = scanPath.nextLine();
        if(XMLHelper.isXmlFile(filePath)) {
            try {
                XMLHelper.FromXmlFileToObject(filePath, sdmSystem);
                succeeded = true;
            } catch (JAXBException e) {
                System.out.println("Something went wrong with JAXB. Please try different file.");
            } catch (FileNotFoundException e) {
                System.out.println("The file does not exist.");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        else{
            System.out.println("The file is not an XML file!");
        }
        return succeeded;
    }



    private void printAllStoresAndTheirProducts() {
        Map<Integer, DTOStore> storesInSystem = sdmSystem.getStoresInSystemBySerialNumber();
        for (DTOStore dtoStore : storesInSystem.values()) {
            System.out.println("-------------------------------------------------------------------");
            printDTOStoreAndItsProducts(dtoStore);
            System.out.println("-------------------------------------------------------------------");
        }
    }

    private void printDTOStoreAndItsProducts(DTOStore dtoStore) {
        System.out.println("Store ID: " + dtoStore.getStoreSerialNumber() +
            "\nStore name: " + dtoStore.getStoreName() +
            "\nProducts in store:\n");
        printDTOProductsInStore(dtoStore);
        System.out.println("Orders history: ");
        printDTOStoreOrderHistory(dtoStore);
        System.out.print("PPK: " + dtoStore.getPpk());
        System.out.printf("\nTotal profit from delivery: %.2f\n", dtoStore.getTotalProfitFromDelivery());
    }

    private void printDTOStoreOrderHistory(DTOStore dtoStore) {
        Collection<DTOOrder> storeOrders = sdmSystem.getOrdersFromStore(dtoStore.getStoreSerialNumber());
        if (storeOrders.size() != 0) {
            for (DTOOrder order : storeOrders) {
                printDTOOrder(order);
            }
        } else {
            System.out.println("There are no any orders yet!");
        }
    }

    private void printDTOOrder(DTOOrder order) {
        System.out.println("-------------------------------------------------------------------");
        System.out.println("Order Date: " + order.getOrderDate().toString());
        System.out.println("Number of products: " + order.getAmountOfProducts());
        System.out.printf("Products cost: %.2f\n", order.getProductsCost());
        System.out.printf("Delivery cost: %.2f\n", order.getDeliveryCost());
        System.out.printf("Order cost: %.2f\n", (order.getProductsCost() + order.getDeliveryCost()));
        System.out.println("-------------------------------------------------------------------");
    }


    private void printDTOProductsInStore(DTOStore dtoStore) {
        for(DTOProductInStore dtoProductInStore : dtoStore.getProductsInStore().values()) {
            printProductInStore(dtoProductInStore);
        }
    }

    private void printProductInStore(DTOProductInStore dtoProductInStore) {
        printProduct(dtoProductInStore);
        System.out.println("Price: " + dtoProductInStore.getPrice() +
                "\nAmount sold: " + dtoProductInStore.getAmountSoldInStore() + "\n");
    }

    private void printProduct(DTOProduct dtoProduct) {
        System.out.println("Product serial number: " + dtoProduct.getProductSerialNumber() +
                "\nProduct name: " + dtoProduct.getProductName() +
                "\nWay of buying: " + dtoProduct.getWayOfBuying());
    }


    private void printAllProducts() {
        Map<Integer, DTOProduct> productsInSystem = sdmSystem.getProductsInSystem();
        for (DTOProduct product : productsInSystem.values()) {
            System.out.println("-------------------------------------------------------------------");
            printProduct(product);
            System.out.println("Number of store selling the product: " + sdmSystem.getNumberOfStoresSellingProduct(product.getProductSerialNumber()));
            System.out.print("Average price: ");

            if(sdmSystem.getNumberOfStoresSellingProduct(product.getProductSerialNumber()) == 0){
                System.out.println( "There are no stores selling the product! ");
            }
            else{
                //System.out.println(product.averagePriceOfProduct());
                System.out.println(sdmSystem.getAveragePriceOfProduct(product.getProductSerialNumber()));
            }
            System.out.println("Amount sold in all stores: " + product.getAmountSoldInAllStores());
            System.out.println("-------------------------------------------------------------------");
        }
    }
}
