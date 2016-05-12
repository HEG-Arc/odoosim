package ch.hearc.ig.tb.odoosim.views;

import ch.hearc.ig.tb.odoosim.business.economics.Offer;
import ch.hearc.ig.tb.odoosim.business.economics.Channel;
import ch.hearc.ig.tb.odoosim.business.economics.Need;
import ch.hearc.ig.tb.odoosim.business.economics.Hypermarket;
import ch.hearc.ig.tb.odoosim.business.economics.Grocery;
import ch.hearc.ig.tb.odoosim.business.economics.Consumer;
import ch.hearc.ig.tb.odoosim.business.economics.Good;
import ch.hearc.ig.tb.odoosim.business.economics.Market;
import ch.hearc.ig.tb.odoosim.business.economics.Ecommerce;
import ch.hearc.ig.tb.odoosim.business.economics.Invoice;
import ch.hearc.ig.tb.odoosim.business.economics.Material;
import ch.hearc.ig.tb.odoosim.business.economics.Vendor;
import ch.hearc.ig.tb.odoosim.business.economics.Region;
import java.net.URL;
import java.util.ArrayList;
import static java.util.Arrays.asList;
import java.util.Collections;
import static java.util.Collections.emptyMap;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import ch.hearc.ig.tb.odoosim.business.interactions.*;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import oracle.sql.STRUCT;

public class Main {

    private final static Random RDM = new Random();
    private static String database;
    private static String username;
    private static String password;
    private static String oracleServer;
    private static String oracleDb;
    private static String oracleUser;
    private static String oraclePassword;
    private final static Scanner SCR = new Scanner(System.in);
    private static int uid = 1;
    private static int day;

    private static Properties prop = new Properties();
    private static InputStream input = null;

    public static void main(String[] args) {
        try {

            if (prop.size() < 1 || input == null) {
                input = new FileInputStream("sim.properties");
                prop.load(input);
                database = prop.getProperty("odoo.default.database");
                username = prop.getProperty("odoo.default.account.name");
                password = prop.getProperty("odoo.default.account.password");
                oracleServer = prop.getProperty("oracle.server");
                oracleDb = prop.getProperty("oracle.database");
                oracleUser = prop.getProperty("oracle.username");
                oraclePassword = prop.getProperty("oracle.password");
            }

            switch (getMenu()) {
                case "1":
                    connection(database, username, password);
                    break;
                case "2":
                    getProducts();
                    break;
                case "3":
                    simulateMarket();
                    main(null);
                    break;
                case "4":
                    System.out.println(randomPrice());
                    main(null);
                case "5":
                    System.out.println(randomQuantity(1));
                    main(null);
                case "6":
                    simulateTimer();
                    break;
                case "7":
                    TestPattern();
                    main(null);
                    break;
                case "8":
                    getSomethings();
                case "9":
                    viewDetailsRecord();
                case "X":
                    break;
            }

        } catch (Exception e) {
            System.out.println("Erreur");
            e.printStackTrace();
        }
    }

    public static String getMenu() {

        System.out.println("\n\rMenu principal du simulateur");
        System.out.println("Veuillez choisir l\'action souhaitée :");

        System.out.println("[1] Tester la connexion à une base de données Odoo en mode Saas");
        System.out.println("[2] Lister les produits de la base de données Odoo");
        System.out.println("[3] Simuler un marché");
        System.out.println("[4] Tester la génération de prix");
        System.out.println("[5] Tester la génération de quantité");
        System.out.println("[6] Simuler le temps qui passe (toute les 30 secondes)");
        System.out.println("[7] Tester le pattern Strategy");
        System.out.println("[8] Requêter un modèle");
        System.out.println("[9] Récupération des détails");
        System.out.println("[X] Mettre fin au simulateur");

        Scanner sc = new Scanner(System.in);
        char c = sc.nextLine().charAt(0);
        return String.valueOf(c).toUpperCase();
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
        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
        } finally {
            main(null);
        }
    }

    private static void getSomethings() {
        try {
            System.out.println("Veuillez indiquer le modèle à requêter ?");
            String model = SCR.nextLine();
            String dns = "odoo.com";
            String URL = "https://" + database + "." + dns;
            Object[] params = new Object[]{database, username, password};

            final XmlRpcClient models = new XmlRpcClient() {
                {
                    setConfig(new XmlRpcClientConfigImpl() {
                        {
                            setServerURL(new URL(String.format("%s/xmlrpc/2/object", URL)));
                        }
                    });
                }
            };

            List<Object> asList = asList((Object[]) models.execute("execute_kw", asList(
                    database, uid, password,
                    model, "search",
                    asList(asList(
                            asList("is_company", "=", true),
                            asList("customer", "=", true)))
            )));

            if (asList.size() > 0) {
                System.out.println("Résultat :");
                for (Object o : asList) {
                    System.out.println(o.toString());
                }
            }

        } catch (Exception ex) {
            System.out.println("Error : " + ex.getMessage());
        } finally {
            main(null);
        }
    }

    private static void getProducts() {
        try {

            String dns = "odoo.com";
            String URL = "https://" + database + "." + dns;
            Object[] params = new Object[]{database, username, password};

            final XmlRpcClient models = new XmlRpcClient() {
                {
                    setConfig(new XmlRpcClientConfigImpl() {
                        {
                            setServerURL(new URL(String.format("%s/xmlrpc/2/object", URL)));
                        }
                    });
                }
            };
            List<Object> asList = asList((Object[]) models.execute("execute_kw", asList(
                    database, uid, password,
                    "res.partner", "search",
                    asList(asList(
                            asList("is_company", "=", true),
                            asList("customer", "=", true)))
            )));
        } catch (Exception e) {
            System.out.println("Erreur" + e.getMessage());
        } finally {
            main(null);
        }
    }

    private static Double randomPrice() {
        Double minBound = 25.00;
        Double maxBound = 50.55;
        Double price = (minBound + (maxBound - minBound)) * RDM.nextDouble();
        return price;
    }

    private static int randomQuantity(int type) {
        int maxBound;
        if (type < 1) {
            maxBound = 70;
        } else {
            maxBound = 20;
        }
        return RDM.nextInt(maxBound);
    }

    private static void simulateMarket() {
        try {
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
            c.addNeed(new Need(goods.get(0), randomQuantity(1)), m1);
            consumers.add(c);
            Consumer c1 = new Consumer(0, "Anthony Tomat", region);
            c1.addNeed(new Need(goods.get(0), randomQuantity(1)), m1);
            consumers.add(c1);
            Consumer c2 = new Consumer(0, "Boris Fritscher", region);
            c2.addNeed(new Need(goods.get(0), randomQuantity(1)), m1);
            consumers.add(c2);
            Consumer c3 = new Consumer(0, "Cédric Gaspoz", region);
            c3.addNeed(new Need(goods.get(0), randomQuantity(1)), m1);
            consumers.add(c3);
            Consumer c4 = new Consumer(0, "Alessio Do Santos", region);
            c4.addNeed(new Need(goods.get(0), randomQuantity(1)), m1);
            consumers.add(c4);
            Consumer c5 = new Consumer(0, "Cédric Baudet", region);
            c5.addNeed(new Need(goods.get(0), randomQuantity(1)), m1);
            consumers.add(c5);
            Consumer c6 = new Consumer(0, "Francesco Termine", region);
            c6.addNeed(new Need(goods.get(0), randomQuantity(1)), m1);
            consumers.add(c6);

            //  Toutes les 60 secondes, récupérer les stocks régionaux de toutes les
            //  sociétés (les équipes) dans la base de données Odoo via l'API        
            Vendor v = new Vendor(0, "Dairy Corp. & Co.", region);
            v.addOffer(new Offer(goods.get(0), randomQuantity(0), randomPrice()), m1);
            Vendor v1 = new Vendor(1, "Michel et Coco", region);
            v1.addOffer(new Offer(goods.get(0), randomQuantity(0), randomPrice()), m1);
            Vendor v2 = new Vendor(0, "MicroMilk", region);
            v2.addOffer(new Offer(goods.get(0), randomQuantity(0), randomPrice()), m1);
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
            System.out.println("Taille du marché en demande : " + m1.getAllNeeds().size());
            System.out.println("Taille du marché en offre : " + m1.getAllOffers().size());

            //  On boucle sur tous les consommateurs
            while (iConsumers.hasNext()) {
                //  Récupération des besoins du client en cours
                con = iConsumers.next();
                iNeeds = con.getNeeds().listIterator();

                //  Parcours des besoins pour un client
                while (iNeeds.hasNext()) {
                    nee = iNeeds.next();
                    iOffers = m1.getAllOffers().listIterator();

                    //  Parcours de l'offre et choix
                    while (iOffers.hasNext()) {
                        off = iOffers.next();

                        //  Si la quantité de l'offre peut assouvir le besoin, on la prend
                        if (off.getQuantity() >= nee.getQuantity()) {

                            //  On change la quantité de l'offre de moins la demande
                            off.setQuantity(off.getQuantity() - nee.getQuantity());
                            System.out.println("L'achat du produit (" + nee.getGood().getName()
                                    + ") par " + con.getName()
                                    + " dans la quantité de " + String.valueOf(nee.getQuantity())
                                    + " s'effectue avec la société qui offre au prix de : "
                                    + off.getPrice());

                            //  L'offre ok, on sort de la boucle
                            break;
                        } else {

                            //  Uniquement pour le test. On affiche comme quoi c'est pas possible
                            //  avec cette offre.
                            System.out.println("Le besoin de " + con.getName()
                                    + " pour " + String.valueOf(nee.getQuantity())
                                    + " ne peut plus être satisfait... avec le prix de "
                                    + off.getPrice());
                        }
                    }

                }
            }
        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
        } finally {
            //main(null);
        }
    }

    private static void simulateTimer() {
        try {
            day = 0;
            Timer t = new Timer();
            MyTimerTask mTTask = new MyTimerTask();
            //  J'ai encore un soucis ici, je n'arrive pas à faire remplir cette condition
            //  de 31 fois... Et quand j'essaie avec le debug, il plante (sans doute à cause
            //  que le timer crée un thread... à voir
            do {
                t.schedule(mTTask, 0, 10000);
            } while (day < 31);
            t.cancel();
        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
        } finally {
            main(null);
        }

    }

    private static void Invoice(OdooAPI odooAPI, int i, Date date, Date date0) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            day++;
            System.out.println("\n\rJour numéro : " + day);
            simulateMarket();
        }
    }

    private static void TestPattern() {
        int identifiantInt = 34;
        Integer n = new Integer(identifiantInt);
        Invoice<Integer> ivi = new Invoice<>(new OdooAPI(), n);

        String identifiant = "049asdf20";
        Invoice<String> ivi2 = new Invoice<>(new alternative(), identifiant);
        System.out.println(ivi.getIdentifier());
        System.out.println(ivi2.getIdentifier());
    }

    public static void viewDetailsRecord() {

        try {

            System.out.println("Indiquer un id valide : ");
            String idstring = SCR.nextLine();
            int id = Integer.parseInt(idstring);
            String dns = "odoo.com";
            String URL = "https://" + database + "." + dns;
            Object[] params = new Object[]{database, username, password};

            final XmlRpcClient models = new XmlRpcClient() {
                {
                    setConfig(new XmlRpcClientConfigImpl() {
                        {
                            setServerURL(new URL(String.format("%s/xmlrpc/2/object", URL)));
                        }
                    });
                }
            };

            final Map record = (Map) ((Object[]) models.execute(
                    "execute_kw", asList(
                            database, uid, password,
                            "res.partner", "read",
                            asList(id)
                    )
            ))[0];

            Set records = record.entrySet();
            Iterator<Object> iR = records.iterator();
            System.out.println("Résultat : ");
            while (iR.hasNext()) {
                iR.next().toString();
            }

        } catch (Exception ex) {
            System.out.println("Error : " + ex.getMessage());
        } finally {main(null);}

    }
}
