package messangerclient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static messangerclient.ClientConstant.*;

public class ClientManager {
    ExecutorService clientExecutor;
    Socket clientSocket;
    boolean isConnected = false;

    ObjectInputStream input;
    ObjectOutputStream output;
    MessagerReceiver messageReceiver;

    public ClientManager() {
        clientExecutor = Executors.newCachedThreadPool();
    }

    public void connect(ClientStatusListener clientStatus) {
        try {
            if(isConnected)
                return;
            else {
                clientSocket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                clientStatus.loginStatus("You are connected to " + SERVER_ADDRESS);
                isConnected = true;
            }
        }
        catch (UnknownHostException ex) {
            clientStatus.loginStatus("No Server found");
        }
        catch (IOException ex) {
            clientStatus.loginStatus("No Server found");
        }
    }

    public void disconnect(ClientStatusListener clientStatus) {
        messageReceiver.stopListening();
        try {
            clientStatus.loginStatus("You are no longer connected to the server");
            clientSocket.close();
        }
        catch (IOException ex) {
        }
    }

    public void sendMessage(String message) {
        clientExecutor.execute(new MessageSender(message));
    }
    
    public void sendFile(String fileName) {
        clientExecutor.execute(new FileSender(fileName));
    }

    boolean flagOutput = true;
    class MessageSender implements Runnable {
        String message;
        public MessageSender(String getMessage) {
            if(flagOutput) {
                try {
                    output = new ObjectOutputStream(clientSocket.getOutputStream());
                    output.flush();
                    flagOutput = false;
                } catch (IOException ex) { }
            }
            message = getMessage;
            System.out.println("user is sending  " + message);
        }
        
        @Override
        public void run() {
            try {
                output.writeObject(message);
                output.flush();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void receiveMessage(ClientListListener getClientListListener, ClientWindowListener getClientWindowListener) {
        messageReceiver = new MessagerReceiver(clientSocket, getClientListListener, getClientWindowListener, this);
        clientExecutor.execute(messageReceiver);
    }
}