package org.example.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.web.filter.HiddenHttpMethodFilter;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private DataSource dataSource;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/", "/registration").permitAll()
                // .antMatchers("/users","/users/**").permitAll()
                .antMatchers("/users/edit", "/users/edit/**").permitAll()
                .antMatchers("/muscleGroups", "/muscleGroups/**").permitAll()
                .antMatchers("/muscles", "/muscles/**").permitAll()
                .antMatchers("/exercises", "/exercises/**").permitAll()
                .antMatchers("/activate/*").permitAll()
                .antMatchers("/actuator/*").permitAll()
                .antMatchers("/health").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                //                    .defaultSuccessUrl("/")
                .permitAll()
                .and()
                .logout()
                .permitAll();
        //        http
        //                .authorizeRequests()
        //                .anyRequest().permitAll();
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers(

                // статика
                // "/css/**",
                // "/js/**",
                // "/fonts/**",
                // "images/all/**",
                "images/**",
                "/images/all/**",
                "/images/all/muscleGroups/",
                "/images/all/muscleGroups/**",
                "/img/all/**",
                "/img/**"

        );
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .passwordEncoder(NoOpPasswordEncoder.getInstance())
                // language=SQL
                .usersByUsernameQuery("SELECT username, password, active FROM usr WHERE username=?")
                // language=SQL
                .authoritiesByUsernameQuery("SELECT u.username, ur.roles " +
                        "FROM usr u INNER JOIN user_role ur ON u.id=ur.user_id " +
                        "WHERE u.username=?");
    }

    @Bean
    HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter();
    }
}
