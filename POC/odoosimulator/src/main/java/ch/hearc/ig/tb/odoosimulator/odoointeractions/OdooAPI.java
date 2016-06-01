package ch.hearc.ig.tb.odoosimulator.odoointeractions;

import java.net.MalformedURLException;
import java.net.URL;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public class OdooAPI {
    private String database;
    private String password;
    private int uidAccess;
    private XmlRpcClientConfigImpl configuration;
    private XmlRpcClient clientCommon;
    private XmlRpcClient clientObject;

    public OdooAPI(String database) throws Exception {
        this.database = database;
        password = "Welcome";
        configuration = new XmlRpcClientConfigImpl();
        configuration.setServerURL(new URL(String.format("%s/xmlrpc/2/common", this.database)));
        clientCommon = new XmlRpcClient();
        clientCommon.setConfig(configuration);
        uidAccess = getUID();
        configuration.setServerURL(new URL(String.format("%s/xmlrpc/2/object", this.database)));
        clientObject = new XmlRpcClient();
        clientObject.setConfig(configuration);
    }
    
    private int getUID() throws Exception {
        return (int) clientCommon.execute(configuration, "authenticate", asList(database, "anthony.tomat@he-arc.ch", password, emptyMap()));
    }
    
    public void createPartner(String name) throws Exception {
        clientObject.execute("execute_kw", 
                asList(database, uidAccess, password, "res.partner", "create",
                        asList(new HashMap(){{put("name", name);}})));
    }
}
