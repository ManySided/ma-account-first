package ru.make.account.core.arving.repository.goods;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.make.account.core.arving.model.goods.Unit;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Long> {
}
