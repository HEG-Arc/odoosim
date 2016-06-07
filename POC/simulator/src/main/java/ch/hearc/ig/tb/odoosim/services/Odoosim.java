package ch.hearc.ig.tb.odoosim.services;

import ch.hearc.ig.tb.odoosim.business.*;
import ch.hearc.ig.tb.odoosim.interactions.Odoo;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import static ch.hearc.ig.tb.odoosim.utils.Utility.*;
import static java.util.Arrays.asList;
import java.util.HashMap;
import java.util.Iterator;
import static ch.hearc.ig.tb.odoosim.utils.Utility.getDataXML;

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
        //  L'ordre des opérations ne peut être changé car des dépendances entre
        //  les objets doivent être possible lors de la création de certains autres
        writeLn(typeOfMessage.INFO, "Début de la configuration");
        
        getOdooAccess();
        System.out.println("generation stackholders");
        generateStakeholders();
        System.out.println("generation compagnies");
        generateCompanies();
        System.out.println("génération des produits");
        generateProduct();
        System.out.println("génération du marché");
        generateMarketPlace();
        System.out.println("Début de l'itération sur chaque société");
        Iterator<Company> iCompany = companies.iterator();
        while (iCompany.hasNext()) {
            Company c = iCompany.next();
            System.out.println("test de la connexion");
            tryConnection(c);
            System.out.println("dm accounts");
            createMasterDataAccounts(c);
            System.out.println("dm banque");
            createMasterDataContacts(c, bankers.iterator());
            System.out.println("dm fournisseur");
            createMasterDataContacts(c, suppliers.iterator());
            System.out.println("dm actionnaire");
            createMasterDataContacts(c, shareholders.iterator());
            System.out.println("dm raw");
            createMasterDataRawMaterials(c);
            System.out.println("dm article");
            createMasterDataProduct(c);
        }
        System.out.println("génération BOM");
        //  La génération BOM se fait après pour avoir accès aux IDS qu'Odoo génère
        generateBOM();
        System.out.println("Nouvelle itération sur les companies");
        Iterator<Company> iC2 = companies.iterator();
        while (iC2.hasNext()) {
            System.out.println("dm BOM");
            Company c = iC2.next();
            createMasterDataBOM(c);
            System.out.println("dm Accounting");
            createAccountingStructure(c);
        }
    }

    public void processingGame() throws Exception {
        System.out.println("processing game !!!");
        Iterator iA = getIterator(areas);
        while (iA.hasNext()) {
            Area a = (Area) iA.next();
            generateDemand(day, a);
        }
        
        Iterator iC = getIterator(companies);
        while (iC.hasNext()) {
            Company c = (Company) iC.next();
            //stockAuto(day, c);
            //generateOffers(day, c);
            List data = wsapi.getData(c.getErp(), c.getUidapiaccess(), passworOdoo, "account.account", asList(asList("code", "=", "1500"))
                    , new HashMap() {{ put("fields", asList("code", "name", "type"));}});
            System.out.println("Résultat" + data);
            
        }

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

        writeLn(typeOfMessage.INFO, "Connexion de la base de données (" + c.getErp() + ") de l'équipe : " + c.getName());
        try {
            c.setUidapiaccess(wsapi.getUID(c.getErp(), accountOdoo, passworOdoo));
            writeLn(typeOfMessage.INFO, "-> OK!");
        } catch (Exception e) {
            writeLn(typeOfMessage.ERROR, e.getMessage());
        }
    }
    
    public void generateOffers(int day, Company c) throws Exception {
        
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
            c.setErp(subQR.get(0));
            c.setUidapiaccess(wsapi.getUID(c.getErp(), accountOdoo, passworOdoo));
            List<String> collaborators = getDataXML(scenario, "//company[@name='" + c.getName() + "']/players/player");
            for (int ii = 0; ii < collaborators.size(); ii++) {
                Collaborator collabo = new Collaborator(
                        getDataXML(scenario, "//company[@name='" + c.getName() + "']/players/player[" + (ii + 1) + "]/name").get(0));
                collabo.setMail(getDataXML(scenario, "//company[@name='" + c.getName() + "']/players/player[" + (ii + 1) + "]/@login").get(0));
                c.addCollaborators(collabo);
//                List<String> roles = getDataXML(scenario, "//company[@name='" + c.getName() + "']/players/player[" + (ii + 1) + "]/roles/role");
//                for (int iii=0; iii<roles.size();iii++) {
//                    List<Collaborator> allCollaborators = (List<Collaborator>) c.getCollaborators();
//                    allCollaborators.get(ii).addFunction(new Endossement(
//                            getDataXML(scenario, "//company[@name='" + c.getName() + "']/players/player[" + (ii + 1) + "]/roles/role[" + (iii + 1) + "]/name").get(0), 
//                                getDataXML(scenario, "//company[@name='" + c.getName() + "']/players/player[" + (ii + 1) + "]/roles/role[" + (iii + 1) + "]/value").get(0)));
//                }
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
                + "']/retailers/retailer[@type='" + r.getType() + "']/preferences/product/code");
        for (String code : preferences) {
            Good p = new Good("tempo");
            p.setCode(code);
            r.addPreference((Product) searchElement(p, products));
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
        int cpt = 1;
        for (String r : retailers) {
            String type = getDataXML(scenario, "//markets/market[name='" + a.getName() + "']/retailers/retailer[" + cpt + "]/@type").get(0);
            int volum = Integer.parseInt(getDataXML(scenario, "//markets/market[name='" + a.getName() + "']/retailers/retailer[" + cpt + "]/@number").get(0));
            int payMin = Integer.parseInt(getDataXML(scenario, "//markets/market[name='" + a.getName() + "']/retailers/retailer[" + cpt + "]/@payMin").get(0));
            int payMax = Integer.parseInt(getDataXML(scenario, "//markets/market[name='" + a.getName() + "']/retailers/retailer[" + cpt + "]/@payMax").get(0));
            Double part = Double.parseDouble(getDataXML(scenario, "//markets/market[name='" + a.getName() + "']/retailers/retailer[" + cpt + "]/@part").get(0));
            Double elasticity = Double.parseDouble(getDataXML(scenario, "//markets/market[name='" + a.getName() + "']/retailers/retailer[" + cpt + "]/@elasticity").get(0));
            cpt++;
            for (int i = 0; i < volum; i++) {
                a.addConsumer(new Retailer(type + (i + 1), part, elasticity, payMin, payMax, type));
                generatePreferences(a.getConsumers().get(i));
            }
        }
    }

    public void generateMarketPlace() throws Exception {

        //  Récupération du volume de vente journalier
        volumPerDayPerProduct = Integer.parseInt(getDataXML(scenario, "//markets/@baseVolumDemandDay").get(0));
        List<String> allArea = getDataXML(scenario, "//markets/market");
        //  Récupération des régions
        for (int i = 0; i < allArea.size(); i++) {
            areas.add(new Area(getDataXML(scenario, "//markets/market[" + (i + 1) + "]/name").get(0),
                    Double.parseDouble(getDataXML(scenario, "//markets/market[" + (i + 1) + "]/@part").get(0))));
            generateRetailers(areas.get(i));
        }
    }

    public void generateDemand(int day, Area a) {
        Iterator<Retailer> consumers = a.getConsumers().iterator();
        while (consumers.hasNext()) {
            Retailer c = consumers.next();
            int quantity = (int) Math.round(volumPerDayPerProduct / 100 * a.getMarketPart() / a.getConsumers().size());
            //  Itérer dans tous les produits (EN DUR ACTUELLEMENT !!!)
            c.addDemand(new Demand(products.get(0), day, quantity));
            c.addDemand(new Demand(products.get(1), day, quantity));
        }

    }
    
    public void createMasterDataAccounts(Company c) throws Exception {

        //  Création des comptes participants
        Iterator<Collaborator> iCollaborators = c.getCollaborators().iterator();
        HashMap data = new HashMap();

        while (iCollaborators.hasNext()) {
            Collaborator co = iCollaborators.next();
            int exist = wsapi.exist(c.getErp(), "res.users", c.getUidapiaccess(), passworOdoo, asList(asList("login", "=", co.getMail()), asList("name", "=", co.getName())));
            if (exist < 0) {
                data.put("name", co.getName());
                data.put("login", co.getMail());
                co.setId(wsapi.addElement(c.getErp(), "res.users", data, c.getUidapiaccess(), passworOdoo));
            } else {
                co.setId(exist);
            }

            /* N'est pas fonctionnel car tous les droits sur toutes les applications sont présent par défaut.
            Cela veut dire qu'il faudrait trouver dans toutes les applications disponibles où l'utilisateur est référentier
            et faire autant de requête de modification pour le supprimer... Puis attribuer le rôle défini dans le XML.
            à voir s'il sera possible de gérer cette partie dans la version POC. Sinon, les participants auront les droits sur
            tous les modules...
            //  Attribution des droits spécifiques
            List<Endossement> endossements = (List<Endossement>) co.getFunction();
            for(int i=0; i<endossements.size(); i++) {
                int catID = wsapi.exist(c.getErp(), "ir.module.category", c.getUidapiaccess(), passworOdoo, asList(asList("name", "=", endossements.get(i).getDepartment())));
                String role = endossements.get(i).getRole();
                int group = wsapi.exist(c.getErp(), "res.groups", c.getUidapiaccess(), passworOdoo, asList(asList("category_id", "=", catID), asList("name", "=", role)));
                data.clear();
                data.put("users", co.getId());
                wsapi.update(c.getErp(), "res.groups", c.getUidapiaccess(), passworOdoo, group, data);
            }
             */
        }

    }

    public <T> void createMasterDataContacts(Company c, Iterator<T> iT) throws Exception {

        HashMap data = new HashMap();
        while (iT.hasNext()) {

            Object o = iT.next();
            if (o instanceof Supplier) {
                Supplier s = (Supplier) o;
                int exist = wsapi.exist(c.getErp(), "res.partner", c.getUidapiaccess(), passworOdoo, asList(asList("customer", "=", false),
                        asList("supplier", "=", true), asList("name", "=", s.getName()), asList("company_type", "=", "company")));
                if (exist < 0) {
                    data.put("name", s.getName());
                    data.put("company_type", "company");
                    data.put("customer", false);
                    data.put("supplier", true);
                    s.setId(wsapi.addElement(c.getErp(), "res.partner", data, c.getUidapiaccess(), passworOdoo));
                } else {
                    s.setId(exist);
                }
            } else if (o instanceof Shareholder) {
                Shareholder s = (Shareholder) o;
                int exist = wsapi.exist(c.getErp(), "res.partner", c.getUidapiaccess(), passworOdoo, asList(asList("customer", "=", false),
                        asList("supplier", "=", false), asList("name", "=", s.getName()), asList("company_type", "=", "company")));
                if (exist < 0) {
                    data.put("name", s.getName());
                    data.put("company_type", "company");
                    data.put("customer", false);
                    data.put("supplier", false);
                    s.setId(wsapi.addElement(c.getErp(), "res.partner", data, c.getUidapiaccess(), passworOdoo));
                } else {
                    s.setId(exist);
                }
            } else if (o instanceof Banker) {
                Banker b = (Banker) o;
                int exist = wsapi.exist(c.getErp(), "res.partner", c.getUidapiaccess(), passworOdoo, asList(asList("customer", "=", false),
                        asList("supplier", "=", false), asList("name", "=", b.getName()), asList("company_type", "=", "company")));
                if (exist < 0) {
                    data.put("name", b.getName());
                    data.put("company_type", "company");
                    data.put("customer", false);
                    data.put("supplier", false);
                    b.setId(wsapi.addElement(c.getErp(), "res.partner", data, c.getUidapiaccess(), passworOdoo));
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
                int exist = wsapi.exist(c.getErp(), "product.template", c.getUidapiaccess(), passworOdoo, asList(asList("name", "=", rm.getName()),
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
                    rm.setId(wsapi.addElement(c.getErp(), "product.template", data, c.getUidapiaccess(), passworOdoo));
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
                int exist = wsapi.exist(c.getErp(), "product.template", c.getUidapiaccess(), passworOdoo, asList(asList("name", "=", gd.getName()),
                        asList("type", "=", "product"), asList("default_code", "=", gd.getCode())));
                if (exist < 0) {
                    data.put("name", gd.getName());
                    data.put("type", "product");
                    data.put("default_code", gd.getCode());
                    data.put("list_price", gd.getIndicativeSalePrice());
                    data.put("standard_price", gd.getPurchasePrice());
                    data.put("sale_ok", true);
                    data.put("purchase_ok", false);
                    gd.setId(wsapi.addElement(c.getErp(), "product.template", data, c.getUidapiaccess(), passworOdoo));
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
                int exist = wsapi.exist(c.getErp(), "mrp.bom", c.getUidapiaccess(), passworOdoo, asList(asList("product_tmpl_id", "=", p.getName())));
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
                    wsapi.addElement(c.getErp(), "mrp.bom", data, c.getUidapiaccess(), passworOdoo);
                }
            }

        }
    }

    public void createAccountingStructure(Company c) throws Exception {
        HashMap data = new HashMap();
        List<String> journals = getDataXML(scenario, "//accounting/journalentry");
        for (int i = 0; i < journals.size(); i++) {
            String name = getDataXML(scenario, "//accounting/journalentry[" + (i + 1) + "]/name").get(0);
            //data.put("journal_id", wsapi.searchID(c.getErp(), c.getUidapiaccess(), passworOdoo, "account.journal", name));
            data.put("journal_id", wsapi.exist(c.getErp(), "account.journal", c.getUidapiaccess(), passworOdoo, asList(asList("name", "=", name))));
            List<String> entries = getDataXML(scenario, "//accounting/journalentry[" + (i + 1) + "]/items/item");
            List aList = new ArrayList<>();
            int exist = 0;
            for (int ii = 0; ii < entries.size(); ii++) {
                String erp = c.getErp();
                int uid = c.getUidapiaccess();
//                int acc_id = wsapi.searchAccountByCode(c.getErp(), c.getUidapiaccess(), passworOdoo, "account.account",
//                        getDataXML(scenario, "//accounting/journalentry[" + (i + 1) + "]/items/item[" + (ii + 1) + "]/account/number").get(0));
                int acc_id = wsapi.exist(c.getErp(), "account.account", c.getUidapiaccess(), passworOdoo, asList(asList("code", "=", 
                        getDataXML(scenario, "//accounting/journalentry[" + (i + 1) + "]/items/item[" + (ii + 1) + "]/account/number").get(0))));
                //int par_id = wsapi.searchID(c.getErp(), c.getUidapiaccess(), passworOdoo, "res.partner",
                        //getDataXML(scenario, "//accounting/journalentry[" + (i + 1) + "]/items/item[" + (ii + 1) + "]/partner/name").get(0));
                int par_id = wsapi.exist(c.getErp(), "res.partner", c.getUidapiaccess(), passworOdoo, asList(asList("name", "=", 
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
                exist = wsapi.exist(erp, "account.move.line", uid, passworOdoo,
                        asList(asList("journal_id", "=", data.get("journal_id")), asList("partner_id", "=", par_id), asList("account_id", "=", acc_id),
                                asList("debit", "=", debit), asList("credit", "=", credit)));
            }
            data.put("line_ids", aList);
            data.put("state", "posted");
            if (exist < 0) {
                wsapi.addElement(c.getErp(), "account.move", data, c.getUidapiaccess(), passworOdoo);
            }
        }
    }

    public <T> Object searchElement(Object o, List<T> liste) {

        int size = liste.size();
        for (int i = 0; i < size; i++) {
            if (liste.get(i).equals(o)) {
                return (Object) liste.get(i);
            }
        }
        return null;
    }
    
    public void stockAuto(int day, Company c) throws Exception {
        
        //  Récupération du statut de la règle
        String enable = getDataXML(scenario, "//autoinventorygoods/@activ").get(0);
        if(Boolean.valueOf(enable)) {
            for (int i = 0; i < products.size(); i++) {
                if (products.get(i) instanceof Good) {
                    
                    int quantity = Integer.valueOf(getDataXML(scenario, "//autoinventorygoods/@quantityPerDay").get(0));
                    int pId = products.get(i).getId();
                    HashMap data = new HashMap();
                    data.put("product_id", pId);
                    data.put("qty", quantity);
                    data.put("name", "Inventaire automatique pour " + products.get(i).getName());
//                    data.put("location_id", String.valueOf(wsapi.searchID(c.getErp(), c.getUidapiaccess(), passworOdoo, "stock.location",
//                            getDataXML(scenario, "//defaultstock/name").get(0))));
                    data.put("location_id", wsapi.exist(c.getErp(), "stock.location", c.getUidapiaccess(), passworOdoo, 
                            asList(asList("name", "=", getDataXML(scenario, "//defaultstock/name").get(0)))));
                    wsapi.addElement(c.getErp(), "stock.quant", data, c.getUidapiaccess(), passworOdoo);
                }
            }
        }
    }
}
