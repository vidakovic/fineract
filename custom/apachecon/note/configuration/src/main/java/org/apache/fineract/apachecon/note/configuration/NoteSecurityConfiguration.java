package org.apache.fineract.apachecon.note.configuration;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class NoteSecurityConfiguration {

    @Bean
    @Order(2)
    public SecurityFilterChain notesSecurityFilterChain(HttpSecurity http) throws Exception {
        // see: https://www.youtube.com/watch?v=PczgM2L3w60
        return http.securityMatcher(antMatcher("/coc/*/*/notes")).authorizeHttpRequests((auth) -> {
            auth.requestMatchers("/coc/client/*/notes").hasAuthority("READ_CLIENT").requestMatchers("/coc/loan/*/notes")
                    .hasAuthority("READ_LOAN").anyRequest().hasAnyAuthority("ALL_FUNCTIONS", "ALL_FUNCTIONS_READ");
        }).build();
    }
}
