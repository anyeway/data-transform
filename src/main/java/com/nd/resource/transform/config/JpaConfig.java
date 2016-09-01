package com.nd.resource.transform.config;

import com.nd.gaea.core.config.ConfigConstant;
import com.nd.gaea.core.utils.PropertiesLoaderUtils;
import com.nd.gaea.repository.jpa.support.NullableJpaRepositoryFactoryBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.dialect.MySQL5InnoDBDialect;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * <p>JPA配置</p>
 * 引入数据源的配置，以及EntityManager工厂类和事务管理类的配置
 * <p/>
 * JPA使用的是Hibernate JPA实现，在系统中必须存在 /config/hibernate.properties，否则该启动错误
 *
 * @author bifeng.liu
 * @since 2015/9/30.
 */
@Configuration
@EnableJpaRepositories(basePackages = {"com.nd.resource"}, repositoryFactoryBeanClass = NullableJpaRepositoryFactoryBean.class)
@EnableTransactionManagement
@Import({DataSourceConfig.class})
public class JpaConfig {
    /**
     * Logger对象
     */
    private static final Log LOGGER = LogFactory.getLog(JpaConfig.class);
    /**
     * Hibernate配置文件的地址
     */
    private static final String HIBERNATE_PROPERTIES_PATH = "/config/hibernate.properties";

    /**
     * 防止多次初始化
     */
    private LocalContainerEntityManagerFactoryBean entityManagerFactory;

    @Resource(name = "dataSource")
    public DataSource dataSource;

    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean() throws IOException {
        if (entityManagerFactory == null) {
            entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
            entityManagerFactory.setDataSource(dataSource);
            entityManagerFactory.setPackagesToScan("com.nd.resource");
            entityManagerFactory.setPersistenceProviderClass(HibernatePersistenceProvider.class);
            entityManagerFactory.setPersistenceUnitName("tag");
            // Jpa Vendor
            HibernateJpaVendorAdapter jpaVendor = new HibernateJpaVendorAdapter();
            jpaVendor.setDatabase(Database.MYSQL);
            jpaVendor.setGenerateDdl(false);
            jpaVendor.setShowSql(true);
            jpaVendor.setDatabasePlatform(MySQL5InnoDBDialect.class.getName());
            entityManagerFactory.setJpaVendorAdapter(jpaVendor);
            entityManagerFactory.setJpaDialect(new HibernateJpaDialect());
            // 载入c3p0.properties配置
            try {
                Properties properties = PropertiesLoaderUtils.loadProperties(HIBERNATE_PROPERTIES_PATH);
                entityManagerFactory.setJpaPropertyMap((Map) properties);
            } catch (Exception ex) {
                LOGGER.warn("载入Hibernate配置文件(" + ConfigConstant.CONFIG_PROJECT_CUSTOM_PROPERTIES_FILE + ")时出错！", ex);
            }
        }
        return entityManagerFactory;
    }

    @Bean(name = "transactionManager")
    @Autowired
    public JpaTransactionManager jpaTransactionManager(LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean) throws IOException {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(localContainerEntityManagerFactoryBean.getObject());
        return jpaTransactionManager;
    }
}
