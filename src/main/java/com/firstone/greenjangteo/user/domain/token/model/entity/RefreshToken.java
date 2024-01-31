package com.firstone.greenjangteo.user.domain.token.model.entity;


import com.firstone.greenjangteo.user.security.JwtTokenProvider;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity(name = "refresh_token")
@Table(name = "refresh_token")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "refresh_token_roles", joinColumns = @JoinColumn(name = "refresh_token_id"))
    @Column(name = "role")
    List<String> roles;

    @Column(nullable = false)
    private String token;

    private RefreshToken(Long userId, List<String> roles, String token) {
        this.userId = userId;
        this.roles = roles;
        this.token = token;
    }

    public static RefreshToken from(Long id, List<String> roles, String token) {
        return new RefreshToken(id, roles, token);
    }

    public String getTokenValue() {
        return token;
    }

    public String createNewAccessToken(JwtTokenProvider jwtTokenProvider) {
        return jwtTokenProvider.generateAccessToken(String.valueOf(userId), roles);
    }
}
