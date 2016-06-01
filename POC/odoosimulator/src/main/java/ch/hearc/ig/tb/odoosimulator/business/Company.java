package ch.hearc.ig.tb.odoosimulator.business;

import ch.hearc.ig.tb.odoosimulator.odoointeractions.OdooAPI;
import java.net.URL;

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
        for(Customer c : market.getCustomers())
            wsapi.createPartner(c.getName());
    }
}
