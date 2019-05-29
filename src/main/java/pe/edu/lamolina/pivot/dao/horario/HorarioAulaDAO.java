package pe.edu.lamolina.pivot.dao.horario;

import java.util.Date;
import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.EventoCicloAcademico;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.bienestar.ReservaAula;
import pe.edu.lamolina.model.enums.EstadoHorarioAulaEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioAula;
import pe.edu.lamolina.model.rolexamen.CursoMasivoExamen;
import pe.edu.lamolina.model.rolexamen.LetraGrupoRegular;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoEspecial;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoRegular;
import pe.edu.lamolina.model.rolexamen.SemanaExamen;

public interface HorarioAulaDAO extends EasyDAO<HorarioAula> {

    List<HorarioAula> allHorarioAula();

    List<HorarioAula> allByAula(Aula aula, CicloAcademico cicloAcademico);

    List<HorarioAula> allBySeccionAula(Seccion seccion, Aula aula);

    List<HorarioAula> allBySeccionCiclo(Seccion seccion, CicloAcademico cicloAcademico);

    List<HorarioAula> allByPabellonCicloDiasHoras(Aula pabellon, EventoCicloAcademico eventoAcademico, List<String> hdias);

    List<HorarioAula> allByAulasCicloDiasHoras(List<Aula> aulas, EventoCicloAcademico eventoAcademico, List<String> hdias);

    List<HorarioAula> allByCambioAulasCiclo(List<Aula> aulas, CicloAcademico cicloAcademico);

    void deleteBySeccionAula(Seccion seccion, Aula aula);

    void deleteBySeccionDiaHoraAula(Seccion seccion, Dia dia, Hora hora, Aula aula);

    List<HorarioAula> allByAulaCiclo(Aula aula, CicloAcademico cicloAcademico);

    List<HorarioAula> allByCiclo(CicloAcademico cicloAcademico);

    List<HorarioAula> allByCiclo(CicloAcademico cicloAcademico, EstadoHorarioAulaEnum... estados);

    void deleteAllInList(List<HorarioAula> muertos);

    List<HorarioAula> allBySecciones(List<Seccion> seccions, CicloAcademico cicloOrigen);

    List<HorarioAula> allBySeccion(Seccion seccion);

    List<HorarioAula> allByFilterAulaTramite(List<Aula> aulas);

    List<HorarioAula> allSoloDiaByDiasHoras(List<String> diashoras, Date fechainicio);

    List<HorarioAula> allRangoDiaByDiasHoras(List<String> diashoras, Date fechainicio, Date fechafin);

    void deleteAllByReservaAula(ReservaAula reservaAulaForm);

    List<HorarioAula> allByAulaFecha(Aula aulaFormFecha);

    List<HorarioAula> allHorarioClasesBySecciones(List<Seccion> secciones, SemanaExamen semanaExamen);

    List<HorarioAula> allByAulas(List<Aula> aulas);

    List<HorarioAula> allByAulasAndSecciones(List<Aula> aulas, List<Seccion> secciones);

    List<HorarioAula> allByAulasAndNotInSecciones(List<Aula> aulas, List<Seccion> secciones, Date fechaInicio, Date fechaFin);

    List<HorarioAula> allForRolExamenesByCicloAcademico(CicloAcademico cicloAcademicoRol);

    List<HorarioAula> allByCicloOrderByDiaHora(CicloAcademico cicloAcademico);

    List<HorarioAula> allByCicloAndSemanaExamenLimitByHours(EventoCicloAcademico eventoCicloAcademico, SemanaExamen semanaExamen);

    List<HorarioAula> allOcupadasByCicloAndSemanaExamen(CicloAcademico cicloAcademico, SemanaExamen semanaExamen);

    List<HorarioAula> allBySeccionGrupoRegular(SeccionGrupoRegular seccionGrupoRegular);

    List<HorarioAula> allBySeccionGrupoEspecial(SeccionGrupoEspecial seccionGrupoEspecial);

    List<HorarioAula> allByCursoMasivo(CursoMasivoExamen cursoMasivoExamen);

    List<HorarioAula> allByRolExamenes(RolExamenes rolExamenes);

    void deleteByRolExamenes(RolExamenes rolExamenes);

    void deleteByLetraGrupoRegular(LetraGrupoRegular letraGrupoRegular);

    void deleteByCursoMasivo(CursoMasivoExamen cursoMasivoExamen);

    void deleteBySeccionGrupoRegular(SeccionGrupoRegular seccionGrupoRegular);

    List<HorarioAula> allByRango(Date fechainicio, Date fechafin);

    List<HorarioAula> allByRango(Date fechainicio, Date fechafin, Aula... aula);

}
