package pe.edu.lamolina.pivot.controller.rolexamen.rolexamenes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.EventoCicloAcademico;
import pe.edu.lamolina.model.enums.RolExamenesEstadoEnum;
import pe.edu.lamolina.model.enums.SituacionRolExamenesEnum;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.rolexamen.CursoMasivoExamen;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SemanaExamen;
import pe.edu.lamolina.pivot.controller.rolexamen.cursomasivos.CursoMasivosService;
import pe.edu.lamolina.pivot.controller.rolexamen.grupoespecial.GrupoEspecialService;
import pe.edu.lamolina.pivot.controller.rolexamen.gruporegular.GrupoRegularService;
import pe.edu.lamolina.pivot.controller.rolexamen.plantillahorario.PlantillaHorarioService;
import pe.edu.lamolina.pivot.dao.academico.EventoCicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.horario.HoraDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.RolExamenesDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.SeccionExcluidoDAO;
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

}
