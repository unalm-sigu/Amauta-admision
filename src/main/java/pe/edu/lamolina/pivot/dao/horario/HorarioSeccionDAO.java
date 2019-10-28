package pe.edu.lamolina.pivot.dao.horario;

import java.util.List;
import java.util.Map;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.OficinaEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioSeccion;

public interface HorarioSeccionDAO extends EasyDAO<HorarioSeccion> {

    List<HorarioSeccion> allBySecciones(List<Seccion> secciones);

    List<HorarioSeccion> allBySeccionesSortByDiaHora(List<Seccion> secciones);

    List<HorarioSeccion> allByCicloCurso(CicloAcademico cicloAcademico, List<Curso> cursos);

    List<HorarioSeccion> allBySeccion(Seccion seccion);

    List<HorarioSeccion> allBySeccionDia(Seccion seccion, Dia dia);

    HorarioSeccion findBySeccionDiaHora(Seccion seccion, Dia dia, Hora hora);

    void deleteAllByNotInList(List<HorarioSeccion> horarios);

    void deleteAllInList(List<HorarioSeccion> horarios);

    List<HorarioSeccion> allByCiclo(CicloAcademico cicloAcademico);

    void deleteAllByCiclo(CicloAcademico ciclo);

    Map<Long, Long> allSeccionDayWithQtyHours(List<Seccion> secciones, Integer horasForDay);

    List<HorarioSeccion> allByGrupoSeccion(GrupoSeccion grupoSeccion);

    List<HorarioSeccion> allByCicloOrderByDiaHora(CicloAcademico cicloAcademico);

    List<HorarioSeccion> allByAulaCiclo(Aula aula, OficinaEnum oficinaEnum, CicloAcademico cicloAcademico);

}
