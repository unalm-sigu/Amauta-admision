package pe.edu.lamolina.amauta.controller.programacionhorarios.asignacionaula;

import java.beans.Transient;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.AsignacionAula;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.CursoCicloAcademico;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.DistanciaPabellon;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.EventoCicloAcademico;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.CicloAcademicoEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.EstadoHorarioAulaEnum;
import pe.edu.lamolina.model.enums.EventoAcademicoEnum;
import pe.edu.lamolina.model.enums.OficinaEnum;
import pe.edu.lamolina.model.enums.SeccionEstadoEnum;
import pe.edu.lamolina.model.enums.TipoCarpetaEnum;
import pe.edu.lamolina.model.enums.TipoHorarioAulaEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.TipoCarpeta;
import pe.edu.lamolina.model.horario.HorarioAula;
import pe.edu.lamolina.model.horario.HorarioSeccion;
import pe.edu.lamolina.amauta.dao.academico.AsignacionAulaDAO;
import pe.edu.lamolina.amauta.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.CursoCicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.DepartamentoAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.DistanciaPabellonDAO;
import pe.edu.lamolina.amauta.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.amauta.dao.academico.EventoCicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.amauta.dao.academico.SeccionDAO;
import pe.edu.lamolina.amauta.dao.general.AulaDAO;
import pe.edu.lamolina.amauta.dao.general.OficinaDAO;
import pe.edu.lamolina.amauta.dao.horario.HorarioAulaDAO;
import pe.edu.lamolina.amauta.dao.horario.HorarioSeccionDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = false)
public class AsignacionAulaServiceImp implements AsignacionAulaService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AulaDAO aulaDAO;

    @Autowired
    SeccionDAO seccionDAO;

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Autowired
    AsignacionAulaDAO asignacionAulaDAO;

    @Autowired
    GrupoSeccionDAO grupoSeccionDAO;

    @Autowired
    DocenteSeccionDAO docenteSeccionDAO;

    @Autowired
    OficinaDAO oficinaDAO;

    @Autowired
    DepartamentoAcademicoDAO departamentoAcademicoDAO;

    @Autowired
    DistanciaPabellonDAO distanciaPabellonDAO;

    @Autowired
    EventoCicloAcademicoDAO eventoCicloAcademicoDAO;

    @Autowired
    HorarioAulaDAO horarioAulaDAO;

    @Autowired
    HorarioSeccionDAO horarioSeccionDAO;

    @Autowired
    CursoCicloAcademicoDAO cursoCicloAcademicoDAO;

    @Override
    public CicloAcademico findCiclo(CicloAcademico cicloAcademico) {
        return cicloAcademicoDAO.find(cicloAcademico);
    }

    @Override
    public AsignacionAula findAsignacionAulaByCiclo(CicloAcademico cicloAcademico) {
        AsignacionAula asignacionAula = asignacionAulaDAO.findByCiclo(cicloAcademico);
        if (asignacionAula != null) {
            asignacionAula.setIdsGpoSecciones(this.getIdsGpoSecciones(asignacionAula));
        }
        return asignacionAula;
    }

    private String getIdsGpoSecciones(AsignacionAula asignacionAula) {
        List<Seccion> secciones = seccionDAO.allSeccionesAulaAutoByCiclo(asignacionAula.getCicloAcademico());
        List<String> idsGpoSeccion = new ArrayList<>();
        secciones.forEach(x -> {
            if (!idsGpoSeccion.contains(x.getGrupoSeccion().getId().toString())) {
                idsGpoSeccion.add(x.getGrupoSeccion().getId().toString());
            }
        });

        return idsGpoSeccion.isEmpty() ? "" : String.join(",", idsGpoSeccion);
    }

    @Override
    @Transactional
    public void deleteAsignacion(AsignacionAula asignacionAula) {
        if (asignacionAula.getId() != null) {
            asignacionAula = asignacionAulaDAO.find(asignacionAula.getId());
            List<Seccion> seccionesByCiclo = seccionDAO.allSeccionesAulaAutoByCiclo(asignacionAula.getCicloAcademico());
            seccionDAO.resetAsignacionAulaAuto(seccionesByCiclo);
            horarioAulaDAO.deleteBySecciones(seccionesByCiclo);
            asignacionAulaDAO.delete(asignacionAula);
        }
    }

    @Override
    public AsignacionAula findAsignacionAula(AsignacionAula asignacionAula) {
        return asignacionAulaDAO.find(asignacionAula.getId());
    }

    @Override
    @Transient
    public AsignacionAula procesarAsignacionAulas(AsignacionAula asignacionAula, DataSessionPivot ds) {
        if (asignacionAula != null && asignacionAula.getId() != null) {
            this.deleteAsignacion(asignacionAula);
        }
        List<CursoCicloAcademico> cursosCiclosAcademicos = cursoCicloAcademicoDAO.allByCiclo(ds.getCicloAcademico(), CicloAcademicoEstadoEnum.ACT);

        List<Seccion> seccionesByCiclo = seccionDAO.allForAsignacionAulaByCiclo(ds.getCicloAcademico(), SeccionEstadoEnum.ACT);
        List<HorarioSeccion> horarios = horarioSeccionDAO.allBySeccionesSortByDiaHora(seccionesByCiclo);
        Map<Long, List<HorarioSeccion>> mapHorariosBySeccion = TypesUtil.convertListToMapList("seccion.id", horarios);

        // INICIO CAMBIO HECHO POR DAVID PINEDA
//        List<HorarioAula> horarioAulas = horarioAulaDAO.allByCiclo(ds.getCicloAcademico());
//        for (HorarioAula horarioAula : horarioAulas) {
//            logger.debug("AULA {}, DIA {}, HORA {}, ESTADO {}, TIPO {}", horarioAula.getAula().getId(), horarioAula.getDia().getId(), horarioAula.getHora().getId(), horarioAula.getEstado(), horarioAula.getTipo());
//        }
//        Map<String, List<HorarioAula>> mapAulaDiaHora = TypesUtil.convertListToMapList("key", horarioAulas);
        // FIN CAMBIO HECHO POR DAVID PINEDA
        int seccionesProgramadas = seccionesByCiclo.size();

        seccionesByCiclo = seccionesByCiclo.stream()
                .filter(x -> x.getAula() == null)
                .collect(Collectors.toList());

        //Ordernar por horas semanalaes de mayor a menor
//        Collections.sort(seccionesByCiclo, (p1, p2) -> p2.getHorasSemanales().compareTo(p1.getHorasSemanales()));
        Collections.sort(seccionesByCiclo, (p1, p2) -> p2.getVacantes().compareTo(p1.getVacantes()));

        EventoCicloAcademico eventoCicloDictado = eventoCicloAcademicoDAO.findActivoByCicloTipoEvento(ds.getCicloAcademico(), EventoAcademicoEnum.CLASES_PRE);
        List<Aula> aulas = aulaDAO.allByOficinaSupervisora(OficinaEnum.OERA, EstadoEnum.ACT);
        aulas = aulas.stream().filter(x -> x.getTipoCarpeta() != null).collect(Collectors.toList());

        Map<Long, List<Aula>> mapAulasByModulo = TypesUtil.convertListToMapList("aulaSuperior.id", aulas);
        logger.debug("Aulas encontradas (id´'s) {}", aulas.stream().map(x -> x.getId()).collect(Collectors.toList()));
        logger.debug("Modulos Encontrados Por Aula (id´'s) {}", new ArrayList(mapAulasByModulo.keySet()));

        List<HorarioAula> horarioAulasDictado = horarioAulaDAO.allByRango(eventoCicloDictado.getFechaInicio(), eventoCicloDictado.getFechaFin());
        Map<String, List<HorarioAula>> mapsHorarioAulaByAulaForDictado = TypesUtil.convertListToMapList("key", horarioAulasDictado);

        List<DocenteSeccion> docentesSeccionPrincipalesByCiclo
                = docenteSeccionDAO.allByCiclo(ds.getCicloAcademico(), EstadoEnum.ACT);
        docentesSeccionPrincipalesByCiclo = docentesSeccionPrincipalesByCiclo.stream()
                .filter(x -> x.getSeccion().getAula() == null)
                .filter(x -> x.isEstadoActivado())
                .filter(x -> x.getPrincipal() == BigDecimal.ONE.intValue())
                .collect(Collectors.toList());

        List<DistanciaPabellon> distanciaPabellonesAll = distanciaPabellonDAO.allByActAndDistanciaOrder("dp.distancia asc");
        Map<Long, List<DistanciaPabellon>> mapDistanciaPabellones = TypesUtil.convertListToMapList("departamentoAcademico.id", distanciaPabellonesAll);

        Map<Long, DocenteSeccion> mapDocentesSeccionPrincipalesBySeccion = TypesUtil.convertListToMap("seccion.id", docentesSeccionPrincipalesByCiclo);

        List<Aula> aulasAsignadas = new ArrayList<>();

        int seccionesTipoLab = 0;
        int seccionesTipoAul = 0;
        int seccionesAsignadas = 0;

        for (Seccion seccion : seccionesByCiclo) {

            logger.debug("Seccion {} Horas semanales {} Vacantes {} ", seccion.getCodigo2(), seccion.getHorasSemanales(), seccion.getVacantes());

            if (seccion.getGrupoSeccion().getCurso().getModalidadEstudio().isPostgrado()) {

                System.err.println("---- Seccion Post grado");
                continue;
            }

            TipoCarpeta tipoCarpeta = this.getTipoCarpeta(seccion, cursosCiclosAcademicos);
            List<HorarioSeccion> horariosSecciones = mapHorariosBySeccion.get(seccion.getId());
            seccion.setHorarioSeccion(horariosSecciones);
            if (seccion.getHorarioSeccion() == null) {

                System.err.println("---- Seccion No tiene horario");
                continue;
            }
            Boolean esDocenteConDiscapacidad = false;
            Map<Long, List<HorarioAula>> mapsHorarioAulaByAulaForFechasModular = null;
            if (seccion.getGrupoSeccion().getFechaInicioModular() != null && seccion.getGrupoSeccion().getFechaFinModular() != null) {
                List<HorarioAula> horariosAulas = horarioAulaDAO.allByRango(seccion.getGrupoSeccion().getFechaInicioModular(), seccion.getGrupoSeccion().getFechaFinModular());
                mapsHorarioAulaByAulaForFechasModular = TypesUtil.convertListToMapList("aula.id", horariosAulas);
            }
            DocenteSeccion docenteSeccionPrincipal = mapDocentesSeccionPrincipalesBySeccion.get(seccion.getId());
            DepartamentoAcademico departamentoAcademicoDocente = docenteSeccionPrincipal.getDocente().getDepartamentoAcademico();
            if (departamentoAcademicoDocente == null) {
                departamentoAcademicoDocente = seccion.getGrupoSeccion().getCurso().getDepartamentoAcademico();
                if (departamentoAcademicoDocente == null) {
                    System.err.println("---- Seccion sin departamento");
                    continue;
                }
            } else {

                esDocenteConDiscapacidad = docenteSeccionPrincipal.getDocente().getPersona().getConDiscapacidad() == 1;
                seccion.settDocenteSeccion(docenteSeccionPrincipal);
            }

            List<DistanciaPabellon> distanciaPabellonByDepartamento = mapDistanciaPabellones.get(departamentoAcademicoDocente.getId());
            if (distanciaPabellonByDepartamento == null) {
                System.err.println("---- Seccion No hay distancias");
                continue;
            }
            FOR_DIST_PAB:
            for (DistanciaPabellon distanciaPabellon : distanciaPabellonByDepartamento) {

                List<Aula> aulasByPabellon = TypesUtil.getListNotNull(mapAulasByModulo.get(distanciaPabellon.getPabellon().getId()));
                aulasByPabellon = aulasByPabellon.stream()
                        .filter(x -> x.getAforo() >= seccion.getVacantes())
                        .filter(x -> tipoCarpeta.getId().compareTo(x.getTipoCarpeta().getId()) == 0)
                        .collect(Collectors.toList());
                //Ordenamos las aulas por aforo de mayor a menor
                Collections.sort(aulasByPabellon, (p1, p2) -> p1.getAforo().compareTo(p2.getAforo()));
                FOR_AULA:
                for (Aula aula : aulasByPabellon) {
                    /*
                    List<HorarioAula> horariosAula = mapsHorarioAulaByAulaForDictado.get(aula.getId());
                    if (mapsHorarioAulaByAulaForFechasModular != null) {
                        horariosAula = mapsHorarioAulaByAulaForFechasModular.get(aula.getId()) != null ? mapsHorarioAulaByAulaForFechasModular.get(aula.getId()) : new ArrayList<>();
                    }
                    if (horariosAula == null) {
                        horariosAula = new ArrayList<>();
                    }
                    aula.setHorariosAula(horariosAula);
                     */

//                    this.fillHorarioAula(aula, mapsHorarioAulaByAulaForDictado, mapsHorarioAulaByAulaForFechasModular);
                    Integer piso = aula.getPiso() == null ? BigDecimal.ONE.intValue() : aula.getPiso();
                    if (esDocenteConDiscapacidad && piso != BigDecimal.ONE.intValue()) {
                        System.err.println("---- Seccion discapacitado sin primer piso");
                        continue;
                    }

                    for (HorarioSeccion horarioSeccion : seccion.getHorarioSeccion()) {

                        String key = aula.getId() + "-" + horarioSeccion.getDia().getId() + "-" + horarioSeccion.getHora().getId();
//                        HorarioAula horarioAula = aula.getHorariosAula()
//                                .stream()
//                                .filter(x -> x.getHoraDia().equals(horarioSeccion.getHoraDia()))
//                                .findFirst().orElse(null);
//                        if (horarioAula != null) {
                        System.err.println("---- Seccion cruce horario");
//                            continue FOR_AULA;
//                        }
                        // INICIO CAMBIO HECHO POR DAVID PINEDA 
//                        String key = aula.getId() + "-" + horarioSeccion.getDia().getId() + "-" + horarioSeccion.getHora().getId();
                        List<HorarioAula> ha = TypesUtil.getListNotNull(mapsHorarioAulaByAulaForDictado.get(key));
                        if (!ha.isEmpty()) {
//                            logger.debug("AULA {}, DIA {}, HORA {}", aula.getId(), horarioSeccion.getDia().getId(), horarioSeccion.getHora().getId());
                            Boolean error = false;
                            for (HorarioAula horarioAu : aula.getHorariosAula()) {
//                                logger.debug("AULA {}, DIA {}, HORA {}, ESTADO {}, TIPO {}", horarioAu.getAula().getId(), horarioAu.getDia().getId(), horarioAu.getHora().getId(), horarioAu.getEstado(), horarioAu.getTipo());
//
                                if (horarioAu != null) {
                                    if (mapsHorarioAulaByAulaForFechasModular != null) {
                                        if (TypesUtil.getStringDate(horarioAu.getFechaInicio(), "yyyy/MM/dd").equals(TypesUtil.getStringDate(seccion.getGrupoSeccion().getFechaInicioModular(), "yyyy/MM/dd"))) {
                                            System.err.println("---- Seccion fecha modular");
                                            error = true;
                                            break;
                                        }
                                    } else {

                                        if (TypesUtil.getStringDate(horarioAu.getFechaInicio(), "yyyy/MM/dd").equals(TypesUtil.getStringDate(eventoCicloDictado.getFechaInicio(), "yyyy/MM/dd"))) {
                                            System.err.println("---- Seccion fecha evento");
                                            error = true;
                                            break;
                                        }
                                    }
                                }
                            }
                            if (error) {
                                continue FOR_AULA;
                            }
                        }
                        //CAMBIO HECHO POR DAVID PINEDA SE AGREGÓ key Y mapAulaDiaHora AL SIGUIENTE METODO
                        this.saveHorarioAula(aula, seccion, horarioSeccion, eventoCicloDictado, mapsHorarioAulaByAulaForFechasModular, mapsHorarioAulaByAulaForDictado, key);
                        // FIN HECHO POR DAVID PINEDA
                        /*  HorarioAula horarioAulaSave = new HorarioAula(seccion, horarioSeccion.getDia(), horarioSeccion.getHora(), aula);
                        horarioAulaSave.setTipoEnum(TipoHorarioAulaEnum.DICT);
                        horarioAulaSave.setEstadoEnum(EstadoHorarioAulaEnum.ACT);
                        if (mapsHorarioAulaByAulaForFechasModular != null) {
                            horarioAulaSave.setFechaInicio(seccion.getGrupoSeccion().getFechaInicioModular());
                            horarioAulaSave.setFechaFin(seccion.getGrupoSeccion().getFechaFinModular());
                            horarioAulaDAO.save(horarioAulaSave);
                        } else {
                            horarioAulaSave.setFechaInicio(eventoCicloDictado.getFechaInicio());
                            horarioAulaSave.setFechaFin(eventoCicloDictado.getFechaFin());
                            if (aula.getHorariosAula() == null) {
                                aula.setHorarioReservaAula(new ArrayList<>());
                            }
                            aula.getHorariosAula().add(horarioAulaSave.clone());
                            horarioAulaDAO.save(horarioAulaSave);
                        }*/
                    }
                    Seccion seccionUpd = new Seccion(seccion.getId());
                    seccionUpd.setAula(aula);
                    seccionUpd.setFechaAsignacionAuto(ds.getFechaAccionAudit());
                    seccionUpd.setAulaAsignadaAuto(Boolean.TRUE);
                    seccionUpd.setHorarioSeccion(seccion.getHorarioSeccion());
                    this.updateSeccion(seccionUpd);
                    if (aulasAsignadas.contains(aula)) {
                        continue;
                    }
                    aulasAsignadas.add(aula);

                    logger.debug("Seccion {}, Con Aula {} Asignada",
                            seccion.getCodigo(), aula.getCodigo());
                    if (tipoCarpeta.getCodigo().equals(TipoCarpetaEnum.AUL.name())) {
                        seccionesTipoAul++;
                    }
                    if (tipoCarpeta.getCodigo().equals(TipoCarpetaEnum.LAB.name())) {
                        seccionesTipoLab++;
                    }
                    seccionesAsignadas = aulasAsignadas.size();
                    break FOR_DIST_PAB;
                }
            }
        }

        if (asignacionAula == null) {
            asignacionAula = new AsignacionAula();
        }
        asignacionAula.setCicloAcademico(ds.getCicloAcademico());
        asignacionAula.setSeccionesModificadas(BigDecimal.ZERO.intValue());
        asignacionAula.setSeccionesProgramadas(seccionesProgramadas);
        if (asignacionAula.getId() == null) {
            asignacionAula.setFechaAsignacion(ds.getFechaAccionAudit());
            asignacionAula.setSeccionesAsignadas(seccionesAsignadas);
            asignacionAula.setSeccionesTipoAula(seccionesTipoAul);
            asignacionAula.setSeccionesTipoLab(seccionesTipoLab);
            asignacionAula.setFechaRegistro(ds.getFechaAccionAudit());
            asignacionAula.setUserRegistro(ds.getUsuario());
            asignacionAula.setSeccionesModificadas(BigDecimal.ZERO.intValue());
            //   asignacionAula.setSeccionesProgramadas( ); Todo
            asignacionAulaDAO.save(asignacionAula);
        } else {
            asignacionAula.setSeccionesAsignadas(seccionesAsignadas);
            asignacionAula.setSeccionesTipoAula(seccionesTipoAul);
            asignacionAula.setSeccionesTipoLab(seccionesTipoLab);
            asignacionAula.setFechaRegistro(ds.getFechaAccionAudit());
            asignacionAula.setUserRegistro(ds.getUsuario());
            asignacionAulaDAO.update(asignacionAula);
        }
        return asignacionAula;
    }

    public void saveHorarioAula(Aula aula, Seccion seccion, HorarioSeccion horarioSeccion,
            EventoCicloAcademico eventoCicloDictado,
            Map<Long, List<HorarioAula>> mapsHorarioAulaByAulaForFechasModular, Map<String, List<HorarioAula>> mapsHorarioAulaByAulaForDictado, String key) {
        HorarioAula horarioAulaSave = new HorarioAula(seccion, horarioSeccion.getDia(), horarioSeccion.getHora(), aula);
        horarioAulaSave.setTipoEnum(TipoHorarioAulaEnum.DICT);
        horarioAulaSave.setEstadoEnum(EstadoHorarioAulaEnum.ACT);
        if (mapsHorarioAulaByAulaForFechasModular != null) {
            Date fechaInicioClases = new DateTime(seccion.getGrupoSeccion().getFechaInicioModular()).withDayOfWeek(1).toDate();
            Date hoy = new LocalDate().toDate();
            Date lunes = new DateTime(hoy).withDayOfWeek(1).toDate();
//            if (lunes.after(fechaInicioClases)) {
//                fechaInicioClases = lunes;
//            }

            horarioAulaSave.setFechaInicio(fechaInicioClases);
            horarioAulaSave.setFechaFin(seccion.getGrupoSeccion().getFechaFinModular());
            System.out.println("------- Modular" + horarioAulaSave.toString());
            horarioAulaDAO.save(horarioAulaSave);
        } else {

            Date fechaInicioClases = new DateTime(eventoCicloDictado.getFechaInicio()).withDayOfWeek(1).toDate();
            Date hoy = new LocalDate().toDate();
            Date lunes = new DateTime(hoy).withDayOfWeek(1).toDate();
//            if (lunes.after(fechaInicioClases)) {
//                fechaInicioClases = lunes;
//            }

            horarioAulaSave.setFechaInicio(fechaInicioClases);
            horarioAulaSave.setFechaFin(eventoCicloDictado.getFechaFin());
            if (aula.getHorariosAula() == null) {
                aula.setHorarioReservaAula(new ArrayList<>());
            }
            aula.getHorariosAula().add(horarioAulaSave.clone());
            System.out.println("------- Evento" + horarioAulaSave.toString());
            horarioAulaDAO.save(horarioAulaSave);
        }
        //INICIO CAMBIO HECHO POR DAVID PINEDA
        if (TypesUtil.getListNotNull(mapsHorarioAulaByAulaForDictado.get(key)).isEmpty()) {
            mapsHorarioAulaByAulaForDictado.put(key, Arrays.asList(horarioAulaSave));
        } else {
            mapsHorarioAulaByAulaForDictado.get(key).add(horarioAulaSave);
        }
        //FIN CAMBIO HECHO POR DAVID PINEDA

    }

    public void fillHorarioAula(Aula aula, Map<Long, List<HorarioAula>> mapsHorarioAulaByAulaForDictado, Map<Long, List<HorarioAula>> mapsHorarioAulaByAulaForFechasModular) {
        List<HorarioAula> horariosAula = mapsHorarioAulaByAulaForDictado.get(aula.getId());
        if (mapsHorarioAulaByAulaForFechasModular != null) {
            horariosAula = mapsHorarioAulaByAulaForFechasModular.get(aula.getId()) != null ? mapsHorarioAulaByAulaForFechasModular.get(aula.getId()) : new ArrayList<>();
        }
        if (horariosAula == null) {
            horariosAula = new ArrayList<>();
        }
        aula.setHorariosAula(horariosAula);
    }

    public TipoCarpeta getTipoCarpeta(Seccion seccion, List<CursoCicloAcademico> cursosCiclosAcademicos) {
        TipoCarpeta tipoCarpeta = null;
        Optional<TipoCarpeta> oTipoCarpeta = Optional.ofNullable(seccion.getTipoCarpeta());
        tipoCarpeta = oTipoCarpeta.isPresent() ? oTipoCarpeta.get() : null;

        CursoCicloAcademico cursoCicloAcademico = cursosCiclosAcademicos.stream()
                .filter(x -> x.getCurso().equals(seccion.getGrupoSeccion().getCurso()))
                .findFirst().orElse(null);
        if (tipoCarpeta == null && cursoCicloAcademico != null) {
            if (seccion.isTipoSeccionTCUR() || seccion.isTipoSeccionTEO()) {
                oTipoCarpeta = Optional.ofNullable(cursoCicloAcademico.getTipoCarpetaTeoria());
            } else if (seccion.isTipoSeccionPRA() || seccion.isTipoSeccionPCUR()) {
                oTipoCarpeta = Optional.ofNullable(cursoCicloAcademico.getTipoCarpetaPractica());
            }
            tipoCarpeta = oTipoCarpeta.isPresent() ? oTipoCarpeta.get() : null;
        }
        if (tipoCarpeta == null) {
            if (seccion.isTipoSeccionTCUR() || seccion.isTipoSeccionTEO()) {
                oTipoCarpeta = Optional.ofNullable(seccion.getGrupoSeccion().getCurso().getTipoCarpetaTeoria());
            } else if (seccion.isTipoSeccionPRA() || seccion.isTipoSeccionPCUR()) {
                oTipoCarpeta = Optional.ofNullable(seccion.getGrupoSeccion().getCurso().getTipoCarpetaPractica());
            }
            tipoCarpeta = oTipoCarpeta.isPresent() ? oTipoCarpeta.get() : null;
        }
        return tipoCarpeta;
    }

    public void updateSeccion(Seccion seccion) {
        seccionDAO.updateAsignacionAula(seccion);
        for (HorarioSeccion horarioSeccion : seccion.getHorarioSeccion()) {
            horarioSeccion.setAula(seccion.getAula());
            horarioSeccionDAO.update(horarioSeccion);
        }
    }

    public List<Aula> allAulasOeraWithHorario(EventoCicloAcademico eventoCicloDictado) {

        List<HorarioAula> horarioAulas = horarioAulaDAO.allByRango(eventoCicloDictado.getFechaInicio(), eventoCicloDictado.getFechaFin());
        Map<Long, List<HorarioAula>> mapsHorarioAulaByAula = TypesUtil.convertListToMapList("aula.id", horarioAulas);
        List<Aula> aulas = aulaDAO.allByOficinaSupervisora(OficinaEnum.OERA, EstadoEnum.ACT);
        for (Aula aula : aulas) {
            List<HorarioAula> horariosAulas = mapsHorarioAulaByAula.get(aula.getId());
            aula.setHorariosAula(horariosAulas == null ? new ArrayList<>() : horariosAulas);
        }
        return aulas;
    }

}
