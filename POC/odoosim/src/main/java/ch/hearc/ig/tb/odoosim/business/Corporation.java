package ch.hearc.ig.tb.odoosim.business;

import java.util.ArrayList;

public class Corporation {
    private int identifier;
    private String name;
    private ArrayList<Offer> supply;

    public Corporation(int identifier, String name) {
        this.identifier = identifier;
        this.name = name;
    }
    
}