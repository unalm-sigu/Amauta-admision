package pe.edu.lamolina.pivot.controller.programacionhorarios.asignacionaula;

import java.beans.Transient;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.PhobosException;
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
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.TipoCarpeta;
import pe.edu.lamolina.model.horario.HorarioAula;
import pe.edu.lamolina.model.horario.HorarioSeccion;
import pe.edu.lamolina.pivot.dao.academico.AsignacionAulaDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoCicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.DepartamentoAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.DistanciaPabellonDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.EventoCicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.general.AulaDAO;
import pe.edu.lamolina.pivot.dao.general.OficinaDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioAulaDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioSeccionDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

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
        return asignacionAulaDAO.findByCiclo(cicloAcademico);
    }

    @Override
    @Transient
    public AsignacionAula procesarAsignacionAulas(AsignacionAula asignacionAula, DataSessionPivot ds) {
        List<CursoCicloAcademico> cursosCiclosAcademicos = cursoCicloAcademicoDAO.allByCiclo(ds.getCicloAcademico(), CicloAcademicoEstadoEnum.ACT);

        List<Seccion> seccionesByCiclo = seccionDAO.allForAsignacionAulaByCiclo(ds.getCicloAcademico(), SeccionEstadoEnum.ACT);
        List<HorarioSeccion> horarios = horarioSeccionDAO.allBySeccionesSortByDiaHora(seccionesByCiclo);
        Map<Long, List<HorarioSeccion>> mapHorariosBySeccion = TypesUtil.convertListToMapList("seccion.id", horarios);

        int seccionesProgramadas = seccionesByCiclo.size();

        seccionesByCiclo = seccionesByCiclo.stream()
                .filter(x -> x.getAula() == null)
                .collect(Collectors.toList());

        //Ordernar por horas semanalaes de mayor a menor
        Collections.sort(seccionesByCiclo, (p1, p2) -> p2.getHorasSemanales().compareTo(p1.getHorasSemanales()));

        EventoCicloAcademico eventoCicloDictado = eventoCicloAcademicoDAO.findActivoByCicloTipoEvento(ds.getCicloAcademico(), EventoAcademicoEnum.CLASES_PRE);
        List<Aula> aulas = aulaDAO.allByOficinaSupervisora(OficinaEnum.OERA, EstadoEnum.ACT);
        aulas = aulas.stream().filter(x -> x.getTipoCarpeta() != null).collect(Collectors.toList());

        Map<Long, List<Aula>> mapAulasByModulo = TypesUtil.convertListToMapList("aulaSuperior.id", aulas);
        logger.debug("Aulas encontradas (id´'s) {}", aulas.stream().map(x -> x.getId()).collect(Collectors.toList()));
        logger.debug("Modulos Encontrados Por Aula (id´'s) {}", new ArrayList(mapAulasByModulo.keySet()));

        List<HorarioAula> horarioAulasDictado = horarioAulaDAO.allByRango(eventoCicloDictado.getFechaInicio(), eventoCicloDictado.getFechaFin());
        Map<Long, List<HorarioAula>> mapsHorarioAulaByAulaForDictado = TypesUtil.convertListToMapList("aula.id", horarioAulasDictado);

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
            TipoCarpeta tipoCarpeta = this.getTipoCarpeta(seccion, cursosCiclosAcademicos);
            List<HorarioSeccion> horariosSecciones = mapHorariosBySeccion.get(seccion.getId());
            seccion.setHorarioSeccion(horariosSecciones);
            if (seccion.getHorarioSeccion() == null) {
                continue;
            }
            Map<Long, List<HorarioAula>> mapsHorarioAulaByAulaForFechasModular = null;
            if (seccion.getGrupoSeccion().getFechaInicioModular() != null && seccion.getGrupoSeccion().getFechaFinModular() != null) {
                List<HorarioAula> horariosAulas = horarioAulaDAO.allByRango(seccion.getGrupoSeccion().getFechaInicioModular(), seccion.getGrupoSeccion().getFechaFinModular());
                mapsHorarioAulaByAulaForFechasModular = TypesUtil.convertListToMapList("aula.id", horariosAulas);
            }
            DocenteSeccion docenteSeccionPrincipal = mapDocentesSeccionPrincipalesBySeccion.get(seccion.getId());
            DepartamentoAcademico departamentoAcademicoDocente = docenteSeccionPrincipal.getDocente().getDepartamentoAcademico();
            if (departamentoAcademicoDocente == null) {
                continue;
            }
            Boolean esDocenteConDiscapacidad = docenteSeccionPrincipal.getDocente().getPersona().getConDiscapacidad() == 1;
            seccion.settDocenteSeccion(docenteSeccionPrincipal);

            List<DistanciaPabellon> distanciaPabellonByDepartamento = mapDistanciaPabellones.get(departamentoAcademicoDocente.getId());
            if (distanciaPabellonByDepartamento == null) {
                continue;
            }
            FOR_DIST_PAB:
            for (DistanciaPabellon distanciaPabellon : distanciaPabellonByDepartamento) {
                List<Aula> aulasByPabellon = mapAulasByModulo.get(distanciaPabellon.getPabellon().getId());
                aulasByPabellon = aulas.stream()
                        .filter(x -> x.getAforo() >= seccion.getMatriculados())
                        .filter(x -> tipoCarpeta.getId().compareTo(x.getTipoCarpeta().getId()) == 0)
                        .collect(Collectors.toList());
                //Ordenamos las aulas por aforo de menor a mayor
                Collections.sort(aulasByPabellon, (p1, p2) -> p1.getAforo().compareTo(p2.getAforo()));
                FOR_AULA:
                for (Aula aula : aulasByPabellon) {
                    List<HorarioAula> horariosAula = mapsHorarioAulaByAulaForDictado.get(aula.getId());
                    if (mapsHorarioAulaByAulaForFechasModular != null) {
                        horariosAula = mapsHorarioAulaByAulaForFechasModular.get(aula.getId());
                    }
                    aula.setHorariosAula(horariosAula);
                    Integer piso = aula.getPiso() == null ? BigDecimal.ONE.intValue() : aula.getPiso();
                    if (esDocenteConDiscapacidad && piso != BigDecimal.ONE.intValue()) {
                        continue;
                    }

                    for (HorarioSeccion horarioSeccion : seccion.getHorarioSeccion()) {
                        HorarioAula horarioAula = aula.getHorariosAula()
                                .stream()
                                .filter(x -> x.getHoraDia().equals(horarioSeccion.getHoraDia()))
                                .filter(x -> x.getDia().equals(horarioSeccion.getDia()))
                                .filter(x -> x.getHora().equals(horarioSeccion.getHora()))
                                .findFirst().orElse(null);
                        if (horarioAula != null) {
                            continue FOR_AULA;
                        }
                        HorarioAula horarioAulaSave = new HorarioAula(seccion, horarioSeccion.getDia(), horarioSeccion.getHora(), aula);
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
                        }
                    }
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
