package pe.edu.lamolina.pivot.controller.programacionhorarios.asignacionaula;

import java.beans.Transient;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.DistanciaPabellon;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.EventoCicloAcademico;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.EventoAcademicoEnum;
import pe.edu.lamolina.model.enums.OficinaEnum;
import pe.edu.lamolina.model.enums.SeccionEstadoEnum;
import pe.edu.lamolina.model.enums.TipoCarpetaEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.horario.HorarioAula;
import pe.edu.lamolina.pivot.dao.academico.AsignacionAulaDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.DepartamentoAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.DistanciaPabellonDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.EventoCicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.general.AulaDAO;
import pe.edu.lamolina.pivot.dao.general.OficinaDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioAulaDAO;
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
        
        List<Seccion> seccionesByCiclo = seccionDAO.allForAsignacionAulaByCiclo(ds.getCicloAcademico(), SeccionEstadoEnum.ACT);
        int seccionesProgramadas = seccionesByCiclo.size();
        
        seccionesByCiclo = seccionesByCiclo.stream()
                .filter(x -> x.getTipoCarpeta() != null)
                .filter(x -> x.getAula() == null)
                .collect(Collectors.toList());

        //Ordernar por horas semanalaes de mayor a menor
        Collections.sort(seccionesByCiclo, (p1, p2) -> p2.getHorasSemanales().compareTo(p1.getHorasSemanales()));
        
        List<Aula> aulas = this.allAulasOeraWithHorario(ds.getCicloAcademico());
        aulas = aulas.stream().filter(x -> x.getTipoCarpeta() != null).collect(Collectors.toList());
        
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
            Aula aulaWithHorario = aulas.stream().filter(x -> x.equals(seccion.getAula())).findFirst().orElse(null);
            if (seccion.getGrupoSeccion().getFechaInicioModular() != null && seccion.getGrupoSeccion().getFechaFinModular() != null) {
                List<HorarioAula> horariosAulas = horarioAulaDAO.allByRango(seccion.getGrupoSeccion().getFechaInicioModular(), seccion.getGrupoSeccion().getFechaFinModular(), aulaWithHorario);
                aulaWithHorario = aulaWithHorario.clone();
                aulaWithHorario.setHorariosAula(horariosAulas);
            }
            DocenteSeccion docenteSeccionPrincipal = mapDocentesSeccionPrincipalesBySeccion.get(seccion.getId());
            
            DepartamentoAcademico departamentoAcademicoDocente = docenteSeccionPrincipal.getDocente().getDepartamentoAcademico();
            if (departamentoAcademicoDocente == null) {
                continue;
            }
            Boolean esDocenteConDiscapacidad = docenteSeccionPrincipal.getDocente().getPersona().getConDiscapacidad() == 1;
            //    logger.debug("Seccion {}", seccion.getCodigo());
            seccion.settDocenteSeccion(docenteSeccionPrincipal);
            
            List<DistanciaPabellon> distanciaPabellonByDepartamento = mapDistanciaPabellones.get(departamentoAcademicoDocente.getId());
            if (distanciaPabellonByDepartamento == null) {
                continue;
            }
            FOR_DIST_PAB:
            for (DistanciaPabellon distanciaPabellon : distanciaPabellonByDepartamento) {
                List<Aula> aulasByPabellon = aulas.stream()
                        .filter(x -> x.getAulaSuperior().equals(distanciaPabellon.getPabellon()))
                        .filter(x -> x.getTipoCarpeta().equals(seccion.getTipoCarpeta()))
                        .filter(x -> x.getAforo() >= seccion.getMatriculados())
                        .collect(Collectors.toList());
                //Ordenamos las aulas por aforo de menor a mayor
                Collections.sort(aulasByPabellon, (p1, p2) -> p1.getAforo().compareTo(p2.getAforo()));
                /*       logger.debug("Departamento {}, Pabellon {}, Distancia {}, Aulas {}", distanciaPabellon.getDepartamentoAcademico().getId(),
                        distanciaPabellon.getPabellon().getId(),
                        distanciaPabellon.getDistancia(),
                        aulasByPabellon.size());*/
                
                for (Aula aula : aulasByPabellon) {
                    if (aulasAsignadas.contains(aula)) {
                        continue;
                    }
                    Integer piso = aula.getPiso() == null ? BigDecimal.ONE.intValue() : aula.getPiso();
                    
                    if (esDocenteConDiscapacidad) {
                        if (piso != BigDecimal.ONE.intValue()) {
                            continue;
                        }
                    }
                    
                    aulasAsignadas.add(aula);
                    seccionDAO.updateAsignacionAula(seccion);
                    logger.debug("Seccion {}, Con Aula {} Asignada",
                            seccion.getCodigo(), aula.getCodigo());
                    if (seccion.getTipoCarpeta().getCodigo().equals(TipoCarpetaEnum.AUL.name())) {
                        seccionesTipoAul++;
                    }
                    if (seccion.getTipoCarpeta().getCodigo().equals(TipoCarpetaEnum.LAB.name())) {
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
    
    public List<Aula> allAulasOeraWithHorario(CicloAcademico cicloAcademico) {
        EventoCicloAcademico eventoCicloDictado = eventoCicloAcademicoDAO.findActivoByCicloTipoEvento(cicloAcademico, EventoAcademicoEnum.CLASES_PRE);
        
        List<HorarioAula> horarioAulas = horarioAulaDAO.allByRango(eventoCicloDictado.getFechaInicio(), eventoCicloDictado.getFechaFin());
        Map<Long, List<HorarioAula>> mapsHorarioAulaByAula = TypesUtil.convertListToMapList("aula.id", horarioAulas);
        List<Aula> aulas = aulaDAO.allByOficinaSupervisora(OficinaEnum.OERA, EstadoEnum.ACT);
        for (Aula aula : aulas) {
            List<HorarioAula> horariosAulas = mapsHorarioAulaByAula.get(aula.getId());
            aula.setHorariosAula(horariosAulas);
        }
        return aulas;
    }
    
}
