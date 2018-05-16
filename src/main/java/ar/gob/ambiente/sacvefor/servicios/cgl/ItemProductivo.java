
package ar.gob.ambiente.sacvefor.servicios.cgl;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Entidad que encapsula los datos de los Items productivos
 * @author rincostante
 */
@XmlRootElement
public class ItemProductivo implements Serializable {
    private Long id;
    private String nombreCientifico;
    private String nombreVulgar;
    private String clase;
    private String unidad;
    /**
     * Id de la Especie en el registro de Taxonomías
     */
    private Long idEspecieTax;
    /**
     * Equivalente en Kg de la unidad de medida del Producto
     */
    private float kilosXUnidad;
    /**
     * Total autorizado del Producto
     */
    private float total;
    /**
     * Equivalente en Kg del total autorizado
     */
    private float totalKg;
    /**
     * Saldo disponible del Producto
     */
    private float saldo;  
    private float saldoKg;  
    private String obs;
    /**
     * Código del producto constituído por:
     * nombreCientifico: nombre científico de la Especie constituido por 'Género/Especie'
     * nombreVulgar: nombre vulgar de la Especie definido de manera local
     * clase: clase en la que se comercializa el Producto definido de manera local
     * unidad: unidad de medida en la que se comercializa el Producto/Clase definido de manera local
     * resolución: numero de la resolución (campo numero de la entidad Autorización)
     * provincia: nombre de la Provincia dentro de la cual se extraerá el Producto
     * EJ.:"1|Prosopis caldenia|Caldén|Rollo|Unidad|123-DGB-2017|Santiago del Estero".
     */
    private String codigoProducto;
    
    /******************
     * Constructores **
     ******************/
    
    public ItemProductivo(){
        this.id = Long.valueOf(0);
        this.nombreCientifico = "default";
        this.nombreVulgar = "default";
        this.clase = "default";
        this.unidad = "default";
        this.idEspecieTax = Long.valueOf(0);
        this.kilosXUnidad = 0;
        this.total = 0;
        this.totalKg = 0;
        this.saldo = 0;
        this.saldoKg = 0;
        this.obs = "default";
        this.codigoProducto = "default";
    }
    
    public ItemProductivo(Long id, String nombreCientifico, String nombreVulgar, String clase, String unidad, Long idEspecieTax, float kilosXUnidad, 
            float total, float totalKg, float saldo, float saldoKg, String obs, String codigoProducto){
        this.id = Long.valueOf(0);
        this.nombreCientifico = "default";
        this.nombreVulgar = "default";
        this.clase = "default";
        this.unidad = "default";
        this.idEspecieTax = Long.valueOf(0);
        this.kilosXUnidad = 0;
        this.total = 0;
        this.totalKg = 0;
        this.saldo = 0;
        this.saldoKg = 0;
        this.obs = "default";
        this.codigoProducto = "default";
    }    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreCientifico() {
        return nombreCientifico;
    }

    public void setNombreCientifico(String nombreCientifico) {
        this.nombreCientifico = nombreCientifico;
    }

    public String getNombreVulgar() {
        return nombreVulgar;
    }

    public void setNombreVulgar(String nombreVulgar) {
        this.nombreVulgar = nombreVulgar;
    }

    public String getClase() {
        return clase;
    }

    public void setClase(String clase) {
        this.clase = clase;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public Long getIdEspecieTax() {
        return idEspecieTax;
    }

    public void setIdEspecieTax(Long idEspecieTax) {
        this.idEspecieTax = idEspecieTax;
    }

    public float getKilosXUnidad() {
        return kilosXUnidad;
    }

    public void setKilosXUnidad(float kilosXUnidad) {
        this.kilosXUnidad = kilosXUnidad;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public float getTotalKg() {
        return totalKg;
    }

    public void setTotalKg(float totalKg) {
        this.totalKg = totalKg;
    }

    public float getSaldo() {
        return saldo;
    }

    public void setSaldo(float saldo) {
        this.saldo = saldo;
    }

    public float getSaldoKg() {
        return saldoKg;
    }

    public void setSaldoKg(float saldoKg) {
        this.saldoKg = saldoKg;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public String getCodigoProducto() {
        return codigoProducto;
    }

    public void setCodigoProducto(String codigoProducto) {
        this.codigoProducto = codigoProducto;
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
        if (!(object instanceof ItemProductivo)) {
            return false;
        }
        ItemProductivo other = (ItemProductivo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuffer(" id: ").append(id).
                append(" nombreVulgar: ").append(nombreVulgar).
                append(" nombreCientifico: ").append(nombreCientifico).
                append(" clase: ").append(clase).
                append(" unidad: ").append(unidad).
                append(" idEspecieTax: ").append(idEspecieTax).
                append(" kilosXUnidad: ").append(kilosXUnidad).
                append(" total: ").append(total).
                append(" totalKg: ").append(totalKg).
                append(" saldo: ").append(saldo).
                append(" saldoKg: ").append(saldoKg).
                append(" obs: ").append(obs).
                append(" codigoProducto: ").append(codigoProducto).toString();
    }      
}
