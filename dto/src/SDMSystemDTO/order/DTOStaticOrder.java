//package SDMSystemDTO.order;
//
//import SDMSystemDTO.product.DTOProductInStore;
//import SDMSystemDTO.store.DTOStore;
//import javafx.util.Pair;
//
//import java.util.Collection;
//import java.util.Date;
//
//public class DTOStaticOrder extends DTOOrder {
//    private DTOStore storeFromWhomTheOrderWasMade;
//
//    public DTOStaticOrder(Date orderDate,
//                          Collection<Pair<Float, DTOProductInStore>> productsInOrder,
//                          float productsCost,
//                          float deliveryCost,
//                          int orderSerialNumber,
//                          int amountOfProducts,
//                          int amountOfProductsKinds,
//                          DTOStore storeFromWhomTheOrderWasMade) {
//        super(orderDate, productsInOrder, productsCost, deliveryCost, orderSerialNumber, amountOfProducts, amountOfProductsKinds);
//        this.storeFromWhomTheOrderWasMade = storeFromWhomTheOrderWasMade;
//    }
//}
//
