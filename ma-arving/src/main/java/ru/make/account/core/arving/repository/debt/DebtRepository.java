package ru.make.account.core.arving.repository.debt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.make.account.core.arving.model.debt.Debt;

import java.util.List;
import java.util.Optional;

@Repository
public interface DebtRepository extends JpaRepository<Debt, Long> {
    Optional<Debt> findById(Long id);

    List<Debt> getDebtByIsActiveIsTrueAndAccountIdOrderByCreateDateDesc(Long accountId);
}
