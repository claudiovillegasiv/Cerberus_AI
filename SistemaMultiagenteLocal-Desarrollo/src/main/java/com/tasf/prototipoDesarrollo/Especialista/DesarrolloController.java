package com.tasf.prototipoDesarrollo.Especialista;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;


@RestController
@RequestMapping("/api/desarrollo")
public class DesarrolloController {

    @Autowired
    private AgenteDesarrollo agente;

    @Autowired
    private HerramientasDesarrollo herramientas;

    @PostMapping("/resolver")
    public String resolverPeticion(@RequestBody String prompt) {
        
        System.out.println("[MICROSERVICIO DEV] Petición inicial recibida...");
        String respuestaQwen = agente.resolverProblema(prompt);


        if (respuestaQwen.contains("\"name\"") && respuestaQwen.contains("\"arguments\"")) {
            System.out.println("[INTERCEPTOR] JSON de herramienta detectado. Parseando...");
            
            try {
                String jsonLimpio = limpiarJson(respuestaQwen);
                
                ObjectMapper mapper = new ObjectMapper();
                JsonNode nodo = mapper.readTree(jsonLimpio);
                
                String nombreHerramienta = nodo.get("name").asText();
                JsonNode argumentos = nodo.get("arguments");

                /*if ("escribirArchivo".equals(nombreHerramienta)) {
                    return herramientas.escribirArchivo(
                            argumentos.get("ruta").asText(), 
                            argumentos.get("contenido").asText()
                    );
                } 
                else*/ 
                if ("leerArchivoProyecto".equals(nombreHerramienta)) {
                	String contenidoArchivo = herramientas.leerArchivoProyecto(
                	        argumentos.get("nombreArchivo").asText(), 
                	        argumentos.get("nombreDir").asText()
                	);
                    
                    System.out.println("[INTERCEPTOR] Devolviendo contenido leído a Qwen para análisis final...");
                    
                    return agente.resolverProblema(
                            "Ejecuté la herramienta. Aquí está el contenido de " + argumentos.get("nombreArchivo").asText() + 
                            ":\n\n" + contenidoArchivo + "\n\nPor favor, responde a mi petición original basándote en este código."
                    );
                }
            } catch (Exception e) {
                System.out.println("⚠️ [INTERCEPTOR] Error al parsear la herramienta: " + e.getMessage());
                return respuestaQwen;
            }
        }

        return respuestaQwen;
    }

    private String limpiarJson(String texto) {
        if (texto.contains("```json")) {
            int inicio = texto.indexOf("```json") + 7;
            int fin = texto.lastIndexOf("```");
            return texto.substring(inicio, fin).trim();
        }
        if (texto.contains("```")) {
            int inicio = texto.indexOf("```") + 3;
            int fin = texto.lastIndexOf("```");
            return texto.substring(inicio, fin).trim();
        }
        return texto.trim();
    }
}