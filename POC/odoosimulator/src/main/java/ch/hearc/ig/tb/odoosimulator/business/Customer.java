package ch.hearc.ig.tb.odoosimulator.business;

import java.util.ArrayList;
import java.util.List;

public class Customer {
    
    private int id;
    private String name;
    private List<Demand> needs;

    public Customer() {
    }

    public Customer(int id, String name) {
        this.id = id;
        this.name = name;
        needs = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Demand> getNeeds() {
        return needs;
    }

    public void setNeeds(Demand d) {
        this.needs.add(d);
    }
    
    
}
