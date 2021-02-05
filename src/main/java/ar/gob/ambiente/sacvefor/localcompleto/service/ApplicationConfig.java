
package ar.gob.ambiente.sacvefor.localcompleto.service;

import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author rincostante
 */
@javax.ws.rs.ApplicationPath("rest")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(ar.gob.ambiente.sacvefor.localcompleto.service.AutorizacionTrazFacadeREST.class);
        resources.add(ar.gob.ambiente.sacvefor.localcompleto.service.EntidadGuiaFacadeREST.class);
        resources.add(ar.gob.ambiente.sacvefor.localcompleto.service.EstadoGuiaFacadeREST.class);
        resources.add(ar.gob.ambiente.sacvefor.localcompleto.service.GiuaTrazFacadeREST.class);
        resources.add(ar.gob.ambiente.sacvefor.localcompleto.service.GuiaFacadeREST.class);
        resources.add(ar.gob.ambiente.sacvefor.localcompleto.service.GuiaSICMAFacadeREST.class);
        resources.add(ar.gob.ambiente.sacvefor.localcompleto.service.ItemProductivoFacadeREST.class);
        resources.add(ar.gob.ambiente.sacvefor.localcompleto.service.ItemProductivoTrazFacadeREST.class);
        resources.add(ar.gob.ambiente.sacvefor.localcompleto.service.LocalidadFacadeREST.class);
        resources.add(ar.gob.ambiente.sacvefor.localcompleto.service.PersonaFacadeREST.class);
        resources.add(ar.gob.ambiente.sacvefor.localcompleto.service.ProductoFacadeREST.class);
        resources.add(ar.gob.ambiente.sacvefor.localcompleto.service.ProductoTrazFacadeREST.class);
        resources.add(ar.gob.ambiente.sacvefor.localcompleto.service.RestSecurityFilter.class);
        resources.add(ar.gob.ambiente.sacvefor.localcompleto.service.TasasFacadeREST.class);
        resources.add(ar.gob.ambiente.sacvefor.localcompleto.service.UsuarioApiResource.class);
        resources.add(ar.gob.ambiente.sacvefor.localcompleto.service.VehiculoFacadeREST.class);
    }
    
}
