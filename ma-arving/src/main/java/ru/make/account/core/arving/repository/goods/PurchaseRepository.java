package ru.make.account.core.arving.repository.goods;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.make.account.core.arving.model.goods.Purchase;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
}
