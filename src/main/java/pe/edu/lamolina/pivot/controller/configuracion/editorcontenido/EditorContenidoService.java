package pe.edu.lamolina.pivot.controller.configuracion.editorcontenido;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.inscripcion.ContenidoCarta;
import pe.edu.lamolina.model.inscripcion.ContenidoCartaVariable;
import pe.edu.lamolina.model.inscripcion.ContenidoVariable;
import pe.edu.lamolina.model.seguridad.Sistema;

public interface EditorContenidoService {

    ContenidoCarta findContenidoCartaById(Long idContenido);

    ContenidoCarta findSoloContenidoCartaById(Long idContenido);

    void updateContenido(Long idContenido, String contenido, Long idSistema);

    List<ContenidoCarta> allContenidoCartaByDynaTable(DynatableFilter filter);

    void save(ContenidoCarta contenido);

    List<ContenidoVariable> allVariablesByContenido(Long idContenido);

    List<ContenidoCartaVariable> allVariablesCartaByContenido(Long idContenido);

    void updateImgUrl(Long idContenido, String fileName);

    List<Sistema> allSistema();

    List<ContenidoVariable> allVariables();

    void addVariable(ContenidoVariable variable, Long idContenido);

}
