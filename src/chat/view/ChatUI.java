package chat.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import chat.controller.Client;

/**
 * 聊天界面
 * @author 钟富澎
 *
 */
public class ChatUI extends JFrame {

    private JButton sndBtn;
    private JTextField sndContent;
    private JTextArea chatContent;

    private JList<String> contactList;
    private DefaultListModel<String> model;
    private Client client;

    public ChatUI(Client client) {
        super();
        
        this.client = client;
        
        sndBtn = new JButton("发送信息");
        sndContent = new JTextField(20);
        chatContent = new JTextArea();
        model = new DefaultListModel<String>() { };
        contactList = new JList<String>(model);
        
        JPanel southPanel = new JPanel(new FlowLayout());
        southPanel.add(sndContent);
        southPanel.add(sndBtn);
        this.add(southPanel, BorderLayout.SOUTH);
        
        chatContent.setEditable(false);
        JScrollPane sp1 = new JScrollPane(chatContent);
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(sp1);
        this.add(centerPanel, BorderLayout.CENTER);
        
        JPanel eastPanel = new JPanel(new BorderLayout());
        JScrollPane sp2 = new JScrollPane(contactList);
        eastPanel.add(sp2);
        this.add(eastPanel, BorderLayout.EAST);
        
        // 注册监听器
        addListener();
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(700, 500);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    /**
     * 更新在线用户名列表
     * @param list 在线用户名列表
     */
    public void update(List<String> list) {
        model.clear();
        for (String user : list)
            model.addElement(user);
    }

    /**
     * 更新聊天消息
     * @param content
     * @param user 
     */
    public void updateContont(String content, String user) {
        chatContent.append(user + " " + new Date() + "\n    " + content + "\n");
    }
    
    /**
     * 注册监听器
     */
    private void addListener() {
        sndBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 获取发送消息
                String content = sndContent.getText();
                // 追加发送消息到聊天界面
                chatContent.append(client.getName() + " " + new Date() + "\n    " + content + "\n");
                // 发送消息
                client.sendChatMsg(content);
            }
        });
        contactList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == true) {
                    String user = contactList.getSelectedValue();
                    // 指定发送人
                    client.setReceiver(user);
                }                
            }
        });
    }
}