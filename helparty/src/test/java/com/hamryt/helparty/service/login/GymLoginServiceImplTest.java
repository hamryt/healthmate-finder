package com.hamryt.helparty.service.login;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import com.hamryt.helparty.dto.UserType;
import com.hamryt.helparty.dto.gym.GymDTO;
import com.hamryt.helparty.exception.common.UserTypeDoesNotMatchException;
import com.hamryt.helparty.service.gym.GymServiceImpl;
import com.hamryt.helparty.service.session.Encryptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;

@ExtendWith(MockitoExtension.class)
class GymLoginServiceImplTest {
    
    @InjectMocks
    private GymLoginServiceImpl loginService;
    
    @Mock
    private GymServiceImpl gymService;
    
    @Mock
    private Encryptor encryptor;
    
    @Mock
    private MockHttpSession session;
    
    String email = "test@example.com";
    String password = "123";
    
    
    @Test
    @DisplayName("운동시설 관리자 로그인 성공")
    public void loginGym_Success() {
        
        String encryptedPassword = encryptor.encrypt(password);
        String gymName = "test";
        String phoneNumber = "01012345678";
        String addressCode = "0123";
        UserType userType = UserType.GYM;
        
        GymDTO mockGym = GymDTO.builder()
            .email(email)
            .password(encryptedPassword)
            .gymName(gymName)
            .phoneNumber(phoneNumber)
            .addressCode(addressCode)
            .userType(userType)
            .build();
        
        given(gymService.findGymByEmailAndPassword(eq(email), eq(encryptedPassword)))
            .willReturn(mockGym);
        
        GymDTO gymDTO = loginService.login(email, password);
        
        assertEquals(gymDTO.getEmail(), email);
    }
    
    @Test
    @DisplayName("운동시설 관리자 로그인 실패 : 유저 타입 불일치 예외")
    public void loginGym_Fail_UserTypeDoesNotMatchException() {
        
        String encryptedPassword = encryptor.encrypt(password);
        String gymName = "test";
        String phoneNumber = "01012345678";
        String addressCode = "0123";
        UserType userType = UserType.USER;
        
        GymDTO mockGym = GymDTO.builder()
            .email(email)
            .password(encryptedPassword)
            .gymName(gymName)
            .phoneNumber(phoneNumber)
            .addressCode(addressCode)
            .userType(userType)
            .build();
        
        given(gymService.findGymByEmailAndPassword(eq(email), eq(encryptedPassword)))
            .willReturn(mockGym);
        
        UserTypeDoesNotMatchException userTypeDoesNotMatchException
            = assertThrows(UserTypeDoesNotMatchException.class,
            () -> loginService.login(email, password));
        
        assertEquals("userType dose not match does not match with : GYM",
            userTypeDoesNotMatchException.getMessage());
    }
    
}