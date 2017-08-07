package chat.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import chat.main.ClientMainApp;
import chat.model.Message;
import chat.model.Operator;
import chat.model.Result;
import chat.model.User;
import chat.utils.MyStringUtils;

/**
 * 客户端
 * @author 富澎
 *
 */
public class Client{
    
    private static final String REMOTE_HOST  = "192.168.0.246"; // 远程tcp服务器地址
    private static final int TCP_SERVER_PORT = 30000;           // 远程tcp服务器端口号
    private static final Pattern PATTERN = Pattern.compile("\\{(\\w+:(\\w|\\.)+,?)+\\}"); // 匹配模式
    
    private Map<String, SocketAddress> logList; // 登录清单
    private SocketAddress receiver;             // 接收者
    private ClientMainApp clientMainApp;        // 客户端主程序
    private Socket tcpSocket;                   // tcp连接
    private DatagramSocket udpSocket;           // udp连接
    private User user;                          // 当前用户
    
    private PrintWriter writer;                 // 当前输出流
    private BufferedReader reader;              // 当前输入流
    
    public Client(ClientMainApp clientMainApp) {
        this.clientMainApp = clientMainApp;
    }
    
    public Client(Socket tcpSocket) {
        this.tcpSocket = tcpSocket;
    }

    
    
    /**
     * 获取输入流
     * @return reader
     */
    private BufferedReader reader() {
        if (reader == null)
            try { reader = new  BufferedReader(new InputStreamReader(tcpSocket.getInputStream())); }
            catch (IOException e) { e.printStackTrace(); }
        return reader;
    }
    
    /**
     * 注册
     * @param user 注册的用户信息
     * @return result 注册结果
     */
    public boolean reg(User user) {
        // 从注册界面获取用户信息
        this.user = user;
        // 向服务器发送注册消息
        sendRegMessage(this.user);
        // 接收服务器返回信息
        Message recvMsg = receiveMessage();
        // 返回注册结果
        return recvMsg.getRs() == Result.YES;
    }

    /**
     * 在注册之后用户登录
     */
    public void logAfterReg() {
        logList = new HashMap<String, SocketAddress>();
        // 向服务器发送登录信息
        this.sendLogMsg(this.user);
        // 接收服务器返回信息
        receiveMessage();
        // 启动刷新在线客户线程
        new Thread(new Refresher()).start();
        // 启动接收聊天消息线程
        new Thread(new Receiver()).start();
    }
    
    /**
     * 登录
     * @param user 注册的用户信息
     * @return result 登录结果
     */
    public boolean log(User user) {
        // 设置登录用户
        this.user = user;
        logList = new HashMap<String, SocketAddress>();
        // 向服务器发送登录用户消息
        this.sendLogMsg(this.user);
        // 接收服务器返回信息
        Message recvMsg = receiveMessage();
        if (recvMsg.getRs() == Result.YES) { // 登录成功
            // 更新用户信息
            this.user = new User(recvMsg);
            // 启动刷新在线客户线程
            new Thread(new Refresher()).start();
            // 启动接收聊天消息线程
            new Thread(new Receiver()).start();
            return true;
        } else return false; // 登录失败
    }
    
    /**
     * 向服务器发送注册用户信息
     * @param user 要注册的用户信息
     */
    private void sendRegMessage(User user) {
        this.sendMessage(new Message(Operator.REGISTER, user)); // 向服务器发送注册用户消息
    }
    
    /**
     * 向服务器发送登录用户消息
     * @param user 用户,包含账号和密码
     * @param addr 用户的本地地址
     * @param port 用户的本地端口号
     */
    private void sendLogMsg(User user) {
        try {
            // 建立udp连接
            DatagramSocket ds = new DatagramSocket();
            this.udpSocket = ds;
            // 获取udp端口号
            int port = ds.getLocalPort();
            // 向服务器发送登录信息
            this.sendMessage(new Message(Operator.LOGIN, user, port));
        } catch (Exception e) { e.printStackTrace(); }
    }
    
    /**
     * 接收消息
     * @return 
     */
    public Message receiveMessage() {
        try {
            String line = this.reader().readLine();
            return new Message(line);
        } catch (IOException e) { e.printStackTrace(); }
        return null;
    }
    
    /**
     * 向服务器端发送消息
     * @param sndMsg
     */
    public void sendMessage(Message sndMsg) {
        this.writer().println(sndMsg.getContent());
        this.writer().flush();
    }
    
    /**
     * 获取输出流
     * @return writer
     */
    private PrintWriter writer() {
        if (writer == null)
            try { writer = new PrintWriter(new OutputStreamWriter(tcpSocket.getOutputStream())); }
            catch (IOException e) { e.printStackTrace(); }
        return writer;
    }
    
    /**
     * 获取登录用户的地址信息
     * @return address 地址
     */
    public String getAddress() {
        return tcpSocket.getInetAddress().getHostAddress();
    }

    /**
     * 连接服务器
     */
    public void connect() {
        if (tcpSocket == null) {
            try {
                tcpSocket = new Socket(REMOTE_HOST, TCP_SERVER_PORT);
            }
            catch (UnknownHostException e) { e.printStackTrace(); }
            catch (IOException e) { e.printStackTrace(); }
        }
    }
    
    /**
     * 获取用户名
     * @return name 本客户端用户名
     */
    public String getName() {
        return user.getUser();
    }
    
    /**
     * 指定发送人
     * @param user 用户名
     */
    public void setReceiver(String user) {
        receiver = logList.get(user);
    }
    
    /**
     * 发送聊天消息
     * @param content 文本内容
     */
    public void sendChatMsg(String content) {
        // 对聊天信息进行编码
        String msg = "{msg:" + content + ",user:" + user.getUser() + "}";
        try {
            udpSocket.send(new DatagramPacket(msg.getBytes(), msg.getBytes().length, receiver));
        } catch (IOException e) { e.printStackTrace(); }
    }
    
    /**
     * 客户端接收器线程
     * @author 富澎
     *
     */
    class Receiver implements Runnable {

        public static final int _1_MB = 1024 * 1024; // 1MB的数据包
        
        public void run() {
            DatagramPacket packet = new DatagramPacket(new byte[_1_MB], _1_MB);
            while (true)
                try {
                    // 接收udp数据包
                    udpSocket.receive(packet);
                    // 对聊天信息进行解码
                    String msg = new String(packet.getData(), 0, packet.getLength());
                    String content = MyStringUtils.get("msg", msg);
                    String user = MyStringUtils.get("user", msg);
                    // 将消息显示到聊天界面
                    clientMainApp.getChatUI().updateContont(content, user);
                } catch (IOException e) { e.printStackTrace(); }
        }
        
    }

    /**
     * 刷新用户列表线程
     * @author 富澎
     *
     */
    private class Refresher implements Runnable {
        
        public void run() {
            while (true) {
                List<String> contacts = new LinkedList<String>();
                // 从服务器获取登录信息
                Message refreshMsg = receiveMessage();
                String content = refreshMsg.getContent();
                Matcher matcher = PATTERN.matcher(content);
                // 更新登录列表
                while (matcher.find()) {
                    String _user = matcher.group();
                    if (!containsLogUser(_user)) { // 如果不存在该用户的登录信息,添加到登录清单
                        String user = MyStringUtils.get("user", _user);
                        String addr = MyStringUtils.get("addr", _user);
                        int port = Integer.parseInt(MyStringUtils.get("port", _user));
                        addToLogList(user, new InetSocketAddress(addr, port));
                        // 增加到通讯录
                        contacts.add(user);
                    }
                }
                // 更新聊天界面的用户清单
                clientMainApp.getChatUI().update(contacts);
            }
        }
    }
    
    /**
     * 将登录用户添加到在线用户清单
     * @param user 用户名
     * @param address 登录地址
     */
    public void addToLogList(String user, InetSocketAddress address) {
        logList.put(user, address);
    }

    /**
     * 验证是否存在其登录用户
     * @param user 用户名
     * @return result 验证结果
     */
    public boolean containsLogUser(String user) {
        return logList.containsKey(user);
    }
}
