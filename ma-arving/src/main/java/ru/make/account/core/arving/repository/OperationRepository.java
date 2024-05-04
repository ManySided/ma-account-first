package ru.make.account.core.arving.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.make.account.core.arving.model.Operation;

import java.util.List;

@Repository
public interface OperationRepository extends JpaRepository<Operation, Long> {
    List<Operation> findByTicketIdAndIsActiveTrueOrderByIdAsc(Long ticketId);
}
