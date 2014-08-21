package il.ac.huji.cs.nlp.ucca;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.StringUtils;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "attributes",
    "layer"
})
@XmlRootElement(name = "root")
public class Passage {
	
    protected String attributes;
    @XmlAttribute(name = "annotationID", required = true)
    protected int annotationID;
    @XmlAttribute(name = "passageID", required = true)
    protected int passageID;
    @XmlElement(required = true)
    protected List<Layer> layers;

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String value) {
        attributes = value;
    }

    public int getAnnotationID() {
        return annotationID;
    }
    
    public void setAnnotationID(int value) {
        annotationID = value;
    }

    public int getPassageID() {
        return passageID;
    }

    public void setPassageID(int value) {
        passageID = value;
    }

    public List<Layer> getLayers() {
        if (layers == null) {
        	layers = new ArrayList<Layer>();
        }
        return layers;
    }
	
	public static Passage read(File xmlFile) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(Passage.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		return (Passage) jaxbUnmarshaller.unmarshal(xmlFile);
	}
	
	public void write(File xmlFile) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(Passage.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.marshal(this, xmlFile);
	}
	
	@Override
	public String toString() {
		List<String> tokens = new ArrayList<String>();
		for (Layer layer : getLayers()) {
			switch (layer.getLayerID()) {
			case 0:
				for (Node node : layer.getNodes()) {
					tokens.add(node.getAttributes().getText());
				}
				break;
			}
		}
		return StringUtils.join(tokens, " ");
	}
	
	public static void main(String[] args) throws JAXBException {
		System.out.println(read(new File("../ucca/corpus/ucca_passage20.xml")));
	}

}
