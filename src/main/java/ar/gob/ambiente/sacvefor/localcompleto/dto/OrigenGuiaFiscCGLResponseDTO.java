/*
 * Objetos DTO para gestionar los servicios para la versión 2 de TRAZ
 */
package ar.gob.ambiente.sacvefor.localcompleto.dto;

/**
 * Objeto para encapsular los atributos de una EntidadGuia, que oficia de origen
 * Utilizado como atributo de GuiaFiscCGLResponseDTO
 * @author rincostante
 */
public class OrigenGuiaFiscCGLResponseDTO {
    
    private Long id;
    private Long cuit_actuante;
    private String nombre_actuante;
    private String email_actuante;
    private Long id_establecimiento;
    
    public OrigenGuiaFiscCGLResponseDTO(){
        
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCuit_actuante() {
        return cuit_actuante;
    }

    public void setCuit_actuante(Long cuit_actuante) {
        this.cuit_actuante = cuit_actuante;
    }

    public String getNombre_actuante() {
        return nombre_actuante;
    }

    public void setNombre_actuante(String nombre_actuante) {
        this.nombre_actuante = nombre_actuante;
    }

    public String getEmail_actuante() {
        return email_actuante;
    }

    public void setEmail_actuante(String email_actuante) {
        this.email_actuante = email_actuante;
    }

    public Long getId_establecimiento() {
        return id_establecimiento;
    }

    public void setId_establecimiento(Long id_establecimiento) {
        this.id_establecimiento = id_establecimiento;
    }
}
