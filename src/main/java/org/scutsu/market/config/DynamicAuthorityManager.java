package org.scutsu.market.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;


@Component
class DirectAuthenticationManager implements AuthenticationManager {

	private static final List<GrantedAuthority> userAuthority=new ArrayList<GrantedAuthority>();//用户权限
	private static final List<GrantedAuthority> adminAuthority=new ArrayList<GrantedAuthority>();//管理员权限

	static{
		userAuthority.add(new SimpleGrantedAuthority("ROLE_USER"));
		adminAuthority.add(new SimpleGrantedAuthority("ROLE_USER"));
		adminAuthority.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
	}

	@Override
	public Authentication authenticate(Authentication au)throws AuthenticationException {
		//给au添加用户权限
		return new UsernamePasswordAuthenticationToken(au.getName(),au.getCredentials(),userAuthority);
	}
}


@Component
public class DynamicAuthorityManager {

	@Autowired
	AuthenticationManager authenticationManager;

	//以name-password的标识符对当前pricipal授权，但是name-password怎么定，还没想好
	private void authenticateUser(String name,String password){
		Authentication request=new UsernamePasswordAuthenticationToken(name,password);
		Authentication result=authenticationManager.authenticate(request);
		SecurityContextHolder.getContext().setAuthentication(result);
	}
}
