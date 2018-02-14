package pe.edu.lamolina.pivot.controller.comun;

import java.util.List;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.PlanCalificacion;
import pe.edu.lamolina.model.general.Empresa;
import pe.edu.lamolina.model.general.Pais;
import pe.edu.lamolina.model.general.Ubicacion;
import pe.edu.lamolina.model.general.Universidad;

public interface BuscarService {

    List<Curso> allCursosSCA(String nombre, PlanCalificacion planCalificacion, CicloAcademico ciclo);

    List<Ubicacion> allDistritosByName(String nombre);

    List<DepartamentoAcademico> allDepartamentosByName(String nombre);

    List<Docente> allCoordinadoresByIdDptoName(Long idDpto, String nombre);

    List<Pais> allPaisesByName(String nombre);

    List<Universidad> allUniversidadByName(String nombre);

    public List<Empresa> allEmpresaByName(Pais pais, String nombre);

}
