package com.jazasoft.tna;

import org.hibernate.MultiTenancyStrategy;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mdzahidraza on 26/06/17.
 */
@Configuration
@EnableConfigurationProperties(JpaProperties.class)
@EnableJpaRepositories(
    entityManagerFactoryRef = "tenantEntityManager",
    transactionManagerRef = "tenantTransactionManager",
    basePackages = {"com.jazasoft.tna.repository"}
)
@EnableTransactionManagement
public class TenantPersistenceContext {
  private final Logger LOGGER = LoggerFactory.getLogger(TenantPersistenceContext.class);


  @Value("${spring.jpa.properties.hibernate.dialect}")
  private String dialect;

  @Bean
  public JpaVendorAdapter jpaVendorAdapter() {
    return new HibernateJpaVendorAdapter();
  }

  //@Primary
  @Bean(name = "tenantEntityManager")
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource,
                                                                     MultiTenantConnectionProvider connectionProvider,
                                                                     CurrentTenantIdentifierResolver tenantResolver) {
    LOGGER.debug("-$$$- tenantEntityManager()");

    LocalContainerEntityManagerFactoryBean emfBean = new LocalContainerEntityManagerFactoryBean();
    emfBean.setPersistenceUnitName("tenant");
    emfBean.setDataSource(dataSource);
    emfBean.setPackagesToScan("com.jazasoft.tna.entity");
    emfBean.setJpaVendorAdapter(jpaVendorAdapter());

    Map<String, Object> properties = new HashMap<>();
    properties.put(org.hibernate.cfg.Environment.MULTI_TENANT, MultiTenancyStrategy.DATABASE);
    properties.put(org.hibernate.cfg.Environment.MULTI_TENANT_CONNECTION_PROVIDER, connectionProvider);
    properties.put(org.hibernate.cfg.Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, tenantResolver);

//    properties.put("hibernate.hbm2ddl.auto","update");

    properties.put("hibernate.implicit_naming_strategy", "org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy");
    properties.put("hibernate.physical_naming_strategy", "org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy");
    properties.put("hibernate.id.new_generator_mappings", "false");
    properties.put("hibernate.dialect", dialect);
    properties.put("hibernate.jdbc.lob.non_contextual_creation", true);
    properties.put("hibernate.show_sql", true);
    properties.put("hibernate.format_sql", true);
    emfBean.setJpaPropertyMap(properties);

    return emfBean;
  }

  //@Primary
  @Bean(name = "tenantTransactionManager")
  public JpaTransactionManager transactionManager(@Qualifier("tenantEntityManager") EntityManagerFactory tenantEntityManager) {
    LOGGER.debug("-$$$- tenantTransactionManager()");
    JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(tenantEntityManager);
    return transactionManager;
  }

}