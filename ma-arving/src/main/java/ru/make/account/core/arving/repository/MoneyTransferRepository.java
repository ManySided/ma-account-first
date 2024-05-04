package ru.make.account.core.arving.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.make.account.core.arving.model.MoneyTransfer;

@Repository
public interface MoneyTransferRepository extends JpaRepository<MoneyTransfer, Long> {
}
