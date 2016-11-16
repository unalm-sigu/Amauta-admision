package pe.edu.lamolina.pivot.controller.comun;

import java.util.List;
import pe.edu.lamolina.pivot.model.academico.Curso;

public interface BuscarService {

    List<Curso> allCursosAutocomplete(String nombre, Long idDepartamentoAca);

}
