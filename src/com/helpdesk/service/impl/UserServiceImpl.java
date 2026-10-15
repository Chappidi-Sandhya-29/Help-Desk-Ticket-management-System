
package com.helpdesk.service.impl;

import java.util.regex.Pattern;

import com.helpdesk.dao.UserDAO;
import com.helpdesk.dao.UserDAOImpl;
import com.helpdesk.model.User;
import com.helpdesk.service.UserService;
import com.helpdesk.util.PasswordUtil;

public class UserServiceImpl implements UserService {

    private final UserDAO userDAO = new UserDAOImpl();

    @Override
    public boolean registerUser(
            String fullName, String email, String password, String role)
            throws Exception {

        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required.");
        }

        if (email == null || !Pattern.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", email.trim())) {
            throw new IllegalArgumentException("Enter a valid email.");
        }

        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException(
                    "Password must contain at least 8 characters.");
        }

        if (role == null) {
            throw new IllegalArgumentException("Role is required.");
        }

        role = role.trim().toUpperCase();

        if (!role.equals("EMPLOYEE")
                && !role.equals("AGENT")
                && !role.equals("ADMIN")) {
            throw new IllegalArgumentException(
                    "Role must be EMPLOYEE, AGENT, or ADMIN.");
        }

        if (userDAO.findByEmail(email.trim()) != null) {
            throw new IllegalArgumentException(
                    "This email is already registered.");
        }

        String hash = PasswordUtil.hashPassword(password);

        User user = new User(
                fullName.trim(), email.trim(), hash, role);

        return userDAO.registerUser(user);
    }

    @Override
    public User loginUser(String email, String password) throws Exception {
        if (email == null || email.trim().isEmpty()
                || password == null || password.isEmpty()) {
            return null;
        }

        User user = userDAO.findByEmail(email.trim());

        if (user != null && PasswordUtil.verifyPassword(
                password, user.getPasswordHash())) {
            return user;
        }

        return null;
    }
}
