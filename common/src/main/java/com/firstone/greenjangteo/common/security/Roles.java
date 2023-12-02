package com.firstone.greenjangteo.common.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Roles {
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
            throw new IllegalArgumentException("회원 분류가 조재하지 않습니다.");
        }
    }

    private static Role parseRole(String role) {
        try {
            return Role.valueOf(role);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("잘못된 형식의 회원 분류입니다. 전송된 회원 분류: " + role);
        }
    }
}
