package pe.edu.lamolina.pivot.controller.academico.tramitesacademicos.tramiteRetiroCiclo;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.enums.AmbienteAplicacionEnum;
import pe.edu.lamolina.model.enums.ParametrosSistemasEnum;
import pe.edu.lamolina.model.general.Parametro;
import pe.edu.lamolina.model.seguridad.Sistema;
import pe.edu.lamolina.pivot.config.DespliegueConfig;
import pe.edu.lamolina.pivot.controller.rest.AbstractRestClient;
import pe.edu.lamolina.pivot.dao.general.ParametroDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
public class ResponseRestServiceImpl extends AbstractRestClient<JsonResponse> implements ResponseRestService {

    @Autowired
    DespliegueConfig despliegueConfig;

    @Autowired
    ParametroDAO parametroDAO;

    @Override
    @Transactional
    public JsonResponse updateRest(MatriculaResumen matriculaResumen, DataSessionPivot ds) {
        Parametro parametro = findParametro(ParametrosSistemasEnum.SALTO_PIVOT_MATRICULA);

        ObjectNode json = new ObjectNode(JsonNodeFactory.instance);
        json.put("idUsuario", ds.getUsuario().getId());
        json.put("idMatricula", matriculaResumen.getId());

        String url = String.format("%s/matriculaSeccion/deleteMatricula",
                parametro.getValor());

        return this.postToBackEnd(url, json);
    }

    @Override
    @Transactional
    public JsonResponse generarAporte(Alumno alumno, DataSessionPivot ds) {
        Parametro parametro = findParametro(ParametrosSistemasEnum.SALTO_PIVOT_BIENESTAR);

        ObjectNode json = new ObjectNode(JsonNodeFactory.instance);
        json.put("idUsuario", ds.getUsuario().getId());
        json.put("idAlumno", alumno.getId());

        String url = String.format("%s/aportesRest/verificacionAporte",
                parametro.getValor());

        return this.postToBackEnd(url, json);
    }

    private Parametro findParametro(ParametrosSistemasEnum parametrosSistemasEnum) {

        return parametroDAO.findBySistemaAmbienteParametrosSistemas(new Sistema(despliegueConfig.getSistema()),
                AmbienteAplicacionEnum.valueOf(despliegueConfig.getAmbiente().toUpperCase()),
                parametrosSistemasEnum);
    }
}
