package pe.edu.lamolina.amauta.controller.academico.graduado;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import pe.edu.lamolina.amauta.controller.seguridad.verificador.VerificadorService;
import pe.edu.lamolina.amauta.controller.seguridad.verificador.VerificadorServiceImp;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.constantines.GlobalMessages;
import pe.edu.lamolina.model.tramite.ObtencionGrado;

@Controller
@RequestMapping("academico/graduado")
public class GraduadoController {

    @Autowired
    GraduadoService service;

    @Autowired
    VerificadorService verificadorService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String rutaModulo = this.getClass().getAnnotation(RequestMapping.class).value()[0];

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        model.addAttribute("rutaModulo", rutaModulo);
        model.addAttribute("isDeveloper", verificadorService.isDeveloperOERA(ds));
        return "academico/graduado/graduado";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session, HttpServletRequest request) {

        DynatableResponse json = new DynatableResponse();
        String codeRequest = verificadorService.generateCodeRequest();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            List<Carrera> carreras = new ArrayList();
            List<ObtencionGrado> graduadosLista = new ArrayList();

            VerificadorServiceImp.CantidadItemsEnum cantidadEnum = verificadorService.verificarCantidad(TipoOficinaEnum.ESP, request, ds);
            if (cantidadEnum == VerificadorServiceImp.CantidadItemsEnum.PARCIAL) {
                carreras = verificadorService.allInstanciasByMenuRol(TipoOficinaEnum.ESP, request, ds, codeRequest);
            }

            if (cantidadEnum != VerificadorServiceImp.CantidadItemsEnum.SIN_PERMISO) {
                graduadosLista = service.allEgresadoByDynatable(filter, carreras, cantidadEnum.name());
            }

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (ObtencionGrado graduado : graduadosLista) {
                ObjectNode node = JaneHelper
                        .from(graduado)
                        .only("id")
                        .join("cicloAcademico", "descripcion")
                        .join("alumno", "id,codigo,estado,estadoEnum,promedioAcumulado,creditosCursados,creditosAprobados")
                        .join("alumno.persona", "id,apellidosNombres,rutaFoto,tipoFoto,numeroDocIdentidad,email,emailCompania")
                        .join("alumno.persona.tipoDocumento", "simbolo")
                        .join("alumno.carrera", "nombre,codigo,tipoEnum,tipo,descripcionCarreraFacultad")
                        .join("alumno.carrera.facultad", "codigo,nombre")
                        .join("alumno.modalidadEstudio", "codigo,nombre")
                        .join("alumno.situacionAcademica", "codigo,nombre")
                        .join("gradoAcademico", "nombre,tipo")
                        .join("estadoTramite", "nombre,codigo")
                        .join("resolucion", "descripcion,rutaUrl")
                        .json();

                array.add(node);
            }

            json.setData(array);
            json.setTotal(filter.getTotal());
            json.setFiltered(filter.getFiltered());

        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }
        return json;
    }

    @ResponseBody
    @RequestMapping("resumen")
    public JsonResponse resumen(HttpSession session, HttpServletRequest request) {

        JsonResponse response = new JsonResponse();
        String codeRequest = verificadorService.generateCodeRequest();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

            List<Carrera> carreras = new ArrayList();
            VerificadorServiceImp.CantidadItemsEnum cantidadEnum = verificadorService.verificarCantidad(TipoOficinaEnum.ESP, request, ds);
            if (cantidadEnum == VerificadorServiceImp.CantidadItemsEnum.PARCIAL) {
                carreras = verificadorService.allInstanciasByMenuRol(TipoOficinaEnum.ESP, request, ds, codeRequest);
            }

            GraduadoResumen resumen = new GraduadoResumen();
            if (cantidadEnum != VerificadorServiceImp.CantidadItemsEnum.SIN_PERMISO) {
                resumen = service.findResumenEgresado(carreras, cantidadEnum.name());
            }

            response.setData(JsonHelper.createJson(resumen, JsonNodeFactory.instance, true, new String[]{"*"}));
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("anular")
    public JsonResponse anular(@RequestBody ObtencionGrado obtencionGrado, HttpSession session) {
        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

            service.anular(obtencionGrado, ds.getUsuario());
            response.setMessage("Se anuló el grado satisfactoriamente");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("cambiarsituacionacademica/{idAlumno}")
    public String cambiarSituacionAcademica(@PathVariable Long idAlumno, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.cambiarSituacionAcademica(idAlumno);
        return GlobalMessages.UPDATED;
    }

}
