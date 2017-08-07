package chat.model;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;

import chat.utils.MyStringUtils;

// 操作消息
public class Message {
    
    private Operator op;
    private Result rs;
    private String content;
    
    public Message(String content) {
        if (containsOp(content)) {
            this.op = findOp(content);
            if (MyStringUtils.contains("result", content))
                this.rs = findRs(content);
        }
        this.content = content;
    }

    public Message(Operator op, Result rs, User user) {
        this.op = op;
        this.rs = rs;
        this.content = "{op:" + op + ",result:" + rs + "," + user + "}";
    }

    public Message(Operator op, Result rs) {
        this.op = op;
        this.rs = rs;
        this.content = "{op:" + op + ",result:" + rs + "}";
    }

    public Message(Operator op, User user) {
        this.op = op;
        this.content = "{op:" + op + "," + user + "}";
    }
    
    public Message(Operator op, User user, int port) {
        this.op = op;
        this.content = "{op:" + op + ",user:" + user.getUser() + ",pass:" + user.getPass() + ",port:"+ port + "}";
    }

    public Message(Operator op, String user) {
        this.op = op;
        this.content = "{op:" + op + ",user:" + user + "}";
    }

    public Message(Map<String, SocketAddress> loginList) {
        String content = "";
        for (String user : loginList.keySet()) {
            InetSocketAddress address = (InetSocketAddress) loginList.get(user);
            String addr = address.getHostString();
            int port = address.getPort();
            content += "{user:" + user + ",addr:" + addr + ",port:" + port + "},";
        }
        this.content = content.substring(0, content.length() - 1);
    }

    private boolean containsOp(String line) {
        return line.indexOf("{op:") == 0;
    }
    
    private Operator findOp(String line) {
        return Operator.findOp(MyStringUtils.get("op", line));
    }
    
    private Result findRs(String line) {
        return Result.findRs(MyStringUtils.get("result", line));
    }
    
    public Operator getOp() {
        return op;
    }

    public Result getRs() {
        return rs;
    }

    public String getContent() {
        return content;
    }
}
