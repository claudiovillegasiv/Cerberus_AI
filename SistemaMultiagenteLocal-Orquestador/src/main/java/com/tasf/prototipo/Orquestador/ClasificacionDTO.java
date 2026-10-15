package com.tasf.prototipo.Orquestador;

public class ClasificacionDTO {
    private String departamento;
    private String motivo;

    // Constructores vacíos y con parámetros
    public ClasificacionDTO() {}
    
    public ClasificacionDTO(String departamento, String motivo) {
        this.departamento = departamento;
        this.motivo = motivo;
    }

    // Getters y Setters
    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}