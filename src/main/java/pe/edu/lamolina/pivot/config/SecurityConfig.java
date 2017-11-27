package pe.edu.lamolina.pivot.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import pe.edu.lamolina.pivot.security.http.LoginSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UserDetailsService details;

    @Autowired
    LoginSuccessHandler loginHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .antMatchers("/academico/**").fullyAuthenticated()
                .antMatchers("/ciclo/**").fullyAuthenticated()
                .antMatchers("/comun/**").fullyAuthenticated()
                .antMatchers("/general/**").fullyAuthenticated()
                .antMatchers("/seguridad/**").fullyAuthenticated()
                .antMatchers("/test/**").fullyAuthenticated()
                .antMatchers("/**").permitAll();

        http.formLogin()
                .loginPage("/login")
                .failureUrl("/login?error")
                .successHandler(loginHandler)
                .permitAll();

        http.logout().logoutSuccessUrl("/");

        http.sessionManagement().invalidSessionUrl("/");

        http.exceptionHandling().accessDeniedPage("/");

        http.csrf().disable();

    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(details);

        auth.authenticationProvider(provider);
    }

}
