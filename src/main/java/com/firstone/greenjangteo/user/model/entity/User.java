package com.firstone.greenjangteo.user.model.entity;

import com.firstone.greenjangteo.audit.BaseEntity;
import com.firstone.greenjangteo.coupon.model.entity.Coupon;
import com.firstone.greenjangteo.user.dto.AddressDto;
import com.firstone.greenjangteo.user.form.SignUpForm;
import com.firstone.greenjangteo.user.model.Email;
import com.firstone.greenjangteo.user.model.FullName;
import com.firstone.greenjangteo.user.model.Phone;
import com.firstone.greenjangteo.user.model.Username;
import com.firstone.greenjangteo.user.model.embedment.Address;
import com.firstone.greenjangteo.user.model.embedment.Roles;
import com.firstone.greenjangteo.user.model.security.Password;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static javax.persistence.CascadeType.*;

@Entity(name = "users")
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
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

    @Column
    private LocalDateTime lastLoggedInAt;

    @OneToMany(mappedBy = "user", cascade = {PERSIST, MERGE, REMOVE}, fetch = FetchType.LAZY)
    private List<Coupon> coupons;


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

    public void updateLoginTime() {
        lastLoggedInAt = LocalDateTime.now();
    }

    public void updateAddress(AddressDto addressDto) {
        address = Address.from(addressDto);
    }

    public void updateEmail(String email) {
        this.email = Email.of(email);
    }

    public void updatePhone(String phone) {
        this.phone = Phone.of(phone);
    }

    public void updatePassword(String passwordToChange, PasswordEncoder passwordEncoder) {
        password = Password.from(passwordToChange, passwordEncoder);
    }
}
