package ch.hearc.ig.tb.odoosim.views;

//import ch.hearc.ig.tb.odoosim.business.*;
import ch.hearc.ig.tb.odoosim.businesstwo.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import static java.util.Arrays.asList;
import java.util.Collection;
import java.util.Collections;
import static java.util.Collections.emptyMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
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
            System.out.println("[4] Générer un prix aléatoire");
            System.out.println("[X] Terminer le programme");

            System.out.println("Entrer l'option désirée : ");

            switch (s.nextLine()) {

                case "1":
                    //paramDB();
                    connection(database, username, password);
                    break;
                case "2":
                    //  Récupération des produits
                    getProducts();
                    break;
                case "3":
                    //  Simulation du marché achat-vente
                    simulateMarket();
                    break;
                case "4":
                    randomQuantity(1);
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
    
    private static Double randomPrice() {
        Double minBound = 25.00;
        Double maxBound = 50.55;
        Random r = new Random();
        Double price = minBound + (maxBound - minBound) * r.nextDouble();
        return price;
    }
    
    private static int randomQuantity(int type) {
        int maxBound = 0;
        if(type<1)
            maxBound = 70;
        else
            maxBound = 20;
        Random r = new Random();
        return r.nextInt(maxBound);
    }
    
    private static void simulateMarket(){
        
        //  Création des éléments
        ArrayList<Good> goods = new ArrayList<>();
        ArrayList<Region> regions = new ArrayList<>();
        ArrayList<Vendor> vendors = new ArrayList();
        ArrayList<Consumer> consumers = new ArrayList<>();
        ArrayList<Market> markets = new ArrayList<>();
        ArrayList<Channel> channels = new ArrayList();
        
        //  Création d'un produit
        goods.add(new Material(0, "Lait"));
        
        //  Création des canaux de distribution
        channels.add(new Ecommerce("DC00", 10));
        channels.add(new Grocery("DC01", 15));
        channels.add(new Hypermarket("DC02", 30));
        
        //  Création de la région
        Region region = new Region("West");
        
        //  Création du marché -> Un marché est une place d'échange (vente-achat)
        //  pour 3 éléments distincts : Un produit, un canal de distribution et une région
        Market m1 = new Market("Lait-West-DC00", goods.get(0), channels.get(0), region);
        
        //  L'idée c'est de mettre en place un timer pour itérer toutes les
        //  60 secondes et recréer les demandes pour chaque consommateur.
        //  CI-DESSOUS -> STATIQUE!!        
        Consumer c = new Consumer(0, "John Q. Bishop", region);
        c.addNeed(new Need(goods.get(0), randomQuantity(1)),m1);
        consumers.add(c);
        Consumer c1 = new Consumer(0, "Anthony Tomat", region);
        c1.addNeed(new Need(goods.get(0), randomQuantity(1)),m1);
        consumers.add(c1);
        Consumer c2 = new Consumer(0, "Boris Fritscher", region);
        c2.addNeed(new Need(goods.get(0), randomQuantity(1)),m1);
        consumers.add(c2);
        Consumer c3 = new Consumer(0, "Cédric Gaspoz", region);
        c3.addNeed(new Need(goods.get(0), randomQuantity(1)),m1);
        consumers.add(c3);
        Consumer c4 = new Consumer(0, "Alessio Do Santos", region);
        c4.addNeed(new Need(goods.get(0), randomQuantity(1)),m1);
        consumers.add(c4);
        Consumer c5 = new Consumer(0, "Cédric Baudet", region);
        c5.addNeed(new Need(goods.get(0), randomQuantity(1)),m1);
        consumers.add(c5);
        Consumer c6 = new Consumer(0, "Francesco Termine", region);
        c6.addNeed(new Need(goods.get(0), randomQuantity(1)),m1);
        consumers.add(c6);
        
        //  Toutes les 60 secondes, récupérer les stocks régionaux de toutes les
        //  sociétés (les équipes) dans la base de données Odoo via l'API        
        Vendor v = new Vendor(0, "Dairy Corp. & Co.", region);
        v.addOffer(new Offer(goods.get(0), randomQuantity(0), randomPrice()),m1);
        Vendor v1 = new Vendor(1, "Michel et Coco", region);
        v1.addOffer(new Offer(goods.get(0), randomQuantity(0), randomPrice()),m1);
        Vendor v2 = new Vendor(0, "MicroMilk", region);
        v2.addOffer(new Offer(goods.get(0), randomQuantity(0), randomPrice()),m1);
        vendors.add(v2);
        vendors.add(v1);
        vendors.add(v);
        
        //  Algorithme du choix de la meilleure offre pour chaque client avec 
        //  comme critère, le prix et la quantité.
        
        //  On trie l'offre d'un certain marché par prix du plus bas au plus haut
        Collections.sort(m1.getAllOffers());
        
        //  On crée les itérateurs pour les différents éléments
        Iterator<Consumer> iConsumers = consumers.listIterator();
        Iterator<Vendor> iVendors = vendors.listIterator();
        Iterator<Need> iNeeds;    
        Iterator<Offer> iOffers;
        Consumer con;
        Need nee;
        Offer off;
        
        //  Récapitulatif des données du marché
        System.out.println("Taille du marché en demande : "+m1.getAllNeeds().size());
        System.out.println("Taille du marché en offre : "+m1.getAllOffers().size());
        
        //  On boucle sur tous les consommateurs
        while (iConsumers.hasNext()) {
            //  Récupération des besoins du client en cours
            con = iConsumers.next();
            iNeeds = con.getNeeds().listIterator();
            
            //  Parcours des besoins pour un client
            while(iNeeds.hasNext()) {
                nee = iNeeds.next();
                iOffers = m1.getAllOffers().listIterator();
                
                //  Parcours de l'offre et choix
                while(iOffers.hasNext()) {
                    off = iOffers.next();
                    
                    //  Si la quantité de l'offre peut assouvir le besoin, on la prend
                    if(off.getQuantity()>=nee.getQuantity()) {
                        
                        //  On change la quantité de l'offre de moins la demande
                        off.setQuantity(off.getQuantity()-nee.getQuantity());
                        System.out.println("L'achat du produit ("+nee.getGood().getName()+
                                ") par " + con.getName()+
                                " dans la quantité de "+ String.valueOf(nee.getQuantity())+
                                " s'effectue avec la société qui offre au prix de : "+
                                off.getPrice());
                        
                        //  L'offre ok, on sort de la boucle
                        break;
                    } else {
                        
                        //  Uniquement pour le test. On affiche comme quoi c'est pas possible
                        //  avec cette offre.
                        System.out.println("Le besoin de "+con.getName()+
                                " pour "+String.valueOf(nee.getQuantity())+
                                " ne peut plus être satisfait... avec le prix de "+
                                off.getPrice());
                    }
                }
                
            }               
        }
        displayMenu("Fin de la simulation du marché");
    }

}
