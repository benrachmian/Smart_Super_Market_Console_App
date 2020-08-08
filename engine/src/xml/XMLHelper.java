package xml;

import SDMSystem.system.SDMSystem;
import xml.generated.SuperDuperMarketDescriptor;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;

public class XMLHelper {

    private final static String JAXB_XML_GAME_PACKAGE_NAME = "xml.generated";



    //Blocks the  option of creating an object of XMLHelper
    private XMLHelper() {
    }

    public static void FromXmlFileToObject(String fileName, SDMSystem sdmSystem){
        InputStream inputStream = XMLHelper.class.getResourceAsStream(fileName);
        try {
            SuperDuperMarketDescriptor superDuperMarketDescriptor = deserializeFrom(inputStream);
            sdmSystem.loadSystem(superDuperMarketDescriptor);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }


    private static SuperDuperMarketDescriptor deserializeFrom(InputStream in) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(JAXB_XML_GAME_PACKAGE_NAME);
        Unmarshaller u = jc.createUnmarshaller();
        return (SuperDuperMarketDescriptor) u.unmarshal(in);
    }
}

