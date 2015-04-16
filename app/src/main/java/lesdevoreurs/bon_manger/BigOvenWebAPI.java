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
 * Created by Nicolas on 2015-02-19.
 */
public class BigOvenWebAPI {
    ArrayList<String> IDS;
    ArrayList<String> titres;
    ArrayList<String> cuisines;
    ArrayList<String> categories;
    ArrayList<String> sousCategories;
    ArrayList<String> images;
    ArrayList<String> ratings;
    ArrayList<Drawable> imagesDraw;
    String nbResultats;

    public BigOvenWebAPI(String query, int numPage, String numByPage){

        //Search URL style from BigOven
        String url = "http://api.bigoven.com/recipes?any_kw="+query+"&pg="+numPage+"&rpp="+numByPage+"&api_key=dvxRg7vK4t5RBlTap04zYHqbu08e374G";
        IDS = new ArrayList<String>();
        titres = new ArrayList<String>();
        images = new ArrayList<String>();
        cuisines = new ArrayList<String>();
        categories = new ArrayList<String>();
        sousCategories = new ArrayList<String>();
        ratings = new ArrayList<String>();
        nbResultats = "0";

        //http://openclassrooms.com/courses/structurez-vos-donnees-avec-xml/dom-exemple-d-utilisation-en-java
        //Etape 1 : récupération d'une instance de la classe "DocumentBuilderFactory"
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        try {
            HttpEntity page = getHttp(url);
            String contenu  = EntityUtils.toString(page, HTTP.UTF_8);
            URL urlXML = new URL(url);

            //Etape 2 : création d'un parseur
            final DocumentBuilder builder = factory.newDocumentBuilder();
            //Etape 3 : création d'un Document
            final Document document= builder.parse(url);
            //Etape 4 : récupération de l'Element racine
            final Element racine = document.getDocumentElement();
            //Etape 5 : récupération des recettes
            //final NodeList racineNoeuds = racine.getChildNodes();
            //final int nbRacineNoeuds = racineNoeuds.getLength();
            final Element nbResult = (Element) racine.getElementsByTagName("ResultCount").item(0);
            nbResultats = nbResult.getTextContent();
            final NodeList racineNoeuds = racine.getElementsByTagName("Results");
            //final int nbRacineNoeuds = racineNoeuds.getLength();
            final Element racineNoeudsNoeuds = (Element) racineNoeuds.item(0);
            final NodeList resultNoeuds = racineNoeudsNoeuds.getElementsByTagName("RecipeInfo");
            final int nbResultNoeuds = resultNoeuds.getLength();

            if(nbResultNoeuds != 0) {
                Log.d("WEB", "Nombre de recettes: " + nbResultNoeuds);

                for (int i = 0; i < nbResultNoeuds; i++) {
                    if (resultNoeuds.item(i).getNodeType() == Node.ELEMENT_NODE) {
                        final Element recette = (Element) resultNoeuds.item(i);
                        //Etape 6 : récupération du nom et du prénom
                        final Element id = (Element) recette.getElementsByTagName("RecipeID").item(0);
                        final Element title = (Element) recette.getElementsByTagName("Title").item(0);
                        final Element cuisine = (Element) recette.getElementsByTagName("Cuisine").item(0);
                        final Element categorie = (Element) recette.getElementsByTagName("Category").item(0);
                        final Element souscategorie = (Element) recette.getElementsByTagName("Subcategory").item(0);
                        final Element image = (Element) recette.getElementsByTagName("ImageURL120").item(0);
                        final Element rating = (Element) recette.getElementsByTagName("StarRating").item(0);
                        IDS.add(id.getTextContent());
                        titres.add(title.getTextContent());
                        if (cuisine != null)
                            cuisines.add(cuisine.getTextContent());
                        else
                            cuisines.add("");
                        categories.add(categorie.getTextContent());
                        sousCategories.add(souscategorie.getTextContent());
                        images.add(image.getTextContent().replace("http://redirect.bigoven.com/pics/rs/120/", "http://images.bigoven.com/image/upload/t_recipe-120/"));
                        ratings.add(rating.getTextContent());
                    }
                }
            }
            //Si on n'a aucun résultat
            else {
                IDS.add("");
                titres.add("Nothing found");
                cuisines.add("");
                categories.add("");
                sousCategories.add("");
                images.add("");
                ratings.add("");
            }
        } catch (IOException e) {
            Log.d("Web ", "Erreur: "+e.getMessage());
        } catch (ParserConfigurationException e) {
            Log.d("Parser ", "Erreur: " + e.getMessage());
        } catch (SAXException e) {
            Log.d("SAX ", "Erreur: " + e.getMessage());
        }
    }

    public HttpEntity getHttp(String url) throws ClientProtocolException, IOException{
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