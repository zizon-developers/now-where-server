package com.spring.nowwhere.api.v1.entity.user.repository;

import com.spring.nowwhere.api.v1.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByCheckId(String checkId);
    Optional<User> findByEmail(String username);
    Optional<User> findByName(String name);
    Optional<User> findByRemittanceId(String remittanceId);

    @Query("select u from User u left join fetch u.roles " +
            "where u.email = :email")
    Optional<User> getUserByEmailWithRole(@Param("email") String email);

    List<User> findAll();

    @Query("SELECT u FROM User u WHERE u.checkId IN (:senderId, :receiverId)" +
            "ORDER BY FIELD(u.checkId, :senderId, :receiverId)")
    List<User> findSenderAndReceiver(@Param("senderId") String senderId,
                                     @Param("receiverId") String receiverId);
}
