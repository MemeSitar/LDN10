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
    private String receiver;
    private Instant timestamp;

    /* Message types:
     - PUBLIC
     - PRIVATE
     - LOGIN
     - JOIN
     - LEAVE
     - ERROR
     - STOP
     */

    private Message(String type, String sender, String receiver, String text, Instant timestamp){
        this.type = type;
        this.sender = sender;
        this.receiver = receiver;
        this.text = text;
        this.timestamp = timestamp;
    }

    public Message(String type, String sender, String receiver, String text){
        this(type, sender, receiver, text, Instant.now());
    }

    public static Message fromJSON(String JSONString) throws ParseException{
        JSONParser parser = new JSONParser();
        JSONObject obj;
    
        obj = (JSONObject) parser.parse(JSONString);
        return new Message((String) obj.get("type"),
         (String) obj.get("sender"),
         (String) obj.get("receiver"),
         (String) obj.get("message"),
         (Instant) Instant.parse((String) obj.get("timestamp")));
    }

    @SuppressWarnings("unchecked") // TODO lol
    public JSONObject toJson(){
        JSONObject obj = new JSONObject();
        obj.put("type", type);
        obj.put("sender", sender);
        obj.put("receiver", receiver);
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
            rezultat = String.format("[%s] (%s) [%s] -> [%s]:\n> %s\n",
            type.toUpperCase(), formatter.format(timestamp), sender, receiver, text);
        } else if (type.toUpperCase().equals("JOIN") || type.toUpperCase().equals("LEAVE")){
            rezultat = String.format("[%s] (%s) [%s]:\n> @%s %s\n",
            type.toUpperCase(), formatter.format(timestamp), "system", sender, text);
            System.out.printf("meow\n");
        } else {
            rezultat = String.format("[%s] (%s) [%s]:\n> %s\n",
            type.toUpperCase(), formatter.format(timestamp), sender, text);
        }

        return rezultat;
    }
    

    // getterji
    public String getType(){
        return this.type.toUpperCase();
    }

    public String getSender(){
        return this.sender;
    }

    public String getReceiver(){
        return this.receiver;
    }

    //  This is probably a stupid way to do this
    // since it has to be updated every time a message would be added
    
    public boolean isOnlyWhitespace(){
        if (this.type.toUpperCase().equals("PUBLIC") || this.type.toUpperCase().equals("PRIVATE")){
            return this.text.trim().isEmpty();
        } else {
            return false;
        }
    }
}