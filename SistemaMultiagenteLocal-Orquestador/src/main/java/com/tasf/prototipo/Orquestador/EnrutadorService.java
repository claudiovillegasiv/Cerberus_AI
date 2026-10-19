package com.tasf.prototipo.Orquestador;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;

@Service
public class EnrutadorService {

    private final RestTemplate restTemplate = new RestTemplate();

    public ResponseEntity<?> enrutarPeticion(String prompt, ClasificacionDTO clasificacion) {
        String departamento = clasificacion.getDepartamento();
        
        switch (departamento) {
                
            case "Desarrollo de software":
                String urlMicroservicioDesarrollo = "http://localhost:8081/api/desarrollo/resolver";
                try {
                    System.out.println("Redirigiendo petición vía HTTP al Microservicio de Desarrollo...");
                    ResponseEntity<String> respuesta = restTemplate.postForEntity(urlMicroservicioDesarrollo, prompt, String.class);
                    String cuerpoRespuesta = respuesta.getBody();
                    
                    try {
                        // Intentar parsear como JSON
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode nodo = mapper.readTree(cuerpoRespuesta);
                        
                        if (nodo.has("nombreArchivo") && nodo.has("contenido")) {
                            String nombreArchivo = nodo.get("nombreArchivo").asText();
                            String contenido = nodo.get("contenido").asText();
                            String explicacion = nodo.has("explicacion") ? nodo.get("explicacion").asText() : "";
                            
                            byte[] archivoBytes = contenido.getBytes(StandardCharsets.UTF_8);
                            
                            HttpHeaders headers = new HttpHeaders();
                            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                            headers.setContentDispositionFormData("attachment", nombreArchivo);
                            headers.add("X-Explicacion-Agente", explicacion.replaceAll("[\\r\\n]", " "));
                            
                            System.out.println("[ORQUESTADOR] Generando archivo virtual para descarga: " + nombreArchivo);
                            return new ResponseEntity<>(archivoBytes, headers, HttpStatus.OK);
                        }
                    } catch (Exception parseEx) {
                        return ResponseEntity.ok(cuerpoRespuesta);
                    }
                    
                    return ResponseEntity.ok(cuerpoRespuesta);
                    
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                            .body("[Error] El microservicio de Desarrollo está apagado o inaccesible: " + e.getMessage());
                }
                
            case "QA":
                String urlMicroservicioQA = "http://localhost:8083/api/qa/auditar";
                try {
                    System.out.println("Redirigiendo petición vía HTTP al Microservicio de QA...");
                    ResponseEntity<String> respuesta = restTemplate.postForEntity(urlMicroservicioQA, prompt, String.class);
                    

                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8")
                            .body(respuesta.getBody());
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("[Error] QA inaccesible: " + e.getMessage());
                }

            case "Recursos humanos":
                String urlMicroservicioRRHH = "http://localhost:8082/api/rrhh/consultar";
                try {
                    System.out.println("Redirigiendo petición vía HTTP al Microservicio de Recursos Humanos...");
                    ResponseEntity<String> respuesta = restTemplate.postForEntity(urlMicroservicioRRHH, prompt, String.class);
                    
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8")
                            .body(respuesta.getBody());
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("[Error] RRHH inaccesible: " + e.getMessage());
                }
            case "No aplicable":
                return ResponseEntity.ok()
                		.header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8")
                        .body("[Ruta: Bloqueada] -> Petición rechazada. Motivo: " + clasificacion.getMotivo());
                
            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("[Error de Enrutamiento] -> El orquestador devolvió una categoría no mapeada: " + departamento);
        }
    }
}