package ua.scalors.test.parser;


import ua.scalors.test.entity.Offers;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;

public class JaxBParser {

    private final Class aClass;

    public JaxBParser(Class aClass) {
        this.aClass = aClass;
    }

    /**
     * This method create xml file with usages JaxB parser
     *
     * @param data <{@link Offers}> wrapper for all parsing session results
     */

    public void createXmlFile(Offers data) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(aClass);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);


            jaxbMarshaller.marshal(data, new File("../result.xml"));


        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }
}
