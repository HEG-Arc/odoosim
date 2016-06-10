package ch.hearc.ig.tb.odoosim.simulator;

import ch.hearc.ig.tb.odoosim.business.*;
import ch.hearc.ig.tb.odoosim.saasinterfacing.Odoo;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import static ch.hearc.ig.tb.odoosim.utilities.Utility.*;
import static java.util.Arrays.asList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Collection;
import java.util.Collections;

public class Odoosim {

    private String accountOdoo;
    private String passworOdoo;
    private List<Company> companies;
    private List<Product> products;
    private List<Banker> bankers;
    private List<Supplier> suppliers;
    private List<Shareholder> shareholders;
    private List<Area> areas;
    //  New !
    private List<Demand> demands;
    private List<Offer> offers;
    //  ----;
    private final Odoo wsapi;
    private final Document scenario;
    private int volumPerDayPerProduct;
    private int day;
    private int month;
    private int rounds;
    private int dayByRound;
    private int daysDuration;
    private int PKI_volum_demands;
    private int PKI_volum_offers;
    private int PKI_volum_transactions;
    private Double PKI_volum_monetary;
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

        demands = new ArrayList<>();
        offers = new ArrayList<>();

        wsapi = new Odoo("https", "odoo.com");
        scenario = getXMLConfiguration();
        day = 1;
        month = 1;
    }

    public void configurationGame() throws Exception {
        //  L'ordre des opérations ne peut être changé car des dépendances entre
        //  les objets doivent être possible lors de la création de certains autres
        getSettingsXML();
        getOdooAccess();
        generateStakeholders();
        generateCompanies();
        generateProduct();
        generateMarketPlace();
        Iterator<Company> iCompany = companies.iterator();
        while (iCompany.hasNext()) {
            Company c = iCompany.next();
            tryConnection(c);
            createMasterDataAccounts(c);
            createMasterDataContacts(c, bankers.iterator());
            createMasterDataContacts(c, suppliers.iterator());
            createMasterDataContacts(c, shareholders.iterator());
            createMasterDataRawMaterials(c);
            createMasterDataProduct(c);
        }
        //  La génération BOM se fait après pour avoir accès aux IDS qu'Odoo génère
        generateBOM();
        Iterator<Company> iC2 = companies.iterator();
        while (iC2.hasNext()) {
            Company c = iC2.next();
            createMasterDataBOM(c);
            createAccountingStructure(c);
        }
    }

    public void processingGame() throws Exception {
        
        while (day <= dayByRound) {
            //  Génération d'indicateurs journaliers
            PKI_volum_demands = 0;
            PKI_volum_offers = 0;
            PKI_volum_monetary = 00.00;
            PKI_volum_transactions = 0;

            long before = System.currentTimeMillis();
            System.out.println("\n\rDate : " + day + "/" + month + " (durée jdu jours simulés = " + (daysDuration) + " secondes)\n\r");

            //  Gestion de la demande
            Iterator iA = getIterator(areas);
            while (iA.hasNext()) {
                Area a = (Area) iA.next();
                generateDemand(day, a);
            }

            //  Gestion de l'offre
            Iterator iC = getIterator(companies);
            while (iC.hasNext()) {
                Company c = (Company) iC.next();
                stockAuto(day, c);
                generateOffers(day, c);
            }

            Collections.shuffle(areas);
            
            for (Area a : areas) {
                
                Collections.shuffle(a.getConsumers());
                for (Retailer r : a.getConsumers()) {
                    r.buy(wsapi, day, month);
                }
            }
            //  à supprimer en version finale
            PKI_volum_transactions = countExchanges();
            long after = System.currentTimeMillis();
            day++;
            System.out.println("Temps des opérations : " + (after-before));
            //  Au cas où les opérations ont été anormalements longues, nous ne faisons pas l'attente et enchaînons directement
            long waiting = (daysDuration * 1000) - (after - before);
            System.out.println("Temps d'attente : " + waiting);
            if (waiting > 0) {
                Thread.sleep(waiting);
            }
            /*
            System.out.println("\n\r   --> Quantité de produit demandée sur le marché  " + PKI_volum_demands + "(volume unitaire de demandes = " + countDemands() + ")");
            System.out.println("   --> Quantité de produit offerte sur le marché  " + PKI_volum_offers + "(volume unitaire d'offres = " + countOffers() + ")");
            System.out.println("   --> Nombre de transactions sur le marché (cumulée) " + PKI_volum_transactions);
            System.out.println("\n\rDurée des opérations de simulation " + ((after - before) / 1000) + " secondes..."); */
        }
        if(--rounds>0) {
            month++;
            day = 1;
            this.processingGame();
        }
    }

    public void getSettingsXML() throws Exception {
        //  Auparavant dans la génération du marché
        volumPerDayPerProduct = Integer.parseInt(getDataXML(scenario, "//markets/@baseVolumDemandDay").get(0));
        rounds = Integer.parseInt(getDataXML(scenario, "//simulation/round/@qty").get(0));
        dayByRound = Integer.parseInt(getDataXML(scenario, "//simulation/round/day/number").get(0));
        daysDuration = Integer.parseInt(getDataXML(scenario, "//simulation/round/day/@durationSeconds").get(0));
    }

    public int countExchanges() {
        int counter = 0;
        for(Company c : companies)
            counter += c.getExchanges().size();
        return counter;
    }
    
    public int countOffers() {
        int counter = 0;
        for (Company company : companies) {
            counter += company.getOffers().size();
        }
        return counter;
    }

    public int countDemands() {
        int counter = 0;
        for (Area area : areas) {
            for (Retailer retailer : area.getConsumers()) {
                counter += retailer.getDemands().size();
            }
        }
        return counter;
    }

    public void persistDataGame() throws Exception {
        //  Récupération des données générées et persistence (DB ou File)
        throw new Exception("Phase du jeu non-disponible actuellement");
    }

    public void getOdooAccess() throws Exception {

        //  Récupération du compte d'accès à Odoo.com
        accountOdoo = getDataXML(scenario, "//account").get(0);
        passworOdoo = getDataXML(scenario, "//password").get(0);
    }

    public void tryConnection(Company c) {

        writeLn(typeOfMessage.INFO, "Connexion à l'instance : " + c.getErp() + " (" + c.getName() + ")");
        try {
            c.setUidapiaccess(wsapi.getUID(c.getErp(), accountOdoo, passworOdoo));
            writeLn(typeOfMessage.INFO, " -> Succès!");
        } catch (Exception e) {
            writeLn(typeOfMessage.ERROR, e.getMessage());
        }
    }

    public void generateDemand(int day, Area a) throws Exception {
        Iterator<Retailer> consumers = a.getConsumers().iterator();
        while (consumers.hasNext()) {
            Retailer retailer = consumers.next();
            Collection<Good> productsPreferences = retailer.getProductsPreferences();
            for (Good good : productsPreferences) {
                Demand d = new Demand(good, day, 0.0);
                d.setOwner(retailer);
                Integer nbCustomers = a.getConsumers().size();
                Double partArea = a.getMarketPart();
                Double partCons = retailer.getMarketPart();
                //  nbCustomers ne doit pas être comme celui du haut car sinon ça compte tous les revendeurs de la région et c'est
                //  faux! Là, il faut compter uniquement les revendeurs du même type !!!
                nbCustomers = Integer.parseInt(getDataXML(scenario, "//markets/market[name='" + a.getName() + "']/retailers/retailer[@type='" + retailer.getType() + "']/@number").get(0));
                Double demandQty = (double) Math.round(volumPerDayPerProduct / 100 * partArea / nbCustomers / 100 * partCons);
                if(demandQty<1)
                    demandQty=1.0;

                try {
                    d = (Demand) searchElement(d, (List<Demand>) retailer.getDemands());
                } catch (Exception ex) {
                    //Logger.getLogger(Odoosim.class.getName()).log(Level.SEVERE, null, ex);
                    d.setProduct(good);
                    retailer.addDemand(d);
                    //  Tester comme pour l'offre (relatif au deux attributs ajoutés le 0906 pour éviter de devoir tout rappatrier
                    //  lors de la sélection de l'offre (algorithme)
                    demands.add(d);
                } finally {
                    d.setDay(day);
                    d.setQuantity(demandQty);
                }
                PKI_volum_demands += demandQty;
            }
        }

    }

    public void generateOffers(int day, Company c) throws Exception {

        for (Product p : products) {
            if (p instanceof Good) {
                Good g = (Good) p;
                Object tuple = wsapi.getTuple(c.getErp(), c.getUidapiaccess(), passworOdoo, "product.template",
                        asList(asList("name", "=", p.getName())), new HashMap() {
                    {
                        put("fields", asList("list_price", "qty_available"));
                    }
                });

                if (tuple instanceof HashMap) {
                    HashMap product = (HashMap) tuple;
                    Offer o = new Offer(g, c);

                    try {
                        o = (Offer) searchElement(o, (List<Offer>) c.getOffers());
                    } catch (Exception e) {
                        o.setOwner(c);
                        o.setProduct(g);
                        c.addOffer(o);
                        //  Tester si cela fonctionne !
                        //offers.add(o);
                    } finally {
                        if (!((Double) product.get("qty_available") < 1)) {
                            o.setDay(day);
                            o.setPrice((Double) product.get("list_price"));
                            o.setQuantity((Double) product.get("qty_available"));
                            PKI_volum_offers += o.getQuantity();
                        }
                    }

                } else {
                    writeLn(typeOfMessage.ERROR, "Erreur de génération de l'offre pour " + c.getName());
                }
            }
        }
    }

    public void generateProduct() throws Exception {
        List<String> queryResults;
        queryResults = getDataXML(scenario, "//product_purchasable");
        for (int i = 0; i < queryResults.size(); i++) {
            Rawmaterial r = new Rawmaterial(getDataXML(scenario, "//product_purchasable[" + (i + 1) + "]/@default_code").get(0),
                    getDataXML(scenario, "//product_purchasable[" + (i + 1) + "]/name").get(0),
                    Double.parseDouble(getDataXML(scenario, "//product_purchasable[" + (i + 1) + "]/@standard_price").get(0)));
            Supplier s = new Supplier(getDataXML(scenario, "//product_purchasable[" + (i + 1) + "]/supplier/name").get(0));
            r.setSupplier((Supplier) searchElement(s, suppliers));
            products.add(r);
        }
        //  Création des produits destinés à la vente
        queryResults = getDataXML(scenario, "//product_sellable");
        for (int i = 0; i < queryResults.size(); i++) {
            Good g = new Good(Double.parseDouble(getDataXML(scenario, "//product_sellable[" + (i + 1) + "]/@list_price").get(0)),
                    getDataXML(scenario, "//product_sellable[" + (i + 1) + "]/@default_code").get(0),
                    getDataXML(scenario, "//product_sellable[" + (i + 1) + "]/name").get(0),
                    Double.parseDouble(getDataXML(scenario, "//product_sellable[" + (i + 1) + "]/@standard_price").get(0)));
            products.add(g);
        }
    }

    public void generateCompanies() throws Exception {

        List<String> companies = getDataXML(scenario, "//company/@name");
        for (int i = 0; i < companies.size(); i++) {
            Company c = new Company(companies.get(i));
            List<String> subQR = getDataXML(scenario, "//company[@name='" + companies.get(i) + "']/erp/@database ");
            c.setErp(String.valueOf(subQR.get(0)).toLowerCase());
            c.setUidapiaccess(wsapi.getUID(c.getErp(), accountOdoo, passworOdoo));
            c.setPassapiaccess(passworOdoo);
            List<String> collaborators = getDataXML(scenario, "//company[@name='" + c.getName() + "']/players/player");
            for (int ii = 0; ii < collaborators.size(); ii++) {
                Collaborator collabo = new Collaborator(
                        getDataXML(scenario, "//company[@name='" + c.getName() + "']/players/player[" + (ii + 1) + "]/name").get(0));
                collabo.setMail(getDataXML(scenario, "//company[@name='" + c.getName() + "']/players/player[" + (ii + 1) + "]/@login").get(0));
                c.addCollaborators(collabo);
            }

            Banker b = new Banker(getDataXML(scenario, "//company[@name='" + c.getName() + "']/bank/name").get(0));
            Shareholder sh = new Shareholder(getDataXML(scenario, "//company[@name='" + c.getName() + "']/shareholder/name").get(0));
            c.setBanker((Banker) searchElement(b, bankers));
            c.setShareholder((Shareholder) searchElement(sh, shareholders));
            this.companies.add(c);
        }
    }

    public void generatePreferences(Retailer r) throws Exception {
        //  Récupération à partir du fichier settings.xml
        List<String> preferences = getDataXML(scenario, "//markets/market[name='" + r.getLocalisation().getName()
                + "']/retailers/retailer[@type='" + r.getType() + "']/preferences/product");
        for (int i=0; i<preferences.size();i++) {
            Good p = new Good("tempo");
            p.setCode(getDataXML(scenario, "//markets/market[name='" + r.getLocalisation().getName()
                + "']/retailers/retailer[@type='" + r.getType() + "']/preferences/product[" + (i + 1) + "]/code").get(0));
            try {
                r.addPreference((Good) searchElement(p, products));
            } catch (Exception e) {
                //  Log!
            }
        }
    }

    public void generateStakeholders() throws Exception {
        List<String> shareholders = getDataXML(scenario, "//contact[@type='shareholder']");
        for (int i = 0; i < shareholders.size(); i++) {
            Shareholder shareholder = new Shareholder(shareholders.get(i));
            this.shareholders.add(shareholder);
        }

        List<String> bankers = getDataXML(scenario, "//contact[@type='banker']");
        for (int i = 0; i < bankers.size(); i++) {
            Banker banker = new Banker(bankers.get(i));
            this.bankers.add(banker);
        }

        List<String> suppliers = getDataXML(scenario, "//contact[@type='supplier']");
        for (int i = 0; i < suppliers.size(); i++) {
            Supplier supplier = new Supplier(suppliers.get(i));
            this.suppliers.add(supplier);
        }
    }

    public void generateBOM() throws Exception {

        Iterator<Product> iP = products.iterator();
        while (iP.hasNext()) {
            Product p = (Product) iP.next();
            if (p instanceof Good) {
                Good pGood = (Good) p;
                List<String> bomXML = getDataXML(scenario, "//product_sellable[name='" + p.getName() + "']/bom/product");
                for (int ii = 0; ii < bomXML.size(); ii++) {
                    String qty = getDataXML(scenario, "//product_sellable[name='" + p.getName() + "']/bom/product[" + (ii + 1) + "]/@quantity").get(0);
                    String pro = getDataXML(scenario, "//product_sellable[name='" + p.getName() + "']/bom/product[" + (ii + 1) + "]/name").get(0);
                    Rawmaterial rwm = new Rawmaterial(pro);
                    rwm = (Rawmaterial) searchElement(rwm, products);
                    pGood.addBOM(rwm.getId(), Double.parseDouble(qty));
                }
            }
        }
    }

    public void generateRetailers(Area a) throws Exception {
        List<String> retailers = getDataXML(scenario, "//markets/market[name='" + a.getName() + "']/retailers/retailer");
        for(int i=0; i<retailers.size(); i++) {
            String name = getDataXML(scenario, "//markets/market[name='" + a.getName() + "']/retailers/retailer[" + (i + 1) + "]/name").get(0);
            String type = getDataXML(scenario, "//markets/market[name='" + a.getName() + "']/retailers/retailer[" + (i + 1) + "]/@type").get(0);
            int volum = Integer.parseInt(getDataXML(scenario, "//markets/market[name='" + a.getName() + "']/retailers/retailer[" + (i + 1) + "]/@number").get(0));
            int payMin = Integer.parseInt(getDataXML(scenario, "//markets/market[name='" + a.getName() + "']/retailers/retailer[" + (i + 1) + "]/@payMin").get(0));
            int payMax = Integer.parseInt(getDataXML(scenario, "//markets/market[name='" + a.getName() + "']/retailers/retailer[" + (i + 1) + "]/@payMax").get(0));
            Double part = Double.parseDouble(getDataXML(scenario, "//markets/market[name='" + a.getName() + "']/retailers/retailer[" + (i + 1) + "]/@part").get(0));
            Double elasticity = Double.parseDouble(getDataXML(scenario, "//markets/market[name='" + a.getName() + "']/retailers/retailer[" + (i + 1) + "]/@elasticity").get(0));
            for (int ii = 0; ii < volum; ii++) {
                name = name + String.valueOf(ii+1);
                Retailer r = new Retailer(name, part, elasticity, payMin, payMax, type);
                a.addConsumer(r);
                generatePreferences(r);
            }
        }
    }

    public void generateMarketPlace() throws Exception {

        //  Récupération du volume de vente journalier
        //volumPerDayPerProduct = Integer.parseInt(getDataXML(scenario, "//markets/@baseVolumDemandDay").get(0));
        List<String> allArea = getDataXML(scenario, "//markets/market");
        //  Récupération des régions
        for (int i = 0; i < allArea.size(); i++) {
            areas.add(new Area(getDataXML(scenario, "//markets/market[" + (i + 1) + "]/name").get(0),
                    Double.parseDouble(getDataXML(scenario, "//markets/market[" + (i + 1) + "]/@part").get(0))));
            generateRetailers(areas.get(i));
        }
    }

    public void createMasterDataAccounts(Company c) throws Exception {

        //  Création des comptes participants
        Iterator<Collaborator> iCollaborators = c.getCollaborators().iterator();
        HashMap data = new HashMap();

        while (iCollaborators.hasNext()) {
            Collaborator co = iCollaborators.next();
            int exist = wsapi.getID(c.getErp(), "res.users", c.getUidapiaccess(), passworOdoo, asList(asList("login", "=", co.getMail()), asList("name", "=", co.getName())));
            if (exist < 0) {
                data.put("name", co.getName());
                data.put("login", co.getMail());
                co.setId(wsapi.insert(c.getErp(), "res.users", data, c.getUidapiaccess(), passworOdoo));
            } else {
                co.setId(exist);
            }
        }

    }

    public <T> void createMasterDataContacts(Company c, Iterator<T> iT) throws Exception {

        HashMap data = new HashMap();
        while (iT.hasNext()) {

            Object o = iT.next();
            if (o instanceof Supplier) {
                Supplier s = (Supplier) o;
                int exist = wsapi.getID(c.getErp(), "res.partner", c.getUidapiaccess(), passworOdoo, asList(asList("customer", "=", false),
                        asList("supplier", "=", true), asList("name", "=", s.getName()), asList("company_type", "=", "company")));
                if (exist < 0) {
                    data.put("name", s.getName());
                    data.put("company_type", "company");
                    data.put("customer", false);
                    data.put("supplier", true);
                    s.setId(wsapi.insert(c.getErp(), "res.partner", data, c.getUidapiaccess(), passworOdoo));
                } else {
                    s.setId(exist);
                }
            } else if (o instanceof Shareholder) {
                Shareholder s = (Shareholder) o;
                int exist = wsapi.getID(c.getErp(), "res.partner", c.getUidapiaccess(), passworOdoo, asList(asList("customer", "=", false),
                        asList("supplier", "=", false), asList("name", "=", s.getName()), asList("company_type", "=", "company")));
                if (exist < 0) {
                    data.put("name", s.getName());
                    data.put("company_type", "company");
                    data.put("customer", false);
                    data.put("supplier", false);
                    s.setId(wsapi.insert(c.getErp(), "res.partner", data, c.getUidapiaccess(), passworOdoo));
                } else {
                    s.setId(exist);
                }
            } else if (o instanceof Banker) {
                Banker b = (Banker) o;
                int exist = wsapi.getID(c.getErp(), "res.partner", c.getUidapiaccess(), passworOdoo, asList(asList("customer", "=", false),
                        asList("supplier", "=", false), asList("name", "=", b.getName()), asList("company_type", "=", "company")));
                if (exist < 0) {
                    data.put("name", b.getName());
                    data.put("company_type", "company");
                    data.put("customer", false);
                    data.put("supplier", false);
                    b.setId(wsapi.insert(c.getErp(), "res.partner", data, c.getUidapiaccess(), passworOdoo));
                } else {
                    b.setId(exist);
                }
            }
        }
    }

    public void createMasterDataRawMaterials(Company c) throws Exception {
        HashMap data = new HashMap();
        Iterator<Product> iP = products.iterator();
        while (iP.hasNext()) {
            Product p = iP.next();
            if (p instanceof Rawmaterial) {
                Rawmaterial rm = (Rawmaterial) p;
                int exist = wsapi.getID(c.getErp(), "product.template", c.getUidapiaccess(), passworOdoo, asList(asList("name", "=", rm.getName()),
                        asList("type", "=", "product")));
                if (exist < 0) {
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
                    rm.setId(wsapi.insert(c.getErp(), "product.template", data, c.getUidapiaccess(), passworOdoo));
                } else {
                    rm.setId(exist);
                }
            }
        }
    }

    public void createMasterDataProduct(Company c) throws Exception {
        HashMap data = new HashMap();
        Iterator<Product> iP = products.iterator();
        while (iP.hasNext()) {
            Product p = iP.next();
            if (p instanceof Good) {
                Good gd = (Good) p;
                int exist = wsapi.getID(c.getErp(), "product.template", c.getUidapiaccess(), passworOdoo, asList(asList("name", "=", gd.getName()),
                        asList("type", "=", "product"), asList("default_code", "=", gd.getCode())));
                if (exist < 0) {
                    data.put("name", gd.getName());
                    data.put("type", "product");
                    data.put("default_code", gd.getCode());
                    data.put("list_price", gd.getIndicativeSalePrice());
                    data.put("standard_price", gd.getPurchasePrice());
                    data.put("sale_ok", true);
                    data.put("purchase_ok", false);
                    gd.setId(wsapi.insert(c.getErp(), "product.template", data, c.getUidapiaccess(), passworOdoo));
                } else {
                    gd.setId(exist);
                }
            }
        }
    }

    public void createMasterDataBOM(Company c) throws Exception {

        HashMap data = new HashMap();
        HashMap dataBOM;
        Iterator<Product> iP = products.iterator();

        while (iP.hasNext()) {
            Product p = iP.next();
            if (p instanceof Good) {
                int exist = wsapi.getID(c.getErp(), "mrp.bom", c.getUidapiaccess(), passworOdoo, asList(asList("product_tmpl_id", "=", p.getName())));
                if (exist < 0) {
                    dataBOM = (HashMap) ((Good) p).getBillOfMaterials();
                    Iterator iBOM = dataBOM.keySet().iterator();
                    List aList = new ArrayList<>();

                    while (iBOM.hasNext()) {
                        int idProduct = (int) iBOM.next();
                        Double quantity = (Double) dataBOM.get(idProduct);
                        HashMap hhmp = new HashMap<String, Object>() {
                            {
                                put("product_id", idProduct);
                                put("product_qty", quantity);
                            }
                        };
                        aList.add(asList(0, false, hhmp));
                    }
                    data.put("product_tmpl_id", p.getId());
                    data.put("product_qty", 1.00);
                    data.put("type", "normal");
                    data.put("bom_line_ids", aList);
                    wsapi.insert(c.getErp(), "mrp.bom", data, c.getUidapiaccess(), passworOdoo);
                }
            }
        }
    }

    public void createAccountingStructure(Company c) throws Exception {
        HashMap data = new HashMap();
        List<String> journals = getDataXML(scenario, "//accounting/journalentry");
        for (int i = 0; i < journals.size(); i++) {
            String name = getDataXML(scenario, "//accounting/journalentry[" + (i + 1) + "]/name").get(0);
            data.put("journal_id", wsapi.getID(c.getErp(), "account.journal", c.getUidapiaccess(), passworOdoo, asList(asList("name", "=", name))));
            List<String> entries = getDataXML(scenario, "//accounting/journalentry[" + (i + 1) + "]/items/item");
            List aList = new ArrayList<>();
            int exist = 0;
            for (int ii = 0; ii < entries.size(); ii++) {
                String erp = c.getErp();
                int uid = c.getUidapiaccess();
                int acc_id = wsapi.getID(c.getErp(), "account.account", c.getUidapiaccess(), passworOdoo, asList(asList("code", "=",
                        getDataXML(scenario, "//accounting/journalentry[" + (i + 1) + "]/items/item[" + (ii + 1) + "]/account/number").get(0))));
                int par_id = wsapi.getID(c.getErp(), "res.partner", c.getUidapiaccess(), passworOdoo, asList(asList("name", "=",
                        getDataXML(scenario, "//accounting/journalentry[" + (i + 1) + "]/items/item[" + (ii + 1) + "]/partner/name").get(0))));
                String label = getDataXML(scenario, "//accounting/journalentry[" + (i + 1) + "]/items/item[" + (ii + 1) + "]/label").get(0);
                Double debit = Double.parseDouble(getDataXML(scenario, "//accounting/journalentry[" + (i + 1) + "]/items/item[" + (ii + 1) + "]/amountdebit").get(0));
                Double credit = Double.parseDouble(getDataXML(scenario, "//accounting/journalentry[" + (i + 1) + "]/items/item[" + (ii + 1) + "]/amountcredit").get(0));
                HashMap hsmap = new HashMap<String, Object>();
                hsmap.put("account_id", acc_id);
                hsmap.put("partner_id", par_id);
                hsmap.put("name", label);
                hsmap.put("debit", debit);
                hsmap.put("credit", credit);
                aList.add(asList(0, false, hsmap));
                exist = wsapi.getID(erp, "account.move.line", uid, passworOdoo,
                        asList(asList("journal_id", "=", data.get("journal_id")), asList("partner_id", "=", par_id), asList("account_id", "=", acc_id),
                                asList("debit", "=", debit), asList("credit", "=", credit)));
            }
            data.put("line_ids", aList);
            data.put("state", "posted");
            if (exist < 0) {
                wsapi.insert(c.getErp(), "account.move", data, c.getUidapiaccess(), passworOdoo);
            }
        }
    }

    public <T> Object searchElement(Object o, List<T> liste) throws Exception {

        int size = liste.size();
        for (int i = 0; i < size; i++) {
            if (liste.get(i).equals(o)) {
                return (Object) liste.get(i);
            }
        }
        throw new Exception("Element introuvable ! ");
    }

    public void stockAuto(int day, Company c) throws Exception {

        //  Récupération du statut de la règle
        String enable = getDataXML(scenario, "//autoinventorygoods/@activ").get(0);
        if (Boolean.valueOf(enable)) {
            for (int i = 0; i < products.size(); i++) {
                if (products.get(i) instanceof Good) {

                    int quantity = Integer.valueOf(getDataXML(scenario, "//autoinventorygoods/@quantityPerDay").get(0));
                    int pId = products.get(i).getId();
                    HashMap data = new HashMap();
                    data.put("in_date", "2016-" + month + "-" + day + " 00:00:00");
                    data.put("product_id", pId);
                    data.put("qty", quantity);
                    data.put("name", "Inventaire automatique pour " + products.get(i).getName());
                    data.put("location_id", wsapi.getID(c.getErp(), "stock.location", c.getUidapiaccess(), passworOdoo,
                            asList(asList("name", "=", getDataXML(scenario, "//defaultstock/name").get(0)))));
                    wsapi.insert(c.getErp(), "stock.quant", data, c.getUidapiaccess(), passworOdoo);
                    /*HashMap picking = (HashMap) wsapi.getTuple(c.getErp(), c.getUidapiaccess(), c.getPassapiaccess(), "stock.picking.type", 
                            asList(asList("name", "=", "Receipts")), new HashMap(){{ put("fields", asList("id")); }});
                    int quantity = Integer.valueOf(getDataXML(scenario, "//autoinventorygoods/@quantityPerDay").get(0));
                    int pId = products.get(i).getId();
                    int lId = wsapi.getID(c.getErp(), "stock.location", c.getUidapiaccess(), passworOdoo,
                            asList(asList("name", "=", getDataXML(scenario, "//defaultstock/name").get(0))));
                    HashMap data = new HashMap();
                    List<Collaborator> col = (List<Collaborator>) c.getCollaborators();
                    data.put("partner_id", col.get(0).getId());
                    data.put("min_date", "2016-01-28 00:00:00");
                    data.put("move_type", "one");
                    data.put("date", "2016-01-28 00:00:00");
                    data.put("location_dest_id", lId);
                    data.put("location_id", lId);
                    data.put("move_lines", asList(asList(0, false, 
                            new HashMap() {{ put("product_id", pId); put("product_uom_qty", quantity); put("product_uom", 1);
                            put("name", "Automatic by Odoosim");}})));
                    data.put("picking_type_id", picking.get("id"));
                    
                    int quantID = wsapi.insert(c.getErp(), "stock.picking", data, c.getUidapiaccess(), passworOdoo);
                    wsapi.workflowProgress(c.getErp(), c.getUidapiaccess(), c.getPassapiaccess(), "stock.picking", "do_new_transfert");
                    HashMap move = (HashMap) wsapi.getTuple(c.getErp(), c.getUidapiaccess(), c.getPassapiaccess(), "stock.picking",
                            asList(asList("id", "=", quantID)), new HashMap() {{ put("fields", Collections.EMPTY_LIST); }});
                    System.out.println("Ajoute = " + quantID);*/
                }
            }
        }
    }
}
