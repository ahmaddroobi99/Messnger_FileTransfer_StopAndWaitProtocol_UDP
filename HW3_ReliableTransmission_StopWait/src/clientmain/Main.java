package clientmain;

import messangerclient.ClientManager;
import messangerclient.LoginFrame;

public class Main {

    public static void main(String[] args) {
        ClientManager clientManager = new ClientManager();
        LoginFrame loginFrame = new LoginFrame(clientManager);
        loginFrame.setVisible(true);
    }

}