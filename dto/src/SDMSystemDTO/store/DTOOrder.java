package SDMSystemDTO.store;

import SDMSystemDTO.product.DTOProductInStore;
import javafx.util.Pair;

import java.util.Collection;
import java.util.Date;

public class DTOOrder {
    private Date orderDate;
    private Collection<Pair<Float,DTOProductInStore>> productsInOrder;
    //private Customer whoOrdered;
    private float productsCost;
    private float deliveryCost;
    private final int orderSerialNumber;
    private DTOStore storeFromWhomTheOrderWasMade;
    private int amountOfProducts;
    private int amountOfProductsKinds;


    public DTOOrder(Date orderDate,
                    Collection<Pair<Float,DTOProductInStore>> productsInOrder,
                    float productsCost,
                    float deliveryCost,
                    int orderSerialNumber,
                    DTOStore storeFromWhomTheOrderWasMade,
                    int amountOfProducts,
                    int amountOfProductsKinds) {
        this.orderDate = orderDate;
        this.productsInOrder = productsInOrder;
        this.productsCost = productsCost;
        this.deliveryCost = deliveryCost;
        this.orderSerialNumber = orderSerialNumber;
        this.storeFromWhomTheOrderWasMade = storeFromWhomTheOrderWasMade;
        this.amountOfProducts = amountOfProducts;
        this.amountOfProductsKinds = amountOfProductsKinds;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public int getAmountOfProducts() {
        return amountOfProducts;
    }

    public float getOrderCost(){
        return deliveryCost + productsCost;
    }

    public int getAmountOfProductsKinds() {
        return amountOfProductsKinds;
    }

    public DTOStore getStoreFromWhomTheOrderWasMade() {
        return storeFromWhomTheOrderWasMade;
    }

    public int getOrderSerialNumber() {
        return orderSerialNumber;
    }

    public Collection<Pair<Float,DTOProductInStore>> getProductsInOrder() {
        return productsInOrder;
    }

    public float getProductsCost() {
        return productsCost;
    }

    public float getDeliveryCost() {
        return deliveryCost;
    }
}
