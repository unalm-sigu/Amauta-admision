package pe.edu.lamolina.amauta.controller.nivelacioneegg.programacionnivelacion.helperprogramacionnivelacion;

import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.List;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.programacionnivelacion.dto.CambioCursoNivevalacionDTO;
import pe.edu.lamolina.model.nivelacioneegg.CursoNivelacion;

public interface ChangeProgramacionNivelacionService {

    String createCambiosJson(CursoNivelacion cursoNivelacion);

    String createCambiosJson(CursoNivelacion cursoNivelacion, String cambio, String motivo, String anterior);

    List<CambioCursoNivevalacionDTO> recrearLista(String jsonString);

    ArrayNode getCambios(CursoNivelacion cursoNivelacion);

}
