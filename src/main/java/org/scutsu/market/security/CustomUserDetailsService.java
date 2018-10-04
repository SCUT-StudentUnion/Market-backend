package org.scutsu.market.security;

import org.scutsu.market.repositories.UserRepository;
import org.scutsu.market.models.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService{
	
	UserRepository userRepository;

	@Transactional
	public UserDetails loadUserByUserId(Long UserId) {
		User user=userRepository.findById(UserId)
								.orElseThrow(()->
									new UsernameNotFoundException("User not found with id: "+UserId)
		);
	return UserPrincipal.create(user);
	}

	@Override
	public UserDetails loadUserByUsername(String arg0) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}
}
