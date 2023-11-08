package P2PFileShare_CC.src;

import java.io.Serializable;
import java.util.UUID;

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

    private String id;
    private Type type;
    private Query query;
    private String value;
    private String content;

    public Package(Type type, Query query, String value, String content) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.query = query;
        this.value = value;
        this.content = content;
    }

    public Package(String pPackageAsString) {
        String[] parts = pPackageAsString.split(": | ;");
        if (parts.length == 5) {
            this.id = parts[0].substring("Package-".length());
            this.type = Type.valueOf(parts[1]);
            this.query = Query.valueOf(parts[2]);
            this.value = parts[3];
            this.content = parts[4];
        }
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

    public String getId() {
        return id;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Package-" + id + ":");
        sb.append(type.toString() + ";");
        sb.append(query.toString() + ";");
        sb.append(value.toString() + ";");
        sb.append(content);

        return sb.toString();
    }

}