package org.SitekickRemastered.utils;

import java.util.ArrayList;

public class UserController {

    public static ArrayList<User> userList = new ArrayList<>();


    public UserController() { }


    public void addUser(User user) {
        userList.add(user);
    }


    public User getUser(String Name) {
        for (User user : userList) {
            if (user.getName().equals(Name))
                return user;
        }
        return null;
    }


    public boolean userExists(User user) {
        return userList.contains(user);
    }
}
