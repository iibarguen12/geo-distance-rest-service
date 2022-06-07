package com.wcc.geographicdistance.security;

import com.wcc.geographicdistance.domain.Roles;
import com.wcc.geographicdistance.filter.CustomAuthenticationFilter;
import com.wcc.geographicdistance.filter.CustomAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManagerBean());
        customAuthenticationFilter.setFilterProcessesUrl("/v1/geo-distance/login");
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(STATELESS);
        http.authorizeHttpRequests().antMatchers("/v1/geo-distance/login").permitAll();
        http.authorizeHttpRequests().antMatchers(HttpMethod.GET,"/v1/geo-distance/management/users").hasAnyAuthority(Roles.ROLE_USER.name(),Roles.ROLE_ADMIN.name());
        http.authorizeHttpRequests().antMatchers(HttpMethod.POST,"/v1/geo-distance/management/user/save").hasAuthority(Roles.ROLE_ADMIN.name());
        http.authorizeHttpRequests().antMatchers(HttpMethod.POST,"/v1/geo-distance/management/role/save").hasAuthority(Roles.ROLE_ADMIN.name());
        http.authorizeHttpRequests().antMatchers(HttpMethod.POST,"/v1/geo-distance/management/role/add-to-user").hasAuthority(Roles.ROLE_ADMIN.name());
        http.authorizeHttpRequests().antMatchers(HttpMethod.GET,"/v1/geo-distance/postcodes").hasAnyAuthority(Roles.ROLE_USER.name(),Roles.ROLE_ADMIN.name());
        http.authorizeHttpRequests().antMatchers(HttpMethod.GET,"/v1/geo-distance/postcodes/distance").hasAnyAuthority(Roles.ROLE_USER.name(),Roles.ROLE_ADMIN.name());
        http.authorizeHttpRequests().antMatchers(HttpMethod.POST,"/v1/geo-distance/postcodes/save").hasAnyAuthority(Roles.ROLE_ADMIN.name());
        http.authorizeHttpRequests().antMatchers(HttpMethod.POST,"/v1/geo-distance/postcode/save").hasAnyAuthority(Roles.ROLE_ADMIN.name());
        http.authorizeHttpRequests().anyRequest().authenticated();
        http.addFilter(customAuthenticationFilter);
        http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
