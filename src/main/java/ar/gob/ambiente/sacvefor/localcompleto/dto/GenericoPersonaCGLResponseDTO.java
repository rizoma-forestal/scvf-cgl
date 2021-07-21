/*
 * Objetos DTO para gestionar los servicios para la versión 2 de TRAZ
 */
package ar.gob.ambiente.sacvefor.localcompleto.dto;

import java.io.Serializable;

/**
 * Objeto que contiene cuit y nombre de Persona, vinculada como Proponente a una Autorización
 * Utilizado como atributo de AutorizacionCGLResponseDTO
 * @author rincostante
 */
public class GenericoPersonaCGLResponseDTO implements Serializable {
    
    private Long cuit;
    private String nombre;
    
    public GenericoPersonaCGLResponseDTO(){
        
    }

    public Long getCuit() {
        return cuit;
    }

    public void setCuit(Long cuit) {
        this.cuit = cuit;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }    
}
