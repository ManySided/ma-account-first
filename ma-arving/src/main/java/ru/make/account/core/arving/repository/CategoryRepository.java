package ru.make.account.core.arving.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.make.account.core.arving.model.Category;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByIdAndFlagActivityTrue(Long id);

    List<Category> findByAccountIdAndParentIsNullAndFlagActivityTrueOrderByName(Long accountId);

    List<Category> findByAccountIdAndFlagActivityTrueOrderByName(Long accountId);

    List<Category> findByParentAndFlagActivityTrueOrderByName(Long parent);

    List<Category> findByAccountIdAndNameAndStuffFlagFalseAndFlagActivityTrue(Long accountId, String name);
    List<Category> findByAccountIdAndNameAndFlagActivityTrue(Long accountId, String name);
}
