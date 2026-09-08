package com.example.serbisyofullstack.dto.nested;

import com.example.serbisyofullstack.model.enums.Status;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserSummaryDto {

    private Long id;

    private String email;

    private String phone;

    private Status status;
}
