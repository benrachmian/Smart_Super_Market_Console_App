package SDMSystemDTO.product;

import SDMSystemDTO.store.DTOStore;

import java.util.Collection;

public class DTOProductInStore extends DTOProduct {
    private float price;
    private float amountSoldInStore;
    private int storeTheProductBelongsID;

    public DTOProductInStore(int productSerialNumber,
                             String productName,
                             WayOfBuying wayOfBuying,
                             //Collection<DTOStore> storesSellingTheProduct,
                             float amountSoldInAllStores,
                             float price,
                             float amountSoldInStore,
                             int storeTheProductBelongsID) {
        super(productSerialNumber, productName, wayOfBuying, /*storesSellingTheProduct*/ amountSoldInAllStores);
        this.price = price;
        this.amountSoldInStore = amountSoldInStore;
        this.storeTheProductBelongsID = storeTheProductBelongsID;
    }


    public int getStoreTheProductBelongsID() {
        return storeTheProductBelongsID;
    }

    public float getPrice() {
        return price;
    }

    public float getAmountSoldInStore() {
        return amountSoldInStore;
    }

    public void setAmountSoldInStore(float amountSoldInStore) {
        this.amountSoldInStore = amountSoldInStore;
    }
}

