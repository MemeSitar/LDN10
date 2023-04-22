import org.json.simple.*;

import java.time.Instant;

public class Message{
    private String type;
    private String sender;
    private String text;
    private Instant timestamp;

    public Message(String type, String sender, String text){
        this.type = type;
        this.sender = sender;
        this.text = text;
        this.timestamp = Instant.now();
    }

    public Message(JSONObject obj){
        this.type = (String) obj.get("type");
        this.sender = (String) obj.get("sender");
        this.text = (String) obj.get("message");
        this.timestamp = Instant.now();
    }

    @SuppressWarnings("unchecked") // TODO lol
    public JSONObject toJson(){
        JSONObject obj = new JSONObject();
        obj.put("type", type);
        obj.put("sender", sender);
        obj.put("message", text);
        obj.put("timestamp", timestamp);
        return obj;
    }

    public String toString(){
        String rezultat = new String();

        rezultat = String.format("Time: %s; Type: %s; Sent by: %s; Message:\n %s",
        timestamp, type, sender, text);
        return rezultat;
    }
}