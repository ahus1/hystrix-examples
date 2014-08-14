package de.ahus1.hystrix.base;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Message {
    private String message;

    public Message() {
        // no-arg constructor to satisfy JAXB
    }

    public Message(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
