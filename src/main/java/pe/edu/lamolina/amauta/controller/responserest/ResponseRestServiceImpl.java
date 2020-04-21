package pe.edu.lamolina.amauta.controller.responserest;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Date;
import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.aporte.Aporte;
import pe.edu.lamolina.model.enums.AmbienteAplicacionEnum;
import pe.edu.lamolina.model.enums.AportesEnum;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.enums.OrigenTokenEnum;
import pe.edu.lamolina.model.enums.ParametrosSistemasEnum;
import pe.edu.lamolina.model.enums.TokenEstadoEnum;
import pe.edu.lamolina.model.general.Parametro;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.seguridad.TokenIngresante;
import pe.edu.lamolina.amauta.config.DespliegueConfig;
import pe.edu.lamolina.amauta.controller.rest.AbstractRestClient;
import pe.edu.lamolina.amauta.dao.academico.AlumnoDAO;
import pe.edu.lamolina.amauta.dao.aporte.AporteDAO;
import pe.edu.lamolina.amauta.dao.general.ParametroDAO;
import pe.edu.lamolina.amauta.dao.seguridad.TokenIngresanteDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

@Service
public class ResponseRestServiceImpl extends AbstractRestClient<JsonResponse> implements ResponseRestService {

    @Autowired
    DespliegueConfig despliegueConfig;

    @Autowired
    ParametroDAO parametroDAO;

    @Autowired
    AlumnoDAO alumnoDAO;

    @Autowired
    AporteDAO aporteDAO;

    @Autowired
    TokenIngresanteDAO tokenIngresanteDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private ObjectNode createFormJson(DataSessionPivot ds, TokenIngresante token) {
        ObjectNode json = new ObjectNode(JsonNodeFactory.instance);
        json.put("idUsuario", ds.getUsuario().getId());
        json.put("token", token.getValor());
        return json;
    }

    @Override
    public Parametro findParametro(ParametrosSistemasEnum parametrosSistemasEnum) {
        AmbienteAplicacionEnum ambiente = AmbienteAplicacionEnum.valueOf(despliegueConfig.getAmbiente().toUpperCase());
        Parametro paramRutaIntranet = parametroDAO.findByAmbienteParametroSistema(ambiente, parametrosSistemasEnum);
        return paramRutaIntranet;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public TokenIngresante createToken(DataSessionPivot ds) {
        String valor = RandomStringUtils.randomAlphanumeric(45);

        TokenIngresante token = new TokenIngresante();
        token.setOrigenEnum(OrigenTokenEnum.AMAUTA);
        token.setEstado(TokenEstadoEnum.ACT);
        token.setFechaRegistro(new Date());
        token.setFechaVencimiento(new DateTime().plusMinutes(120).toDate());
        token.setPersona(ds.getPersona());
        token.setValor(valor);
        token.setUserRegistro(ds.getUsuario());
        tokenIngresanteDAO.save(token);

        return token;
    }

    @Override
    @Transactional
    public TokenIngresante createTokenForAlumno(Alumno alumnoForm, DataSessionPivot ds) {
        Alumno alumno = alumnoDAO.find(alumnoForm);
        TokenIngresante token = tokenIngresanteDAO.findUltimoVigente(alumno.getPersona());

        if (token == null) {
            String valor = RandomStringUtils.randomAlphanumeric(45);

            token = new TokenIngresante();
            token.setOrigenEnum(OrigenTokenEnum.AMAUTA);
            token.setEstado(TokenEstadoEnum.ACT);
            token.setFechaRegistro(new Date());
            token.setFechaVencimiento(new DateTime().plusSeconds(120).toDate());
            token.setPersona(alumno.getPersona());
            token.setAlumno(alumno);
            token.setValor(valor);
            token.setUserRegistro(ds.getUsuario());
            tokenIngresanteDAO.save(token);
        }

        return token;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public TokenIngresante createToken(Persona persona, DataSessionPivot ds) {
        String valor = RandomStringUtils.randomAlphanumeric(45);

        TokenIngresante token = new TokenIngresante();
        token.setOrigenEnum(OrigenTokenEnum.AMAUTA);
        token.setEstado(TokenEstadoEnum.ACT);
        token.setFechaRegistro(new Date());
        token.setFechaVencimiento(new DateTime().plusSeconds(120).toDate());
        token.setPersona(persona);
        token.setValor(valor);
        token.setUserRegistro(ds.getUsuario());
        tokenIngresanteDAO.save(token);

        return token;
    }

    @Override
    public JsonResponse retirarMatriculaCurso(MatriculaCurso matriculaCurso, DataSessionPivot ds, EstadoMatriculaEnum estadoEnum, TokenIngresante token) {
        estadoEnum = (estadoEnum == null) ? EstadoMatriculaEnum.RET : estadoEnum;
        Parametro parametro = findParametro(ParametrosSistemasEnum.REST_MATRICULA);
        ObjectNode json = createFormJson(ds, token);
        json.put("idMatricula", matriculaCurso.getId());
        json.put("estado", estadoEnum.name());

        String url = String.format("%s/matriculaSeccion/deleteMatriculaCurso", parametro.getValor());
        return this.postToBackEnd(url, json);
    }

    @Override
    public JsonResponse matricularSeccion(Alumno alumno, Seccion seccion, DataSessionPivot ds, TokenIngresante token) {
        Parametro parametro = findParametro(ParametrosSistemasEnum.REST_MATRICULA);
        ObjectNode json = createFormJson(ds, token);
        json.put("idMatricula", alumno.getId());
        json.put("idSeccion", seccion.getId());

        String url = String.format("%s/matriculaSeccion/matricularSeccion", parametro.getValor());
        return this.postToBackEnd(url, json);
    }

    @Override
    public JsonResponse matricularSeccionReservada(Alumno alumno, Seccion seccion, DataSessionPivot ds, TokenIngresante token) {
        Parametro parametro = findParametro(ParametrosSistemasEnum.REST_MATRICULA);
        ObjectNode json = createFormJson(ds, token);
        json.put("idMatricula", alumno.getId());
        json.put("idSeccion", seccion.getId());

        String url = String.format("%s/matriculaSeccion/matricularSeccionReservada", parametro.getValor());
        return this.postToBackEnd(url, json);
    }

    @Override
    @Transactional
    public JsonResponse retirarMatriculaCiclo(MatriculaResumen matriculaResumen, DataSessionPivot ds, TokenIngresante token) {
        Parametro parametro = findParametro(ParametrosSistemasEnum.REST_MATRICULA);
        ObjectNode json = createFormJson(ds, token);
        json.put("idMatricula", matriculaResumen.getId());

        String url = String.format("%s/matriculaSeccion/deleteMatricula", parametro.getValor());
        return this.postToBackEnd(url, json);
    }

    @Override
    @Transactional
    public JsonResponse ampliarVacante(Seccion seccion, Integer variacion, DataSessionPivot ds, TokenIngresante token) {
        Parametro parametro = findParametro(ParametrosSistemasEnum.REST_MATRICULA);

        ObjectNode json = createFormJson(ds, token);
        json.put("idSeccion", seccion.getId());
        json.put("cantidadVariacion", Math.abs(variacion));
        String metodo = "agregarVacanteSeccion";
        if (variacion < 0) {
            metodo = "restarVacanteSeccion";
        }

        String url = String.format("%s/matriculaSeccion/%s", parametro.getValor(), metodo);
        return this.postToBackEnd(url, json);
    }

    @Override
    @Transactional
    public JsonResponse generarAporte(Alumno alumno, CicloAcademico ciclo, MatriculaResumen matriculaResumen, DataSessionPivot ds, TokenIngresante token) {
        Parametro parametro = findParametro(ParametrosSistemasEnum.REST_BIENESTAR);

        ObjectNode json = createFormJson(ds, token);
        json.put("idAlumno", alumno.getId());
        json.put("idCicloAcademico", ciclo.getId());
        if (matriculaResumen != null) {
            json.put("idMatricula", matriculaResumen.getId());
        }

        String url = String.format("%s/aportesRest/generarAllAportes", parametro.getValor());
        return this.postToBackEnd(url, json);
    }

    @Override
    @Transactional
    public JsonResponse agregarAporte(Aporte aporte, MatriculaResumen matriculaResumen, DataSessionPivot ds, TokenIngresante token) {
        Parametro parametro = findParametro(ParametrosSistemasEnum.REST_BIENESTAR);

        ObjectNode json = createFormJson(ds, token);
        json.put("idMatricula", matriculaResumen.getId());
        json.put("idAporte", aporte.getId());

        String url = String.format("%s/aportesRest/agregarAporte", parametro.getValor());
        return this.postToBackEnd(url, json);
    }

    @Override
    @Transactional
    public JsonResponse generarAporteCarnet(MatriculaResumen matriculaResumen, DataSessionPivot ds, TokenIngresante token) {
        Parametro parametro = findParametro(ParametrosSistemasEnum.REST_BIENESTAR);
        Aporte aporte = aporteDAO.findByCode(AportesEnum.A05);
        ObjectNode json = createFormJson(ds, token);
        json.put("idMatricula", matriculaResumen.getId());
        json.put("idAporte", aporte.getId());

        String url = String.format("%s/aportesRest/agregarAporte", parametro.getValor());
        return this.postToBackEnd(url, json);
    }

    @Override
    @Transactional
    public JsonResponse generarAporteSegundaCarrera(MatriculaResumen matriculaResumen, DataSessionPivot ds, TokenIngresante token) {
        Parametro parametro = findParametro(ParametrosSistemasEnum.REST_BIENESTAR);
        Aporte aporte = aporteDAO.findByCode(AportesEnum.A42);
        ObjectNode json = createFormJson(ds, token);
        json.put("idMatricula", matriculaResumen.getId());
        json.put("idAporte", aporte.getId());

        String url = String.format("%s/aportesRest/agregarAporte", parametro.getValor());
        return this.postToBackEnd(url, json);
    }

    @Override
    @Transactional
    public JsonResponse eliminarAporteCarnet(MatriculaResumen matriculaResumen, DataSessionPivot ds, TokenIngresante token) {
        Parametro parametro = findParametro(ParametrosSistemasEnum.REST_BIENESTAR);
        Aporte aporte = aporteDAO.findByCode(AportesEnum.A05);
        ObjectNode json = createFormJson(ds, token);
        json.put("idMatricula", matriculaResumen.getId());
        json.put("idAporte", aporte.getId());

        String url = String.format("%s/aportesRest/quitarAporte", parametro.getValor());
        return this.postToBackEnd(url, json);
    }

    @Override
    @Transactional
    public JsonResponse eliminarAporte(MatriculaResumen matriculaResumen, DataSessionPivot ds, Aporte aporte, TokenIngresante token) {
        Parametro parametro = findParametro(ParametrosSistemasEnum.REST_BIENESTAR);

        ObjectNode json = createFormJson(ds, token);
        json.put("idMatricula", matriculaResumen.getId());
        json.put("idAporte", aporte.getId());

        String url = String.format("%s/aportesRest/quitarAporte", parametro.getValor());
        return this.postToBackEnd(url, json);
    }

    @Override
    @Transactional
    public JsonResponse modificarAporte(MatriculaResumen matriculaResumen, DataSessionPivot ds, Aporte aporte, TokenIngresante token) {
        Parametro parametro = findParametro(ParametrosSistemasEnum.REST_BIENESTAR);

        ObjectNode json = createFormJson(ds, token);
        json.put("idMatricula", matriculaResumen.getId());
        json.put("idAporte", aporte.getId());

        String url = String.format("%s/aportesRest/modificarAporte", parametro.getValor());
        return this.postToBackEnd(url, json);
    }

    @Override
    public JsonResponse anularBoletas(MatriculaResumen matriculaResumen, DataSessionPivot ds, TokenIngresante token) {
        Parametro parametro = findParametro(ParametrosSistemasEnum.REST_BIENESTAR);

        ObjectNode json = createFormJson(ds, token);
        json.put("idMatricula", matriculaResumen.getId());

        String url = String.format("%s/aportesRest/anularBoletas", parametro.getValor());
        return this.postToBackEnd(url, json);
    }

    @Override
    public JsonResponse downloadHistorial(Alumno alumno, CicloAcademico academico, Parametro paramRutaMatricula, DataSessionPivot ds, TokenIngresante token) {
        ObjectNode json = createFormJson(ds, token);
        json.put("idAlumno", alumno.getId());
        json.put("idCiclo", academico.getId());

        String url = String.format("%s/restMaipi/historialpdf", paramRutaMatricula.getValor());
        return this.postToBackEnd(url, json);
    }

    @Override
    public JsonResponse eliminarAporteDuplicadoCarnet(MatriculaResumen matriculaResumen, DataSessionPivot ds, TokenIngresante token) {
        Parametro parametro = findParametro(ParametrosSistemasEnum.REST_BIENESTAR);
        Aporte aporte = aporteDAO.findByCode(AportesEnum.A51);
        ObjectNode json = createFormJson(ds, token);
        json.put("idMatricula", matriculaResumen.getId());
        json.put("idAporte", aporte.getId());

        String url = String.format("%s/aportesRest/quitarAporte", parametro.getValor());
        return this.postToBackEnd(url, json);
    }

    @Override
    public JsonResponse generarAporteDuplicadoCarnet(MatriculaResumen matriculaResumen, DataSessionPivot ds, TokenIngresante token) {
        Parametro parametro = findParametro(ParametrosSistemasEnum.REST_BIENESTAR);
        Aporte aporte = aporteDAO.findByCode(AportesEnum.A51);
        ObjectNode json = createFormJson(ds, token);
        json.put("idMatricula", matriculaResumen.getId());
        json.put("idAporte", aporte.getId());

        String url = String.format("%s/aportesRest/agregarAporte", parametro.getValor());
        return this.postToBackEnd(url, json);
    }

    @Override
    public JsonResponse limpiarCache(DataSessionPivot ds, TokenIngresante token) {
        Parametro parametro = findParametro(ParametrosSistemasEnum.REST_MATRICULA);
        ObjectNode json = createFormJson(ds, token);

        String url = String.format("%s/matriculaSeccion/limpiarCache", parametro.getValor());
        return this.postToBackEnd(url, json);
    }

}
