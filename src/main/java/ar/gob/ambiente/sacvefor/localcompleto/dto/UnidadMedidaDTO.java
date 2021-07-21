/*
 * Objetos DTO para gestionar los servicios para la versión 2 de TRAZ
 */
package ar.gob.ambiente.sacvefor.localcompleto.dto;

import java.io.Serializable;

/**
 * Objeto que encapsula los atributos de una Unidad de medida
 * @author rincostante
 */
public class UnidadMedidaDTO implements Serializable {
    private Long id;
    private String nombre;
    private String abreviatura;
    private String tipo_numero;
    private boolean habilitado;
    
    public UnidadMedidaDTO(){
        
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

    public String getAbreviatura() {
        return abreviatura;
    }

    public void setAbreviatura(String abreviatura) {
        this.abreviatura = abreviatura;
    }

    public String getTipo_numero() {
        return tipo_numero;
    }

    public void setTipo_numero(String tipo_numero) {
        this.tipo_numero = tipo_numero;
    }

    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }
    
    
}
