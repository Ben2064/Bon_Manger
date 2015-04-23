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
public class BigOvenRecipeWebAPI {
    public Drawable image = null;
    public String imagePath = "";
    public String ID = "";
    public String titre = "";
    public String description = "";
    public String cuisine = "";
    public String categorie = "";
    public String sousCategorie = "";
    public String ingredientPrimaire = "";
    public String instructions = "";
    public String tempsTotal = "0";
    public String tempsCuisson = "0";
    public ArrayList<String> ingredientsNom;
    public ArrayList<String> ingredientsQuantite;
    public ArrayList<String> ingredientsMetric;

    public BigOvenRecipeWebAPI(String query) {

        String url = "http://api.bigoven.com/recipe/" + query + "?api_key=dvxRg7vK4t5RBlTap04zYHqbu08e374G";
        ingredientsNom = new ArrayList<String>();
        ingredientsQuantite = new ArrayList<String>();
        ingredientsMetric = new ArrayList<String>();

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
            final Element id = (Element) racine.getElementsByTagName("RecipeID").item(0);
            ID = id.getTextContent();
            final Element title = (Element) racine.getElementsByTagName("Title").item(0);
            titre = title.getTextContent();
            final Element desc = (Element) racine.getElementsByTagName("Description").item(0);
            if (desc != null)
                description = desc.getTextContent();
            if (description == "")
                description = "No description";
            final Element cuis = (Element) racine.getElementsByTagName("Cuisine").item(0);
            if (cuis != null)
                cuisine = cuis.getTextContent();
            final Element cat = (Element) racine.getElementsByTagName("Category").item(0);
            if (cat != null)
                categorie = cat.getTextContent();
            final Element scat = (Element) racine.getElementsByTagName("Subcategory").item(0);
            if (scat != null)
                sousCategorie = scat.getTextContent();
            final Element prim = (Element) racine.getElementsByTagName("PrimaryIngredients").item(0);
            if (prim != null)
                ingredientPrimaire = prim.getTextContent();
            final Element im = (Element) racine.getElementsByTagName("HeroPhotoUrl").item(0);
            if (im != null) {
                Log.d("test", im.getTextContent());
                imagePath = im.getTextContent().replace("http://redirect.bigoven.com/pics/rs/640/",
                        "http://images.bigoven.com/image/upload/t_recipe-256/")
                        .replace("http://images.bigoven.com/image/upload/", "http://images.bigoven.com/image/upload/t_recipe-256/")
                        .replace(" ", "");
                Log.d("test", imagePath);
                image = loadHttpImage(imagePath);
            } else
                imagePath = "http://images.bigoven.com/image/upload/t_recipe-256/recipe-no-image.jpg";
            image = loadHttpImage(imagePath);
            final Element inst = (Element) racine.getElementsByTagName("Instructions").item(0);
            if (inst != null)
                instructions = inst.getTextContent();
            final Element tt = (Element) racine.getElementsByTagName("TotalMinutes").item(0);
            if (tt != null) {
                if (tt.getTextContent().length() > 0)  //For weird things in API
                    tempsTotal = tt.getTextContent();
            }
            final Element tc = (Element) racine.getElementsByTagName("ActiveMinutes").item(0);
            if (tc != null) {
                if (tc.getTextContent().length() > 0)   //For weird things in API
                    tempsCuisson = tc.getTextContent();
            } else
                tempsCuisson = "0";

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
                        final Element quantite = (Element) ingredient.getElementsByTagName("MetricDisplayQuantity").item(0);
                        if (quantite != null)
                            ingredientsQuantite.add(quantite.getTextContent());
                        final Element metric = (Element) ingredient.getElementsByTagName("MetricUnit").item(0);
                        if (metric != null)
                            ingredientsMetric.add(metric.getTextContent());
                        else
                            ingredientsMetric.add("");
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