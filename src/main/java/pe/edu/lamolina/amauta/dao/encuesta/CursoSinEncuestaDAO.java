package pe.edu.lamolina.amauta.dao.encuesta;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.encuestaestudiantil.CursoSinEncuesta;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaEstudiantil;

public interface CursoSinEncuestaDAO extends EasyDAO<CursoSinEncuesta> {

    CursoSinEncuesta findByEncuestaEstudiantilCurso(EncuestaEstudiantil encuestaEstudiantil, Curso curso);

    List<CursoSinEncuesta> allByEncuestaEstudiantil(EncuestaEstudiantil encuestaEstudiantil);

    void deleteByEncuestaEstudiantil(EncuestaEstudiantil encuesta);

}
