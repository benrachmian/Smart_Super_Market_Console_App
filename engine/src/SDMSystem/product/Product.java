package SDMSystem.product;

import SDMSystem.HasSerialNumber;

public class Product implements HasSerialNumber<Integer> {


    public enum WayOfBuying {
        BY_QUANTITY, BY_WEIGHT
    }

    protected static int generatedSerialNumber = 1000;
    protected final int productSerialNumber;
    protected final String productName;
    protected final WayOfBuying wayOfBuying;

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

    @Override
    public String toString() {
        return "Product serial number: " + productSerialNumber +
                "\nProduct name: " + productName +
                "\nWay of buying: " + wayOfBuying;
    }
}
