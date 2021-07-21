/*
 * Objetos DTO para gestionar los servicios para la versión 2 de TRAZ
 */
package ar.gob.ambiente.sacvefor.localcompleto.dto;

import java.io.Serializable;

/**
 * Objeto que encapsula los atributos de una Especie local
 * @author rincostante
 */
public class EspecieLocalDTO implements Serializable {
    private Long id;
    private Long id_tax;
    private String nombre_cientifico;
    private String nombre_vulgar;
    private int grupo_especie;
    private String observaciones;
    private boolean habilitado;
    
    public EspecieLocalDTO(){
        
    }

    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId_tax() {
        return id_tax;
    }

    public void setId_tax(Long id_tax) {
        this.id_tax = id_tax;
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

    public int getGrupo_especie() {
        return grupo_especie;
    }

    public void setGrupo_especie(int grupo_especie) {
        this.grupo_especie = grupo_especie;
    }    
}
