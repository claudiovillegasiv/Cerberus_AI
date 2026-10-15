package com.tasf.prototipoDesarrollo.Especialista;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface AgenteDesarrollo {
    
    String resolverProblema(@UserMessage String peticion);
}