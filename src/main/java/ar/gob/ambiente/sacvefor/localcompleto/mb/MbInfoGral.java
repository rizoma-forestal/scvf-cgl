
package ar.gob.ambiente.sacvefor.localcompleto.mb;

import ar.gob.ambiente.sacvefor.localcompleto.entities.InfoGral;
import ar.gob.ambiente.sacvefor.localcompleto.facades.InfoGralFacade;
import java.io.IOException;
import javax.ejb.EJB;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;


/**
 * Bean de gestión de la vista infoGral.xhtml para la edición del texto de información general a ver en la portada
 * @author rincostante
 */
public class MbInfoGral {
    
    /**
     * Variable privada: cadena que contendrá la información general guardada en la base
     */
    private String info;
    
    /**
     * Variable privada: objeto que encapsula la información general
     */
    private InfoGral infoGral;
    
    /**
     * Variable privada: Logger para escribir en el log del server
     */ 
    static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MbInfoGral.class);    
    
    ///////////////////////////////////////////////////
    // acceso a datos mediante inyección de recursos //
    ///////////////////////////////////////////////////
    /**
     * Variable privada: EJB inyectado para el acceso a datos de ZonaIntervencion
     */  
    @EJB
    private InfoGralFacade infoFacade;
    
    /**
     * Constructor
     */
    public MbInfoGral() {
    }
   
    ///////////////////////
    // Métodos de acceso //
    ///////////////////////
    public InfoGral getInfoGral(){
        return infoGral;
    }

    public void setInfoGral(InfoGral infoGral) {
        this.infoGral = infoGral;
    }

    public String getInfo() {
        try{
            infoGral = infoFacade.find(Long.valueOf(1));
            info = infoGral.getInfo();
        }catch(Exception ex){
            LOG.fatal("Hubo un error al leer la información general. " + ex.getMessage());
        }
        return info;
    }
    
    public void setInfo(String info) {    
        this.info = info;
    }
    
    /**
     * Método para guardar la información general
     */
    public void save(){
        ExternalContext contextoExterno = FacesContext.getCurrentInstance().getExternalContext();
        try{
            infoGral.setInfo(info);
            infoFacade.edit(infoGral);
            String ctxPath = ((ServletContext) contextoExterno.getContext()).getContextPath();
            contextoExterno.redirect(ctxPath + "/");
        }catch (IOException ex) {
            LOG.fatal("Hubo un error al guardar la información general. " + ex.getMessage());
        }
    }
}
