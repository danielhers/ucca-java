package il.ac.huji.cs.nlp.ucca;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.StringUtils;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "attributes",
    "edges"
})
public class Node {

	public static final String PUNCTUATION_TERMINAL = "Punctuation";
	public static final String WORD_TERMINAL = "Word";
	public static final String REGULAR = "FN";
	public static final String PUNCTUATION = "PNCT";
	public static final String LINKAGE = "LKG";

	protected Attributes attributes;
    @XmlAttribute(name = "ID", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String id;
    @XmlAttribute(name = "type", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String type;
    @XmlElement(name = "edge", required = true)
    protected List<Edge> edges;
    @XmlTransient
    protected final List<Node> children = new ArrayList<>();
	@XmlTransient
	private boolean addedToString = false;

	public Node() {}

	public Node(String id, String type) {
		attributes = new Attributes();
		this.id = id;
		this.type = type;
		edges = new ArrayList<>();
	}

	public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes value) {
        this.attributes = value;
    }

    public String getID() {
        return id;
    }

    public void setID(String value) {
        id = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String value) {
        this.type = value;
    }

    public List<Edge> getEdges() {
        if (edges == null) {
            edges = new ArrayList<>();
        }
        return edges;
    }

	public void addEdge(Edge edge) {
		edges.add(edge);
		addChild(edge.getToNode());
	}
    
    public List<Node> getChildren() {
    	return children;
    }
    
    public void addChild(Node node) {
    	children.add(node);
    }

	public void setAddedToString(boolean value) {
		addedToString = value;
	}

	public boolean isAddedToString() {
		return addedToString;
	}
    
    @Override
    public String toString() {
	    setAddedToString(true); // TODO only print in the correct linear position in the text, not the first one
	    // leaf node
	    if (getEdges().isEmpty()) {
		    return getAttributes().getText();
	    }
	    // find edges that haven't been printed yet
	    List<Edge> edgesToAdd = new ArrayList<>();
	    for (Edge edge: getEdges()) {
		    if (!edge.getToNode().isAddedToString()) {
			    edgesToAdd.add(edge);
		    }
	    }
    	// inner node
    	return StringUtils.join(edgesToAdd, " ");
    }
}
