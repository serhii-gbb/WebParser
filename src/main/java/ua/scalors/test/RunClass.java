package ua.scalors.test;


import ua.scalors.test.entity.Offer;
import ua.scalors.test.entity.Offers;
import ua.scalors.test.parser.JaxBParser;
import ua.scalors.test.parser.OfferCollector;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.List;

public class RunClass {

    private static final double MS_IN_ONE_SECOND = 1000;
    private static final int BYTE_IN_ONE_KB = 1024;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        Runtime runtime = Runtime.getRuntime();

        String keyWord = args[0];

        final String strUrl = String.format("https://www.aboutyou.de/suche?term=%s&category=20201", keyWord);

        URL url = new URL(strUrl);

        OfferCollector offerCollector = new OfferCollector(url);

        Offers offers = new Offers();
        List<Offer> offersResult = offerCollector.getOffers();
        offers.setOffers(offersResult);

        JaxBParser jaxBParser = new JaxBParser(Offers.class);
        jaxBParser.createXmlFile(offers);


        System.out.println("Amount extracted product: " + offersResult.size());
        System.out.println("Amount http requests: " + offerCollector.getCounHttpRequests());
        System.out.println("Memory footprint: " + ((runtime.totalMemory() - runtime.freeMemory()) / BYTE_IN_ONE_KB)+" KB");
        System.out.println("Runtime: " + (BigDecimal.valueOf((System.currentTimeMillis() - start) / MS_IN_ONE_SECOND)
                .setScale(2, BigDecimal.ROUND_DOWN)) + " sec");
    }
}
