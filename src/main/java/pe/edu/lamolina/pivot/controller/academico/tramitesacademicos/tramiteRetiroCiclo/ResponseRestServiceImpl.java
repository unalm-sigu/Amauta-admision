package pe.edu.lamolina.pivot.controller.academico.tramitesacademicos.tramiteRetiroCiclo;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.general.Parametro;
import pe.edu.lamolina.pivot.controller.rest.AbstractRestClient;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
public class ResponseRestServiceImpl extends AbstractRestClient<JsonResponse> implements ResponseRestService {

    @Autowired
    TramiteRetiroCicloService retiroCicloService;

    @Override
    @Transactional
    public JsonResponse updateRest(MatriculaResumen matriculaResumen, DataSessionPivot ds) {
        Parametro parametro = retiroCicloService.findParametro();

        ObjectNode json = new ObjectNode(JsonNodeFactory.instance);
        json.put("idUsuario", ds.getUsuario().getId());
        json.put("idMatricula", matriculaResumen.getId());

        String url = String.format("%s/matriculaSeccion/deleteMatricula",
                parametro.getValor());

        return this.postToBackEnd(url, json);
    }

}
