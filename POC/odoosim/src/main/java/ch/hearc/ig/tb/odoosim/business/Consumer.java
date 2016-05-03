package ch.hearc.ig.tb.odoosim.business;

import java.util.ArrayList;

public class Consumer {
    private int identifier;
    private String name;
    private ArrayList<Demand> needs;

    public Consumer(int identifier, String name) {
        this.identifier = identifier;
        this.name = name;
    }

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Demand> getNeeds() {
        return needs;
    }

    public void setNeeds(ArrayList<Demand> needs) {
        this.needs = needs;
    }
    
    
    
}