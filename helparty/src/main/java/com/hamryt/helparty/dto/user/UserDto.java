package com.hamryt.helparty.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class UserDto {

    private Long id;

    private String email;

    private String name;

    private String password;

    private String addressCode;

    private String addressDetail;

    @Builder
    public UserDto(String email, String name, String password, String addressCode,
        String addressDetail) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.addressCode = addressCode;
        this.addressDetail = addressDetail;
    }
}
