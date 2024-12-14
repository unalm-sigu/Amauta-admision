package pe.edu.lamolina.amauta.controller.nivelacioneegg.programacionnivelacion.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.type.CollectionType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.programacionnivelacion.dto.CambioCursoNivevalacionDTO;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.horario.GrupoHorasNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.CursoNivelacion;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ChangeProgramacionNivelacionServiceImpl implements ChangeProgramacionNivelacionService {

    @Override
    public String createCambiosJson(CursoNivelacion cursoNiv) {
        Assert.isNotNull(cursoNiv.getDocente(), "No ha indicado el código del docente en el registro de cambios");
        Assert.isNotNull(cursoNiv.getDocente().getCodigo(), "No ha indicado el código del docente en el registro de cambios");
        Assert.isNotNull(cursoNiv.getGrupoHoras(), "No ha indicado el grupo de horario en el registro de cambios");
        Assert.isNotNull(cursoNiv.getGrupoHoras().getCodigo(), "No ha indicado el grupo de horario en el registro de cambios");
        Assert.isNotNull(cursoNiv.getHorasDictado(), "No ha indicado las horas de dictado en el registro de cambios");
        Assert.isNotNull(cursoNiv.getVacantes(), "No ha indicado las vacantes en el registro de cambios");

        Docente docente = cursoNiv.getDocente();
        Aula aula = cursoNiv.getAula();
        GrupoHorasNivelacion gpoHoras = cursoNiv.getGrupoHoras();

        String cambio = "Creación con ";
        cambio += "docente " + docente.getCodigo() + ", ";
        if (aula != null) {
            cambio += "aula " + aula.getCodigo() + ", ";
        }
        cambio += "gpo horario " + gpoHoras.getCodigo() + ", ";
        cambio += "horas dictado " + cursoNiv.getHorasDictado() + " y ";
        cambio += "vacantes " + cursoNiv.getVacantes();

        List<CambioCursoNivevalacionDTO> cambios = new ArrayList();
        cambios.add(new CambioCursoNivevalacionDTO(cursoNiv, cambio, null));

        return JaneHelper
                .from(cambios)
                .only("cambio,motivo,fechaRegistro")
                .join("userRegistro", "id,google")
                .join("userRegistro.persona", "id,paterno,materno,nombres")
                .array().toString();
    }

    @Override
    public String createCambiosJson(CursoNivelacion cursoNiv, String cambio, String motivo, String anterior) {
        Assert.isNotNull(cambio, "Debe indicar que cambio se va a registrar");
        List<CambioCursoNivevalacionDTO> cambios = this.recrearLista(anterior);
        if (StringUtils.isBlank(motivo)) {
            motivo = null;
        }

        cambios.add(new CambioCursoNivevalacionDTO(cursoNiv, cambio, motivo));

        return JaneHelper
                .from(cambios)
                .only("cambio,motivo,fechaRegistro")
                .join("userRegistro", "id,google")
                .join("userRegistro.persona", "id,paterno,materno,nombres")
                .array().toString();
    }

    @Override
    public List<CambioCursoNivevalacionDTO> recrearLista(String jsonString) {
        if (StringUtils.isBlank(jsonString)) {
            return new ArrayList();
        }

        ObjectMapper mapper = new ObjectMapper();
        final CollectionType javaType = mapper.getTypeFactory().constructCollectionType(List.class, CambioCursoNivevalacionDTO.class);
        try {
            return mapper.readValue(jsonString, javaType);
        } catch (IOException e) {
            throw new PhobosException("No se pudo construir la lista CambioCursoNivevalacionDTO");
        }
    }

    @Override
    public ArrayNode getCambios(CursoNivelacion cursoNiv) {
        if (cursoNiv == null) {
            return new ArrayNode(JsonNodeFactory.instance);
        }

        List<CambioCursoNivevalacionDTO> cambios = this.recrearLista(cursoNiv.getCambios());

        return JaneHelper
                .from(cambios)
                .join("userRegistro", "id,google")
                .join("userRegistro.persona", "id,paterno,materno,nombres")
                .array();
    }

}
