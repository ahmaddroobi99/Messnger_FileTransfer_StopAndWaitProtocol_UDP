package messangerserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static messangerserver.ServerConstant.*;


public class ServerManager implements MessageListener {
    ExecutorService serverExecutor;
    ServerSocket server;
    Socket clientSocket;
    Clients[] clients;
    int clientNumber = 0;
    static String[] clientTracker;
    String users = "";

    public ServerManager() {
        clients = new Clients[CLIENT_NUMBER];
        clientTracker = new String [CLIENT_NUMBER];
        serverExecutor = Executors.newCachedThreadPool();
    }

    public void startServer(ServerStatusListener statusListener, ClientListener clientListener) {
        try {
            statusListener.status("Server is listening on port " + SERVER_PORT);
            server = new ServerSocket(SERVER_PORT, BACKLOG);
            serverExecutor.execute(new ConnectionController(statusListener, clientListener));
        }
        catch(IOException ioe) {
            statusListener.status("IOException occured when server starting");
        }
    }

    public void stopServer(ServerStatusListener statusListener) {
        try {
            server.close();
            statusListener.status("Server is stopped");
        }
        catch(SocketException ex) {
            statusListener.status("SocketException Occured When Server is going to stoped");
        }
        catch (IOException ioe) {
            statusListener.status("IOException Occured When Server is going to stoped");
        }
    }

    public void controllConnection(ServerStatusListener statusListener, ClientListener clientListener) {
        while(clientNumber < CLIENT_NUMBER) {  // While current clients number < max clients number
            try {
                clientSocket = server.accept();
                clients[clientNumber] = new Clients(clientListener, clientSocket, this, clientNumber);
                serverExecutor.execute(clients[clientNumber]);  // Run the last entered client
                clientNumber++;
                //System.out.println(clientNumber);
            }
            catch(SocketException ex) {
                ex.printStackTrace();
                break;
            }
            catch (IOException ioe) {
                ioe.printStackTrace();
                statusListener.status("Some problem occured when connection received");
                break;
            }
        }
    }

    @Override
    public void sendInfo(String message) {
        StringTokenizer tokens=new StringTokenizer(message);
        String to = tokens.nextToken();

        for(int i=0; i < clientNumber; i++) {
            if(clientTracker[i].equalsIgnoreCase(to)) {
                try {
                    clients[i].output.writeObject(message);
                    clients[i].output.flush();
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public void sendNameToAll(String message) {
        for(int i=0;i<clientNumber;i++) {
            try {
                System.out.println("Server is sending   " + message);
                clients[i].output.writeObject(message);
                clients[i].output.flush();
            } catch (IOException ex) { }
        }
    }

    class ConnectionController implements Runnable {
        ServerStatusListener statusListener;
        ClientListener clientListener;

        ConnectionController(ServerStatusListener getStatusListener, ClientListener getClientListener) {
            statusListener = getStatusListener;
            clientListener = getClientListener;
        }

        @Override
        public void run() {
            controllConnection(statusListener, clientListener);
        }
    }
}