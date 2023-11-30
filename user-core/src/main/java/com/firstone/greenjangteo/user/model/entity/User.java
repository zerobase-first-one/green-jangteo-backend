package com.firstone.greenjangteo.user.model.entity;

import com.firstone.greenjangteo.common.audit.BaseEntity;
import com.firstone.greenjangteo.user.form.SignUpForm;
import com.firstone.greenjangteo.user.model.*;
import com.firstone.greenjangteo.user.model.embedment.Address;
import com.firstone.greenjangteo.user.model.embedment.Roles;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.Objects;

@Entity(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Convert(converter = Email.EmailConverter.class)
    @Column(nullable = false, unique = true, length = 30)
    private Email email;

    @Convert(converter = Username.UsernameConverter.class)
    @Column(nullable = false, unique = true, length = 20)
    private Username username;

    @Convert(converter = Password.PasswordConverter.class)
    @Column(nullable = false)
    private Password password;

    @Convert(converter = FullName.FullNameConverter.class)
    @Column(nullable = false, length = 5)
    private FullName fullName;

    @Convert(converter = Phone.PhoneConverter.class)
    @Column(unique = true, length = 11)
    private Phone phone;

    @Embedded
    private Address address;

    @Embedded
    private Roles roles;

    @Builder
    private User(Long id, Email email, Username username, Password password,
                 FullName fullName, Phone phone, Address address, Roles roles) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
        this.roles = roles;
    }

    public static User from(SignUpForm signUpForm, PasswordEncoder passwordEncoder) {
        return User.builder()
                .email(Email.of(signUpForm.getEmail()))
                .username(Username.of(signUpForm.getUsername()))
                .password(Password.from(signUpForm.getPassword(), passwordEncoder))
                .fullName(FullName.of(signUpForm.getFullName()))
                .phone(Phone.of(signUpForm.getPhone()))
                .address(Address.from(signUpForm.getAddressDto()))
                .roles(Roles.from(signUpForm.getRoles()))
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(
                email, user.email) && Objects.equals(username, user.username)
                && Objects.equals(fullName, user.fullName) && Objects.equals(phone, user.phone)
                && Objects.equals(address, user.address) && Objects.equals(roles, user.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, username, fullName, phone, address, roles);
    }
}
