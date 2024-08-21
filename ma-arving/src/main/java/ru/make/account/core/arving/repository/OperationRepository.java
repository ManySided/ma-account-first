package ru.make.account.core.arving.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.make.account.core.arving.model.Operation;

import java.util.List;

@Repository
public interface OperationRepository extends JpaRepository<Operation, Long> {
    List<Operation> findByTicketIdAndIsActiveTrueOrderByIdAsc(Long ticketId);

    @Query("select o from Operation o where o.isActive=true and o.ticketId=:ticketId and upper(o.name) like :likeName")
    List<Operation> findLikeText(Long ticketId, String likeName);

    @Query("select o.name from Operation o, Ticket t where o.ticketId=t.id " +
            "and o.isActive=true " +
            "and upper(o.name) like :likeName " +
            "and t.accountId=:accountId " +
            "group by o.name ")
    List<String> findOperationGroups(Long accountId, String likeName);

    @Query("select o from Operation o, Ticket t where o.ticketId=t.id " +
            "and o.isActive=true " +
            "and o.name=:name " +
            "and t.accountId=:accountId " +
            "order by t.date desc")
    List<Operation> findOperationsByName(Long accountId, String name);

    @Query("select o.id from Operation o where o.isActive = true and o.categoryId=:categoryId")
    List<Long> findActivityOperationIdsByCategoryId(Long categoryId);

    @Modifying
    @Query("update Operation o set o.categoryId=:categoryId  where o.id=:operationId")
    void updateCategoryByOperationId(Long operationId, Long categoryId);
}
