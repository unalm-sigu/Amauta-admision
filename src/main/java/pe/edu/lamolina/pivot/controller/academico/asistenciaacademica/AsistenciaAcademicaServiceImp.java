package pe.edu.lamolina.pivot.controller.academico.asistenciaacademica;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.ListsInspector;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.EventoCicloAcademico;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.InasistenciaAlumno;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.academico.TemaLeccion;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.EstadoHorarioAulaEnum;
import pe.edu.lamolina.model.enums.EventoAcademicoEnum;
import static pe.edu.lamolina.model.enums.EventoAcademicoEnum.CLASES_EPG;
import static pe.edu.lamolina.model.enums.EventoAcademicoEnum.CLASES_PRE;
import static pe.edu.lamolina.model.enums.EventoAcademicoEnum.CLASES_VER;
import pe.edu.lamolina.model.enums.TipoCicloEnum;
import pe.edu.lamolina.model.enums.TipoLeccionEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HoraReprogramada;
import pe.edu.lamolina.model.horario.HorarioAula;
import pe.edu.lamolina.model.horario.HorarioSeccion;
import pe.edu.lamolina.model.horario.LeccionReprogramada;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.EventoAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.EventoCicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.InasistenciaAlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.TemaLeccionDAO;
import pe.edu.lamolina.pivot.dao.general.AulaDAO;
import pe.edu.lamolina.pivot.dao.general.DiaDAO;
import pe.edu.lamolina.pivot.dao.horario.HoraDAO;
import pe.edu.lamolina.pivot.dao.horario.HoraReprogramadaDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioAulaDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioSeccionDAO;
import pe.edu.lamolina.pivot.dao.horario.LeccionReprogramadaDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class AsistenciaAcademicaServiceImp implements AsistenciaAcademicaService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;

    @Autowired
    MatriculaCursoDAO matriculaCursoDAO;

    @Autowired
    SeccionDAO seccionDAO;

    @Autowired
    HorarioSeccionDAO horarioSeccionDAO;

    @Autowired
    DiaDAO diaDAO;

    @Autowired
    InasistenciaAlumnoDAO inasistenciaAlumnoDAO;

    @Autowired
    TemaLeccionDAO temaLeccionDAO;

    @Autowired
    EventoAcademicoDAO eventoAcademicoDAO;

    @Autowired
    EventoCicloAcademicoDAO eventoCicloAcademicoDAO;

    @Autowired
    CursoDAO cursoDAO;

    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;

    @Autowired
    LeccionReprogramadaDAO leccionReprogramadaDAO;

    @Autowired
    HoraReprogramadaDAO horaReprogramadaDAO;

    @Autowired
    HoraDAO horaDAO;

    @Autowired
    AulaDAO aulaDAO;

    @Autowired
    HorarioAulaDAO horarioAulaDAO;

    @Autowired
    GrupoSeccionDAO grupoSeccionDAO;

    @Autowired
    DocenteSeccionDAO docenteSeccionDAO;

    @Override
    public List<Date> findStartEndDateReschedule(Seccion seccion, Docente docente, CicloAcademico cicloAcademico) {
        seccion = seccionDAO.find(seccion.getId());
        GrupoSeccion grupoSeccion = seccion.getGrupoSeccion();
        Curso curso = cursoDAO.find(grupoSeccion.getCurso().getId());

        DateTime fechaInicio = null;
        DateTime fechaFin = null;

        if (curso.getModalidadEstudio().isPregrado()) {
            EventoCicloAcademico eventCicloClasesPre1 = eventoCicloAcademicoDAO.findActivoByCicloTipoEvento(cicloAcademico, EventoAcademicoEnum.CLASES_PRE);
            EventoCicloAcademico eventCicloClasesPre2 = eventoCicloAcademicoDAO.findActivoByCicloTipoEvento(cicloAcademico, EventoAcademicoEnum.CLASES_PRE);
            fechaInicio = new DateTime(eventCicloClasesPre1.getFechaInicio());
            fechaFin = new DateTime(eventCicloClasesPre2.getFechaFin());
        } else if (curso.getModalidadEstudio().isPostgrado()) {
            EventoCicloAcademico clasesEpg = eventoCicloAcademicoDAO.findActivoByCicloTipoEvento(cicloAcademico, EventoAcademicoEnum.CLASES_EPG);
            fechaInicio = new DateTime(clasesEpg.getFechaInicio());
            fechaFin = new DateTime(clasesEpg.getFechaFin());
        }

        List<Date> fechas = new ArrayList<>();
        fechas.add(fechaInicio.toDate());
        fechas.add(fechaFin.toDate());
        return fechas;
    }

    @Override
    public TemaLeccion findTemaLeccionSeccionDocenteFecha(Seccion seccion, Docente docente, DateTime today) {
        TemaLeccion temaLeccion = temaLeccionDAO.findBySeccionDocenteFecha(seccion, docente, today.toDate());
        return temaLeccion;
    }

    @Override
    public List<TemaLeccion> allTemaLeccionBySeccionDocenteDyna(Seccion seccion, Docente docente, DynatableFilter filter) {
        return temaLeccionDAO.allBySeccionDocenteDyna(seccion, docente, filter);
    }

    @Override
    public List<TemaLeccion> allTemaLeccionBySeccion(Seccion seccion) {
        return temaLeccionDAO.allBySeccion(seccion);
    }

    @Override
    public List<LeccionReprogramada> allLeccionReprogramadaBySeccion(Seccion seccion) {
        return leccionReprogramadaDAO.allBySeccion(seccion);
    }

    @Override
    public List<MatriculaSeccion> allMatriculaSeccionBySeccion(Seccion seccion, Docente docente, DateTime today) {

        TemaLeccion temaLeccion = temaLeccionDAO.findBySeccionDocenteFecha(seccion, docente, today.toDate());
        seccion = this.findSeccionDia(seccion, today);

        List<InasistenciaAlumno> inasistenciasAlumnos = new ArrayList();
        if (temaLeccion != null) {
            inasistenciasAlumnos = inasistenciaAlumnoDAO.allActivosByTemaLeccion(temaLeccion);
        }
        Map<String, InasistenciaAlumno> mapInasistenicias = TypesUtil.convertListToMap("keyMatResumen", inasistenciasAlumnos);

        List<MatriculaSeccion> matriculasSeccion = matriculaSeccionDAO.allMatriculadosBySeccion(seccion);
        for (MatriculaSeccion matriculaSeccionEach : matriculasSeccion) {
            MatriculaResumen resumenAlumno = matriculaSeccionEach.getMatriculaResumen();
            Seccion seccionClone = seccion.clone();
            seccionClone.setHorarioSeccion(new ArrayList<>());
            for (HorarioSeccion horSeccion : seccion.getHorarioSeccion()) {
                seccionClone.getHorarioSeccion().add(horSeccion.clone());
            }

            matriculaSeccionEach.setSeccion(seccionClone);

            if (!inasistenciasAlumnos.isEmpty()) {

                for (HorarioSeccion horaSeccion : matriculaSeccionEach.getSeccion().getHorarioSeccion()) {
                    Hora hora = horaSeccion.getHora();
                    InasistenciaAlumno inasistencia = mapInasistenicias.get(hora.getId() + "-" + resumenAlumno.getId());
                    if (inasistencia != null) {
                        horaSeccion.setSeleccionado(false);
                    }

                }
            }
        }

        return matriculasSeccion;
    }

    @Override
    public Seccion findSeccionDia(Seccion seccion, DateTime today) {
        seccion = seccionDAO.find(seccion.getId());
        Dia dia = diaDAO.findByNumeroDia(today.getDayOfWeek());

        List<HorarioSeccion> horarioSeccion = horarioSeccionDAO.allBySeccionDia(seccion, dia);
        Collections.sort(horarioSeccion, (p1, p2) -> p1.getHora().getHora().compareTo(p2.getHora().getHora()));
        for (HorarioSeccion horarioSeccionEach : horarioSeccion) {
            horarioSeccionEach.setSeleccionado(true);
        }
        seccion.setHorarioSeccion(horarioSeccion);

        return seccion;
    }

    @Override
    @Transactional
    public Seccion findSeccion(Long idSeccion) {
        Seccion seccion = seccionDAO.find(idSeccion);
        List<HorarioSeccion> horariosSeccion = horarioSeccionDAO.allBySeccion(seccion);
        seccion.setHorarioSeccion(horariosSeccion);
        return seccion;
    }

    @Override
    public TemaLeccion findTemaLeccion(Long idTemaLeccion) {
        TemaLeccion temaLeccion = temaLeccionDAO.find(idTemaLeccion);
        return temaLeccion;
    }

    @Override
    @Transactional
    public void saveInasistencia(TemaLeccion temaLeccion, Docente docente, CicloAcademico cicloAcademico, DataSessionPivot ds) {
        DateTime today = new DateTime();

        temaLeccion.setDocente(docente);
        temaLeccion.setFecha(today.toDate());
        temaLeccion.setUsuarioRegistro(ds.getUsuario());
        temaLeccion.setFechaRegistro(today.toDate());
        temaLeccion.setTipoEnum(TipoLeccionEnum.REG);
        temaLeccionDAO.save(temaLeccion);

        Curso curso = temaLeccion.getSeccion().getGrupoSeccion().getCurso();
        List<MatriculaSeccion> matriculasSeccionForm = temaLeccion.getSeccion().getMatriculaSeccion();
        List<MatriculaResumen> resumenes = matriculasSeccionForm.stream().map(x -> x.getMatriculaResumen()).collect(Collectors.toList());
        List<MatriculaCurso> matriculasCursosBD = matriculaCursoDAO.allByMatriculaResumenCurso(resumenes, curso);
        Map<Long, MatriculaCurso> mapMatriculaCurso = TypesUtil.convertListToMap("matriculaResumen.id", matriculasCursosBD);

        List<InasistenciaAlumno> inasistencias = createInasistencias(temaLeccion, matriculasSeccionForm, mapMatriculaCurso, today, ds);
        for (InasistenciaAlumno inasistencia : inasistencias) {
            inasistenciaAlumnoDAO.save(inasistencia);
            MatriculaCurso matCurso = inasistencia.getMatriculaCurso();
            matCurso.setInasistencias(matCurso.getInasistencias() + 1);
        }

    }

    private List<InasistenciaAlumno> createInasistencias(
            TemaLeccion temaLeccion,
            List<MatriculaSeccion> matriculasSeccionForm,
            Map<Long, MatriculaCurso> mapMatriculaCurso,
            DateTime today,
            DataSessionPivot ds) {

        List<InasistenciaAlumno> inasistencias = new ArrayList();
        for (MatriculaSeccion matriculaSeccion : matriculasSeccionForm) {
            List<HorarioSeccion> horarioSeccionAlu = matriculaSeccion.getSeccion().getHorarioSeccion();
            if (!horarioSeccionAlu.isEmpty()) {

                MatriculaCurso matriculaCurso = mapMatriculaCurso.get(matriculaSeccion.getMatriculaResumen().getId());

                for (HorarioSeccion horarioSeccion : horarioSeccionAlu) {
                    if (!horarioSeccion.isSeleccionado()) {
                        InasistenciaAlumno inasistencia = new InasistenciaAlumno();
                        inasistencia.setEsReprogramado(BigDecimal.ZERO.intValue());
                        inasistencia.setEstadoEnum(EstadoEnum.ACT);
                        inasistencia.setHora(horarioSeccion.getHora());
                        inasistencia.setMatriculaCurso(matriculaCurso);
                        inasistencia.setTemaLeccion(temaLeccion);

                        inasistencia.setFechaRegistro(today.toDate());
                        inasistencia.setUsuarioRegistro(ds.getUsuario());
                        inasistencias.add(inasistencia);
                    }
                }

            }
        }

        return inasistencias;
    }

    @Override
    @Transactional
    public void updateInasistencia(TemaLeccion temaLeccion, Docente docente, CicloAcademico cicloAcademico, DataSessionPivot ds) {
        DateTime today = new DateTime();

        TemaLeccion temaLeccionUpd = new TemaLeccion();
        temaLeccionUpd.setId(temaLeccion.getId());
        temaLeccionUpd.setTema(temaLeccion.getTema());
        temaLeccionDAO.updateTema(temaLeccionUpd);

        Curso curso = temaLeccion.getSeccion().getGrupoSeccion().getCurso();
        List<MatriculaSeccion> matriculasSeccionForm = temaLeccion.getSeccion().getMatriculaSeccion();
        List<MatriculaResumen> resumenes = matriculasSeccionForm.stream().map(x -> x.getMatriculaResumen()).collect(Collectors.toList());
        List<MatriculaCurso> matriculasCursosBD = matriculaCursoDAO.allByMatriculaResumenCurso(resumenes, curso);
        Map<Long, MatriculaCurso> mapMatriculaCurso = TypesUtil.convertListToMap("matriculaResumen.id", matriculasCursosBD);

        List<InasistenciaAlumno> inasistenciasForm = createInasistencias(temaLeccion, matriculasSeccionForm, mapMatriculaCurso, today, ds);
        List<InasistenciaAlumno> inasistenciasDB = inasistenciaAlumnoDAO.allByTemaLeccion(temaLeccion);
        ListsInspector inspector = TypesUtil.analizeLists(inasistenciasDB, inasistenciasForm, "key");

        List<InasistenciaAlumno> inasistenciasNuevos = inspector.getNewList();
        for (InasistenciaAlumno inasistencia : inasistenciasNuevos) {
            inasistenciaAlumnoDAO.save(inasistencia);
            MatriculaCurso matCurso = inasistencia.getMatriculaCurso();
            matCurso.setInasistencias(matCurso.getInasistencias() + 1);
            matriculaCursoDAO.updateInasistencias(matCurso);
        }

        List<InasistenciaAlumno> inasistenciasRepetidosBD = inspector.getOldListDB();
        for (InasistenciaAlumno inasistencia : inasistenciasRepetidosBD) {
            if (inasistencia.getEstadoEnum() != EstadoEnum.ACT) {
                inasistencia.setEstadoEnum(EstadoEnum.ACT);
                inasistencia.setFechaModificacion(today.toDate());
                inasistencia.setUsuarioModificacion(ds.getUsuario());
                inasistenciaAlumnoDAO.update(inasistencia);

                MatriculaCurso matCurso = inasistencia.getMatriculaCurso();
                matCurso.setInasistencias(matCurso.getInasistencias() + 1);
                matriculaCursoDAO.updateInasistencias(matCurso);
            }
        }

        List<InasistenciaAlumno> inasistenciasMuertos = inspector.getDeadList();
        for (InasistenciaAlumno inasistencia : inasistenciasMuertos) {
            if (inasistencia.getEstadoEnum() != EstadoEnum.ANU) {
                inasistencia.setEstadoEnum(EstadoEnum.ANU);
                inasistencia.setFechaModificacion(today.toDate());
                inasistencia.setUsuarioModificacion(ds.getUsuario());
                inasistenciaAlumnoDAO.update(inasistencia);

                MatriculaCurso matCurso = inasistencia.getMatriculaCurso();
                matCurso.setInasistencias(matCurso.getInasistencias() - 1);
                matriculaCursoDAO.updateInasistencias(matCurso);
            }
        }
    }

    @Override
    @Transactional
    public void saveReprogramacion(LeccionReprogramada leccionReprogramada, Usuario usuario, Docente docente, CicloAcademico cicloAcademico) {
        DateTime today = new DateTime();
        DateTime fechaReprogramada = new DateTime(leccionReprogramada.getFechaReprogramada());

        Seccion seccion = leccionReprogramada.getSeccion();
        DateTime fechaOrigen = new DateTime(leccionReprogramada.getFechaOrigen());
        Dia diaOrigen = diaDAO.findByNumeroDia(fechaOrigen.getDayOfWeek());

        List<HorarioSeccion> horariosSeccion = horarioSeccionDAO.allBySeccionDia(seccion, diaOrigen);
        String[] horaInicio = leccionReprogramada.getHoraInicio().split(":");
        String[] horaFin = leccionReprogramada.getHoraFin().split(":");

        LocalTime horaInicioLT = LocalTime.of(Integer.parseInt(horaInicio[0]), Integer.parseInt(horaInicio[1]), 0);
        LocalTime horaFinLT = LocalTime.of(Integer.parseInt(horaFin[0]), Integer.parseInt(horaFin[1]), 0);

        if (horaFinLT.getHour() <= horaInicioLT.getHour()) {
            throw new PhobosException("Error en las horas seleccionadas");
        }

        long horas = ChronoUnit.HOURS.between(horaInicioLT, horaFinLT);
        if (horas != horariosSeccion.size()) {
            throw new PhobosException("Solo puede reprogramar %s horas", horariosSeccion.size());
        }

        List<HorarioAula> horariosAulas = horarioAulaDAO.allByAula(leccionReprogramada.getAula(), cicloAcademico);
        horariosAulas.stream().filter(x -> x.getDia().getNumeroDia().compareTo(fechaReprogramada.getDayOfWeek()) == 0).collect(Collectors.toList());
        for (int i = horaInicioLT.getHour(); i <= horaFinLT.getHour(); i++) {
            for (HorarioAula horariosAula : horariosAulas) {
                if (horariosAula.getHora().getNumero().compareTo(i) == 0) {
                    throw new PhobosException("Las horas seleccionadas, no están disponibles para el aula.");
                }
            }
        }

        leccionReprogramada.setEstadoEnum(EstadoEnum.ACT);
        leccionReprogramada.setUsuarioRegistro(usuario);
        leccionReprogramada.setFechaRegistro(today.toDate());
        leccionReprogramadaDAO.save(leccionReprogramada);

        for (int i = horaInicioLT.getHour(); i <= horaFinLT.getHour(); i++) {
            Hora hora = horaDAO.findByNumeroHora(i);
            HoraReprogramada horaReprogramada = new HoraReprogramada();
            horaReprogramada.setLeccionReprogramada(leccionReprogramada);
            horaReprogramada.setHora(hora);
            horaReprogramadaDAO.save(horaReprogramada);
        }

        Seccion seccionDb = seccionDAO.find(leccionReprogramada.getSeccion());
        ModalidadEstudio modalidadCurso = seccionDb.getGrupoSeccion().getCurso().getModalidadEstudio();
        EventoCicloAcademico eventoAcademico = this.getEventoCicloAcademico(cicloAcademico, modalidadCurso);

        for (int i = horaInicioLT.getHour(); i <= horaFinLT.getHour(); i++) {
            Hora hora = horaDAO.findByNumeroHora(i);
            Dia dia = diaDAO.findByNumeroDia(fechaReprogramada.getDayOfWeek());
            HorarioAula horarioAula = new HorarioAula();
            horarioAula.setHora(hora);
            horarioAula.setDia(dia);
            horarioAula.setAula(leccionReprogramada.getAula());
            horarioAula.setSeccion(leccionReprogramada.getSeccion());

            horarioAula.setEstadoEnum(EstadoHorarioAulaEnum.ACT);

            if (eventoAcademico != null) {
                horarioAula.setFechaInicio(eventoAcademico.getFechaInicio());
                horarioAula.setFechaFin(eventoAcademico.getFechaFin());
            }

            horarioAulaDAO.save(horarioAula);
        }

        TemaLeccion temaLeccion = new TemaLeccion();
        temaLeccion.setTema(leccionReprogramada.getMotivo());
        temaLeccion.setDocente(docente);
        temaLeccion.setSeccion(leccionReprogramada.getSeccion());
        temaLeccion.setFecha(leccionReprogramada.getFechaReprogramada());
        temaLeccion.setUsuarioRegistro(usuario);
        temaLeccion.setFechaRegistro(today.toDate());
        temaLeccion.setTipoEnum(TipoLeccionEnum.REP);
        temaLeccionDAO.save(temaLeccion);
    }

    @Override
    public List<Aula> searchAulaByName(String nombre) {
        return aulaDAO.searchByNombreFilter(nombre, Integer.SIZE);
    }

    @Override
    public List<GrupoSeccion> allGposSeccionesByDocente(Docente docente, CicloAcademico ciclo, DataSessionPivot ds) {

        List<GrupoSeccion> gruposSecciones = grupoSeccionDAO.allActivosByDocenteCiclo(docente, ciclo);
        List<Seccion> secciones = seccionDAO.allActivosByGposSeccion(gruposSecciones);
        List<DocenteSeccion> profesoresSecciones = docenteSeccionDAO.allActivosBySecciones(secciones);
        List<HorarioSeccion> horariosSecciones = horarioSeccionDAO.allBySecciones(secciones);

        Map<Long, List<Seccion>> mapSecciones = TypesUtil.convertListToMapList("grupoSeccion.id", secciones);
        Map<Long, List<DocenteSeccion>> mapProfeSecciones = TypesUtil.convertListToMapList("seccion.id", profesoresSecciones);
        Map<Long, List<HorarioSeccion>> mapHorariosSecciones = TypesUtil.convertListToMapList("seccion.id", horariosSecciones);

        for (GrupoSeccion gpoSecc : gruposSecciones) {
            List<Seccion> seccionesGpo = TypesUtil.getListNotNull(mapSecciones.get(gpoSecc.getId()));
            gpoSecc.setSecciones(seccionesGpo);
            for (Seccion seccion : seccionesGpo) {
                List<HorarioSeccion> horariosSeccion = TypesUtil.getListNotNull(mapHorariosSecciones.get(seccion.getId()));
                seccion.setHorarioSeccion(horariosSeccion);

                List<DocenteSeccion> profesSeccion = TypesUtil.getListNotNull(mapProfeSecciones.get(seccion.getId()));
                seccion.setDocenteSeccion(profesSeccion);
            }
        }
        
        Collections.sort(gruposSecciones, new GrupoSeccion.CompareNombreCurso());

        return gruposSecciones;

    }

    private EventoCicloAcademico getEventoCicloAcademico(CicloAcademico cicloAcademico, ModalidadEstudio modalidadCurso) {
        EventoAcademicoEnum eventoEnum = cicloAcademico.getTipoEnum() == TipoCicloEnum.NIV ? CLASES_VER
                : (modalidadCurso.isPostgrado() ? CLASES_EPG : (modalidadCurso.isPregrado() ? CLASES_PRE : null));
        if (eventoEnum == null) {
            throw new PhobosException("No se ha encontrado algun evento de clases.");
        }
        EventoCicloAcademico eventoCiclo = eventoCicloAcademicoDAO.findActivoByCicloTipoEvento(cicloAcademico, eventoEnum);
        return eventoCiclo;
    }

}
