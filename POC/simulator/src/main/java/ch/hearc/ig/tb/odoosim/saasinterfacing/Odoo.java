package ch.hearc.ig.tb.odoosim.saasinterfacing;

import java.net.URL;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public class Odoo {
    /* Les méthodes "getID", "getData", "searchID", "searchAccountByCode" font la même
    chose mais il faut essayer de les remplacer par la méthode getData ou getID qui sont plus optimisée!
    */
    private String protocol;
    private String dns;
    private XmlRpcClientConfigImpl configuration;
    private XmlRpcClient common;
    private XmlRpcClient object;

    public Odoo(String protocol, String dns) {
        this.protocol = protocol;
        this.dns = dns;
        configuration = new XmlRpcClientConfigImpl();
        common = new XmlRpcClient();
        object = new XmlRpcClient();
    }

    /**
     * Cette méthode récupère l'identifiant du couple login/password avec lequel
     * il est possible d'accèder au point d'entré "object". C'est ce point
     * d'entré qui permet de requêter sur le modèle object Odoo.
     *
     * @param database La base de données sur laquelle nous voulons effectuer la
     * récupération.
     * @param account Le compte, i.e., l'adresse e-mail valide avec laquelle on
     * se connecte sur l'interface web pour créer les bases de données.
     * @param password Le mot de passe du compte d'administration de l'instance
     * @return Elle retourne l'uid qui ouvre l'accès au requête sur le point de
     * terminaison "object".
     * @throws Exception
     */
    public int getUID(String database, String account, String password) throws Exception {
        setConfiguration(1, database);
        return (int) common.execute(configuration, "authenticate", asList(database, account, password, emptyMap()));
    }

    /**
     * Cherche dans Odoo l'existance ou non d'un enregistrement selon des
     * critères de recherche et retourne l'ID ou -1 si rien n'est existant
     *
     * @param database L'instance requêtée
     * @param model Le modèle i.e. l'objet Odoo (Exemple : res.partner)
     * @param uid L'uid permettant de s'authentifier
     * @param password Le mot de passe du compte d'administration de l'instance
     * @param criteria Une liste de critère pour affiner la recherche
     * @return une valeur supérieur à 0 si un élément est trouvé sinon -1
     */
    public int getID(String database, String model, int uid, String password, List criteria) {
        try {
            setConfiguration(2, database);
            object.setConfig(configuration);
            return (int) asList((Object[]) object.execute(
                    "execute_kw", asList(
                            database, uid, password,
                            model, "search",
                            asList(criteria)))).get(0);
        } catch (Exception e) {
            //  En faisant le .get(0), si la requête ne retourne aucun resultat
            //  alors le get(0) fera une erreur car la liste sera vide! Donc on retourne le -1
            return -1;
        }
    }

    /**
     * Cette méthode va lire les données dans Odoo et récupérer les enregistrements qui correspondes
     * aux critères (s'il y en a). Cette méthode donne aussi la possiblité de renseigner les champs que 
     * l'ont veut en retour.
     * Exemple d'appel : uneInstanceOdoo.getData(database, uid, password, model, asList(asList("attribut", "opérateur", "valeur"),
     * asList("attribut2", "opérateur", "valeur"), ...), new HashMap() {{ put("fields", asList("field1", "field2", "field3"));}});
     * A savoir : le asList et HashMap peuvent être remplacés par Collections.emptyList() ou Collections.emptyMap()
     * @param database L'instance requêtée
     * @param uid L'uid permettant de s'authentifier
     * @param password Le mot de passe du compte d'administration de l'instance
     * @param model Le modèle i.e. l'objet Odoo (Exemple : res.partner)
     * @param criteria Une liste de critère pour affiner la recherche
     * @param fields Une liste de champ à récupérer.
     * @return Une liste d'enregistrements et leurs attributs
     */
    public List getData(String database, int uid, String password, String model, List criteria, HashMap fields) {
        try {
            setConfiguration(2, database);
            object.setConfig(configuration);
            
            List<Object> asList = asList((Object[]) object.execute("execute_kw", asList(
                    database, uid, password,
                    model, "search_read",
                    asList(criteria), fields)));
            
            
            if(asList.size()<1)
                return asList(-1);
            
            return asList;
            

        } catch (Exception e) {
            return asList(-1);
        }
    }
    
    public Object getTuple(String database, int uid, String password, String model, List criteria, HashMap fields) {
        try {
            setConfiguration(2, database);
            object.setConfig(configuration);
            
            Object[] asList = (Object[]) object.execute("execute_kw", asList(
                    database, uid, password,
                    model, "search_read",
                    asList(criteria), fields));
            System.out.println("test");
            
            if(asList.length>0)
                return asList[0];
            else
                return -1;
                
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Fonction de mise à jour d'enregistrement dans Odoo
     *
     * @param database L'instance requêtée
     * @param model Le modèle i.e. l'objet Odoo (Exemple : res.partner)
     * @param uid L'uid permettant de s'authentifier
     * @param password Le mot de passe du compte d'administration de l'instance
     * @param tupleId L'enregistrement à mettre à jour (identifié grâce à son
     * ID)
     * @param fields Les champs impactés par la modification
     * @throws Exception
     */
    public void update(String database, String model, int uid, String password, int tupleId, HashMap fields) throws Exception {
        setConfiguration(2, database);
        object.setConfig(configuration);
        object.execute("execute_kw", asList(database, uid, password, model, "write", asList(asList(tupleId), fields)));
    }

    /**
     * Fonction d'ajout d'élément dans Odoo
     *
     * @param database L'instance requêtée
     * @param model Le modèle i.e. l'objet Odoo (Exemple : res.partner)
     * @param data Les données à insérer
     * @param uid L'uid permettant de s'authentifier
     * @param password Le mot de passe du compte d'administration de l'instance
     * @return
     * @throws Exception
     */
    public int insert(String database, String model, HashMap data, int uid, String password) throws Exception {
        setConfiguration(2, database);
        object.setConfig(configuration);
        return (int) object.execute("execute_kw",
                asList(database, uid, password, model, "create",
                        asList(data)));
    }

    /**
     *
     * @param database L'instance requêtée
     * @param uid L'uid permettant de s'authentifier
     * @param password Le mot de passe du compte d'administration de l'instance
     * @param model Le modèle i.e. l'objet Odoo (Exemple : res.partner)
     * @param nextStep Prochaine étape dans le flux
     * @param idObject Identifiant permettant d'identifier l'objet à faire
     * évoluer
     * @throws Exception
     */
    public void workflowProgress(String database, int uid, String password, String model, String nextStep, int idObject) throws Exception {
        setConfiguration(2, database);
        object.setConfig(configuration);
        object.execute(
                "exec_workflow", asList(
                        database, uid, password,
                        model, nextStep, idObject));
    }

    /**
     * Cette méthode permet de visualiser la structure d'un objet (ses
     * attributs). Elle est très utile pour comprendre comment se compose un
     * objet.
     *
     * @param database L'instance requêtée
     * @param uid L'uid permettant de s'authentifier
     * @param password Le mot de passe du compte d'administration de l'instance
     * @param model Le modèle i.e. l'objet Odoo (Exemple : res.partner)
     * @param fields s'utilise afin de sélectionner les champs que l'on veut
     * voir apparaître dans le résultat. Lorsqu'on invoque cette méthode à
     * partir d'un client, on peut soit renseigner ce paramètre
     * "asList(asList("champs 1", "champs 2", "...", ...,)) ou sinon pour tout
     * afficher on passe en paramètre "Collections.emptyList()".
     * @return
     * @throws Exception
     */
    public Map<String, Map<String, Object>> viewModel(String database, int uid, String password, String model, List fields) throws Exception {
        setConfiguration(2, database);
        object.setConfig(configuration);
        return (Map<String, Map<String, Object>>) object.execute("execute_kw", asList(
                database, uid, password,
                model, "fields_get",
                fields,
                new HashMap() {
            {
                put("attributes", asList("string", "help", "type"));
            }
        }
        ));
    }

    private void setConfiguration(int type, String database) throws Exception {
        switch (type) {
            case 1:
                configuration.setServerURL(new URL(String.format("%s/xmlrpc/2/common", protocol + "://" + database + "." + dns)));
                break;
            case 2:
                configuration.setServerURL(new URL(String.format("%s/xmlrpc/2/object", protocol + "://" + database + "." + dns)));
                break;
            default:
                throw new Exception("Le type passé n'est pas valide");
        }
    }
}
