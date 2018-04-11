package pe.edu.lamolina.pivot.dao.encuesta.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.pivot.dao.encuesta.ExamenVirtualInteresadoDAO;
import pe.edu.lamolina.model.examen.ExamenVirtualInteresado;
import pe.edu.lamolina.model.inscripcion.CicloPostula;

@Repository
public class ExamenVirtualInteresadoDAOH extends AbstractEasyDAO<ExamenVirtualInteresado> implements ExamenVirtualInteresadoDAO {

    public ExamenVirtualInteresadoDAOH() {
        super();
        setClazz(ExamenVirtualInteresado.class);
    }

    @Override
    public ExamenVirtualInteresado findByCiclo(CicloPostula ciclo) {
        Octavia sql = Octavia.query()
                .from(ExamenVirtualInteresado.class, "evi")
                .join("cicloPostula cp", "examenVirtual")
                .filter("cp.id", ciclo.getId());
        return (ExamenVirtualInteresado) sql.find(getCurrentSession());
    }

}
