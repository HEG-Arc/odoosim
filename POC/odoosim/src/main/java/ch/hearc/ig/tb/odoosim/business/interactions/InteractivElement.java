/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hearc.ig.tb.odoosim.business.interactions;

/**
 *
 * @author tomant
 * @param <T>
 */
public abstract class InteractivElement<T> {
    
    protected iCrud managment;
    protected T identifier;
    
    public InteractivElement() {}
    
    public InteractivElement(iCrud type, T id) { 
        managment = type;
        identifier = id;
    }

    public iCrud getManagment() {
        return managment;
    }

    public void setManagment(iCrud managment) {
        this.managment = managment;
    }

    public T getIdentifier() {
        return identifier;
    }

    public void setIdentifier(T identifier) {
        this.identifier = identifier;
    }
        
    public void creating() {
        managment.create();
    }
    
    public void reading() {
        managment.read();
    }
    
    public void updating() {
        managment.update();
    }
    
    public void deleting() {
        managment.delete();
    }
}
