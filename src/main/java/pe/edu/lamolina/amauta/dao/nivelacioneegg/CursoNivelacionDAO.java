package pe.edu.lamolina.amauta.dao.nivelacioneegg;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.CursoCicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.horario.GrupoHorasNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.CursoNivelacion;

public interface CursoNivelacionDAO extends EasyDAO<CursoNivelacion> {

    List<CursoNivelacion> allByDynatable(DynatableFilter filter, CicloAcademico ciclo);

    List<CursoNivelacion> allByCursoCiclo(CursoCicloAcademico cursoCiclo, GrupoHorasNivelacion grupoHoras);

    List<CursoNivelacion> allByDocenteCiclo(Docente docente, CicloAcademico ciclo);

    List<CursoNivelacion> allActivosByCiclo(CicloAcademico ciclo);

    CursoNivelacion findLastByCiclo(CicloAcademico ciclo);

}
