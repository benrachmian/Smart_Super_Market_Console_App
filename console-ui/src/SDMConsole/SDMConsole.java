package SDMConsole;

import SDMSystem.exceptions.ExistenceException;
import SDMSystem.product.Product;
import SDMSystem.product.ProductInStore;
import SDMSystem.store.Order;
import SDMSystem.system.SDMSystem;
import SDMSystem.store.Store;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;
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
        int choose = Validation.getValidChoice(MIN_CHOOSE,MAX_CHOOSE);

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
                if(loadFileToSystem()){ //option 1
                    fileLoaded = true;
                    System.out.println("File loaded successfully!");
                }
            }
            printOpeningMenu();
            choose = Validation.getValidChoice(MIN_CHOOSE,MAX_CHOOSE);
        }
    }

    private void makeOrder() {
        Scanner s = new Scanner(System.in);
        Order orderInProgress = new Order();
        //Collection<ProductInStore> productsInOrder = new LinkedList<>();
        boolean succeeded = false;
        printAllStoresIdNamePpk();
        do {
            try {
                System.out.println("Please choose a store by entering its serial number: ");
                int chosenStoreSerialNumber = s.nextInt();
                Store chosenStore = sdmSystem.getStoreFromStores(chosenStoreSerialNumber);
                if (chosenStore != null) {
                    orderInProgress.setOrderDate(getOrderDateFromUser());
                    Point userLocation = getLocationFromTheUser(chosenStore.getLocation());
                    if (userLocation != null) {
                        printAllProductsForOrderFromStore(chosenStore);
                        chooseProductAndBuy(chosenStore, orderInProgress);
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
        while(!succeeded);


    }

    private void chooseProductAndBuy(Store chosenStore, Order orderInProgress) {
        Scanner s = new Scanner(System.in);
        float amountToBuy;
        boolean finished = false;
        while(!finished) {
            try {
                System.out.println("Choose a product by entering its serial number");
                int chosenProductSerialNumber = s.nextInt();
                ProductInStore chosenProduct = chosenStore.getProductInStore(chosenProductSerialNumber);
                amountToBuy = getAmountToBuy(chosenProduct);
                orderInProgress.addProductToOrder(chosenProduct);
                finished = !askIfFinishedOrdering();
                if(!finished && askIfShowProductsAgain()){
                    printAllProductsForOrderFromStore(chosenStore);
                }
            } catch (ExistenceException ex) {
                System.out.println(ex.getMessage());
            } catch (InputMismatchException ex){
                System.out.println("You must enter an integer!");
                s.nextLine();
            }
        }

    }

    private boolean askIfShowProductsAgain() {
        System.out.println("Would you like to view the products in the store again?");
        return Validation.getValidYesOrNoAnswer();
    }

    private boolean askIfFinishedOrdering() {
        System.out.println("Would you like to order another product? insert Y\\N");
        return Validation.getValidYesOrNoAnswer();
    }

    private float getAmountToBuy(ProductInStore chosenProduct) {
        float amountToBuy;
        if(chosenProduct.getWayOfBuying() == Product.WayOfBuying.BY_QUANTITY){
            System.out.println("Please enter the number of units you would like to buy:");
            amountToBuy = Validation.getValidPositiveInteger();
        }
        else{ //by weight
            System.out.println("Please enter how many kilos you would like to buy:");
            amountToBuy = Validation.getValidPositiveNumber();
        }

        return amountToBuy;
    }

    private void printAllProductsForOrderFromStore(Store chosenStore) {
        System.out.println("The products from store " + chosenStore.getSerialNumber() + ":");
        ProductInStore productInChosenStore;
        for(Product product : sdmSystem.getProductsInSystem().values()){
            System.out.println("-------------------------------------------------------------------");
            System.out.println("Product serial number: " + product.getSerialNumber());
            System.out.println("Product name: " + product.getProductName());
            System.out.println("Way of buying: " + product.getWayOfBuying());
            System.out.print("Price: ");
            //productInChosenStore = chosenStore.getProductInStore(product.getSerialNumber());
            if(!chosenStore.isAvailableInStore(product.getSerialNumber())){
                System.out.println("The product is not available in that store!");
            }
            else{
                System.out.println(chosenStore.getProductInStore(product.getSerialNumber()).getPrice());
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

    private Date getOrderDateFromUser() throws ParseException {
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
        Map<Integer, Store> storesInSystem = sdmSystem.getStoresInSystemBySerialNumber();
        System.out.println("The stores in the system are:");
        for(Store store : storesInSystem.values()){
            System.out.println("-------------------------------------------------------------------");
            System.out.println("Store serial number: " + store.getSerialNumber());
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
        Map<Integer, Store> storesInSystem = sdmSystem.getStoresInSystemBySerialNumber();
        for (Store store : storesInSystem.values()) {
            System.out.println("-------------------------------------------------------------------");
            System.out.println(store.toString());
            System.out.println("-------------------------------------------------------------------");
        }
    }

    private void printAllProducts() {
        Map<Integer, Product> productsInSystem = sdmSystem.getProductsInSystem();
        for (Product product : productsInSystem.values()) {
            System.out.println("-------------------------------------------------------------------");
            System.out.println(product.toString());
            System.out.println("Number of store selling the product: " + product.numberOfStoresSellingTheProduct());
            System.out.print("Average price: ");

            if(product.numberOfStoresSellingTheProduct() == 0){
                System.out.println( "There are no stores selling the product! ");
            }
            else{
                System.out.println(product.averagePriceOfProduct());
            }
            System.out.println("Amount sold in all stores: " + product.getAmountSoldInAllStores());
            System.out.println("-------------------------------------------------------------------");
        }
    }
}
