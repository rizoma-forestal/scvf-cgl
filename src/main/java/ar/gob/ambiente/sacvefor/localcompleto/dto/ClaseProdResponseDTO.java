/*
 * Objetos DTO para gestionar los servicios para la versión 2 de TRAZ
 */
package ar.gob.ambiente.sacvefor.localcompleto.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Objeto que encapsula los atributos de una Clase de productos
 * Contiene todos los atributos de la entidad sin el listado de clases derivadas
 * @author rincostante
 */
public class ClaseProdResponseDTO implements Serializable{
    private Long id;
    private String nombre;
    private UnidadMedidaDTO unidad;
    private boolean habilitado;
    private int nivel_transformacion;
    private ClaseProdGenericoResponseDTO clase_origen;
    private int grado_elaboracion;
    private boolean genera_residuos;
    private int factor_transf_directo;
    private boolean define_piezas;
    private List<SubClaseProdResponseDTO> sub_clases; 
    
    public ClaseProdResponseDTO(){
        
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

    public UnidadMedidaDTO getUnidad() {
        return unidad;
    }

    public void setUnidad(UnidadMedidaDTO unidad) {
        this.unidad = unidad;
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

    public ClaseProdGenericoResponseDTO getClase_origen() {
        return clase_origen;
    }

    public void setClase_origen(ClaseProdGenericoResponseDTO clase_origen) {
        this.clase_origen = clase_origen;
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
