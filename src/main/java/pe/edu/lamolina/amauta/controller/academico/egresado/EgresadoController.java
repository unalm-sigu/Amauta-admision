package pe.edu.lamolina.amauta.controller.academico.egresado;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import static com.helger.commons.io.stream.StreamHelper.close;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.Egresado;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import pe.edu.lamolina.amauta.controller.seguridad.verificador.VerificadorService;
import pe.edu.lamolina.amauta.controller.seguridad.verificador.VerificadorServiceImp;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.bean.BusquedaBean;
import pe.edu.lamolina.model.enums.TipoBusquedaReporteEgresadoEnum;

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

        ArrayNode tiposBusqueda = new ArrayNode(JsonNodeFactory.instance);
        for (TipoBusquedaReporteEgresadoEnum value : TipoBusquedaReporteEgresadoEnum.values()) {
            ArrayNode permisosCombinacion = new ArrayNode(JsonNodeFactory.instance);
            ObjectNode obj = new ObjectNode(JsonNodeFactory.instance);
            obj.put("name", value.name());
            obj.put("value", value.getValue());
            for (String permiso : value.getPermisos()) {
                ObjectNode objPermiso = new ObjectNode(JsonNodeFactory.instance);
                TipoBusquedaReporteEgresadoEnum permisoEnum = TipoBusquedaReporteEgresadoEnum.valueOf(permiso);
                objPermiso.put("name", permisoEnum.name());
                objPermiso.put("value", permisoEnum.getValue());
                permisosCombinacion.add(objPermiso);
            }
            obj.set("permisos", permisosCombinacion);
            tiposBusqueda.add(obj);
        }
        model.addAttribute("tiposBusqueda", tiposBusqueda);
        return "academico/egresado/egresado";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session, HttpServletRequest request) {

        DynatableResponse json = new DynatableResponse();
        String codeRequest = verificadorService.generateCodeRequest();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            List<Carrera> carreras = new ArrayList();
            List<Egresado> egresados = new ArrayList();

            VerificadorServiceImp.CantidadItemsEnum cantidadEnum = verificadorService.verificarCantidad(TipoOficinaEnum.ESP, request, ds);
            if (cantidadEnum == VerificadorServiceImp.CantidadItemsEnum.PARCIAL) {
                carreras = verificadorService.allInstanciasByMenuRol(TipoOficinaEnum.ESP, request, ds, codeRequest);
            }

            if (cantidadEnum != VerificadorServiceImp.CantidadItemsEnum.SIN_PERMISO) {
                egresados = service.allEgresadoByDynatable(filter, carreras, cantidadEnum.name());
            }

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (Egresado egresado : egresados) {
                ObjectNode node = JaneHelper
                        .from(egresado)
                        .only("id,promedioGraduacion")
                        .join("cicloAcademico", "descripcion")
                        .join("alumno", "id,codigo,estado,estadoEnum,promedioAcumulado,creditosCursados,creditosAprobados")
                        .join("alumno.persona", "id,apellidosNombres,rutaFoto,tipoFoto,numeroDocIdentidad,telefono,celular,email,emailCompania")
                        .join("alumno.persona.tipoDocumento", "simbolo")
                        .join("alumno.carrera", "nombre,codigo,tipoEnum,tipo")
                        .join("alumno.carrera.facultad", "codigo,nombre")
                        .join("alumno.modalidadEstudio", "codigo,nombre")
                        .join("alumno.situacionAcademica", "codigo,nombre")
                        .join("alumno.cicloIngreso", "descripcion")
                        .join("alumno.cicloActivo", "descripcion")
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

            EgresadoResumen resumen = new EgresadoResumen();
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
    @RequestMapping("allCiclos")
    public JsonResponse allCiclos(@RequestParam("nombre") String nombre, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        try {

            JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
            List<CicloAcademico> lista = service.allCiclosByNombre(nombre, ds);
            ArrayNode jsonList = new ArrayNode(jsonFactory);

            for (CicloAcademico alum : lista) {
                jsonList.add(JsonHelper.createJson(alum, jsonFactory, true,
                        new String[]{
                            "*"}));
            }

            response.setData(jsonList);
            response.setTotal(jsonList.size());
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("allFacultad")
    public JsonResponse allFacultad(@RequestParam("nombre") String nombre, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        try {

            JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
            List<Facultad> lista = service.allFacultadByNombre(nombre, ds);
            ArrayNode jsonList = new ArrayNode(jsonFactory);

            for (Facultad alum : lista) {
                jsonList.add(JsonHelper.createJson(alum, jsonFactory, true,
                        new String[]{
                            "*"}));
            }

            response.setData(jsonList);
            response.setTotal(jsonList.size());
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("allCarrera")
    public JsonResponse allCarrera(@RequestParam("nombre") String nombre, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        try {

            JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
            List<Carrera> lista = service.allCarreraByNombre(nombre, ds);
            ArrayNode jsonList = new ArrayNode(jsonFactory);

            for (Carrera alum : lista) {
                jsonList.add(JsonHelper.createJson(alum, jsonFactory, true,
                        new String[]{
                            "*"}));
            }

            response.setData(jsonList);
            response.setTotal(jsonList.size());
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @RequestMapping("reporte")
    public void bachillerReporte(@RequestParam("busquedaBean") BusquedaBean busquedaBean, Model model, HttpSession session, HttpServletResponse response) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        try {
            String fileName = service.downloadReporte(busquedaBean, ds);
            pdfResponse(fileName, "Reporte.pdf", response);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, model);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, model);
        }
    }

    private void pdfResponse(String name, String outputFile, HttpServletResponse response) throws IOException {
        if (!name.isEmpty()) {
            File filex = new File(name);
            if (!filex.exists()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            DateTime hoy = new DateTime();

            response.reset();
            response.setBufferSize(GlobalConstantine.DEFAULT_BUFFER_SIZE_DOWNLOAD);
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "inline; filename=\"" + outputFile + "\"");

            BufferedInputStream input = null;
            BufferedOutputStream output = null;

            try {
                input = new BufferedInputStream(new FileInputStream(filex), GlobalConstantine.DEFAULT_BUFFER_SIZE_DOWNLOAD);
                output = new BufferedOutputStream(response.getOutputStream(), GlobalConstantine.DEFAULT_BUFFER_SIZE_DOWNLOAD);
                IOUtils.copy(input, output);
                response.flushBuffer();
            } finally {
                close(output);
                close(input);
            }
        }
    }

}
