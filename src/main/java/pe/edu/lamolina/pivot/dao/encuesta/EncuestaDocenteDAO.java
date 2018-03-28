package pe.edu.lamolina.pivot.dao.encuesta;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.encuesta.EncuestaDocente;
import pe.edu.lamolina.model.encuesta.EncuestaEstudiantil;

public interface EncuestaDocenteDAO extends EasyDAO<EncuestaDocente> {

    public List<EncuestaDocente> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico);

    public List<EncuestaDocente> allByEncuestaEstudiantil(EncuestaEstudiantil encuestaEstudiantil);

    public EncuestaDocente findEncuestaDocente(EncuestaDocente encuestaForm);

}
