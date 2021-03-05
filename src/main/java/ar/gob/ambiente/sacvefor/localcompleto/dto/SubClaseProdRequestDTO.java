
package ar.gob.ambiente.sacvefor.localcompleto.dto;

/**
 * Objeto que encapsula los atributos de una SubClase de productos
 * para ser persistido mediante el servicio correspondiente
 * @author rincostante
 */
public class SubClaseProdRequestDTO {
    private Long id;
    private Long id_clase_derivada;
    private float factor_relativo;
    private Long id_clase_principal;
    
    public SubClaseProdRequestDTO(){
        
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId_clase_derivada() {
        return id_clase_derivada;
    }

    public void setId_clase_derivada(Long id_clase_derivada) {
        this.id_clase_derivada = id_clase_derivada;
    }

    public float getFactor_relativo() {
        return factor_relativo;
    }

    public void setFactor_relativo(float factor_relativo) {
        this.factor_relativo = factor_relativo;
    }

    public Long getId_clase_principal() {
        return id_clase_principal;
    }

    public void setId_clase_principal(Long id_clase_principal) {
        this.id_clase_principal = id_clase_principal;
    }
   
}
