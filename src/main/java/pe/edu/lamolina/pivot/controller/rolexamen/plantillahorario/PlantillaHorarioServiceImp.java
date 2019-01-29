package pe.edu.lamolina.pivot.controller.rolexamen.plantillahorario;

import java.util.ArrayList;
import java.util.Collections;
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
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.RolExamenesEstadoEnum;
import pe.edu.lamolina.model.enums.SituacionRolExamenesEnum;
import pe.edu.lamolina.model.enums.TipoGrupoHorasEnum;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.horario.DiaHoraGrupo;
import pe.edu.lamolina.model.horario.GrupoHoras;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.rolexamen.FechaHoraGrupoExamen;
import pe.edu.lamolina.model.rolexamen.GrupoHorasExamen;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SemanaExamen;
import pe.edu.lamolina.pivot.dao.general.DiaDAO;
import pe.edu.lamolina.pivot.dao.horario.DiaHoraGrupoDAO;
import pe.edu.lamolina.pivot.dao.horario.GrupoHorasDAO;
import pe.edu.lamolina.pivot.dao.horario.HoraDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.FechaHoraGrupoExamenDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.GrupoHorasExamenDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.RolExamenesDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.SemanaExamenDAO;

@Service
@Transactional(readOnly = true)
public class PlantillaHorarioServiceImp implements PlantillaHorarioService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    RolExamenesDAO rolExamenesDAO;

    @Autowired
    SemanaExamenDAO semanaExamenDAO;

    @Autowired
    GrupoHorasDAO grupoHorasDAO;

    @Autowired
    DiaHoraGrupoDAO diaHoraGrupoDAO;

    @Autowired
    GrupoHorasExamenDAO grupoHorasExamenDAO;

    @Autowired
    FechaHoraGrupoExamenDAO fechaHoraGrupoExamenDAO;

    @Autowired
    DiaDAO diaDAO;

    @Autowired
    HoraDAO horaDAO;

    private void checkEstadoPublicado(RolExamenes rol) {
        Assert.isTrue(rol.getEstadoEnum() != RolExamenesEstadoEnum.PUB, "El rol de examanes ya ha sido publicado");
    }

    @Override
    public List<RolExamenes> allRolExamenesActives(CicloAcademico cicloAcademico) {
        return rolExamenesDAO.allActiveByCiclo(cicloAcademico);
    }

    @Override
    public RolExamenes findRolExamenes(RolExamenes rolExamenes) {
        rolExamenes = rolExamenesDAO.find(rolExamenes.getId());
        List<SemanaExamen> semanasExamen = semanaExamenDAO.allByRolExamenes(rolExamenes);
        rolExamenes.setSemanasExamen(semanasExamen);
        return rolExamenes;
    }

    @Override
    @Transactional(readOnly = false)
    public void calcularPlantillaHorario(RolExamenes rolExamenes) {
        RolExamenes rolBD = rolExamenesDAO.find(rolExamenes.getId());
        checkEstadoPublicado(rolBD);

        this.deletePlantillaHorario(rolExamenes);
        List<SemanaExamen> semanas = semanaExamenDAO.allByRolExamenes(rolExamenes);
        List<Hora> horas = horaDAO.all();

        for (SemanaExamen semana : semanas) {
            logger.debug("######################################################");
            logger.debug("CALCULAR PLANTILLA HORARIO DE LA SEMANA " + semana.getNumeroSemana());
            this.calcularPlantillaHorario(semana, horas);
        }
        RolExamenes rolExamenesUpd = new RolExamenes(rolExamenes.getId());
        rolExamenesUpd.setSituacionEnum(SituacionRolExamenesEnum.CONF_HOR);
        rolExamenesDAO.updateSituacion(rolExamenesUpd);
    }

    public void calcularPlantillaHorario(SemanaExamen semanaExamen, List<Hora> horas) {
        List<GrupoHoras> gruposHoras = this.allGrupoHorasBySemanaExamen(semanaExamen);
        //  List<GrupoHorasExamen> gruposHorasExamenes = new ArrayList<>();
        for (GrupoHoras gruposHora : gruposHoras) {
            GrupoHorasExamen grupoHorasExamen = new GrupoHorasExamen();
            grupoHorasExamen.setGrupoHoras(gruposHora);
            grupoHorasExamen.setRolExamenes(semanaExamen.getRolExamenes());
            grupoHorasExamen.setVerificado(Boolean.FALSE);

            grupoHorasExamen.setFechasHorasGruposExamen(new ArrayList<>());
            for (DiaHoraGrupo diaHoraGrupo : gruposHora.getDiaHoraGrupo()) {
                FechaHoraGrupoExamen fechaHoraGrupoExamen = new FechaHoraGrupoExamen();
                fechaHoraGrupoExamen.setGrupoHorasExamen(grupoHorasExamen);
                fechaHoraGrupoExamen.setDia(diaHoraGrupo.getDia());
                fechaHoraGrupoExamen.setHora(diaHoraGrupo.getHora());
                fechaHoraGrupoExamen.setSemanaExamen(semanaExamen);

                DateTime fechaInicio = new DateTime(semanaExamen.getFechaInicio());
                DateTime fecha = fechaInicio.withDayOfWeek(fechaHoraGrupoExamen.getDia().getNumeroDia());
                fechaHoraGrupoExamen.setFecha(fecha.toDate());

                if (grupoHorasExamen.getFecha() == null) {
                    grupoHorasExamen.setFecha(fecha.toDate());
                    grupoHorasExamen.setDia(fechaHoraGrupoExamen.getDia());
                    grupoHorasExamen.setHoraInicio(fechaHoraGrupoExamen.getHora());
                }

                if (grupoHorasExamen.getFechasHorasGruposExamen().size() < semanaExamen.getRolExamenes().getHorasExamen()) {
                    Hora horaFinalVisual = horas.stream().filter(x -> x.getNumero().compareTo(fechaHoraGrupoExamen.getHora().getNumero() + 1) == 0).findFirst().orElse(null);
                    grupoHorasExamen.setHoraFin(horaFinalVisual);
                    grupoHorasExamen.getFechasHorasGruposExamen().add(fechaHoraGrupoExamen);
                }
            }
            if (grupoHorasExamen.getFechasHorasGruposExamen().size() == grupoHorasExamen.getRolExamenes().getHorasExamen()) {
                grupoHorasExamen.setVerificado(Boolean.TRUE);
            }
            GrupoHorasExamen grupoHorasExamenFound = grupoHorasExamenDAO.findByRolExamenAndGrupoHoras(semanaExamen.getRolExamenes(), gruposHora);
            if (grupoHorasExamenFound == null) {
                grupoHorasExamenDAO.save(grupoHorasExamen);
            }
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void agregarFechoHoraGrupoExamen(FechaHoraGrupoExamen fechaHoraGrupoExamen) {
        SemanaExamen semanaExamen = semanaExamenDAO.find(fechaHoraGrupoExamen.getSemanaExamen().getId());
        fechaHoraGrupoExamen.setSemanaExamen(semanaExamen);

        checkEstadoPublicado(semanaExamen.getRolExamenes());

        List<FechaHoraGrupoExamen> fechasHorasGrupos = fechaHoraGrupoExamenDAO.allByGrupoHorasExamen(fechaHoraGrupoExamen.getGrupoHorasExamen());
        if (fechasHorasGrupos.size() >= semanaExamen.getRolExamenes().getHorasExamen()) {
            throw new PhobosException("No puede programar mas horas.");
        }

        DateTime fechaInicio = new DateTime(fechaHoraGrupoExamen.getSemanaExamen().getFechaInicio());
        DateTime fecha = fechaInicio.withDayOfWeek(fechaHoraGrupoExamen.getDia().getNumeroDia());
        fechaHoraGrupoExamen.setFecha(fecha.toDate());
        fechaHoraGrupoExamenDAO.save(fechaHoraGrupoExamen);

        this.actualizarFechaGrupoHorasExamen(fechaHoraGrupoExamen.getGrupoHorasExamen(), semanaExamen.getRolExamenes());

    }

    public void actualizarFechaGrupoHorasExamen(GrupoHorasExamen grupoHorasExamen, RolExamenes rolExamenes) {
        List<FechaHoraGrupoExamen> fechasHorasGrupos = fechaHoraGrupoExamenDAO.allByGrupoHorasExamenOrderByDiaHora(grupoHorasExamen);

        if (fechasHorasGrupos.isEmpty()) {
            GrupoHorasExamen grupoHorasExamenUpd = new GrupoHorasExamen();
            grupoHorasExamenUpd.setId(grupoHorasExamen.getId());
            grupoHorasExamenUpd.setFecha(null);
            grupoHorasExamenUpd.setDia(null);
            grupoHorasExamenUpd.setHoraInicio(null);
            grupoHorasExamenUpd.setHoraFin(null);
            grupoHorasExamenDAO.updateFechaExamen(grupoHorasExamenUpd);
        } else {
            DateTime fechaInicio = new DateTime(fechasHorasGrupos.get(0).getSemanaExamen().getFechaInicio());
            DateTime fecha = fechaInicio.withDayOfWeek(fechasHorasGrupos.get(0).getDia().getNumeroDia());

            GrupoHorasExamen grupoHorasExamenUpd = new GrupoHorasExamen();
            grupoHorasExamenUpd.setId(grupoHorasExamen.getId());
            grupoHorasExamenUpd.setFecha(fecha.toDate());
            grupoHorasExamenUpd.setDia(fechasHorasGrupos.get(0).getDia());
            grupoHorasExamenUpd.setHoraInicio(fechasHorasGrupos.get(0).getHora());
            grupoHorasExamenUpd.setHoraFin(fechasHorasGrupos.get(fechasHorasGrupos.size() - 1).getHora());

            Hora horaVisual = horaDAO.findByNumeroHora(grupoHorasExamenUpd.getHoraFin().getNumero() + 1);
            grupoHorasExamenUpd.setHoraFin(horaVisual);

            grupoHorasExamenDAO.updateFechaExamen(grupoHorasExamenUpd);
        }
        fechasHorasGrupos = fechaHoraGrupoExamenDAO.allByGrupoHorasExamenOrderByDiaHora(grupoHorasExamen);

        GrupoHorasExamen grupoHorasExamenUpd = new GrupoHorasExamen();
        grupoHorasExamenUpd.setId(grupoHorasExamen.getId());
        if (fechasHorasGrupos.size() == rolExamenes.getHorasExamen()) {
            grupoHorasExamenUpd.setVerificado(Boolean.TRUE);
        } else {
            grupoHorasExamenUpd.setVerificado(Boolean.FALSE);
        }
        grupoHorasExamenDAO.updateVerificado(grupoHorasExamenUpd);
    }

    @Override
    public void deletePlantillaHorario(RolExamenes rolExamenes) {
        RolExamenes rolBD = rolExamenesDAO.find(rolExamenes.getId());
        checkEstadoPublicado(rolBD);

        List<GrupoHorasExamen> gruposHoras = grupoHorasExamenDAO.allByRolExamenes(rolExamenes);
        List<SemanaExamen> semanasExamenes = semanaExamenDAO.allByRolExamenes(rolExamenes);

        for (SemanaExamen semanasExamene : semanasExamenes) {
            List<FechaHoraGrupoExamen> fechasHorasGruposExamen = fechaHoraGrupoExamenDAO.allBySemanaExamen(semanasExamene);
            for (FechaHoraGrupoExamen fechaHoraGrupoExamen : fechasHorasGruposExamen) {
                fechaHoraGrupoExamenDAO.delete(fechaHoraGrupoExamen);
            }
            // semanaExamenDAO.delete(semanasExamene);
        }
        for (GrupoHorasExamen gruposHora : gruposHoras) {
            grupoHorasExamenDAO.delete(gruposHora);
        }
    }

    @Override
    public List<GrupoHoras> allGrupoHorasBySemanaExamen(SemanaExamen semanaExamen) {
        semanaExamen = semanaExamenDAO.find(semanaExamen.getId());
        RolExamenes rolExamenes = rolExamenesDAO.find(semanaExamen.getRolExamenes().getId());

        Map<Long, Long> groupsAndDays = grupoHorasDAO.allGruposCountBySemanaExamen(semanaExamen,
                rolExamenes.getEventoCicloAcademico().getCicloAcademico(), TipoGrupoHorasEnum.REGULAR,
                rolExamenes.getHorasExamen());
        List<Long> gruposIdsFiltered = groupsAndDays.entrySet().stream()
                .map(x -> x.getKey()).collect(Collectors.toList());

        List<GrupoHoras> gruposHoras = grupoHorasDAO.allGrupoHoras(gruposIdsFiltered);
        final int HORA_INICIO = semanaExamen.getHoraInicio().getNumero();
        final int HORA_FIN = semanaExamen.getHoraFin().getNumero();
        for (GrupoHoras gruposHora : gruposHoras) {
            List<DiaHoraGrupo> diasHorasGrupo = diaHoraGrupoDAO.allByGrupoCiclo(gruposHora, rolExamenes.getEventoCicloAcademico().getCicloAcademico());
            diasHorasGrupo = diasHorasGrupo.stream()
                    .filter(x -> x.getDia().getId().compareTo(groupsAndDays.get(gruposHora.getId())) == 0)
                    .filter(x -> x.getHora().getNumero() >= HORA_INICIO)
                    .filter(x -> x.getHora().getNumero() <= HORA_FIN)
                    .collect(Collectors.toList());
            Collections.sort(diasHorasGrupo, (p1, p2) -> p1.getHora().getNumero().compareTo(p2.getHora().getNumero()));
            gruposHora.setDiaHoraGrupo(diasHorasGrupo);
        }
        return gruposHoras;
    }

    @Override
    public List<GrupoHorasExamen> allGrupoHorasExamenByRolExamen(RolExamenes rolExamenes, DynatableFilter filter) {
        List<GrupoHorasExamen> gruposHorasExamenes = grupoHorasExamenDAO.allByRolExamenesAndDyna(rolExamenes, filter);

        for (GrupoHorasExamen gruposHora : gruposHorasExamenes) {
            List<FechaHoraGrupoExamen> fechasHorasGrupoExamen = fechaHoraGrupoExamenDAO.allByGrupoHorasExamen(gruposHora);
            gruposHora.setFechasHorasGruposExamen(fechasHorasGrupoExamen);
            gruposHora.setSemanaExamen(null);
            if (!fechasHorasGrupoExamen.isEmpty()) {
                gruposHora.setSemanaExamen(fechasHorasGrupoExamen.get(0).getSemanaExamen());
            }
        }
        return gruposHorasExamenes;
    }

    @Override
    public GrupoHorasExamen findGrupoHorasExamen(GrupoHorasExamen grupoHorasExamen) {
        grupoHorasExamen = grupoHorasExamenDAO.find(grupoHorasExamen.getId());
        List<FechaHoraGrupoExamen> fechasHorasGrupoExamen = fechaHoraGrupoExamenDAO.allByGrupoHorasExamen(grupoHorasExamen);
        grupoHorasExamen.setFechasHorasGruposExamen(fechasHorasGrupoExamen);
        grupoHorasExamen.setSemanaExamen(null);
        if (!fechasHorasGrupoExamen.isEmpty()) {
            grupoHorasExamen.setSemanaExamen(fechasHorasGrupoExamen.get(0).getSemanaExamen());
        }
        return grupoHorasExamen;
    }

    /*
    public List<GrupoHorasExamen> allGrupoHorasExamenBySemanaExamen(SemanaExamen semanaExamen, DynatableFilter filter) {
        List<GrupoHorasExamen> gruposHorasExamenes = grupoHorasExamenDAO.allByRolExamenesAndDyna(rolExamenes, filter);
        for (GrupoHorasExamen gruposHora : gruposHorasExamenes) {
            List<FechaHoraGrupoExamen> fechasHorasGrupoExamen = fechaHoraGrupoExamenDAO.allByGrupoHorasExamen(gruposHora);
            gruposHora.setFechasHorasGruposExamen(fechasHorasGrupoExamen);
        }
        return gruposHorasExamenes;
    }
     */
    public List<SemanaExamen> allSemanasExamenByRolExamenes(RolExamenes rolExamenes) {
        List<FechaHoraGrupoExamen> fechasHorasGrupoExamen = fechaHoraGrupoExamenDAO.allByRolExamens(rolExamenes);

        List<SemanaExamen> semanasExamen = new ArrayList<>();
        fechasHorasGrupoExamen.forEach(x -> {
            if (!semanasExamen.contains(x.getSemanaExamen())) {
                semanasExamen.add(x.getSemanaExamen());
            }
        });
        List<GrupoHorasExamen> gruposHorasExamen = new ArrayList<>();
        fechasHorasGrupoExamen.forEach(x -> {
            if (!gruposHorasExamen.contains(x.getGrupoHorasExamen())) {
                gruposHorasExamen.add(x.getGrupoHorasExamen());
            }
        });

        return null;
    }

    @Override
    public List<Dia> allDias() {
        return diaDAO.all();
    }

    @Override
    public List<Hora> allHoras() {
        return horaDAO.all();
    }

    @Override
    public List<FechaHoraGrupoExamen> allFechaHoraGrupoExamenBySemanaExamen(SemanaExamen semanaExamen) {
        return fechaHoraGrupoExamenDAO.allBySemanaExamen(semanaExamen);
    }

    @Override
    public List<FechaHoraGrupoExamen> allFechaHoraGrupoExamenByRolExamen(RolExamenes rolExamenes) {
        return fechaHoraGrupoExamenDAO.allByRolExamens(rolExamenes);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteFechaHoraGrupoExamen(FechaHoraGrupoExamen fechaHoraGrupoExamen) {
        GrupoHorasExamen grupoHorasExamen = grupoHorasExamenDAO.find(fechaHoraGrupoExamen.getGrupoHorasExamen().getId());

        checkEstadoPublicado(grupoHorasExamen.getRolExamenes());

        fechaHoraGrupoExamen = fechaHoraGrupoExamenDAO.find(fechaHoraGrupoExamen.getId());
        fechaHoraGrupoExamenDAO.delete(fechaHoraGrupoExamen);

        this.actualizarFechaGrupoHorasExamen(grupoHorasExamen, grupoHorasExamen.getRolExamenes());

    }
}
