package com.example.serbisyofullstack.dto.nested;

import com.example.serbisyofullstack.model.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryDto {

    private Long id;

    private String email;

    private String phone;

    private Status status;
}
