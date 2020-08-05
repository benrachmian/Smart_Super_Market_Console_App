package SDMSystem.Product;

public abstract class Product {


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

    public Product(Product newProduct) {
        this.productSerialNumber = newProduct.getProductSerialNumber();
        this.productName = newProduct.getProductName();
        this.wayOfBuying = newProduct.getWayOfBuying();
    }

    public int getProductSerialNumber() {
        return productSerialNumber;
    }

    public String getProductName() {
        return productName;
    }

    public WayOfBuying getWayOfBuying(){
        return wayOfBuying;
    }



}
