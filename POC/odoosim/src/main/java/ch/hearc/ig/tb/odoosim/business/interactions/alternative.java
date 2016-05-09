/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hearc.ig.tb.odoosim.business.interactions;

import ch.hearc.ig.tb.odoosim.business.interactions.iCrud;

/**
 *
 * @author tomant
 */
public class alternative implements iCrud {

    @Override
    public void create() {
        System.out.println("Je me crée en tant qu'alternative!");
    }

    @Override
    public void read() {
        System.out.println("Je me lis en tant qu'alternative!");
    }

    @Override
    public void update() {
        System.out.println("Je me change en tant qu'alternative!");
    }

    @Override
    public void delete() {
        System.out.println("Je me supprime en tant qu'alternative!");
    }
    
}
