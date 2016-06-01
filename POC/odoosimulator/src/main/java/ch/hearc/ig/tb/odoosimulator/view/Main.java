/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hearc.ig.tb.odoosimulator.view;

import java.net.URL;
import java.util.ArrayList;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import java.util.HashMap;
import java.util.List;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

/**
 *
 * @author tomant
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        String password = "Welcome";
        String db = "edu-c1";
        XmlRpcClientConfigImpl configuration = new XmlRpcClientConfigImpl();
        configuration.setServerURL(new URL(String.format("%s/xmlrpc/2/common", "https://edu-c1.odoo.com")));
        XmlRpcClient clientCommon = new XmlRpcClient();
        clientCommon.setConfig(configuration);
        int uidAccess = (int) clientCommon.execute(configuration, "authenticate", asList("edu-c1", "anthony.tomat@he-arc.ch", password, emptyMap()));
        configuration.setServerURL(new URL(String.format("%s/xmlrpc/2/object", "https://edu-c1.odoo.com")));
        XmlRpcClient clientObject = new XmlRpcClient();
        clientObject.setConfig(configuration);
        List resultat = asList((Object[]) clientObject.execute("execute_kw", asList(
                db, uidAccess, password,
                "stock.location", "search_read",
                asList(asList(
                        asList("name", "=", "Stock"))),
                new HashMap() {{
                    put("fields", asList("id"));}}
        )));
        
        HashMap hsM = (HashMap) resultat.get(0);
        System.out.println("List de résultat = " + hsM.get("id"));
        HashMap data = new HashMap();
        data.put("name", "Inventaire initial");
        data.put("location_id", hsM.get("id"));
        clientObject.execute("execute_kw", 
                asList(db, uidAccess, password, "stock.inventory", "create",
                        asList(data)));
    }

}
