package pe.edu.lamolina.pivot.controller.rolexamen.cursomasivos;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.rolexamen.CursoMasivoExamen;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface CursoMasivosService {

    List<CursoMasivoExamen> allCursoMasivoExamenes(DynatableFilter filter, CicloAcademico cicloAcademico);

    List<Curso> allCursosByCicloActivo(CicloAcademico cicloAcademico);

    List<RolExamenes> allRolExamenesByCicloActivo(CicloAcademico cicloAcademico);  

    void save(CursoMasivoExamen cursoMasivosExamen, CicloAcademico cicloAcademico, DataSessionPivot ds);

    List<CursoMasivoExamen> listCursosMasivosExamenes(RolExamenes rolExamenes);

    List<Curso> allCursosByCiclo(String nombre, RolExamenes rolExamenes, CicloAcademico cicloAcademico);

}
