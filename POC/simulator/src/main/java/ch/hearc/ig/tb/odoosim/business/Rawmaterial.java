package ch.hearc.ig.tb.odoosim.business;

import java.util.Objects;

public class Rawmaterial extends Product {
    
    private Supplier supplier;

    public Rawmaterial(String name) {
        super(name);
    }
    public Rawmaterial(String code, String name, Double purchasePrice) {
        super(code, name, purchasePrice);
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.getName());
    }
    
    @Override
    public boolean equals(Object rawmaterial) {
        if(this == rawmaterial)
            return true;
        if(this.getClass() != rawmaterial.getClass())
            return false;
        
        Rawmaterial rwm = (Rawmaterial) rawmaterial;
        return Objects.equals(this.getName(), rwm.getName());
    }
}
