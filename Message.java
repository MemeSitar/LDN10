import org.json.simple.*;
import org.json.simple.parser.*;

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

    public Message(String JSONString) throws ParseException{
        JSONParser parser = new JSONParser();
        JSONObject obj;
    
        obj = (JSONObject) parser.parse(JSONString);
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

    public String toJSONString(){
        JSONObject obj = this.toJson();
        return obj.toJSONString();
    }

    public String toString(){
        String rezultat;
        // Zone in Locale sta tukaj kljucna, brez tega ne dela.
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault()).withZone(ZoneId.of("UTC"));

        if (type.toUpperCase().equals("PUBLIC")){
            rezultat = String.format("(%s) [%s]:\n> %s\n",
            formatter.format(timestamp), sender, text);
        } else if (type.toUpperCase().equals("LOGIN")){
            rezultat = String.format("[%s] (%s) [%s] LOGGED IN\n",
            type.toUpperCase(), formatter.format(timestamp), sender, text);
        } else if (type.toUpperCase().equals("PRIVATE")){
            rezultat = String.format("[%s] (%s) [%s]:\n> %s\n",
            type.toUpperCase(), formatter.format(timestamp), sender, text);
        } else {
            rezultat = String.format("[%s] (%s) [%s]:\n> %s\n",
            type.toUpperCase(), formatter.format(timestamp), sender, text);
        }
        
        return rezultat;
    }
    
    public String getType(){
        return this.type.toUpperCase();
    }

    public String getSender(){
        return this.sender;
    }
}