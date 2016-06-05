package ch.hearc.ig.tb.odoosim.business;

public abstract class Product {
    
    private int id;
    private String code;
    private String name;
    private Double purchasePrice;

    public Product (String name) {
        this.name = name;
    }
    public Product(String code, String name, Double purchasePrice) {
        this.code = code;
        this.name = name;
        this.purchasePrice = purchasePrice;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(Double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }
    
}
