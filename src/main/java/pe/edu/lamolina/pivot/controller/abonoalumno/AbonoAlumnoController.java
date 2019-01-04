package pe.edu.lamolina.pivot.controller.abonoalumno;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.finanzas.ItemCargaAbono;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("abonoalumno")
public class AbonoAlumnoController {

    @Autowired
    AbonoAlumnoService service;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        model.addAttribute("cicloAcademico", ds.getCicloAcademico());
        return "abonoalumno/abonoAlumno";
    }

    @RequestMapping("loadarchivohistorico")
    public String loadArchivoHistorico(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        model.addAttribute("cicloAcademico", ds.getCicloAcademico());
        return "abonoalumno/loadArchivoHistorico";
    }

    @ResponseBody
    @RequestMapping("uploadArchivoHistorico")
    public JsonResponse uploadArchivo(@RequestParam("file") MultipartFile file, HttpSession session) {
        JsonResponse json = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            List<Observado> observados = service.loadArchivoHistorico(file, ds.getCicloAcademico(), ds.getUsuario());

            json.setSuccess(true);
            if (!observados.isEmpty()) {
                ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
                for (Observado cargaAbono : observados) {
                    ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                    node.put("operacion", cargaAbono.getOperacion());
                    node.put("descripcion", cargaAbono.getDescripcion());
                    array.add(node);
                }
                json.setMessage("No se ha conseguido relacionar " + observados.size() + " registro(s) a postulantes.");
                json.setData(array);
            } else {
                json.setMessage("Carga de pagos finalizada.");
            }

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, json);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, json);

        } finally {
            return json;
        }

    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            List<ItemCargaAbono> abonosPostulante = service.allAbonosByPostulante(ds.getCicloAcademico(), filter);
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            for (ItemCargaAbono abono : abonosPostulante) {
                ObjectNode node = JsonHelper.createJson(abono, JsonNodeFactory.instance, true,
                        new String[]{
                            "id",
                            "numeroOperacion",
                            "sucursal",
                            "usuarioBanco",
                            "fechaAbono",
                            "importe",
                            "extornado",
                            "descripcion",
                            "postulante.persona.nombreCompleto",
                            "postulante.cicloPostula.cicloAcademico.descripcion",
                            "postulante.codigo",
                            "postulante.modalidadIngreso.nombre",
                            "cuentaBancaria.numero",
                            "cuentaBancaria.nombre"
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
    @RequestMapping("extorno")
    public JsonResponse extorno(@RequestBody ItemCargaAbono form, HttpSession session) {
        JsonResponse json = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            List<ItemCargaAbono> extornados = service.allExtornados(form);
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            for (ItemCargaAbono abono : extornados) {
                ObjectNode node = JsonHelper.createJson(abono, JsonNodeFactory.instance, true,
                        new String[]{
                            "id",
                            "afectadoExtorno",
                            "numeroOperacion",
                            "sucursal",
                            "usuarioBanco",
                            "fechaAbono",
                            "importe",
                            "extornado",
                            "descripcion",
                            "postulante.persona.nombreCompleto",
                            "postulante.cicloPostula.cicloAcademico.descripcion",
                            "postulante.codigo",
                            "postulante.importeTotal",
                            "postulante.pagos",
                            "postulante.modalidadIngreso.nombre",
                            "cuentaBancaria.numero",
                            "cuentaBancaria.nombre"
                        });
                array.add(node);
            }

            json.setData(array);
            json.setSuccess(true);

        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }

        return json;
    }

    @ResponseBody
    @RequestMapping("reasignarExtorno")
    public JsonResponse reasignarExtorno(ItemCargaAbono extornado, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.reasignarExtorno(extornado, ds.getUsuario());
            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            response.setData(node);
            response.setSuccess(true);
            response.setMessage("Extorno reasignado");
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        } finally {
            return response;
        }
    }

    @RequestMapping("loadarchivodiario")
    public String loadArchivoDiario(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        model.addAttribute("cicloAcademico", ds.getCicloAcademico());
        return "abonoalumno/loadArchivoDiario";
    }

    @ResponseBody
    @RequestMapping("uploadArchivoDiario")
    public JsonResponse uploadArchivoDiario(@RequestParam("file") MultipartFile file, Model model, HttpSession session) {
        JsonResponse json = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            List<Observado> observados = service.loadArchivoDiario(file, ds.getCicloAcademico(), ds.getUsuario());

            json.setSuccess(true);
            if (!observados.isEmpty()) {
                ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
                for (Observado cargaAbono : observados) {
                    ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                    node.put("operacion", cargaAbono.getOperacion());
                    node.put("descripcion", cargaAbono.getDescripcion());
                    array.add(node);
                }
                json.setMessage("No se ha conseguido relacionar " + observados.size() + " registro(s) a postulantes.");
                json.setData(array);
            } else {
                json.setMessage("Carga de pagos finalizada.");
            }

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, json);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, json);

        } finally {
            return json;
        }

    }
}
