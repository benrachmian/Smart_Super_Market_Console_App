package SDMConsole;

import SDMSystem.system.SDMSystem;
import SDMSystem.store.Store;
import xml.XMLHelper;

import java.util.Map;
import java.util.Scanner;

public class SDMConsole {
    private SDMSystem sdmSystem;
    private static final int EXIT = 6;

    public SDMConsole() {
        sdmSystem = new SDMSystem();
    }

    private String getOpeningMenu(){
        String openingMenu =
                "Welcome to Super Duper Market!\n" +
                "What would you like to do?\n" +
                "Please insert the option's number.\n" +
                "1.Read the system details from an XML file\n" +
                "2.Show stores information\n" +
                "3.Show all products in the system\n" +
                "4.Make order\n" +
                "5.Show order history\n" +
                "6.Exit";
        return openingMenu;
    }

    public void startApp(){
        String openingMenu = getOpeningMenu();
        System.out.println(openingMenu);
        Scanner s = new Scanner(System.in);
        int answer = s.nextInt();

        while(answer != EXIT) {
            switch (answer) {
                case 1:
                    XMLHelper.FromXmlFileToObject("/xml/ex1-small.xml", sdmSystem);
                    break;
                case 2:
                    printAllStores();
                    break;
                default:
                    System.out.println("def");
            }
            System.out.println(openingMenu);
            answer = s.nextInt();
        }





    }

    private void printAllStores() {
        Map<Integer, Store> storesInSystem = sdmSystem.getStoresInSystemBySerialNumber();
        for(Store store : storesInSystem.values()){
            System.out.println("-------------------------------------------------------------------");
            System.out.println(store.toString());
            System.out.println("-------------------------------------------------------------------");
        }
    }


}
