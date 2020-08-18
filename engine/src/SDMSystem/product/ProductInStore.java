package SDMSystem.product;

import SDMSystemDTO.product.DTOProductInStore;
import SDMSystemDTO.product.WayOfBuying;

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

    public void increaseAmountSoldInStore(float amountSold){
        amountSoldInStore += amountSold;
    }

    public float getPrice() {
        return price;
    }

    public float getAmountSoldInStore() {
        return amountSoldInStore;
    }

    public DTOProductInStore createDTOProductInStore() {
        DTOProductInStore newDTOProductInStore = new DTOProductInStore(
                productSerialNumber,
                productName,
                wayOfBuying,
                amountSoldInAllStores,
                getPrice(),
                amountSoldInStore);
        return newDTOProductInStore;
    }

//    @Override
//    public String toString() {
//        return super.toString() +
//                "\nPrice: " + price +
//                "\nAmount sold: " + amountSoldInStore + "\n";
//    }
}
