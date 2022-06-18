package abertay.uad.ac.uk.coursework;

public class User {

    public static final String COL_PATH = "users";
    public static final String USERNAME = "username";
    public static final String USERID = "userID";
    public static final String EMAIL = "email";

    private String username;
    private String email;
    private String userID;

    public User(String username, String email, String userID) {
        this.username = username;
        this.email = email;
        this.userID = userID;
    }

    public String getEmail(){return email;}
    public String getUsername(){return username;}
    public String getID(){return userID;}


}