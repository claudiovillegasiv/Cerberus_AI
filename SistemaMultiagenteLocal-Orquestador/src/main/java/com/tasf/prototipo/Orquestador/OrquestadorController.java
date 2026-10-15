package com.tasf.prototipo.Orquestador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/agente")
public class OrquestadorController {

    @Autowired
    private AgenteOrquestador agenteOrquestador;

    @Autowired
    private EnrutadorService enrutador;


    @PostMapping(value = "/procesar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String procesarPeticion(
            @RequestParam("prompt") String prompt,
            @RequestParam(value = "archivo", required = false) MultipartFile archivo) {
       
        try {
            String promptParaClasificar = prompt;
            
            String promptParaEspecialista = prompt;

            if (archivo != null && !archivo.isEmpty()) {
                System.out.println("[ORQUESTADOR] Archivo adjunto detectado: " + archivo.getOriginalFilename());
                
                promptParaClasificar += "\n[Nota del sistema: El usuario ha adjuntado exitosamente un archivo fuente llamado '" + archivo.getOriginalFilename() + "'. Clasifica esta intención asumiendo que el código está presente].";

                String contenidoArchivo = new String(archivo.getBytes(), StandardCharsets.UTF_8);
                
                promptParaEspecialista += "\n\n--- Contenido del archivo (" + archivo.getOriginalFilename() + ") ---\n";
                promptParaEspecialista += contenidoArchivo;
            }

            ClasificacionDTO clasificacion = agenteOrquestador.clasificar(promptParaClasificar);
            System.out.println("[ORQUESTADOR] Intención detectada: " + clasificacion.getDepartamento());

            return enrutador.enrutarPeticion(promptParaEspecialista, clasificacion);
            
        } catch (Exception e) {
            return "[Error en el Orquestador] Hubo un problema al procesar la petición: " + e.getMessage();
        }
    }
}