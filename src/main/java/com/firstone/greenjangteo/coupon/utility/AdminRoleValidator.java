package com.firstone.greenjangteo.coupon.utility;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import static com.firstone.greenjangteo.exception.message.AccessDeniedMessage.ADMIN_ONLY;
import static com.firstone.greenjangteo.user.model.Role.ROLE_ADMIN;

public class AdminRoleValidator {
    public static void checkAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority(ROLE_ADMIN.name()))) {
            return;
        }

        throw new AccessDeniedException(ADMIN_ONLY);
    }
}
