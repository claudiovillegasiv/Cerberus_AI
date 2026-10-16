package com.tasf.prototipo.Orquestador;

public class RespuestaChatDTO {
    private String respuesta;
    private ClasificacionDTO clasificacion;

    public RespuestaChatDTO(String respuesta, ClasificacionDTO clasificacion) {
        this.respuesta = respuesta;
        this.clasificacion = clasificacion;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    public ClasificacionDTO getClasificacion() {
        return clasificacion;
    }

    public void setClasificacion(ClasificacionDTO clasificacion) {
        this.clasificacion = clasificacion;
    }
}