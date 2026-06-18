package io.github.etorg.users;

import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import jakarta.annotation.PostConstruct;

@Configuration
public class UserDatabaseConfig {
	
	private DataSource dataSource; 
	
	public UserDatabaseConfig(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	@PostConstruct
	public void migrateUserModule() {
		Flyway flyway = Flyway.configure()
				.dataSource(dataSource)
				.schemas("users")
				.createSchemas(true)
				.locations("classpath:db/migration/users")
				.defaultSchema("users")
				.load();
		
		flyway.migrate();
	}
	
}
