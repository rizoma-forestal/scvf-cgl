/*
 * Objetos DTO para gestionar los servicios para la versión 2 de TRAZ
 */
package ar.gob.ambiente.sacvefor.localcompleto.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Objeto que contiene los atributos básicos de una Autorización
 * Utilizado como response de los métodos de busquedas de Autorizaciones
 * @author rincostante
 */
public class AutorizacionCGLResponseDTO implements Serializable {
    
    private Long id;
    private GenericoParamCGLResponseDTO tipo;
    private String numero;
    private String fecha_instrumento;
    private GenericoParamCGLResponseDTO intervencion;
    private GenericoParamCGLResponseDTO cuenca_forestal;
    private List<GenericoParamCGLResponseDTO> zonas;
    private List<GenericoPersonaCGLResponseDTO> proponentes;
    private List<PredioGCLResponseDTO> predios;
    private String num_exp;
    private String fecha_vencimiento;
    
    public AutorizacionCGLResponseDTO() {
        
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GenericoParamCGLResponseDTO getTipo() {
        return tipo;
    }

    public void setTipo(GenericoParamCGLResponseDTO tipo) {
        this.tipo = tipo;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getFecha_instrumento() {
        return fecha_instrumento;
    }

    public void setFecha_instrumento(String fecha_instrumento) {
        this.fecha_instrumento = fecha_instrumento;
    }

    public GenericoParamCGLResponseDTO getIntervencion() {
        return intervencion;
    }

    public void setIntervencion(GenericoParamCGLResponseDTO intervencion) {
        this.intervencion = intervencion;
    }

    public GenericoParamCGLResponseDTO getCuenca_forestal() {
        return cuenca_forestal;
    }

    public void setCuenca_forestal(GenericoParamCGLResponseDTO cuenca_forestal) {
        this.cuenca_forestal = cuenca_forestal;
    }

    public List<GenericoParamCGLResponseDTO> getZonas() {
        return zonas;
    }

    public void setZonas(List<GenericoParamCGLResponseDTO> zonas) {
        this.zonas = zonas;
    }

    public List<GenericoPersonaCGLResponseDTO> getProponentes() {
        return proponentes;
    }

    public void setProponentes(List<GenericoPersonaCGLResponseDTO> proponentes) {
        this.proponentes = proponentes;
    }

    public List<PredioGCLResponseDTO> getPredios() {
        return predios;
    }

    public void setPredios(List<PredioGCLResponseDTO> predios) {
        this.predios = predios;
    }

    public String getNum_exp() {
        return num_exp;
    }

    public void setNum_exp(String num_exp) {
        this.num_exp = num_exp;
    }

    public String getFecha_vencimiento() {
        return fecha_vencimiento;
    }

    public void setFecha_vencimiento(String fecha_vencimiento) {
        this.fecha_vencimiento = fecha_vencimiento;
    }    
}
