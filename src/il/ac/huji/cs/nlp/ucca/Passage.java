package il.ac.huji.cs.nlp.ucca;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.StringUtils;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "attributes",
    "layers"
})
@XmlRootElement(name = "root")
public class Passage {
	
    private static final int TEXT_LAYER = 0;
    
	protected String attributes;
    @XmlAttribute(name = "annotationID", required = true)
    protected int annotationID;
    @XmlAttribute(name = "passageID", required = true)
    protected int passageID;
    @XmlElement(name = "layer", required = true)
    protected List<Layer> layers;
    @XmlTransient
    protected Map<String, Node> nodes;

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
	
    /**
     * populate data structures, including inside nodes and edges
     */
	private void initialize() {
		// create id to node map
		nodes = new TreeMap<String, Node>();
		for (Layer layer : getLayers()) {
			for (Node node : layer.getNodes()) {
				nodes.put(node.getID(), node);
			}
		}
		// initialize node children
		for (Layer layer : getLayers()) {
			for (Node node : layer.getNodes()) {
				for (Edge edge : node.getEdges()) {
					Node toNode = nodes.get(edge.getToID());
					node.addChild(toNode);
					edge.setToNode(toNode);
				}
			}
		}
	}
	
	public static Passage read(File xmlFile) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(Passage.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		Passage passage = (Passage) jaxbUnmarshaller.unmarshal(xmlFile);
		passage.initialize();
		return passage;
	}

	public void write(File xmlFile) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(Passage.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.marshal(this, xmlFile);
	}
	
	/**
	 * @return a simplified tree representation of the passage,
	 * where edges are shown as tags surrounding their spans.
	 * NOTE: if the underlying DAG is not a tree, some nodes will
	 * appear more than once, which is wrong and should be fixed.
	 */
	@Override
	public String toString() {
		return StringUtils.join(getTopNodes(), " ");
	}
    
    /**
     * @return all nodes without incoming edges
     */
    public List<Node> getTopNodes() {
    	List<Node> roots = new ArrayList<Node>();
    	List<Node> children = new ArrayList<Node>();
		for (Layer layer : getLayers()) {
			for (Node node : layer.getNodes()) {
	    		roots.add(node);
				children.addAll(node.children);
			}
		}
		roots.removeAll(children);
		return roots;
    }
    
    /**
     * @return just the tokenized text of the passage in linear order
     */
    public String getText() {
		List<String> tokens = new ArrayList<String>();
		for (Layer layer : getLayers()) {
			switch (layer.getLayerID()) {
			case TEXT_LAYER:
				for (Node node : layer.getNodes()) {
					tokens.add(node.getAttributes().getText());
				}
				break;
			}
		}
		return StringUtils.join(tokens, " ");
	}

	public static void main(String[] args) throws JAXBException {
//		System.out.println(read(new File("../ucca/corpus/ucca_passage20.xml")));
		Passage passage = read(new File("../ucca/documentation/toy.xml"));
		System.out.println(passage);
		System.out.println(passage.getText());
	}

}
