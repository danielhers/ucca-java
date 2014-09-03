package il.ac.huji.cs.nlp.ucca;

import java.io.File;
import java.util.*;

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
import org.deeplearning4j.rntn.Tree;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "attributes",
    "layers"
})
@XmlRootElement(name = "root")
public class Passage {

	protected Attributes attributes;
    @XmlAttribute(name = "annotationID", required = true)
    protected int annotationID;
    @XmlAttribute(name = "passageID", required = true)
    protected int passageID;
    @XmlElement(name = "layer", required = true)
    protected List<Layer> layers;
    @XmlTransient
    protected Map<String, Node> nodes;

	public Passage() {}
	public Passage(Tree tree) throws Exception {
		layers = new ArrayList<>();
		Map<Tree, Node> treeToNode = new HashMap<>();

		Layer textLayer = new Layer(Layer.TEXT);
		int i = 1;
		for (String token : tree.yield()) {
			String type;
			if (token.matches("\\p{Punct}")) {
				type = Node.PUNCTUATION_TERMINAL;
			} else {
				type = Node.WORD_TERMINAL;
			}
			Node node = new Node(String.format("%d.%d", Layer.TEXT, i++), type);
			node.getAttributes().setText(token);
			textLayer.addNode(node);
			treeToNode.put(tree, node);
		}
		layers.add(textLayer);

		Layer foundationalLayer = new Layer(Layer.FOUNDATIONAL);
		List<Tree> level = tree.getLeaves();
		i = 1;
		while (!level.isEmpty()) {
			List<Tree> nextLevel = new ArrayList<>();
			for (Tree treeNode : level) {
				nextLevel.add(treeNode.parent());
				Node node = new Node(String.format("%d.%d", Layer.FOUNDATIONAL, i++), Node.REGULAR);
				for (Tree treeChild : treeNode.getChildren()) {
					Node child = treeToNode.get(treeChild);
					if (child.getType().equals(Node.PUNCTUATION_TERMINAL)) {
						node.setType(Node.PUNCTUATION);
					}
					List<String> tags = treeChild.tags();
					if (tags.size() != 1) {
						throw new Exception("Tree node has " + tags.size() + " tags: " + tags);
					}
					Edge edge = new Edge(child.getID(), tags.get(0));
					edge.setToNode(child);
					node.addEdge(edge);
				}
				textLayer.addNode(node);
				treeToNode.put(tree, node);
			}
			level = nextLevel;
		}
		layers.add(foundationalLayer);

		initialize();
	}

	public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes value) {
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
        	layers = new ArrayList<>();
        }
        return layers;
    }
	
    /**
     * populate data structures, including inside nodes and edges
     */
	private void initialize() {
		// create id to node map
		nodes = new TreeMap<>();
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
    	List<Node> roots = new ArrayList<>();
    	List<Node> children = new ArrayList<>();
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
		List<String> tokens = new ArrayList<>();
		for (Layer layer : getLayers()) {
			switch (layer.getLayerID()) {
			case Layer.TEXT:
				for (Node node : layer.getNodes()) {
					tokens.add(node.getAttributes().getText());
				}
				break;
			}
		}
		return StringUtils.join(tokens, " ");
	}
    
    public Set<String> getAllEdgeTypes() {
    	Set<String> types = new TreeSet<>();
		for (Layer layer : getLayers()) {
			for (Node node : layer.getNodes()) {
				for (Edge edge : node.getEdges()) {
					types.add(edge.getType());
				}
			}
		}
    	return types;
    }

	public static void main(String[] args) throws JAXBException {
//		System.out.println(read(new File("../ucca/corpus/ucca_passage20.xml")));
		Passage passage = read(new File("../ucca/documentation/toy.xml"));
		System.out.println(passage);
		System.out.println(passage.getText());
	}

}
