package org.scutsu.market.security;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.scutsu.market.models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class UserPrincipal implements UserDetails{
	private Long id;

    private String weChatOpenId;

    @JsonIgnore
    private String email;

    //private Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(Long id, String weChatOpenId, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.weChatOpenId = weChatOpenId;
        //this.authorities = authorities;
    }
    public static UserPrincipal create(User user) {
        /*List<GrantedAuthority> authorities = user.getRoles().stream().map(role ->
                new SimpleGrantedAuthority(role.getWeChatOpenId().name())
        ).collect(Collectors.toList());*/

        return new UserPrincipal(
                user.getId(),
                user.getWeChatOpenId()/*,
                authorities*/
        );
    }
    
    public Long getId() {
        return id;
    }

    public String getName() {
        return weChatOpenId;
    }

    public String getEmail() {
        return email;
    }
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

}
