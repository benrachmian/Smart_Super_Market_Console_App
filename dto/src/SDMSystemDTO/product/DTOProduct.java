package SDMSystemDTO.product;

import SDMSystem.product.Product;
import SDMSystemDTO.store.DTOStore;

import java.util.Collection;
import java.util.LinkedList;



public class DTOProduct {

    protected final int productSerialNumber;
    protected final String productName;
    protected final WayOfBuying wayOfBuying;
   // protected Collection<DTOStore> storesSellingTheProduct = new LinkedList<>();
    protected float amountSoldInAllStores = 0;

    public DTOProduct(int productSerialNumber,
                      String productName,
                      WayOfBuying wayOfBuying,
                      //Collection<DTOStore> storesSellingTheProduct,
                      float amountSoldInAllStores) {
        this.productSerialNumber = productSerialNumber;
        this.productName = productName;
        this.wayOfBuying = wayOfBuying;
        //this.storesSellingTheProduct = null;
        this.amountSoldInAllStores = amountSoldInAllStores;
    }

    public int getProductSerialNumber() {
        return productSerialNumber;
    }

    public String getProductName() {
        return productName;
    }

    public WayOfBuying getWayOfBuying() {
        return wayOfBuying;
    }

//    public Collection<DTOStore> getStoresSellingTheProduct() {
//        return storesSellingTheProduct;
//    }

    public float getAmountSoldInAllStores() {
        return amountSoldInAllStores;
    }

//    public int numberOfStoresSellingTheProduct() {
//        return storesSellingTheProduct.size();
//    }
}
