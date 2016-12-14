package pe.edu.lamolina.pivot.dao.general.hibernate;

import java.util.List;
import org.hibernate.Query;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.general.PersonaDAO;
import pe.edu.lamolina.pivot.model.general.Persona;
import org.springframework.stereotype.Repository;

@Repository
public class PersonaDAOH extends AbstractDAO<Persona> implements PersonaDAO {

    public PersonaDAOH() {
        super();
        setClazz(Persona.class);
    }
    
    
      @Override
    public List<Persona> allByNombre(String nombre) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        StringBuilder sql = new StringBuilder();
        sql.append("  from ").append(Persona.class.getName()).append(" as per ");
        sql.append(" where concat( coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',per.nombres) like :BUSQUEDA or ");
        sql.append("       concat( per.nombres,' ',coalesce(per.paterno,''),' ',coalesce(per.materno,'')) like :BUSQUEDA or ");
        sql.append("       per.numeroDocIdentidad like :BUSQUEDA ");
        sql.append(" order by per.paterno, per.materno, per.nombres ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setString("BUSQUEDA", nombre);
        query.setMaxResults(15);

        return query.list();
    }
    
    
}

