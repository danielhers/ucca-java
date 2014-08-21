package il.ac.huji.cs.nlp.ucca;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "value"
})
public class Attributes {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "paragraph", required = true)
    protected int paragraph;
    @XmlAttribute(name = "paragraph_position", required = true)
    protected int paragraphPosition;
    @XmlAttribute(name = "text", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String text;
    @XmlAttribute(name = "implicit")
    protected Boolean implicit;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getParagraph() {
        return paragraph;
    }

    public void setParagraph(int value) {
        paragraph = value;
    }

    public int getParagraphPosition() {
        return paragraphPosition;
    }

    public void setParagraphPosition(int value) {
        paragraphPosition = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String value) {
        text = value;
    }

    public Boolean isImplicit() {
        return implicit;
    }

    public void setImplicit(Boolean value) {
        implicit = value;
    }

}
