package app.dao;

import app.domain.UserAccount;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserAccountRepository extends CrudRepository<UserAccount, Long> {
    Optional<UserAccount> findByUserId(Long username);

}

