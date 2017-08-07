package chat.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import chat.controller.Client;
import chat.main.ClientMainApp;
import chat.model.User;

/**
 * 
 * @author 陈志杰
 * 注册界面
 * 感谢陈志杰同学无私提供用户界面
 *
 */
public class RegisterUI extends JFrame {
    
    private JTextField text1;
    private JPasswordField text2;
    private JTextField text3;
    private JTextField text4;
    private JButton button1;
    private JButton button2;
    
    private ClientMainApp clientMainApp;
    private LoginUI logUI;
    private Client client;
    
    public RegisterUI(ClientMainApp clientMainApp, LoginUI logUI, Client client){
        super("注册界面");
        this.clientMainApp = clientMainApp;
        this.logUI = logUI;
        this.client = client;
        
        this.setSize(400, 400);
        this.setResizable(false);
        this.setLayout(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //账号
        JLabel label1 = new JLabel("账号：");
        label1.setBounds(80, 30, 100, 40);
        this.add(label1);
        text1 = new JTextField();
        text1.setBounds(120, 35, 160, 30);
        this.add(text1);
        //密码
        JLabel label2 = new JLabel("密码：");
        label2.setBounds(80, 70, 100, 40);
        this.add(label2);
        text2 = new JPasswordField();
        text2.setBounds(120, 75, 160, 30);
        this.add(text2);
        //昵称
        JLabel label3 = new JLabel("昵称：");
        label3.setBounds(80, 150, 100, 40);
        this.add(label3);
        text3 = new JTextField();
        text3.setBounds(120, 155, 160, 30);
        this.add(text3);
        //个人签名
        JLabel label4= new JLabel("签名：");
        label4.setBounds(80, 190, 100, 40);
        this.add(label4);
        text4= new JTextField();
        text4.setBounds(120, 195, 160, 30);
        this.add(text4);
        //登陆注册
        button1 = new JButton("登陆已有账号");
        button1.setBounds(80, 260, 120, 30);
        this.add(button1);
        button2 = new JButton("注册账号");
        button2.setBounds(210, 260, 120, 30);
        this.add(button2);
        // 注册监听器
        addListener();
        this.setLocationRelativeTo(null);
        this.setVisible(false);
    }
    
    /**
     * 根据注册界面生成用户
     * @return User 申请注册的用户信息
     */
    private User getUserFromRegUI() {
        // 获取账号,密码,昵称,签名
        String user = text1.getText();
        String pass = new String(text2.getPassword());
        String nickname = text3.getText();
        String mark = text4.getText();
        User _user = new User(user, pass, nickname, mark);
        return _user;
    }
    
    /**
     * 注册监听器
     */
    private void addListener() {
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RegisterUI.this.logUI .setVisible(true);
                RegisterUI.this.setVisible(false);
            }
        });
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 连接服务器
                client.connect();
                // 从注册界面得到注册用户信息
                User user = getUserFromRegUI();
                if (!client.reg(user)) {    // 注册失败
                    JOptionPane.showMessageDialog(null, "该账号已存在!", "注册信息", JOptionPane.ERROR_MESSAGE); // 提示注册失败
                } else {                    // 注册成功,进行登录
                    client.logAfterReg();
                    clientMainApp.getChatUI().setTitle(client.getName());
                    RegisterUI.this.setVisible(false);
                    clientMainApp.getChatUI().setVisible(true);
                    JOptionPane.showMessageDialog(null, "注册并登录成功!", "登录信息", JOptionPane.INFORMATION_MESSAGE); // 提示登录成功
                }
            }
        });
    }
}