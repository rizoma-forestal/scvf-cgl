
package ar.gob.ambiente.sacvefor.localcompleto.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

/**
 * Clase utilitaria para gestionar el token recibido para autenticarse ante una API rest
 * Contiene un String con el token y un long con el tiempo en milisegundos de su obtención
 * @author rincostante
 */
public class Token {
    /**
     * Variable privada que contiene el token recibido
     */
    private String strToken;
    
    /**
     * Variable privada que contiene el tiempo en milisegundos de la obtención del token
     */
    private Long mlsTiempo;
    
    /**
     * Variable privada para gestionar el archivo de propiedades
     */
    private Properties properties;
    
    /**
     * Variable privada para obtener el archivo de propiedades
     */
    private InputStream inputStream;

    /**
     * Constructor
     * @param strToken String token recibido desde la API rest
     * @param mlsTiempo Long tiempo en milisegundos del momento de la obtención
     */
    public Token(String strToken, Long mlsTiempo){
        this.properties = new Properties();
        this.strToken = strToken;
        this.mlsTiempo = mlsTiempo;
    }
    
    public String getStrToken() {
        return strToken;
    }

    public void setStrToken(String strToken) {
        this.strToken = strToken;
    }

    public Long getMlsTiempo() {
        return mlsTiempo;
    }

    public void setMlsTiempo(Long mlsTiempo) {
        this.mlsTiempo = mlsTiempo;
    }
    
    /**
     * Método que retorna true si el token está vigente y false si se venció
     * @return boolean true si el token está vigente y false si no
     * @throws java.io.IOException
     */
    public boolean isVigente() throws IOException{
        Long mlsVigencia, mlsDiferencia, limite;
        // instancio el stream
        inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("Config.properties");
        
        // leo las propiedades
        properties.load(inputStream);
        // seteo la vigencia
        mlsVigencia = Long.valueOf(properties.getProperty("TimeToken"));
        // obtengo la diferencia entre el momento de obtención del token y la hora actual
        mlsDiferencia = (Math.abs(mlsTiempo - System.currentTimeMillis())) / 1000;
        // seteo el límite de tiempo 
        limite = mlsVigencia / 1000L;
        // retorno lo que corresponda
        return mlsDiferencia <= limite;
    }
}
