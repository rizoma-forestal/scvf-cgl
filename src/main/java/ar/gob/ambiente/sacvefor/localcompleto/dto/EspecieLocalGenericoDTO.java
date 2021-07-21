/*
 * Objetos DTO para gestionar los servicios para la versión 2 de TRAZ
 */
package ar.gob.ambiente.sacvefor.localcompleto.dto;

import java.io.Serializable;

/**
 * Objeto que contiene los atributos básicos de una especie local
 * Utilizado como atributo de la especie local de un Producto
 * @author rincostante
 */
public class EspecieLocalGenericoDTO implements Serializable {
    private Long id;
    private String nombre_cientifico;
    private String nombre_vulgar;
    
    public EspecieLocalGenericoDTO() {
        
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre_cientifico() {
        return nombre_cientifico;
    }

    public void setNombre_cientifico(String nombre_cientifico) {
        this.nombre_cientifico = nombre_cientifico;
    }

    public String getNombre_vulgar() {
        return nombre_vulgar;
    }

    public void setNombre_vulgar(String nombre_vulgar) {
        this.nombre_vulgar = nombre_vulgar;
    }
}
