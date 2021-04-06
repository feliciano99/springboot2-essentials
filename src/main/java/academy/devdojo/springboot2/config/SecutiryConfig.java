package academy.devdojo.springboot2.config;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import academy.devdojo.springboot2.service.DevDojoUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@EnableWebSecurity
@Log4j2
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecutiryConfig extends WebSecurityConfigurerAdapter {
	private final DevDojoUserDetailsService devDojoUserDetailsService;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable()
		//csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).and()
		.authorizeRequests()
        .antMatchers("/animes/admin/**").hasRole("ADMIN")
        .antMatchers("/animes/**").hasRole("USER")
        .antMatchers("/actuator/**").permitAll()
	    .anyRequest()
	    .authenticated()
	    .and()
	    .formLogin()
	    .and()
	    .httpBasic();

	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		log.info("Password Encoded {}", passwordEncoder.encode("academy"));
//		auth.inMemoryAuthentication()
//				.withUser("feliciano").password(passwordEncoder.encode("academy")).roles("USER", "ADMIN")
//				.and().withUser("devdojo").password(passwordEncoder.encode("academy")).roles("USER");
		auth.userDetailsService(devDojoUserDetailsService)
		.passwordEncoder(passwordEncoder);
	}

}
