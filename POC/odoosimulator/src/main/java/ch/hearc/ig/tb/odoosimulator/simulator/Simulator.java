package ch.hearc.ig.tb.odoosimulator.simulator;

import ch.hearc.ig.tb.odoosimulator.business.*;
import ch.hearc.ig.tb.odoosimulator.odoointeractions.OdooAPI;
import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.Timer;
import java.util.function.Consumer;

public class Simulator {
    
    private String idSimulation;
    private Date date;
    private Market market;
    private SimulatorTask progress;
    private Timer clock;
    private int numberOfDays;
    private int renewalQuantity;
    private int averageVolumDemand;
    private double startPrice;
    private double elasticityPercentil;
    private int numberOfCustomer;

    public Simulator() {
        idSimulation = "test";
        date = new Date();
        market = new Market(this);
        progress = new SimulatorTask(this);
        clock = new Timer("OdooSIM");
        numberOfDays = 10;
        renewalQuantity = 100;
        averageVolumDemand = 10000;
        startPrice = 10.00;
        elasticityPercentil = 10.00;
        numberOfCustomer = 10;
    }

    public String getIdSimulation() {
        return idSimulation;
    }

    public void setIdSimulation(String idSimulation) {
        this.idSimulation = idSimulation;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Market getMarket() {
        return market;
    }

    public void setMarket(Market market) {
        this.market = market;
    }

    public SimulatorTask getProgress() {
        return progress;
    }

    public void setProgress(SimulatorTask progress) {
        this.progress = progress;
    }

    public int getNumberOfDays() {
        return numberOfDays;
    }

    public void setNumberOfDays(int numberOfDays) {
        this.numberOfDays = numberOfDays;
    }

    public int getRenewalQuantity() {
        return renewalQuantity;
    }

    public void setRenewalQuantity(int renewalQuantity) {
        this.renewalQuantity = renewalQuantity;
    }

    public int getVolumDemand() {
        return averageVolumDemand;
    }

    public void setVolumDemand(int volumDemand) {
        this.averageVolumDemand = volumDemand;
    }

    public double getStartPrice() {
        return startPrice;
    }

    public void setStartPrice(double startPrice) {
        this.startPrice = startPrice;
    }

    public double getElasticityPercentil() {
        return elasticityPercentil;
    }

    public void setElasticityPercentil(double elasticityPercentil) {
        this.elasticityPercentil = elasticityPercentil;
    }

    public int getNumberOfCustomer() {
        return numberOfCustomer;
    }

    public void setNumberOfCustomer(int numberOfCustomer) {
        this.numberOfCustomer = numberOfCustomer;
    }
    
    public void start() {
        int counter = 0;
        
        do {
            clock.schedule(progress, 0, 2000);
            counter++;
        } while (counter<numberOfDays);
        
        System.out.println("Etat de progrès : " + String.valueOf(progress.cancel()));
    }
    
    private int createCompanies() {
        try {
            String access = "edu-c1";
            Company comp = new Company(access, "Mister Ture A", market);
            this.market.setCompanies(comp);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }
    
    private int createCustomers() {
        //  Création de consommateurs virtuels
        for(int i=0; i<this.numberOfCustomer; i++) {
            market.setCustomers(new Customer(0, "Brewery & Co. " + String.valueOf(i)));
        }
        return 1;
    }
    
    private int createDemands() {
        for(Customer c : market.getCustomers()) {
            for(int i=0; i<numberOfDays;i++) {
                Demand d = new Demand(c,averageVolumDemand,market.getProduct(),false,i);
                c.setNeeds(d);
            }
        }
        return 1;
    }
    
    private int createOffers() {
        return 1;
    }
    
    private int createProduct() {
        market.setProduct(new Product(0, "Pot de miel 1kg", "Miel de fleurs - Haute qualité"));
        return 1;
    }
    
    private void buildingMasterData() throws Exception {
        
    }
    
    public void buildingScenario() throws Exception {
        if(createProduct()<1)
            throw new Exception();
        if(createCompanies()<1)
            throw new Exception();
        if(createOffers()<1)
            throw new Exception();
        if(createCustomers()<1)
            throw new Exception();
        if(createDemands()<1)
            throw new Exception();
        
        for(Company c : market.getCompanies())
            c.masterDataCreation();
    }
    
    public void roundExecution() {
        
        //  Trier les offres par rapport aux prix
        Collections.sort(market.getOffers());
        
        //  Création des itérateurs
        Iterator<Customer> iCustomers = market.getCustomers().listIterator();
        Iterator<Company> iCompanies = market.getCompanies().listIterator();
        
        //  Itération dans les consommateurs
         while (iCustomers.hasNext()) {
             Iterator<Demand> iDemands = iCustomers.next().getNeeds().listIterator();
             while(iDemands.hasNext()) {
                 
             }
         }
    }
}
