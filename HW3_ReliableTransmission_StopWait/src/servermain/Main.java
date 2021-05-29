package servermain;

import messangerserver.ServerManager;
import messangerserver.ServerMonitor;

public class Main {

    public static void main(String[] args) {
        ServerManager serverManager=new ServerManager();
        ServerMonitor monitor=new ServerMonitor(serverManager);
        monitor.setVisible(true);
    }

}