
package ar.gob.ambiente.sacvefor.localcompleto.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Objeto utilitario para encaspular las diferentes tasas que pueden liquidarse de cada Item
 * @author rincostante
 */
public class DetalleTasas implements Serializable{
    
    /**
     * Nombre vulgar del prducto proveniente de la Entidad Producto, encapsulado en el Item
     */
    private String nombreProd;
    
    /**
     * Clase del prducto proveniente de la Entidad Producto, encapsulado en el Item
     */
    private String clase;    
    
    /**
     * Unidad de medida del Producto, obtenida del Item
     */
    private String unidad;
    
    /**
     * Cantidad de Producto, obtenida del Item
     */
    private float cantidad;
    
    /**
     * Total a liquidar por todas las tasas configuradas
     */
    private float total;
    
    /**
     * Listado con los pares nombre-valor para cada tasa
     */
    private List<TasaModel> tasas;

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

    public float getCantidad() {
        return cantidad;
    }

    public void setCantidad(float cantidad) {
        this.cantidad = cantidad;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public String getNombreProd() {
        return nombreProd;
    }

    public void setNombreProd(String nombreProd) {
        this.nombreProd = nombreProd;
    }

    public List<TasaModel> getTasas() {
        return tasas;
    }

    public void setTasas(List<TasaModel> tasas) {
        this.tasas = tasas;
    }
    
    public DetalleTasas(){
        this.tasas = new ArrayList<>();
    }
    
    /**
     * Clase estática para cada tasa
     */
    static public class TasaModel implements Serializable {
 
        private String header;
        private float property;
        private float subTotal;
 
        public TasaModel(String header, float property, float subTotal) {
            this.header = header;
            this.property = property;
            this.subTotal = subTotal;
        }

        public float getSubTotal() {
            return subTotal;
        }
 
        public String getHeader() {
            return header;
        }
 
        public float getProperty() {
            return property;
        }
    }    
}
