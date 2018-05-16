
package ar.gob.ambiente.sacvefor.servicios.cgl;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Clase para manipular los Vehículos registrados localmente
 * @author rincostante
 */
@XmlRootElement
public class Vehiculo implements Serializable {

    private Long id;
    private String matricula;    
    /**
     * Id del Vehículo registrado en el RUE
     */
    private Long idRue;
    private String marca;
    private String modelo;
    private int anio; 
    /**
     * Persona vinculada al Vehículo como Titular
     */
    private Persona titular;
    
    /******************
     * Constructores **
     ******************/
    public Vehiculo(){
        this.id = Long.valueOf(0);
        this.matricula = "default";
        this.idRue = Long.valueOf(0);
        this.marca = "default";
        this.modelo = "default";
        this.anio = 0;
        this.titular = new Persona();
    }
    
    public Vehiculo(Long id, String matricula, Long idRue, String marca, String modelo, int anio, Persona titular){
        this.id = id;
        this.matricula = matricula;
        this.idRue = idRue;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = 0;
        this.titular = titular;
    }    
    


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public Long getIdRue() {
        return idRue;
    }

    public void setIdRue(Long idRue) {
        this.idRue = idRue;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public Persona getTitular() {
        return titular;
    }

    /**********************
     * Métodos de acceso **
     **********************/
    public void setTitular(Persona titular) {
        this.titular = titular;
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
        if (!(object instanceof Vehiculo)) {
            return false;
        }
        Vehiculo other = (Vehiculo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuffer(" id: ").append(id).
                append(" matricula: ").append(matricula).
                append(" idRue: ").append(idRue).
                append(" marca: ").append(marca).
                append(" modelo: ").append(modelo).
                append(" anio: ").append(anio).
                append(" titular: ").append(titular.getNombreCompleto()).toString();
    }
}
