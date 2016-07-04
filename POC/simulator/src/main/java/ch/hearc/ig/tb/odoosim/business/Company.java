package ch.hearc.ig.tb.odoosim.business;

import ch.hearc.ig.tb.odoosim.saasinterfacing.Odoo;
import java.io.Serializable;
import java.util.*;
import static java.util.Arrays.asList;

public class Company  implements Comparable<Company> {

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
    private Integer idBankAccount;
    private Collection<Collaborator> collaborators;
    private Collection<Offer> offers;
    private Collection<Exchange> exchanges;
    private Banker banker;
    private Shareholder shareholder;
    private Double ca;

    public Company(String name) {
        this.name = name;
        this.collaborators = new ArrayList<>();
        this.offers = new ArrayList<>();
        this.exchanges = new ArrayList<>();
        this.ca = 0.0;
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

    public Integer getIdBankAccount() {
        return idBankAccount;
    }

    public void setIdBankAccount(Integer idBankAccount) {
        this.idBankAccount = idBankAccount;
    }

    public Double getCa() {
        return ca;
    }

    public void setCa(Double ca) {
        this.ca = ca;
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
            data.put("in_date", date + " 00:00:00");
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
                put("date", ex.getDate() + " 00:00:00");
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
                put("date", ex.getDate() + " 00:00:00");
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
                put("date", ex.getDate() + " 00:00:00");
            }
        });
    }

    public void registerSale(Odoo wsapi, Exchange ex) throws Exception {
        HashMap d = new HashMap<String, Object>();
        d.put("date_order", ex.getDate() + " 00:00:00");
        d.put("state", "sale");
        d.put("partner_id", ex.getBuyer().getOwner().getId());
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
        data.put("partner_id", ex.getBuyer().getOwner().getId());
        data.put("origin", ex.getName());
        data.put("account_id", idAccountDebitors);
        data.put("date", ex.getDate());
        data.put("date_invoice", ex.getDate());
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
        payment.put("partner_id", ex.getBuyer().getOwner().getId());
        payment.put("payment_date", ex.getDate() + " 00:00:00");
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
    
    public void processQuickSale(Odoo wsapi, Exchange ex) throws Exception {
        HashMap data = new HashMap<>();
        data.put("date_order", ex.getDate() + " 00:00:00");
        data.put("state", "sale");
        data.put("partner_id", ex.getBuyer().getOwner().getId());
        data.put("order_line", asList(asList(0, false, new HashMap<String, Object>() {
            {
                put("product_id", ex.getProduct().getId());
                put("product_uom_qty", ex.getQuantity());
                put("price_unit", ex.getPrice());
            }
        })));
        ex.setId(wsapi.insert(erp, "sale.order", data, uidapiaccess, passapiaccess));
        
        data.clear();
        data = (HashMap) wsapi.getTuple(erp, uidapiaccess, passapiaccess, "sale.order", asList(asList("id", "=", ex.getId())),
                new HashMap() {
            {
                put("fields", asList("name","amount_total"));
            }
        });
        ex.setName((String) data.get("name"));
        Double amount = (Double) data.get("amount_total");
        
        data.clear();
        data.put("product_id", ex.getProduct().getId());
        data.put("product_tmpl_id", ex.getProduct().getId());
        data.put("new_quantity", ex.getVendor().getQuantity());
        data.put("location_id", idStock);
        data.put("product_variant_count", 1);
        wsapi.insert(erp, "stock.change.product.qty", data, uidapiaccess, passapiaccess);
      
        data.clear();
        //  Fonctionne bien! Essayer de reproduire les deux entreés dans le journal avec le compte 1100 débiteur comme sur les feuilles papier
        //  Ne pas oublier de post le paiement ! 
        data.put("journal_id", idJournalSaleProduct);
        data.put("date", ex.getDate());
        data.put("line_ids", asList(asList(0, false, new HashMap<String, Object>() {
            {
                put("account_id", idAccountSaleProduct);
                put("debit", 0.0);
                put("credit", amount);
                put("quantity", ex.getQuantity());
                put("date_maturity", ex.getDate());
                put("move_id", 1);
                put("name", "Auto-Sales Odoosim");
                put("partner_id", ex.getBuyer().getOwner().getId());
            }
        }), asList(0, false, new HashMap<String, Object>() {
            {
                put("account_id", idBankAccount);
                put("debit", amount);
                put("credit", 0.0);
                put("quantity", ex.getQuantity());
                put("date_maturity", ex.getDate());
                put("move_id", 1);
                put("name", "Auto-Sales Odoosim");
                put("partner_id", ex.getBuyer().getOwner().getId());
            }
        })));
        //  Récupéré l'id puis faire l'action POST! poru que la transaction soit comptabilisée!
        wsapi.insert(erp, "account.move", data, uidapiaccess, passapiaccess);
        wsapi.changeState(erp, uidapiaccess, passapiaccess, "sale.order", "action_done", ex.getId());
        //  La partie ci-dessus la faire à ce moment là et avant faire la transaction avec le compte 3200 vente marchandise avec 1100 débiteur
    }
    
    public void processQuickSale2(Odoo wsapi, Exchange ex) throws Exception {
        //long start = System.currentTimeMillis();
        //  Création de la vente
        HashMap d = new HashMap<>();
        d.put("state", "sale");
        d.put("date_order", ex.getDate() + " 00:00:00");
        d.put("partner_id", ex.getBuyer().getOwner().getId());
        d.put("picking_policy","direct");
        d.put("order_line", asList(asList(0, false, new HashMap<String, Object>() {
            {
                put("product_id", ex.getProduct().getId());
                put("product_uom_qty", ex.getQuantity());
                put("price_unit", ex.getPrice());
                put("tax_id", asList(asList(6, false, asList(14))));
            }
        })));
        int idSale = wsapi.insert(erp,"sale.order",d,uidapiaccess,passapiaccess);
        //pas besoin wsapi.changeState(erp, uidapiaccess, passapiaccess, "sale.order", "action_confirm", idSale);
        HashMap changeState = wsapi.changeState(erp, uidapiaccess, passapiaccess, "sale.order", "action_view_delivery", idSale);
        wsapi.changeState(erp, uidapiaccess, passapiaccess, "stock.picking", "do_new_transfer", (int) changeState.get("res_id"));
        int id = wsapi.getID(erp, "stock.immediate.transfer", uidapiaccess, passapiaccess, asList(asList("pick_id", "=", changeState.get("res_id"))));
        wsapi.changeState(erp, uidapiaccess, passapiaccess, "stock.immediate.transfer", "process", id);
        d.clear();
        d.put("advance_payment_method", "all");
        d.put("amount", 0);
        d.put("deposit_account_id", false);
        d.put("product_id", false);
        int insert = wsapi.insert(erp,"sale.advance.payment.inv",d,uidapiaccess,passapiaccess);
        d.clear();
        d.put("active_id", idSale);
        d.put("active_ids", asList(idSale));
        d.put("active_model", "sale.order");
        d.put("search_disable_custom_filters", true);
        wsapi.update2(erp, uidapiaccess, passapiaccess, "sale.advance.payment.inv", "create_invoices", insert, d);
        HashMap invoices = wsapi.changeState(erp, uidapiaccess, passapiaccess, "sale.order", "action_view_invoice", idSale);
        int idInvoice = (int) invoices.get("res_id");
        //wsapi.getTuple(erp, uidapiaccess, passapiaccess, "account.invoice", asList(asList("id", "=", ex.getId())), d)
        wsapi.workflowProgress(erp, uidapiaccess, passapiaccess, "account.invoice", "invoice_open", idInvoice);
          //System.out.println("Avant le paiement");
          d.clear();
          d.put("amount", (ex.getQuantity()*ex.getPrice())); //faire qty * prix !
          d.put("communication", "Effectué par le simulateur ODOOSIM");
          d.put("journal_id", idJournalSaleProduct); // Bank
          d.put("partner_id", ex.getBuyer().getOwner().getId()); //celui du client !
          d.put("partner_type","customer");
          d.put("payment_date", ex.getDate() + " 00:00:00");
          d.put("payment_difference_handling","open");
          d.put("payment_method_id",1);
          d.put("payment_type","inbound");
          d.put("writeoff_account_id",false);
          d.put("invoice_ids", asList(asList(4, idInvoice, false)));
          int idPayment = wsapi.insert(erp,"account.payment",d,uidapiaccess,passapiaccess);
          d.clear();
          d.put("active_id", idInvoice);
          d.put("active_ids", asList(idInvoice));
            d.put("active_model", "account.invoice");
          wsapi.changeState(erp, uidapiaccess, passapiaccess, "account.payment", "post", idPayment);
          wsapi.changeState(erp, uidapiaccess, passapiaccess, "sale.order", "action_done", idSale);
          this.ca += ex.getQuantity() * ex.getPrice();
          //long end = System.currentTimeMillis();
          System.out.println("Vente entre " + ex.getVendor().getOwner().getName() + " et " + ex.getBuyer().getOwner().getName()
                + " pour " + ex.getQuantity() + " unité(s) de " + ex.getProduct().getName() + " à " + ex.getPrice() + " CHF");
          //System.out.println("TEMPS PROCESS SALE : " + (end-start));
    }

    @Override
    public int compareTo(Company o) {
        if(this.ca < o.ca)
            return 1;
        else if (this.ca == o.ca)
            return 0;
        else
            return -1;
    }

}
