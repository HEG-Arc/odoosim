/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hearc.ig.tb.odoosim.business.economics;

import ch.hearc.ig.tb.odoosim.business.interactions.InteractivElement;
import ch.hearc.ig.tb.odoosim.business.interactions.alternative;
import ch.hearc.ig.tb.odoosim.business.interactions.iCrud;
import java.util.Date;

/**
 *
 * @author tomant
 */
public class Invoice<T> extends InteractivElement {
    private Date dateCreated;
    private Date dateToPay;
    
    public Invoice(){} 
    public Invoice(iCrud type, T id) {
        super(type, id);
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateToPay() {
        return dateToPay;
    }

    public void setDateToPay(Date dateToPay) {
        this.dateToPay = dateToPay;
    }
}
