package pe.edu.lamolina.pivot.controller.docente.ampliacionvacante;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.enums.AmbienteAplicacionEnum;
import pe.edu.lamolina.model.enums.ParametrosSistemasEnum;
import pe.edu.lamolina.model.general.Parametro;
import pe.edu.lamolina.model.seguridad.Sistema;
import pe.edu.lamolina.pivot.config.DespliegueConfig;
import pe.edu.lamolina.pivot.controller.rest.AbstractRestClient;
import pe.edu.lamolina.pivot.dao.general.ParametroDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class AmpliacionVacanteRestServiceImp extends AbstractRestClient<JsonResponse> implements AmpliacionVacanteRestService {

    @Autowired
    ParametroDAO parametroDAO;

    @Autowired
    DespliegueConfig despliegueConfig;

    @Override
    public JsonResponse validarAmpliacionVacante(MatriculaSeccion matriculaSeccion, DataSessionPivot ds) {
        Parametro parametro = this.findParametro();

        ObjectNode json = new ObjectNode(JsonNodeFactory.instance);
        json.put("idMatriculaSeccion", "");

        String url = String.format("%s/matriculaSeccion/solicitud",
                parametro.getValor());

        return this.postToBackEnd(url, json);
    }

    public Parametro findParametro() {
        return parametroDAO.findBySistemaAmbienteParametrosSistemas(new Sistema(despliegueConfig.getSistema()),
                AmbienteAplicacionEnum.valueOf(despliegueConfig.getAmbiente().toUpperCase()),
                ParametrosSistemasEnum.SALTO_PIVOT_MATRICULA);
    }

}
