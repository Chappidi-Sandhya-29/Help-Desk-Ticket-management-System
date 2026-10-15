
package com.helpdesk.dao;

import java.sql.SQLException;
import java.util.List;

import com.helpdesk.model.User;

public interface UserDAO {

    boolean registerUser(User user) throws SQLException;

    User findByEmail(String email) throws SQLException;

    List<User> findUsersByRole(String role) throws SQLException;
}
