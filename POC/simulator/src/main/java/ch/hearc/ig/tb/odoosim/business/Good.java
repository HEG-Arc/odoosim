package ch.hearc.ig.tb.odoosim.business;

import ch.hearc.ig.tb.odoosim.saasinterfacing.Odoo;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Good extends Product {
    
    private Collection<Offer> vendors;
    private Collection<Demand> demands;
    private Double indicativeSalePrice;
    private Double customerBestPrice;
    private Map<Integer, Double> billOfMaterials;
    
    public Good(String name) {
        super(name);
    }

    public Good(Double indicativeSalePrice, String code, String name, Double purchasePrice, Double customerBestPrice) {
        super(code, name, purchasePrice);
        this.vendors = new ArrayList<>();
        this.indicativeSalePrice = indicativeSalePrice;
        this.billOfMaterials = new HashMap();
        this.demands = new ArrayList<>();
        this.customerBestPrice = customerBestPrice;
    }
    
    public Collection<Demand> getDemands() {
        return demands;
    }
    
    public void setDemands(Collection<Demand> demands) {
        
    }
    
    public Collection<Offer> getVendors() {
        return vendors;
    }

    public void setVendors(Collection<Offer> vendors) {
        this.vendors = vendors;
    }

    public Double getIndicativeSalePrice() {
        return indicativeSalePrice;
    }

    public void setIndicativeSalePrice(Double indicativeSalePrice) {
        this.indicativeSalePrice = indicativeSalePrice;
    }

    public Map getBillOfMaterials() {
        return billOfMaterials;
    }

    public void setBillOfMaterials(HashMap billOfMaterials) {
        this.billOfMaterials = billOfMaterials;
    }
    
    public Double getCustomerBestPrice() {
        return customerBestPrice;
    }
    
    public void setCustomerBestPrice(Double customerBestPrice) {
        this.customerBestPrice = customerBestPrice;
    }
    
    public void addBOM(int idProduct, Double quantity) {
        this.billOfMaterials.put(idProduct, quantity);
    }
    
    public void addOffer(Offer offer) {
        vendors.add(offer);
    }
    
    public void addDemand(Demand demand) {
        demands.add(demand);
    }
    
    public void oadApplication(Odoo wsapi, String date) throws Exception {
        Collections.shuffle((List<?>) demands);
        for (Demand demand : demands) {
            Collections.shuffle((List<?>) vendors);
            Collections.sort((List<Offer>) vendors);
            int numberOffers = vendors.size();
            //  Avec cette ligne, il faudrait faire que la méthode getMarketAvailability puisse définir le prix maximum auquel le client est d'accord
            //  d'acheter... Mais cela ne fonctionne pas encore !
            
            //  Comme cela, je fais un si prix de l'offre est plus grand que prix max alors je "break" directement la boucle
            //  for du haut sans faire d'autre itération => GAIN EN Rapidité!!!
            
            //while (d.getQuantity() > 1 && good.getMarketAvailability(d.getProduct().getIndicativeSalePrice()) > 1 || d.getQuantity() < 1) {
            while (demand.getQuantity() > 1 && getMarketAvailability()>1 ) {
                //  Si nous sommes ici c'est qu'il y a encore des produits sur le marché et que notre demande n'est pas satisfaite !
                for (Offer offer : vendors) {
                    if(offer.getQuantity()>0) {
                        Double qty = demand.getOwner().getAdjustedQuantity(demand, offer);
                        if(qty>1) {
                            if (qty > offer.getQuantity()) {
                                
                                Exchange ex = new Exchange(offer.getQuantity(), offer.getPrice(), demand, 
                                        offer, this, date);
                                //demand.getOwner().addExchange(ex);
                                demand.setQuantity(demand.getQuantity() - offer.getQuantity());
                                offer.setQuantity(offer.getQuantity() - offer.getQuantity());
                                //offer.getOwner().processSale(wsapi, ex);   
                                offer.getOwner().processQuickSale2(wsapi, ex);  
                            } else if (qty <= offer.getQuantity()) {
                                
                                Exchange ex = new Exchange(offer.getQuantity(), offer.getPrice(), demand, 
                                        offer, this, date);
                                //demand.getOwner().addExchange(ex);
                                //  Je met à 0.0 car vu que l'offre est triée sur le prix, cela n'aurait pas de sens
                                //  d'aller voir chez un autre fournisseur qui sera de toute manière plus chère.
                                //  Dans ce cas là (si on se trouve dans le else if alors c'est que l'effet de l'élasticité
                                //  a contraint le consommateur à faire un trait sur une partie de ses besoins.
                                demand.setQuantity(0.0);
                                offer.setQuantity(offer.getQuantity() - qty);
                                offer.getOwner().processQuickSale2(wsapi, ex); 
                                break;
                            }
                        } else {
                            demand.setQuantity(0.0);
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
            break;
        }
    }
    
    public Double getMarketAvailability() {
        Double available = 0.0;
        for(Offer o : vendors)
            available+=o.getQuantity();
        return available;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.getName());
    }
    
    @Override
    public boolean equals(Object good) {
        if(this == good)
            return true;
        if(this.getClass() != good.getClass())
            return false;
        
        Good gd = (Good) good;
        if((Objects.equals(this.getName(), gd.getName())) || (Objects.equals(this.getCode(), gd.getCode())))
            return true;
        else
            return false;
    }
}
