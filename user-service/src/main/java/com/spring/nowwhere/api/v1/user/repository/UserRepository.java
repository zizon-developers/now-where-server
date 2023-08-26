package com.spring.nowwhere.api.v1.user.repository;

import com.spring.nowwhere.api.v1.user.entity.User;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByCheckId(String checkId);
    Optional<User> findByEmail(String username);
    Optional<User> findByName(String name);
    Optional<User> findByRemittanceId(String remittanceId);

    List<User> findAll();

//    @Query("SELECT u FROM User u WHERE u.userId IN (:bettorId, :receiverId)" +
//            "ORDER BY FIELD(u.userId, :bettorId, :receiverId)")
//    List<User> findBettorAndReceiver(@Param("bettorId") String bettorId,
//                                     @Param("receiverId") String receiverId);
}
