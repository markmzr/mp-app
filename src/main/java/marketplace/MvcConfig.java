package marketplace;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("search");
        registry.addViewController("/search").setViewName("search");
        registry.addViewController("/results").setViewName("results");
        registry.addViewController("/signin").setViewName("signin");
        registry.addViewController("/myaccount").setViewName("myaccount");
        registry.addViewController("/register").setViewName("register");
        registry.addViewController("/registersuccess").setViewName("registersuccess");
    }

    @Bean(name = "dataSource")
    public DriverManagerDataSource dataSource() {
        DriverManagerDataSource dmds = new DriverManagerDataSource();
        dmds.setUrl(System.getenv("DB_URL"));
        dmds.setUsername(System.getenv("USERNAME"));
        dmds.setPassword(System.getenv("PASSWORD"));
        return dmds;
    }

    @Bean(name = "passwordEncoder")
    public BCryptPasswordEncoder passwordEncoder() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder;
    }
}
