
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
    
    /**
     * Campos para leer archivo de configuracion
     */
    Properties properties;
    InputStream inputStream;
    
    private String provincia;
    private String tipoGuia;
    private String codGuia;
    private Date fechaEmisionVolante;
    private float totalALiquidar;
   
    private List<DetalleTasas> detalles;

    /**
     * Método que devuelve el total por Tasa
     * @param nombreTasa : nombre de la Tasa a calcular su total
     * @return 
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
    
    public LiqTotalTasas(){
        properties = new Properties();
    }
    /**
     * Método que devuelve el total a pagar por tasas para todos los Items de la Guía
     * @return 
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
     * @return 
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
