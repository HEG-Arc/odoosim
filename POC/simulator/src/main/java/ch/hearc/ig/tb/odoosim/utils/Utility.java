package ch.hearc.ig.tb.odoosim.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Utility {
    
    public static List<String> getDataXML(org.w3c.dom.Document xmlDocument, String request) throws Exception {
        List<String> result = new ArrayList();
        XPath xPath = XPathFactory.newInstance().newXPath();
        NodeList nodeList = (NodeList) xPath.compile(request).evaluate(xmlDocument, XPathConstants.NODESET);
        Node node = (Node) nodeList.item(0);
        if (node != null && !node.getTextContent().isEmpty()) result = Arrays.asList(node.getTextContent().split("::"));
        return result;
    }
    
    public static List<String> getContentsListOfSelectItem(org.w3c.dom.Document xmlDocument, String request) throws Exception {
        List<String> result = new ArrayList();
        XPath xPath = XPathFactory.newInstance().newXPath();
        NodeList nodeList = (NodeList) xPath.compile(request).evaluate(xmlDocument, XPathConstants.NODESET);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = (Node) nodeList.item(i);
            result.add(node.getTextContent());
        }
        return result;
    }
    
    public static void writeLn(typeOfMessage type, Object message) {
        try {
            if(message==null)
                message = "Inconnu";
            else {
                if(!(message instanceof String))
                    message = message.toString();
            }
        
        System.out.println(type + "" + message);
        } catch (Exception e) {
            System.out.println(typeOfMessage.ERROR + e.getMessage());
        }
    }
    
    public static void parseResult(List<String> resultats) throws Exception {
        writeLn(typeOfMessage.INFO, "---->[size=" + resultats.size() + "]");
        Iterator<String> iR = resultats.iterator();
         while (iR.hasNext()) {
            writeLn(typeOfMessage.INFO, iR.next());
        }
    }
    
    public static Document getXMLConfiguration() throws Exception {
        //  Chemin vers le XML
        String path = "settings.xml";
        
        File settings = new File(path);

        //  Est-ce que le fichier est lisible?
        if (!settings.canRead())
            throw new Exception("Le fichier de paramétre n'est pas lisible");
        
        //  Récupération du contenu XML
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document doc = builder.parse(settings);
        return doc;
    }
    
    public enum typeOfMessage {
        ERROR("Message d'erreur : "),
        INFO(""),
        WARNING("Attention : ");
        
        private String format;
        
        private typeOfMessage(String format) {
            this.format = format;
        }
        
        @Override
        public String toString() {
            return format;
        }
    }
}
