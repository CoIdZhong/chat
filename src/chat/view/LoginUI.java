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
 * 感谢陈志杰同学无私提供登录界面
 * 
 * @author 陈志杰
 *
 */
public class LoginUI extends JFrame {

    private JTextField text1;
    private JPasswordField text2;
    private JButton button1;
    private JButton button2;
    
    private ClientMainApp clientMainApp;
    private Client client;
    private RegisterUI regUI;

    public LoginUI(ClientMainApp clientMainApp, Client client) {
        super("登陆界面");
        
        this.clientMainApp = clientMainApp;
        this.client = client;
        
        this.setSize(400, 300);
        this.setResizable(false);
        this.setLayout(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // 账号
        JLabel label1 = new JLabel("账号：");
        label1.setBounds(80, 90, 100, 40);
        this.add(label1);
        text1 = new JTextField();
        text1.setBounds(120, 95, 150, 30);
        this.add(text1);
        // 密码
        JLabel label2 = new JLabel("密码：");
        label2.setBounds(80, 130, 100, 40);
        this.add(label2);
        text2 = new JPasswordField();
        text2.setBounds(120, 135, 150, 30);
        this.add(text2);
        // 登陆注册
        button1 = new JButton("登陆");
        button1.setBounds(90, 175, 90, 30);
        this.add(button1);
        button2 = new JButton("注册账号");
        button2.setBounds(190, 175, 90, 30);
        this.add(button2);
        // 注册监听器
        addListener();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
    
    /**
     * 从登录界面得到登录用户信息
     * @return User 登录用户
     */
    private User getUserFromLogUI() {
        String user = text1.getText();
        String pass = new String(text2.getPassword());
        return new User(user, pass);
    }
    
    /**
     * 注册监听器
     */
    private void addListener() {
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 连接服务器
                client.connect();
                // 从登录界面得到登录用户信息
                User user = getUserFromLogUI();
                if (!client.log(user)) {    // 登录失败
                    JOptionPane.showMessageDialog(null, "账号或密码错误!", "登录信息", JOptionPane.ERROR_MESSAGE); // 提示登录失败
                } else {                    // 登录成功
                    clientMainApp.getChatUI().setTitle(client.getName());
                    LoginUI.this.setVisible(false);
                    clientMainApp.getChatUI().setVisible(true);
                    JOptionPane.showMessageDialog(null, "登录成功!", "登录信息", JOptionPane.INFORMATION_MESSAGE); // 提示登录成功
                }
            }
        });

        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(regUI == null) {
                    regUI = new RegisterUI(clientMainApp, LoginUI.this, client);
                    clientMainApp.setRegUI(regUI);
                }
                LoginUI.this.setVisible(false);
                regUI.setVisible(true);
            }
        });
    }
}