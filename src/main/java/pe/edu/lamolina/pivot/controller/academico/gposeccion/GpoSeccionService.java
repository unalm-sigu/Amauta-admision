package pe.edu.lamolina.pivot.controller.academico.gposeccion;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.academico.AnexoBoletin;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.Docente;
import pe.edu.lamolina.pivot.model.academico.DocenteSeccion;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.model.academico.Seccion;
import pe.edu.lamolina.pivot.model.general.Aula;
import pe.edu.lamolina.pivot.model.general.Dia;
import pe.edu.lamolina.pivot.model.general.Oficina;
import pe.edu.lamolina.pivot.model.horario.DiaHoraGrupo;
import pe.edu.lamolina.pivot.model.horario.GrupoHoras;
import pe.edu.lamolina.pivot.model.horario.Hora;
import pe.edu.lamolina.pivot.model.horario.HorarioAula;
import pe.edu.lamolina.pivot.model.horario.TipoGrupoHoras;
import pe.edu.lamolina.pivot.zelper.enums.TipoGrupoHorasEnum;

public interface GpoSeccionService {

    List<GrupoSeccion> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico);

    GpoSeccionResumen resumen();

    List<AnexoBoletin> allAnexosSuperiores();

    List<Curso> allCursosForProgramacion(String nomString);

    List<AnexoBoletin> allAnexoBoletionHijos();

    AnexoBoletin findAnexoBoletin(Long idAnexoBoletin);

    Curso findCurso(Long id);

    GrupoSeccion saveGpoSeccionHeader(GrupoSeccion grupoSeccion, CicloAcademico cicloAcademico);

    GrupoSeccion findGpoSeccion(Long id);

    List<GrupoHoras> allByTipoGrupoHorasCiclo(TipoGrupoHoras tipoGrupoHoras, CicloAcademico cicloAcademico);

    List<Seccion> allSeccionesByGrupo(GrupoSeccion grupoSeccion);

    void addSeccion(GrupoSeccion grupoSeccion);

    void addDocenteSeccion(Seccion seccion);

    void deleteSeccion(Seccion seccion);

    List<DocenteSeccion> allDocentesSeccionBySeccion(Seccion seccion);

    void deleteDocSeccion(DocenteSeccion docenteSeccion);

    List<Docente> allDocenterByNombre(String nombre);

    void cambiarDocentePrincipal(DocenteSeccion docenteSeccion);

    void actualizarDocente(Long docenteSeccionId, Long docenteId);

    void actualizarSeccionVacantes(Seccion seccion);

    void updatePorcentajeAvance(DocenteSeccion docenteSeccion);

    Seccion findSeccion(Long seccionId);

    List<TipoGrupoHoras> allGrupoHorasActivosTipoAndCiclo(CicloAcademico cicloAcademico, TipoGrupoHorasEnum tipoGrupoHorasEnum);

    List<DiaHoraGrupo> allDiaHoraGrupoByGrupo(GrupoHoras grupoHoras, CicloAcademico cicloAcademico);

    List<DiaHoraGrupo> allDiaHoraGrupoByTipo(TipoGrupoHoras tipoGrupoHoras, CicloAcademico cicloAcademico);

    TipoGrupoHoras findTipoGrupoHoras(Long idTipoGrupoHoras);

    List<Dia> allDia();

    List<Hora> allHora();

    List<GrupoHoras> allGrupoHorasBySeccionAndTipoGrupoHoras(Seccion seccion, TipoGrupoHoras tipoGrupoHoras, CicloAcademico cicloAcademico);

    void saveSeccionGrupoHorario(Long seccionId, List<DiaHoraGrupo> diasHorasGrupo, CicloAcademico cicloAcademico);

    void saveAula(Long seccionId, Long aulaId);

    List<HorarioAula> allHorariosAula(Aula aula, CicloAcademico cicloAcademico);

    List<Aula> allAulasSuperiorByOficina(Oficina oficina);

    List<Aula> allAulasBySuperior(Seccion seccion, Aula aula, CicloAcademico cicloAcademico);

    List<HorarioAula> allHorarioAulaByAulaCiclo(Aula aula, Seccion seccion, CicloAcademico cicloAcademico);

    Aula findAula(Long aulaId);

    List<Oficina> allOficinasWithAula(List<Oficina> oficinas);

    List<Aula> allAulaSuperiorByOficinasWithAula(List<Oficina> oficinas);

    List<Aula> searchAulaByName(String nombre);

    TipoGrupoHoras findTipoGrupoHoraByTipo(TipoGrupoHorasEnum tipoGrupoHorasEnum);

    List<GrupoHoras> allGrupoHorasZetasDyna(pe.albatross.octavia.dynatable.DynatableFilter filter,
            TipoGrupoHoras tipoGrupoHoras,
            CicloAcademico cicloAcademico);

    List<DiaHoraGrupo> allDiaHoraGrupo(List<GrupoHoras> grupos);

    GrupoHoras findGrupoHoras(GrupoHoras grupoHoras);

    TipoGrupoHoras findTipoGrupoHoraByTipoAndCiclo(TipoGrupoHorasEnum tipoGrupoHorasEnum, CicloAcademico cicloAcademico);

    List<GrupoHoras> allGrupoHoraByTipoGrupoHoraDyna(pe.albatross.octavia.dynatable.DynatableFilter filter,
            TipoGrupoHoras tipoGrupoHoras,
            CicloAcademico cicloAcademico);

}
