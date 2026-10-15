package com.tasf.prototipoRRHH.Especialista;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.SystemMessage;

@AiService
public interface AgenteRH {
	
	String evaluar(@MemoryId String idSesion, @UserMessage String peticion);
}