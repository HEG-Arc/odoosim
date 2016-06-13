package ch.hearc.ig.tb.odoosim.business;

import ch.hearc.ig.tb.odoosim.saasinterfacing.Odoo;
import java.util.*;
import static java.util.Arrays.asList;

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
    private Integer idStock;

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

    public Integer getIdStock() {
        return idStock;
    }

    public void setIdStock(Integer idStock) {
        this.idStock = idStock;
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
    
    public <T>void stockEntry(Odoo wsapi, Integer day, Integer month, Double quantity, List<T> products) {
        List<Product> allProducts = (List<Product>) products;
        for(Product product : allProducts) {
            HashMap data = new HashMap();
                    data.put("in_date", "2016-" + month + "-" + day + " 00:00:00");
                    data.put("product_id", product.getId());
                    data.put("qty", quantity);
                    data.put("name", "Inventaire automatique pour " + product.getName());
                    data.put("location_id", idStock);
                    try {
                        wsapi.insert(erp, "stock.quant", data, uidapiaccess, passapiaccess);
                    } catch (Exception e) {
                        System.out.println("Une erreur est survenue dans l'entrée de stock automatique");
                    }
        }
    }
    
    public void updateDateStock(Odoo wsapi, String sourceDocument, Integer day, Integer month, Integer year) throws Exception {
        //  Terminer la sortie de stock
        HashMap hm = new HashMap();
        int picking = wsapi.getID(erp, "stock.picking", uidapiaccess, passapiaccess, asList(asList("origin", "=", sourceDocument)));
        wsapi.update(erp, "stock.picking", uidapiaccess, passapiaccess, picking, new HashMap(){{ put("date", "2016-" + month + "-" + day + " 00:00:00");}});
        wsapi.changeState(erp, uidapiaccess, passapiaccess, "stock.picking", "do_new_transfer", picking);
        int immedia = wsapi.getID(erp, "stock.immediate.transfer", uidapiaccess, passapiaccess, asList(asList("pick_id", "=", picking)));
        wsapi.changeState(erp, uidapiaccess, passapiaccess, "stock.immediate.transfer", "process", immedia);
        //  get stock.pack.operation and update date (optimiser en récupérant uniquement le champ id
        Object tuple = wsapi.getTuple(erp, uidapiaccess, passapiaccess, "stock.pack.operation", asList(asList("picking_id", "=", picking)), 
                new HashMap() {{ put("fields", Collections.emptyList());}});
        hm = (HashMap) tuple;
        wsapi.update(erp, "stock.pack.operation", uidapiaccess, passapiaccess, (int) hm.get("id"), 
                new HashMap(){{ put("date", "2016-" + month + "-" + day + " 00:00:00");}});
        //  get stock.move and update date
        Object move = wsapi.getTuple(erp, uidapiaccess, passapiaccess, "stock.move", asList(asList("picking_id", "=", picking)), 
                new HashMap() {{ put("fields", Collections.emptyList());}});
        hm = (HashMap) move;
           wsapi.update(erp, "stock.move", uidapiaccess, passapiaccess, (int) hm.get("id"), 
                new HashMap(){{ put("date", "2016-" + month + "-" + day + " 00:00:00");}});
        
        
        
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
        updateDateStock(wsapi, (String) name.get("name"), day, month, 2016);
        //registerDelivery(wsapi, (String) name.get("name"));
        //registerInvoice(wsapi, (String) name.get("name"));
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
