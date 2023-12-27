package com.firstone.greenjangteo.utility;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import static com.firstone.greenjangteo.exception.message.AccessDeniedMessage.*;
import static com.firstone.greenjangteo.user.model.Role.ROLE_ADMIN;

public class RoleValidator {
    public static void checkAdminAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority(ROLE_ADMIN.name()))) {
            return;
        }

        throw new AccessDeniedException(ADMIN_ONLY);
    }

    public static void checkAdminOrPrincipalAuthentication(String requestedId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        if (requestedId.equals(currentUsername)
                || authentication.getAuthorities().contains(new SimpleGrantedAuthority(ROLE_ADMIN.name()))) {
            return;
        }

        throw new AccessDeniedException(
                ACCESS_DENIED_REQUEST_ID + requestedId + ACCESS_DENIED_LOGIN_ID + currentUsername
        );
    }
}
