package pe.edu.lamolina.amauta.controller.nivelacioneegg.alumnosnivelacion.helpernotaalumno;

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
import pe.edu.lamolina.amauta.controller.nivelacioneegg.alumnosnivelacion.dto.NotaAlumnoNivelacionDTO;
import pe.edu.lamolina.model.nivelacioneegg.NotaAlumnoNivelacion;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ChangeNotaAlumnoNivelacionServiceImpl implements ChangeNotaAlumnoNivelacionService {

    @Override
    public String createCambiosJson(NotaAlumnoNivelacion notaAlumno, String motivo, String anterior) {
        List<NotaAlumnoNivelacionDTO> cambios = this.recrearLista(anterior);
        cambios.add(new NotaAlumnoNivelacionDTO(notaAlumno, motivo));

        return JaneHelper
                .from(cambios)
                .only("id,estado,notaExamen,puntajeExamen,temaAprobado,notaCurso,esMatriculable,fechaRegistro")
                .join("alumnoNivelacion", "id")
                .join("temaCiclo", "id,codigo")
                .join("temaCiclo.temaExamen", "id,codigo,nombre")
                .join("curso", "id,codigo,nombre")
                .join("cursoNivelacion", "id,codigo,nombre")
                .join("userRegistro", "id,google")
                .join("userRegistro.persona", "id,paterno")
                .array().toString();
    }

    @Override
    public List<NotaAlumnoNivelacionDTO> recrearLista(String jsonString) {
        if (StringUtils.isBlank(jsonString)) {
            return new ArrayList();
        }

        ObjectMapper mapper = new ObjectMapper();
        final CollectionType javaType = mapper.getTypeFactory().constructCollectionType(List.class, NotaAlumnoNivelacionDTO.class);
        try {
            return mapper.readValue(jsonString, javaType);
        } catch (IOException e) {
            throw new PhobosException("No se pudo construir la lista NotaAlumnoNivelacionDTO");
        }
    }

    @Override
    public ArrayNode getCambios(NotaAlumnoNivelacion alumnoNiv) {
        if (alumnoNiv == null) {
            return new ArrayNode(JsonNodeFactory.instance);
        }

        List<NotaAlumnoNivelacionDTO> cambios = this.recrearLista(alumnoNiv.getCambios());

        return JaneHelper
                .from(cambios)
                .only("id,estado,notaExamen,puntajeExamen,temaAprobado,notaCurso,esMatriculable,fechaRegistro")
                .join("alumnoNivelacion", "id")
                .join("temaCiclo", "id,codigo")
                .join("temaCiclo.temaExamen", "id,codigo,nombre")
                .join("curso", "id,codigo,nombre")
                .join("cursoNivelacion", "id,codigo,nombre")
                .join("userRegistro", "id,google")
                .join("userRegistro.persona", "id,paterno")
                .array();
    }

}
