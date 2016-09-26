/*Aayush Karki - aayushka
Distributed Systems 2016f - PS1
*/

/*
* This is the implementation of a GUI client.
* It connects to a server on localhost only at port 15440
* Note that when the server has disconnected, 
* no message can be sent. It waits for the user
* to close the window.
*/

import java.io.*;
import java.net.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


/*
* This class implements the client logic while 
* it inherits the GUI features from GUIWindow
*/
public class GraphClient extends GUIwindow{

    private BufferedReader console;
    private BufferedReader rd;
    private PrintWriter pw;
    private String clientName ;
    private Socket sock;
    private final String host = "localhost";
    private final int port = 15440;

    private GraphClient(String clientName){
        // Write the author's info on the title bar of a client window
        super("Aayush Karki's Chat Client - " + clientName);
        this.clientName = clientName;
        try{
            this.sock = new Socket(host, port);
            this.pw = new PrintWriter(this.sock.getOutputStream(), true);
            this.rd = new BufferedReader(
                            new InputStreamReader(this.sock.getInputStream())
                            );

            super.setPrintWriter(this.pw);
            super.setClientName(this.clientName);

            //inform other clients including self that self has joined the chat
            super.sendConnectionMsg();
        }
        catch(IOException ioe){
            System.out.println("IO exp. Connection to server failed");
            System.out.println("Server probably not running.");
            System.exit(0);
        }
    }

 

        
        private void startClient(){
            String inMsg;

            while(true){
	            try{
	                inMsg = rd.readLine(); //read msg from the input stream (server)
	                if (inMsg!=null){ 
	                    displayMessage(inMsg);//this is a method in superclass
	                }
	                else{ // if inMsg is null, it means the connection issue with server.
                        //Hence quit.
	                    
	                    System.out.println("....Quitting the chat.");
	                    this.sock.close();
	                    displayMessage("\nServer Disconnected. Close the window.\n");
                        this.rd.close();
                        this.pw.close();
	                    while (true); // wait for user to close. 
                        //This disconnets from the server but keeps the GUI window open
                        //until the user closes manually
	                    
	                }       
	            }
	            catch(IOException ioe){
	                System.out.println("on here.....");
	                
	            }
        }//while

        }
    

    

    public static void main(String[] args) {

        if (args.length <1) {
            System.out.println("\n\nType : java GraphClient <username>\n\n");
            System.exit(0);
        }

        GraphClient t = new GraphClient(args[0]);
        t.startClient();
            
    }
}


/***************************************************************************/

/*
* This is a super class that my client class (class GraphClient) will
* extend for GUI features.
*/

class GUIwindow{
    private static JFrame frame ;
    private static JTextArea area;
    private static JTextField field ;
    private static PrintWriter pw = null;
    private static String clientName = "Anonymous Client";

    /*
    * Sets a printwriter.
    * Gets the printwriter from the subclass (upon explicit invocation
        in the sublass)
    */
    protected void setPrintWriter(PrintWriter pw){
        this.pw = pw;       

    }

    /*
    * Set the client's (user's) name
    */
    protected void setClientName(String name){
        this.clientName = name;
    }


    /*
    * Notifies all other clients that this client has joined the chat.
    */
    protected static void sendConnectionMsg(){
        if (pw!=null){
                pw.println("----- " + clientName + " has joined. -----" );
        }
    }

    /*
    * Write the message to a GUI interface area
    */
    protected void displayMessage(String msg){
            this.area.append("  "+msg+"\n");
    }

    /*
    * Notifies all other clients that this client has quit.
    */
    private static void sendDisconnetMsg(){
        if (pw!=null){
            pw.printf("----- %s has quit. -----", clientName);
            }
    }



    private static void init_Frame(){
        frame.setSize(500,400); //frame resolution
        frame.setLayout(new FlowLayout(FlowLayout.LEFT));

        frame.addWindowListener(
                new WindowAdapter(){
                     //override the windowClosing() method
                     // This reacts to the clicking of the 'x' on the
                     // GUI window(frame)
                     public void windowClosing(WindowEvent e){
                            //notify other clients that this user is quitting
                            //the chat
                            sendDisconnetMsg();
                            System.exit(0);
                        }

                    }

            );
    }


    private void custom_addListener(JTextField tf){

        tf.addActionListener(new ActionListener() {
                //overried actionPerformed() method
                //This reacts to [Return] key from the keyboard on
                //the filed f
               public void actionPerformed(ActionEvent e) {
                
                String line = tf.getText();
                
                if (pw!=null){
                        // write on the window : own name and own's message
                        pw.println(clientName + "-> " + line);
                        tf.setText(""); //clear the text field once the message
                                //has been sent.
                }

                
            }
        });

    }


    /*
    *   The constructor sets up the GUI window
    */
    public GUIwindow(String title){
        frame = new JFrame(title);    // frame (window)
        area = new JTextArea(6, 40); // message display area
        field = new JTextField(40);  //text entry area
        init_Frame(); //set up the frame; launches the GUI 

        this.area = new JTextArea(20, 40); //messages log area
        this.field = new JTextField(40);    //message entry area

        area.setEditable(false); //message log area shouldn't be editable
        field.setEditable(true);  // message entry field should be editable

        custom_addListener(field); // binds the Enter key to 'field'


        /*More GUI specifications*/
        Container c = frame.getContentPane();
        JScrollPane spane = new JScrollPane(area,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JLabel label = new JLabel("Press <Enter> to send message");

        c.add(spane, "Center");
        c.add(field);
        c.add(label);

        area.append( "         Begin Chat\n");
        frame.setVisible(true); //displays the frame on the OS window

    }


}