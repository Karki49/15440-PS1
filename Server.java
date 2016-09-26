
/*Aayush Karki - aayushka
Distributed Systems 2016f - PS1
*/

/**
* This server runs on localhost only.
* It listens on port 15440
**/


import java.io.*;
import java.net.*;
import java.util.HashSet;


public class Server {
    private ServerSocket serverSock;
    private Socket socket ;
    private final int port = 15440;

    //make a list of all active clients.
    //Using HashSet as there should be no duplicate printwriters
    private HashSet<PrintWriter> clientWriters = new HashSet<PrintWriter>();

    public static void main(String[] args) {
            new Server().startServer();
    }
    

    /*
        Listen on the socket, and as new client connets, create new server threads
    */
    private void startServer(){
        try{
                this.serverSock = new ServerSocket(port);
                System.out.println("Server is up and running.");
                System.out.println("Waiting for client...");
                
                //Spawn new thread
                 while (true){
                        socket = serverSock.accept(); //listen for new clients
                        new ThreadedServer(socket).start();
                }
            }
        catch (IOException ioe){
                System.out.println("I/O exception while binding. " 
                                        + ioe.toString() );
        }
        finally{
            try{socket.close();}
            catch (IOException ioe2) {
                System.out.println("I/O exception while closing socket. " 
                                    + ioe2.toString());}
            
        }


    }


    /*
    * A helper class to implement the server logic.
    *
    *
    */

    private class ThreadedServer extends Thread{
            private BufferedReader reader; //handles input stream
            private PrintWriter writer; // handles output stream
            private Socket sock; // socket for client

            public ThreadedServer(Socket s){
                this.sock = s;
            }

            /*
            *Writes a message to all clients
            *
            */
            public synchronized void sendToAllClients(String line){

                for (PrintWriter pw: clientWriters){
                    pw.println(line);
                }
            }


            /*
            * Removes a client from the list of active clients.
            *
            */
            public synchronized void handle_clientExit(PrintWriter w){
            
                System.out.println("----A client has quit----");
                clientWriters.remove(w);
                try{
                    this.sock.close();
                }
                catch(IOException ioe){
                    System.out.println("Error while closing sock"+ ioe.toString());
                }
               
            }


          
            public void run(){
                System.out.println("A new client has been connected.");
                String msg_line ;

                try{
                    
                    reader = new BufferedReader(
                        new InputStreamReader(this.sock.getInputStream())
                        );

                    writer = new PrintWriter(this.sock.getOutputStream(), true);
                    
                    // add a client's output stream to the list of clients
                    synchronized(this){
                        clientWriters.add(writer);
                    }

                    boolean  isClientConneted = true;
                    while (isClientConneted){
                        //read from input stream/ from client
                        msg_line = reader.readLine();
                        //if a read from stream is not null, the client is still active
                        if (msg_line !=null){
                            System.out.println(msg_line);
                            sendToAllClients(msg_line);
                        }
                        else{ // client has quit already
                            handle_clientExit(writer);
                            isClientConneted = false;
                        }
                    }
                }
                catch(IOException ioe){
                    System.out.println("Error in run() method. " + ioe.toString());
                }


            }//run

}


}