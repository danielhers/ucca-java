package il.ac.huji.cs.nlp.ucca;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "attributes",
    "edge"
})
public class Node {

    protected Attributes attributes;
    @XmlAttribute(name = "ID", required = true)
    protected BigDecimal id;
    @XmlAttribute(name = "type", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String type;
    @XmlElement(required = true)
    protected List<Edge> edges;

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes value) {
        this.attributes = value;
    }

    public BigDecimal getID() {
        return id;
    }

    public void setID(BigDecimal value) {
        this.id = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String value) {
        this.type = value;
    }

    public List<Edge> getEdges() {
        if (edges == null) {
            edges = new ArrayList<Edge>();
        }
        return edges;
    }

}
