package ch.hearc.ig.tb.odoosim.business;

import ch.hearc.ig.tb.odoosim.saasinterfacing.Odoo;
import java.util.*;
import static java.util.Arrays.asList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Company {

    private Collection<Offer> offers;
    private Collection<Exchange> exchanges;
    private int id;
    private String code;
    private String name;
    private String erp;
    private int uidapiaccess;
    private String passapiaccess;
    private Collection<Collaborator> collaborators;
    private Banker banker;
    private Shareholder shareholder;

    public Company(String name) {
        this.name = name;
        this.collaborators = new ArrayList<>();
        this.offers = new ArrayList<>();
        this.exchanges = new ArrayList<>();
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<Offer> getOffers() {
        return offers;
    }

    public void setOffers(Collection<Offer> offers) {
        this.offers = offers;
    }

    public String getErp() {
        return erp;
    }

    public void setErp(String erp) {
        this.erp = erp;
    }

    public int getUidapiaccess() {
        return uidapiaccess;
    }

    public void setUidapiaccess(int uidapiaccess) {
        this.uidapiaccess = uidapiaccess;
    }

    public String getPassapiaccess() {
        return passapiaccess;
    }

    public void setPassapiaccess(String passapiaccess) {
        this.passapiaccess = passapiaccess;
    }

    public Collection<Collaborator> getCollaborators() {
        return collaborators;
    }

    public void setCollaborators(Collection<Collaborator> collaborators) {
        this.collaborators = collaborators;
    }

    public Banker getBanker() {
        return banker;
    }

    public void setBanker(Banker banker) {
        this.banker = banker;
    }

    public Shareholder getShareholder() {
        return shareholder;
    }

    public void setShareholder(Shareholder shareholder) {
        this.shareholder = shareholder;
    }

    public void addCollaborators(Collaborator c) {
        c.setCompany(this);
        this.collaborators.add(c);
    }

    public void addExchange(Exchange e) {
        exchanges.add(e);
    }

    public void addOffer(Offer o) {
        this.offers.add(o);
    }

    public Collection<Exchange> getExchanges() {
        return exchanges;
    }

    public void setExchanges(Collection<Exchange> exchanges) {
        this.exchanges = exchanges;
    }
    
    public void registerSale(Odoo wsapi, Exchange ex, int day, int month, Double accordingPrice) throws Exception{
        
        HashMap d = new HashMap<String, Object>();
        d.put("date_order", "2016-" + month + "-" + day + " 00:00:00");
        d.put("state", "sale");
        d.put("partner_id", 7);
        d.put("order_line", asList(asList(0, false, new HashMap<String, Object>() 
            {{ put("product_id",ex.getProduct().getId()); put("product_uom_qty",ex.getQuantity());
                put("price_unit", accordingPrice);}})));
        int id = wsapi.insert(erp, "sale.order", d, uidapiaccess, passapiaccess);
        HashMap name = (HashMap) wsapi.getTuple(erp, uidapiaccess, passapiaccess, "sale.order", asList(asList("id", "=", id)), 
                new HashMap() {{ put("fields", asList("name")); }});
        registerDelivery(wsapi, (String) name.get("name"));
        registerInvoice(wsapi, (String) name.get("name"));
    }
    
    public void registerDelivery(Odoo wsapi, String sale) {
        //  create_date=2016-06-10 06:29:05
        //  availability=2209.0
        
        //  stock.picking see "move_lines => #1866 java.lang.Object[](length=1)"
        int saleID = wsapi.getID(erp, "sale.order", uidapiaccess, passapiaccess, asList(asList("name", "=", sale)));
        HashMap transfert = (HashMap) wsapi.getTuple(erp, uidapiaccess, passapiaccess, "stock.picking", asList(asList("sale_id", "=", saleID)), 
                    new HashMap() {{ put("fields", Collections.EMPTY_LIST); }});
        System.out.println("Effectuer un bon de livraison");
        try {
            wsapi.update(erp, "", uidapiaccess, passapiaccess, (int) transfert.get("id"), new HashMap() {{ put("state", "done"); }});
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public void registerInvoice(Odoo wsapi, String sale) {
        System.out.println("Enregistrerla facture !");
    }

}
