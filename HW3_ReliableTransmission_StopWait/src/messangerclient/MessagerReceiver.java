package messangerclient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static messangerclient.ClientConstant.*;


public class MessagerReceiver implements Runnable {
    ObjectInputStream input;
    boolean keepListening = true;
    ClientListListener clientListListener;
    ClientWindowListener clientWindowListener;
    ClientManager clientManager;
    Socket clientSocket;
    ExecutorService clientExecutor;

    MessagerReceiver(Socket getClientSocket, ClientListListener getClientListListener, 
            ClientWindowListener getClientWindowListener, ClientManager getClientManager)
    {
        clientExecutor = Executors.newCachedThreadPool();
        clientManager = getClientManager;
        clientSocket = getClientSocket;
        try {
            input = new ObjectInputStream(getClientSocket.getInputStream());
        }
        catch (IOException ex) {}
        clientListListener = getClientListListener;
        clientWindowListener = getClientWindowListener;
    }
    
    @Override
    public void run() {
        String message, name = "";
        while(keepListening) {
            try {
                message = (String) input.readObject();  // Receive message
                System.out.println("user is receiving " + message);
                StringTokenizer tokens = new StringTokenizer(message);

                String header = tokens.nextToken();
                if(tokens.hasMoreTokens())
                    name = tokens.nextToken();
                if(header.equalsIgnoreCase("login"))
                    clientListListener.addToList(name);
                else if(header.equalsIgnoreCase(DISCONNECT_STRING))
                    clientListListener.removeFromList(name);
                else if(header.equalsIgnoreCase("server"))
                    clientWindowListener.closeWindow(message);
                else if(name.equalsIgnoreCase("file")) {
                    clientWindowListener.fileStatus("One File is Receiving");
                    String address=tokens.nextToken();
                    String fileName=tokens.nextToken();
                    clientExecutor.execute(new FileReceiver(address, header));
                }
                else
                    clientWindowListener.openWindow(message);
            }
            catch (IOException ex) {
                clientListListener.removeFromList(name);
            }
            catch (ClassNotFoundException ex) {}
        }
    }

    void stopListening() {
        keepListening = false;
    }
}
