package com.jplProject.service;

import com.jplProject.model.UserEntity;

public interface UserService {

    public UserEntity findUserByJwtToken(String jwt) throws Exception;

    public UserEntity findUserByEmail(String email) throws Exception;
}
