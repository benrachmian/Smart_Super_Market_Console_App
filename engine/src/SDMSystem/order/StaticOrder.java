package SDMSystem.order;

import SDMSystem.product.ProductInStore;
import SDMSystem.store.Store;
import SDMSystemDTO.order.DTOOrder;
import SDMSystemDTO.store.DTOStore;
import javafx.util.Pair;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

public class StaticOrder extends Order {
    private Store storeFromWhomTheOrderWasMade;

    public StaticOrder(Date orderDate,
                       Collection<Pair<Float, ProductInStore>> productsInOrder,
                       float productsCost,
                       float deliveryCost,
                       int amountOfProducts,
                       int amountOfProductsKinds,
                       Store storeFromWhomTheOrderWasMade) {
        super(orderDate, productsInOrder, productsCost, deliveryCost, amountOfProducts, amountOfProductsKinds);
        this.storeFromWhomTheOrderWasMade = storeFromWhomTheOrderWasMade;
    }

    @Override
    public DTOOrder createDTOOrderFromOrder() {
        Collection<DTOStore> storesFromWhomTheOrderWasMade = new LinkedList();
        storesFromWhomTheOrderWasMade.add(storeFromWhomTheOrderWasMade.createDTOStore());
        DTOOrder dtoOrder = new DTOOrder(getOrderDate(),
                getDTOProductsInOrder(),
                getProductsCost(),
                getDeliveryCost(),
                getOrderSerialNumber(),
                storesFromWhomTheOrderWasMade,
                getAmountOfProducts(),
                getAmountOfProductsKinds());

        return dtoOrder;
    }

    public Store getStoreFromWhomTheOrderWasMade() {
        return storeFromWhomTheOrderWasMade;
    }
}

