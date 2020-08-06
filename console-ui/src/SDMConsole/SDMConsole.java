package SDMConsole;

import SDMSystem.SDMSystem;
import xml.XMLHelper;

public class SDMConsole {
    SDMSystem sdmSystem;

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


        XMLHelper.FromXmlFileToObject("/xml/ex1-small.xml",sdmSystem);

    }


}
