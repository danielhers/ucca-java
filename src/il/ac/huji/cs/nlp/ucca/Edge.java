package il.ac.huji.cs.nlp.ucca;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "attributes"
})
public class Edge {

    protected Object attributes;
    @XmlAttribute(name = "toID", required = true)
    protected BigDecimal toID;
    @XmlAttribute(name = "type", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String type;

    public Object getAttributes() {
        return attributes;
    }

    public void setAttributes(Object value) {
        attributes = value;
    }

    public BigDecimal getToID() {
        return toID;
    }

    public void setToID(BigDecimal value) {
        toID = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String value) {
        type = value;
    }

}
