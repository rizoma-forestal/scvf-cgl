
package ar.gob.ambiente.sacvefor.localcompleto.util;

import ar.gob.ambiente.sacvefor.localcompleto.util.DetalleTasas.TasaModel;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Objeto utilitario que encapsula la liquidación total de tasas para una Guía
 * @author rincostante
 */
public class LiqTotalTasas implements Serializable{
    
    ///////////////////////////////////////////////
    // Campos para leer archivo de configuracion //
    ///////////////////////////////////////////////
    
    /**
     * Variable privada: objeto para gestionar el archivo de propuedades
     */
    Properties properties;
    
    /**
     * Variable privada: objeto para obtener el flujo con el archivo de propiedades
     */
    InputStream inputStream;
    
    /**
     * Variable privada: guarda el nombre de la provincia correspondiente al componente local
     */
    private String provincia;
    
    /**
     * Variable privada: guarda el tipo de guía gestionada
     */
    private String tipoGuia;
    
    /**
     * Variable privada: guarda el código único de la guía
     */
    private String codGuia;
    
    /**
     * Variable privada: fecha de emisión del volante con las tasas
     */
    private Date fechaEmisionVolante;
    
    /**
     * Variable privada: total a abonar por el titular de la guía en concepto
     * de las tasas que correspondan a la totalidad de los productos
     */
    private float totalALiquidar;
    
    /**
     * Variable privada: listado de los detalles de tasas por cada producto incluído en la guía
     */
    private List<DetalleTasas> detalles;

    /**
     * Método que devuelve el total por Tasa
     * @param nombreTasa String nombre de la Tasa a calcular su total
     * @return float total a abonar para la tasa recibida
     */    
    public float getTotalByTasa(String nombreTasa) {
        float result = 0;
        for(DetalleTasas detTasas : detalles){
            for(TasaModel tm : detTasas.getTasas()){
                if(tm.getHeader().equals(nombreTasa)){
                    result += tm.getProperty() * detTasas.getCantidad();
                }
            }
        }
        return result;
    }
    
    /**
     * Constructor que instacia el objeto properties
     */
    public LiqTotalTasas(){
        properties = new Properties();
    }
    /**
     * Método que devuelve el total a pagar por tasas para todos los Items de la Guía
     * @return float monto correspondiente
     */
    public float getTotalALiquidar(){
        float result = 0;
        for(DetalleTasas detTasas : detalles){
            result += detTasas.getTotal();
        }        
        totalALiquidar = result;
        return totalALiquidar;
    }    

    /**
     * Método que retorna la Provincia configurada en el archivo de propiedades
     * @return String nombre de la provincia
     */
    public String getProvincia() {
        // instancio el stream
        inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("Config.properties");
        try{
            // leo las propiedades
            properties.load(inputStream);
            provincia = properties.getProperty("Provincia");
            
            // cierro el stream
            inputStream.close(); 
            
        }catch(IOException ex){
            Logger.getLogger(LiqTotalTasas.class.getName()).log(Level.SEVERE, null, ex);
        }
    
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getTipoGuia() {
        return tipoGuia;
    }

    public void setTipoGuia(String tipoGuia) {
        this.tipoGuia = tipoGuia;
    }

    public String getCodGuia() {
        return codGuia;
    }

    public void setCodGuia(String codGuia) {
        this.codGuia = codGuia;
    }

    public Date getFechaEmisionVolante() {
        return fechaEmisionVolante;
    }

    public void setFechaEmisionVolante(Date fechaEmisionVolante) {
        this.fechaEmisionVolante = fechaEmisionVolante;
    }

    public List<DetalleTasas> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleTasas> detalles) {
        this.detalles = detalles;
    }
    
}
