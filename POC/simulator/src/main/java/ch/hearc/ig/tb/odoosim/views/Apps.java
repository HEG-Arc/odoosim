package ch.hearc.ig.tb.odoosim.views;

import ch.hearc.ig.tb.odoosim.simulator.Odoosim;
import static ch.hearc.ig.tb.odoosim.utilities.Utility.*;

public class Apps {

    public static void main(String[] args) {
        try {
            run();
        } catch (Exception e) { System.out.println("Erreur dans le main :\n\r" + e.getCause().toString() + "\n\r" + e.getMessage());}
    }
    
    public static void run() throws Exception {
        
        writeLn(typeOfMessage.INFO, "Démarrage du simulateur");
        
        Odoosim odoosimulator = new Odoosim();
        
        try {
            writeLn(typeOfMessage.INFO, "Configuration des instances");
            odoosimulator.configurationGame();
        } catch (Exception e) { writeLn(typeOfMessage.ERROR, e.getMessage()); }
        try {
            writeLn(typeOfMessage.INFO, "Démarrage du jeu", true);
            odoosimulator.processingGame();
        } catch (Exception e) { writeLn(typeOfMessage.ERROR, e.getMessage()); }
        try {
            writeLn(typeOfMessage.INFO, "Historisation des données générées");
            odoosimulator.persistDataGame();
        } catch (Exception e) { writeLn(typeOfMessage.ERROR, e.getMessage()); }
        
        writeLn(typeOfMessage.INFO, "Simulation terminée ! Aurevoir");
    }
}
