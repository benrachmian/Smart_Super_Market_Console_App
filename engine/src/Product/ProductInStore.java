package Product;

public class ProductInStore extends Product {

    float price;

    public ProductInStore(String productName, WayOfBuying wayOfBuying, float price) {
        super(productName, wayOfBuying);
        this.price = price;
    }

    public float getPrice() {
        return price;
    }
}
