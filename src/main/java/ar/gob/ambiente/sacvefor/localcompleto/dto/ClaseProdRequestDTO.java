/*
 * Objetos DTO para gestionar los servicios para la versión 2 de TRAZ
 */
package ar.gob.ambiente.sacvefor.localcompleto.dto;

import java.io.Serializable;

/**
 * Objeto que encapsula los atributos de una Clase de productos
 * para ser persistido mediante el servicio correspondiente
 * @author rincostante
 */
public class ClaseProdRequestDTO implements Serializable{
    private Long id;
    private String nombre;
    private Long id_unidad;
    private boolean habilitado;
    private int nivel_transformacion;
    private Long id_clase_origen;
    private int grado_elaboracion;
    private boolean genera_residuos;
    private int factor_transf_directo;
    private boolean define_piezas;
    
    public ClaseProdRequestDTO(){
        
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

    public Long getId_unidad() {
        return id_unidad;
    }

    public void setId_unidad(Long id_unidad) {
        this.id_unidad = id_unidad;
    }

    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }

    public int getNivel_transformacion() {
        return nivel_transformacion;
    }

    public void setNivel_transformacion(int nivel_transformacion) {
        this.nivel_transformacion = nivel_transformacion;
    }

    public Long getId_clase_origen() {
        return id_clase_origen;
    }

    public void setId_clase_origen(Long id_clase_origen) {
        this.id_clase_origen = id_clase_origen;
    }

    public int getGrado_elaboracion() {
        return grado_elaboracion;
    }

    public void setGrado_elaboracion(int grado_elaboracion) {
        this.grado_elaboracion = grado_elaboracion;
    }

    public boolean isGenera_residuos() {
        return genera_residuos;
    }

    public void setGenera_residuos(boolean genera_residuos) {
        this.genera_residuos = genera_residuos;
    }

    public int getFactor_transf_directo() {
        return factor_transf_directo;
    }

    public void setFactor_transf_directo(int factor_transf_directo) {
        this.factor_transf_directo = factor_transf_directo;
    }

    public boolean isDefine_piezas() {
        return define_piezas;
    }

    public void setDefine_piezas(boolean define_piezas) {
        this.define_piezas = define_piezas;
    }    
}
