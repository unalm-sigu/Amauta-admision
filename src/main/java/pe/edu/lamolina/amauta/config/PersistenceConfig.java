package pe.edu.lamolina.amauta.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.beans.PropertyVetoException;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
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
        config.setIdleTimeout(this.maxIddleTime);
        

        return new HikariDataSource(config);
    }
    
//    @Primary
//    @Bean(name = "dataSource")
//    public ComboPooledDataSource dataSource() throws PropertyVetoException {
//
//        ComboPooledDataSource ds = new ComboPooledDataSource();
//        ds.setDriverClass(this.driver);
//        ds.setJdbcUrl(this.jdbcUrl);
//        ds.setUser(this.username);
//        ds.setPassword(this.password);
//        ds.setAcquireIncrement(this.acquireIncrement);
//        ds.setMinPoolSize(this.minPool);
//        ds.setMaxPoolSize(this.maxPool);
//        ds.setMaxIdleTime(this.maxIddleTime);
//
//        return ds;
//    }

//    @Bean
//    public LocalSessionFactoryBean sessionFactory() throws PropertyVetoException {
//        LocalSessionFactoryBean factoryBean = new LocalSessionFactoryBean();
//        factoryBean.setDataSource(dataSource());
//        factoryBean.setPackagesToScan(this.model);
//        
//
//        return factoryBean;
//    }
    
    @Primary
    @Autowired
    @Bean(name = "sessionFactory")
    public LocalSessionFactoryBean factoryBean() {

        LocalSessionFactoryBean fb = new LocalSessionFactoryBean();
        fb.setDataSource(dataSource());
        fb.setPackagesToScan(this.model);

        Properties prop = new Properties();
        prop.setProperty("hibernate.dialect", this.dialect);
        prop.setProperty("hibernate.show_sql", this.showSql);
        prop.setProperty("hibernate.connection.release_mode", "after_transaction");
        prop.setProperty("hibernate.connection.useUnicode", "true");
        prop.setProperty("hibernate.connection.charSet", "UTF8");

        fb.setHibernateProperties(prop);

        return fb;
    }

}
