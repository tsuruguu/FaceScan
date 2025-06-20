package com.faceScan.dao;

import com.faceScan.model.User;

public interface IUserDAO {
    User loginUser(String username, String password);
    boolean registerUser(User user);
}
