package ru.make.account.core.arving.repository.debt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.make.account.core.arving.model.debt.DebtOperation;

import java.util.List;
import java.util.Optional;

@Repository
public interface DebtOperationRepository extends JpaRepository<DebtOperation, Long> {
    List<DebtOperation> findDebtOperationByDeleteFlagIsFalseAndDebtIdOrderByIdAsc(Long debtId);

    Optional<DebtOperation> findDebtOperationByDebtIdAndFirstOperationIsTrue(Long debtId);
}
