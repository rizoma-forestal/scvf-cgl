
package ar.gob.ambiente.sacvefor.localcompleto.entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Entidad para gestionar los factores de transformación a aplicar según:
 * nivel de desempeño de establecimiento
 * grupo especie
 * grado de elaboración
 * Para hacer transformación de productos para guías de fiscalización y para remitos en TRAZ
 * @author rincostante
 */
@Entity
public class FactoresTransformacion implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "grupo_especie")
    private int grupoEspecie;
    
    @Column(name = "grado_elaboracion")
    private int gradoElaboracion;
    
    @Column(name = "nivel_desempenio")
    private int nivelDesempenio;
    
    @Column(name = "factor_transf_total")
    private float factorTransfTotal;
    
    @Column(name = "factor_residuos_grado")
    private float factorResiduosGrado;

    public Long getId() {
        return id;
    }

    public int getGrupoEspecie() {
        return grupoEspecie;
    }

    public void setGrupoEspecie(int grupoEspecie) {
        this.grupoEspecie = grupoEspecie;
    }

    public int getGradoElaboracion() {
        return gradoElaboracion;
    }

    public void setGradoElaboracion(int gradoElaboracion) {
        this.gradoElaboracion = gradoElaboracion;
    }

    public int getNivelDesempenio() {
        return nivelDesempenio;
    }

    public void setNivelDesempenio(int nivelDesempenio) {
        this.nivelDesempenio = nivelDesempenio;
    }

    public float getFactorTransfTotal() {
        return factorTransfTotal;
    }

    public void setFactorTransfTotal(float factorTransfTotal) {
        this.factorTransfTotal = factorTransfTotal;
    }

    public float getFactorResiduosGrado() {
        return factorResiduosGrado;
    }

    public void setFactorResiduosGrado(float factorResiduosGrado) {
        this.factorResiduosGrado = factorResiduosGrado;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof FactoresTransformacion)) {
            return false;
        }
        FactoresTransformacion other = (FactoresTransformacion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.gob.ambiente.sacvefor.localcompleto.entities.FatoresTransformacion[ id=" + id + " ]";
    }
    
}
