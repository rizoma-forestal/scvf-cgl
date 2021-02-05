/*
 * Objetos DTO para gestionar los servicios para la versión 2 de TRAZ
 */
package ar.gob.ambiente.sacvefor.localcompleto.dto;

import java.io.Serializable;

/**
 * Objeto que contiene los atributos básicos de un Inmueble vinculado a una Autorización
 * Utilizado como atributo de AutorizacionCGLResponseDTO
 * @author rincostante
 */
public class PredioGCLResponseDTO implements Serializable {
    
    private Long id;
    private String id_catastral;
    private String nombre;
    private String domicilio;
    private String departamento;
    private String localidad;
    private Long id_loc_gt;
    
    public PredioGCLResponseDTO() {
        
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getId_catastral() {
        return id_catastral;
    }

    public void setId_catastral(String id_catastral) {
        this.id_catastral = id_catastral;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public Long getId_loc_gt() {
        return id_loc_gt;
    }

    public void setId_loc_gt(Long id_loc_gt) {
        this.id_loc_gt = id_loc_gt;
    }
}
