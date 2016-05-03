package ch.hearc.ig.tb.odoosim.business;

import java.util.ArrayList;

public class Market {
    private String name;
    private Good good;
    private ArrayList<Exchange> exchanges;

    public Market(String name, Good good) {
        this.name = name;
        this.good = good;
        this.exchanges = null;
    }
    
    
    @Override
    public String toString() {
        String demands = "";
        for(Demand d : good.getDemands())
            demands += d.toString() + " \n\r";
        return demands;
    }
}