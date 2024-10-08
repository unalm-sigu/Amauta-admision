package pe.edu.lamolina.amauta.controller.nivelacioneegg.alumnosnivelacion.helpernotaalumno;

import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.List;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.alumnosnivelacion.dto.NotaAlumnoNivelacionDTO;
import pe.edu.lamolina.model.nivelacioneegg.NotaAlumnoNivelacion;

public interface ChangeNotaAlumnoNivelacionService {

    String createCambiosJson(NotaAlumnoNivelacion notaAlumno, String motivo, String anterior);

    List<NotaAlumnoNivelacionDTO> recrearLista(String jsonString);

    ArrayNode getCambios(NotaAlumnoNivelacion notaAlumno);

}
