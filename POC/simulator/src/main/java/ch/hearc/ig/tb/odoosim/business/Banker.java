/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hearc.ig.tb.odoosim.business;

import java.util.Objects;

public class Banker {
    
    private int id;
    private String name;

    public Banker(String name) {
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
    public boolean equals(Object banker) {
        if(this == banker)
            return true;
        if(this.getClass() != banker.getClass())
            return false;
        
        Banker b = (Banker) banker;
        return Objects.equals(this.id, b.id) && Objects.equals(this.name, b.name);
    }
}
