/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hearc.ig.tb.odoosimulator.simulator;

import java.util.TimerTask;

/**
 *
 * @author tomant
 */
public class SimulatorTask extends TimerTask {

    private int day;
    private Simulator odoosim;

    public SimulatorTask(Simulator odoosim) {
        day = 0;
        this.odoosim = odoosim;
    }
    
    @Override
    public void run() {
        day++;
        System.out.println("Sim : " + odoosim.getIdSimulation() + "\n\rUn jours de plus (" + day + ") !!!");
    }
    
}
