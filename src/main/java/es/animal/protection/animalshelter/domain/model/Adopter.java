package es.animal.protection.animalshelter.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Adopter {
    @NotBlank
    private String name;
    @NotBlank
    private String nif;
    private String address;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String birthDay;
}
