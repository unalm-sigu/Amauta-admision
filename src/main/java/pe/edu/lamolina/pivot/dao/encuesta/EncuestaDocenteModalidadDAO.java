package pe.edu.lamolina.pivot.dao.encuesta;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaDocenteModalidad;

public interface EncuestaDocenteModalidadDAO extends EasyDAO<EncuestaDocenteModalidad> {

    List<EncuestaDocenteModalidad> allByCiclo(CicloAcademico cicloAcademico);

    List<EncuestaDocenteModalidad> allConEncuestadosByCiclo(CicloAcademico cicloAcademico);

    List<EncuestaDocenteModalidad> allByDynatableCicloAcademico(DynatableFilter filter, CicloAcademico ciclo);

}
