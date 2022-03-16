package com.cinua.spacetrader.gameplay.planet;

import com.cinua.spacetrader.gameplay.Cargo;

import java.util.Arrays;
import java.util.HashMap;

public class Market{
    private HashMap<Cargo, Integer> priceMultiplierLookup;
    private HashMap<Cargo, Integer> stockLookup;
    public static final int itemNotInStock = 0;

    public Market(HashMap<Cargo, Integer> stock, HashMap<Cargo, Integer> prices){
        stockLookup = stock;
        priceMultiplierLookup = prices;
    }

    public Cargo[] getItemsInStock(){
        return stockLookup.keySet().toArray(new Cargo[0]);
    }

    public boolean isItemInStock(Cargo item){
        return Arrays.asList(getItemsInStock()).contains(item);
    }

    public int getStockByItem(Cargo item){
        return stockLookup.get(item);
    }

    public int getPriceForItemInStock(Cargo item){
        return item.getBasePrice()*priceMultiplierLookup.get(item);
    }
}