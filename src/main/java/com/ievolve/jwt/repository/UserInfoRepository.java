package com.ievolve.jwt.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ievolve.jwt.model.UserInfo;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, Integer>    {
    Optional<UserInfo> findByUsername(String username);
}
