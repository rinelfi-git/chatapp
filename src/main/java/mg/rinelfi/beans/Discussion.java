package mg.rinelfi.beans;

public class Discussion {
    private User user;
    private String message;
    
    public Discussion() {
        this.message = "";
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}
