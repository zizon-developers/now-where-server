package com.spring.userservice.service;

import com.spring.userservice.dto.UserDto;
import com.spring.userservice.entity.User;
import com.spring.userservice.entity.UserRepository;
import com.spring.userservice.vo.ResponseOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final Environment evn;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
                true, true, true, true,
                new ArrayList<>());
    }

    public UserDto createUser(UserDto userDto) {

        User user = User.builder()
                .userId(UUID.randomUUID().toString())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                .build();
        userRepository.save(user);

        return UserDto.of(user);
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException(userId));

        return UserDto.of(user);
    }

    @Override
    public UserDto getUserDetailsByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));

        return UserDto.of(user);
    }

    @Override
    public List<UserDto> getUserByAll() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(UserDto::of)
                .collect(Collectors.toList());
    }


}
