package chat.controller;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import chat.model.Message;
import chat.model.Operator;
import chat.model.Result;
import chat.model.User;
import chat.utils.MyStringUtils;

/**
 * 服务器
 * @author 富澎
 *
 */
public class Server {
    
    private static final String DATA_SOURCE  = "datasource.txt";    // 数据源文件名
    private static final int TCP_SERVER_PORT = 30000;               // tcp服务器端口号
    
    private ServerSocket tcpServer;             // tcp服务器
    private Map<String, User> userList;         // 用户清单
    private Map<String, SocketAddress> logList; // 在线用户清单
    private List<Client> broadcastList;         // 广播清单
    
    /**
     * 默认的构造器,对其初始化
     */
    public Server() {
        init(); // 初始化
    }

    /**
     * 服务器的初始化
     */
    private void init() {
        try {
            // 启动tcp服务器
            tcpServer     = new ServerSocket(TCP_SERVER_PORT);
            // 初始化用户清单,在线用户清单,广播清单
            userList      = new HashMap<String, User>();
            broadcastList = new LinkedList<Client>();
            logList       = new HashMap<String, SocketAddress>();
            // 加载用户清单
            loadUserList();
        } catch (IOException e) { e.printStackTrace(); }
    }
    
    /**
     * 加载用户信息
     */
    private void loadUserList() {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(DATA_SOURCE)));
            String line = null;
            while ((line = br.readLine()) != null) { // 访问数据库,读取每条用户信息
                String user = MyStringUtils.get("user", line);
                String pass = MyStringUtils.get("pass", line);
                String nickname = MyStringUtils.get("nickname", line);
                String mark = MyStringUtils.get("mark", line);
                userList.put(user, new User(user, pass, nickname, mark)); // 将用户信息添加到用户清单
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) try { br.close(); } catch (IOException e) { e.printStackTrace(); }
        }
    }

    /**
     * 启动服务器
     */
    public void start() {
        Client client = null;
        while ((client = accept()) != null)             // 监听客户端连接
            new Thread(new Receiver(client)).start();   // 启动服务器接收器,接收tcp消息
    }
    
    /**
     * 监听客户端连接
     * @return
     */
    private Client accept() {
        try {
            // 监听Socket连接
            Socket socket = tcpServer.accept();
            Client client = new Client(socket);
            return client;
        } catch (IOException e) { e.printStackTrace(); }
        return null;
    }
    
    /**
     * 用户是否存在
     * @param user 待验证的用户
     * @return result 验证的结果
     */
    private boolean isUserExsits(User user) {
        return userList.containsKey(user.getUser());
    }
    
    /**
     * 检索用户
     * @param user 带检索的用户
     * @return User 返回用户清单中的用户
     */
    private User retrieveUser(User user) {
        return userList.get(user.getUser());
    }

    /**
     * 创建用户
     * @param user 待创建的用户
     */
    private void createUser(User user) {
        // 将该用户添加到用户清单
        userList.put(user.getUser(), user);
        // 将该用户保存到数据库
        FileWriter fw = null;
        try {
            fw = new FileWriter(DATA_SOURCE, true);
            fw.write(user.toString() + "\n");
            fw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fw != null) try { fw.close(); } catch (IOException e) { e.printStackTrace(); }
        }
    }

    /**
     * 验证用户的账号密码是否与数据库的一致
     * @param user 需要验证的用户
     * @return result 验证的结果
     */
    private boolean isUserValid(User user) {
        // 检索出数据中的对应用户
        User _user = retrieveUser(user);
        // 验证账号密码是否一致
        return _user.getUser().equals(user.getUser())
                && _user.getPass().equals(user.getPass());
    }
    
    /**
     * 添加用户的登录信息(地址,端口号)
     * @param client 对应的客户端
     * @param recvMsg 接收到的登录消息
     */
    private void addToLogList(Client client, Message recvMsg) {
        String content = recvMsg.getContent();  // 接受到的消息内容
        String user = MyStringUtils.get("user", content);                   // 登录用户名
        String addr = client.getAddress();                                  // 登录地址
        int port = Integer.parseInt(MyStringUtils.get("port", content));    // 登录端口
        logList.put(user, new InetSocketAddress(addr, port));               // 将用户登录信息添加到在线用户清单
    }

    /**
     * 广播所有客户刷新在线客户列表
     */
    private void broadcasting() {
        // 当在线用户不为空
        if (!logList.isEmpty()) {
            // 创建在线客户列表的广播消息
            Message msg = new Message(logList);
            // 广播刷新消息
            for (Client client : broadcastList)
                this.sendMessage(client, msg);
        }
    }
    
    /**
     * 接收客户端的tcp消息
     * @param client 客户端
     * @return Message 接收到的消息
     */
    private Message receiveMessage(Client client) {
        return client.receiveMessage();
    }
    
    /**
     * 向客户端发送tcp消息
     * @param client 客户端
     * @param sndMsg 发送消息
     */
    private void sendMessage(Client client, Message sndMsg) {
        client.sendMessage(sndMsg);
    }
    
    /**
     * 处理tcp消息的接收器内部类
     * @author 富澎
     *
     */
    private class Receiver implements Runnable {

        Client client;
        
        public Receiver(Client client) {
            this.client = client;
        }

        public void run() {
            while (true) {
                Message recvMsg = receiveMessage(client); // 接收客户的操作消息
                if (recvMsg == null) break; // 客户端断开,退出
                Message sndMsg = null;
                if (Operator.REGISTER == recvMsg.getOp()) {     // 注册操作
                    // 根据注册消息得到用户信息
                    User user = new User(recvMsg);
                    if (!isUserExsits(user)) {                  // 注册用户不存在
                        createUser(user);                                    // 创建新用户
                        sndMsg = new Message(Operator.REGISTER, Result.YES, user);  // 创建注册成功消息
                    } else {                                    // 用户存在,创建用户失败
                        sndMsg = new Message(Operator.REGISTER, Result.NO);         // 创建注册失败消息
                    }
                    sendMessage(client, sndMsg); // 将注册结果消息发给客户端
                } else if (Operator.LOGIN == recvMsg.getOp()) { // 登录操作
                    // 根据登录消息得到用户信息
                    User user = new User(recvMsg);
                    if (!isUserExsits(user)) {   // 不存在该登录用户
                        sndMsg = new Message(Operator.LOGIN, Result.NO);
                        sendMessage(client, sndMsg);
                    } else {                            // 存在该登录用户
                        if (!isUserValid(user)) {// 但账号密码不一致,登录失败
                            // 将登录失败消息发给客户端
                            sndMsg = new Message(Operator.LOGIN, Result.NO);
                            sendMessage(client, sndMsg);
                        }
                        else {                          // 登录成功
                            // 把客户端添加到广播列表
                            broadcastList.add(client);
                            // 将登录信息添加到登录清单
                            addToLogList(client, recvMsg);
                            // 检索用户信息
                            User _user = retrieveUser(user);
                            client.setUer(_user);
                            // 将登录成功消息和用户信息发给客户端
                            sndMsg = new Message(Operator.LOGIN, Result.YES, _user);
                            sendMessage(client, sndMsg);
                            // 广播所有客户刷新在线用户列表
                            broadcasting();
                        }
                    }
                }
            }
            // 客户端断开连接的处理
            // 删除广播列表所在的客户端
            broadcastList.remove(client);
            // 删除登录清单所在客户信息
            logList.remove(client.getName());
            // 广播所有客户刷新在线用户列表
            broadcasting();
        }
    }
}
