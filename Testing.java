import org.json.simple.*;

public class Testing {
    
    public static void main(String[] args){
        stestirajJSONSporocila();
    }
    
    public static void stestirajJSONSporocila(){
        Message sporocilo = new Message("public", "meow", "this is a test message.");
    
        System.out.printf("%s\n", sporocilo);
    
        Message novoSporocilo = new Message("public", "hacker", "you've been pwned!!!");
        JSONObject leteceSporocilo = novoSporocilo.toJson();
        Message prejetoSporocilo = new Message(leteceSporocilo);
        System.out.printf("%s\n", prejetoSporocilo);

    }
}
