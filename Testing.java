public class Testing {
    
    public static void main(String[] args){
        stestirajJSONSporocila();
    }
    
    public static void stestirajJSONSporocila(){
        Message sporocilo = new Message("public", "meow", null, "this is a test message.");
    
        System.out.printf("%s\n", sporocilo);
    
        Message novoSporocilo = new Message("public", "hacker", null, "you've been pwned!!!");
        System.out.printf("%s\n", novoSporocilo);
        String leteceSporocilo = novoSporocilo.toJSONString();
        Message prejetoSporocilo;
        try {
            prejetoSporocilo = Message.fromJSON(leteceSporocilo);
            System.out.printf("%s\n", prejetoSporocilo);
    
            // testing equality
    
            String color;
            if (novoSporocilo.toString().equals(prejetoSporocilo.toString())){
                color = "[32m";
            } else {
                color = "[31m";
            }
            System.out.printf((char)27 + color + "Sent message equals received: %b\n",
             novoSporocilo.toString().equals(prejetoSporocilo.toString()));
            
            System.out.println((char)27 + "[39m");
        } catch (Exception e) {

        }

    }
}
