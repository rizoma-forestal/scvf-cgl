
package ar.gob.ambiente.sacvefor.localcompleto.mb;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

/**
 * Bean de gestión de la vista infoGral.xhtml para la edición del texto de información general a ver en la portada
 * @author rincostante
 */
public class MbInfoGral {

    /**
     * Variable privada: Campo para leer archivo de configuracion
     */
    private Properties properties;
    
    /**
     * Variable privada: Flujo para obtener el archivo de propiedades
     */
    private InputStream inputStream;  
    
    /**
     * Variable privada: cadena que contendrá el contenido de la propiedad InfoGral del Config.properties
     */
    private String info;
    
    /**
     * Variable privada: guarda el archivo .properties para leer y editar la información general a mostrar en el index.
     */
    private File archivo;
    
    /**
     * Variable privada: Logger para escribir en el log del server
     */ 
    static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MbInfoGral.class);    
    
    /**
     * Constructor
     */
    public MbInfoGral() {
    }
   
    ///////////////////////
    // Métodos de acceso //
    ///////////////////////
    
    public String getInfo(){
        return info;
    }
    
    public void setInfo(String info) {    
        this.info = info;
    }

    /**
     * Método que se ejecuta luego de instanciada la clase y setea las variables para el acceso al archivo de propiedades y
     * llama al método cargarProperties(). Solo si el inputstream no está seteado ya
     */
    @PostConstruct
    public void init() {
        // instancio el stream y seteo el archivo
        if(inputStream == null){
            try{
                inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("Config.properties");
                java.net.URL urlProp = Thread.currentThread().getContextClassLoader().getResource("Config.properties");
                archivo = new File(urlProp.getFile());
                properties = new Properties();
                // leo las propiedades
                properties.load(inputStream);
                cargarProperties();
            }catch(IOException ex){
                LOG.fatal("Hubo un error al acceder al archivo Config.properties. " + ex.getMessage());
            }
        }
    }
    
    /**
     * Método para guardar la información editada por el usuario
     */
    public void save() {
        ExternalContext contextoExterno = FacesContext.getCurrentInstance().getExternalContext();
        try{
            // actualizo la propiedad
            properties.setProperty("InfoGral", info);
            FileOutputStream fos = new FileOutputStream(archivo);
            properties.store(fos, null);
            // cierro el stream
            inputStream.close(); 
            // redirecciono al index
            String ctxPath = ((ServletContext) contextoExterno.getContext()).getContextPath();
            contextoExterno.redirect(ctxPath + "/faces/index.xhtml");            
        }catch(IOException ex){
            LOG.fatal("Hubo un error al editar el archivo Config.properties. " + ex.getMessage());
        }
    }
    
    /**
     * Método para volver al contenido previo a la edición
     */
    public void cancelar(){
        cargarProperties();
    }
    
    //////////////////////
    // métodos privados //
    //////////////////////
    /**
     * Método para setear el campo de texto con la información del properties.
     * Si hubo un error de acceso lo escribe en el log
     * Utilizado en el itit() y cancelar()
     */
    private void cargarProperties(){
        try{
            info = properties.getProperty("InfoGral");
            
            // cierro el stream
            inputStream.close(); 
            
        }catch(IOException ex){
            LOG.fatal("Hubo un error al leer las propiedad InfoGral del Config.properties. " + ex.getMessage());
        }  
    }
}
