package com.heng.property_manager.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class LoginUser implements UserDetails {

    private User user;

    private List<String> permissions;

    private List<SimpleGrantedAuthority> authorities;

    public LoginUser(User user, List<String> permissions) {
        this.user = user;
        this.permissions = permissions;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (authorities != null) {
            return authorities;
        }
        authorities = permissions.stream()
                .map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        for (String permission : permissions) {

        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
