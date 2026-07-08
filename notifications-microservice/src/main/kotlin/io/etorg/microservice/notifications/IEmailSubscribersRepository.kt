package io.etorg.microservice.notifications

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface IEmailSubscribersRepository : JpaRepository<EmailSubscribersEntity, UUID> {
    @Query("select e.email from EmailSubscribersEntity e where e.userId = :id")
    fun findEmailByUserId(@Param("id") id: UUID) : String?

    @Query("select e.email from EmailSubscribersEntity e where e.userId in :ids and e.isSubscribed = true")
    fun findEmailWhereUserIdIn(@Param("ids") ids: List<UUID?>) : List<String>

    @Query("select e.username from EmailSubscribersEntity e where e.userId = :id")
    fun findUsernameByUserId(@Param("id") id: UUID) : String?

    fun existsByUserId(id: UUID) : Boolean
    fun deleteByUserId(id: UUID)
}