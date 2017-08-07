package chat.model;

public enum Operator {
    
    REGISTER, LOGIN, REFRESH, GETLIST, GETADDR;
    
    public String toString() {
        switch (this) {
            case REGISTER:  return "register";
            case LOGIN:     return "login";
            case REFRESH:   return "refresh";
            case GETLIST:   return "getList";
            case GETADDR:   return "getAddr";
        }
        return null;
    }
    
    public static Operator findOp(String op) {
        switch (op) {
            case "register":  return REGISTER;
            case "login":     return LOGIN;
            case "refresh":   return REFRESH;
            case "getList":   return GETLIST;
            case "getAddr":   return GETADDR;
        }
        return null;
    }
}
