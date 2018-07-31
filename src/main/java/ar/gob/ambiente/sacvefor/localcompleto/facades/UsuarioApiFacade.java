
package ar.gob.ambiente.sacvefor.localcompleto.facades;

import ar.gob.ambiente.sacvefor.localcompleto.entities.UsuarioApi;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.Query;

/**
 *
 * @author rincostante
 */
@Stateless
public class UsuarioApiFacade extends AbstractFacade<UsuarioApi> {

    /**
     * Cosntructor
     */
    public UsuarioApiFacade() {
        super(UsuarioApi.class);
    }
    
    /**
     * Método para obtener un UsuarioApi según su nombre, si existe
     * @param nombre String nombre del Usuario
     * @return UsuarioApi el UsuarioApi buscado o null, en caso de no existir
     */    
    public UsuarioApi getExistente(String nombre) {
        List<UsuarioApi> lstUsuarios;
        em = getEntityManager();
        
        String queryString = "SELECT usApi FROM UsuarioApi usApi "
                + "WHERE usApi.nombre = :nombre";
        Query q = em.createQuery(queryString)
                .setParameter("nombre", nombre);
        lstUsuarios = q.getResultList();
        if(lstUsuarios.isEmpty()){
            return null;
        }else{
            return lstUsuarios.get(0);
        }
    }

    /**
     * Método que valida que el usuarioApi recibido está registrado como usuario de la API
     * @param nombre String Nombre del usuarioApi recibido, enviado por le cliente.
     * @return boolean Verdadero o falso según el caso
     */
    public boolean validarUsuarioApi(String nombre){
        em = getEntityManager();
        String queryString = "SELECT usApi FROM UsuarioApi usApi "
                + "WHERE usApi.nombre = :nombre "
                + "AND (usApi.rol.nombre = 'CLIENTE_CTRL' OR usApi.rol.nombre = 'CLIENTE_TRAZ')";
        Query q = em.createQuery(queryString)
                .setParameter("nombre", nombre);
        return !q.getResultList().isEmpty();
    }      
}
