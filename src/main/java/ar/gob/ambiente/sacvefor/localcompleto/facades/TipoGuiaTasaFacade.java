/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.gob.ambiente.sacvefor.localcompleto.facades;

import ar.gob.ambiente.sacvefor.localcompleto.entities.TipoGuiaTasa;
import javax.ejb.Stateless;

/**
 *
 * @author rincostante
 */
@Stateless
public class TipoGuiaTasaFacade extends AbstractFacade<TipoGuiaTasa> {

    public TipoGuiaTasaFacade() {
        super(TipoGuiaTasa.class);
    }
    
}
