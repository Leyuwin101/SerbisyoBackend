package com.example.serbisyofullstack.mapper;

import com.example.serbisyofullstack.dto.nested.QuoteDto;
import com.example.serbisyofullstack.model.entity.Quote;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * Maps {@link Quote} to {@link QuoteDto}. Quotes are created by the provider
 * acceptance flow, so only entity-to-DTO mapping is needed.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface QuoteMapper {

    @Mapping(source = "quoteId", target = "id")
    @Mapping(source = "booking.bookingId", target = "bookingId")
    QuoteDto toDto(Quote entity);
}
