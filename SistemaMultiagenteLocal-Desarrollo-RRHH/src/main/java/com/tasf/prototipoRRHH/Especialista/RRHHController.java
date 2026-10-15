package com.tasf.prototipoRRHH.Especialista;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@RestController
@RequestMapping("/api/rrhh")
public class RRHHController {

    @Autowired
    private AgenteRH agente;

    @PostMapping("/consultar")
    public String resolverPeticion(@RequestBody String prompt) {
        System.out.println("[MICROSERVICIO RRHH] Petición recibida");
        
        String idTemporal = UUID.randomUUID().toString();
        
        return agente.evaluar(idTemporal, prompt);
    }
}