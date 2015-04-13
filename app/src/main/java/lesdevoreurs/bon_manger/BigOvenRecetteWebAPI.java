package lesdevoreurs.bon_manger;

import android.graphics.drawable.Drawable;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Nicolas on 2015-04-12.
 */
public class BigOvenRecetteWebAPI {
    String image = "";
    String ID = "";
    String titre = "";
    String description = "";
    String cuisine = "";
    String categorie = "";
    String sousCategorie = "";
    String ingredientPrimaire = "";
    ArrayList<String> ingredientsNom;
    ArrayList<String> ingredientsQuantite;
    Drawable images;

    public BigOvenRecetteWebAPI(String query) {

        String url = "http://api.bigoven.com/recipe/" + query + "?api_key=dvxRg7vK4t5RBlTap04zYHqbu08e374G";
        ingredientsNom = new ArrayList<String>();
        ingredientsQuantite = new ArrayList<String>();

        //http://openclassrooms.com/courses/structurez-vos-donnees-avec-xml/dom-exemple-d-utilisation-en-java
        //Etape 1 : récupération d'une instance de la classe "DocumentBuilderFactory"
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        try {
            HttpEntity page = getHttp(url);
            String contenu = EntityUtils.toString(page, HTTP.UTF_8);
            URL urlXML = new URL(url);

            //Etape 2 : création d'un parseur
            final DocumentBuilder builder = factory.newDocumentBuilder();
            //Etape 3 : création d'un Document
            final Document document = builder.parse(url);
            //Etape 4 : récupération de l'Element racine
            final Element racine = document.getDocumentElement();
            //Etape 5 : récupération des recettes
            final Element ident = (Element) racine.getElementsByTagName("RecipeID");
            ID = ident.getTextContent();
            final Element title = (Element) racine.getElementsByTagName("Title");
            titre = title.getTextContent();
            final Element desc = (Element) racine.getElementsByTagName("Description");
            if (desc != null)
                description = desc.getTextContent();
            final Element cuis = (Element) racine.getElementsByTagName("Cuisine");
            if (cuis != null)
                cuisine = cuis.getTextContent();
            final Element cat = (Element) racine.getElementsByTagName("Category");
            categorie = cat.getTextContent();
            final Element scat = (Element) racine.getElementsByTagName("Subcategory");
            sousCategorie = scat.getTextContent();
            final Element prim = (Element) racine.getElementsByTagName("PrimaryIngredients");
            ingredientPrimaire = prim.getTextContent();
            final Element im = (Element) racine.getElementsByTagName("ImageURL");
            image = im.getTextContent();

            final NodeList racineNoeuds = racine.getElementsByTagName("Ingredients");
            final Element racineNoeudsNoeuds = (Element) racineNoeuds.item(0);
            final NodeList ingredientNoeuds = racineNoeudsNoeuds.getElementsByTagName("Ingredient");
            final int nbIngredientNoeuds = ingredientNoeuds.getLength();
            if (nbIngredientNoeuds != 0) {
                Log.d("WEB", "Nombre d'ingrédients: " + nbIngredientNoeuds);
                for (int i = 0; i < nbIngredientNoeuds; i++) {
                    if (ingredientNoeuds.item(i).getNodeType() == Node.ELEMENT_NODE) {
                        final Element ingredient = (Element) ingredientNoeuds.item(i);
                        //Etape 6 : récupération du nom et du prénom
                        final Element nom = (Element) ingredient.getElementsByTagName("Name").item(0);
                        ingredientsNom.add(nom.getTextContent());
                        final Element quantite = (Element) ingredient.getElementsByTagName("Title").item(0);
                        ingredientsQuantite.add(quantite.getTextContent());
                    }
                }
            } else {
                ingredientsQuantite.add("");
                ingredientsNom.add("Nothing found");
            }
        } catch (IOException e) {
            Log.d("Web ", "Erreur: " + e.getMessage());
        } catch (ParserConfigurationException e) {
            Log.d("Parser ", "Erreur: " + e.getMessage());
        } catch (SAXException e) {
            Log.d("SAX ", "Erreur: " + e.getMessage());
        }
    }

    public HttpEntity getHttp(String url) throws ClientProtocolException, IOException {
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet http = new HttpGet(url);
        HttpResponse response = httpClient.execute(http);
        return response.getEntity();
    }

    private Drawable loadHttpImage(String url) throws ClientProtocolException, IOException {
        InputStream is = getHttp(url).getContent();
        Drawable d = Drawable.createFromStream(is, "src");
        return d;
    }
}