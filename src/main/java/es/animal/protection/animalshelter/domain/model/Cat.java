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
public class Cat {
    private String name;
    @NotNull
    private Integer chip;
    private Boolean sociable;
    @NotBlank
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String admissionDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String departureDate;
    private String adopterNif;
    private String colonyRegistry;

}
