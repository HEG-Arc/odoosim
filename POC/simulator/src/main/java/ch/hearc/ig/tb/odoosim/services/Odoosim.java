/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hearc.ig.tb.odoosim.services;

import ch.hearc.ig.tb.odoosim.business.Collaborator;
import ch.hearc.ig.tb.odoosim.business.Company;
import ch.hearc.ig.tb.odoosim.persistence.Odoo;
import static ch.hearc.ig.tb.odoosim.utils.Utility.getContentsListOfSelectItem;
import static ch.hearc.ig.tb.odoosim.utils.Utility.getDataXML;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import static ch.hearc.ig.tb.odoosim.utils.Utility.*;
import static java.util.Arrays.asList;
import java.util.HashMap;

/**
 *
 * @author tomant
 */
public class Odoosim {
    
    private String accountOdoo;
    private String passworOdoo;
    private List<Company> companies;
    private final Odoo wsapi;
    
    public Odoosim() {
        accountOdoo = "";
        passworOdoo = "";
        companies = new ArrayList<>();
        wsapi = new Odoo("https", "odoo.com");
    }
    
    public void configurationGame() throws Exception {
        String xPathQuery;
        List<String> queryResults;
        HashMap data = new HashMap();
        Document  scenario = getXMLConfiguration();
        
        //  Récupération du compte d'accès à Odoo.com
        xPathQuery = "//account";
        accountOdoo = getDataXML(scenario, xPathQuery).get(0);
        xPathQuery = "//password";
        passworOdoo = getDataXML(scenario, xPathQuery).get(0);
        
        //  Lecture des équipes et test des connexions
        xPathQuery = "//company/@name";
        queryResults = getContentsListOfSelectItem(scenario, xPathQuery);
        for(int i=0;i<queryResults.size();i++) {
            List<String> subQR;
            Company c = new Company(queryResults.get(i));
            
            xPathQuery = "//company[@name='"+queryResults.get(i)+"']/erp/@database ";
            subQR = getContentsListOfSelectItem(scenario, xPathQuery);
            c.setErp(subQR.get(0));
            c.setUidapiaccess(wsapi.getUID(c.getErp(), accountOdoo, passworOdoo));
            companies.add(c);
        }
        
        //  Création des équipes grâce au XML
        for(int i=0;i<companies.size();i++) {
            
            //  Récupération des informations sur les joueurs
            xPathQuery = "//company[@name='"+companies.get(i).getName()+"']/players/player";
            queryResults = getContentsListOfSelectItem(scenario, xPathQuery);
            
            for(int ii=0;ii<queryResults.size();ii++) {
                Collaborator collabo = new Collaborator(queryResults.get(ii));
                
                xPathQuery = "//company[@name='"+companies.get(i).getName()+"']/players/player["+(ii+1)+"]/@login";
                collabo.setMail(getContentsListOfSelectItem(scenario, xPathQuery).get(0));
                xPathQuery = "//company[@name='"+companies.get(i).getName()+"']/players/player["+(ii+1)+"]/@password";
                collabo.setPassword(getContentsListOfSelectItem(scenario, xPathQuery).get(0));
                companies.get(i).addCollaborators(collabo);
                
                //  Création du compte participant dans l'ERP -- Pas fonctionnel les noms changent d'une base à l'autre !
                List<Collaborator> collaborators = (List<Collaborator>) companies.get(i).getCollaborators();
                data.clear();
                data.put("name", collaborators.get(ii).getName());
                data.put("login", collaborators.get(ii).getMail());
                data.put("sel_groups_9_27_10", 10);
                data.put("sel_groups_34_35", false);
                data.put("sel_groups_42_43", false);
                data.put("sel_groups_23_24_25", false);
                data.put("sel_groups_46_47", false);
                data.put("sel_groups_4", 4);
                data.put("sel_groups_1_2_3", false);
                data.put("in_group_33", false);
                data.put("in_group_17", true);
                data.put("in_group_15", true);
                data.put("in_group_11", false);
                wsapi.addElement(companies.get(i).getErp(), "res.users", data, companies.get(i).getUidapiaccess(), passworOdoo);
            }
            
            //  Création des contacts (fournisseurs, banque et actionnaire)
            
            queryResults = getContentsListOfSelectItem(scenario, "//contact");
            for(int ii=0;ii<queryResults.size();ii++) {
                data.clear();
                data.put("name", getContentsListOfSelectItem(scenario, "//contact["+(ii+1)+"]").get(0));
                data.put("company_type", getContentsListOfSelectItem(scenario, "//contact["+(ii+1)+"]/@company_type").get(0));
                data.put("customer", getContentsListOfSelectItem(scenario, "//contact["+(ii+1)+"]/@customer").get(0));
                data.put("supplier", getContentsListOfSelectItem(scenario, "//contact["+(ii+1)+"]/@supplier").get(0));
                wsapi.addElement(companies.get(i).getErp(), "res.partner", data, companies.get(i).getUidapiaccess(), passworOdoo);
            }
            
            //  Création de matières premières
            queryResults = getContentsListOfSelectItem(scenario, "//product_purchasable");
            for(int ii=0;ii<queryResults.size();ii++) {
                int idSupplier = wsapi.searchVendorByName(companies.get(i).getErp(), companies.get(i).getUidapiaccess(), passworOdoo, getContentsListOfSelectItem(scenario, "//product_purchasable["+(ii+1)+"]/@supplier").get(0));
                String stndPrice = getContentsListOfSelectItem(scenario, "//product_purchasable["+(ii+1)+"]/@standard_price").get(0);
                data.put("name", getContentsListOfSelectItem(scenario, "//product_purchasable["+(ii+1)+"]/name").get(0));
                data.put("type", getContentsListOfSelectItem(scenario, "//product_purchasable["+(ii+1)+"]/@type").get(0));
                data.put("default_code", getContentsListOfSelectItem(scenario, "//product_purchasable["+(ii+1)+"]/@default_code").get(0));
                data.put("list_price", getContentsListOfSelectItem(scenario, "//product_purchasable["+(ii+1)+"]/@list_price").get(0));
                data.put("standard_price", stndPrice);
                data.put("sale_ok", false);
                data.put("purchase_ok", true);
                data.put("seller_ids", asList(asList(0, false, new HashMap<String, Object>() {{
                        put("name", idSupplier);
                        put("min_qty", 1.00);
                        put("price", stndPrice);}})));
                wsapi.addElement(companies.get(i).getErp(), "product.template", data, companies.get(i).getUidapiaccess(), passworOdoo);
            }
            
            //  Création des produits destinés à la vente
            
            //  Création des nomenclatures
            
            //  Création des clients
            
            //  Mise en place du bilan comptable
            
        }
    }
    
    public void processingGame() throws Exception {
        //  Dérouler le scénario
        throw new Exception("Phase du jeu non-disponible actuellement");
    }
    
    public void persistDataGame() throws Exception {
        //  Récupération des données générées et persistence (DB ou File)
        throw new Exception("Phase du jeu non-disponible actuellement");
    }
}
