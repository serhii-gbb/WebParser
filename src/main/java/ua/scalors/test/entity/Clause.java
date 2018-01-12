package ua.scalors.test.entity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "clause")
@XmlAccessorType(XmlAccessType.FIELD)
public class Clause {

    @XmlAttribute
    private String name;
    private String text;

    public Clause() {
    }

    public Clause(String name, String text) {
        this.text = text;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
