package SDMSystem.product;

public class ProductInStore extends Product {
    private float price;
    private float amountSoldInStore;


    public ProductInStore(String productName, WayOfBuying wayOfBuying, float price) {
        super(productName, wayOfBuying);
        this.price = price;
        this.amountSoldInStore = 0;
    }

    public ProductInStore(Product newProduct, float price) {
        super(newProduct);
        this.price = price;
    }

    public float getPrice() {
        return price;
    }

    public float getAmountSoldInStore() {
        return amountSoldInStore;
    }

    @Override
    public String toString() {
        return super.toString() +
                "\nPrice: " + price +
                "\nAmount sold: " + amountSoldInStore + "\n";
    }
}
