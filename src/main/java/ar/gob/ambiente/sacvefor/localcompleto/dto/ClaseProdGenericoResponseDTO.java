/*
 * Objetos DTO para gestionar los servicios para la versión 2 de TRAZ
 */
package ar.gob.ambiente.sacvefor.localcompleto.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Objeto que encapsula los atributos de una Clase de productos
 * Solo contiene los atributos básicos
 * @author rincostante
 */
public class ClaseProdGenericoResponseDTO implements Serializable {
    private Long id;
    private String nombre;
    private String nombre_unidad;
    private List<SubClaseProdResponseDTO> sub_clases; 
    
    public ClaseProdGenericoResponseDTO(){
        
    }

    public List<SubClaseProdResponseDTO> getSub_clases() {
        return sub_clases;
    }

    public void setSub_clases(List<SubClaseProdResponseDTO> sub_clases) {
        this.sub_clases = sub_clases;
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

    public String getNombre_unidad() {
        return nombre_unidad;
    }

    public void setNombre_unidad(String nombre_unidad) {
        this.nombre_unidad = nombre_unidad;
    }
}
