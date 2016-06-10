package ch.hearc.ig.tb.odoosim.business;

import ch.hearc.ig.tb.odoosim.saasinterfacing.Odoo;
import java.util.*;

public class Retailer {

    protected int id;
    protected String name;
    protected Collection<Demand> demands;
    protected Collection<Good> productsPreferences;
    protected Collection<Exchange> exchanges;
    protected Double marketPart;
    protected Double elasticity;
    protected int dayToPay;
    protected Area localisation;
    protected String type;

    public Retailer(String name, Double marketPart, Double elasticity, int toPay, int untilPay, String type) {
        this.name = name;
        this.marketPart = marketPart;
        this.elasticity = elasticity;
        this.type = type;
        dayToPay = whenPaid(toPay, untilPay);
        demands = new ArrayList<>();
        productsPreferences = new ArrayList<>();
        exchanges = new ArrayList<>();
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addDemand(Demand d) {
        d.setOwner(this);
        this.demands.add(d);
    }

    private int whenPaid(int start, int stop) {
        return 0;
    }

    public Collection<Demand> getDemands() {
        return demands;
    }

    public void setDemands(Collection<Demand> demands) {
        this.demands = demands;
    }

    public Collection<Good> getProductsPreferences() {
        return productsPreferences;
    }

    public void setProductsPreferences(Collection<Good> productsPreferences) {
        this.productsPreferences = productsPreferences;
    }

    public Double getMarketPart() {
        return marketPart;
    }

    public void setMarketPart(Double marketPart) {
        this.marketPart = marketPart;
    }

    public Double getElasticity() {
        return elasticity;
    }

    public void setElasticity(Double elasticity) {
        this.elasticity = elasticity;
    }

    public int getDayToPay() {
        return dayToPay;
    }

    public void setDayToPay(int dayToPay) {
        this.dayToPay = dayToPay;
    }

    public Area getLocalisation() {
        return localisation;
    }

    public void setLocalisation(Area localisation) {
        this.localisation = localisation;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void addPreference(Good p) {
        this.productsPreferences.add(p);
    }

    public void buy(Odoo wsapi, int day, int month) throws Exception {

        for (Demand d : this.demands) {
            Good good = d.getProduct();
            Collections.sort((List<Offer>) good.getVendors());
            int numberOffers = good.getVendors().size();
            //  Avec cette ligne, il faudrait faire que la méthode getMarketAvailability puisse définir le prix maximum auquel le client est d'accord
            //  d'acheter... Mais cela ne fonctionne pas encore !
            //while (d.getQuantity() > 1 && good.getMarketAvailability(d.getProduct().getIndicativeSalePrice()) > 1 || d.getQuantity() < 1) {
            while (d.getQuantity() > 1 && good.getMarketAvailability()>1 ) {
                //  Si nous sommes ici c'est qu'il y a encore des produits sur le marché et que notre demande n'est pas satisfaite !
                for (Offer o : good.getVendors()) {
                    if(o.getQuantity()>0) {
                        Double qty = this.getAdjustedQuantity(d, o);
                        if(qty>1) {
                            if (qty > o.getQuantity()) {
                                System.out.println(o.getOwner().getName() + " - " + d.getOwner().getName());
                                System.out.println("Quantité demandée : " + qty);
                                System.out.println("Quantité disponible : " + o.getQuantity());
                                
                                Exchange ex = new Exchange(day, o.getQuantity(), this, o.getOwner(), o.getProduct());
                                this.addExchange(ex);
                                d.setQuantity(d.getQuantity() - o.getQuantity());
                                o.setQuantity(o.getQuantity() - o.getQuantity());
                                o.getOwner().registerSale(wsapi, ex, day, month, o.getPrice());
                                System.out.println("Echange de : " + o.getQuantity());
                                
                            } else if (qty <= o.getQuantity()) {
                                
                                System.out.println(o.getOwner().getName() + " - " + d.getOwner().getName());
                                System.out.println("Quantité demandée : " + qty);
                                System.out.println("Quantité disponible : " + o.getQuantity());
                                
                                Exchange ex = new Exchange(day, qty, this, o.getOwner(), o.getProduct());
                                this.addExchange(ex);
                                //  Je met à 0.0 car vu que l'offre est triée sur le prix, cela n'aurait pas de sens
                                //  d'aller voir chez un autre fournisseur qui sera de toute manière plus chère.
                                //  Dans ce cas là (si on se trouve dans le else if alors c'est que l'effet de l'élasticité
                                //  a contraint le consommateur à faire un trait sur une partie de ses besoins.
                                d.setQuantity(0.0);
                                o.setQuantity(o.getQuantity() - qty);
                                o.getOwner().registerSale(wsapi, ex, day, month, o.getPrice());
                                System.out.println("Echange de : " + qty);
                                
                                break;
                            }
                        } else {
                            d.setQuantity(0.0);
                            break;
                        }
                    }
                }
                //  Ce test est mis en place si jamais la boucle tourne à l'infinie.
                //  C'est possible si les prix sont aussi haut que rapporté à l'élasticité
                //  la quantité définie est zéro -> Donc pas d'achat par contre, dans la condition
                //  du while, la valeur de la quantité disponible sur le marché serait toujours plus que 1
                //  donc sans cela, impossible d'en sortir !
                if(--numberOffers<1)
                    break;
            }
        }
    }

    public void addExchange(Exchange ex) {
        Company c = ex.getVendor();
        c.addExchange(ex);
        exchanges.add(ex);
    }

    private Double getAdjustedQuantity(Demand d, Offer o) {

        if (elasticity > 0) {
            elasticity = elasticity * -1;
        }

        Double optimum = d.getProduct().getIndicativeSalePrice();
        Double offerPrice = o.getPrice();

        Double variationPrice = (offerPrice - optimum) / optimum;

        Double variationQuantity = variationPrice * elasticity;

        Double resultat = (variationQuantity / 100) * d.getQuantity() + d.getQuantity();

        return (double) Math.round(resultat);
    }

}
