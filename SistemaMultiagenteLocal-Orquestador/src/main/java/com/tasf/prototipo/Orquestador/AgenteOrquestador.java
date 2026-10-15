package com.tasf.prototipo.Orquestador;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface AgenteOrquestador {
    
    @UserMessage("{{it}}")
    ClasificacionDTO clasificar(String peticion);
}