package Product;

public class Product {
    public enum WayOfBuying {
        BYQUANTITY, BYWEIGHT
    }

    private static int generatedSerialNumber = 1000;
    private final int serialNumber;
    private final String productName;
    private final WayOfBuying wayOfBuying;

    public Product(String productName, WayOfBuying wayOfBuying) {
        this.serialNumber = generatedSerialNumber++;
        this.productName = productName;
        this.wayOfBuying = wayOfBuying;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public String getProductName() {
        return productName;
    }

    public WayOfBuying getWayOfBuying(){
        return wayOfBuying;
    }

}
