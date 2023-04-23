import org.json.simple.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

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
        this.timestamp = (Instant) Instant.parse((String) obj.get("timestamp"));
    }

    @SuppressWarnings("unchecked") // TODO lol
    public JSONObject toJson(){
        JSONObject obj = new JSONObject();
        obj.put("type", type);
        obj.put("sender", sender);
        obj.put("message", text);
        obj.put("timestamp", timestamp.toString());
        return obj;
    }

    public String toString(){
        // Zone in Locale sta tukaj kljucna, brez tega ne dela.
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault()).withZone(ZoneId.of("UTC"));
        String rezultat = new String();
        if (type == "public"){
            rezultat = String.format("(%s) [%s]:\n> %s",
            formatter.format(timestamp), sender, text);
        } else {
            rezultat = String.format("[%s] (%s) [%s]:\n> %s",
            type.toUpperCase(), formatter.format(timestamp), sender, text);
        }
        return rezultat;
    }
}