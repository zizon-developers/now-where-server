package com.spring.userservice.dto;

import com.spring.userservice.vo.ResponseOrder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class UserDto {
    private String email;
    private String name;
    private String password;
    private String userId;
    private Date createDate;

    private String encryptedPassword;

    private List<ResponseOrder> orders;

}
