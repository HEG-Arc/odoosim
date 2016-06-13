package ch.hearc.ig.tb.odoosim.business;

public class Exchange {

    private int id;
    private Double quantity;
    private Double price;
    private Company vendor;
    private Retailer buyer;
    private Good product;
    private String name;
    private String date;
    private Integer idInvoice;
    private Integer idPayment;

    public Exchange() {
    }
    
    public Exchange(Double quantity,Double price, Retailer buyer, Company vendor, Good product, String date) {
        this.quantity = quantity;
        this.price = price;
        this.vendor = vendor;
        this.buyer = buyer;
        this.product = product;
        this.date = date;
    }
    
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Double getQuantity() {
        return this.quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getIdInvoice() {
        return idInvoice;
    }

    public void setIdInvoice(Integer idInvoice) {
        this.idInvoice = idInvoice;
    }

    public Integer getIdPayment() {
        return idPayment;
    }

    public void setIdPayment(Integer idPayment) {
        this.idPayment = idPayment;
    }
    
}
