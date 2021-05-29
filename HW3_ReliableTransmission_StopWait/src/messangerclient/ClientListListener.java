package messangerclient;

public interface ClientListListener {
    void addToList(String userName);
    void removeFromList(String userName);
}