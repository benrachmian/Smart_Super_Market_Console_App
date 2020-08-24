//package SDMSystemDTO.order;
//
//import SDMSystemDTO.product.DTOProductInStore;
//import javafx.util.Pair;
//
//import java.util.Collection;
//import java.util.Date;
//
//public class DTODynamicOrder extends DTOOrder {
//    private Collection<DTOStaticOrder> subOrders;
//
//    public DTODynamicOrder(Date orderDate,
//                           Collection<Pair<Float, DTOProductInStore>> productsInOrder,
//                           float productsCost,
//                           float deliveryCost,
//                           int orderSerialNumber,
//                           int amountOfProducts,
//                           int amountOfProductsKinds,
//                           Collection<DTOStaticOrder> subOrders) {
//        super(orderDate, productsInOrder, productsCost, deliveryCost, orderSerialNumber, amountOfProducts, amountOfProductsKinds);
//        this.subOrders = subOrders;
//    }
//}
