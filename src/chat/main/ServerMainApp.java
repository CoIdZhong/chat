package chat.main;

import chat.controller.Server;

/**
 * 服务器主程序
 * @author 富澎
 *
 */
public class ServerMainApp {

    public ServerMainApp() {
        Server server = new Server();   // 创建服务器并初始化
        server.start();                 // 启动服务器
    }
    
    public static void main(String[] args) {
        new ServerMainApp();
    }
}
