package com.firstone.greenjangteo.user.model.embedment;

import com.firstone.greenjangteo.user.model.Role;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.firstone.greenjangteo.user.excpeption.message.BlankExceptionMessage.ROLE_NO_VALUE_EXCEPTION;
import static com.firstone.greenjangteo.user.excpeption.message.InvalidExceptionMessage.INVALID_ROLE_EXCEPTION;

@Embeddable
public class Roles {
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private List<Role> roles;

    private Roles(List<Role> roles) {
        this.roles = roles;
    }

    public static Roles from(List<String> roles) {
        validate(roles);

        List<Role> parsedRoles = new ArrayList<>();
        for (String role : roles) {
            parsedRoles.add(parseRole(role));
        }

        return new Roles(parsedRoles);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Roles roles1 = (Roles) o;
        return Objects.equals(roles, roles1.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roles);
    }

    public Role get(int index) {
        return roles.get(index);
    }

    private static void validate(List<String> roles) {
        if (roles == null || roles.isEmpty()) {
            throw new IllegalArgumentException(ROLE_NO_VALUE_EXCEPTION);
        }
    }

    private static Role parseRole(String role) {
        try {
            return Role.valueOf(role);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(INVALID_ROLE_EXCEPTION + role);
        }
    }
}
