package pe.edu.lamolina.pivot.controller.academico.egresado;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.Egresado;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.EPG;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.ESP;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.PRE;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.VIS;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import pe.edu.lamolina.pivot.controller.seguridad.verificador.VerificadorService;
import pe.edu.lamolina.pivot.controller.seguridad.verificador.VerificadorServiceImp;
import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/egresado")
public class EgresadoController {

    @Autowired
    EgresadoService service;

    @Autowired
    VerificadorService verificadorService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        return "academico/egresado/egresado";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session, HttpServletRequest request) {

        DynatableResponse json = new DynatableResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            List<Carrera> carreras = new ArrayList();
            List<Egresado> egresados = new ArrayList();

            VerificadorServiceImp.CantidadItemsEnum cantidadEnum = verificadorService.verificarCantidad(TipoOficinaEnum.ESP, request, ds);
            if (cantidadEnum == VerificadorServiceImp.CantidadItemsEnum.PARCIAL) {
                carreras = verificadorService.allInstanciasByMenuRol(TipoOficinaEnum.ESP, request, ds);
            }

            if (cantidadEnum != VerificadorServiceImp.CantidadItemsEnum.SIN_PERMISO) {
                egresados = service.allEgresadoByDynatable(filter, carreras, cantidadEnum.name());
            }

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (Egresado egresado : egresados) {
                ObjectNode node = JsonHelper.createJson(egresado, JsonNodeFactory.instance, true,
                        new String[]{
                            "id", "cicloAcademico.descripcion", "promedioGraduacion",
                            "alumno.id", "alumno.codigo", "alumno.estado", "alumno.estadoEnum",
                            "alumno.promedioAcumulado", "alumno.creditosCursados", "alumno.creditosAprobados",
                            "alumno.persona.id",
                            "alumno.persona.apellidosNombres",
                            "alumno.persona.rutaFoto",
                            "alumno.persona.tipoFoto",
                            "alumno.persona.tipoDocumento.simbolo",
                            "alumno.persona.numeroDocIdentidad",
                            "alumno.persona.telefono",
                            "alumno.persona.celular",
                            "alumno.persona.email",
                            "alumno.persona.emailCompania",
                            "alumno.carrera.nombre",
                            "alumno.carrera.codigo",
                            "alumno.carrera.tipoEnum",
                            "alumno.carrera.tipo",
                            "alumno.carrera.facultad.codigo",
                            "alumno.carrera.facultad.nombre",
                            "alumno.modalidadEstudio.codigo",
                            "alumno.situacionAcademica.codigo",
                            "alumno.situacionAcademica.nombre",
                            "alumno.modalidadEstudio.nombre",
                            "alumno.cicloIngreso.descripcion",
                            "alumno.cicloActivo.descripcion"
                        });

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

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

            List<Carrera> carreras = new ArrayList();
            VerificadorServiceImp.CantidadItemsEnum cantidadEnum = verificadorService.verificarCantidad(TipoOficinaEnum.ESP, request, ds);
            if (cantidadEnum == VerificadorServiceImp.CantidadItemsEnum.PARCIAL) {
                carreras = verificadorService.allInstanciasByMenuRol(TipoOficinaEnum.ESP, request, ds);
            }

            EgresadoResumen resumen = new EgresadoResumen();
            if (cantidadEnum != VerificadorServiceImp.CantidadItemsEnum.SIN_PERMISO) {
                resumen = service.findResumenEgresado(carreras, cantidadEnum.name());
            }
            
            response.setData(JsonHelper.createJson(resumen, JsonNodeFactory.instance, true,new String[]{"*"}));
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

}
