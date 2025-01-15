package pe.edu.lamolina.amauta.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.beans.PropertyVetoException;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@ConfigurationProperties(prefix = "persistence")
public class PersistenceConfig {

    @Value("${driver}")
    String driver;

    @Value("${jdbcUrl}")
    String jdbcUrl;

    @Value("${userdb}")
    String username;

    @Value("${password}")
    String password;

    @Value("${model}")
    String model;

    @Value("${showSql}")
    String showSql;

    @Value("${dialect}")
    String dialect;

    @Value("${minPool}")
    int minPool;

    @Value("${maxPool}")
    int maxPool;

    @Value("${maxIddleTime}")
    int maxIddleTime;

    @Value("${acquireIncrement}")
    int acquireIncrement;

    @Primary
    @Bean(name = "dataSource")
    public HikariDataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(this.jdbcUrl);
        config.setUsername(this.username);
        config.setPassword(this.password);
        config.setDriverClassName(this.driver);
        config.setMaximumPoolSize(this.maxPool);
        config.setMinimumIdle(this.minPool);

        return new HikariDataSource(config);
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory() throws PropertyVetoException {
        LocalSessionFactoryBean factoryBean = new LocalSessionFactoryBean();
        factoryBean.setDataSource(dataSource());
        factoryBean.setPackagesToScan(this.model);

        return factoryBean;
    }

}
