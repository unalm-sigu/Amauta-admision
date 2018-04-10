package pe.edu.lamolina.pivot.controller.academico.gposeccion;

import java.util.Date;
import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.AnexoBoletin;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.EventoCicloAcademico;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.academico.TipoRepitencia;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.TipoGrupoHorasEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.horario.DiaHoraGrupo;
import pe.edu.lamolina.model.horario.GrupoHoras;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioAula;
import pe.edu.lamolina.model.horario.TipoGrupoHoras;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.zelper.enums.TipoRestriccionEnum;

public interface GpoSeccionService {

    List<GrupoSeccion> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico);

    void cambiarEstadoGpoSeccion(EstadoEnum estadoEnum, GrupoSeccion grupoSeccion, Usuario usuario);

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

    void addDocenteSeccion(Seccion seccion, CicloAcademico cicloAcademico);

    void deleteSeccion(Seccion seccion);

    void activarSeccion(Seccion seccion, Usuario usuario);

    void bloquearSeccion(Seccion seccion, Usuario usuario);

    void anularSeccion(Seccion seccion, Usuario usuario);

    List<DocenteSeccion> allDocentesSeccionBySeccion(Seccion seccion);

    void deleteDocSeccion(DocenteSeccion docenteSeccion);

    List<Docente> allDocenterByNombre(String nombre);

    void cambiarDocentePrincipal(DocenteSeccion docenteSeccion);

    void actualizarDocente(Long docenteSeccionId, Long docenteId);

    void actualizarSeccionVacantes(Seccion seccion, Usuario usuario);

    void updatePorcentajeAvance(DocenteSeccion docenteSeccion);

    Seccion findSeccion(Long seccionId);

    List<TipoGrupoHoras> allGrupoHorasActivosTipoAndCiclo(CicloAcademico cicloAcademico, TipoGrupoHorasEnum tipoGrupoHorasEnum);

    List<DiaHoraGrupo> allDiaHoraGrupoByGrupo(GrupoHoras grupoHoras, CicloAcademico cicloAcademico);

    List<DiaHoraGrupo> allDiaHoraGrupoByTipo(TipoGrupoHoras tipoGrupoHoras, CicloAcademico cicloAcademico);

    TipoGrupoHoras findTipoGrupoHoras(Long idTipoGrupoHoras);

    List<Dia> allDia();

    List<Hora> allHora();

    List<GrupoHoras> allGrupoHorasBySeccionAndTipoGrupoHoras(Seccion seccion, TipoGrupoHoras tipoGrupoHoras, CicloAcademico cicloAcademico);

    void saveSeccionGrupoHorario(Long seccionId, GrupoHoras grupoHoras, CicloAcademico cicloAcademico);

    void saveAula(Long seccionId, Long aulaId, CicloAcademico cicloAcademico);

    List<HorarioAula> allHorariosAula(Aula aula, CicloAcademico cicloAcademico);

    List<Aula> allAulasSuperiorByOficina(Oficina oficina);

    List<Aula> allAulasBySuperior(Seccion seccion, Aula aula, CicloAcademico cicloAcademico);

    List<HorarioAula> allHorarioAulaByAulaCiclo(Aula aula, Seccion seccion, CicloAcademico cicloAcademico);

    Aula findAula(Long aulaId);

    Aula findAulaFull(Long aulaId, CicloAcademico cicloAcademico);

    List<Oficina> allOficinasWithAula(List<Oficina> oficinas);

    List<Aula> allAulaSuperiorByOficinasWithAula(List<Oficina> oficinas);

    List<Aula> searchAulaByName(String nombre);

    TipoGrupoHoras findTipoGrupoHoraByTipo(TipoGrupoHorasEnum tipoGrupoHorasEnum);

    List<GrupoHoras> allGrupoHorasZetasDyna(pe.albatross.octavia.dynatable.DynatableFilter filter,
            TipoGrupoHoras tipoGrupoHoras,
            CicloAcademico cicloAcademico);

    List<DiaHoraGrupo> allDiaHoraGrupo(List<GrupoHoras> grupos);

    GrupoHoras findGrupoHoras(GrupoHoras grupoHoras);

    GrupoHoras findGrupoHorasFull(GrupoHoras grupoHoras, CicloAcademico cicloAcademico);

    TipoGrupoHoras findTipoGrupoHoraByTipoAndCiclo(TipoGrupoHorasEnum tipoGrupoHorasEnum, CicloAcademico cicloAcademico);

    List<GrupoHoras> allGrupoHoraByTipoGrupoHoraDyna(pe.albatross.octavia.dynatable.DynatableFilter filter,
            TipoGrupoHoras tipoGrupoHoras,
            CicloAcademico cicloAcademico,
            Seccion seccion);

    GpoSeccionResumen resumenByCiclo(CicloAcademico ciclo);

    List<AnexoBoletin> allAnexosBySuperiorCiclo(String anexoSuperior, CicloAcademico ciclo);

    List<Facultad> allFacultadesActivas();

    List<ModalidadEstudio> allModalidadesEstudioActivas();

    List<Carrera> allCarrerasActivas();

    List<Carrera> allCarrerasActivasPrePost();

    void saveRestriccion(Seccion seccion, Usuario usuario, TipoRestriccionEnum tipoRestriccionEnum, List<Long> restricciones);

    List<TipoRepitencia> allTipoRepitencia();

    void saveTipoRepitenciaRestriccion(Seccion seccion, Usuario usuario, List<TipoRepitencia> tiposRepitencia);

    List<EventoCicloAcademico> allEventoCicloAcademicoForPeriodo(CicloAcademico cicloAcademico);

    List<Date> allDatesEventoCicloAcademicoForPeriodo(CicloAcademico cicloAcademico);

    void updateDocenteSecFechaInicio(DocenteSeccion docenteSeccion);

    void updateDocenteSecFechaFin(DocenteSeccion docenteSeccion);

    void analizedDocenteSeccion(Seccion seccion, CicloAcademico cicloAcademico);

    void analizedDocenteSeccion(GrupoSeccion grupoSeccion, CicloAcademico cicloAcademico);

    Aula findAulaActiveByCode(String codigoAula);

    GrupoHoras findGrupoHorasForDirectUpdate(String code, CicloAcademico cicloAcademico, Seccion seccion);

    void actualizarSeccionResctriccionCapa(Seccion seccionForm, Usuario usuario);

}
