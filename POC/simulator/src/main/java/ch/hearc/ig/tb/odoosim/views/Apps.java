package ch.hearc.ig.tb.odoosim.views;

import ch.hearc.ig.tb.odoosim.simulator.Odoosim;
import static ch.hearc.ig.tb.odoosim.utilities.Utility.*;
import java.util.Scanner;

public class Apps {
    private final static Scanner reader = new Scanner(System.in);   
    public static void main(String[] args) {
        try {
            run();
        } catch (Exception e) { System.out.println("Erreur dans le main :\n\r" + e.getCause().toString() + "\n\r" + e.getMessage());}
    }
    
    public static void run() throws Exception {
        
        Odoosim odoosimulator = new Odoosim();
        
        try {
            writeLn(typeOfMessage.INFO, "'Y' pour lancer la phase de configuration 1 sur 3");
            while(!action(readChoice())) {
                writeLn(typeOfMessage.ERROR, "Mauvaise saisie! Veuillez entrer 'Y' pour continuer!");
            }
            writeLn(typeOfMessage.INFO, "(1/3) Début de la configuration, veuillez patienter...");
            odoosimulator.configurationGame();
        } catch (Exception e) { writeLn(typeOfMessage.ERROR, e.getMessage()); }
        try {
            writeLn(typeOfMessage.INFO, "'Y' pour lancer la phase de simulation 2 sur 3");
            while(!action(readChoice())) {
                writeLn(typeOfMessage.ERROR, "Mauvaise saisie! Veuillez entrer 'Y' pour continuer!");
            }
            writeLn(typeOfMessage.INFO, "(2/3) Début de la simulation...");
            odoosimulator.processingGame();
        } catch (Exception e) { writeLn(typeOfMessage.ERROR, e.getMessage()); }
        try {
            writeLn(typeOfMessage.INFO, "(3/3) Calcul des résultats finaux");
            odoosimulator.persistDataGame();
        } catch (Exception e) { writeLn(typeOfMessage.ERROR, e.getMessage()); }
        
        writeLn(typeOfMessage.INFO, "Simulation terminée ! Aurevoir");
    }
    
    public static String readChoice() {
        return String.valueOf(reader.nextLine().charAt(0)).toUpperCase();
    }
    
    public static Boolean action(String choice) {
        return "Y".equals(choice);
    }
    
}
