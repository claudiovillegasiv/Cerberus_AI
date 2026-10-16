package com.tasf.prototipo.Orquestador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/agente")
@CrossOrigin(origins = "*") // Crucial para permitir peticiones desde la UI
public class OrquestadorController {

    @Autowired
    private AgenteOrquestador agenteOrquestador;

    @Autowired
    private EnrutadorService enrutador;

    //Recibe un JSON con el SolicitudChatDTO, le extrae el prompt, le pide al agenteOrquestador que clasifique la intención del usuario, utiliza el enrutador para enviar el prompt al agente especialista adecuado, y finalmente empaqueta todo en un RespuestaChatDTO
    @PostMapping(value = "/procesar", consumes = MediaType.APPLICATION_JSON_VALUE)
    public RespuestaChatDTO procesarPeticionJson(@Valid@RequestBody SolicitudChatDTO solicitud) {
        String prompt = solicitud.getPrompt();
        
        ClasificacionDTO clasificacion = agenteOrquestador.clasificar(prompt);
        String respuestaTexto = enrutador.enrutarPeticion(prompt, clasificacion);
        
        return new RespuestaChatDTO(respuestaTexto, clasificacion);
    }

    // 2. Endpoint para cuando se envían archivos mediante Multipart (Postman o UI con archivos)
    @PostMapping(value = "/procesar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RespuestaChatDTO procesarPeticionConArchivo(
            @RequestParam("prompt") String prompt,
            @RequestParam(value = "archivo", required = false) MultipartFile archivo) {
       
        try {
            String promptParaClasificar = prompt;
            String promptParaEspecialista = prompt;

            if (archivo != null && !archivo.isEmpty()) {
                promptParaClasificar += "\n[Nota del sistema: El usuario ha adjuntado un archivo fuente llamado '" + archivo.getOriginalFilename() + "'].";
                String contenidoArchivo = new String(archivo.getBytes(), StandardCharsets.UTF_8);
                promptParaEspecialista += "\n\n--- Contenido del archivo (" + archivo.getOriginalFilename() + ") ---\n" + contenidoArchivo;
            }

            ClasificacionDTO clasificacion = agenteOrquestador.clasificar(promptParaClasificar);
            String respuestaTexto = enrutador.enrutarPeticion(promptParaEspecialista, clasificacion);

            return new RespuestaChatDTO(respuestaTexto, clasificacion);
            
        } catch (Exception e) {
            ClasificacionDTO errorClasificacion = new ClasificacionDTO();
            errorClasificacion.setDepartamento("ERROR");
            return new RespuestaChatDTO("[Error en el Orquestador]: " + e.getMessage(), errorClasificacion);
        }
    }
}