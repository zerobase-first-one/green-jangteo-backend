package com.firstone.greenjangteo.user.repository;

import com.firstone.greenjangteo.user.model.Email;
import com.firstone.greenjangteo.user.model.Phone;
import com.firstone.greenjangteo.user.model.Username;
import com.firstone.greenjangteo.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(Email email);

    boolean existsByUsername(Username username);

    boolean existsByPhone(Phone phone);

    Optional<User> findByEmail(Email email);

    Optional<User> findByUsername(Username username);

    @Query("SELECT u.id FROM users u")
    List<Long> findAllUserIds();
}
