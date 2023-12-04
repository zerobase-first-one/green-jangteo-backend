package com.firstone.greenjangteo.user.service;

import com.firstone.greenjangteo.user.dto.AddressDto;
import com.firstone.greenjangteo.user.model.entity.User;
import com.firstone.greenjangteo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

import static com.firstone.greenjangteo.user.excpeption.message.NotFoundExceptionMessage.USER_ID_NOT_FOUND_EXCEPTION;
import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;

/**
 * 회원 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(isolation = READ_COMMITTED, timeout = 10)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional(isolation = READ_COMMITTED, readOnly = true, timeout = 10)
    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(USER_ID_NOT_FOUND_EXCEPTION + id));
    }

    @Override
    public void updateAddress(Long id, AddressDto addressDto) {
        User user = getUser(id);

        user.updateAddress(addressDto);
    }
}
