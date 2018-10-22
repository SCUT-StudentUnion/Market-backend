package org.scutsu.market.configuration;
import org.scutsu.market.controllers.wechat.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private WeChatProperties admin;
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
					.anyRequest().authenticated()
					.antMatchers("/**").hasRole("ADMIN")
				.and()
			.formLogin().loginPage(admin.getLoginURL())
						.defaultSuccessUrl(admin.getLoginSuccess())
						.failureForwardUrl(admin.getLoginError())
				.and()
			.logout().logoutSuccessUrl(admin.getLogoutURL())
				.and()
			.csrf().disable();
	}

	@Autowired
	protected void Authorize(AuthenticationManagerBuilder Author)throws Exception{
		Author
			.inMemoryAuthentication()
				.passwordEncoder(new BCryptPasswordEncoder()).withUser(admin.getAppId())
				.password(new BCryptPasswordEncoder().encode(admin.getSecret())).roles("ADMIN");
	}
}
