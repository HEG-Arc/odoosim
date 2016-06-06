package ch.hearc.ig.tb.odoosim.business;

import java.util.ArrayList;
import java.util.Collection;

public class Area {
    private int id;
    private String name;
    private Double marketPart;
    private Double elasticity;
    private Double partByConsumers;
    private Collection<Consumer> consumers;
    
    public Area(String name, Double marketPart, Double elasticity) {
        this.name = name;
        this.marketPart = marketPart;
        this.elasticity = elasticity;
        consumers = new ArrayList<>();
    }

    public Double getMarketPart() {
        return marketPart;
    }

    public void setMarketPart(Double marketPart) {
        this.marketPart = marketPart;
    }

    public Collection<Consumer> getConsumers() {
        return consumers;
    }

    public void setConsumers(Collection<Consumer> consumers) {
        this.consumers = consumers;
    }    

    public Area(String name) {
        this.name = name;
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

    public Double getElasticity() {
        return elasticity;
    }

    public void setElasticity(Double elasticity) {
        this.elasticity = elasticity;
    }

    public Double getPartByConsumers() {
        return partByConsumers;
    }

    public void setPartByConsumers(Double partByConsumers) {
        this.partByConsumers = partByConsumers;
    }
    
    
    
    public void addConsumer(Consumer c) {
        consumers.add(c);
    }
}
