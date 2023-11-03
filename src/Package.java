package P2PFileShare_CC.src;

import java.io.Serializable;

public class Package implements Serializable {

    enum Type {
        REQUEST,
        RESPONSE
    }

    enum Query {
        GET,
        REGISTER,
        UPDATE
    }

    private String value;            
    private Query query;
    private Type type;
    private String content;
    private int id;
    private Byte[] payload = new Byte[256];

    public Package(String value, Query query, Type type, String content, int id, Byte[] payload) {
        this.value = value;
        this.query = query;
        this.type = type;
        this.content = content;
        this.id = id;
        this.payload = payload;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Byte[] getPayload() {
        return payload;
    }

    public void setPayload(Byte[] payload) {
        this.payload= payload;
    }

}