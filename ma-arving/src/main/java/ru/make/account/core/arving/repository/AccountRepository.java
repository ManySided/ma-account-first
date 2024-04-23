package ru.make.account.core.arving.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.make.account.core.arving.model.Account;

import java.util.List;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findAllByActualTrueAndAccountCreatorOrderByCreatedDesc(UUID accountCreator);

    List<Account> findAllByAccountCreatorOrderByCreatedDesc(UUID accountCreator);
}
