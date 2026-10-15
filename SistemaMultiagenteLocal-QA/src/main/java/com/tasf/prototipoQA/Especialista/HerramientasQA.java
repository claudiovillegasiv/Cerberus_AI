package com.tasf.prototipoQA.Especialista;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class HerramientasQA {

    private static final String DIRECTORIO_RAIZ = System.getProperty("user.dir");
    private static final String CARPETA_CODIGO = "CodigoQA";

    @Tool("Lee el contenido de un archivo de código fuente (ej. Java, XML, SQL) para auditarlo, buscar errores o sugerir pruebas. USA ESTA HERRAMIENTA SÓLO SI RECIBES UN ARCHIVO O SI ENCUENTRAS ALGUNA CARPETA LLAMADA 'codigo'.")
    public String inspeccionarCodigo(
            @P("El nombre exacto del archivo a inspeccionar, ej: MiControlador.java") String nombreArchivo) {
        
        System.out.println("⚡ [TOOL CALLING QA] Inspeccionando código del archivo: " + nombreArchivo);
        return leerArchivoSeguro(CARPETA_CODIGO, nombreArchivo);
    }

    // ==========================================
    // MÉTODOS INTERNOS MAESTROS (Ocultos al LLM)
    // ==========================================

    private String leerArchivoSeguro(String carpetaBase, String nombreArchivo) {
        try {
            Path ruta = validarRutaSegura(carpetaBase, nombreArchivo);
            
            if (Files.exists(ruta) && Files.isRegularFile(ruta)) {
                return Files.readString(ruta);
            }
            return "[Error] No se encontró el archivo de código llamado '" + nombreArchivo + "' en la carpeta '" + carpetaBase + "'.";
        } catch (SecurityException e) {
            return "[Acceso Denegado] " + e.getMessage();
        } catch (Exception e) {
            return "[Error de Lectura Interna] " + e.getMessage();
        }
    }

    private Path validarRutaSegura(String carpetaBase, String nombreArchivo) throws SecurityException {
        Path pathBase = Paths.get(DIRECTORIO_RAIZ, carpetaBase).normalize().toAbsolutePath();
        Path pathDestino = pathBase.resolve(nombreArchivo).normalize().toAbsolutePath();

        if (!pathDestino.startsWith(pathBase)) {
            throw new SecurityException("Intento de manipulación de rutas detectado.");
        }
        return pathDestino;
    }
}