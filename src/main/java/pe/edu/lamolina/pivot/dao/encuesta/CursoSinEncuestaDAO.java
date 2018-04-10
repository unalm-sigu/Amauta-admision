package pe.edu.lamolina.pivot.dao.encuesta;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.encuestaestudiantil.CursoSinEncuesta;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaEstudiantil;

public interface CursoSinEncuestaDAO extends EasyDAO<CursoSinEncuesta> {

    public CursoSinEncuesta findByEncuestaEstudiantilCurso(EncuestaEstudiantil encuestaEstudiantil, Curso curso);

    public List<CursoSinEncuesta> allByEncuestaEstudiantil(EncuestaEstudiantil encuestaEstudiantil);

}
