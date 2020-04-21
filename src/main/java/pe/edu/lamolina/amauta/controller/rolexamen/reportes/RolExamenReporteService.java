package pe.edu.lamolina.amauta.controller.rolexamen.reportes;

import pe.edu.lamolina.model.rolexamen.CursoMasivoExamen;
import pe.edu.lamolina.model.rolexamen.LetraGrupoRegular;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoEspecial;

import java.util.List;
import org.springframework.ui.Model;
import pe.edu.lamolina.model.academico.CicloAcademico;

public interface RolExamenReporteService {

    List<LetraGrupoRegular> allLetrasGrupoRegularByRolExamenes(RolExamenes rol);

    List<SeccionGrupoEspecial> allSeccionGrupoEspecialByRolExamenes(RolExamenes rol);

    List<CursoMasivoExamen> allCursoMasivoExamenByRolExamenes(RolExamenes rol);

    void infoReporteAulas(Model model, RolExamenes rol);

    public RolExamenes find(Long id);

}
