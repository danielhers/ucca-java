package il.ac.huji.cs.nlp.ucca;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "attributes"
})
public class Edge {

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
		return "<" + type + "> " + toNode + " </" + type + ">";
	}

}
