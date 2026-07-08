package io.etorg.microservice.notifications

import jakarta.annotation.PostConstruct
import org.flywaydb.core.Flyway
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource


@Configuration
class DatabaseConfiguration (val dataSource: DataSource) {

    @PostConstruct
    fun migrateNotifications() {
        var flyway:Flyway = Flyway.configure()
            .dataSource(dataSource)
            .schemas("notifications")
            .createSchemas(true)
            .locations("classpath:db/migration")
            .defaultSchema("notifications")
            .load()

        flyway.migrate()
    }
}