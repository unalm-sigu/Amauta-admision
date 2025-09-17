package pe.edu.lamolina.amauta.controller.nivelacioneegg.leccionnivelacion;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import static org.joda.time.DateTimeConstants.MONDAY;
import static org.joda.time.DateTimeConstants.SUNDAY;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.edu.lamolina.amauta.config.DespliegueConfig;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.leccionnivelacion.dto.ControlAsistenciaDTO;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.leccionnivelacion.dto.PeriodoCantidadDiasDTO;
import pe.edu.lamolina.amauta.dao.horario.HorarioAulaDAO;
import pe.edu.lamolina.amauta.dao.horario.HorarioCursoDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.AsistenciaNivelacionDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.CursoNivelacionDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.NotaAlumnoNivelacionDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.TemaAsistenciaDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.CursoCicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.enums.dictadoclases.AsistenciaClasesEstadoEnum;
import pe.edu.lamolina.model.enums.dictadoclases.ControlAsistenciaEstadoEnum;
import pe.edu.lamolina.model.horario.GrupoHorasNivelacion;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioAula;
import pe.edu.lamolina.model.horario.HorarioCurso;
import pe.edu.lamolina.model.nivelacioneegg.AsistenciaNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.CursoNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.NotaAlumnoNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.TemaAsistencia;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class LeccionNivelacionServiceImpl implements LeccionNivelacionService {

    private final AsistenciaNivelacionDAO asistenciaNivelacionDAO;
    private final CursoNivelacionDAO cursoNivelacionDAO;
    private final HorarioAulaDAO horarioAulaDAO;
    private final HorarioCursoDAO horarioCursoDAO;
    private final NotaAlumnoNivelacionDAO notaAlumnoNivelacionDAO;
    private final TemaAsistenciaDAO temaAsistenciaDAO;

    private final DespliegueConfig despliegueConfig;

    @Override
    public CursoNivelacion findSeccion(CursoNivelacion form, Docente docenteForm, CicloAcademico cicloForm) {
        Assert.isNotNull(docenteForm, "No existe un docente");

        CursoNivelacion seccion = cursoNivelacionDAO.find(form.getId());
        Assert.isNotNull(seccion, "No existe la sección solicitada");

        Docente docente = seccion.getDocente();
        Assert.isTrue(docente.getId().equals(docenteForm.getId()), "Esta sección no corresponde al docente");

        CicloAcademico ciclo = seccion.getCursoCiclo().getCicloAcademico();
        Assert.isTrue(ciclo.getId().equals(cicloForm.getId()), "Esta sección no corresponde al ciclo actual");

        return seccion;
    }

    @Override
    public CursoNivelacion findSeccion(CursoNivelacion form, CicloAcademico cicloForm) {
        CursoNivelacion seccion = cursoNivelacionDAO.find(form.getId());
        Assert.isNotNull(seccion, "No existe la sección solicitada");

        CicloAcademico ciclo = seccion.getCursoCiclo().getCicloAcademico();
        Assert.isTrue(ciclo.getId().equals(cicloForm.getId()), "Esta sección no corresponde al ciclo actual");

        return seccion;
    }

    @Override
    public List<TemaAsistencia> allLecciones(DynatableFilter filter, CursoNivelacion seccion) {
        return temaAsistenciaDAO.allSeccionByDynatable(filter, seccion);
    }

    @Override
    public List<ControlAsistenciaDTO> allFechasLecciones(CursoNivelacion seccion) {
        CursoCicloAcademico cursoCiclo = seccion.getCursoCiclo();
        GrupoHorasNivelacion grupoHoras = seccion.getGrupoHoras();
        List<HorarioCurso> horarios = horarioCursoDAO.allByCursoCicloHorario(cursoCiclo, grupoHoras);
        log.info("[allFechasLecciones] horarios.size={}", horarios.size());

        Map<String, PeriodoCantidadDiasDTO> mapPeriodos = new HashMap();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        horarios.forEach(hor -> {
            DateTime semana = new DateTime(hor.getSemana());
            Date lunes = semana.withDayOfWeek(MONDAY).withTimeAtStartOfDay().toDate();
            Date domingo = semana.withDayOfWeek(SUNDAY).withTimeAtStartOfDay().toDate();
            log.info("[allFechasLecciones] semana={} lunes={} domingo={}",
                    sdf.format(semana.toDate()), sdf.format(lunes), sdf.format(domingo));

            String key = sdf.format(lunes) + "-" + sdf.format(domingo);
            PeriodoCantidadDiasDTO periodo = mapPeriodos.get(key);

            if (periodo == null) {
                periodo = new PeriodoCantidadDiasDTO(lunes, domingo, hor.getDia().getNumeroDia());
                mapPeriodos.put(key, periodo);
            } else {
                periodo.getDiasSemanas().add(hor.getDia().getNumeroDia());
            }
        });

        log.info("[allFechasLecciones] periodos.size={}", mapPeriodos.size());
        List<PeriodoCantidadDiasDTO> periodos = new ArrayList(mapPeriodos.values());
        List<ControlAsistenciaDTO> fechas = this.crearFechas(periodos);
        log.info("[allFechasLecciones] fechas.size={}", fechas.size());

        List<TemaAsistencia> lecciones = temaAsistenciaDAO.allByCursoNivelacion(seccion);
        log.info("[allFechasLecciones] lecciones.size={}", lecciones.size());

        for (TemaAsistencia leccion : lecciones) {
            ControlAsistenciaDTO control = fechas.stream()
                    .filter(fec -> fec.getFecha().compareTo(leccion.getFecha()) == 0)
                    .findFirst()
                    .orElse(null);
            if (control == null) {
                fechas.add(new ControlAsistenciaDTO(leccion.getFecha(), ControlAsistenciaEstadoEnum.EJECUTADO));
            } else {
                control.setEstadoEnum(ControlAsistenciaEstadoEnum.EJECUTADO);
            }
        }

        Date hoy = new LocalDate().toDate();
        fechas.stream()
                .filter(fec -> fec.getEstadoEnum() == ControlAsistenciaEstadoEnum.SIN_CONTROL)
                .forEach(fec -> {
                    if (hoy.before(fec.getFecha())) {
                        fec.setEstadoEnum(ControlAsistenciaEstadoEnum.PENDIENTE);
                    }
                });

        Collections.sort(fechas, (f1, f2) -> f1.getFecha().compareTo(f2.getFecha()));
        return fechas;
    }

    private List<ControlAsistenciaDTO> crearFechas(List<PeriodoCantidadDiasDTO> periodos) {
        List<ControlAsistenciaDTO> fechas = new ArrayList();
        Map<Date, ControlAsistenciaDTO> mapFecha = new HashMap();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        for (PeriodoCantidadDiasDTO periodo : periodos) {
            LocalDate lunes = new LocalDate(periodo.getFechaInicio());
            LocalDate domingo = new LocalDate(periodo.getFechaFin());

            log.info("[crearFechas] lunes={} domingo={} dias={}",
                    sdf.format(lunes.toDate()), sdf.format(domingo.toDate()), periodo.getDiasSemanas());

            while (!lunes.isAfter(domingo)) {
                int diaSemana = lunes.getDayOfWeek();
                if (periodo.getDiasSemanas().contains(diaSemana)) {
                    Date fecha = lunes.toDate();
                    ControlAsistenciaDTO control = mapFecha.get(fecha);
                    if (control == null) {
                        control = new ControlAsistenciaDTO(fecha, ControlAsistenciaEstadoEnum.SIN_CONTROL);
                        mapFecha.put(fecha, control);
                        fechas.add(control);
                    }
                }
                lunes = lunes.plusDays(1);
            }
        }
        return fechas;
    }

    @Override
    @Transactional
    public TemaAsistencia crearLeccion(TemaAsistencia form, Docente docente, CicloAcademico ciclo, DataSessionPivot ds) {
        Assert.isNotNull(form.getTemaClase(), "No ha indicado el tema de la lección");
        Assert.isNotNull(form.getFecha(), "No ha indicado la fecha");
        Assert.isNotNull(form.getCursoNivelacion(), "No ha indicado la sección");
        Assert.isNotNull(form.getCursoNivelacion().getId(), "No ha indicado la sección");

        CursoNivelacion seccion = this.findSeccion(form.getCursoNivelacion(), docente, ciclo);
        TemaAsistencia tema = temaAsistenciaDAO.findByCursoNivelacionFecha(seccion, form.getFecha());
        Assert.isNull(tema, "Ya existe una lección registrada con esta fecha");

        List<NotaAlumnoNivelacion> inscritos = notaAlumnoNivelacionDAO.allInscritosByCursoNivelacion(seccion);
        Assert.isFalse(inscritos.isEmpty(), "No existe alumnos inscritos en esta sección");

        LocalDate hoy = new LocalDate();
        LocalDate fecha = new LocalDate(form.getFecha());
        if (noEsModoPruebas(form)) {
            Assert.isTrue(hoy.toDate().compareTo(form.getFecha()) >= 0, "No se puede crear lecciones de fechas futuras");
        }

        List<HorarioAula> horariosAll = horarioAulaDAO.allByCursoNivelacionFecha(seccion, form.getFecha());
        int diaSemana = fecha.getDayOfWeek();
        List<Hora> horas = horariosAll.stream()
                .filter(hor -> hor.getDia().getNumeroDia() == diaSemana)
                .map(hor -> hor.getHora())
                .distinct()
                .collect(Collectors.toList());
        Assert.isFalse(horas.isEmpty(), "No existe horas programadas para esta fecha");

        Collections.sort(horas, (h1, h2) -> h1.getNumero().compareTo(h2.getNumero()));
        if (hoy.equals(fecha) && noEsModoPruebas(form)) {
            int horaMin = horas.get(0).getNumero();
            int horaHoy = new DateTime().getHourOfDay();
            Assert.isTrue(horaHoy >= horaMin, "No puede marcar la asistencia antes de la hora programada");
        }

        tema = new TemaAsistencia();
        tema.setCursoNivelacion(seccion);
        tema.setHoraInicio(horas.get(0));
        tema.setCantidadHoras(horas.size());
        tema.setFecha(form.getFecha());
        tema.setTemaClase(form.getTemaClase());
        tema.setInscritos(inscritos.size());
        tema.setAsistentes(inscritos.size());
        tema.setFaltantes(0);
        tema.setUserRegistro(ds.getUsuario());
        tema.setFechaRegistro(new Date());
        temaAsistenciaDAO.save(tema);

        for (Hora hora : horas) {
            for (NotaAlumnoNivelacion inscrito : inscritos) {
                AsistenciaNivelacion asistencia = new AsistenciaNivelacion();
                asistencia.setAlumnoNivelacion(inscrito.getAlumnoNivelacion());
                asistencia.setTemaAsistencia(tema);
                asistencia.setHora(hora);
                asistencia.setEstadoEnum(AsistenciaClasesEstadoEnum.ASISTIO);
                asistencia.setUserRegistro(ds.getUsuario());
                asistencia.setFechaRegistro(new Date());
                asistenciaNivelacionDAO.save(asistencia);
            }
        }

        return tema;
    }

    private boolean noEsModoPruebas(TemaAsistencia form) {
        if (despliegueConfig.isProduccion()) {
            return true;
        }
        if (form.getModoPrueba() == null) {
            return true;
        }
        return form.getModoPrueba() != 1;
    }

}
