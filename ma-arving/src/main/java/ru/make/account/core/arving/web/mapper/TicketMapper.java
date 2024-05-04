package ru.make.account.core.arving.web.mapper;

import org.mapstruct.Mapper;
import ru.make.account.core.arving.model.Ticket;
import ru.make.account.core.arving.web.dto.operation.TicketDto;

@Mapper
public interface TicketMapper {
    Ticket toEntity(TicketDto dto);

    TicketDto toDto(Ticket entity);
}
