/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hearc.ig.tb.odoosim.business;

import java.util.Random;

/**
 *
 * @author tomant
 */
public abstract class Customer {
    
    protected int identifier;
    protected int planNeeds;
    
    public Customer() {
        this.identifier = 0;
        this.planNeeds = 0;
    }
    
    protected abstract void generatePlanNeeds();
    
}
