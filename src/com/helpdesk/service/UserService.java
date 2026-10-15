
package com.helpdesk.service;

import com.helpdesk.model.User;

public interface UserService {

    boolean registerUser(
            String fullName, String email, String password, String role)
            throws Exception;

    User loginUser(String email, String password) throws Exception;
}
