package ru.make.account.core.arving.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.make.account.core.arving.model.Category;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findById(Long id);

    List<Category> findByAccountIdAndParentIsNullOrderByName(Long accountId);

    List<Category> findByAccountIdOrderByName(Long accountId);

    List<Category> findByParentOrderByName(Long parent);

    List<Category> findByAccountIdAndNameAndStuffFlagIsFalse(Long accountId, String name);
}
