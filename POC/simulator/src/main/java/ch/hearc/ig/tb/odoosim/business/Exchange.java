package ch.hearc.ig.tb.odoosim.business;

public class Exchange {

    private int id;
    private int day;
    private Double quantity;
    private Company vendor;
    private Retailer buyer;
    private Good product;

    public Exchange(int day, Double quantity, Retailer buyer, Company vendor, Good product) {
        this.day = day;
        this.quantity = quantity;
        this.vendor = vendor;
        this.buyer = buyer;
        this.product = product;
    }
    
    public Exchange() {
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDay() {
        return this.day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public Double getQuantity() {
        return this.quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Company getVendor() {
        return vendor;
    }

    public void setVendor(Company vendor) {
        this.vendor = vendor;
    }

    public Retailer getBuyer() {
        return buyer;
    }

    public void setBuyer(Retailer buyer) {
        this.buyer = buyer;
    }

    public Good getProduct() {
        return product;
    }

    public void setProduct(Good product) {
        this.product = product;
    }

}
