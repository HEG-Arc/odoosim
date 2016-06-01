package ch.hearc.ig.tb.odoosimulator.business;

public class Demand {
    
    private Customer customer;
    private int quantity;
    private Product product;
    private Boolean satisfied;
    private int when;

    public Demand() {
    }

    public Demand(Customer customer, int quantity, Product product, Boolean satisfied, int when) {
        this.customer = customer;
        this.quantity = quantity;
        this.product = product;
        this.satisfied = satisfied;
        this.when = when;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Boolean getSatisfied() {
        return satisfied;
    }

    public void setSatisfied(Boolean satisfied) {
        this.satisfied = satisfied;
    }

    public int getWhen() {
        return when;
    }

    public void setWhen(int when) {
        this.when = when;
    }
    
    
}
