
package ar.gob.ambiente.sacvefor.localcompleto.facades;

import ar.gob.ambiente.sacvefor.localcompleto.entities.Vehiculo;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.Query;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;

/**
 * Acceso a datos para la entidad Vehículo
 * @author rincostante
 */
@Stateless
public class VehiculoFacade extends AbstractFacade<Vehiculo> {

    /**
     * Constructor
     */
    public VehiculoFacade() {
        super(Vehiculo.class);
    }
    
    /**
     * Metodo para validar un Vehículo existente según su matrícula
     * @param matricula String matrícula del vehículo a validar
     * @return Vehiculo vehículo existente
     */
    public Vehiculo getExistente(String matricula) {
        List<Vehiculo> lstPersonas;
        String queryString = "SELECT veh FROM Vehiculo veh "
                + "WHERE veh.matricula = :matricula";
        Query q = em.createQuery(queryString)
                .setParameter("matricula", matricula);
        lstPersonas = q.getResultList();
        if(lstPersonas.isEmpty()){
            return null;
        }else{
            return lstPersonas.get(0);
        }
    }    
    
    /**
     * Método sobreescrito que lista las Vehiculos ordenados por su Maraca
     * @return List<Vehiculo> listado de todos los vehículos ordenados por marca
     */
    @Override
    public List<Vehiculo> findAll(){
        String queryString = "SELECT veh FROM Vehiculo veh "
                + "ORDER BY veh.marca";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }     
    
    /**
     * Método que devuelve un listado de Vehículos habilitados cuya marca contenga la cadena ingresada como parámetro
     * @param param String cadena a buscar entre las marcas
     * @return List<Vehiculo> listado de los vehículos correspondiente
     */
    public List<Vehiculo> findByMarca(String param){
        String queryString = "SELECT veh FROM Vehiculo veh "
                + "WHERE LOWER(veh.marca.nombre) LIKE :param "
                + "AND veh.habilitado = true"
                + "ORDER BY veh.marca";
        Query q = em.createQuery(queryString)
                .setParameter("param", "%" + param + "%".toLowerCase());
        return q.getResultList();
    }
    
    /**
     * Método que devuelve los Vehículos habilitados ordenados por marca
     * Sin distinción del tipo.
     * @return List<Vehiculo> listado de todos los vehículos habilitados 
     */
    public List<Vehiculo> getHabilitadas(){
        List<Vehiculo> lstVehiculos;
        String queryString = "SELECT veh FROM Vehiculo veh "
                + "WHERE veh.habilitado = true "
                + "ORDER BY veh.marca";
        Query q = em.createQuery(queryString);
        lstVehiculos = q.getResultList();
        return lstVehiculos;
    } 
   
    /**
     * Método para obtener todas las revisiones de la entidad
     * @param matricula String matrícula del vehículo
     * @return List<Vehiculo> listado de revisiones del vehículo para su auditoría
     */
    public List<Vehiculo> findRevisions(String matricula){
        List<Vehiculo> lstClientes = new ArrayList<>();
        Vehiculo veh = this.getExistente(matricula);
        if(veh != null){
            Long id = this.getExistente(matricula).getId();
            AuditReader reader = AuditReaderFactory.get(getEntityManager());
            List<Number> revisions = reader.getRevisions(Vehiculo.class, id);
            for (Number n : revisions) {
                Vehiculo cli = reader.find(Vehiculo.class, id, n);
                cli.setFechaRevision(reader.getRevisionDate(n));
                lstClientes.add(cli);
            }
        }
        return lstClientes;
    }       
}
