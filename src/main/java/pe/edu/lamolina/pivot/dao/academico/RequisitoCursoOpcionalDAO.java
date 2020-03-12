package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CursoCurricula;
import pe.edu.lamolina.model.academico.CursoOpcionalCurricula;
import pe.edu.lamolina.model.academico.RequisitoCursoOpcional;

public interface RequisitoCursoOpcionalDAO extends EasyDAO<RequisitoCursoOpcional> {

    List<RequisitoCursoOpcional> allPostRequisitosByCursosCurricula(List<CursoCurricula> cursosCurricula);

    List<RequisitoCursoOpcional> allRequisitosByCursosElectivos(List<CursoOpcionalCurricula> cursosElectivos);

    List<RequisitoCursoOpcional> allPostRequisitosByCursosElectivo(List<CursoOpcionalCurricula> cursosElectivos);

    List<RequisitoCursoOpcional> allByCursoElectivo(CursoOpcionalCurricula cursoElectivo);

    List<RequisitoCursoOpcional> allPostRequisitosByCursoElectivo(CursoOpcionalCurricula cursoElectivo);

    List<RequisitoCursoOpcional> allRequisitoOpcionalDe(CursoCurricula cursosCurricula);
}
