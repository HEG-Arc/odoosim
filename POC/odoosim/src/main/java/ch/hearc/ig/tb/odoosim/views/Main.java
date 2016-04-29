package ch.hearc.ig.tb.odoosim.views;

import ch.hearc.ig.tb.odoosim.business.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public class Main {

    private static XmlRpcClientConfigImpl configuration;
    private static XmlRpcClient client;
    private static String database = "edu-hegarc-odoosim";
    private static String username = "anthony.tomat@he-arc.ch";
    private static String password = "blabla14";
    private static int dbconfigured;
    private static Scanner s = new Scanner(System.in);
    private static int uid = 1;

    public static void main(String[] args) {
        displayMenu("");
    }

    private static void connection(String database, String username, String password) {

        try {
            System.out.println("Test d'authentification !");
            String dns = "odoo.com";
            String URL = "https://" + database + "." + dns;
            Object[] params = new Object[]{database, username, password};

            XmlRpcClientConfigImpl clientConfiguration = new XmlRpcClientConfigImpl();
            clientConfiguration.setServerURL(new URL(String.format("%s/xmlrpc/2/common", URL)));
            XmlRpcClient client = new XmlRpcClient();
            client.setConfig(clientConfiguration);
            uid = (int) client.execute(clientConfiguration, "authenticate", asList(database, username, password, emptyMap()));
            System.out.println("Effectué avec succès avec l'identifiant N° " + uid + " référencé");
            displayMenu("");
        } catch (Exception e) {
            displayMenu("Erreur : " + e.getMessage());
        }
    }

    private static void displayMenu(String message) {

        try {
            s.reset();
            if (message != "") {
                System.out.println("Message : " + message);
            }
            System.out.println("Bienvenue sur le simulateur OdooSIM");
            System.out.println("Que voulez-vous faire?");
            System.out.println("[1] Tester la connexion à une base de données Odoo en mode Saas");
            System.out.println("[2] Lister les produits de la base de données Odoo");
            System.out.println("[3] Exécuter le simulateur et visualiser les opérations");
            System.out.println("[X] Terminer le programme");

            System.out.println("Entrer l'option désirée : ");

            switch (s.nextLine()) {

                case "1":
                    //paramDB();
                    connection(database, username, password);
                    break;
                case "2":
                    //paramDB();
                    getProducts();
                    break;
                case "3":
                    startSimulation();
                    break;
                case "X":
                    System.out.println("Aurevoir !");
                    break;
                default:
                    displayMenu("Fonctionnalité non conforme ! Veuillez choisir une option valide. Merci");
            }
        } catch (Exception e) {
            displayMenu("Erreur : " + e.getMessage());
        }
    }

    private static void getProducts() {
        try {

            String dns = "odoo.com";
            String URL = "https://" + database + "." + dns;
            Object[] params = new Object[]{database, username, password};
            XmlRpcClientConfigImpl clientConfiguration = new XmlRpcClientConfigImpl();
            System.out.println("\n\rTest de récupération des Produits");
            clientConfiguration.setServerURL(new URL(String.format("%s/xmlrpc/2/object", URL)));
            XmlRpcClient cliento = new XmlRpcClient();
            int o = (int) cliento.execute("execute_kw", asList(database, uid, password,"res.partner", "search_count",
    asList(asList(
        asList("is_company", "=", true),
        asList("customer", "=", true)))));
            System.out.println(String.valueOf(o));
        } catch (Exception e) {
            displayMenu("Erreur : " + e.getMessage());
        }
    }

    private static void paramDB() {
        System.out.println("Veuillez entrer la base de données : ");
        database = s.nextLine();
        System.out.println("Veuillez entrer le nom d'utilisateur : ");
        username = s.nextLine();
        System.out.println("Veuillez entrer le mot de passe : ");
        password = s.nextLine();
    }

    private static void startSimulation() {
        throw new UnsupportedOperationException("Cette fonctionnalité n'est pas encore disponible");
//        System.out.println("Créer les demandeurs");
//        ArrayList customers = new ArrayList();
//
//        for (int i = 0; i < 20; i++) {
//            customers.add(new Person());
//        }
//
//        for (int i = 0; i < 30; i++) {
//            customers.add(new Compagny());
//        }
//
//        System.out.println("Créer les offreurs");
//
//        Manufacturer m1 = new Manufacturer(0, "A");
//        Manufacturer m2 = new Manufacturer(1, "B");
//        Manufacturer m3 = new Manufacturer(2, "C");
//
//        System.out.println("Créer les produits de l'offre 1");
//        Product p1 = new Product(0, "Lait", 1.20, m1);
//        m1.addProduct(p1);
//
//        Product p2 = new Product(0, "Crème", 3.30, m1);
//        m1.addProduct(p2);
//
//        Product p3 = new Product(0, "Fromage", 2.10, m1);
//        m1.addProduct(p3);
//
//        Product p4 = new Product(0, "Bière", 3.80, m1);
//        m1.addProduct(p4);
//
//        System.out.println("Créer les produits de l'offre 2");
//        Product p5 = new Product(0, "Lait", 1.10, m2);
//        m2.addProduct(p5);
//
//        Product p6 = new Product(0, "Crème", 3.75, m2);
//        m2.addProduct(p6);
//
//        System.out.println("Créer les produits de l'offre 3");
//        Product p7 = new Product(0, "Fromage", 2.90, m3);
//        m3.addProduct(p7);
//
//        Product p8 = new Product(0, "Bière", 4.05, m3);
//        m3.addProduct(p8);
//
//        System.out.println("FIN CONFIGURATION");
    }

}
