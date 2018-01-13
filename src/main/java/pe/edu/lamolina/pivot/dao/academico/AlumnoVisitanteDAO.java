package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.AlumnoVisitante;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.general.Persona;

public interface AlumnoVisitanteDAO extends EasyDAO<AlumnoVisitante> {

    AlumnoVisitante findByPersona(Persona persona);

    AlumnoVisitante findByPersona(Persona persona, CicloAcademico cicloAcademico);

    public List<AlumnoVisitante> allByDynatable(DynatableFilter filter);

}
