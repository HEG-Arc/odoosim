package ch.hearc.ig.tb.odoosim.business;

import java.util.*;

public class Retailer {

    protected int id;
    protected String name;
    protected Collection<Demand> demands;
    protected Collection<Good> productsPreferences;
    protected Collection<Exchange> exchanges;
    protected Double marketPart;
    protected Double elasticity;
    protected int dayToPay;
    protected Area localisation;
    protected String type;

    public Retailer(String name, Double marketPart, Double elasticity, int toPay, int untilPay, String type) {
        this.name = name;
        this.marketPart = marketPart;
        this.elasticity = elasticity;
        this.type = type;
        dayToPay = whenPaid(toPay, untilPay);
        demands = new ArrayList<>();
        productsPreferences = new ArrayList<>();
        exchanges = new ArrayList<>();
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addDemand(Demand d) {
        d.setOwner(this);
        this.demands.add(d);
    }

    private int whenPaid(int start, int stop) {
        return 0;
    }

    public Collection<Demand> getDemands() {
        return demands;
    }

    public void setDemands(Collection<Demand> demands) {
        this.demands = demands;
    }

    public Collection<Good> getProductsPreferences() {
        return productsPreferences;
    }

    public void setProductsPreferences(Collection<Good> productsPreferences) {
        this.productsPreferences = productsPreferences;
    }

    public Double getMarketPart() {
        return marketPart;
    }

    public void setMarketPart(Double marketPart) {
        this.marketPart = marketPart;
    }

    public Double getElasticity() {
        return elasticity;
    }

    public void setElasticity(Double elasticity) {
        this.elasticity = elasticity;
    }

    public int getDayToPay() {
        return dayToPay;
    }

    public void setDayToPay(int dayToPay) {
        this.dayToPay = dayToPay;
    }

    public Area getLocalisation() {
        return localisation;
    }

    public void setLocalisation(Area localisation) {
        this.localisation = localisation;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void addPreference(Good p) {
        this.productsPreferences.add(p);
    }

    public void addExchange(Exchange ex) {
        Company c = ex.getVendor();
        c.addExchange(ex);
        exchanges.add(ex);
    }

    public Double getAdjustedQuantity(Demand d, Offer o) {

        if (elasticity > 0) {
            elasticity = elasticity * -1;
        }

        Double optimum = d.getProduct().getCustomerBestPrice();
        Double offerPrice = o.getPrice();

        Double variationPrice = (offerPrice - optimum) / optimum;

        Double variationQuantity = variationPrice * elasticity;

        Double resultat = (variationQuantity / 100) * d.getQuantity() + d.getQuantity();

        return (double) Math.round(resultat);
    }

}
