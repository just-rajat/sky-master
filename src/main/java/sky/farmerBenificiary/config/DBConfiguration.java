/**
 * 
 */
package sky.farmerBenificiary.config;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 
 */

import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DBConfiguration {

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.procprd")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "procprd")
    @Primary
    @ConfigurationProperties("spring.datasource.procprd.hikari")
    public DataSource ncolDataSource() {
        return dataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }


//    @Bean
//    @ConfigurationProperties("spring.datasource.centralprd")
//    public DataSourceProperties centralDataSourceProperties() {
//        return new DataSourceProperties();
//    }
//
//    @Bean(name = "centralprd")
//    @ConfigurationProperties("spring.datasource.centralprd.hikari")
//    public DataSource centralDataSource() {
//        return centralDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
//    }
}
