package io.github.etorg.lot.internal;

import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import jakarta.annotation.PostConstruct;

@Configuration
public class LotDatabaseConfig {
	
	private DataSource dataSource; 
	
	public LotDatabaseConfig(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	@PostConstruct
	public void migrateUserModule() {
		Flyway flyway = Flyway.configure()
				.dataSource(dataSource)
				.schemas("lots")
				.createSchemas(true)
				.locations("classpath:db/migration/lot")
				.defaultSchema("lots")
				.load();
		
		flyway.migrate();
	}
	
}
