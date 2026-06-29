package com.dyma.tennis.features.accounts.db;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findOneWithRolesByLoginIgnoreCase(String login);
}
