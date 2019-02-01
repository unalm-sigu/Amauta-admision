package pe.edu.lamolina.pivot.controller.rolexamen.rolexamenes;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.EventoCicloAcademico;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.EstadoHorarioAulaEnum;
import pe.edu.lamolina.model.enums.RolExamenesEstadoEnum;
import pe.edu.lamolina.model.enums.SituacionRolExamenesEnum;
import pe.edu.lamolina.model.enums.TipoHorarioAulaEnum;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioAula;
import pe.edu.lamolina.model.rolexamen.AulaCursoMasivo;
import pe.edu.lamolina.model.rolexamen.CursoMasivoExamen;
import pe.edu.lamolina.model.rolexamen.FechaHoraGrupoExamen;
import pe.edu.lamolina.model.rolexamen.GrupoHorasExamen;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionCursoMasivo;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoEspecial;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoRegular;
import pe.edu.lamolina.model.rolexamen.SemanaExamen;
import pe.edu.lamolina.pivot.controller.rolexamen.cursomasivos.CursoMasivosService;
import pe.edu.lamolina.pivot.controller.rolexamen.grupoespecial.GrupoEspecialService;
import pe.edu.lamolina.pivot.controller.rolexamen.gruporegular.GrupoRegularService;
import pe.edu.lamolina.pivot.controller.rolexamen.plantillahorario.PlantillaHorarioService;
import pe.edu.lamolina.pivot.dao.academico.EventoCicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.horario.HoraDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioAulaDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.AulaCursoMasivoDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.FechaHoraGrupoExamenDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.GrupoHorasExamenDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.RolExamenesDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.SeccionCursoMasivoDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.SeccionExcluidoDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.SeccionGrupoEspecialDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.SeccionGrupoRegularDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.SemanaExamenDAO;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class RolExamenesServiceImp implements RolExamenesService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    GrupoEspecialService grupoEspecialService;

    @Autowired
    GrupoRegularService grupoRegularService;

    @Autowired
    CursoMasivosService cursoMasivosService;

    @Autowired
    PlantillaHorarioService plantillaHorarioService;

    @Autowired
    RolExamenesDAO rolexamenesDAO;

    @Autowired
    EventoCicloAcademicoDAO eventoCicloAcademicoDAO;

    @Autowired
    HoraDAO horaDAO;

    @Autowired
    SemanaExamenDAO semanaExamenDAO;

    @Autowired
    SeccionExcluidoDAO seccionExcluidoDAO;

    @Autowired
    FechaHoraGrupoExamenDAO fechaHoraGrupoExamenDAO;

    @Autowired
    GrupoHorasExamenDAO grupoHorasExamenDAO;

    @Autowired
    SeccionCursoMasivoDAO seccionCursoMasivoDAO;

    @Autowired
    HorarioAulaDAO horarioAulaDAO;

    @Autowired
    SeccionGrupoRegularDAO seccionGrupoRegularDAO;

    @Autowired
    SeccionGrupoEspecialDAO seccionGrupoEspecialDAO;

    @Autowired
    AulaCursoMasivoDAO aulaCursoMasivoDAO;

    @Override
    public RolExamenes findRolExamenes(long rolExamenId) {
        RolExamenes rolExamenes = rolexamenesDAO.find(rolExamenId);
        List<SemanaExamen> semanaExamens = semanaExamenDAO.allByRolExamenes(rolExamenes);
        rolExamenes.setSemanasExamen(semanaExamens);
        return rolExamenes;
    }

    @Override
    public List<EventoCicloAcademico> allEventoCicloAcademicos(CicloAcademico cicloAcademico) {
        List<EventoCicloAcademico> eventoCicloAcademicos = eventoCicloAcademicoDAO.allEventoCicloAcademicos(cicloAcademico);
        return eventoCicloAcademicos;
    }

    @Override
    public List<RolExamenes> allRolExamenes(DynatableFilter filter, CicloAcademico cicloAcademico) {
        return rolexamenesDAO.allByDynatable(filter, cicloAcademico);
    }

    @Override
    @Transactional
    public void save(RolExamenes rolExamenes, DataSessionPivot ds) {
        rolExamenes.setEstadoEnum(RolExamenesEstadoEnum.CRE);
        rolExamenes.setFechaRegistro(new Date());
        rolExamenes.setUserRegistro(ds.getUsuario());
        rolExamenes.setHorasExamen(Constantine.CANTIDAD_HORAS_POR_EXAMEN);
        rolExamenes.setSituacionEnum(SituacionRolExamenesEnum.CONF_ROL);

        List<String> errors = new ArrayList<>();
        for (SemanaExamen semanaExamen : rolExamenes.getSemanasExamen()) {
            if (semanaExamen.getHoraInicio().getNumero() >= semanaExamen.getHoraFin().getNumero()) {
                String message = String.format("Semana %s, hora inicio debe ser menor que hora fin.", semanaExamen.getNumeroSemana());
                errors.add(message);
            }
            semanaExamen.setRolExamenes(rolExamenes);
        }
        Assert.isTrue(errors.isEmpty(), String.join("<br/>", errors));

        rolexamenesDAO.save(rolExamenes);
    }

    @Override
    @Transactional
    public void update(RolExamenes rolExamenes, DataSessionPivot ds) {
        RolExamenes rolBD = rolexamenesDAO.find(rolExamenes.getId());
        Assert.isTrue(rolBD.isSituacionConfigurarRol(), "No puede grabar en este momento.");

        RolExamenes rolExamenesUpd = new RolExamenes();
        rolExamenesUpd.setId(rolExamenes.getId());
        rolExamenes.setEventoCicloAcademico(rolExamenes.getEventoCicloAcademico());
        rolexamenesDAO.updateRolExamenes(rolExamenes);

        semanaExamenDAO.deleteByRolExamenes(rolExamenes);
        //    semanaExamenDAO.allByRolExamenes(rolExamenes);
        for (SemanaExamen semanaExamen : rolExamenes.getSemanasExamen()) {
            semanaExamen.setId(null);
            semanaExamen.setRolExamenes(rolExamenes);
            semanaExamenDAO.save(semanaExamen);
        }
    }

    @Override
    @Transactional
    public void publicarRolExamen(RolExamenes rolExamenes, DataSessionPivot ds) {
        rolExamenes = rolexamenesDAO.find(rolExamenes.getId());
        Assert.isTrue(rolExamenes.isSituacionConfigurarGrupoEspecial(), "No se puede publicar el rol examenes, sin configurar las secciones especiales.");
        RolExamenes rolExamenesUpd = new RolExamenes();
        rolExamenesUpd.setId(rolExamenes.getId());
        rolExamenesUpd.setEstadoEnum(RolExamenesEstadoEnum.PUB);
        rolExamenesUpd.setFechaPublicacion(ds.getFechaAccionAudit());
        rolexamenesDAO.updatePublicacion(rolExamenesUpd);
    }

    @Transactional
    @Override
    public void eliminarConfiguracion(RolExamenes rolExamenes, DataSessionPivot ds) {
        rolExamenes = rolexamenesDAO.find(rolExamenes.getId());
        Assert.isFalse(rolExamenes.isSituacionConfigurarRol(), "No tiene avance en la configuración.");

        this.deleteSeccionesExcluidasByRolExamenes(rolExamenes);
        grupoEspecialService.deleteGrupoEspecial(rolExamenes);
        grupoRegularService.deleteGrupoRegular(rolExamenes);
        cursoMasivosService.deleteCursosMasivos(rolExamenes);
        plantillaHorarioService.deletePlantillaHorario(rolExamenes);

        RolExamenes rolExamenesUpd = new RolExamenes();
        rolExamenesUpd.setId(rolExamenes.getId());
        rolExamenesUpd.setSituacionEnum(SituacionRolExamenesEnum.CONF_ROL);
        rolExamenesUpd.setEstadoEnum(RolExamenesEstadoEnum.CRE);
        rolexamenesDAO.updateEstadoAndSituacion(rolExamenesUpd);
    }

    public void deleteSeccionesExcluidasByRolExamenes(RolExamenes rolExamenes) {
        seccionExcluidoDAO.deleteByRolExamenes(rolExamenes);
    }

    @Override
    public List<Hora> allHoras() {
        return horaDAO.all();
    }

    @Override
    public List<SemanaExamen> allSemanaExamenByEventoCiclo(EventoCicloAcademico eventoCicloAcademico) {
        eventoCicloAcademico = eventoCicloAcademicoDAO.findEventoCicloAcademico(eventoCicloAcademico);
        DateTime fechaInicio = new DateTime(eventoCicloAcademico.getFechaInicio());
        DateTime fechaFin = new DateTime(eventoCicloAcademico.getFechaFin());

        Assert.isTrue(fechaInicio.getDayOfWeek() == DateTimeConstants.MONDAY, "El dia inicial del evento debe ser lunes.");
        Assert.isTrue(fechaFin.getDayOfWeek() == DateTimeConstants.SUNDAY, "El dia final del evento debe ser domingo.");

        int dias = Days.daysBetween(fechaInicio.toLocalDate(), fechaFin.toLocalDate()).getDays();
        int diasSemana = fechaInicio.dayOfWeek().withMaximumValue().getDayOfWeek();
        if (++dias % diasSemana != 0) {
            throw new PhobosException("La fecha inicio y fin programadas al evento, deben ser semanas contabilizables.");
        }
        //Weeks weeks = Weeks.weeksBetween(dateTime1.toLocalDate(), dateTime2.toLocalDate());
        int weeks = dias / diasSemana;
        List<SemanaExamen> semanasExamen = new ArrayList<>();

        DateTime lastDateOfWeek = null;
        for (int i = 1; i <= weeks; i++) {
            SemanaExamen semanaExamen = new SemanaExamen();

            semanaExamen.setNumeroSemana(i);

            DateTime fechaInicioEach = lastDateOfWeek == null ? new DateTime(fechaInicio) : lastDateOfWeek.plusDays(BigDecimal.ONE.intValue());
            semanaExamen.setFechaInicio(fechaInicioEach.toDate());

            lastDateOfWeek = fechaInicioEach.dayOfWeek().withMaximumValue();
            semanaExamen.setFechaFin(lastDateOfWeek.toDate());

            semanaExamen.setHoraInicio(new Hora());
            semanaExamen.setHoraFin(new Hora());
            semanasExamen.add(semanaExamen);
        }

        return semanasExamen;
    }

    @Override
    @Transactional
    public void fijarHorarioAula(RolExamenes rolExamenes, DataSessionPivot ds) {

        List<SemanaExamen> semanaExamenes = semanaExamenDAO.allByRolExamenes(rolExamenes);
        logger.debug("all semana examen {}", semanaExamenes.size());

        for (SemanaExamen semanaExamen : semanaExamenes) {

            List<FechaHoraGrupoExamen> fechaHoraGrupoExamenes = fechaHoraGrupoExamenDAO.allBySemanaExamen(semanaExamen);
            List<GrupoHorasExamen> grupoHorasExamenes = fechaHoraGrupoExamenes.stream().map(x -> x.getGrupoHorasExamen()).collect(Collectors.toList());

            Map<Long, List<FechaHoraGrupoExamen>> fechaHoraGrupoExamenXgrupoExamen = TypesUtil.convertListToMapList("grupoHorasExamen.id", fechaHoraGrupoExamenes);

            this.allHorarioClasesCursoMasivo(semanaExamenes, semanaExamen, grupoHorasExamenes, fechaHoraGrupoExamenXgrupoExamen);

            this.allHorarioClasesCursoRegular(semanaExamenes, semanaExamen, grupoHorasExamenes, fechaHoraGrupoExamenXgrupoExamen);

            this.allHorarioClasesCursoEspecial(semanaExamenes, semanaExamen, grupoHorasExamenes, fechaHoraGrupoExamenXgrupoExamen);

        }
    }

    public Date toDate(LocalDate dateToConvert) {

        try {
            return Date.from(dateToConvert.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public LocalDate toLocal(Date date) {

        try {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault()).toLocalDate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Transactional
    private void allHorarioClasesCursoMasivo(
            List<SemanaExamen> semanaExamenes,
            SemanaExamen semanaExamen,
            List<GrupoHorasExamen> grupoHorasExamenes,
            Map<Long, List<FechaHoraGrupoExamen>> fechaHoraGrupoExamenXgrupoExamen) {

        List<SeccionCursoMasivo> seccionCursoMasivos = seccionCursoMasivoDAO.allByGrupoHorasExamen(grupoHorasExamenes);

        List<CursoMasivoExamen> cursoMasivoExamenes = seccionCursoMasivos.stream().map(x -> x.getCursoMasivoExamen()).collect(Collectors.toList());

        List<AulaCursoMasivo> aulaCursoMasivos = aulaCursoMasivoDAO.allByCursosMasivos(cursoMasivoExamenes);

        List<Seccion> secciones = seccionCursoMasivos.stream().map(x -> x.getSeccion()).collect(Collectors.toList());

        Integer numerosemana = 1;

        for (SemanaExamen semanaExamenFree : semanaExamenes) {

            List<HorarioAula> horarioAulas = horarioAulaDAO.allHorarioClasesBySecciones(secciones, semanaExamenFree);

            for (HorarioAula horarioAula : horarioAulas) {

                boolean test = (numerosemana % 2 == 0) ? 8<= horarioAula.getHora().getNumero() && horarioAula.getHora().getNumero() <=13
                        :14<= horarioAula.getHora().getNumero()  && horarioAula.getHora().getNumero() <=18;

                if (test) {
                    continue;
                }

                Date fechaFin = horarioAula.getFechaFin();
                Date fechaInicio = horarioAula.getFechaInicio();

                if (fechaInicio.after(semanaExamenFree.getFechaInicio()) && fechaFin.before(semanaExamenFree.getFechaFin())) {
                    continue;
                }

                LocalDate fechainicioexamen = this.toLocal(semanaExamenFree.getFechaInicio());
                LocalDate fechafinfirst = fechainicioexamen.minusDays(1L);
                Date fechafin = this.toDate(fechafinfirst);

                LocalDate fechafinsemanaexamen = this.toLocal(semanaExamenFree.getFechaFin());
                LocalDate fechainiciosecond = fechafinsemanaexamen.plusDays(1L);
                Date fechainicio = this.toDate(fechainiciosecond);

                HorarioAula horarioAulaNew = new HorarioAula();

                horarioAulaNew.setFechaInicio(fechainicio);
                horarioAulaNew.setFechaFin(horarioAula.getFechaFin());

                horarioAulaNew.setAula(horarioAula.getAula());
                horarioAulaNew.setEstado(horarioAula.getEstado());
                horarioAulaNew.setReservado(horarioAula.getReservado());
                horarioAulaNew.setTipo(horarioAula.getTipo());
                horarioAulaNew.setDia(horarioAula.getDia());
                horarioAulaNew.setHora(horarioAula.getHora());
                horarioAulaNew.setSeccion(horarioAula.getSeccion());
                horarioAulaNew.setReservaAula(horarioAula.getReservaAula());
                horarioAulaNew.setCursoMasivoExamen(horarioAula.getCursoMasivoExamen());
                horarioAulaDAO.save(horarioAulaNew);
                
                horarioAula.setFechaFin(fechafin);
                horarioAulaDAO.update(horarioAula);

            }

            numerosemana++;

        }

        for (AulaCursoMasivo aulaCursoMasivo : aulaCursoMasivos) {

            GrupoHorasExamen grupoHorasExamen = aulaCursoMasivo.getCursoMasivoExamen().getGrupoHorasExamen();
            List<FechaHoraGrupoExamen> fechaHoraGrupoExamenes = fechaHoraGrupoExamenXgrupoExamen.get(grupoHorasExamen.getId());
            if (fechaHoraGrupoExamenes == null) {
                continue;
            }
            for (FechaHoraGrupoExamen fechaHoraGrupoExamene : fechaHoraGrupoExamenes) {

                HorarioAula horarioAula = new HorarioAula();
                horarioAula.setFechaInicio(semanaExamen.getFechaInicio());
                horarioAula.setFechaFin(semanaExamen.getFechaFin());

                horarioAula.setAula(aulaCursoMasivo.getAula());
                horarioAula.setCursoMasivoExamen(aulaCursoMasivo.getCursoMasivoExamen());
                horarioAula.setSeccion(null);

                horarioAula.setEstadoEnum(EstadoHorarioAulaEnum.ACT);
                horarioAula.setTipoEnum(TipoHorarioAulaEnum.EXAM);
                horarioAula.setDia(fechaHoraGrupoExamene.getDia());
                horarioAula.setHora(fechaHoraGrupoExamene.getHora());
                horarioAulaDAO.save(horarioAula);

            }
        }

    }

    @Transactional
    private void allHorarioClasesCursoRegular(
            List<SemanaExamen> semanaExamenes,
            SemanaExamen semanaExamen,
            List<GrupoHorasExamen> grupoHorasExamenes,
            Map<Long, List<FechaHoraGrupoExamen>> fechaHoraGrupoExamenXgrupoExamen) {

        List<SeccionGrupoRegular> seccionGrupoRegulares = seccionGrupoRegularDAO.allByGrupoHorasExamen(grupoHorasExamenes);

        List<Seccion> secciones = seccionGrupoRegulares.stream().map(x -> x.getSeccion()).collect(Collectors.toList());

        Integer numerosemana = 1;

        for (SemanaExamen semanaExamenFree : semanaExamenes) {

            List<HorarioAula> horarioAulas = horarioAulaDAO.allHorarioClasesBySecciones(secciones, semanaExamenFree);

            for (HorarioAula horarioAula : horarioAulas) {

                 boolean test = (numerosemana % 2 == 0) ? 8<= horarioAula.getHora().getNumero() && horarioAula.getHora().getNumero() <=13
                        :14<= horarioAula.getHora().getNumero()  && horarioAula.getHora().getNumero() <=18;

                if (test) {
                    continue;
                }

                Date fechaFin = horarioAula.getFechaFin();
                Date fechaInicio = horarioAula.getFechaInicio();

                if (fechaInicio.after(semanaExamenFree.getFechaInicio()) && fechaFin.before(semanaExamenFree.getFechaFin())) {
                    continue;
                }

                LocalDate fechainicioexamen = this.toLocal(semanaExamenFree.getFechaInicio());
                LocalDate fechafinfirst = fechainicioexamen.minusDays(1L);
                Date fechafin = this.toDate(fechafinfirst);

                LocalDate fechafinsemanaexamen = this.toLocal(semanaExamenFree.getFechaFin());
                LocalDate fechainiciosecond = fechafinsemanaexamen.plusDays(1L);
                Date fechainicio = this.toDate(fechainiciosecond);

                HorarioAula horarioAulaNew = new HorarioAula();

                horarioAulaNew.setFechaInicio(fechainicio);
                horarioAulaNew.setFechaFin(horarioAula.getFechaFin());

                horarioAulaNew.setAula(horarioAula.getAula());
                horarioAulaNew.setEstado(horarioAula.getEstado());
                horarioAulaNew.setReservado(horarioAula.getReservado());
                horarioAulaNew.setTipo(horarioAula.getTipo());
                horarioAulaNew.setDia(horarioAula.getDia());
                horarioAulaNew.setHora(horarioAula.getHora());
                horarioAulaNew.setSeccion(horarioAula.getSeccion());
                horarioAulaNew.setReservaAula(horarioAula.getReservaAula());
                horarioAulaNew.setCursoMasivoExamen(horarioAula.getCursoMasivoExamen());

                horarioAulaDAO.save(horarioAulaNew);

                horarioAula.setFechaFin(fechafin);
                horarioAulaDAO.update(horarioAula);

            }

            numerosemana++;
        }

        for (SeccionGrupoRegular seccionGrupoRegulare : seccionGrupoRegulares) {

            GrupoHorasExamen grupoHorasExamen = seccionGrupoRegulare.getLetraGrupoRegular().getGrupoHorasExamen();
            List<FechaHoraGrupoExamen> fechaHoraGrupoExamenes = fechaHoraGrupoExamenXgrupoExamen.get(grupoHorasExamen.getId());
            if (fechaHoraGrupoExamenes == null) {
                continue;
            }
            for (FechaHoraGrupoExamen fechaHoraGrupoExamene : fechaHoraGrupoExamenes) {

                HorarioAula horarioAula = new HorarioAula();

                horarioAula.setFechaInicio(semanaExamen.getFechaInicio());
                horarioAula.setFechaFin(semanaExamen.getFechaFin());

                horarioAula.setAula(seccionGrupoRegulare.getAula());
                horarioAula.setCursoMasivoExamen(null);
                horarioAula.setSeccion(seccionGrupoRegulare.getSeccion());

                horarioAula.setEstadoEnum(EstadoHorarioAulaEnum.ACT);
                horarioAula.setTipoEnum(TipoHorarioAulaEnum.EXAM);
                horarioAula.setDia(fechaHoraGrupoExamene.getDia());
                horarioAula.setHora(fechaHoraGrupoExamene.getHora());
                horarioAulaDAO.save(horarioAula);

            }

        }

    }

    @Transactional
    private void allHorarioClasesCursoEspecial(
            List<SemanaExamen> semanaExamenes,
            SemanaExamen semanaExamen,
            List<GrupoHorasExamen> grupoHorasExamenes,
            Map<Long, List<FechaHoraGrupoExamen>> fechaHoraGrupoExamenXgrupoExamen) {

        List<SeccionGrupoEspecial> seccionGrupoEspeciales = seccionGrupoEspecialDAO.allByGrupoHorasExamen(grupoHorasExamenes);

        List<Seccion> secciones = seccionGrupoEspeciales.stream().map(x -> x.getSeccion()).collect(Collectors.toList());

        Integer numerosemana = 1;

        for (SemanaExamen semanaExamenFree : semanaExamenes) {

            List<HorarioAula> horarioAulas = horarioAulaDAO.allHorarioClasesBySecciones(secciones, semanaExamenFree);

            for (HorarioAula horarioAula : horarioAulas) {

                boolean test = (numerosemana % 2 == 0) ? 8<= horarioAula.getHora().getNumero() && horarioAula.getHora().getNumero() <=13
                        :14<= horarioAula.getHora().getNumero()  && horarioAula.getHora().getNumero() <=18;

                if (test) {
                    continue;
                }

                Date fechaFin = horarioAula.getFechaFin();
                Date fechaInicio = horarioAula.getFechaInicio();

                if (fechaInicio.after(semanaExamenFree.getFechaInicio()) && fechaFin.before(semanaExamenFree.getFechaFin())) {
                    continue;
                }

                LocalDate fechainicioexamen = this.toLocal(semanaExamenFree.getFechaInicio());
                LocalDate fechafinfirst = fechainicioexamen.minusDays(1L);
                Date fechafin = this.toDate(fechafinfirst);

                LocalDate fechafinsemanaexamen = this.toLocal(semanaExamenFree.getFechaFin());
                LocalDate fechainiciosecond = fechafinsemanaexamen.plusDays(1L);
                Date fechainicio = this.toDate(fechainiciosecond);

                HorarioAula horarioAulaNew = new HorarioAula();

                horarioAulaNew.setFechaInicio(fechainicio);
                horarioAulaNew.setFechaFin(horarioAula.getFechaFin());

                horarioAulaNew.setAula(horarioAula.getAula());
                horarioAulaNew.setEstado(horarioAula.getEstado());
                horarioAulaNew.setReservado(horarioAula.getReservado());
                horarioAulaNew.setTipo(horarioAula.getTipo());
                horarioAulaNew.setDia(horarioAula.getDia());
                horarioAulaNew.setHora(horarioAula.getHora());
                horarioAulaNew.setSeccion(horarioAula.getSeccion());
                horarioAulaNew.setReservaAula(horarioAula.getReservaAula());
                horarioAulaNew.setCursoMasivoExamen(horarioAula.getCursoMasivoExamen());

                horarioAulaDAO.save(horarioAulaNew);

                horarioAula.setFechaFin(fechafin);
                horarioAulaDAO.update(horarioAula);

            }

            numerosemana++;
        }

        for (SeccionGrupoEspecial seccionGrupoEspeciale : seccionGrupoEspeciales) {

            GrupoHorasExamen grupoHorasExamen = seccionGrupoEspeciale.getGrupoHorasExamen();
            List<FechaHoraGrupoExamen> fechaHoraGrupoExamenes = fechaHoraGrupoExamenXgrupoExamen.get(grupoHorasExamen.getId());
            if (fechaHoraGrupoExamenes == null) {
                continue;
            }

            for (FechaHoraGrupoExamen fechaHoraGrupoExamene : fechaHoraGrupoExamenes) {

                HorarioAula horarioAula = new HorarioAula();
                horarioAula.setFechaInicio(semanaExamen.getFechaInicio());
                horarioAula.setFechaFin(semanaExamen.getFechaFin());

                horarioAula.setAula(seccionGrupoEspeciale.getAula());
                horarioAula.setCursoMasivoExamen(null);
                horarioAula.setSeccion(seccionGrupoEspeciale.getSeccion());

                horarioAula.setEstadoEnum(EstadoHorarioAulaEnum.ACT);
                horarioAula.setTipoEnum(TipoHorarioAulaEnum.EXAM);
                horarioAula.setDia(fechaHoraGrupoExamene.getDia());
                horarioAula.setHora(fechaHoraGrupoExamene.getHora());
                horarioAulaDAO.save(horarioAula);

            }
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void cerrar(RolExamenes rolExamenes, DataSessionPivot ds) {
        RolExamenes rolBD = rolexamenesDAO.find(rolExamenes.getId());
        if (rolBD.isEstadoCerrado()) {
            return;
        }

        Assert.isTrue(rolBD.isEstadoPublicado(), "El rol de examenes aun no ha sido publicado");

        List<SemanaExamen> semanas = semanaExamenDAO.allByRolExamenes(rolExamenes);

        Date fechaMax = null;

        for (SemanaExamen semana : semanas) {
            if (fechaMax == null || fechaMax.compareTo(semana.getFechaFin()) < 0) {
                fechaMax = semana.getFechaFin();
            }
        }

        if (fechaMax == null) {
            throw new PhobosException();
        }

        org.joda.time.LocalDate date = new org.joda.time.LocalDate(fechaMax);
        org.joda.time.LocalDate today = org.joda.time.LocalDate.now();
        logger.debug("Comparando {} con {}", date, today);

        Assert.isTrue(date.isBefore(today), "La semana de examenes aun no ha finalizado");

        rolBD.setEstadoEnum(RolExamenesEstadoEnum.CER);
        rolexamenesDAO.update(rolBD);
    }

    @Override
    @Transactional
    public void modificar(RolExamenes rolExamenes, DataSessionPivot ds) {
        RolExamenes rolBD = rolexamenesDAO.find(rolExamenes.getId());
        if (rolBD.isEstadoCerrado()) {
            return;
        }

        rolBD.setEstadoEnum(RolExamenesEstadoEnum.MOD);
        rolexamenesDAO.update(rolBD);
    }

}
