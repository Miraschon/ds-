package kattycandy.ds.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.csrf()
		    .disable()
		    .authorizeRequests()
		    .antMatchers("/registration/**", "/login/**", "/css/**", "/img/**").permitAll()
		    .antMatchers("/index").hasAnyRole("USER")
		    .anyRequest()
		    .authenticated()
		    .and()
		    .formLogin()
		    .loginPage("/login")
		    .permitAll()
		    .and()
		    .logout()
		    .permitAll();
	}
}
