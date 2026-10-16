package com.tasf.prototipo.Orquestador;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudChatDTO {
    @NotBlank(message = "El prompt no puede estar vacío")
    private String prompt;
}