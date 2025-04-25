package com.example.booklibrary.dao;

import com.example.booklibrary.model.User;

public class UserDAO extends BaseDAO<User, Integer >{
    public UserDAO() {
        super(User.class);
    }
}
