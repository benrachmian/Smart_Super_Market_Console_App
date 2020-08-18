package SDMSystemDTO.store;

import SDMSystem.store.Order;
import SDMSystemDTO.product.DTOProductInStore;

import java.util.Collection;
import java.util.Date;

public class DTOOrder {
    private Date orderDate;
    private Collection<DTOProductInStore> productsInOrder;
    //private Customer whoOrdered;
    private float productsCost;
    private float deliveryCost;

    public DTOOrder(Date orderDate, Collection<DTOProductInStore> productsInOrder, float productsCost, float deliveryCost) {
        this.orderDate = orderDate;
        this.productsInOrder = productsInOrder;
        this.productsCost = productsCost;
        this.deliveryCost = deliveryCost;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public Collection<DTOProductInStore> getProductsInOrder() {
        return productsInOrder;
    }

    public float getProductsCost() {
        return productsCost;
    }

    public float getDeliveryCost() {
        return deliveryCost;
    }
}
