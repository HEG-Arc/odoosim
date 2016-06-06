package ch.hearc.ig.tb.odoosim.business;

import java.util.*;

public class Consumer {

    private int id;
    private String code;
    private Collection<Demand> demands;

    public Consumer(String code) {
        this.code = code;
        demands = new ArrayList<>();
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    
    public void addDemand(Demand d) {
        this.demands.add(d);
    }

}
