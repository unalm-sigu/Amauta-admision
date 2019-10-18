package pe.edu.lamolina.pivot.dao.encuesta;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaCurso;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaEstudiantil;

public interface EncuestaCursoDAO extends EasyDAO<EncuestaCurso> {

    List<EncuestaCurso> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico);

    List<EncuestaCurso> allByEncuestaEstudiantil(EncuestaEstudiantil encuestaEstudiantil);

    EncuestaCurso findEncuestaCurso(EncuestaCurso encuestaForm);

    void deleteByEncuestaTipoCurso(EncuestaEstudiantil encuesta);

}
