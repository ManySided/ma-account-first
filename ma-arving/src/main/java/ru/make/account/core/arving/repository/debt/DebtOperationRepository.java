package ru.make.account.core.arving.repository.debt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.make.account.core.arving.model.debt.DebtOperation;

@Repository
public interface DebtOperationRepository extends JpaRepository<DebtOperation, Long> {
}
