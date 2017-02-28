package pe.edu.lamolina.pivot.controller.academico.acta;

import java.util.ArrayList;
import java.util.List;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.pivot.dao.academico.AlumnoEvaluacionDAO;
import pe.edu.lamolina.pivot.dao.academico.DepartamentoAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.pivot.dao.auditoria.ControlDeActasDAO;
import pe.edu.lamolina.pivot.model.academico.AlumnoEvaluacion;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.pivot.model.academico.Docente;
import pe.edu.lamolina.pivot.model.academico.DocenteSeccion;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.model.academico.Seccion;
import pe.edu.lamolina.pivot.model.auditoria.ControlDeActas;
import pe.edu.lamolina.pivot.model.auditoria.ControlDeActasDet;
import pe.edu.lamolina.pivot.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.zelper.enums.EstadoGrupoSeccionEnum;

@Service
@Transactional(readOnly = true)
public class ActaServiceImpl implements ActaService {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    DepartamentoAcademicoDAO departamentoAcademicoDAO;
    
    @Autowired
    GrupoSeccionDAO grupoSeccionDAO;
    
    @Autowired
    DocenteSeccionDAO docenteSeccionDAO;
    
    @Autowired
    AlumnoEvaluacionDAO alumnoEvaluacionDAO;
    
    @Autowired
    ControlDeActasDAO controlDeActasDAO;
    
    @Override
    public List<DepartamentoAcademico> allActiveDepartamentosAcademicos(DynatableFilter filter) {
        return departamentoAcademicoDAO.allActiveByDyna(filter);
    }
    
    @Override
    public DepartamentoAcademico findDepartamento(Long idDepartamentoAcad) {
        return departamentoAcademicoDAO.find(idDepartamentoAcad);
    }
    
    @Override
    public List<GrupoSeccion> allGrupoSeccionByFilter(CicloAcademico cicloAcademico, DepartamentoAcademico departamentoAcademico) {
        return grupoSeccionDAO.allByFilter(null, cicloAcademico, departamentoAcademico);
    }
    
    @Override
    public List<GrupoSeccion> allGrupoSeccionByFilterDyna(CicloAcademico cicloAcademico, DepartamentoAcademico departamentoAcademico, DynatableFilter dynatableFilter) {
        return grupoSeccionDAO.allByFilter(cicloAcademico, departamentoAcademico, dynatableFilter);
    }
    
    @Override
    public DocenteSeccion findDocenteSeccionByFilter(Docente docente, Seccion seccion) {
        return docenteSeccionDAO.findByFilter(docente, seccion);
    }
    
    @Override
    public List<DocenteSeccion> allDocenteSeccionByGrupo(GrupoSeccion grupoSeccion) {
        return docenteSeccionDAO.allByGrupoSeccion(grupoSeccion);
    }
    
    @Override
    @Transactional
    public void reabrirGrupo(GrupoSeccion grupoSeccion, Usuario usuario) {
        logger.debug("Service method reabrirGrupo");
        DateTime today = new DateTime();
        grupoSeccion = grupoSeccionDAO.find(grupoSeccion.getId());
        logger.debug("Id Grupo Seccion {}", grupoSeccion.getId());
        
        ControlDeActas controlDeActas = new ControlDeActas();
        controlDeActas.setFechaCierreActa(grupoSeccion.getFechaCierreActa());
        controlDeActas.setUsuarioCierraActa(grupoSeccion.getUsuarioCierraActa());
        controlDeActas.setFechaRegistro(today.toDate());
        controlDeActas.setGrupoSeccion(grupoSeccion);
        controlDeActas.setUsuarioRegistro(usuario);
        controlDeActas.setVersion(grupoSeccion.getVersion());
        
        controlDeActas.setControlDeActasDets(new ArrayList<>());
        
        List<AlumnoEvaluacion> evaluacionesBySeccion = alumnoEvaluacionDAO.allByFilter(null, grupoSeccion.getId(), null);
        logger.debug("Cantidad de evaluaciones del grupo {}", evaluacionesBySeccion.size());
        if (evaluacionesBySeccion.isEmpty()) {
            throw new PhobosException("Error. El grupo no cuenta con notas ingresadas.");
        }
        
        ControlDeActasDet controlDeActasDet = null;
        for (AlumnoEvaluacion alumnoEvaluacion : evaluacionesBySeccion) {
            controlDeActasDet = new ControlDeActasDet();
            controlDeActasDet.setControlDeActas(controlDeActas);
            controlDeActasDet.setEvaluacion(alumnoEvaluacion.getEvaluacion());
            controlDeActasDet.setAlumno(alumnoEvaluacion.getAlumno());
            if (ObjectUtil.getParentTree(alumnoEvaluacion.getEvaluacion(), "evaluacionSuperior.id") != null) {
                controlDeActasDet.setEvaluacionSuperior(alumnoEvaluacion.getEvaluacion().getEvaluacionSuperior());
            } else {
                controlDeActasDet.setEvaluacionSuperior(null);
            }
            controlDeActasDet.setFechaIngresoNota(alumnoEvaluacion.getFechaIngresoNota());
            controlDeActasDet.setUsuarioIngresoNota(alumnoEvaluacion.getUsuarioIngresoNota());
            controlDeActasDet.setNota(alumnoEvaluacion.getNota());
            controlDeActasDet.setNotaNumerica(alumnoEvaluacion.getValorNumerico());
            controlDeActasDet.setNumeroEvaluacion(alumnoEvaluacion.getEvaluacion().getNumero());
            controlDeActasDet.setSeccion(alumnoEvaluacion.getEvaluacion().getSeccionResponsable());
            controlDeActasDet.setTipoSeccion(alumnoEvaluacion.getEvaluacion().getTipoSeccion());
            
            StringBuilder codigoPadre = new StringBuilder();
            StringBuilder codigoHijo = new StringBuilder();
            if (ObjectUtil.getParentTree(alumnoEvaluacion.getEvaluacion(), "evaluacionSuperior.id") != null) {
                codigoPadre.append(alumnoEvaluacion.getEvaluacion().getEvaluacionSuperior().getTipoEvaluacion().getCodigo()).append(alumnoEvaluacion.getEvaluacion().getEvaluacionSuperior().getNumero());
            }
            codigoHijo.append(alumnoEvaluacion.getEvaluacion().getTipoEvaluacion().getCodigo()).append(alumnoEvaluacion.getEvaluacion().getNumero());
            StringBuilder nombreEvaluacion = new StringBuilder();
            if (codigoPadre.length() > 0) {
                nombreEvaluacion.append("(").append(codigoPadre).append(")");
            }
            nombreEvaluacion.append(codigoHijo);
            controlDeActasDet.setEvaluacionDescripcion(nombreEvaluacion.toString());
            controlDeActas.getControlDeActasDets().add(controlDeActasDet);
        }
        
        controlDeActasDAO.save(controlDeActas);
        grupoSeccion.setEstadoGrupoEnum(EstadoGrupoSeccionEnum.RAB);
        grupoSeccion.setVersion("" + (Integer.valueOf(grupoSeccion.getVersion()) + 1));
        grupoSeccionDAO.update(grupoSeccion);
    }
    
}
