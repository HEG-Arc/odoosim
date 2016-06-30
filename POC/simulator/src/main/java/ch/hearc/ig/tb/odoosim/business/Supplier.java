package ch.hearc.ig.tb.odoosim.business;

import java.util.Objects;

public class Supplier {
    
    private int id;
    private String name;
    private Integer leadTime;

    public Supplier(String name) {
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

    public Integer getLeadTime() {
        return leadTime;
    }

    public void setLeadTime(Integer leadTime) {
        this.leadTime = leadTime;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.name);
    }
    
    @Override
    public boolean equals(Object supplier) {
        if(this == supplier)
            return true;
        if(this.getClass() != supplier.getClass())
            return false;
        
        Supplier s = (Supplier) supplier;
        return Objects.equals(this.id, s.id) && Objects.equals(this.name, s.name);
    }
}
