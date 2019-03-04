package pe.edu.lamolina.pivot.controller.programacionhorarios.asignacionaula;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.AsignacionAula;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.SeccionEstadoEnum;
import pe.edu.lamolina.pivot.dao.academico.AsignacionAulaDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.general.AulaDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = false)
public class AsignacionAulaServiceImp implements AsignacionAulaService {
    
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
    
    @Override
    public CicloAcademico findCiclo(CicloAcademico cicloAcademico) {
        return cicloAcademicoDAO.find(cicloAcademico);
    }
    
    @Override
    public AsignacionAula findAsignacionAulaByCiclo(CicloAcademico cicloAcademico) {
        return asignacionAulaDAO.findByCiclo(cicloAcademico);
    }
    
    @Override
    public void procesarAsignacionAulas(AsignacionAula asignacionAula, DataSessionPivot ds) {
        /*    List<GrupoSeccion> gruposSeccionesByCiclo = grupoSeccionDAO.allByCiclo(ds.getCicloAcademico());
        gruposSeccionesByCiclo = gruposSeccionesByCiclo.stream()
                .filter(x -> x.isEstadoActivo())
                .collect(Collectors.toList());*/
        List<Seccion> seccionesByCiclo = seccionDAO.allByCiclo(ds.getCicloAcademico(), SeccionEstadoEnum.ACT);
        seccionesByCiclo = seccionesByCiclo.stream()
                .filter(x -> x.getAula() == null)
                .collect(Collectors.toList());
        
        List<DocenteSeccion> docentesSeccionPrincipalesByCiclo
                = docenteSeccionDAO.allByCiclo(ds.getCicloAcademico());
        docentesSeccionPrincipalesByCiclo = docentesSeccionPrincipalesByCiclo.stream()
                .filter(x -> x.getSeccion().getAula() == null)
                .filter(x -> x.isEstadoActivado())
                .filter(x -> x.getPrincipal() == BigDecimal.ONE.intValue())
                .collect(Collectors.toList());
        Map<Long, DocenteSeccion> mapDocentesSeccionPrincipalesBySeccion = TypesUtil.convertListToMap("seccion.id", docentesSeccionPrincipalesByCiclo);
        
        for (Seccion seccion : seccionesByCiclo) {
            DocenteSeccion docenteSeccionPrincipal = mapDocentesSeccionPrincipalesBySeccion.get(seccion.getId());
            
        }
    }
    
}
