package chat.model;

public enum Result {

    YES,NO;
    
    public String toString() {
        switch (this) {
            case YES:  return "yes";
            case NO:     return "no";
        }
        return null;
    }

    public static Result findRs(String rs) {
        switch (rs) {
            case "yes":  return YES;
            case "no":     return NO;
        }
        return null;
    }
}
