package ch.hearc.ig.tb.odoosim.business;

import ch.hearc.ig.tb.odoosim.saasinterfacing.Odoo;
import java.io.Serializable;
import java.util.*;
import static java.util.Arrays.asList;

public class Company {

    private int id;
    private String code;
    private String name;
    private String erp;
    private int uidapiaccess;
    private String passapiaccess;
    private Integer idStock;
    private Integer idAccountSaleProduct;
    private Integer idJournalSaleProduct;
    private Integer idAccountDebitors;
    private Collection<Collaborator> collaborators;
    private Collection<Offer> offers;
    private Collection<Exchange> exchanges;
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

    public Integer getIdAccountSaleProduct() {
        return idAccountSaleProduct;
    }

    public void setIdAccountSaleProduct(Integer idAccountSaleProduct) {
        this.idAccountSaleProduct = idAccountSaleProduct;
    }

    public Integer getIdJournalSaleProduct() {
        return idJournalSaleProduct;
    }

    public Integer getIdAccountDebitors() {
        return idAccountDebitors;
    }

    public void setIdAccountDebitors(Integer idAccountDebitors) {
        this.idAccountDebitors = idAccountDebitors;
    }

    public void setIdJournalSaleProduct(Integer idJournalSaleProduct) {
        this.idJournalSaleProduct = idJournalSaleProduct;
    }

    public <T> void stockEntry(Odoo wsapi, String date, Double quantity, List<T> products) {
        List<Product> allProducts = (List<Product>) products;
        for (Product product : allProducts) {
            HashMap data = new HashMap();
            data.put("in_date", date);
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

    public void updateDateStock(Odoo wsapi, Exchange ex) throws Exception {
        int picking = wsapi.getID(erp, "stock.picking", uidapiaccess, passapiaccess, asList(asList("origin", "=", ex.getName())));
        wsapi.update(erp, "stock.picking", uidapiaccess, passapiaccess, picking, new HashMap() {
            {
                put("date", ex.getDate());
            }
        });
        wsapi.changeState(erp, uidapiaccess, passapiaccess, "stock.picking", "do_new_transfer", picking);
        int immedia = wsapi.getID(erp, "stock.immediate.transfer", uidapiaccess, passapiaccess, asList(asList("pick_id", "=", picking)));
        wsapi.changeState(erp, uidapiaccess, passapiaccess, "stock.immediate.transfer", "process", immedia);
        //  get stock.pack.operation and update date (optimiser en récupérant uniquement le champ id
        Object tuple = wsapi.getTuple(erp, uidapiaccess, passapiaccess, "stock.pack.operation", asList(asList("picking_id", "=", picking)),
                new HashMap() {
            {
                put("fields", Collections.emptyList());
            }
        });
        HashMap hm = (HashMap) tuple;
        wsapi.update(erp, "stock.pack.operation", uidapiaccess, passapiaccess, (int) hm.get("id"),
                new HashMap() {
            {
                put("date", ex.getDate());
            }
        });
        //  get stock.move and update date
        Object move = wsapi.getTuple(erp, uidapiaccess, passapiaccess, "stock.move", asList(asList("picking_id", "=", picking)),
                new HashMap() {
            {
                put("fields", Collections.emptyList());
            }
        });
        hm = (HashMap) move;
        wsapi.update(erp, "stock.move", uidapiaccess, passapiaccess, (int) hm.get("id"),
                new HashMap() {
            {
                put("date", ex.getDate());
            }
        });
    }

    public void registerSale(Odoo wsapi, Exchange ex) throws Exception {
        HashMap d = new HashMap<String, Object>();
        d.put("date_order", ex.getDate());
        d.put("state", "sale");
        d.put("partner_id", ex.getBuyer().getId());
        d.put("order_line", asList(asList(0, false, new HashMap<String, Object>() {
            {
                put("product_id", ex.getProduct().getId());
                put("product_uom_qty", ex.getQuantity());
                put("price_unit", ex.getPrice());
            }
        })));
        ex.setId(wsapi.insert(erp, "sale.order", d, uidapiaccess, passapiaccess));
        HashMap name = (HashMap) wsapi.getTuple(erp, uidapiaccess, passapiaccess, "sale.order", asList(asList("id", "=", ex.getId())),
                new HashMap() {
            {
                put("fields", asList("name"));
            }
        });
        ex.setName((String) name.get("name"));
    }

    public void registerInvoice(Odoo wsapi, Exchange ex) throws Exception {
        HashMap data = new HashMap();
        data.put("state", "open");
        data.put("partner_id", ex.getBuyer().getId());
        data.put("origin", ex.getName());
        data.put("account_id", idAccountDebitors);
        data.put("invoice_line_ids", asList(asList(0, false, new HashMap() {
            {
                put("product_id", ex.getProduct().getId());
                put("name", "Odoosim");
                put("quantity", ex.getQuantity());
                put("price_unit", ex.getPrice());
                put("account_id", idAccountSaleProduct);
            }
        })));
        ex.setIdInvoice(wsapi.insert(erp, "account.invoice", data, uidapiaccess, passapiaccess));
    }

    public void registerPayment(Odoo wsapi, Exchange ex) throws Exception {
        HashMap payment = new HashMap();
        payment.put("payment_type", "inbound");
        payment.put("journal_id", idJournalSaleProduct);
        payment.put("amount", ex.getPrice());
        payment.put("partner_type", "customer");
        payment.put("partner_id", ex.getBuyer().getId());
        payment.put("payment_date", ex.getDate());
        payment.put("invoice_ids", asList(asList(4, ex.getIdInvoice(), false)));
        payment.put("payment_method_id", 1);

        ex.setIdPayment(wsapi.insert(erp, "account.payment", payment, uidapiaccess, passapiaccess));
    }

    public void processSale(Odoo wsapi, Exchange ex) throws Exception {
        registerSale(wsapi, ex);
        updateDateStock(wsapi, ex);
        registerInvoice(wsapi, ex);
        registerPayment(wsapi, ex);
        wsapi.changeState(erp, uidapiaccess, passapiaccess, "account.payment", "post", ex.getIdPayment());
        wsapi.update(erp, "account.invoice", uidapiaccess, passapiaccess, ex.getIdInvoice(), new HashMap() {
            {
                put("state", "paid");
            }
        });
        wsapi.changeState(erp, uidapiaccess, passapiaccess, "sale.order", "action_done", ex.getId());
    }

}
