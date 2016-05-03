package ch.hearc.ig.tb.odoosim.business;

import java.util.ArrayList;

public abstract class Good {
    protected int identifier;
    protected String name;
    private ArrayList<Demand> demands;
    private ArrayList<Offer> offers;

    public Good(int identifier, String name) {
        this.identifier = identifier;
        this.name = name;
        this.demands = new ArrayList();
        this.offers = new ArrayList();
    }

    public ArrayList<Offer> getOffers() {
        return offers;
    }

    public void setOffers(ArrayList<Offer> offers) {
        this.offers = offers;
    }

    public ArrayList<Demand> getDemands() {
        return demands;
    }

    public void setDemands(ArrayList<Demand> demands) {
        this.demands = demands;
    }
        
    public void addDemand(Demand d) {
        this.demands.add(d);
    }
    
    public void addOffer(Offer o) {
        this.offers.add(o);
    }
    
}