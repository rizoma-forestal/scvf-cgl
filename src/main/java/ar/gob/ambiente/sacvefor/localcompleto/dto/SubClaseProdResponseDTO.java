/*
 * Objetos DTO para gestionar los servicios para la versión 2 de TRAZ
 */

package ar.gob.ambiente.sacvefor.localcompleto.dto;

import java.io.Serializable;

/**
 * Objeto que encapsula los atributos de una Sub Clase de productos
 * Contiene todos los atributos de la entidad con la clase derivada en formato genérico
 * @author rincostante
 */
public class SubClaseProdResponseDTO implements Serializable {
    
    private Long id;
    private Long id_clase_derivada;
    private String nombre_clase_derivada;
    private String unidad_clase_derivada;
    private float factor_relativo;
    
    public SubClaseProdResponseDTO(){
        
    }

    public Long getId_clase_derivada() {
        return id_clase_derivada;
    }

    public void setId_clase_derivada(Long id_clase_derivada) {
        this.id_clase_derivada = id_clase_derivada;
    }

    public String getNombre_clase_derivada() {
        return nombre_clase_derivada;
    }

    public void setNombre_clase_derivada(String nombre_clase_derivada) {
        this.nombre_clase_derivada = nombre_clase_derivada;
    }

    public String getUnidad_clase_derivada() {
        return unidad_clase_derivada;
    }

    public void setUnidad_clase_derivada(String unidad_clase_derivada) {
        this.unidad_clase_derivada = unidad_clase_derivada;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public float getFactor_relativo() {
        return factor_relativo;
    }

    public void setFactor_relativo(float factor_relativo) {
        this.factor_relativo = factor_relativo;
    }

}
