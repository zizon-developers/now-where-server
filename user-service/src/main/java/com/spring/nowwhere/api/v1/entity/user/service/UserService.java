package com.spring.nowwhere.api.v1.entity.user.service;

import com.spring.nowwhere.api.v1.auth.dto.TokenDto;
import com.spring.nowwhere.api.v1.entity.bet.dto.BetSummaryDto;
import com.spring.nowwhere.api.v1.entity.user.dto.UserDto;
import com.spring.nowwhere.api.v1.entity.user.User;
import com.spring.nowwhere.api.v1.auth.dto.OAuthUserDto;

import java.util.List;

public interface UserService {
    void logout(String token);

    TokenDto login(OAuthUserDto userDto);

    User updateEmail(OAuthUserDto userDto);

    OAuthUserDto checkAndRegisterUser(OAuthUserDto userDto);

    User reissueWithUserVerification(String email); //

    UserDto updateName(String checkId, String name);

    UserDto updateRemittanceId(String checkId, String remittanceId);

    BetSummaryDto getUserInfoWithBetSummery(String checkId);
}
