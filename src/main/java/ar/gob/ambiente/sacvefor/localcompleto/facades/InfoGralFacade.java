
package ar.gob.ambiente.sacvefor.localcompleto.facades;

import ar.gob.ambiente.sacvefor.localcompleto.entities.InfoGral;
import javax.ejb.Stateless;

/**
 * Acceso a datos para la entidad InfoGral
 * @author rincostante
 */
@Stateless
public class InfoGralFacade extends AbstractFacade<InfoGral> {

    /**
     * Constructor
     */
    public InfoGralFacade() {
        super(InfoGral.class);
    }
    
}
