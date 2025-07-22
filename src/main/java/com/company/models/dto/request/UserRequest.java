package com.company.models.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserRequest {
    @NotBlank(message = "firstname must be not blank")
    @Size(max = 25, message = "FirstName character count must be less than 26")
    private String firstName;

    @NotBlank(message = "lastname must be not blank")
    @Size(max = 40, message = "LastName character count must be less than 41")
    private String lastName;

    @Past(message = "birth date must be in the past ")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate birthDate;
}
