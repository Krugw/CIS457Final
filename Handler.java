import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.*;

public class Handler extends Thread {

    Socket clientSocket;
    ServerSocket dataSocket;
    DataInputStream in;
    DataOutputStream out;
    ObjectInputStream dataIn;
    ObjectOutputStream dataOut;
    Food[] foodlist;
    private final int FRIDGE_SIZE = 100;

    public Handler(Socket socket, ServerSocket datalistener, Food[] foodlist) throws IOException {
        this.clientSocket = socket;
        this.dataSocket = datalistener;
        this.foodlist = foodlist;
        this.in = new DataInputStream (clientSocket.getInputStream());
        this.out = new DataOutputStream(clientSocket.getOutputStream());
    }

    public void run(){
        try {
            String cmd = new String("");
            String[] args;

            while (!clientSocket.isClosed()) {
                cmd = in.readUTF();
                args = cmd.split(" ");

                switch (args[0].toLowerCase()) {

                    case "list" :
                        Socket clientDataSocket = dataSocket.accept();
                        dataOut = new ObjectOutputStream(clientDataSocket.getOutputStream());
                        dataIn = new ObjectInputStream(clientDataSocket.getInputStream());
                        System.out.println("List requested from " + clientSocket.getInetAddress().getHostAddress());
                        dataOut.writeObject(foodlist);
                        System.out.println("List sent.");
                        dataIn.close();
                        dataOut.close();
                        clientDataSocket.close();
                        break;

                    case "quit" :
                        in.close();
                        out.close();
                        clientSocket.close();
                        System.out.println("Session Ended.");
                        break;

                    case "add" :
                        clientDataSocket = dataSocket.accept();
                        dataOut = new ObjectOutputStream(clientDataSocket.getOutputStream());
                        dataIn = new ObjectInputStream(clientDataSocket.getInputStream());
                        int sum = 0;
                        try{
                            Food food = (Food) dataIn.readObject();
                            boolean f = false;
                            for (int i = 0; i < foodlist.length; i++){
                                if (foodlist[i] != null){
                                    sum += foodlist[i].getVolume();
                                }
                            }
                            if ((sum + food.getVolume()) > FRIDGE_SIZE){
                                dataOut.writeObject("overflow");
                            }
                            else {
                                for (int i = 0; i < foodlist.length; i++){
                                    if (foodlist[i] == null && !f){
                                        foodlist[i] = food;
                                        f = true;
                                    }
                                }
                                if (f){
                                    dataOut.writeObject("true");
                                }
                                else {
                                    dataOut.writeObject("false");
                                }
                            }
                        }
                        catch (ClassNotFoundException e){
                            e.printStackTrace();
                        }
                        dataOut.writeObject(new Integer(FRIDGE_SIZE - sum));
                        dataIn.close();
                        dataOut.close();
                        clientDataSocket.close();
                        break;

                    case "remove" :
                        clientDataSocket = dataSocket.accept();
                        dataOut = new ObjectOutputStream(clientDataSocket.getOutputStream());
                        dataIn = new ObjectInputStream(clientDataSocket.getInputStream());
                        try{
                            Integer id = (Integer) dataIn.readObject();
                            if(id.intValue() >= 0 && id.intValue() <= foodlist.length){
                                if (foodlist[id.intValue()] != null){
                                    foodlist[id.intValue()] = null;
                                    dataOut.writeObject("true");
                                }
                                else{
                                    dataOut.writeObject("false");
                                }
                            }
                        }
                        catch(ClassNotFoundException e){
                            e.printStackTrace();
                        }

                        dataIn.close();
                        dataOut.close();
                        clientDataSocket.close();
                        break;

                    case "nextday" :
                        clientDataSocket = dataSocket.accept();
                        dataOut = new ObjectOutputStream(clientDataSocket.getOutputStream());
                        ArrayList<Food> expList = new ArrayList<>();
                        for (int i = 0; i < foodlist.length; i++) {
                            if (foodlist[i] != null) {
                                foodlist[i].decrementDays();
                                if (foodlist[i].getDays() <= 0) {
                                    expList.add(foodlist[i]);
                                }
                            }
                        }
                        dataOut.writeObject(expList);
                        dataOut.close();
                        clientDataSocket.close();
                        break;

                    default :
                        System.out.println("Invalid command.");
                        break;
                }
            }
        } catch (IOException e) {
            System.out.println("Thread closed unexpectedly.");
            System.out.println(e);
        }
    }
}
