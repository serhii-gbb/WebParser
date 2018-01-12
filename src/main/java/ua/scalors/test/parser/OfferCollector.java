package ua.scalors.test.parser;


import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ua.scalors.test.entity.Clause;
import ua.scalors.test.entity.Description;
import ua.scalors.test.entity.Offer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public class OfferCollector {

    private URL url;
    private final String HOST;
    private static int counHttpRequests;

    public OfferCollector(URL url) {
        this.url = url;
        String PROTOCOL = this.url.getProtocol() + "://";
        HOST = PROTOCOL + this.url.getHost();
    }

    /**
     *   This method provide <{@link Connection}> to the page what is to
     *   the <@param> url </@param>
     *
     * @param url <{@code String}> full link to the needed page
     * @return
     */


    private Connection getConnection(String url){
        Connection connection = Jsoup.connect(url);
        counHttpRequests++;

        return connection;
    }

    /**
     *  This method gives us all offers from all pages upon request of keyword
     *
     * @return @code List<{@link Offer}>
     */

    public List<Offer> getOffers() {
        List<Offer> offersOnAPage = new ArrayList<>();

        Connection connect = getConnection(url.toString());
        Document document = null;
        try {

            document = connect.get();

            Elements offersBlocks = document.getElementsByClass("wrapper_8yay2a");

            if (!offersBlocks.isEmpty()) {
                Element offerBlock = offersBlocks.first();

                Elements elementsByClass = offerBlock.getElementsByClass("col-sm-6 col-md-4");
                if (!elementsByClass.isEmpty()) {
                    for (Element byClass : elementsByClass) {

                        if (byClass.className().equals("col-sm-6 col-md-4")) {

                            Element aOffer = byClass.getElementsByClass("anchor_wgmchy").first();
                            if (aOffer != null) {
                                String offerLink = aOffer.attr("href");
                                String fullLink = HOST.concat(offerLink);
                                offersOnAPage.add(parseOneOffer(fullLink));

                            }

                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (document != null) {
                Elements currentPageLink = document.getElementsByAttributeValue("class", "pageNumbers_ffrt32 active_l7iksl");

                if (!currentPageLink.isEmpty()) {
                    Element existingLink = currentPageLink.first();

                    Element child = existingLink.nextElementSibling().child(0);

                    if (child.hasAttr("href")) {
                        String linkToTheNextPage = child.attr("href");
                        url = new URL(linkToTheNextPage);

                        List<Offer> offers = getOffers();
                        offersOnAPage.addAll(offers);
                    }
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return offersOnAPage;
    }


    /**
     *  This method collect all data from offer page
     *
     * @param fullLink link to the offer page
     * @return <{@link Offer}> entity with all key fields
     */

    private Offer parseOneOffer(String fullLink) {

        Offer offer = new Offer();

        Connection connection = getConnection(fullLink);
        Document document;

        try {
            document = connection.get();

            parseOriginalPrice(offer, document);

            parseOriginalPrice(offer, document);

            parseBrandName(offer, document);

            parseArticle(offer, document);

            parseColor(offer, document);

            parseDescription(offer, document);

            parseShipping(offer, document);


        } catch (IOException e) {
            e.printStackTrace();
        }

        return offer;
    }


    /**
     *  Bellow all methods for parsing every field in <{@link Offer}>
     *
     * @param offer current <{@code Offer}>
     * @param document <{@link Document}> instance for offer page
     */


    private void parseOriginalPrice(Offer offer, Document document) {
        Elements oPriceElements = document.getElementsByAttributeValueMatching("class", "(?i).*(originalPrice).*");
        Pattern pattern = Pattern.compile("[a-z]");

        // Original price
        String originalPrice;
        if (oPriceElements != null && !oPriceElements.isEmpty()) {
            originalPrice = oPriceElements.first().text();

            originalPrice = pattern.matcher(originalPrice).replaceAll("").trim();

            offer.setStartPrice(originalPrice);
        }

    }

    private void parseFinalPrice(Offer offer, Document document){
        Elements fPrice = document.getElementsByAttributeValueMatching("class", "(?i).*(finalPrice).*");

        Pattern pattern = Pattern.compile("[a-z]");
        //Final price
        if (fPrice != null && !fPrice.isEmpty()) {
            String ePrice = fPrice.first().text();

            String s = pattern.matcher(ePrice).replaceAll("").trim();

            offer.setFinalPrice(s);
        }

    }

    private void parseBrandName(Offer offer, Document document) {
        //Brand & Name
        Elements prodNameBrand = document.getElementsByClass("productName_192josg");
        String[] nameBrand = prodNameBrand.text().split("\\|");

        offer.setBrand(nameBrand[0].trim());
        if (nameBrand.length > 1) {
            offer.setName(nameBrand[1].trim());
        }
    }

    private void parseArticle(Offer offer, Document document) {
        //Article
        if (!document.getElementsContainingText("Artikel-Nr:").isEmpty()) {
            Elements select = document.select("div:containsOwn(Artikel-Nr:)");
            if (select != null && !select.isEmpty()) {
                String text = select.first().text();
                String article = text.split("Artikel-Nr:")[1].trim();

                offer.setArticle(article);
            } else {
                select = document.select("p:containsOwn(Artikel-Nr:)");

                String text = select.first().text();
                String article = text.split("Artikel-Nr:")[1].trim();

                offer.setArticle(article);
            }
        }

    }

    private void parseColor(Offer offer, Document document) {
        //Color
        String data = document.data();
        String[] split1 = data.split("\"color\":\"");
        String[] split2 = split1[1].split("\\s |[\"]");
        String color = split2[0].replace("u002F", "").replace(" ", "");

        offer.setColor(color);
    }

    private void parseDescription(Offer offer, Document document) {

        Description description = new Description();
        description.setClauses(new ArrayList<>());

        Element descrBlock = document.getElementsByClass("wrapper_1w5lv0w").first();

        for (Element redLine : descrBlock.getElementsByClass("subline_19eqe01")) {
            Clause clause = new Clause(redLine.text(), redLine.nextElementSibling().text());

            description.getClauses().add(clause);
        }

        offer.setDescription(description);
    }


    private void parseShipping(Offer offer, Document document) {
        Elements elements = document.getElementsByClass("headline_1crhtoo");
        Element element = elements.get(1);
        offer.setShipping(element.text());
    }


    /**
     *  Give us amount of Http request during all parsing session
     *
     * @return static <field>counHttpRequests</field>
     */

    public int getCounHttpRequests() {
        return counHttpRequests;
    }
}


