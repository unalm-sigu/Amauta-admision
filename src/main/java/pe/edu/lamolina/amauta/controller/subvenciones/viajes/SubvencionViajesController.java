package pe.edu.lamolina.amauta.controller.subvenciones.viajes;

import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.List;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.bienestar.InformeSubvencionado;
import pe.edu.lamolina.model.constantines.GlobalConstantine;

@Slf4j
@Controller
@RequestMapping("subvenciones/viajes")
public class SubvencionViajesController {

    @Autowired
    SubvencionViajesService service;

    private final String rutaModulo = this.getClass().getAnnotation(RequestMapping.class).value()[0];

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        model.addAttribute("ciclo", ds.getCicloAcademico());
        model.addAttribute("rutaModulo", rutaModulo);
        return "subvenciones/viajes/subvencionViajes";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(HttpSession session, DynatableFilter filter) {

        DynatableResponse json = new DynatableResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            List<InformeSubvencionado> informes = service.allInformesByDynatble(ds.getPersona(), ds.getCicloAcademico(), filter);

            ArrayNode array = JaneHelper
                    .from(informes)
                    .only("id,importeAsignado,motivoCambioImporte,observaciones,estado,estadoEnum,tituloInvestigacion,fechaInforme")
                    .join("archivoInforme", "id,ruta")
                    .join("alumnoSubvencionado", "horasLaborales")
                    .join("alumnoSubvencionado.tipoSubvencion", "nombre,codigo")
                    .join("alumnoSubvencionado.alumno", "id,codigo")
                    .join("alumnoSubvencionado.alumno.persona", "id,apellidosNombres,numeroDocIdentidad")
                    .join("alumnoSubvencionado.alumno.persona.tipoDocumento", "simbolo")
                    .join("alumnoSubvencionado.alumno.carrera", "id,codigo,nombre")
                    .join("alumnoSubvencionado.alumno.carrera.facultad", "id,codigo,nombre")
                    .join("personaCuentaBancaria", "id,numeroCuenta,cuentaInterbancaria,esBcp")
                    .join("personaCuentaBancaria.banco", "nombre")
                    .join("personaCuentaBancaria.banco.empresa", "razonSocial")
                    .join("calendarioInforme", "year")
                    .join("calendarioInforme.mes", "nombre")
                    .join("supervisorVoBo", "id")
                    .join("supervisorVoBo.persona", "id")
                    .array();

            json.setData(array);
            json.setTotal(informes.size());
            json.setFiltered(informes.size());

        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }
        return json;
    }

    @ResponseBody
    @RequestMapping("aprobarInforme")
    public JsonResponse aprobarInforme(@RequestBody InformeSubvencionado informe, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.aprobarInforme(informe, ds.getPersona(), ds);

            response.setSuccess(true);
            response.setMessage("Se aprobó satisfactoriamente el informe");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("observarInforme")
    public JsonResponse observarInforme(@RequestBody InformeSubvencionado informe, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.observarInforme(informe, ds.getPersona(), ds);

            response.setSuccess(true);
            response.setMessage("Se observó satisfactoriamente el informe");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

}
