package pe.edu.lamolina.pivot.controller.rolexamen.plantillahorario;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.EventoCicloAcademico;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.EventoAcademicoEnum;
import pe.edu.lamolina.model.enums.RolExamenesEstadoEnum;
import pe.edu.lamolina.model.enums.SituacionRolExamenesEnum;
import pe.edu.lamolina.model.enums.TipoGrupoHorasEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.horario.DiaHoraGrupo;
import pe.edu.lamolina.model.horario.GrupoHoras;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioAula;
import pe.edu.lamolina.model.rolexamen.FechaHoraGrupoExamen;
import pe.edu.lamolina.model.rolexamen.GrupoHorasExamen;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SemanaExamen;
import pe.edu.lamolina.pivot.dao.academico.EventoCicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.general.DiaDAO;
import pe.edu.lamolina.pivot.dao.horario.DiaHoraGrupoDAO;
import pe.edu.lamolina.pivot.dao.horario.GrupoHorasDAO;
import pe.edu.lamolina.pivot.dao.horario.HoraDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioAulaDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.FechaHoraGrupoExamenDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.GrupoHorasExamenDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.RolExamenesDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.SemanaExamenDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

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

    @Autowired
    HorarioAulaDAO horarioAulaDAO;

    @Autowired
    RolExamenesDAO rolexamenesDAO;

    @Autowired
    EventoCicloAcademicoDAO eventoCicloAcademicoDAO;

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
        Assert.isTrue(rolBD.isSituacionConfigurarRol() || rolBD.isSituacionConfigurarHorario(), "La plantilla de horarioas ya ha sido generada");

        this.deletePlantillaHorario(rolExamenes);

        List<SemanaExamen> semanas = semanaExamenDAO.allByRolExamenes(rolExamenes);
        List<Hora> horas = horaDAO.all();

        for (SemanaExamen semana : semanas) {
            logger.debug("######################################################");
            logger.debug("CALCULAR PLANTILLA HORARIO DE LA SEMANA " + semana.getNumeroSemana());
            this.calcularPlantillaHorario(semana, horas);
        }

        this.agregarGrupoHorasFaltantes(rolBD);

        RolExamenes rolExamenesUpd = new RolExamenes(rolExamenes.getId());
        rolExamenesUpd.setEstadoEnum(RolExamenesEstadoEnum.CON);
        rolExamenesUpd.setSituacionEnum(SituacionRolExamenesEnum.CFG_HOR);
        rolExamenesDAO.updateEstadoAndSituacion(rolExamenesUpd);
    }

    @Override
    @Transactional
    public void confirmarPlantillaHorario(RolExamenes rolExamenes, DataSessionPivot ds) {
        logger.debug("confirmarPlantillaHorario");
        rolExamenes = rolExamenesDAO.find(rolExamenes.getId());

        Assert.isTrue(rolExamenes.isSituacionConfigurarHorario(), "Debe estar configurando los horarios del rol examen para confirmarlos.");

        CicloAcademico cicloRol = rolExamenes.getEventoCicloAcademico().getCicloAcademico();

        final RolExamenes firstRolExamen = rolExamenesDAO.findByCicloAndEstadoAndEventoAcademico(cicloRol, null, EventoAcademicoEnum.EXAMEN_PARC);

        List<SemanaExamen> semanaExamenes = semanaExamenDAO.allByRolExamenes(rolExamenes);

        List<HorarioAula> horariosAulasByCiclo = horarioAulaDAO.allForRolExamenesByCicloAcademico(cicloRol);
        if (rolExamenes.getEventoCicloAcademico().getEventoAcademico().isExamenFinal()) {
            horariosAulasByCiclo.removeIf(x
                    -> x.getFechaFinDateTime().toLocalDate().isBefore(firstRolExamen.getEventoCicloAcademico().getFechaFinDateTime().toLocalDate())
                    || x.getFechaFinDateTime().toLocalDate().isEqual(firstRolExamen.getEventoCicloAcademico().getFechaFinDateTime().toLocalDate()));
        }
        List<Long> horariosAulasProcesados = new ArrayList<>();
        for (SemanaExamen semanaExamen : semanaExamenes) {
            List<HorarioAula> horariosAulasFull = horarioAulaDAO.allByCicloAndSemanaExamenLimitByHours(rolExamenes.getEventoCicloAcademico(), semanaExamen);
            if (rolExamenes.getEventoCicloAcademico().getEventoAcademico().isExamenFinal()) {
                horariosAulasFull.removeIf(x
                        -> x.getFechaFinDateTime().toLocalDate().isBefore(firstRolExamen.getEventoCicloAcademico().getFechaFinDateTime().toLocalDate())
                        || x.getFechaFinDateTime().toLocalDate().isEqual(firstRolExamen.getEventoCicloAcademico().getFechaFinDateTime().toLocalDate()));
            }
            Map<Long, List<HorarioAula>> mapHorariosBySeccion = TypesUtil.convertListToMapList("seccion.id", horariosAulasFull);
            for (Map.Entry<Long, List<HorarioAula>> entry : mapHorariosBySeccion.entrySet()) {
                Seccion seccion = new Seccion(entry.getKey());
                List<HorarioAula> horariosAulasBySeccion = entry.getValue();
                if (seccion.getId().compareTo(257400L) == 0) {
                    logger.debug("");
                }

                Map<Long, List<HorarioAula>> mapHorariosBySeccionAndDia = TypesUtil.convertListToMapList("dia.id", horariosAulasBySeccion);
                for (Map.Entry<Long, List<HorarioAula>> entry1 : mapHorariosBySeccionAndDia.entrySet()) {
                    Dia dia = new Dia(entry1.getKey());
                    //  List<HorarioAula> horariosAulasBySeccionAndDia = entry1.getValue();
                    List<HorarioAula> horariosAulasBySeccionAndDia = horariosAulasByCiclo.stream()
                            .filter(x -> x.getSeccion().equals(seccion))
                            .filter(x -> x.getDia().equals(dia))
                            .collect(Collectors.toList());

                    for (HorarioAula horarioAula : horariosAulasBySeccionAndDia) {
                        if (horariosAulasProcesados.contains(horarioAula.getId())) {
                            continue;
                        }
                        logger.debug("Original - Dia {}, Hora {}, Fecha Inicio {}, Fecha Fin {} ",
                                horarioAula.getDia().getNumeroDia(),
                                horarioAula.getHora().getNumero(),
                                horarioAula.getFechaInicio(),
                                horarioAula.getFechaFin());
                        Date endDateOrigin = (Date) horarioAula.getFechaFin().clone();

                        DateTime fechaFin = new DateTime(semanaExamen.getFechaInicio()).plusDays(-1);
                        /*   if (horarioAula.getFechaFin().equals(fechaFin.toDate())) {
                            continue;
                        }*/
                        horarioAula.setFechaFin(fechaFin.toDate());
                        horarioAulaDAO.update(horarioAula);
                        logger.debug("Actualizado - Dia {}, Hora {}, Fecha Inicio {}, Fecha Fin {} ",
                                horarioAula.getDia().getNumeroDia(),
                                horarioAula.getHora().getNumero(),
                                TypesUtil.getStringDate(horarioAula.getFechaInicio(), "yyyy-MM-dd"),
                                TypesUtil.getStringDate(horarioAula.getFechaFin(), "yyyy-MM-dd"));

                        HorarioAula horarioAulaNew = horarioAula.clone();
                        horarioAulaNew.setId(null);
                        DateTime fechaInicio = new DateTime(semanaExamen.getFechaFin()).plusDays(1);
                        horarioAulaNew.setFechaInicio(fechaInicio.toDate());
                        horarioAulaNew.setFechaFin(endDateOrigin);
                        horarioAulaNew.setSeccion(null);
                        //   horarioAulaNew.setRolExamenes(rolExamenes);
                        horarioAulaDAO.save(horarioAulaNew);

                        logger.debug("Nuevo - Dia {}, Hora {}, Fecha Inicio {}, Fecha Fin {} ",
                                horarioAulaNew.getDia().getNumeroDia(),
                                horarioAulaNew.getHora().getNumero(),
                                TypesUtil.getStringDate(horarioAulaNew.getFechaInicio(), "yyyy-MM-dd"),
                                TypesUtil.getStringDate(horarioAulaNew.getFechaFin(), "yyyy-MM-dd"));
                        horariosAulasProcesados.add(horarioAula.getId());
                    }
                }
            }
        }
        RolExamenes rolExamenesUpd = new RolExamenes(rolExamenes.getId());
        rolExamenesUpd.setEstadoEnum(RolExamenesEstadoEnum.CON);
        rolExamenesUpd.setSituacionEnum(SituacionRolExamenesEnum.CONF_HOR);
        rolExamenesDAO.updateEstadoAndSituacion(rolExamenesUpd);
        // throw new PhobosException("no pasaras");
    }

    public void agregarGrupoHorasFaltantes(RolExamenes rolExamenes) {
        Set<GrupoHoras> gruposGenerados = grupoHorasExamenDAO.allByRolExamenes(rolExamenes).stream().map(GrupoHorasExamen::getGrupoHoras).collect(Collectors.toSet());

        List<GrupoHoras> gruposFaltantes = grupoHorasDAO.allByTipoCiclo("REGULAR")
                .stream()
                .filter(grupo -> !gruposGenerados.contains(grupo))
                .collect(Collectors.toList());
        GrupoHoras grupoHorasM = grupoHorasDAO.findByCode("M");
        if (!gruposGenerados.contains(grupoHorasM)) {
            gruposFaltantes.add(grupoHorasM);
        }

        for (GrupoHoras grupoFaltante : gruposFaltantes) {
            logger.debug("Generando grupo faltante {}", grupoFaltante.getCodigo());
            GrupoHorasExamen grupoHorasExamen = new GrupoHorasExamen();
            grupoHorasExamen.setGrupoHoras(grupoFaltante);
            grupoHorasExamen.setRolExamenes(rolExamenes);
            grupoHorasExamen.setVerificado(Boolean.FALSE);
            grupoHorasExamenDAO.save(grupoHorasExamen);
        }
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
                    Hora horaFinalVisual = horas.stream().filter(x -> x.getNumero().compareTo(fechaHoraGrupoExamen.getHora().getNumero()) == 0).findFirst().orElse(null);
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
        this.restoreHorariosAulas(rolBD, null, null);
    }

    public void restoreHorariosAulas(RolExamenes rolExamenes, Seccion seccion, Aula aula) {
        CicloAcademico cicloAcademico = rolExamenes.getEventoCicloAcademico().getCicloAcademico();
        List<SemanaExamen> semanas = semanaExamenDAO.allByRolExamenes(rolExamenes);

        final RolExamenes rolExamParcial = rolexamenesDAO.findByCicloAndEstadoAndEventoAcademico(cicloAcademico, null, EventoAcademicoEnum.EXAMEN_PARC);
        EventoCicloAcademico dictadoClases = eventoCicloAcademicoDAO.findActivoByCicloTipoEvento(cicloAcademico, EventoAcademicoEnum.CLASES_PRE);

        List<HorarioAula> horariosAulasByCiclo = horarioAulaDAO.allForRolExamenesByCicloAcademico(rolExamenes.getEventoCicloAcademico().getCicloAcademico());
        if (seccion != null && aula != null) {
            horariosAulasByCiclo.removeIf(x -> !x.getSeccion().equals(seccion));
            horariosAulasByCiclo.removeIf(x -> !x.getAula().equals(aula));
        }
        if (rolExamenes.getEventoCicloAcademico().getEventoAcademico().isExamenFinal()) {
            //removemos todos los horarios generados antes del examen parcial
            horariosAulasByCiclo.removeIf(x
                    -> x.getFechaFinDateTime().toLocalDate().isBefore(rolExamParcial.getEventoCicloAcademico().getFechaFinDateTime().toLocalDate())
                    || x.getFechaFinDateTime().toLocalDate().isEqual(rolExamParcial.getEventoCicloAcademico().getFechaFinDateTime().toLocalDate()));
        }
        for (SemanaExamen semana : semanas) {
            List<HorarioAula> horariosAulasFull = horarioAulaDAO.allByCicloAndSemanaExamenLimitByHours(rolExamenes.getEventoCicloAcademico(), semana);
            if (rolExamenes.getEventoCicloAcademico().getEventoAcademico().isExamenFinal()) {
                //removemos todos los horarios generados antes del examen parcial
                horariosAulasFull.removeIf(x
                        -> x.getFechaFinDateTime().toLocalDate().isBefore(rolExamParcial.getEventoCicloAcademico().getFechaFinDateTime().toLocalDate())
                        || x.getFechaFinDateTime().toLocalDate().isEqual(rolExamParcial.getEventoCicloAcademico().getFechaFinDateTime().toLocalDate()));
            }
            Map<Long, List<HorarioAula>> mapHorariosBySeccion = TypesUtil.convertListToMapList("seccion.id", horariosAulasFull);
            for (Map.Entry<Long, List<HorarioAula>> entry : mapHorariosBySeccion.entrySet()) {
                Seccion iSeccion = new Seccion(entry.getKey());
                List<HorarioAula> horariosAulasBySeccion = entry.getValue();

                Map<Long, List<HorarioAula>> mapHorariosBySeccionAndDia = TypesUtil.convertListToMapList("dia.id", horariosAulasBySeccion);
                for (Map.Entry<Long, List<HorarioAula>> entry1 : mapHorariosBySeccionAndDia.entrySet()) {
                    Dia dia = new Dia(entry1.getKey());
                    //  List<HorarioAula> horariosAulasBySeccionAndDia = entry1.getValue();
                    List<HorarioAula> horariosAulasBySeccionAndDia = horariosAulasByCiclo.stream()
                            .filter(x -> x.getSeccion().equals(iSeccion))
                            .filter(x -> x.getDia().equals(dia))
                            .filter(x -> !x.isTipoExamen())
                            .collect(Collectors.toList());
                    for (HorarioAula horarioAula : horariosAulasBySeccionAndDia) {
                        if (horarioAula.getFechaFinDateTime().plusDays(1).toLocalDate()
                                .equals(semana.getFechaInicioDateTime().toLocalDate())) {
                            horarioAula.setFechaFin(dictadoClases.getFechaFin());
                            horarioAulaDAO.update(horarioAula);
                        }
                        if (horarioAula.getFechaInicioDateTime().plusDays(-1).toLocalDate()
                                .equals(semana.getFechaFinDateTime().toLocalDate())) {
                            horarioAulaDAO.delete(horarioAula);
                        }
                    }

                }
            }
            //to delete
            /*
            List<HorarioAula> horariosAulasByCicloOcupadas = horarioAulaDAO.allOcupadasByCicloAndSemanaExamen(cicloAcademico, semana);
            for (HorarioAula horariosAulasByCicloOcupada : horariosAulasByCicloOcupadas) {
                horarioAulaDAO.delete(horariosAulasByCicloOcupada);

            }*/
        }
        horarioAulaDAO.deleteByRolExamenes(rolExamenes);
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
        logger.debug(gruposIdsFiltered.toString());

        List<GrupoHoras> gruposHoras = grupoHorasDAO.allGrupoHoras(gruposIdsFiltered);
        final int HORA_INICIO = semanaExamen.getHoraInicio().getNumero();
        final int HORA_FIN = semanaExamen.getHoraFin().getNumero();
        for (GrupoHoras gruposHora : gruposHoras) {
            List<DiaHoraGrupo> diasHorasGrupo = diaHoraGrupoDAO.allByGrupoCiclo(gruposHora, rolExamenes.getEventoCicloAcademico().getCicloAcademico());
            diasHorasGrupo = diasHorasGrupo.stream()
                    .filter(x -> x.getDia().getId().compareTo(groupsAndDays.get(gruposHora.getId())) == 0)
                    .filter(x -> x.getHora().getNumero() >= HORA_INICIO)
                    .filter(x -> x.getHora().getNumero() < HORA_FIN)
                    .collect(Collectors.toList());
            Collections.sort(diasHorasGrupo, (p1, p2) -> p1.getHora().getNumero().compareTo(p2.getHora().getNumero()));
            gruposHora.setDiaHoraGrupo(diasHorasGrupo);
        }
        return gruposHoras;
    }

    @Override
    public List<GrupoHorasExamen> allGrupoHorasExamenByRolExamen(RolExamenes rolExamenes, DynatableFilter filter) {
        List<GrupoHorasExamen> gruposHorasExamenes = grupoHorasExamenDAO.allByRolExamenesAndDyna(rolExamenes, filter);
        List<FechaHoraGrupoExamen> fechasHorasGpoExamenTodos = fechaHoraGrupoExamenDAO.allByGrupoHorasExamen(gruposHorasExamenes);
        Map<Long, List<FechaHoraGrupoExamen>> mapFechaHoraGpoExamen = TypesUtil.convertListToMapList("grupoHorasExamen.id", fechasHorasGpoExamenTodos);

        for (GrupoHorasExamen gruposHora : gruposHorasExamenes) {
            List<FechaHoraGrupoExamen> fechasHorasGrupoExamen = TypesUtil.getListNotNull(mapFechaHoraGpoExamen.get(gruposHora.getId()));
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
    public List<SemanaExamen> allSemanasByRolExamen(RolExamenes rolExamenes) {
        return semanaExamenDAO.allByRolExamenes(rolExamenes);
    }

    @Override
    public List<GrupoHoras> allGrupoHoraDisponibles(RolExamenes rolExamenes) {
        List<GrupoHorasExamen> gposHorasExame = grupoHorasExamenDAO.allByRolExamenes(rolExamenes);
        List<GrupoHoras> gruposLetras = grupoHorasDAO.allSimples();
        Map<Long, GrupoHoras> mapGpoHoras = TypesUtil.convertListToMap("id", gruposLetras);

        for (GrupoHorasExamen gpoHorasExam : gposHorasExame) {
            GrupoHoras gh = mapGpoHoras.get(gpoHorasExam.getGrupoHoras().getId());
            if (gh != null) {
                gruposLetras.remove(gh);
            }
        }
        List<GrupoHoras> gruposOk = new ArrayList();
        for (GrupoHoras gpo : gruposLetras) {
            if (gpo.getLetra().equals("Z")) {
                continue;
            }
            if (gpo.getCodigo().equals(gpo.getLetra())) {
                gruposOk.add(gpo);
            }
            if (gpo.getCodigo().equals(gpo.getLetra() + "*")) {
                gruposOk.add(gpo);
            }
        }

        return gruposOk;
    }

    @Override
    public List<FechaHoraGrupoExamen> allFechaHoraGrupoExamenBySemanaExamen(SemanaExamen semanaExamen) {
        return fechaHoraGrupoExamenDAO.allBySemanaExamen(semanaExamen);
    }

    @Override
    public List<FechaHoraGrupoExamen> allFechaHoraGrupoExamenBySemanas(List<SemanaExamen> semanasExamen) {
        return fechaHoraGrupoExamenDAO.allBySemanasExamen(semanasExamen);
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

    @Override
    @Transactional(readOnly = false)
    public void deleteGrupoHoraExamen(GrupoHorasExamen grupoHorasExamenForm) {
        GrupoHorasExamen grupoHorasExamenBD = grupoHorasExamenDAO.find(grupoHorasExamenForm.getId());
        checkEstadoPublicado(grupoHorasExamenBD.getRolExamenes());

        List<FechaHoraGrupoExamen> fechas = fechaHoraGrupoExamenDAO.allByGrupoHorasExamen(grupoHorasExamenBD);
        Assert.isTrue(fechas.isEmpty(), "No puede eliminarse un grupo con fecha y horas programadas");
        Assert.isFalse(grupoHorasExamenBD.getVerificado(), "Este grupo ya fue verificado. No puede ser elimnado.");

        grupoHorasExamenDAO.delete(grupoHorasExamenBD);
    }

    @Override
    @Transactional
    public void saveGrupoHorasExamen(GrupoHorasExamen gpoHorasExamen, DataSessionPivot ds) {
        RolExamenes rolExamenes = gpoHorasExamen.getRolExamenes();
        RolExamenes rolBD = rolExamenesDAO.find(rolExamenes.getId());
        List<GrupoHorasExamen> gruposHorasExamenBD = grupoHorasExamenDAO.allByRolExamenes(rolExamenes);
        Map<Long, GrupoHorasExamen> mapGpoHoras = TypesUtil.convertListToMap("grupoHoras.id", gruposHorasExamenBD);

        GrupoHorasExamen gpoHorasExamenBD = mapGpoHoras.get(gpoHorasExamen.getGrupoHoras().getId());
        Assert.isNull(gpoHorasExamenBD, "Este grupos ya se encuentra configurado en este rol de exámenes");

        Dia dia = diaDAO.find(gpoHorasExamen.getDia().getId());
        SemanaExamen semana = semanaExamenDAO.find(gpoHorasExamen.getSemanaExamen().getId());
        DateTime fecha = new DateTime(semana.getFechaInicio()).withDayOfWeek(dia.getNumeroDia());

        gpoHorasExamen.setFecha(fecha.toDate());
        gpoHorasExamen.setVerificado(Boolean.TRUE);
        gpoHorasExamen.setFechasHorasGruposExamen(new ArrayList());

        List<Hora> horas = horaDAO.allHoras();
        Map<Long, Hora> mapHoras = TypesUtil.convertListToMap("id", horas);
        Hora horaInicio = mapHoras.get(gpoHorasExamen.getHoraInicio().getId());

        int loop = 0;
        Hora horaFin = null;
        for (Hora hora : horas) {
            if (hora.getCodigo().compareTo(horaInicio.getCodigo()) < 0) {
                continue;
            }

            FechaHoraGrupoExamen fechaGpo = new FechaHoraGrupoExamen();
            fechaGpo.setFecha(fecha.toDate());
            fechaGpo.setHora(hora);
            fechaGpo.setDia(dia);
            fechaGpo.setGrupoHorasExamen(gpoHorasExamen);
            fechaGpo.setSemanaExamen(semana);
            gpoHorasExamen.setHoraFin(hora);
            gpoHorasExamen.getFechasHorasGruposExamen().add(fechaGpo);
            horaFin = hora;

            loop++;
            if (loop >= rolBD.getHorasExamen()) {
                break;
            }
        }

        List<FechaHoraGrupoExamen> fechaHorasRol = fechaHoraGrupoExamenDAO.allByRolExamens(rolExamenes);
        Map<Date, List<FechaHoraGrupoExamen>> mapFechaHora = TypesUtil.convertListToMapList("fecha", fechaHorasRol);
        List<FechaHoraGrupoExamen> fechaHorasByDia = TypesUtil.getListNotNull(mapFechaHora.get(fecha.toDate()));
        //Assert.isFalse(fechaHorasByDia.isEmpty(), "No se obtuvo las horas del día");
        System.out.println("fechaHorasByDia.size = " + fechaHorasByDia.size());

        for (FechaHoraGrupoExamen fechaBD : fechaHorasByDia) {
            for (FechaHoraGrupoExamen fechaHora : gpoHorasExamen.getFechasHorasGruposExamen()) {
                if (fechaHora.getIdDiaHora().equals(fechaBD.getIdDiaHora())) {
                    throw new PhobosException("La hora " + fechaBD.getHora().getDescripcion() + " ya está ocupada para el " + fecha.toString("dd/MM/yyyy"));
                }
            }
        }

        if (semana.getHoraFin().getCodigo().compareTo(horaFin.getCodigo()) < 0) {
            semana.setHoraFin(horaFin);
            semanaExamenDAO.update(semana);
        }

        grupoHorasExamenDAO.save(gpoHorasExamen);
        for (FechaHoraGrupoExamen fechaHora : gpoHorasExamen.getFechasHorasGruposExamen()) {
            fechaHoraGrupoExamenDAO.save(fechaHora);
        }

        //gpoHorasExamen.setFecha(fecha);
    }

}
