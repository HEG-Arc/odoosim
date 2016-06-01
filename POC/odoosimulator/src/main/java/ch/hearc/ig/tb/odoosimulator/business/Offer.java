package ch.hearc.ig.tb.odoosimulator.business;

public class Offer implements Comparable<Offer> {
   
    private Company company;
    private Product product;
    private int quantity;
    private double price;
    private Boolean available;

    public Offer() {
    }

    public Offer(Company company, Product product, int quantity, double price, Boolean available) {
        this.company = company;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
        this.available = available;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    @Override
    public int compareTo(Offer o) {
        if(this.price>o.price)
            return 1;
        else if(this.price==o.price) {
            if(this.quantity>o.quantity)
                return 1;
            else if(this.quantity==o.quantity)
                return 0;
            else
                return -1;
        }
        else
            return -1;
    }
    
}
