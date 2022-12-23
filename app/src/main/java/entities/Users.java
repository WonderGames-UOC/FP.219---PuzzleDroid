package entities;

import java.util.ArrayList;

public class Users {
    ArrayList<String> id;
    ArrayList<User> users;
    public Users(ArrayList<String> id, ArrayList<User> users){
        this.id  = id;
        this.users = users;
    }
}
