package ch.hearc.ig.tb.odoosimulator.view;

import ch.hearc.ig.tb.odoosimulator.simulator.Simulator;
import java.util.Scanner;

public class Apps {
    
    private final static Scanner reader = new Scanner(System.in);
    private static Simulator odoosim;

    public static void main(String[] args) {
        
        //  Création du simulateur
        odoosim = new Simulator();
        //  Afficher le menu de l'application
        displayMenu("Bienvenue sur le simulateur OdooSIM");
        
    }
    
    public static void displayMenu(String message) {
        if(!message.equals(""))
            System.out.println("\n\r[INFO] " + message);
        
        System.out.println("- Menu principal du simulateur -");
        System.out.println("Vous pouvez gérer l'initialisation d'une nouvelle"
                + " partie et ses paramétres à partir d'ici\n\rOptions disponibles :");
        
        System.out.println("[1] Gestion de l'approvisionnement");
        System.out.println("[2] Gestion de la tarification");
        System.out.println("[3] Gestion des équipes");
        System.out.println("[4] Gestion de la demande");
        System.out.println("[5] Gestion de l'elasticité demande-prix");
        
        System.out.println("[0] Démarrage d'une nouvelle partie");
        System.out.println("[?] Affichage de l'aide");
        System.out.println("[X] Fin du programme");
        action(readChoice());
    }
    
    public static String readChoice() {
        return String.valueOf(reader.nextLine().charAt(0)).toUpperCase();
    }
    
    public static void action(String choice) {
        try {
            switch(choice) {
                case "1": handlePurchase();
                break;
                case "2": handlePrice();
                break;
                case "3": displayMenu("Non-implémentée!");
                break;
                case "4": handleDemand();
                break;
                case "5": handleElasticity();
                break;
                case "0": odoosim.buildingScenario();
                    displayMenu("Scénario créé!");
                break;
                case "?": displayHelp();
                break;
                case "X": System.out.println("Arrêt du simulateur...");
                break;
                default : displayMenu("L'option que vous venez de choisir n'en n'est pas une :-)");
                break;
            }
        } catch (Exception e) {
            displayMenu("Erreur : " + e.getMessage());
        }
    }
    
    private static void handleElasticity() {
        System.out.println("\n\r");
        System.out.println("L'élasticité actuelle : " + String.valueOf(odoosim.getElasticityPercentil()) + "%");
        System.out.println("Nouvelle valeur (de 0.00 à 100.00) : ");
        try {
            String newValue = reader.nextLine();
            double nV = Double.valueOf(newValue);
            if(!(nV<=100 & nV>0))
                throw new NumberFormatException();
            odoosim.setElasticityPercentil(nV);
        } catch (NumberFormatException e) {
            displayMenu("La valeur entrée n'est pas une valeur acceptable!");
            
        } finally {
            displayMenu("La valeur " + odoosim.getElasticityPercentil() + "% a bien été définie!");
        }
    }
    
    private static void handlePurchase() {
        System.out.println("\n\r");
        System.out.println("L'approvisionnement actuel est de : " + String.valueOf(odoosim.getRenewalQuantity()) + " unité(s)/jour");
        System.out.println("Nouvelle valeur : ");
        try {
            String newValue = reader.nextLine();
            int nV = Integer.valueOf(newValue);
            if(nV<0)
                throw new NumberFormatException();
            odoosim.setRenewalQuantity(nV);
        } catch (NumberFormatException e) {
            displayMenu("La valeur entrée n'est pas une valeur acceptable!");
        } finally {
            displayMenu("La valeur " + odoosim.getRenewalQuantity() + " unité(s) a bien été définie!");
        }
    }
    
    private static void handlePrice() {
        System.out.println("\n\r");
        System.out.println("La tarification actuelle est fixée à : " + String.valueOf(odoosim.getStartPrice()) + " CHF");
        System.out.println("Nouvelle valeur : ");
        try {
            String newValue = reader.nextLine();
            double nV = Double.valueOf(newValue);
            if(nV<0)
                throw new NumberFormatException();
            odoosim.setStartPrice(nV);
        } catch (NumberFormatException e) {
            displayMenu("La valeur entrée n'est pas une valeur acceptable!");
        } finally {
            displayMenu("La valeur " + odoosim.getStartPrice() + " CHF a bien été définie!");
        }
    }
    
    
    private static void handleDemand() {
        System.out.println("\n\r");
        System.out.println("Le volume de demande est fixé à : " + String.valueOf(odoosim.getVolumDemand()) + " par jour");
        System.out.println("Nouvelle valeur : ");
        try {
            String newValue = reader.nextLine();
            int nV = Integer.valueOf(newValue);
            if(nV<0)
                throw new NumberFormatException();
            odoosim.setVolumDemand(nV);
        } catch (NumberFormatException e) {
            displayMenu("La valeur entrée n'est pas une valeur acceptable!");
        } finally {
            displayMenu("La valeur " + odoosim.getVolumDemand() + " a bien été définie en tant que volume quotidien!");
        }
    }
    
    private static void displayHelp() {
        System.out.println("\n\r");
        System.out.println("Rubrique d'aide");
        System.out.println("Cette version du simulateur est une preuve de concept "
                + "(POC) destiné à démontrer qu'il est réalisable de créer un "
                + "jeu sérieux grâce de pilotage d'entreprise grâce à Odoo. "
                + "L'idée de ce cours scénario est de présenter l'impact d'une nouvelle "
                + "tarification sur la demande des consommateurs.");
        
        System.out.println("\n\rPour lancer une nouvelle partie, vous devez paramétrer plusieurs "
                + "chose : \n\rLa quantité quotidiennement produite par les sociétés (option 1)");
        System.out.println("\n\rLe prix auquel les consommateurs achètent la quantité (option 2)");
        System.out.println("\n\rLes équipes prenant part à la partie (option 3)");
        System.out.println("\n\rLe volume de demande quotidienne (option 4)");
        System.out.println("\n\rL'élasticité prix-demande. i.e., l'impact qu'a le "
                + "changement de prix sur la quantité demandée (option 5)");
        System.out.println("\n\rPour toutes autres questions, merci de contacter anthony.tomat@he-arc.ch");
        displayMenu("");
    }
}
