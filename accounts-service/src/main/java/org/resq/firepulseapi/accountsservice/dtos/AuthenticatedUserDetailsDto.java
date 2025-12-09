package org.resq.firepulseapi.accountsservice.dtos;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class AuthenticatedUserDetailsDto extends User {
    private final String id;

    public AuthenticatedUserDetailsDto(String id, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.id = id;
    }
}
