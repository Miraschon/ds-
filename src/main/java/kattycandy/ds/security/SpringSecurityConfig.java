package kattycandy.ds.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.csrf().disable()
		    .authorizeRequests()
		    .antMatchers("/", "/home", "/about").permitAll()
		    .antMatchers("/user/**").hasAnyRole("USER")
		    .anyRequest().authenticated()
		    .and()
		    .formLogin()
		    .loginPage("/login")
		    .permitAll()
		    .and()
		    .logout()
		    .permitAll();
	}
}
