package com.tasf.prototipoDesarrollo.Especialista;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class HerramientasDesarrollo {

    // Define la carpeta donde se encontrarán los demás subdirectorios y archivos
    private static final String DIRECTORIO_RAIZ = System.getProperty("user.dir");
    
    @Tool("Lee un archivo. El parámetro 1 debe ser el nombre exacto del archivo, el parametro 2 debe ser el nombre del directorio donde se encuentra ese archivo")
    public String leerArchivoProyecto(String nombreArchivo, String nombreDir) {
        
        System.out.println("[TOOL CALLING] El Agente solicitó leer: " + nombreDir + "/" + nombreArchivo);
        
        try {
            // Concatenamos el directorio y el archivo, y pasamos por el validador de seguridad
            String rutaRelativa = nombreDir + "/" + nombreArchivo;
            Path rutaArchivo = validarRutaSegura(rutaRelativa); 
            
            if (Files.exists(rutaArchivo)) {
                return Files.readString(rutaArchivo);
            } else {
                return "[Error] El archivo " + nombreArchivo + " no existe en la ruta " + rutaArchivo.toString();
            }
        } catch (SecurityException e) {
            return "[Acceso Denegado] " + e.getMessage();
        } catch (Exception e) {
            return "[Error de Lectura] " + e.getMessage();
        }
    }

    /*@Tool("Crea un archivo nuevo o sobrescribe uno existente con el código proporcionado.")
    public String escribirArchivo(
            @P("La ruta relativa del archivo, ej: src/main/java/Prueba.java") String ruta,
            @P("El contenido exacto a guardar") String contenido) {
        
        System.out.println("⚡ [TOOL CALLING] El Agente intentará escribir en: " + ruta);
        
        try {
            Path pathDestino = validarRutaSegura(ruta);
            Files.createDirectories(pathDestino.getParent());
            Files.writeString(pathDestino, contenido);
            return "Éxito: Archivo guardado correctamente en " + pathDestino.toString();
        } catch (SecurityException e) {
            return "[Acceso Denegado] " + e.getMessage();
        } catch (Exception e) {
            return "[Error al escribir] " + e.getMessage();
        }
    }*/
    
    /*private final RestTemplate restTemplate = new RestTemplate();
    private static final String URL_QA_INTERNO = "http://localhost:8083/api/qa/interno/perfeccionar";

    @Tool("Obligatorio usar antes de entregar el código final. Envía tu código fuente al agente de QA para que lo audite y corrija. QA te devolverá la versión perfeccionada.")
    public String enviarAQA(
            @P("El código fuente completo en texto plano que acabas de escribir y necesitas que QA perfeccione.") String codigoCrudo) {
        
        System.out.println("🤝 [DESARROLLO] Enviando código a QA para revisión técnica...");
        
        try {
            String codigoPerfeccionado = restTemplate.postForObject(URL_QA_INTERNO, codigoCrudo, String.class);
            System.out.println("✅ [DESARROLLO] Código perfeccionado recibido desde QA.");
            
            return "QA ha revisado y corregido tu código. Aquí está la versión perfeccionada: \n" + codigoPerfeccionado + 
                   "\n\nInstrucción: Entrega este código final al usuario.";
                   
        } catch (Exception e) {
            System.out.println("❌ [DESARROLLO] Error de red al contactar a QA: " + e.getMessage());
            return "QA no está disponible. Debes revisar tu propio código minuciosamente y entregarlo bajo tu propia responsabilidad.";
        }
    }*/


    // 🔒 MÉTODO DE SEGURIDAD INTERNO (No expuesto al modelo)
    private Path validarRutaSegura(String rutaSolicitada) throws SecurityException {
        Path pathBase = Paths.get(DIRECTORIO_RAIZ).normalize().toAbsolutePath();
        Path pathDestino = pathBase.resolve(rutaSolicitada).normalize().toAbsolutePath();

        // Verificamos matemáticamente que la ruta final siga estando dentro de la carpeta raíz
        if (!pathDestino.startsWith(pathBase)) {
            throw new SecurityException("Intento de manipulación fuera del directorio permitido del proyecto.");
        }
        return pathDestino;
    }
}