/*
 * Objetos DTO para gestionar los servicios para la versión 2 de TRAZ
 */
package ar.gob.ambiente.sacvefor.localcompleto.dto;

/**
 * Objeto que encapsula los atributos de un Producto
 * para ser persistido mediante el servicio correspondiente
 * @author rincostante
 */
public class ProductoRequestDTO {
    private Long id;
    private Long id_especie_local;
    private Long id_clase;
    private float equival_kg;
    private float equival_m3;
    private boolean habilitado;
    
    public ProductoRequestDTO(){
        
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId_especie_local() {
        return id_especie_local;
    }

    public void setId_especie_local(Long id_especie_local) {
        this.id_especie_local = id_especie_local;
    }

    public Long getId_clase() {
        return id_clase;
    }

    public void setId_clase(Long id_clase) {
        this.id_clase = id_clase;
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

    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }
}
