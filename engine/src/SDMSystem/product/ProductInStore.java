package SDMSystem.product;

public class ProductInStore extends Product {
    private float price;
    private float amountSold;


    public ProductInStore(String productName, WayOfBuying wayOfBuying, float price) {
        super(productName, wayOfBuying);
        this.price = price;
        this.amountSold = 0;
    }

    public ProductInStore(Product newProduct, float price) {
        super(newProduct);
        this.price = price;
    }

    public float getPrice() {
        return price;
    }

    public float getAmountSold() {
        return amountSold;
    }
}
