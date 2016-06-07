package ch.hearc.ig.tb.odoosim.views;

import ch.hearc.ig.tb.odoosim.services.Odoosim;
import static ch.hearc.ig.tb.odoosim.utils.Utility.*;

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
            odoosimulator.configurationGame();
        } catch (Exception e) { writeLn(typeOfMessage.ERROR, e.getMessage()); }
        try {
            odoosimulator.processingGame();
        } catch (Exception e) { writeLn(typeOfMessage.ERROR, e.getMessage()); }
        try {
            odoosimulator.persistDataGame();
        } catch (Exception e) { writeLn(typeOfMessage.ERROR, e.getMessage()); }
        
        writeLn(typeOfMessage.INFO, "Fin de la simulation");
    }
}
