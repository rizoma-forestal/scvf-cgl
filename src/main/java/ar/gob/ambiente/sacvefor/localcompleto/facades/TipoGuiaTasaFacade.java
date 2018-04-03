
package ar.gob.ambiente.sacvefor.localcompleto.facades;

import ar.gob.ambiente.sacvefor.localcompleto.entities.TipoGuiaTasa;
import javax.ejb.Stateless;

/**
 * Acceso a datos para la entidad TipoGuiaTasa que gestiona las tasas de los tipos de guías
 * Utiliza los métodos de la clase abstracta
 * @author rincostante
 */
@Stateless
public class TipoGuiaTasaFacade extends AbstractFacade<TipoGuiaTasa> {

    /**
     * Constructor
     */
    public TipoGuiaTasaFacade() {
        super(TipoGuiaTasa.class);
    }
    
}
