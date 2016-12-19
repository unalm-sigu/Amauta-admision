package pe.edu.lamolina.pivot.controller.comun;

import java.util.List;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.general.Ubicacion;

public interface BuscarService {

    List<Curso> allCursosSCA(String nombre, Long idDepartamentoAca, Long planCalificacion, Long idCiclo);

    List<Ubicacion> allDistritosByName(String nombre);

}
