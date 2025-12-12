package com.ievolve.jwt.config;

import java.util.Collection;
import java.util.List;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import com.ievolve.jwt.model.UserInfo;

public class UserInfoUserDetails implements UserDetails{

    private UserInfo userInfo;

    public UserInfo getUserInfo() {
        return userInfo;
    }

    private String name;
    private String password;
    private List<GrantedAuthority> authorities;

    public UserInfoUserDetails(UserInfo userInfo) {
       
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return AuthorityUtils.commaSeparatedStringToAuthorityList(userInfo.getRoles());
    }

    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return null;
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
