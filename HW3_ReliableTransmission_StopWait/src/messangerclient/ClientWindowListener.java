package messangerclient;

public interface ClientWindowListener {
    public void openWindow(String message);
    public void closeWindow(String message);
    public void fileStatus(String filesStatus);
}