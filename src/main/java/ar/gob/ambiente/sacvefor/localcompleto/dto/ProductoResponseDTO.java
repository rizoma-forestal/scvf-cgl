/*
 * Objetos DTO para gestionar los servicios para la versión 2 de TRAZ
 */
package ar.gob.ambiente.sacvefor.localcompleto.dto;

import java.io.Serializable;

/**
 * Objeto que encapsula los atributos de un Producto
 * Contiene todos los atributos del producto
 * @author rincostante
 */
public class ProductoResponseDTO implements Serializable {
    private Long id;
    private EspecieLocalDTO especie_local;
    private ClaseProdGenericoResponseDTO clase;
    private float equival_kg;
    private float equival_m3;
    private String fecha_alta;
    private boolean habilitado;
    
    public ProductoResponseDTO() {
        
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EspecieLocalDTO getEspecie_local() {
        return especie_local;
    }

    public void setEspecie_local(EspecieLocalDTO especie_local) {
        this.especie_local = especie_local;
    }

    public ClaseProdGenericoResponseDTO getClase() {
        return clase;
    }

    public void setClase(ClaseProdGenericoResponseDTO clase) {
        this.clase = clase;
    }

    public float getEquival_kg() {
        return equival_kg;
    }

    public void setEquival_kg(float equival_kg) {
        this.equival_kg = equival_kg;
    }

    public float getEquival_m3() {
        return equival_m3;
    }

    public void setEquival_m3(float equival_m3) {
        this.equival_m3 = equival_m3;
    }

    public String getFecha_alta() {
        return fecha_alta;
    }

    public void setFecha_alta(String fecha_alta) {
        this.fecha_alta = fecha_alta;
    }

    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }
}
