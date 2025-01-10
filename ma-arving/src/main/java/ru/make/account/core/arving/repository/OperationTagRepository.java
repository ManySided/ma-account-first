package ru.make.account.core.arving.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.make.account.core.arving.model.OperationTag;

import java.util.List;

@Repository
public interface OperationTagRepository extends JpaRepository<OperationTag, Long> {
    List<OperationTag> findAllByName(String name);
    List<OperationTag> findAllByNameAndAccountId(String name, Long accountId);
    List<OperationTag> findAllByAccountId(Long accountId);
}
