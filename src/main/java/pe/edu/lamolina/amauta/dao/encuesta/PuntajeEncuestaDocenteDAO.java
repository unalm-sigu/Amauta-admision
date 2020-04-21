package pe.edu.lamolina.amauta.dao.encuesta;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaDocente;
import pe.edu.lamolina.model.encuestaestudiantil.PuntajeEncuestaDocente;

public interface PuntajeEncuestaDocenteDAO extends EasyDAO<PuntajeEncuestaDocente> {

    List<PuntajeEncuestaDocente> allByCicloAcademico(CicloAcademico cicloAcademico);

    List<PuntajeEncuestaDocente> allByDocenteModalidadCicloAcademico(Docente docente, ModalidadEstudio modalidadEstudio, CicloAcademico cicloAcademico);

    List<PuntajeEncuestaDocente> allByEncuestaDocente(EncuestaDocente encuestaDocente);

}
