package ru.make.account.core.arving.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.make.account.core.arving.model.Currency;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> {
}
