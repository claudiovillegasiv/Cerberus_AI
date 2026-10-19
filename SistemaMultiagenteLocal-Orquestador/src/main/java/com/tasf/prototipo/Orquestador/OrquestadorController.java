package com.tasf.prototipo.Orquestador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> procesarPeticion(
       @RequestParam("prompt") String prompt,
       @RequestParam(value = "archivo", required = false) MultipartFile archivo) {
       
        try {
            String promptParaClasificar = prompt;
            String promptParaEspecialista = prompt;

            if (archivo != null && !archivo.isEmpty()) {
                String nombreOriginal = archivo.getOriginalFilename();
                System.out.println("[ORQUESTADOR] Archivo adjunto detectado: " + nombreOriginal);
                
                // 1. Validar que el archivo sea texto antes de leer los bytes
                if (!esArchivoDeTexto(nombreOriginal)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("[Filtro de Seguridad] El sistema no soporta archivos binarios (Excel, PDF, Imágenes). Por favor, sube solo archivos de código fuente o texto plano.");
                }

                // 2. Si pasa el filtro, se lee de forma segura
                promptParaClasificar += "\n[Nota del sistema: El usuario ha adjuntado exitosamente un archivo fuente llamado '" + nombreOriginal + "'. Clasifica esta intención asumiendo que el código está presente].";

                String contenidoArchivo = new String(archivo.getBytes(), StandardCharsets.UTF_8);
                
                promptParaEspecialista += "\n\n--- Contenido del archivo (" + nombreOriginal + ") ---\n";
                promptParaEspecialista += contenidoArchivo;
            }

            ClasificacionDTO clasificacion = agenteOrquestador.clasificar(promptParaClasificar);
            System.out.println("[ORQUESTADOR] Intención detectada: " + clasificacion.getDepartamento());

            return enrutador.enrutarPeticion(promptParaEspecialista, clasificacion);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("[Error en el Orquestador] Hubo un problema al procesar la petición: " + e.getMessage());
        }
    }
    
    private boolean esArchivoDeTexto(String nombreArchivo) {
        if (nombreArchivo == null) return false;
        
        String nombreLower = nombreArchivo.toLowerCase();
        String[] extensionesPermitidas = {
            ".java", ".txt", ".json", ".xml", ".properties", 
            ".md", ".yml", ".yaml", ".sql", ".html", ".css", ".js"
        };

        for (String ext : extensionesPermitidas) {
            if (nombreLower.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }
}