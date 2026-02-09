package pe.edu.lamolina.amauta.dao.nivelacioneegg;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.CursoCicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.horario.PlantillaNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.CursoNivelacion;

public interface CursoNivelacionDAO extends EasyDAO<CursoNivelacion> {

    List<CursoNivelacion> allByDynatable(DynatableFilter filter, CicloAcademico ciclo);

    List<CursoNivelacion> allDocenteByDynatable(DynatableFilter filter, CicloAcademico ciclo, Docente docente);

    List<CursoNivelacion> allByCursoCicloPlantilla(CursoCicloAcademico cursoCiclo, PlantillaNivelacion plantilla);

    List<CursoNivelacion> allByCicloPlantilla(CicloAcademico ciclo, PlantillaNivelacion plantilla);

    List<CursoNivelacion> allByDocenteCiclo(Docente docente, CicloAcademico ciclo);

    List<CursoNivelacion> allActivosByCiclo(CicloAcademico ciclo);

    List<CursoNivelacion> allByCiclo(CicloAcademico ciclo);

    CursoNivelacion findLastByCiclo(CicloAcademico ciclo);

}
