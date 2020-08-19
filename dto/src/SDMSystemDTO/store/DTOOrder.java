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
    private Collection<DTOStore> storesFromWhomTheOrderWasMade;
    private int amountOfProducts;
    private int amountOfProductsKinds;


    public DTOOrder(Date orderDate,
                    Collection<Pair<Float,DTOProductInStore>> productsInOrder,
                    float productsCost,
                    float deliveryCost,
                    int orderSerialNumber,
                    Collection<DTOStore> storesFromWhomTheOrderWasMade,
                    int amountOfProducts,
                    int amountOfProductsKinds) {
        this.orderDate = orderDate;
        this.productsInOrder = productsInOrder;
        this.productsCost = productsCost;
        this.deliveryCost = deliveryCost;
        this.orderSerialNumber = orderSerialNumber;
        this.storesFromWhomTheOrderWasMade = storesFromWhomTheOrderWasMade;
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

    public Collection<DTOStore> getStoreFromWhomTheOrderWasMade() {
        return storesFromWhomTheOrderWasMade;
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
