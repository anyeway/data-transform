package com.nd.resource.transform.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.nd.gaea.core.config.ConfigConstant;
import com.nd.gaea.core.utils.PropertiesLoaderUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.io.FileNotFoundException;
import java.util.Properties;

/**
 * <p>数据源配置</p>
 * 从${classpath}/config/database.properties中取得相应的参数
 * <p/>
 * C3P0未在database.properties中配置的参数可以通过/config/c3p0.properties来配置
 *
 * @author bifeng.liu
 * @since 2015/9/30.
 */
@Configuration
@PropertySource({"classpath:/config/database.properties"})
public class DataSourceConfig {
    /**
     * Logger对象
     */
    private static final Log LOGGER = LogFactory.getLog(DataSourceConfig.class);
    /**
     * C3P0扩展配置文件的地址
     */
    private static final String C3P0_EXTEND_PROPERTIES_PATH = "/config/c3p0.properties";

    @Value("${jdbc.driver}")
    String driverClass;
    @Value("${jdbc.url}")
    String jdbcUrl;
    @Value("${jdbc.username}")
    String userName;
    @Value("${jdbc.password}")
    String password;

    @Value("${c3p0.initialPoolSize}")
    int initialPoolSize;
    @Value("${c3p0.minPoolSize}")
    int minPoolSize;
    @Value("${c3p0.maxPoolSize}")
    int maxPoolSize;
    @Value("${c3p0.acquireIncrement}")
    int acquireIncrement;
    @Value("${c3p0.maxIdleTime}")
    int maxIdleTime;
    @Value("${c3p0.idleConnectionTestPeriod}")
    int idleConnectionTestPeriod;
    @Value("${c3p0.checkoutTimeout}")
    int checkoutTimeout;
    @Value("${c3p0.maxStatements}")
    int maxStatements;
    @Value("${c3p0.numHelperThreads}")
    int numHelperThreads;
    @Value("${c3p0.testConnectionOnCheckout}")
    boolean testConnectionOnCheckout;
    @Value("${c3p0.preferredTestQuery}")
    String preferredTestQuery;

    @Bean(name = "dataSource", destroyMethod = "close")
    public DataSource ComboPooledDataSource() throws PropertyVetoException {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass(driverClass);

        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setUser(userName);
        dataSource.setPassword(password);

        dataSource.setInitialPoolSize(initialPoolSize);
        dataSource.setMinPoolSize(minPoolSize);
        dataSource.setMaxPoolSize(maxPoolSize);
        dataSource.setAcquireIncrement(acquireIncrement);
        dataSource.setMaxIdleTime(maxIdleTime);
        dataSource.setIdleConnectionTestPeriod(idleConnectionTestPeriod);
        dataSource.setCheckoutTimeout(checkoutTimeout);
        dataSource.setMaxStatements(maxStatements);
        dataSource.setNumHelperThreads(numHelperThreads);
        dataSource.setTestConnectionOnCheckout(testConnectionOnCheckout);
        dataSource.setPreferredTestQuery(preferredTestQuery);
        // 载入c3p0.properties配置
        try {
            Properties properties = PropertiesLoaderUtils.loadProperties(C3P0_EXTEND_PROPERTIES_PATH);
            dataSource.setProperties(properties);
        } catch (FileNotFoundException ex) {
            LOGGER.debug("C3P0配置文件(" + ConfigConstant.CONFIG_PROJECT_CUSTOM_PROPERTIES_FILE + ")不存在！");
        } catch (Exception ex) {
            LOGGER.warn("载入C3P0配置文件(" + ConfigConstant.CONFIG_PROJECT_CUSTOM_PROPERTIES_FILE + ")时出错！", ex);
        }
        return dataSource;
    }
}
