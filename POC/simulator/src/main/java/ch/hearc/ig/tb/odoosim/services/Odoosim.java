package ch.hearc.ig.tb.odoosim.services;

import ch.hearc.ig.tb.odoosim.business.*;
import ch.hearc.ig.tb.odoosim.interactions.Odoo;
import static ch.hearc.ig.tb.odoosim.utils.Utility.getContentsListOfSelectItem;
import static ch.hearc.ig.tb.odoosim.utils.Utility.getDataXML;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import static ch.hearc.ig.tb.odoosim.utils.Utility.*;
import static java.util.Arrays.asList;
import java.util.HashMap;
import java.util.Iterator;

public class Odoosim {

    private String accountOdoo;
    private String passworOdoo;
    private List<Company> companies;
    private List<Product> products;
    private List<Banker> bankers;
    private List<Supplier> suppliers;
    private List<Shareholder> shareholders;
    private List<Area> areas;
    private final Odoo wsapi;
    private final Document scenario;
    private int volumPerDayPerProduct;
    private int day;
    //  Prévoir un champ supplémentaire qui récupère la date système du serveur Saas Odoo Online

    public Odoosim() throws Exception {
        accountOdoo = "";
        passworOdoo = "";
        companies = new ArrayList<>();
        products = new ArrayList<>();
        bankers = new ArrayList<>();
        suppliers = new ArrayList<>();
        shareholders = new ArrayList<>();
        areas = new ArrayList<>();
        wsapi = new Odoo("https", "odoo.com");
        scenario = getXMLConfiguration();
        day = 1;
        
    }

    public void configurationGame() throws Exception {
        
        writeLn(typeOfMessage.INFO, "Début de la configuration");
        generateMarket();
        
        getOdooAccess();
        
        writeLn(typeOfMessage.INFO, "Lecture des parties prenantes");
        generateStakeholders();
        
        writeLn(typeOfMessage.INFO, "Lecture des sociétés");
        generateCompanies();
        
        writeLn(typeOfMessage.INFO, "Lecture des produits");
        generateProduct();
        
        writeLn(typeOfMessage.INFO, "Création du master data");
        Iterator<Company> iCompany = companies.iterator();
        while(iCompany.hasNext()) {
            Company c = iCompany.next();
            tryConnection(c);
            createMasterDataAccounts(c);
            createMasterDataContacts(c, bankers.iterator());
            createMasterDataContacts(c, suppliers.iterator());
            createMasterDataContacts(c, shareholders.iterator());
            createMasterDataRawMaterials(c);
            createMasterDataProduct(c);
        }
        
        //  Je fais la génération des BOM après avoir passé la première partie
        //  du master data pour pouvoir lier avec l'id généré par Odoo lors de 
        //  l'insertion.
        generateBOM();
        Iterator<Company> iC2 = companies.iterator();
        while(iC2.hasNext()) {
            Company c = iC2.next();
            createMasterDataBOM(c);
            createAccountingStructure(c);
        }
        //createAccountingStructure(companies.get(0));
        writeLn(typeOfMessage.INFO, "Configuration terminée");
    }

    public void processingGame() throws Exception {
        writeLn(typeOfMessage.INFO, "Création de la demande");
        //  Dérouler le scénario
        Iterator iA = getIterator(areas);
        while(iA.hasNext()) {
            Area a = (Area) iA.next();
            generateDemand(day, a);
            writeLn(typeOfMessage.INFO,"Demande générée");
        }
        Iterator iC = getIterator(companies);
        while(iC.hasNext()) {
            //entryStock(day, (Company) iC.next());
        }
        
        
    }

    public void persistDataGame() throws Exception {
        //  Récupération des données générées et persistence (DB ou File)
        throw new Exception("Phase du jeu non-disponible actuellement");
    }

    public void generateProduct() throws Exception {
        List<String> queryResults;
        queryResults = getContentsListOfSelectItem(scenario, "//product_purchasable");
        for (int i = 0; i < queryResults.size(); i++) {
            Rawmaterial r = new Rawmaterial(getContentsListOfSelectItem(scenario, "//product_purchasable[" + (i + 1) + "]/@default_code").get(0),
                    getContentsListOfSelectItem(scenario, "//product_purchasable[" + (i + 1) + "]/name").get(0),
                    Double.parseDouble(getContentsListOfSelectItem(scenario, "//product_purchasable[" + (i + 1) + "]/@standard_price").get(0)));
            Supplier s = new Supplier(getContentsListOfSelectItem(scenario, "//product_purchasable[" + (i + 1) + "]/supplier/name").get(0));
            r.setSupplier((Supplier) searchElement(s, suppliers));
            products.add(r);
        }
        //  Création des produits destinés à la vente
        queryResults = getContentsListOfSelectItem(scenario, "//product_sellable");
        for (int i = 0; i < queryResults.size(); i++) {
            Good g = new Good(Double.parseDouble(getContentsListOfSelectItem(scenario, "//product_sellable[" + (i + 1) + "]/@list_price").get(0)),
                    getContentsListOfSelectItem(scenario, "//product_sellable[" + (i + 1) + "]/@default_code").get(0),
                    getContentsListOfSelectItem(scenario, "//product_sellable[" + (i + 1) + "]/name").get(0),
                    Double.parseDouble(getContentsListOfSelectItem(scenario, "//product_sellable[" + (i + 1) + "]/@standard_price").get(0)));
            products.add(g);
        }
    }

    public void generateCompanies() throws Exception {

        List<String> companies = getContentsListOfSelectItem(scenario, "//company/@name");
        for (int i = 0; i < companies.size(); i++) {

            Company c = new Company(companies.get(i));
            List<String> subQR = getContentsListOfSelectItem(scenario, "//company[@name='" + companies.get(i) + "']/erp/@database ");
            c.setErp(subQR.get(0));
            c.setUidapiaccess(wsapi.getUID(c.getErp(), accountOdoo, passworOdoo));
            
            List<String> collaborators = getContentsListOfSelectItem(scenario, "//company[@name='" + c.getName() + "']/players/player");
            for (int ii = 0; ii < collaborators.size(); ii++) {
                Collaborator collabo = new Collaborator(collaborators.get(ii));
                collabo.setMail(getContentsListOfSelectItem(scenario, "//company[@name='" + c.getName() + "']/players/player[" + (ii + 1) + "]/@login").get(0));
                collabo.setPassword(getContentsListOfSelectItem(scenario, "//company[@name='" + c.getName() + "']/players/player[" + (ii + 1) + "]/@password").get(0));
                c.addCollaborators(collabo);
            }
            
            Banker b = new Banker(getContentsListOfSelectItem(scenario, "//company[@name='" + c.getName() + "']/bank/name").get(0));
            Shareholder sh = new Shareholder(getContentsListOfSelectItem(scenario, "//company[@name='" + c.getName() + "']/shareholder/name").get(0));
            c.setBanker((Banker) searchElement(b, bankers));
            c.setShareholder((Shareholder) searchElement(sh, shareholders));
            this.companies.add(c);
        }
    }
    
    public void generateStakeholders() throws Exception {
        List<String> shareholders = getContentsListOfSelectItem(scenario, "//contact[@type='shareholder']");
        for (int i = 0; i < shareholders.size(); i++) {
            Shareholder shareholder = new Shareholder(shareholders.get(i));
            this.shareholders.add(shareholder);
        }
        
        List<String> bankers = getContentsListOfSelectItem(scenario, "//contact[@type='banker']");
        for (int i = 0; i < bankers.size(); i++) {
            Banker banker = new Banker(bankers.get(i));
            this.bankers.add(banker);
        }
        
        List<String> suppliers = getContentsListOfSelectItem(scenario, "//contact[@type='supplier']");
        for (int i = 0; i < suppliers.size(); i++) {
            Supplier supplier = new Supplier(suppliers.get(i));
            this.suppliers.add(supplier);
        }
    }
    
    public void generateBOM() throws Exception {
        
        Iterator<Product> iP = products.iterator();
        while(iP.hasNext()) {
            Product p = (Product) iP.next();
            if(p instanceof Good) {
                Good pGood = (Good) p;
                List<String> bomXML = getContentsListOfSelectItem(scenario, "//product_sellable[name='" + p.getName() + "']/bom/product");
                for(int ii=0; ii<bomXML.size(); ii++) {
                    String qty = getContentsListOfSelectItem(scenario, "//product_sellable[name='" + p.getName() + "']/bom/product[" + (ii + 1) + "]/@quantity").get(0);
                    String pro = getContentsListOfSelectItem(scenario, "//product_sellable[name='" + p.getName() + "']/bom/product[" + (ii + 1) + "]/name").get(0);
                    Rawmaterial rwm = new Rawmaterial(pro);
                    rwm = (Rawmaterial) searchElement(rwm, products);
                    pGood.addBOM(rwm.getId(), Integer.parseInt(qty));
                }
            }
        }
    }
    
    public void getOdooAccess() throws Exception {
        
        //  Récupération du compte d'accès à Odoo.com
        accountOdoo = getDataXML(scenario, "//account").get(0);
        passworOdoo = getDataXML(scenario, "//password").get(0);
    }
    
    
    public <T>Object searchElement(Object o, List<T> liste) {
        
        int size = liste.size();
        for(int i=0;i<size;i++) {
            if(liste.get(i).equals(o))
                return (Object) liste.get(i);
        }
        return null;
    }
    
    public void createMasterDataAccounts(Company c) throws Exception {
        
        //  Création des comptes participants
        Iterator<Collaborator> iCollaborators = c.getCollaborators().iterator();
        HashMap data = new HashMap();
        
        while(iCollaborators.hasNext()) {
            Collaborator co = iCollaborators.next();
            int exist = wsapi.exist(c.getErp(), "res.users", c.getUidapiaccess(), passworOdoo, asList(asList("login", "=", co.getMail()), asList("name", "=", co.getName())));
            if(exist<0) {
                data.put("name", co.getName());
                data.put("login", co.getMail());
                co.setId(wsapi.addElement(c.getErp(), "res.users", data, c.getUidapiaccess(), passworOdoo));
            } else co.setId(exist);
        }
        
    }
    
    public <T>void createMasterDataContacts(Company c, Iterator<T> iT) throws Exception {
        
        HashMap data = new HashMap();
        while(iT.hasNext()) {
            
            Object o = iT.next();
            if(o instanceof Supplier) {
                Supplier s = (Supplier) o;
                int exist = wsapi.exist(c.getErp(), "res.partner", c.getUidapiaccess(), passworOdoo, asList(asList("customer", "=", false), 
                    asList("supplier", "=", true), asList("name", "=", s.getName()),asList("company_type", "=", "company")));
                if(exist<0) {
                    data.put("name", s.getName());
                    data.put("company_type", "company");
                    data.put("customer", false);
                    data.put("supplier", true);
                    s.setId(wsapi.addElement(c.getErp(), "res.partner", data, c.getUidapiaccess(), passworOdoo));
                } else s.setId(exist);
            } else if (o instanceof Shareholder) {
                Shareholder s = (Shareholder) o;
                int exist = wsapi.exist(c.getErp(), "res.partner", c.getUidapiaccess(), passworOdoo, asList(asList("customer", "=", false), 
                    asList("supplier", "=", false), asList("name", "=", s.getName()),asList("company_type", "=", "company")));
                if(exist<0) {
                    data.put("name", s.getName());
                    data.put("company_type", "company");
                    data.put("customer", false);
                    data.put("supplier", false);
                    s.setId(wsapi.addElement(c.getErp(), "res.partner", data, c.getUidapiaccess(), passworOdoo));
                } else s.setId(exist);
            } else if (o instanceof Banker) {
                Banker b = (Banker) o;
                int exist = wsapi.exist(c.getErp(), "res.partner", c.getUidapiaccess(), passworOdoo, asList(asList("customer", "=", false), 
                    asList("supplier", "=", false), asList("name", "=", b.getName()),asList("company_type", "=", "company")));
                if(exist<0) {
                    data.put("name", b.getName());
                    data.put("company_type", "company");
                    data.put("customer", false);
                    data.put("supplier", false);
                    b.setId(wsapi.addElement(c.getErp(), "res.partner", data, c.getUidapiaccess(), passworOdoo));
                } else b.setId(exist);
            }
        }
    }
    
    public void createMasterDataRawMaterials(Company c) throws Exception {
        HashMap data = new HashMap();
        Iterator<Product> iP = products.iterator();
        while(iP.hasNext()) {
            Product p = iP.next();
            if(p instanceof Rawmaterial) {
                Rawmaterial rm = (Rawmaterial) p;
                int exist = wsapi.exist(c.getErp(), "product.template", c.getUidapiaccess(), passworOdoo, asList(asList("name", "=", rm.getName()), 
                    asList("type", "=", "product")));
                if(exist<0) {
                    data.put("name", rm.getName());
                    data.put("type", "product");
                    data.put("default_code", rm.getCode());
                    data.put("list_price", 00.00);
                    data.put("standard_price", rm.getPurchasePrice());
                    data.put("sale_ok", false);
                    data.put("purchase_ok", true);
                    data.put("seller_ids", asList(asList(0, false, new HashMap<String, Object>() {
                        {
                            put("name", rm.getSupplier().getId());
                            put("min_qty", 1.00);
                            put("price", rm.getPurchasePrice());
                        }
                    })));
                    rm.setId(wsapi.addElement(c.getErp(), "product.template", data, c.getUidapiaccess(), passworOdoo));
                } else rm.setId(exist);
            }
        }
    }
    
    public void createMasterDataProduct(Company c) throws Exception {
        HashMap data = new HashMap();
        Iterator<Product> iP = products.iterator();
        while(iP.hasNext()) {
            Product p = iP.next();
            if(p instanceof Good) {
                Good gd = (Good) p;
                int exist = wsapi.exist(c.getErp(), "product.template", c.getUidapiaccess(), passworOdoo, asList(asList("name", "=", gd.getName()), 
                    asList("type", "=", "product"), asList("default_code", "=", gd.getCode())));
                if(exist<0) {
                    data.put("name", gd.getName());
                    data.put("type", "product");
                    data.put("default_code", gd.getCode());
                    data.put("list_price", gd.getIndicativeSalePrice());
                    data.put("standard_price", gd.getPurchasePrice());
                    data.put("sale_ok", true);
                    data.put("purchase_ok", false);
                    gd.setId(wsapi.addElement(c.getErp(), "product.template", data, c.getUidapiaccess(), passworOdoo));
                } else gd.setId(exist);
            }
        }
    }
    
    public void createMasterDataBOM(Company c) throws Exception {
        
        HashMap data = new HashMap();
        HashMap dataBOM;
        Iterator<Product> iP = products.iterator();
        
        while(iP.hasNext()) {
            Product p = iP.next();
            if(p instanceof Good) {
                int exist = wsapi.exist(c.getErp(), "mrp.bom", c.getUidapiaccess(), passworOdoo, asList(asList("product_tmpl_id", "=", p.getName())));
                if(exist<0) {
                    dataBOM = (HashMap) ((Good) p).getBillOfMaterials();
                    Iterator iBOM = dataBOM.keySet().iterator();
                    List aList = new ArrayList<>();

                    while(iBOM.hasNext()) {
                       int idProduct = (int) iBOM.next();
                       int quantity = (int) dataBOM.get(idProduct);
                       HashMap hhmp = new HashMap<String, Object>() {{put("product_id", idProduct); put("product_qty", quantity);}};
                       aList.add(asList(0, false, hhmp));
                    }
                    data.put("product_tmpl_id", p.getId());
                    data.put("product_qty", 1.00);
                    data.put("type", "normal");
                    data.put("bom_line_ids", aList);
                    wsapi.addElement(c.getErp(), "mrp.bom", data, c.getUidapiaccess(), passworOdoo);
                }
            }
                
        }
    }
    
    public void createAccountingStructure(Company c) throws Exception {
        HashMap data = new HashMap();
        List<String> journals = getContentsListOfSelectItem(scenario, "//accounting/journalentry");
        for (int i = 0; i < journals.size(); i++) {
            String name = getContentsListOfSelectItem(scenario, "//accounting/journalentry[" + (i+1) + "]/name").get(0);
            data.put("journal_id", wsapi.searchID(c.getErp(), c.getUidapiaccess(), passworOdoo, "account.journal",name));
            List<String> entries = getContentsListOfSelectItem(scenario, "//accounting/journalentry[" + (i+1) + "]/items/item");
            List aList = new ArrayList<>();
            int exist = 0;
            for(int ii=0; ii<entries.size(); ii++) {
                String erp = c.getErp();
                int uid = c.getUidapiaccess();
                int acc_id = wsapi.searchAccountByCode(c.getErp(), c.getUidapiaccess(), passworOdoo, "account.account", 
                                getContentsListOfSelectItem(scenario, "//accounting/journalentry[" + (i+1) + "]/items/item[" + (ii+1) + "]/account/number").get(0));
                int par_id = wsapi.searchID(c.getErp(), c.getUidapiaccess(), passworOdoo, "res.partner", 
                        getContentsListOfSelectItem(scenario, "//accounting/journalentry[" + (i+1) + "]/items/item[" + (ii+1) + "]/partner/name").get(0));
                String label = getContentsListOfSelectItem(scenario, "//accounting/journalentry[" + (i+1) + "]/items/item[" + (ii+1) + "]/label").get(0);
                Double debit = Double.parseDouble(getContentsListOfSelectItem(scenario, "//accounting/journalentry[" + (i+1) + "]/items/item[" + (ii+1) + "]/amountdebit").get(0));
                Double credit = Double.parseDouble(getContentsListOfSelectItem(scenario, "//accounting/journalentry[" + (i+1) + "]/items/item[" + (ii+1) + "]/amountcredit").get(0));
                HashMap hsmap = new HashMap<String, Object>();
                hsmap.put("account_id", acc_id);
                hsmap.put("partner_id", par_id);
                hsmap.put("name", label);
                hsmap.put("debit", debit);
                hsmap.put("credit", credit);
                aList.add(asList(0, false, hsmap));
                exist = wsapi.exist(erp, "account.move.line", uid, passworOdoo, 
                    asList(asList("journal_id", "=", data.get("journal_id")), asList("partner_id", "=", par_id), asList("account_id", "=", acc_id),
                        asList("debit", "=", debit), asList("credit", "=", credit)));
                /* BEFORE TEST EXIST 
                HashMap hsmap = new HashMap<String, Object>();
                hsmap.put("account_id", wsapi.searchAccountByCode(c.getErp(), c.getUidapiaccess(), passworOdoo, "account.account", 
                                getContentsListOfSelectItem(scenario, "//accounting/journalentry[" + (i+1) + "]/items/item[" + (ii+1) + "]/account/number").get(0)));
                hsmap.put("partner_id", wsapi.searchID(c.getErp(), c.getUidapiaccess(), passworOdoo, "res.partner", 
                        getContentsListOfSelectItem(scenario, "//accounting/journalentry[" + (i+1) + "]/items/item[" + (ii+1) + "]/partner/name").get(0)));
                hsmap.put("name", getContentsListOfSelectItem(scenario, "//accounting/journalentry[" + (i+1) + "]/items/item[" + (ii+1) + "]/label").get(0));
                hsmap.put("debit", Double.parseDouble(getContentsListOfSelectItem(scenario, "//accounting/journalentry[" + (i+1) + "]/items/item[" + (ii+1) + "]/amountdebit").get(0)));
                hsmap.put("credit", Double.parseDouble(getContentsListOfSelectItem(scenario, "//accounting/journalentry[" + (i+1) + "]/items/item[" + (ii+1) + "]/amountcredit").get(0)));
                aList.add(asList(0, false, hsmap));*/
            }
            data.put("line_ids", aList);
            data.put("state", "posted");
            if(exist<0)
                wsapi.addElement(c.getErp(), "account.move", data, c.getUidapiaccess(), passworOdoo);
        }
    }
    
    public void generateMarket() throws Exception {
        //  Récupération des marchés définis dans le XML
        volumPerDayPerProduct = Integer.parseInt(getContentsListOfSelectItem(scenario, "//markets/@baseVolumDemandDay").get(0));            
        List<String> markets = getContentsListOfSelectItem(scenario, "//markets/market");
        for(int i=0;i<markets.size();i++) {
            areas.add(new Area(getContentsListOfSelectItem(scenario, "//markets/market[" + (i+1) + "]/name").get(0),
                    Double.parseDouble(getContentsListOfSelectItem(scenario, "//markets/market[" + (i+1) + "]/@part").get(0)), 
                        Double.parseDouble(getContentsListOfSelectItem(scenario, "//markets/market[" + (i+1) + "]/@elasticity").get(0))));
            int volumDemand = (int) Math.round(volumPerDayPerProduct/100*areas.get(i).getMarketPart());
            areas.get(i).setPartByConsumers(Double.valueOf(getContentsListOfSelectItem(scenario, "//markets/market[" + (i+1) + "]/@demandPartByCustomers").get(0)));
            int nbCustomers = (int) Math.round(volumDemand / 100 * areas.get(i).getPartByConsumers());
            for(int ii=0;ii<nbCustomers;ii++) {
                areas.get(i).addConsumer(new Consumer((areas.get(i).getName() + String.valueOf(ii))));
            }
        }
    }
    
    public void tryConnection(Company c) {
        
        writeLn(typeOfMessage.INFO, "Connexion de la base de données (" + c.getErp() + ") de l'équipe : " + c.getName());
        try {
            c.setUidapiaccess(wsapi.getUID(c.getErp(), accountOdoo, passworOdoo));
            writeLn(typeOfMessage.INFO, "-> OK!");
        } catch (Exception e) {
            writeLn(typeOfMessage.ERROR, e.getMessage());
        }
    }
    
    public void generateDemand(int day, Area a) {
        Iterator<Consumer> consumers = a.getConsumers().iterator();
        while(consumers.hasNext()) {
            Consumer c = consumers.next();
            int quantity = (int) Math.round(volumPerDayPerProduct/100*a.getMarketPart()/a.getConsumers().size());
            //  Itérer dans tous les produits (EN DUR ACTUELLEMENT !!!)
            c.addDemand(new Demand(products.get(0), day, quantity));
            c.addDemand(new Demand(products.get(1), day, quantity));
        }
        
    }
    
    public void entryStock(int day, Company c) throws Exception {

        for(int i=0;i<products.size();i++) {
            if(products.get(i) instanceof Good) {
                int quantity = Integer.valueOf(getContentsListOfSelectItem(scenario, "//product_sellable[name='" + products.get(i).getName() + 
                        "']/stockentry").get(0));
                HashMap data = new HashMap();
                data.put("name", "Auto production of " + products.get(i).getName() + " for " + day + "day");
                data.put("location_id", String.valueOf(wsapi.searchID(c.getErp(), c.getUidapiaccess(), passworOdoo, "stock.location", "WH/Stock")));
                data.put("stock.inventory", "product");
                data.put("product_id", products.get(i).getId());
                data.put("line_ids", asList(0, false, new HashMap<String, Object>() {{
                        put("product_id", quantity);}}));
                int inventory = wsapi.addElement(c.getErp(), "stock.inventory", data, c.getUidapiaccess(), passworOdoo);
                wsapi.workflowProgress(c.getErp(), c.getUidapiaccess(), passworOdoo, "stock.inventory", "prepare_inventory", inventory);
            }
        }
    }
}
