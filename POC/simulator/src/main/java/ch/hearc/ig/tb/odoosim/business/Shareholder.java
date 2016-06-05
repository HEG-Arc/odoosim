/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hearc.ig.tb.odoosim.business;

import java.util.Objects;

/**
 *
 * @author tomant
 */
public class Shareholder {
    private int id;
    private String name;

    public Shareholder(String name) {
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
    
    @Override
    public int hashCode() {
        return Objects.hash(this.name);
    }
    
    @Override
    public boolean equals(Object sh) {
        if(this == sh)
            return true;
        if(this.getClass() != sh.getClass())
            return false;
        
        Shareholder shh = (Shareholder) sh;
        return Objects.equals(this.id, shh.id) && Objects.equals(this.name, shh.name);
    }
}
