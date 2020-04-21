package pe.edu.lamolina.amauta.dao.encuesta;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaCurso;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaDocente;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaEstudiantil;

public interface EncuestaCursoDAO extends EasyDAO<EncuestaCurso> {

    List<EncuestaCurso> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico, boolean noEsSimultaneo);

    List<EncuestaCurso> allByEncuestaEstudiantil(EncuestaEstudiantil encuestaEstudiantil, boolean esSimultaneo);

    EncuestaCurso findByEncuestaCurso(EncuestaCurso encuestaForm);

    EncuestaCurso findByEncuestaDocente(EncuestaDocente encuestaDocente);

    void deleteByEncuestaTipoCurso(EncuestaEstudiantil encuesta);

}
