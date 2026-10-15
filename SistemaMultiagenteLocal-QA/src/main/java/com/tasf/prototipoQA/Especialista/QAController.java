package com.tasf.prototipoQA.Especialista;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@RestController
@RequestMapping("/api/qa")
public class QAController {

    @Autowired
    private AgenteQA agente;

    @PostMapping("/auditar")
    public String resolverPeticion(@RequestBody String prompt) {
        System.out.println("[MICROSERVICIO QA] Petición de auditoría recibida...");
        
        // ID temporal para mantener la arquitectura stateless
        String idTemporal = UUID.randomUUID().toString();
        
        return agente.auditar(idTemporal, prompt);
    }
    
    @PostMapping("/interno/perfeccionar")
    public String perfeccionarCodigoInterno(@RequestBody String codigoCrudo) {
        System.out.println("🤖 [QA] Recibiendo código en texto plano desde Desarrollo para perfeccionar...");
        
        // Mantenemos el estado stateless generando un ID único para la revisión
        String promptQA = "Por favor perfecciona este código:\n\n" + codigoCrudo;
        return agente.perfeccionarCodigo(java.util.UUID.randomUUID().toString(), promptQA);
    }
    
}