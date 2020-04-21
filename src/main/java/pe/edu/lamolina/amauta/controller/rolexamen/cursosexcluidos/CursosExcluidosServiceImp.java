package pe.edu.lamolina.amauta.controller.rolexamen.cursosexcluidos;

import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.rolexamen.CursoExcluido;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionExcluido;
import pe.edu.lamolina.model.rolexamen.SemanaExamen;
import pe.edu.lamolina.amauta.dao.academico.SeccionDAO;
import pe.edu.lamolina.amauta.dao.rolexamen.CursoExcluidoDAO;
import pe.edu.lamolina.amauta.dao.rolexamen.CursoMasivoExamenDAO;
import pe.edu.lamolina.amauta.dao.rolexamen.RolExamenesDAO;
import pe.edu.lamolina.amauta.dao.rolexamen.SeccionExcluidoDAO;
import pe.edu.lamolina.amauta.dao.rolexamen.SemanaExamenDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = false)
public class CursosExcluidosServiceImp implements CursosExcluidosService {
    
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    CursoMasivoExamenDAO cursoMasivoExamenDAO;
    
    @Autowired
    CursoExcluidoDAO cursoExcluidoDAO;
    
    @Autowired
    RolExamenesDAO rolExamenesDAO;
    
    @Autowired
    SemanaExamenDAO semanaExamenDAO;
    
    @Autowired
    SeccionDAO seccionDAO;
    
    @Autowired
    SeccionExcluidoDAO seccionExcluidoDAO;
    
    @Override
    public List<CursoExcluido> allCursosExcluidosByRolExamenes(RolExamenes rolExamenes) {
        return cursoExcluidoDAO.allByRolExamenes(rolExamenes);
    }
    
    @Override
    public List<RolExamenes> allRolExamenesByCicloActivo(CicloAcademico cicloAcademico) {
        return cursoMasivoExamenDAO.allRolExamenesByCicloActivo(cicloAcademico);
    }
    
    @Override
    @Transactional
    public void excluirCurso(CursoExcluido cursoExcluido, DataSessionPivot ds) {
        CursoExcluido cursoExcluidoFound = cursoExcluidoDAO.findActiveByCursoAndRolExamenes(cursoExcluido.getCurso(), cursoExcluido.getRolExamenes());
        if (cursoExcluidoFound != null) {
            throw new PhobosException("El curso ya esta excluido, verifique.");
        }
        
        cursoExcluido.setFechaRegistro(ds.getFechaAccionAudit());
        cursoExcluido.setUserRegistro(ds.getUsuario());
        cursoExcluido.setEstadoEnum(EstadoEnum.ACT);
        cursoExcluido.setEsExclusionCompleta(Boolean.FALSE);
        cursoExcluido.setSeccionesTotales(BigDecimal.ZERO.intValue());
        cursoExcluido.setSeccionesExcluidas(BigDecimal.ZERO.intValue());
        cursoExcluidoDAO.save(cursoExcluido);
        
        this.excluirSeccionesCurso(cursoExcluido, ds);
        this.comprobarCursoExcluido(cursoExcluido, ds);
    }
    
    @Override
    @Transactional
    public void excluirSeccion(SeccionExcluido seccionExcluido, DataSessionPivot ds) {
        Seccion seccion = seccionDAO.find(seccionExcluido.getSeccion());
        CursoExcluido cursoExcluido = cursoExcluidoDAO.
                findActiveByCursoAndRolExamenes(seccion.getGrupoSeccion().getCurso(), seccionExcluido.getRolExamenes());
        
        SeccionExcluido seccionExcluidoFound = seccionExcluidoDAO.findByRolExamenesAndSeccion(seccionExcluido.getRolExamenes(), seccion, EstadoEnum.ACT);
        Assert.isNull(seccionExcluidoFound, "La sección ya se encuentre excluida, verifique.");
        
        if (cursoExcluido == null) {
            cursoExcluido = new CursoExcluido();
            cursoExcluido.setCurso(seccion.getGrupoSeccion().getCurso());
            cursoExcluido.setEstadoEnum(EstadoEnum.ACT);
            cursoExcluido.setFechaRegistro(ds.getFechaAccionAudit());
            cursoExcluido.setRolExamenes(seccionExcluido.getRolExamenes());
            cursoExcluido.setUserRegistro(ds.getUsuario());
            cursoExcluido.setEsExclusionCompleta(Boolean.FALSE);
            cursoExcluido.setSeccionesExcluidas(BigDecimal.ZERO.intValue());
            cursoExcluido.setSeccionesTotales(BigDecimal.ZERO.intValue());
            cursoExcluidoDAO.save(cursoExcluido);
        }
        seccionExcluido.setEstadoEnum(EstadoEnum.ACT);
        seccionExcluido.setFechaRegistro(ds.getFechaAccionAudit());
        seccionExcluido.setUserRegistro(ds.getUsuario());
        seccionExcluido.setCursoExcluido(cursoExcluido);
        seccionExcluidoDAO.save(seccionExcluido);
        this.comprobarCursoExcluido(cursoExcluido, ds);
    }
    
    public void comprobarCursoExcluido(CursoExcluido cursoExcluido, DataSessionPivot ds) {
        List<SeccionExcluido> seccionesExcluidas = seccionExcluidoDAO.allByCursoExcluido(cursoExcluido, EstadoEnum.ACT);
        List<Seccion> secciones = seccionDAO.allByCicloAndCurso(ds.getCicloAcademico(), cursoExcluido.getCurso());
        CursoExcluido cursoExcluidoUpd = new CursoExcluido(cursoExcluido.getId());
        cursoExcluidoUpd.setSeccionesExcluidas(seccionesExcluidas.size());
        cursoExcluidoUpd.setSeccionesTotales(secciones.size());
        cursoExcluidoUpd.setEsExclusionCompleta(seccionesExcluidas.size() == secciones.size() ? Boolean.TRUE : Boolean.FALSE);
        cursoExcluidoDAO.updateColumns(cursoExcluidoUpd, "seccionesExcluidas", "seccionesTotales", "esExclusionCompleta");
    }
    
    public void excluirSeccionesCurso(CursoExcluido cursoExcluido, DataSessionPivot ds) {
        List<Seccion> secciones = seccionDAO.allByCicloAndCurso(ds.getCicloAcademico(), cursoExcluido.getCurso());
        for (Seccion seccion : secciones) {
            SeccionExcluido seccionExcluido = new SeccionExcluido();
            seccionExcluido.setEstadoEnum(EstadoEnum.ACT);
            seccionExcluido.setFechaRegistro(ds.getFechaAccionAudit());
            seccionExcluido.setRolExamenes(cursoExcluido.getRolExamenes());
            seccionExcluido.setSeccion(seccion);
            seccionExcluido.setUserRegistro(ds.getUsuario());
            seccionExcluido.setCursoExcluido(cursoExcluido);
            seccionExcluidoDAO.save(seccionExcluido);
        }
    }
    
    @Override
    @Transactional
    public void anularExclusion(CursoExcluido cursoExcluido, DataSessionPivot ds) {
        CursoExcluido cursoExcluidoUpd = new CursoExcluido(cursoExcluido.getId());
        cursoExcluidoUpd.setEstadoEnum(EstadoEnum.ANU);
        cursoExcluidoDAO.updateAnulacion(cursoExcluidoUpd);
        
        List<SeccionExcluido> seccionExcluidos = seccionExcluidoDAO.allByCursoExcluido(cursoExcluido, EstadoEnum.ACT);
        for (SeccionExcluido seccionExcluido : seccionExcluidos) {
            SeccionExcluido seccionExcluidoUpd = new SeccionExcluido(seccionExcluido.getId());
            seccionExcluidoUpd.setEstadoEnum(EstadoEnum.ANU);
            seccionExcluidoDAO.updateColumns(seccionExcluidoUpd, "estado");
        }
        this.comprobarCursoExcluido(cursoExcluido, ds);
    }
    
    @Override
    public RolExamenes findRolExamenes(long rolExamenId) {
        RolExamenes rolExamenes = rolExamenesDAO.find(rolExamenId);
        List<SemanaExamen> semanaExamens = semanaExamenDAO.allByRolExamenes(rolExamenes);
        rolExamenes.setSemanasExamen(semanaExamens);
        return rolExamenes;
    }
    
    @Override
    public List<Seccion> allSeccionesByCicloAndNombreLimit(CicloAcademico ciclo, RolExamenes rolExamenes, String nombre) {
        return seccionDAO.allByCicloAndNombreLimit(ciclo, rolExamenes, nombre);
    }
    
    @Override
    public List<SeccionExcluido> allSeccionesExcluidas(CursoExcluido cursoExcluido) {
        List<SeccionExcluido> seccionesExcluidas = seccionExcluidoDAO.allByCursoExcluido(cursoExcluido);
        return seccionesExcluidas;
    }
    
    @Override
    @Transactional
    public SeccionExcluido updateSeccionExcluidoEstado(SeccionExcluido seccionExcluidoForm, DataSessionPivot ds) {
        SeccionExcluido seccionExcluidoUpd = new SeccionExcluido(seccionExcluidoForm.getId());
        seccionExcluidoUpd.setEstado(seccionExcluidoForm.getEstado());
        seccionExcluidoDAO.updateColumns(seccionExcluidoUpd, "estado");
        
        SeccionExcluido seccionExcluidoDB = seccionExcluidoDAO.find(seccionExcluidoForm.getId());
        CursoExcluido cursoExcluido = seccionExcluidoDB.getCursoExcluido();
        this.comprobarCursoExcluido(cursoExcluido, ds);
        seccionExcluidoDB = seccionExcluidoDAO.find(seccionExcluidoForm.getId());
        return seccionExcluidoDB;
    }
    
}
