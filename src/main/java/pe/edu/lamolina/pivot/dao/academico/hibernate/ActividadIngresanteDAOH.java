package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.ActividadIngresante;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.RecorridoIngresante;
import pe.edu.lamolina.model.enums.ActividadIngresanteEnum;
import pe.edu.lamolina.pivot.dao.academico.ActividadIngresanteDAO;

@Repository
public class ActividadIngresanteDAOH extends AbstractEasyDAO<ActividadIngresante> implements ActividadIngresanteDAO {

    public ActividadIngresanteDAOH() {
        super();
        setClazz(ActividadIngresante.class);
    }

    @Override
    public List<ActividadIngresante> allByRecorridoIngresantes(List<RecorridoIngresante> recorridoIngresantes) {
        Octavia sql = Octavia.query(ActividadIngresante.class, "ai")
                .join("recorridoIngresante ri", "tipoActividadIngresante tai")
                .in("ri.id", recorridoIngresantes);
        return all(sql);
    }

    @Override
    public List<ActividadIngresante> allByCicloAcademico(CicloAcademico cicloAcademico) {
       Octavia sql = Octavia.query(ActividadIngresante.class, "ai")
                .join("recorridoIngresante ri", "tipoActividadIngresante tai" , "ri.cicloAcademico ca")
                .filter("estado", ActividadIngresanteEnum.COMP)
                .filter("ca.id", cicloAcademico);
        return all(sql);
    }

}
