package ma.enset.securecapita.configuration;

import lombok.RequiredArgsConstructor;
import ma.enset.securecapita.handler.CustomAccessDeniedHandler;
import ma.enset.securecapita.handler.CustomAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;



@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final BCryptPasswordEncoder encoder;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final UserDetailsService userDetailsService;

    private static final String[] PUBLIC_URLS = {
            "/user/login/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable().cors().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeHttpRequests().requestMatchers(PUBLIC_URLS).permitAll();
        http.authorizeHttpRequests()
                .requestMatchers("/public/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .;
        //http.authorizeHttpRequests().requestMatchers(HttpMethod.DELETE,"/user/delete/**").hasAuthority("DELETE:USER");
        //http.authorizeHttpRequests().requestMatchers(HttpMethod.DELETE,"/customer/delete/**").hasAuthority("DELETE:CUSTOMER");
        //http.authorizeHttpRequests().requestMatchers(HttpMethod.GET, PUBLIC_URLS).hasAuthority("READ:USER");
        //http.authorizeHttpRequests().requestMatchers(HttpMethod.GET, PUBLIC_URLS).hasAuthority("READ:CUSTOMER");
        http.exceptionHandling().accessDeniedHandler(customAccessDeniedHandler);
               // .authenticationEntryPoint(customAuthenticationEntryPoint);
        //http.authorizeHttpRequests().anyRequest().authenticated();
        return http.build();
    }
    /*public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception->
                        exception.accessDeniedHandler(customAccessDeniedHandler)
                        .authenticationEntryPoint(customAuthenticationEntryPoint))
                .authorizeHttpRequests(request ->
                        request.requestMatchers(PUBLIC_URLS).permitAll()
                                .requestMatchers(HttpMethod.DELETE,"/user/delete/**").hasAuthority("DELETE:USER")
                                .requestMatchers(HttpMethod.DELETE,"/customer/delete/**").hasAuthority("DELETE:CUSTOMER")
                                .anyRequest().authenticated()
                );

        return http.build();
    }
*/
    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(encoder);
        return new ProviderManager((authProvider));
    }

}
