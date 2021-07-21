/*
 * Objetos DTO para gestionar los servicios para la versión 2 de TRAZ
 */
package ar.gob.ambiente.sacvefor.localcompleto.dto;

import java.io.Serializable;

/**
 * Objeto que contiene id y nombre para encapsular de forma genérica una Paramétrica
 * Utilizado como atributo de AutorizacionCGLResponseDTO
 * @author rincostante
 */
public class GenericoParamCGLResponseDTO implements Serializable {
    
    private Long id;
    private String nombre;
    
    public GenericoParamCGLResponseDTO() {
        
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }    
}
