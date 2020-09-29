package javaProject;


public class Main {

    public static void main( String[] args) {
        String mode = System.getProperty("mode").toLowerCase();
        if (mode.equals("client"))
            ClientMain.main(args);
        else if (mode.equals("server"))
            ServerMain.main(args);
        else
            System.err.println("What are u trying to do? only write:\n-Dmode=client/-Dmode=server");
    }
}