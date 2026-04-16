package com.vasu.conference_management.repository;

import com.vasu.conference_management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameIgnoreCase(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailIgnoreCase(String email);
    boolean existsByUsername(String username);
    boolean existsByUsernameIgnoreCase(String username);
    boolean existsByEmail(String email);
    boolean existsByEmailIgnoreCase(String email);

    @Query("select count(u) from User u")
    long countAllUsers();

    @Query("select count(distinct u.id) from User u join u.roles r where upper(r.roleName) = upper(:roleName)")
    long countUsersByRoleName(@Param("roleName") String roleName);
}

