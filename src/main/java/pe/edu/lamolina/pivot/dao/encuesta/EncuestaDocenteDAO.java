package pe.edu.lamolina.pivot.dao.encuesta;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaDocente;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaEstudiantil;

public interface EncuestaDocenteDAO extends EasyDAO<EncuestaDocente> {

    List<EncuestaDocente> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico);

    List<EncuestaDocente> allByEncuestaEstudiantil(EncuestaEstudiantil encuestaEstudiantil);

    public List<EncuestaDocente> allAnuladaByModalidadEstudioDocenteCicloAcademico(ModalidadEstudio modalidadEstudio, Docente docente, CicloAcademico cicloAcademico);

    public List<EncuestaDocente> allAnuladaByModalidadEstudioCicloAcademico(ModalidadEstudio modalidadEstudio, CicloAcademico cicloAcademico);

    public EncuestaDocente findEncuestaDocente(EncuestaDocente encuestaForm);

    void deleteByEncuestaEstudiantil(EncuestaEstudiantil encuesta);

}
