package com.cinua.spacetrader.gameplay;
import com.cinua.spacetrader.database.DatabaseObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

public class  Port extends DatabaseObject{
    private String name;
    private Vector<Integer> position;
    public static final int prohibited = -1;
    private Cargo[] cargos;
    private int[] priceMultipliers;
    private int[] stock;

    public Port(int id, String name, Vector<Integer> position, Cargo[] cargos, int[] priceMultipliers, int[] stock){
        super(id);
        this.name = name;
        this.position = position;
        this.cargos = cargos;
        this.priceMultipliers = priceMultipliers;
        this. stock = stock;


    }
    public String getName(){
        return name;
    }

    public Vector<Integer> getPosition(){
        return position;
    }

    public static int getDistanceBetween(Vector<Integer> origin, Vector<Integer> destination){
        return Math.abs(Math.abs(destination.get(0) - origin.get(0)) + Math.abs(destination.get(0) - origin.get(0)));
    }

    public int getDistanceTo(Port destination){
        Vector<Integer> destinationPosition = destination.getPosition();
        return Math.abs(position.get(0) - destinationPosition.get(0)) + Math.abs(position.get(1) - destinationPosition.get(1));
    }


    public static final int itemNotInStock = 0;

    private int getCargoLookupSize(){
        if(stock.length < priceMultipliers.length && stock.length < cargos.length){
            return stock.length;
        }else if(priceMultipliers.length < cargos.length && priceMultipliers.length < stock.length){
            return priceMultipliers.length;
        }else{
            return cargos.length;
        }
    }
    public Cargo[] getItemsInStock(){
        ArrayList<Cargo> list = new ArrayList<>();
        for(int currentCargo = 0; currentCargo < getCargoLookupSize(); currentCargo++){
            if(stock[currentCargo] > 0){
                list.add(cargos[currentCargo]);
            }
        }
        return list.toArray(new Cargo[0]);
    }

    public boolean isItemInStock(Cargo item){
        return Arrays.asList(getItemsInStock()).contains(item);
    }

    public int getStockByItem(Cargo item){
        return stock[Arrays.binarySearch(cargos, item)];
    }

    public int getPriceForItemInStock(Cargo item){
        return item.getBasePrice()*priceMultipliers[Arrays.binarySearch(cargos, item)];
    }
}
