package ch.hearc.ig.tb.odoosim.business;

import java.util.ArrayList;
import java.util.List;

public class Area {
    private int id;
    private String name;
    private Double marketPart;
    private List<Retailer> consumers;
    
    public Area(String name) {
        this.name = name;
    }
    
    public Area(String name, Double marketPart) {
        this.name = name;
        this.marketPart = marketPart;
        consumers = new ArrayList<>();
    }

    public Double getMarketPart() {
        return marketPart;
    }

    public void setMarketPart(Double marketPart) {
        this.marketPart = marketPart;
    }

    public List<Retailer> getConsumers() {
        return consumers;
    }

    public void setConsumers(List<Retailer> consumers) {
        this.consumers = consumers;
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

    public void addConsumer(Retailer c) {
        try {
            c.setLocalisation(this);
            consumers.add(c);
        } catch (Exception e) {
            
        }
    }
}
