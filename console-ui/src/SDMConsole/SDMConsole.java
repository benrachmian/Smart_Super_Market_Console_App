package SDMConsole;

import SDMSystem.exceptions.ExistenceException;
import SDMSystem.product.Product;
import SDMSystem.product.ProductInStore;
import SDMSystem.store.Order;
import SDMSystem.system.SDMSystem;
import SDMSystem.store.Store;
import SDMSystemDTO.product.DTOProduct;
import SDMSystemDTO.product.DTOProductInStore;
import SDMSystemDTO.product.WayOfBuying;
import SDMSystemDTO.store.DTOOrder;
import SDMSystemDTO.store.DTOStore;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;
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

    private void makeOrder() {
        Scanner s = new Scanner(System.in);
        //Pair: amount,product
        Collection<Pair<Float,DTOProductInStore>> productsInOrder = new LinkedList<>();
        boolean succeeded = false;
        printAllStoresIdNamePpk();
        do {
            try {
                System.out.println("Please choose a store by entering its serial number: ");
                int chosenStoreSerialNumber = s.nextInt();
                DTOStore chosenStore = sdmSystem.getStoreFromStores(chosenStoreSerialNumber);
                if (chosenStore != null) {
                    Date orderDate = getOrderDateFromUser();
                    Point userLocation = getLocationFromTheUser(chosenStore.getStoreLocation());
                    if (userLocation != null) {
                        printAllProductsForOrderFromStore(chosenStore);
                        chooseProductAndBuy(chosenStore, productsInOrder);
                        sdmSystem.makeNewOrder(chosenStore, orderDate, userLocation, productsInOrder);
                        succeeded = true;
                    }
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

    private void chooseProductAndBuy(DTOStore chosenStore,  Collection<Pair<Float,DTOProductInStore>> productsInOrder) {
        Scanner s = new Scanner(System.in);
        float amountToBuy;
        boolean finished = false;
        while (!finished) {
            try {
                System.out.println("Choose a product by entering its serial number");
                int chosenProductSerialNumber = s.nextInt();
                DTOProductInStore chosenProduct = sdmSystem.getProductFromStore(chosenProductSerialNumber, chosenStore.getStoreSerialNumber());
                amountToBuy =  getAmountToBuy(chosenProduct);
                productsInOrder.add(new Pair<Float,DTOProductInStore>(amountToBuy,chosenProduct));
                finished = !askIfFinishedOrdering();
                if (!finished && askIfShowProductsAgain()) {
                    printAllProductsForOrderFromStore(chosenStore);
                }
            } catch (ExistenceException ex) {
                System.out.println(ex.getMessage());
            } catch (InputMismatchException ex) {
                System.out.println("You must enter an integer!");
                s.nextLine();
            }
        }

    }

    private boolean askIfShowProductsAgain() {
        System.out.println("Would you like to view the products in the store again? insert Y\\N");
        return Validation.getValidYesOrNoAnswer();
    }

    private boolean askIfFinishedOrdering() {
        System.out.println("Would you like to order another product? insert Y\\N");
        return Validation.getValidYesOrNoAnswer();
    }

    private float getAmountToBuy(DTOProductInStore chosenProduct) {
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
        ProductInStore productInChosenStore;
        for (DTOProduct product : sdmSystem.getProductsInSystem().values()) {
            System.out.println("-------------------------------------------------------------------");
            System.out.println("Product serial number: " + product.getProductSerialNumber());
            System.out.println("Product name: " + product.getProductName());
            System.out.println("Way of buying: " + product.getWayOfBuying());
            System.out.print("Price: ");
            //if(!chosenStore.isAvailableInStore(product.getSerialNumber())){
            if (!sdmSystem.isAvailableInStore(chosenStore.getStoreSerialNumber(), product.getProductSerialNumber())) {
                System.out.println("The product is not available in that store!");
            } else {
                //System.out.println(chosenStore.getProductInStore(product.getSerialNumber()).getPrice());
                System.out.println(sdmSystem.getProductPrice(chosenStore.getStoreSerialNumber(),product.getProductSerialNumber()));
            }
            System.out.println("-------------------------------------------------------------------");
        }

    }

    private Point getLocationFromTheUser(Point storeLocation) {
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
                    if (!(x == storeLocation.x && y == storeLocation.y)) {
                        userLocation = new Point(x, y);
                        succeeded = true;
                    } else {
                        System.out.println("The location can't be the same as the store location!");
                    }
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
        SimpleDateFormat format = new SimpleDateFormat("dd/MM-hh:mm");
        do {
            try {
                System.out.println("Please enter the order date in this format: dd/MM-hh:mm");
                String dateInput = s.nextLine();
                orderDate = format.parse(dateInput);
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
            System.out.println("Store serial number: " + store.getStoreSerialNumber());
            System.out.println("Store name: " + store.getStoreName());
            System.out.println("Store PPK: " + store.getPpk());
        }
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
        System.out.print("Orders history: ");
        printDTOStoreOrderHistory(dtoStore);
        System.out.print("PPK: " + dtoStore.getPpk());
        System.out.println("\nTotal profit from delivery: " + dtoStore.getTotalProfitFromDelivery());
    }

    private void printDTOStoreOrderHistory(DTOStore dtoStore) {
        if (dtoStore.getOrdersFromStore().size() != 0) {
            for (DTOOrder order : dtoStore.getOrdersFromStore()) {
                printDTOOrder(order);
            }
        } else {
            System.out.println("There are no any orders yet!");
        }
    }

    private void printDTOOrder(DTOOrder order) {
        System.out.println("Order Date=" + order.getOrderDate().toString() +
                "\nNumber of products: " + order.getProductsInOrder().size() +
                "\nProducts cost: " + order.getProductsCost() +
                "\nDelivery cost: " + order.getDeliveryCost() +
                "\nOrder cost: " + (order.getProductsCost() + order.getDeliveryCost()));
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
