import com.sun.org.apache.xpath.internal.SourceTree;

import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.*;

public class Client{
    public static void main (String[] args) throws IOException{
        if (args.length != 2) {
            throw new IllegalArgumentException("Parameter(s): <Server> <port_to_connect_to>\nSuggested port: 2264");
        }
        if (args[1] == "1235"){
            throw new IllegalArgumentException("Invalid port; specify a different one.");
        }

        Scanner input = new Scanner(System.in);
        String cmd = new String("");
        String[] arguments;

        String server = args[0];
        int serverPort = new Integer(args[1]).intValue();
        Socket socket = new Socket(server, serverPort);

        DataInputStream in  = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        Socket dataSocket;
        ObjectInputStream oin;
        ObjectOutputStream oout;

        System.out.println("Connected to:" + "\nServer: " + server + "\nPort: " + serverPort);

        while(!socket.isClosed()){
            System.out.print(">> ");
            cmd = input.nextLine();
            arguments = cmd.split(" ");
            out.writeUTF(cmd);

            switch (arguments[0].toLowerCase()) {

                case "list" :
                    dataSocket = new Socket(server, 1235);
                    oin  = new ObjectInputStream(dataSocket.getInputStream());
                    oout = new ObjectOutputStream(dataSocket.getOutputStream());
                    try {
                        Food[] list = (Food[]) oin.readObject();
                        System.out.println("ID Name Type Days Until Expiration Volume");
                        for (int i = 0; i < list.length; i++){
                            if (list[i] != null) {
                                System.out.println((i + 1) + " " + list[i].toString());
                            }
                        }
                    }
                    catch (ClassNotFoundException e){
                        e.printStackTrace();
                    }

                    oout.close();
                    oin.close();
                    dataSocket.close();


                    cmd = "";
                    break;

                case "add" :
                    System.out.println("Name of food: ");
                    String name = input.nextLine();
                    System.out.println("Type of food: ");
                    String type = input.nextLine();
                    System.out.println("Days Till Expiration: ");
                    int exp = input.nextInt();
                    System.out.println("Volume of food: ");
                    int vol = input.nextInt();
                    dataSocket = new Socket(server, 1235);
                    oin  = new ObjectInputStream(dataSocket.getInputStream());
                    oout = new ObjectOutputStream(dataSocket.getOutputStream());
                    oout.writeObject(new Food(name, type, exp, vol));
                    try{
                        String ack = (String) oin.readObject();
                        if (ack.equals("false")){
                            System.out.println("Insufficient amount of tags!");
                        }
                        else if (ack.equals("overflow")){
                            System.out.print("Insufficient space! ");
                        }
                        else {
                            System.out.print("Food added to fridge. ");
                        }
                        System.out.println("Current available space: " + oin.readObject());
                    }
                    catch (ClassNotFoundException e){
                        e.printStackTrace();
                    }
                    oout.close();
                    oin.close();
                    dataSocket.close();

                    cmd = "";
                    break;

                case "remove" :
                    if (arguments.length != 2){
                        System.out.println("Invalid number of arguments. Syntax is remove <ID>");
                        break;
                    }
                    int temp = 0;
                    try{
                        temp = Integer.parseInt(arguments[1]);
                    }
                    catch (NumberFormatException e){
                        System.out.println("Invalid ID: not a number.");
                        break;
                    }
                    dataSocket = new Socket(server, 1235);
                    oin  = new ObjectInputStream(dataSocket.getInputStream());
                    oout = new ObjectOutputStream(dataSocket.getOutputStream());
                    try {
                        oout.writeObject(new Integer(temp - 1));
                    }
                    catch (NumberFormatException e){
                    }
                    try{
                        String ack = (String) oin.readObject();
                        switch (ack){
                            case "false" :
                                System.out.println("Invalid ID: ID not found.");
                                break;

                            case "true" :
                                System.out.println("Item removed.");
                                break;
                        }
                    }
                    catch (ClassNotFoundException e){
                        e.printStackTrace();
                    }

                    oout.close();
                    oin.close();
                    dataSocket.close();
                    break;

                case "help" :
                    System.out.println("Available commands: \n\"help\": Displays this message.\n"
                            + "\"quit\": Disconnects from the server.");
                    cmd = "";
                    break;

                case "nextday" :
                    dataSocket = new Socket(server, 1235);
                    oin  = new ObjectInputStream(dataSocket.getInputStream());
                    ArrayList<Food> expList = new ArrayList<>();

                    try {
                        expList = (ArrayList<Food>) oin.readObject();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (expList.isEmpty()) {
                        System.out.println("Nothing has expired! Congrats!");
                    } else {
                        for (int i = 0; i < expList.size(); i++) {
                            System.out.println("Your " + expList.get(i).getName() + " has expired. Please consider throwing it away.");
                        }
                    }

                    oin.close();
                    dataSocket.close();
                    break;

                case "quit" :
                    socket.close();
                    System.exit(0);
                    break;


                default :

                    System.out.println("Invalid command.");
                    cmd = "";
                    break;

            }
        }
    }
}
