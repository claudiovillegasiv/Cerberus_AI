package com.tasf.prototipoRRHH.Especialista;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class HerramientasRH {
	
    @Autowired
    private JavaMailSender mailSender;

    // ==========================================
    // HERRAMIENTAS EXPUESTAS AL AGENTE (LLM)
    // ==========================================

    // 1. RUTAS DINÁMICAS Y SEGURAS
    private static final String DIRECTORIO_RAIZ = System.getProperty("user.dir");
    private static final String CARPETA_POLITICAS = "Politicas";
    private static final String CARPETA_CANDIDATOS = "candidatos";
    private static final String CARPETA_CONTACTOS = "Contactos";


    @Tool("Consulta las políticas internas, manuales o normativas de la empresa. Úsalo SIEMPRE que te pregunten sobre reglas.")
    public String consultarPoliticaInterna(
            @P("El nombre exacto del archivo de política a leer, ej: vacaciones.txt") String documento) {
        
        System.out.println("⚡ [TOOL CALLING RRHH] El agente solicitó leer la política: " + documento);
        return leerArchivoSeguro(CARPETA_POLITICAS, documento);
    }
    
    @Tool("Lee el currículum de un candidato. Úsalo cuando te pidan evaluar el perfil, las habilidades o la experiencia de un postulante.")
    public String leerCurriculumCandidato(
            @P("El nombre exacto del archivo del currículum, ej: cv_pedro_perez.txt") String nombreArchivo) {
        
        System.out.println("⚡ [TOOL CALLING RRHH] El agente solicitó leer el CV: " + nombreArchivo);
        return leerArchivoSeguro(CARPETA_CANDIDATOS, nombreArchivo);
    }

    @Tool("Escanea y lee TODOS los currículums en la carpeta de candidatos. Úsalo para comparar múltiples perfiles y decidir cuál es el mejor.")
    public String escanearTodosLosCandidatos(
            @P("Escribe exactamente la palabra 'confirmar' para ejecutar el escaneo") String parametroObligatorio) {
        
        System.out.println("⚡ [TOOL CALLING RRHH] El agente está escaneando toda la carpeta de candidatos...");
        StringBuilder todosLosCVs = new StringBuilder();
        
        try {
            Path rutaDirectorio = Paths.get(DIRECTORIO_RAIZ, CARPETA_CANDIDATOS).normalize().toAbsolutePath();
            
            if (!Files.exists(rutaDirectorio)) {
                return "[Error] La carpeta de candidatos no existe.";
            }


            Files.list(rutaDirectorio)
                 .filter(Files::isRegularFile)
                 .forEach(archivo -> {
                     String nombreArchivo = archivo.getFileName().toString();
                     String contenido = leerArchivoSeguro(CARPETA_CANDIDATOS, nombreArchivo);
                     
                     todosLosCVs.append("=== Archivo: ").append(nombreArchivo).append(" ===\n");
                     todosLosCVs.append(contenido).append("\n\n");
                 });

            if (todosLosCVs.length() == 0) {
                return "La carpeta de candidatos está vacía.";
            }
            return todosLosCVs.toString();

        } catch (Exception e) {
            return "[Error al escanear directorio] " + e.getMessage();
        }
    }

    @Tool("Útil EXCLUSIVAMENTE para enviar correos electrónicos a empleados o candidatos. DEBES redactar el mensaje completo. TIENES PROHIBIDO usar textos de relleno.")
    public String enviarCorreo(
            @P("Dirección de correo electrónico del destinatario") String destinatario,
            @P("Asunto corto y descriptivo del correo") String asunto,
            @P("Cuerpo completo del mensaje. NO USES CORCHETES ni frases como 'insertar resumen'.") String cuerpo) {
        
    	// --- INICIO DE LA VALIDACIÓN DEFENSIVA EQUILIBRADA ---
        String cuerpoMinusculas = cuerpo.toLowerCase();
        
        boolean tieneMarcadores = cuerpo.contains("[") || cuerpo.contains("]") || cuerpo.contains("<") || cuerpo.contains(">");
        boolean tieneFrasesTrampa = cuerpoMinusculas.contains("insertar") || 
                                    cuerpoMinusculas.contains("resumen del currículum leído") ||
                                    cuerpoMinusculas.equals("resumen del currículum") ||
                                    cuerpoMinusculas.contains("texto de relleno");

        if (tieneMarcadores || tieneFrasesTrampa) { 
            
            System.out.println("⚠️ [INTERVENCIÓN] El modelo intentó usar texto de relleno: " + cuerpo);
            return "Fallo interno: Estás intentando usar un texto de relleno. " +
                   "Por favor, invoca primero la herramienta para leer el documento, analiza su contenido, " +
                   "y luego vuelve a llamar a esta herramienta de correo redactando el texto real y completo.";
        }
        // --- FIN DE LA VALIDACIÓN DEFENSIVA EQUILIBRADA ---

        System.out.println("⚡ [TOOL CALLING RRHH] Preparando envío de correo a: " + destinatario);
        
        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setTo(destinatario);
            mensaje.setSubject(asunto);
            mensaje.setText(cuerpo);
            
            mailSender.send(mensaje);
            
            System.out.println("✅ [HERRAMIENTA RRHH] Correo enviado exitosamente a Mailtrap.");
            return "El correo electrónico fue enviado exitosamente a " + destinatario + " con el asunto: '" + asunto + "'.";
            
        } catch (Exception e) {
            System.out.println("❌ [HERRAMIENTA RRHH] Error al enviar el correo: " + e.getMessage());
            return "Fallo al enviar el correo electrónico a " + destinatario + ". Motivo del error: " + e.getMessage();
        }
    }
    
    @Tool("Envía un correo masivo (como avisos, comunicados o informes generales) a una lista de empleados. Lee los destinatarios desde un archivo de texto.")
    public String enviarCorreoMasivo(
            @P("El nombre exacto del archivo que contiene la lista de correos, ej: correos.txt") String nombreArchivo,
            @P("Asunto corto y descriptivo del correo masivo") String asunto,
            @P("Cuerpo del mensaje para todos. Puedes usar la etiqueta [Nombre] para que el sistema lo reemplace automáticamente por el nombre de cada empleado.") String cuerpo) {
        
        System.out.println("⚡ [TOOL CALLING RRHH] Iniciando envío masivo usando la lista: " + nombreArchivo);

        String contenidoLista = leerArchivoSeguro(CARPETA_CONTACTOS, nombreArchivo);
        
        if (contenidoLista.startsWith("[Error]") || contenidoLista.startsWith("[Acceso Denegado]")) {
            return "Fallo al leer la lista de correos: " + contenidoLista;
        }

        String[] lineas = contenidoLista.split("\\r?\\n");
        int correosEnviados = 0;
        int errores = 0;

        for (String linea : lineas) {
            if (linea.trim().isEmpty()) continue;

            String[] partes = linea.trim().split("\\s+", 2);
            String correoDestino = partes[0];
            String nombreDestino = (partes.length > 1) ? partes[1] : "Compañero";

            try {
                SimpleMailMessage mensaje = new SimpleMailMessage();
                mensaje.setTo(correoDestino);
                mensaje.setSubject(asunto);
                
                String cuerpoPersonalizado = cuerpo.replace("[Nombre]", nombreDestino)
                                                   .replace("[nombre]", nombreDestino);
                
                mensaje.setText(cuerpoPersonalizado);
                mailSender.send(mensaje);
                
                System.out.println("  -> ✅ Enviado a: " + correoDestino + " (" + nombreDestino + ")");
                correosEnviados++;
                //borrar la siguiente linea después de pruebas
                TimeUnit.SECONDS.sleep(15); // Pausa por 2 segundos
                //borrar la anterior linea después de pruebas
                
            } catch (Exception e) {
                System.out.println("  -> ❌ Error al enviar a " + correoDestino + ": " + e.getMessage());
                errores++;
            }
        }

        return "El envío masivo ha finalizado. Correos enviados exitosamente: " + correosEnviados + ". Errores: " + errores + ". Informa al usuario del resultado.";
    }

    // ==========================================
    // MÉTODOS INTERNOS MAESTROS (Ocultos al LLM)
    // ==========================================

    /**
     * ÚNICO método encargado de abrir archivos y leer su texto en todo el microservicio.
     */
    private String leerArchivoSeguro(String carpetaBase, String nombreArchivo) {
        try {
            Path ruta = validarRutaSegura(carpetaBase, nombreArchivo);
            
            if (Files.exists(ruta) && Files.isRegularFile(ruta)) {
                return Files.readString(ruta);
            }
            return "[Error] No se encontró el documento llamado '" + nombreArchivo + "' en la carpeta '" + carpetaBase + "'.";
        } catch (SecurityException e) {
            return "[Acceso Denegado] " + e.getMessage();
        } catch (Exception e) {
            return "[Error de Lectura Interna] " + e.getMessage();
        }
    }

    /**
     * Valida matemáticamente que nadie intente leer archivos fuera de las carpetas permitidas.
     */
    private Path validarRutaSegura(String carpetaBase, String nombreArchivo) throws SecurityException {
        Path pathBase = Paths.get(DIRECTORIO_RAIZ, carpetaBase).normalize().toAbsolutePath();
        Path pathDestino = pathBase.resolve(nombreArchivo).normalize().toAbsolutePath();

        // Evita el path traversal (ej. nombreArchivo = "../../../Windows/System32/config")
        if (!pathDestino.startsWith(pathBase)) {
            throw new SecurityException("Intento de manipulación de rutas detectado.");
        }
        return pathDestino;
    }
}