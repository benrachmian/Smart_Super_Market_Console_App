package SDMSystem.system;

import SDMSystem.store.Store;
import SDMSystem.exceptions.*;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;


class StoresInSystem {

    private Map<Integer, Store> storesInSystemBySerialNumber;
    private Map<Point, Store> storesInSystemByLocation;

    public StoresInSystem() {
        storesInSystemBySerialNumber = new HashMap<>();
        storesInSystemByLocation = new HashMap<>();
    }

    public void addStoreToSystem(Store newStore, Point newStoreLocation){
        if (storesInSystemBySerialNumber.putIfAbsent(newStore.getSerialNumber(),newStore) !=null) {
            throw new ExistenceException(true,newStore.getSerialNumber(),"Store","System");
        }
        if (storesInSystemByLocation.putIfAbsent(newStoreLocation,newStore) != null){
            storesInSystemBySerialNumber.remove(newStore.getSerialNumber());
            throw new RuntimeException("There is already a store in that location!");
        }
    }

    public Map<Integer, Store> getStoresInSystemBySerialNumber() {
        return storesInSystemBySerialNumber;
    }

    public Map<Point, Store> getStoresInSystemByLocation() {
        return storesInSystemByLocation;
    }
}
