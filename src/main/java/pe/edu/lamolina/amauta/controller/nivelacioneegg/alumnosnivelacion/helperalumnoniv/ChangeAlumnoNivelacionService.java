package pe.edu.lamolina.amauta.controller.nivelacioneegg.alumnosnivelacion.helperalumnoniv;

import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.List;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.alumnosnivelacion.dto.AlumnoNivelacionDTO;
import pe.edu.lamolina.model.nivelacioneegg.AlumnoNivelacion;

public interface ChangeAlumnoNivelacionService {

    String createCambiosJson(AlumnoNivelacion alumnoNiv, String motivo, String anterior);

    List<AlumnoNivelacionDTO> recrearLista(String jsonString);

    ArrayNode getCambios(AlumnoNivelacion alumnoNiv);

}
