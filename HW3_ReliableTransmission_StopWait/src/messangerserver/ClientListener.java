package messangerserver;

public interface ClientListener {
    void signIn(String userName);
    void signOut(String userNamme);
    void clientStatus(String status);
}