package chat.model;
import chat.utils.MyStringUtils;

public class User {
    
    private String user;
    private String pass;
    private String nickname;
    private String mark;
    
    public User(Message msg) {
        if (MyStringUtils.contains("user", msg.getContent()))
            user = MyStringUtils.get("user", msg.getContent());
        if (MyStringUtils.contains("pass", msg.getContent()))
            pass = MyStringUtils.get("pass", msg.getContent());
        if (MyStringUtils.contains("nickname", msg.getContent()))
            nickname = MyStringUtils.get("nickname", msg.getContent());
        if (MyStringUtils.contains("mark", msg.getContent()))
            mark = MyStringUtils.get("mark", msg.getContent());
    }

    public User(String user, String pass, String nickname, String mark) {
        this(user, pass);
        this.nickname = nickname;
        this.mark = mark;
    }
    
    public User(String user, String pass) {
        this.user = user;
        this.pass = pass;
    }

    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }

    public String toString() {
        return "user:" + user + ",pass:" + pass + ",nickname:" + nickname + ",mark:" + mark;
    }
}
