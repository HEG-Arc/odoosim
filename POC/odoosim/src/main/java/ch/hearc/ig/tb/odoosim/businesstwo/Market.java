/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hearc.ig.tb.odoosim.businesstwo;

import java.util.ArrayList;

/**
 *
 * @author tomant
 */
public class Market {
    private String name;
    private Good good;
    private Channel channel;
    private Region region;
    private ArrayList<Need> allNeeds;
    private ArrayList<Offer> allOffers;

    public Market(String name, Good good, Channel channel, Region region) {
        this.name = name;
        this.good = good;
        this.channel = channel;
        this.region = region;
        allNeeds = new ArrayList<>();
        allOffers = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Need> getAllNeeds() {
        return allNeeds;
    }

    public void setAllNeeds(Need need) {
        allNeeds.add(need);
    }

    public ArrayList<Offer> getAllOffers() {
        return allOffers;
    }

    public void setAllOffers(Offer offer) {
        allOffers.add(offer);
    }

    public Good getGood() {
        return good;
    }

    public void setGood(Good good) {
        this.good = good;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
    
}
