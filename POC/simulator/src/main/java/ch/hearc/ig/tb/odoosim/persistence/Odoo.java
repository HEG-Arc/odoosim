package ch.hearc.ig.tb.odoosim.persistence;

import java.net.URL;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import java.util.HashMap;
import java.util.List;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public class Odoo {

    private String protocol;
    private String dns;
    private XmlRpcClientConfigImpl configuration;
    private XmlRpcClient common;
    private XmlRpcClient object;

    public Odoo(String protocol, String dns) {
        this.protocol = protocol;
        this.dns = dns;
        configuration = new XmlRpcClientConfigImpl();
        common = new XmlRpcClient();
        object = new XmlRpcClient();
    }

    public int getUID(String database, String account, String password) throws Exception {
        setConfiguration(1, database);
        return (int) common.execute(configuration, "authenticate", asList(database, account, password, emptyMap()));
    }

    public int addElement(String database, String model, HashMap data, int uid, String password) throws Exception {
        setConfiguration(2, database);
        object.setConfig(configuration);
        return (int) object.execute("execute_kw",
                asList(database, uid, password, model, "create",
                        asList(data)));
    }

    public int searchID(String database, int uid, String password, String model, String value) throws Exception {
        setConfiguration(2, database);
        object.setConfig(configuration);
        List resultat = asList((Object[]) object.execute("execute_kw", asList(
                database, uid, password,
                model, "search_read",
                asList(asList(asList("name", "=", value))))));
        if(resultat.size()<1)
            return 0;
        else {
            HashMap hsM = (HashMap) resultat.get(0);
            return (int) hsM.get("id");
        }
    }
    
    public int searchAccountByCode(String database, int uid, String password, String model, String value) throws Exception {
        setConfiguration(2, database);
        object.setConfig(configuration);
        List resultat = asList((Object[]) object.execute("execute_kw", asList(
                database, uid, password,
                model, "search_read",
                asList(asList(asList("code", "=", value))))));
        if(resultat.size()<1)
            return 0;
        else {
            HashMap hsM = (HashMap) resultat.get(0);
            return (int) hsM.get("id");
        }
    }

    public int searchVendorByName(String database, int uid, String password, String vendor) throws Exception {
        setConfiguration(2, database);
        object.setConfig(configuration);
        List resultat = asList((Object[]) object.execute("execute_kw", asList(
                database, uid, password,
                "res.partner", "search_read",
                asList(asList(
                        asList("name", "=", vendor))),
                new HashMap() {
            {
                put("fields", asList("id"));
            }
        }
        )));
        HashMap hsM = (HashMap) resultat.get(0);
        return (int) hsM.get("id");
    }

    private void setConfiguration(int type, String database) throws Exception {
        switch (type) {
            case 1:
                configuration.setServerURL(new URL(String.format("%s/xmlrpc/2/common", protocol + "://" + database + "." + dns)));
                break;
            case 2:
                configuration.setServerURL(new URL(String.format("%s/xmlrpc/2/object", protocol + "://" + database + "." + dns)));
                break;
            default:
                throw new Exception("Le type passé n'est pas valide");
        }
    }
}
