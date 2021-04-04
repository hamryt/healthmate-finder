package com.hamryt.helparty.service.login;

import com.hamryt.helparty.dto.user.UserDTO;
import com.hamryt.helparty.exception.login.LoginUserDoesNotMatchException;
import com.hamryt.helparty.exception.login.NoLoginAuthException;
import com.hamryt.helparty.exception.user.UserNotFoundException;
import com.hamryt.helparty.service.user.UserService;
import com.hamryt.helparty.util.SessionKeys;
import com.mysql.cj.Session;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class LoginServiceImpl implements LoginService {

    private final Encryptor encryptor;
    private final UserService userService;
    private final HttpSession session;

    @Transactional
    public UserDTO login(String email, String password) {

        String encryptPassword = encryptor.encrypt(password);
        UserDTO userDto = userService.findUserByEmailAndPassword(email, encryptPassword);

        if (userDto != null) {
            session.setAttribute(SessionKeys.LOGIN_USER_EMAIL, userDto.getEmail());
        } else {
            throw new UserNotFoundException(userDto.toString());
        }

        return userDto;
    }

    public void sessionValidate() {
        String userEmail = (String) session.getAttribute(SessionKeys.LOGIN_USER_EMAIL);

        if (userEmail == null) {
            throw new NoLoginAuthException();
        }
    }

    @Transactional(readOnly = true)
    public void validateUser(Long id) {
        String userEmail = (String) session.getAttribute(SessionKeys.LOGIN_USER_EMAIL);
        UserDTO user = userService.getUserById(id);
        if (!user.getEmail().equals(userEmail)) {
            throw new LoginUserDoesNotMatchException(userEmail);
        }
    }

    public String getSessionEmail(){
        return (String) session.getAttribute(SessionKeys.LOGIN_USER_EMAIL);
    }

}