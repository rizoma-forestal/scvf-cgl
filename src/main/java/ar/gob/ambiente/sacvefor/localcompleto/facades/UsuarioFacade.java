
package ar.gob.ambiente.sacvefor.localcompleto.facades;

import ar.gob.ambiente.sacvefor.localcompleto.entities.Usuario;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.Query;

/**
 * Acceso a datos para la entidad Usuario
 * @author rincostante
 */
@Stateless
public class UsuarioFacade extends AbstractFacade<Usuario> {

    /**
     * Constructor
     */
    public UsuarioFacade() {
        super(Usuario.class);
    }
    
    /**
     * Método para validar la existencia de un Usuario según su DNI, utilizado como login
     * @param login Long DNI del Usuario
     * @return Usuario usuario existente
     */
    public Usuario getExistente(Long login) {
        List<Usuario> lstUsuarios;
        String queryString = "SELECT us FROM Usuario us "
                + "WHERE us.login = :login";
        Query q = em.createQuery(queryString)
                .setParameter("login", login);
        lstUsuarios = q.getResultList();
        if(lstUsuarios.isEmpty()){
            return null;
        }else{
            return lstUsuarios.get(0);
        }
    }    
    
    /**
     * Mátodo que solo devuelve los Usuarios habilitados.
     * Para poblar combos de selección.
     * @return List<Usuario> listado de los usuarios habilitados
     */
    public List<Usuario> getHabilitados(){
        String queryString = "SELECT us FROM Usuario us "
                + "WHERE us.habilitado = true";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }     

    /**
     * Método para validar la existencia de un Usuario con las credenicales recibidas
     * @param login Long DNI del Usuario
     * @param clave String clave encriptada
     * @return Usuario validado
     */
    public Usuario validar(Long login, String clave){
        List<Usuario> lUs;
        String queryString = "SELECT us FROM Usuario us "
                + "WHERE us.login = :login "
                + "AND us.clave = :clave";
        Query q = em.createQuery(queryString)
                .setParameter("login", login)
                .setParameter("clave", clave);
        lUs = q.getResultList();
        if(lUs.isEmpty()){
            return null;
        }else{
            return lUs.get(0);
        }
    }    
    
    /**
     * Método para obtener Usuario para un rol
     * Solo se usa para roles de un solo usuario, como el caso de cliente-traz
     * A utilizar para el registro de productos desde TRAZ
     * @param id_rol Long id del rol
     * @return Usuario solicitado
     */
    public Usuario getByRol(Long id_rol){
        List<Usuario> lstUsuarios;
        String queryString = "SELECT us FROM Usuario us "
                + "WHERE us.rol.id = :id_rol";
        Query q = em.createQuery(queryString)
                .setParameter("id_rol", id_rol);
        lstUsuarios = q.getResultList();
        if(lstUsuarios.isEmpty()){
            return null;
        }else{
            return lstUsuarios.get(0);
        }
    }
}
