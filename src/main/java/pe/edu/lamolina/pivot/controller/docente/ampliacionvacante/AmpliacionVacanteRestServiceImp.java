package pe.edu.lamolina.pivot.controller.docente.ampliacionvacante;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.enums.AmbienteAplicacionEnum;
import pe.edu.lamolina.model.enums.AmpliacionVacanteOperacionEnum;
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
        json.put("seccion", matriculaSeccion.getSeccion().getId());
        json.put("alumno", matriculaSeccion.getMatriculaResumen().getAlumno().getId());
        json.put("usuario", ds.getUsuario().getId());
        json.put("tipoOperacion", AmpliacionVacanteOperacionEnum.MAT.name());
        String url = String.format("%s/matriculaSeccion/solicitud",
                parametro.getValor());

        return this.postToBackEnd(url, json);
    }

    @Override
    public JsonResponse matricularAmpliacionVacante(MatriculaSeccion matriculaSeccion, DataSessionPivot ds) {
        Parametro parametro = this.findParametro();

        ObjectNode json = new ObjectNode(JsonNodeFactory.instance);
        json.put("seccion", matriculaSeccion.getSeccion().getId());
        json.put("alumno", matriculaSeccion.getMatriculaResumen().getAlumno().getId());
        json.put("usuario", ds.getUsuario().getId());
        json.put("tipoOperacion", AmpliacionVacanteOperacionEnum.MAT.name());
        String url = String.format("%s/matriculaSeccion/solicitud",
                parametro.getValor());

        return this.postToBackEnd(url, json);
    }
 
    @Override
    public JsonResponse confirmarAmpliacionVacante(MatriculaSeccion matriculaSeccion, boolean esDocenteTCUR, DataSessionPivot ds) {
        Parametro parametro = this.findParametro();

        ObjectNode json = new ObjectNode(JsonNodeFactory.instance);
        json.put("seccion", matriculaSeccion.getSeccion().getId());
        json.put("alumno", matriculaSeccion.getMatriculaResumen().getAlumno().getId());
        json.put("usuario", ds.getUsuario().getId());
        json.put("tipoOperacion", AmpliacionVacanteOperacionEnum.CONF.name());
        json.put("esDocenteTCUR", esDocenteTCUR);
        json.put("tipoAmpliacion", matriculaSeccion.getTipoAmpliacion());
        String url = String.format("%s/matriculaSeccion/solicitud",
                parametro.getValor());

        JsonResponse response = this.postToBackEnd(url, json);
        if (!response.getSuccess()) {
            throw new PhobosException(response.getMessage());
        }
        return this.postToBackEnd(url, json);
    }

    @Override
    public JsonResponse rechazarAmpliacionVacante(MatriculaSeccion matriculaSeccion, boolean esDocenteTCUR, DataSessionPivot ds) {
        Parametro parametro = this.findParametro();

        ObjectNode json = new ObjectNode(JsonNodeFactory.instance);
        json.put("seccion", matriculaSeccion.getSeccion().getId());
        json.put("alumno", matriculaSeccion.getMatriculaResumen().getAlumno().getId());
        json.put("usuario", ds.getUsuario().getId());
        json.put("tipoOperacion", AmpliacionVacanteOperacionEnum.RHZR.name());
        json.put("esDocenteTCUR", esDocenteTCUR);
        json.put("tipoAmpliacion", matriculaSeccion.getTipoAmpliacion());
        String url = String.format("%s/matriculaSeccion/solicitud",
                parametro.getValor());

        JsonResponse response = this.postToBackEnd(url, json);
        if (!response.getSuccess()) {
            throw new PhobosException(response.getMessage());
        }
        return this.postToBackEnd(url, json);
    }

    @Override
    public JsonResponse solicitarAmpliacionVacante(MatriculaSeccion matriculaSeccion, boolean esDocenteTCUR, DataSessionPivot ds) {
        Parametro parametro = this.findParametro();

        ObjectNode json = new ObjectNode(JsonNodeFactory.instance);
        json.put("seccion", matriculaSeccion.getSeccion().getId());
        json.put("alumno", matriculaSeccion.getMatriculaResumen().getAlumno().getId());
        json.put("usuario", ds.getUsuario().getId());
        json.put("tipoOperacion", AmpliacionVacanteOperacionEnum.SOL.name());
        json.put("esDocenteTCUR", esDocenteTCUR);
        String url = String.format("%s/matriculaSeccion/solicitud",
                parametro.getValor());

        JsonResponse response = this.postToBackEnd(url, json);
        if (!response.getSuccess()) {
            throw new PhobosException(response.getMessage());
        }
        return response;
    }

    public Parametro findParametro() {
        return parametroDAO.findBySistemaAmbienteParametrosSistemas(new Sistema(despliegueConfig.getSistema()),
                AmbienteAplicacionEnum.valueOf(despliegueConfig.getAmbiente().toUpperCase()),
                ParametrosSistemasEnum.SALTO_PIVOT_MATRICULA);
    }

}
