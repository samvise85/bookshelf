package it.samvise85.bookshelf.persist.database.config;

import it.samvise85.bookshelf.exception.BookshelfException;
import it.samvise85.bookshelf.persist.repository.BookshelfRepositoryFactoryBean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.h2.jdbcx.JdbcDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
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
//		repositoryImplementationPostfix="CustomImpl", 
		repositoryFactoryBeanClass=BookshelfRepositoryFactoryBean.class,
		entityManagerFactoryRef="bookshelfEntityManagerFactory", 
		transactionManagerRef = "bookshelfTransactionManager")
@EnableTransactionManagement
public class DatabaseJPAConfig {
	private static final Logger log = Logger.getLogger(DatabaseJPAConfig.class);
	
	private Database driver = Database.MYSQL;
	
    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setShowSql(true);
        jpaVendorAdapter.setGenerateDdl(true);
        jpaVendorAdapter.setDatabase(driver);
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
			dataDir = System.getProperty("user.home") + File.separator;
		log.debug("Data dir is: " + dataDir);
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(dataDir + "db.properties"));
			DriverManagerDataSource ds = new DriverManagerDataSource();
			ds.setDriverClassName(props.getProperty("db.driver"));
			ds.setUrl(props.getProperty("db.url"));
			ds.setUsername(props.getProperty("db.user"));
			ds.setPassword(props.getProperty("db.password"));
			return ds;
		} catch(FileNotFoundException e) {
			return getH2DataSource(dataDir);
		} catch(IOException e) {
			throw new BookshelfException(e.getMessage(), e);
		}
	}
	
	private DataSource getH2DataSource(String dataDir) {
		try {
			JdbcDataSource ds = new JdbcDataSource();
			ds.setURL("jdbc:h2:file:" + dataDir + "Bookshelf;MODE=MYSQL");
			ds.setUser("sa");
			ds.setPassword("sa");
			driver = Database.H2;
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
