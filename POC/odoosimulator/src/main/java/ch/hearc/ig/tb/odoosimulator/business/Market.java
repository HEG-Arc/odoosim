package ch.hearc.ig.tb.odoosimulator.business;

import ch.hearc.ig.tb.odoosimulator.simulator.Simulator;
import java.util.ArrayList;
import java.util.List;

public class Market {
    private Simulator odoosim;
    private List<Company> companies;
    private List<Offer> offers;
    private List<Demand> demands;
    private Product product;
    private List<Customer> customers;

    public Market(Simulator odoosim) {
        this.odoosim = odoosim;
        companies = new ArrayList<>();
        offers = new ArrayList<>();
        demands = new ArrayList<>();
        customers = new ArrayList<>();
    }

    public List<Company> getCompanies() {
        return companies;
    }

    public void setCompanies(Company c) {
        this.companies.add(c);
    }

    public List<Offer> getOffers() {
        return offers;
    }

    public void setOffers(Offer o) {
        this.offers.add(o);
    }

    public List<Demand> getDemands() {
        return demands;
    }

    public void setDemands(Demand d) {
        this.demands.add(d);
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product p) {
        this.product = p;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(Customer c) {
        this.customers.add(c);
    }

}
