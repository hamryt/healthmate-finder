package com.hamryt.helparty.service;

import com.hamryt.helparty.dto.UserDto;

public interface UserService {

    void insertUser(UserDto userDto);

    UserDto findUserByEmailAndPassword(String email, String password);

}
