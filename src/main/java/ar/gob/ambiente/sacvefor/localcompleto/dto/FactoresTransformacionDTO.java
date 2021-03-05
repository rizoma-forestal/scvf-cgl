
package ar.gob.ambiente.sacvefor.localcompleto.dto;

import java.io.Serializable;

/**
 * Objeto que encapsula los atributos de factores de transformación dinámica de productos
 * @author rincostante
 */
public class FactoresTransformacionDTO implements Serializable {
    
    private Long id;
    private int grupo_especie;
    private int grado_elaboracion;
    private int nivel_desempenio;
    private float factor_transf_total;
    private float factor_residuos_grado;
    
    public FactoresTransformacionDTO() {
        
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getGrupo_especie() {
        return grupo_especie;
    }

    public void setGrupo_especie(int grupo_especie) {
        this.grupo_especie = grupo_especie;
    }

    public int getGrado_elaboracion() {
        return grado_elaboracion;
    }

    public void setGrado_elaboracion(int grado_elaboracion) {
        this.grado_elaboracion = grado_elaboracion;
    }

    public int getNivel_desempenio() {
        return nivel_desempenio;
    }

    public void setNivel_desempenio(int nivel_desempenio) {
        this.nivel_desempenio = nivel_desempenio;
    }

    public float getFactor_transf_total() {
        return factor_transf_total;
    }

    public void setFactor_transf_total(float factor_transf_total) {
        this.factor_transf_total = factor_transf_total;
    }

    public float getFactor_residuos_grado() {
        return factor_residuos_grado;
    }

    public void setFactor_residuos_grado(float factor_residuos_grado) {
        this.factor_residuos_grado = factor_residuos_grado;
    }
}
