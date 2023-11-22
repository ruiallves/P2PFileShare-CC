package P2PFileShare_CC.src.packet;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

public class Packet implements Serializable {

    public enum Type {
        REQUEST,
        RESPONSE
    }

    public enum Query {
        GET,
        REGISTER,
        UPDATE;

    }

    private String id;
    private Type type;
    private Query query;
    private String content;

    public Packet(Type type, Query query, String content) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.query = query;
        this.content = content;
    }

    public Packet(String pPackageAsString) throws UnknownHostException {
        String[] parts = pPackageAsString.split(":|;");
        if (parts.length == 4) {
            this.id = parts[0].substring("Package-".length());
            this.type = Type.valueOf(parts[1]);
            this.query = Query.valueOf(parts[2]);
            this.content = parts[3];
        }
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

        if (type != null) {
            sb.append(type.toString() + ";");
        } else {
            sb.append("null;");
        }

        if (query != null) {
            sb.append(query.toString() + ";");
        } else {
            sb.append("null;");
        }

        if (content != null) {
            sb.append(content);
        } else {
            sb.append("null");
        }

        return sb.toString();
    }


}