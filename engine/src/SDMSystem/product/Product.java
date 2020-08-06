package SDMSystem.product;

import SDMSystem.HasSerialNumber;

public class Product implements HasSerialNumber<Integer> {


    public enum WayOfBuying {
        BYQUANTITY, BYWEIGHT
    }

    private static int generatedSerialNumber = 1000;
    private final int productSerialNumber;
    private final String productName;
    private final WayOfBuying wayOfBuying;

    public Product(String productName, WayOfBuying wayOfBuying) {
        this.productSerialNumber = generatedSerialNumber++;
        this.productName = productName;
        this.wayOfBuying = wayOfBuying;
    }

    public Product(int productSerialNumber ,String productName, WayOfBuying wayOfBuying) {
        this.productSerialNumber = productSerialNumber;
        this.productName = productName;
        this.wayOfBuying = wayOfBuying;
    }

    public Product(Product newProduct) {
        this.productSerialNumber = newProduct.getSerialNumber();
        this.productName = newProduct.getProductName();
        this.wayOfBuying = newProduct.getWayOfBuying();
    }

    @Override
    public Integer getSerialNumber() {
        return productSerialNumber;
    }

    public String getProductName() {
        return productName;
    }

    public WayOfBuying getWayOfBuying(){
        return wayOfBuying;
    }



}
