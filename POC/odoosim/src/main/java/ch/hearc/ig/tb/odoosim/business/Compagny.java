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
public class Compagny extends Customer {
    
        public Compagny() {
        super();
        generatePlanNeeds();
    }
    
    @Override
    protected void generatePlanNeeds() {
        Random r = new Random();
        int lowBound = 20;
        int highBound = 100;
        planNeeds = r.nextInt(highBound-lowBound) + lowBound;
    }  
}
