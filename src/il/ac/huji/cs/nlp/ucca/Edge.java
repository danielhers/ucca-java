package il.ac.huji.cs.nlp.ucca;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "attributes"
})
public class Edge {

	public static final String PARTICIPANT = "A";
	public static final String PROCESS = "P";
	public static final String STATE = "S";
	public static final String ADVERBIAL = "D";
	public static final String CENTER = "C";
	public static final String ELABORATOR = "E";
	public static final String CONNECTOR = "N";
	public static final String RELATOR = "R";
	public static final String LINKER = "L";
	public static final String PARALLEL_SCENE = "H";
	public static final String GROUND = "G";
	public static final String FUNCTION = "F";

	public static final String LINK_RELATION = "LR";
	public static final String LINK_ARGUMENT = "LA";
	public static final String PUNCTUATION_TERMINAL = "U";
	public static final String WORD_TERMINAL = "T";

	protected Attributes attributes;
    @XmlAttribute(name = "toID", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String toID;
    @XmlAttribute(name = "type", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String type;
    @XmlTransient
    protected Node toNode;
	@XmlTransient
	private boolean addedToString = false;

    public Object getAttributes() {
        return attributes;
    }

	public Edge() {}

	public Edge(String toID, String type) {
		this.toID = toID;
		this.type = type;
	}

	public void setAttributes(Attributes value) {
        attributes = value;
    }

    public String getToID() {
        return toID;
    }

    public void setToID(String value) {
        toID = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String value) {
        type = value;
    }

	public Node getToNode() {
		return toNode;
	}

	public void setToNode(Node node) {
		toNode = node;
	}

	@Override
	public String toString() {
		if (isAddedToString()) {
			return "";
		}
		setAddedToString(true);
		return "<" + getType() + "> " + getToNode() + " </" + getType() + ">";
	}

	public void setAddedToString(boolean value) {
		addedToString = value;
	}

	public boolean isAddedToString() {
		return addedToString;
	}

}
