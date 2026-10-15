package com.tasf.prototipoQA.Especialista;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface AgenteQA {
    
    String auditar(@MemoryId String idSesion, @UserMessage String peticion);
    
    @SystemMessage({
        "Eres el Ingeniero de QA. Tu compañero de Desarrollo te ha enviado un código.",
        "Tu tarea NO es solo criticar. Debes auditar el código, corregir las vulnerabilidades, optimizarlo y DEVOLVER EL CÓDIGO PERFECCIONADO.",
        "Explica brevemente los cambios que hiciste al inicio y luego proporciona el código limpio."
    })
    String perfeccionarCodigo(@MemoryId String idSesion, @UserMessage String peticion);
}