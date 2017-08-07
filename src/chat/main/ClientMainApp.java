package chat.main;

import chat.controller.Client;
import chat.view.ChatUI;
import chat.view.LoginUI;
import chat.view.RegisterUI;

/**
 * 客户端主程序
 * 
 * @author 富澎
 *
 */
public class ClientMainApp {
    private LoginUI logUI;
    private RegisterUI regUI;
    private ChatUI chatUI;
    private Client client;

    public ClientMainApp() {
        client = new Client(this);
        logUI = new LoginUI(this, client);
        chatUI = new ChatUI(client);
        
        chatUI.setVisible(false);
    }

    public LoginUI getLogUI() {
        return logUI;
    }

    public RegisterUI getRegUI() {
        return regUI;
    }

    public ChatUI getChatUI() {
        return chatUI;
    }

    public Client getClient() {
        return client;
    }

    public void setRegUI(RegisterUI regUI) {
        this.regUI = regUI;
    }

    public void setChatUI(ChatUI chatUI) {
        this.chatUI = chatUI;
    }

    public static void main(String[] args) {
        new ClientMainApp(); // 启动客户端
    }
}
