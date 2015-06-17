package it.samvise85.bookshelf.persist.inmemory.config;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.h2.jdbcx.JdbcDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.hibernate4.HibernateExceptionTranslator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(
		basePackages={"it.samvise85.bookshelf"}, 
		entityManagerFactoryRef="bookshelfEntityManagerFactory", 
		transactionManagerRef = "bookshelfTransactionManager")
@EnableTransactionManagement
public class InMemoryJPAConfig {
	private static final Logger log = Logger.getLogger(InMemoryJPAConfig.class);
	
    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setShowSql(true);
        jpaVendorAdapter.setGenerateDdl(true);
        jpaVendorAdapter.setDatabase(Database.H2);
        return jpaVendorAdapter;
    }

    @Bean
    public HibernateExceptionTranslator hibernateExceptionTranslator() {
        return new HibernateExceptionTranslator();
    }
    
	@Bean(name = "bookshelfDataSource")
	public DataSource dataSource() {
		
		String dataDir = System.getenv("OPENSHIFT_DATA_DIR");
		if(dataDir == null)
			dataDir = "~/";
		log.debug("Data dir is: " + dataDir);
		try {
			JdbcDataSource ds = new JdbcDataSource();
			ds.setURL("jdbc:h2:file:" + dataDir + "Bookshelf;MODE=MYSQL");
			ds.setUser("sa");
			ds.setPassword("sa");
			return ds;
		} catch(Exception e) {
			log.error(e.getMessage(), e);
		}
		log.warn("Creating an embedded database. Its state will not be saved.");
		return new EmbeddedDatabaseBuilder().setName("bookshelfdb").setType(EmbeddedDatabaseType.H2).build();
		
	}

    @Bean(name = "bookshelfEntityManager")
    public EntityManager entityManager() {
        return entityManagerFactory().createEntityManager();
    }

	@Bean(name = "bookshelfEntityManagerFactory")
	public EntityManagerFactory entityManagerFactory() {
	    LocalContainerEntityManagerFactoryBean lef = new LocalContainerEntityManagerFactoryBean();
	    lef.setDataSource(dataSource());
	    lef.setJpaVendorAdapter(jpaVendorAdapter());
	    lef.setPackagesToScan("it.samvise85.bookshelf.model");
	    lef.setPersistenceUnitName("bookshelfPersistenceUnit");
	    lef.afterPropertiesSet();
	    return lef.getObject();
	}

    @Bean(name = "bookshelfTransactionManager")
    public PlatformTransactionManager transactionManager() {
        return new JpaTransactionManager(entityManagerFactory());
    }
}
