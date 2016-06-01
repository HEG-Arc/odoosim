package ch.hearc.ig.tb.odoosimulator.business;

import ch.hearc.ig.tb.odoosimulator.odoointeractions.OdooAPI;
import java.net.URL;
import java.util.HashMap;

public class Company {
    
    private String databaseURL;
    private String name;
    private OdooAPI wsapi;
    private Market market;

    public Company() {
    }

    public Company(String databaseURL, String name, Market market) throws Exception {
        this.databaseURL = databaseURL;
        this.name = name;
        this.market = market;
        wsapi = new OdooAPI(this.databaseURL);
    }

    public String getDatabaseURL() {
        return databaseURL;
    }

    public void setDatabaseURL(String databaseURL) {
        this.databaseURL = databaseURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public void masterDataCreation() throws Exception {
        HashMap data = new HashMap();
        for(Customer c : market.getCustomers()) {
            data.put("name", c.getName());
            wsapi.create(data, "res.partner");
        }
        data.clear();
        data.put("name", market.getProduct().getName());
        data.put("sale_ok", true);
        data.put("list_price", 10.00);
        data.put("standard_price", 10.00);
        data.put("sale_delay", 0.00);
        wsapi.create(data, "product.template");
        market.getProduct().setId(wsapi.searchProduct());
        int warehouse = wsapi.searchWarehouse();
        data.clear();
        data.put("name", "Inventaire initial");
        data.put("location_id", warehouse);
        //  Cela semble fonctionnel. Il faut encore trouver le moyen de gérer les "Selection"
        //  pour créer un enregistrement de stock.inventory en mode inventory of "one product"
        //  et lui passer l'id!
        wsapi.create(data, "stock.inventory");
    }
}