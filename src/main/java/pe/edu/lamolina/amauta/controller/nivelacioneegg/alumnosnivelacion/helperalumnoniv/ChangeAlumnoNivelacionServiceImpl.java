package pe.edu.lamolina.amauta.controller.nivelacioneegg.alumnosnivelacion.helperalumnoniv;

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
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.alumnosnivelacion.dto.AlumnoNivelacionDTO;
import pe.edu.lamolina.model.nivelacioneegg.AlumnoNivelacion;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ChangeAlumnoNivelacionServiceImpl implements ChangeAlumnoNivelacionService {

    @Override
    public String createCambiosJson(AlumnoNivelacion alumnoNiv, String motivo, String anterior) {
        List<AlumnoNivelacionDTO> cambios = this.recrearLista(anterior);
        cambios.add(new AlumnoNivelacionDTO(alumnoNiv, motivo));

        return JaneHelper
                .from(cambios)
                .only("id,estado,puntajeFinal,notaFinal,motivo,fechaRegistro")
                .join("userRegistro", "id,google")
                .join("userRegistro.persona", "id,paterno,materno,nombres")
                .array().toString();
    }

    @Override
    public List<AlumnoNivelacionDTO> recrearLista(String jsonString) {
        if (StringUtils.isBlank(jsonString)) {
            return new ArrayList();
        }

        ObjectMapper mapper = new ObjectMapper();
        final CollectionType javaType = mapper.getTypeFactory().constructCollectionType(List.class, AlumnoNivelacionDTO.class);
        try {
            return mapper.readValue(jsonString, javaType);
        } catch (IOException e) {
            throw new PhobosException("No se pudo construir la lista AlumnoNivelacionDTO");
        }
    }

    @Override
    public ArrayNode getCambios(AlumnoNivelacion alumnoNiv) {
        if (alumnoNiv == null) {
            return new ArrayNode(JsonNodeFactory.instance);
        }

        List<AlumnoNivelacionDTO> cambios = this.recrearLista(alumnoNiv.getCambios());

        return JaneHelper
                .from(cambios)
                .only("id,estado,puntajeFinal,notaFinal,motivo,fechaRegistro")
                .join("userRegistro", "id,google")
                .join("userRegistro.persona", "id,paterno,materno,nombres")
                .array();
    }

}
