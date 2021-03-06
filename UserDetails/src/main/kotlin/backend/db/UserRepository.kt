package backend.db

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager
import backend.db.User

@Repository
@Component("UserRepository")
interface UserRepository : CrudRepository<User, Long>, UserRepositoryCustom {

    fun findAllByUsername(username: String): Iterable<User>

    fun findAllByMail(mail: String): Iterable<User>

    fun findAllByAddress(address: String): Iterable<User>

}

@Transactional
interface UserRepositoryCustom {

    fun createUser(username: String, mail: String, address: String): Long

    fun updateUsername(userId: Long, username: String): Boolean

    fun update(userId: Long,
               username: String,
               mail: String,
               address: String): Boolean
}

@Repository
@Transactional
@Qualifier("UserRepository")
     open class UserRepositoryImpl : UserRepositoryCustom {

    @Autowired
    private lateinit var em: EntityManager

    override fun createUser(username: String, mail: String, address: String): Long {
        val entity = User(username, mail, address)
        em.persist(entity)
        return entity.id!!
    }

    override fun updateUsername(userId: Long, username: String): Boolean {
        val user = em.find(User::class.java, userId) ?: return false
        user.username = username
        return true
    }

    override fun update(userId: Long,
                        username: String,
                        mail: String,
                        address: String): Boolean {
        val user = em.find(User::class.java, userId) ?: return false
        user.username = username
        user.mail = mail
        user.address = address
        return true
    }
}