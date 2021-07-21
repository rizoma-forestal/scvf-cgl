
package ar.gob.ambiente.sacvefor.localcompleto.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Entidad que gestiona las clases de los productos:
 * Rollo
 * Carbón
 * Poste
 * etc.
 * @author rincostante
 */
@Entity
@XmlRootElement
public class ProductoClase implements Serializable {

    private static final long serialVersionUID = 1L;
    
    /**
     * Variable privada: Identificador único
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    
    /**
     * Variable privada: Guarda la Unidad de medida correspondiente a la Clase
     */
    @ManyToOne
    @JoinColumn(name="unidad_id", nullable=false)
    @NotNull(message = "Debe existir una unidad")
    private ProductoUnidadMedida unidad;
    
    /**
     * Variable privada: condición de habilitado de la clase de producto
     */
    private boolean habilitado;
    
    /**
     * Variable privada: número entero que indica el nivel de transformación de la clase de producto
     * (0 => sin transformación, 1 => primera transformación, 2 => segunda transformación)
     * Agregada para la versión 2 de TRAZ
     */
    private int nivelTransformacion;
    
    /**
     * Variable privada: entidad ProductoClase que representa la clase utilizada como materia prima
     * Solo para las que tienen nivel de transformación > 0
     * Agregada para la versión 2 de TRAZ
     */
    @ManyToOne
    @JoinColumn(name="clase_origen_id", nullable=true)
    private ProductoClase claseOrigen;
    
    /**
     * Variable privada: Listado de las clases de las cuales es materia prima
     * Podrá setearse para cualquier nivel de transformación
     * Agregada para la versión 2 de TRAZ
     */
    @OneToMany (mappedBy="claseOrigen", orphanRemoval = true)
    @Basic(fetch = FetchType.LAZY)    
    private List<ProductoClase> clasesDestino;
    
    /**
     * Variable privada: número entero que indica el grado de elaboración (1 – 2 – 3 – 4 – 5)
     * solo para clases de productos con nivelTransformacion > 0
     * Agregada para la versión 2 de TRAZ
     */
    private int gradoElaboracion;
    
    /**
     * Variable privada: condición de generar residuos al generar productos de esta clase
     * solo para clases de productos con nivelTransformacion > 0
     * Agregada para la versión 2 de TRAZ
     */    
    private boolean generaResiduos;
    
    /**
     * Variable privada: número entero que indica el porcentaje a aplicar como factor de transformación sin tener en cuenta otros parámetros.
     * solo para clases de productos con nivelTransformacion > 0 y que tengan leña como clase de origen
     * Agregada para la versión 2 de TRAZ
     */    
    private int factorTransfDirecto;
    
    /**
     * Variable privada: condición de definir un detalle de piezas para conformar el producto
     * solo para clases de productos con nivelTransformacion > 0
     * Agregada para la versión 2 de TRAZ
     */    
    private boolean definePiezas;
    
    /**
     * Variable privada: listado de sub clases derivadas del proceso de transformación de una
     * clase origen a una clase destino de un grado de elaboración mayor
     * Agregada para la versión de 2 de TRAZ y para la transformación de productos en removido
     */
    @OneToMany (mappedBy="clasePrincipal")
    private List<ProductoSubClase> subClases;
    
    public ProductoClase(){
        subClases = new ArrayList<>();
        clasesDestino = new ArrayList<>();
    }

    // métodos de acceso   
    @XmlTransient
    public int getNivelTransformacion() {
        return nivelTransformacion;
    }

    @XmlTransient
    public List<ProductoSubClase> getSubClases() {
        return subClases;
    }

    public void setSubClases(List<ProductoSubClase> subClases) {
        this.subClases = subClases;
    }

    public void setNivelTransformacion(int nivelTransformacion) {
        this.nivelTransformacion = nivelTransformacion;
    }

    @XmlTransient
    public ProductoClase getClaseOrigen() {
        return claseOrigen;
    }

    public void setClaseOrigen(ProductoClase claseOrigen) {
        this.claseOrigen = claseOrigen;
    }

    @XmlTransient
    public List<ProductoClase> getClasesDestino() {
        return clasesDestino;
    }

    public void setClasesDestino(List<ProductoClase> clasesDestino) {
        this.clasesDestino = clasesDestino;
    }

    @XmlTransient
    public int getGradoElaboracion() {
        return gradoElaboracion;
    }

    public void setGradoElaboracion(int gradoElaboracion) {
        this.gradoElaboracion = gradoElaboracion;
    }
    
    @XmlTransient
    public boolean isGeneraResiduos() {
        return generaResiduos;
    }

    public void setGeneraResiduos(boolean generaResiduos) {
        this.generaResiduos = generaResiduos;
    }

    @XmlTransient
    public int getFactorTransfDirecto() {
        return factorTransfDirecto;
    }

    public void setFactorTransfDirecto(int factorTransfDirecto) {
        this.factorTransfDirecto = factorTransfDirecto;
    }

    @XmlTransient
    public boolean isDefinePiezas() {
        return definePiezas;
    }

    public void setDefinePiezas(boolean definePiezas) {
        this.definePiezas = definePiezas;
    }
    
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public ProductoUnidadMedida getUnidad() {
        return unidad;
    }

    public void setUnidad(ProductoUnidadMedida unidad) {
        this.unidad = unidad;
    }

    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }
    
    public Long getId() {
        return id;
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
        if (!(object instanceof ProductoClase)) {
            return false;
        }
        ProductoClase other = (ProductoClase) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.gob.ambiente.sacvefor.localcompleto.entities.ProductoClase[ id=" + id + " ]";
    }
    
}
