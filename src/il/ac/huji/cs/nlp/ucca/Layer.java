package il.ac.huji.cs.nlp.ucca;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "attributes",
    "nodes"
})
public class Layer {

    protected String attributes;
    @XmlAttribute(name = "layerID", required = true)
    protected int layerID;
    @XmlElement(name = "node", required = true)
    protected List<Node> nodes;

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String value) {
        attributes = value;
    }

    public int getLayerID() {
        return layerID;
    }

    public void setLayerID(int value) {
        layerID = value;
    }

    public List<Node> getNodes() {
        if (nodes == null) {
            nodes = new ArrayList<>();
        }
        return nodes;
    }

}
