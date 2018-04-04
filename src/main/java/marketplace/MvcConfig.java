package marketplace;

import javax.sql.DataSource;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    public DataSource dataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.url(System.getenv("DB_URL"));
        dataSourceBuilder.username(System.getenv("USERNAME"));
        dataSourceBuilder.password(System.getenv("PASSWORD"));
        DataSource dataSource = dataSourceBuilder.build();
        return dataSource;
    }

    @Bean(name = "passwordEncoder")
    public BCryptPasswordEncoder passwordEncoder() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder;
    }
}
