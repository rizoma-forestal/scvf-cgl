/*
 * Objetos DTO para gestionar los servicios para la versión 2 de TRAZ
 */
package ar.gob.ambiente.sacvefor.localcompleto.dto;

import java.util.List;

/**
 * Objeto que contiene los atributos básicos de una Guía de fiscalización
 * Utilizado como response de los métodos de busquedas de Guías para fuentes
 * @author rincostante
 */
public class GuiaFiscCGLResponseDTO {
    
    private Long id;
    private String codigo;
    private GenericoParamCGLResponseDTO tipo;
    private String num_fuente;
    private OrigenGuiaFiscCGLResponseDTO origen;
    private String fecha_emision_guia;
    private String fecha_vencimiento;
    private boolean destino_externo;
    private List<GenericoParamCGLResponseDTO> rodales;
    
    public GuiaFiscCGLResponseDTO(){
        
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public GenericoParamCGLResponseDTO getTipo() {
        return tipo;
    }

    public void setTipo(GenericoParamCGLResponseDTO tipo) {
        this.tipo = tipo;
    }

    public String getNum_fuente() {
        return num_fuente;
    }

    public void setNum_fuente(String num_fuente) {
        this.num_fuente = num_fuente;
    }

    public OrigenGuiaFiscCGLResponseDTO getOrigen() {
        return origen;
    }

    public void setOrigen(OrigenGuiaFiscCGLResponseDTO origen) {
        this.origen = origen;
    }

    public String getFecha_emision_guia() {
        return fecha_emision_guia;
    }

    public void setFecha_emision_guia(String fecha_emision_guia) {
        this.fecha_emision_guia = fecha_emision_guia;
    }

    public String getFecha_vencimiento() {
        return fecha_vencimiento;
    }

    public void setFecha_vencimiento(String fecha_vencimiento) {
        this.fecha_vencimiento = fecha_vencimiento;
    }

    public boolean isDestino_externo() {
        return destino_externo;
    }

    public void setDestino_externo(boolean destino_externo) {
        this.destino_externo = destino_externo;
    }

    public List<GenericoParamCGLResponseDTO> getRodales() {
        return rodales;
    }

    public void setRodales(List<GenericoParamCGLResponseDTO> rodales) {
        this.rodales = rodales;
    }
}
