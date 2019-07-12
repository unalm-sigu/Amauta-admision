package pe.edu.lamolina.pivot.controller.academico.tramitesacademicos.tramiteRetiroCiclo;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Date;
import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.enums.AmbienteAplicacionEnum;
import pe.edu.lamolina.model.enums.ParametrosSistemasEnum;
import pe.edu.lamolina.model.enums.TokenEstadoEnum;
import pe.edu.lamolina.model.general.Parametro;
import pe.edu.lamolina.model.seguridad.Sistema;
import pe.edu.lamolina.model.seguridad.TokenIngresante;
import pe.edu.lamolina.pivot.config.DespliegueConfig;
import pe.edu.lamolina.pivot.controller.rest.AbstractRestClient;
import pe.edu.lamolina.pivot.dao.general.ParametroDAO;
import pe.edu.lamolina.pivot.dao.seguridad.TokenIngresanteDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
public class ResponseRestServiceImpl extends AbstractRestClient<JsonResponse> implements ResponseRestService {

    @Autowired
    DespliegueConfig despliegueConfig;

    @Autowired
    ParametroDAO parametroDAO;

    @Autowired
    TokenIngresanteDAO tokenIngresanteDAO;

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
    public JsonResponse generarAporte(Alumno alumno, MatriculaResumen matriculaResumen, DataSessionPivot ds) {
        Parametro parametro = findParametro(ParametrosSistemasEnum.SALTO_PIVOT_BIENESTAR);

        ObjectNode json = new ObjectNode(JsonNodeFactory.instance);
        json.put("idUsuario", ds.getUsuario().getId());
        json.put("idAlumno", alumno.getId());
        json.put("idCicloAcademico", ds.getCicloAcademico().getId());
        if (matriculaResumen != null) {

            json.put("idMatriculaResumen", matriculaResumen.getId());
        }

        String url = String.format("%s/aportesRest/verificacionAporte",
                parametro.getValor());

        return this.postToBackEnd(url, json);
    }

    private Parametro findParametro(ParametrosSistemasEnum parametrosSistemasEnum) {

        return parametroDAO.findBySistemaAmbienteParametrosSistemas(new Sistema(despliegueConfig.getSistema()),
                AmbienteAplicacionEnum.valueOf(despliegueConfig.getAmbiente().toUpperCase()),
                parametrosSistemasEnum);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createToken(DataSessionPivot ds) {
        String valor = RandomStringUtils.randomAlphanumeric(45);
        TokenIngresante token = new TokenIngresante();
        token.setEstado(TokenEstadoEnum.ACT);
        token.setFechaRegistro(new Date());
        token.setFechaVencimiento(new DateTime().plusSeconds(15).toDate());
        token.setPersona(ds.getPersona());
        token.setValor(valor);
        token.setUserRegistro(ds.getUsuario());
        tokenIngresanteDAO.save(token);

    }
}
