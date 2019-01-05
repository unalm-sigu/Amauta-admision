package pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion;

import java.util.Date;
import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.AnexoBoletin;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.CursoCicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.EventoCicloAcademico;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.academico.TipoRepitencia;
import pe.edu.lamolina.model.enums.SeccionEstadoEnum;
import pe.edu.lamolina.model.enums.TipoGrupoHorasEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.horario.DiaHoraGrupo;
import pe.edu.lamolina.model.horario.GrupoHoras;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioAula;
import pe.edu.lamolina.model.horario.HorarioSeccion;
import pe.edu.lamolina.model.horario.TipoGrupoHoras;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.zelper.enums.TipoRestriccionEnum;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface GpoSeccionService {

    CicloAcademico findCiclo(CicloAcademico cicloAcademico);

    Oficina findOficinaOera();

    List<Oficina> allOficinas(Compania compania);

    List<GrupoSeccion> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico);

    List<GrupoSeccion> allCleanByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico);

    void cambiarEstadoGpoSeccion(SeccionEstadoEnum estadoEnum, GrupoSeccion grupoSeccion, Usuario usuario);

    List<AnexoBoletin> allAnexosSuperiores();

    List<Curso> allCursosForProgramacion(String nomString);

    List<AnexoBoletin> allAnexoBoletionHijos();

    AnexoBoletin findAnexoBoletin(Long idAnexoBoletin);

    Curso findCurso(Long id);

    List<GrupoSeccion> saveGpoSeccionHeader(GrupoSeccion grupoSeccion, CicloAcademico cicloAcademico);

    GrupoSeccion findGpoSeccion(Long id);

    List<GrupoHoras> allByTipoGrupoHorasCiclo(TipoGrupoHoras tipoGrupoHoras, CicloAcademico cicloAcademico);

    List<Seccion> allSeccionesByGrupo(GrupoSeccion grupoSeccion, List<DocenteSeccion> docentesSeccion);

    void addSeccion(GrupoSeccion grupoSeccion);

    void addDocenteSeccion(Seccion seccion, CicloAcademico cicloAcademico);

    void deleteSeccion(Seccion seccion);

    void activarSeccion(Seccion seccion, Usuario usuario);

    void bloquearSeccion(Seccion seccion, Usuario usuario);

    void anularSeccion(Seccion seccion, Usuario usuario);

    List<DocenteSeccion> allDocentesSeccionBySeccion(Seccion seccion);

    void deleteDocSeccion(DocenteSeccion docenteSeccion, CicloAcademico academico);

    List<Docente> allDocenterByNombre(String nombre, String codigoDep);

    void cambiarDocentePrincipal(DocenteSeccion docenteSeccion);

    void actualizarDocente(Long docenteSeccionId, Long docenteId, CicloAcademico cicloAcademico);

    void actualizarSeccionVacantes(Seccion seccion, Usuario usuario);

    void updatePorcentajeAvance(DocenteSeccion docenteSeccion, CicloAcademico cicloAcademico);

    Seccion findSeccion(Long seccionId);

    Seccion findSeccionWithRestriccions(Long seccionId);

    List<TipoGrupoHoras> allGrupoHorasActivosTipoAndCiclo(CicloAcademico cicloAcademico, TipoGrupoHorasEnum tipoGrupoHorasEnum);

    List<DiaHoraGrupo> allDiaHoraGrupoByGrupo(GrupoHoras grupoHoras, CicloAcademico cicloAcademico);

    List<DiaHoraGrupo> allDiaHoraGrupoByTipo(TipoGrupoHoras tipoGrupoHoras, CicloAcademico cicloAcademico);

    TipoGrupoHoras findTipoGrupoHoras(Long idTipoGrupoHoras);

    List<Dia> allDia();

    List<Hora> allHora();

    List<GrupoHoras> allGrupoHorasBySeccionAndTipoGrupoHoras(Seccion seccion, TipoGrupoHoras tipoGrupoHoras, CicloAcademico cicloAcademico);

    void saveSeccionGrupoHorario(Seccion seccion, GrupoHoras grupoHoras, CicloAcademico cicloAcademico);

    void saveAula(Long seccionId, Long aulaId, CicloAcademico cicloAcademico);

    List<HorarioAula> allHorariosAula(Aula aula, CicloAcademico cicloAcademico);

    List<Aula> allPabellonesByOficina(Oficina oficina);

    List<Aula> allAulasByPabellon(Seccion seccion, Aula pabellon, CicloAcademico cicloAcademico);

    //List<HorarioAula> allHorarioAulaByAulaCiclo(Aula aula, Seccion seccion, CicloAcademico cicloAcademico);
    Aula findAula(Long aulaId);

    Aula findAulaFull(Long aulaId, CicloAcademico cicloAcademico);

    List<Oficina> allOficinasWithAula(List<Oficina> oficinas);

    List<Aula> allPabellonesByOficinasNoOera(List<Oficina> oficinas);

    List<Aula> searchAulaByName(String nombre, Long seccionId, CicloAcademico ciclo);

    TipoGrupoHoras findTipoGrupoHoraByTipo(TipoGrupoHorasEnum tipoGrupoHorasEnum);

    List<GrupoHoras> allGrupoHorasZetasDyna(DynatableFilter filter, CicloAcademico cicloAcademico);

    //List<DiaHoraGrupo> allDiaHoraGrupo(List<GrupoHoras> grupos, CicloAcademico ciclo);
    GrupoHoras findGrupoHorasWithHorario(Seccion seccion, CicloAcademico ciclo);

    GrupoHoras findGrupoHorasWithHorario(Seccion seccion, GrupoHoras grupoHoras, CicloAcademico ciclo);

    GrupoHoras findGrupoHoras(GrupoHoras grupoHoras, CicloAcademico ciclo);

    GrupoHoras findGrupoHorasFull(GrupoHoras grupoHoras, CicloAcademico cicloAcademico);

    TipoGrupoHoras findTipoGpoByEnumCiclo(TipoGrupoHorasEnum tipoGrupoHorasEnum, CicloAcademico cicloAcademico);

    List<GrupoHoras> allGrupoByTipoGpoSeccionDynatable(DynatableFilter filter,
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

    void saveTipoRepitenciaRestriccion(Seccion seccion, List<TipoRepitencia> tiposRepitencia, DataSessionPivot ds);

    List<EventoCicloAcademico> allEventoCicloAcademicoForPeriodo(CicloAcademico cicloAcademico);

    List<Date> allDatesEventoCicloAcademicoForPeriodo(CicloAcademico cicloAcademico);

    void updateDocenteSecFechaInicio(DocenteSeccion docenteSeccion, CicloAcademico cicloAcademico);

    void updateDocenteSecFechaFin(DocenteSeccion docenteSeccion, CicloAcademico cicloAcademico);

    List<DocenteSeccion> analizedDocenteSeccion(GrupoSeccion grupoSeccion, CicloAcademico cicloAcademico);

    Aula findAulaActiveByCode(String codigoAula);

    GrupoHoras findGrupoHorasForDirectUpdate(String code, CicloAcademico cicloAcademico, Seccion seccion);

    void actualizarSeccionResctriccionCapa(Seccion seccionForm, Usuario usuario);

    List<HorarioSeccion> allHorarioSeccion(Seccion seccion);

    void evaluateSeccion(Seccion seccion);

    List<GrupoSeccion> clonar(GrupoSeccion grupoSeccion, Integer veces, DataSessionPivot ds);

    List<GrupoSeccion> allCleanByDynatableGruposSeccion(DynatableFilter filter, CicloAcademico ciclo, List<GrupoSeccion> gpos);

    Long contarGpoSecc(CicloAcademico ciclo);

    void actualizarBoletin();

    List<DiaHoraGrupo> searchDiasHorasByHorasSemanales(List<DiaHoraGrupo> diasHoras, Integer horasSemanales, List<Dia> dias);

    CursoCicloAcademico findCursoCicloAcademico(Curso curso, CicloAcademico cicloAcademico);

    void generarpagodocente(DocenteSeccion docenteSeccion, DataSessionPivot ds);

    void recrearVacanteAlumno(CicloAcademico ciclo, DataSessionPivot ds);

}
