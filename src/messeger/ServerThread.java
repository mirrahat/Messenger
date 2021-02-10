/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messeger;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mirrahat
 */
public class ServerThread extends Thread {

    public ArrayList userlist;
    public ArrayList<String> user = new ArrayList<String>();
    int count;
    public static int port = 2037;
    static String username;
    static String welcomeusername;
    static DataInputStream din;
    static DataOutputStream dout;
    public PrintWriter writer;
    static ServerSocket ss;
    static Socket s;
    private static ServerThread[] threads = new ServerThread[100];
    PrintWriter client;
    BufferedReader br;
    ArrayList objects;
    private static int c = 0;
    public int rowindex = -1;
    public static int id = -1;
    private ServerThread[] thread;

    public ServerThread(Socket clientSock, PrintWriter writer, ArrayList clo, ArrayList us, ArrayList obj, ServerThread[] threads) {
        try {
            this.s = clientSock;
            this.client = writer;
            this.user = us;
            this.userlist = clo;
            this.objects = obj;
            this.thread = threads;
            br = new BufferedReader(new InputStreamReader(this.s.getInputStream()));
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void tellEveryone(String message) {

        Iterator it = userlist.iterator();
        while (it.hasNext()) {
            try {
                PrintWriter writer = (PrintWriter) it.next();
                writer.println(message);
                System.out.println("Sending: " + message + "\n");
                writer.flush();
            } catch (Exception ex) {
                System.err.println("Error telling everyone. \n" + ex.getMessage());
            }
        }
    }

    public void run() {
        String message, groupchat = "groupchat", connect = "Connect", row = "row", username = "username", logout = "logout", group = "group", leavegroup = "leavegroup";
        String[] data;

        ServerThread threads[] = this.thread;
        try {

            din = new DataInputStream(s.getInputStream());
            writer = new PrintWriter(s.getOutputStream());
            while ((message = br.readLine()) != null) {
                System.out.println("Received: " + message + "\n");
                data = message.split(":");

                for (String token : data) {
                    System.out.println("token data  " + data[1]);
                }
                if (data[0].equals(username)) {
                    System.out.println("tell everyone");
                    user.add(data[1]);
                    if (c == 0) {
                        tellEveryone("u:" + "username:" + data[1]);

                    } else {
                        tellEveryone("u:" + "remove");
                        for (int i = 0; i < user.size(); i++) {
                            tellEveryone("u:" + "username:" + user.get(i));
                        }
                    }
                    c++;
                } else if (data[0].equals(row)) {
                    int rows = Integer.parseInt(data[1]);
                    System.out.println("data 0 " + data[0] + "  data 1 " + data[1] + "  data 2 " + data[2] + "  data 3 " + data[3] + " id " + data[5]);
                    threads[rows].writer.println("chat:" + "messege:" + data[3] + ":clientid:" + data[5]);
                    threads[rows].writer.flush();

                } else if (data[1].equals(logout)) {
                    System.out.println("logout  test");
                    tellEveryone("u:" + "logout:" + data[2]);
                    System.out.println(" logout data " + data[2]);
                    int log = Integer.parseInt(data[2]);
                    user.remove(log);
                    id = id - 2;
                    System.out.println("after server remove  " + id);
                    for (int i = log; i < user.size() - 1; i++) {

                        threads[i] = threads[i + 1];
                    }

                } else if (data[0].equals(group)) {
                    tellEveryone("u:" + "group:" + data[2] + ":senderid:" + data[4]);

                } else if (data[0].equals(groupchat)) {
                    tellEveryone("u:" + "groupchat:");

                } else if (data[0].equals(leavegroup)) {
                    tellEveryone("u:" + "leavegroup:" + data[2]);

                }

            }
        } catch (Exception ex) {
            System.out.println("i am here1");
            //clientoutput.remove(client);
            System.out.println("Lost a connection. \n");
            ex.getMessage();
        }
    }

    public static void main(String[] arg) {
        try {
            ArrayList clo = new ArrayList();

            ArrayList obj = new ArrayList();
            ArrayList<String> us = new ArrayList();
            ServerSocket serverSock = new ServerSocket(port);

            while (true) {
                System.out.println("waiting");
                Socket clientSock = serverSock.accept();
                System.out.println("accepted");
                PrintWriter writer = new PrintWriter(clientSock.getOutputStream());
                clo.add(writer);
                id = id + 1;
                writer.println("u:" + "id:" + id);
                for (int i = 0; i < 100; i++) {
                    if (threads[i] == null) {
                        (threads[i] = new ServerThread(clientSock, writer, clo, us, obj, threads)).start();
                        break;
                    }
                }
                System.out.println("Got a connection. \n");
            }
        } catch (Exception ex) {
            System.out.println("Error making a connection. \n" + ex.getMessage());
        }

        // checkArraylist();
    }

}
