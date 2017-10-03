package pe.edu.lamolina.pivot.controller.academico.plancalificacurso;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.pivot.controller.academico.cargaacademica.CargaAcademicaService;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.PlanCalificacionDAO;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.DocenteSeccion;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.model.academico.PlanCalificacion;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class PlanCalificaCursoServiceImp implements PlanCalificaCursoService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    GrupoSeccionDAO grupoSeccionDAO;
    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;
    @Autowired
    PlanCalificacionDAO planCalificacionDAO;
    @Autowired
    DocenteSeccionDAO docenteSeccionDAO;

    @Autowired
    CargaAcademicaService cargaAcademicaService;

    @Override
    @Transactional
    public void reasignarPlanDocenteCurso(CicloAcademico ciclo, DataSessionPivot ds) {
        CicloAcademico cicloAnterior = cicloAcademicoDAO.findAnteriorRegular(ciclo);
        List<DocenteCursoPlan> profeCursoPlanesAntes = grupoSeccionDAO.allDocenteCursoPlanByCiclo(cicloAnterior);
        Map<String, Long> mapPlanesStr = new LinkedHashMap();
        
        for (DocenteCursoPlan profeCursoPlan : profeCursoPlanesAntes) {
            if (profeCursoPlan.getCantidadPlanes() > 1) {
                continue;
            }
            mapPlanesStr.put(profeCursoPlan.getIdCurso() + "-" + profeCursoPlan.getIdDocente(), profeCursoPlan.getIdPlanCalifica());
        }

        List<DocenteSeccion> profesSinPlan = docenteSeccionDAO.allPendientePlan(ciclo);
        for (DocenteSeccion profeSecc : profesSinPlan) {
            Curso curso = profeSecc.getSeccion().getGrupoSeccion().getCurso();
            GrupoSeccion gpoSecc = profeSecc.getSeccion().getGrupoSeccion();
            Long idPlan = mapPlanesStr.get(curso.getId() + "-" + profeSecc.getDocente().getId());
            System.out.println(curso.getId() + "-" + profeSecc.getDocente().getId() + "-" + idPlan);
            if (idPlan != null) {
                cargaAcademicaService.aceptarPlanCalificacionSession(new PlanCalificacion(idPlan), curso.getId(), gpoSecc.getId(), ds);
            }
        }

    }

}
