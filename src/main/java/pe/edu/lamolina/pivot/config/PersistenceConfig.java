package pe.edu.lamolina.pivot.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.beans.PropertyVetoException;
import java.util.Properties;
import javax.servlet.DispatcherType;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.orm.hibernate4.support.OpenSessionInViewFilter;
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

    @Bean
    public ComboPooledDataSource dataSource() throws PropertyVetoException {

        ComboPooledDataSource ds = new ComboPooledDataSource();
        ds.setDriverClass(this.driver);
        ds.setJdbcUrl(this.jdbcUrl);
        ds.setUser(this.username);
        ds.setPassword(this.password);
        ds.setAcquireIncrement(3);
        ds.setMinPoolSize(5);
        ds.setMaxPoolSize(20);
        ds.setMaxIdleTime(20);

        return ds;
    }

    @Autowired
    @Bean(name = "sessionFactory")
    public LocalSessionFactoryBean factoryBean(ComboPooledDataSource ds) {

        LocalSessionFactoryBean fb = new LocalSessionFactoryBean();
        fb.setDataSource(ds);
        fb.setPackagesToScan(this.model);

        Properties prop = new Properties();
        prop.setProperty("hibernate.dialect", this.dialect);
        prop.setProperty("hibernate.show_sql", this.showSql);
        prop.setProperty("hibernate.hbm2ddl.auto", "create");
        prop.setProperty("hibernate.connection.release_mode", "after_transaction");
        prop.setProperty("hibernate.connection.useUnicode", "true");
        prop.setProperty("hibernate.connection.charSet", "UTF8");

        fb.setHibernateProperties(prop);

        return fb;
    }

    @Bean
    @Autowired
    public HibernateTransactionManager transactionManager(SessionFactory sf) {
        HibernateTransactionManager transManager = new HibernateTransactionManager();
        transManager.setSessionFactory(sf);
        return transManager;
    }

    @Bean
    public FilterRegistrationBean someFilterRegistration() {

        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(someFilter());
        registration.addUrlPatterns("/*");
        registration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.FORWARD);
        registration.setName("hibernateFilter");
        return registration;
    }

    @Bean(name = "hibernateFilter")
    public OpenSessionInViewFilter someFilter() {
        OpenSessionInViewFilter filter = new OpenSessionInViewFilter();
        filter.setSessionFactoryBeanName("sessionFactory");

        return filter;
    }

}
