package pe.edu.lamolina.pivot.controller.academico.carrera;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.AreaPosgrado;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.OrientacionCarrera;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.TipoCarreraEnum;
import static pe.edu.lamolina.model.enums.TipoCarreraEnum.DOC;
import static pe.edu.lamolina.model.enums.TipoCarreraEnum.MAE;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/carrera")
public class CarreraController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CarreraService service;

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {

        dataBinder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String value) {
                try {
                    setValue(new SimpleDateFormat("dd/MM/yyyy").parse(value));
                } catch (ParseException e) {
                    setValue(null);
                }
            }
        });

        dataBinder.registerCustomEditor(BigDecimal.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String value) {
                try {
                    setValue(new BigDecimal(value.replaceAll(",", "")));
                } catch (Exception e) {
                    setValue(null);
                }
            }
        });
    }

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();
        model.addAttribute("ciclo", ciclo);
        model.addAttribute("tiposEstudio", ModalidadEstudioEnum.values());
        model.addAttribute("resumen", service.resumen());
        return "academico/carrera/carrera";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse allByDynatable(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            List<Carrera> carreras = service.allByDynatable(filter);

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            for (Carrera carrera : carreras) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

                node.put("id", carrera.getId());
                node.put("nombre", carrera.getNombre());
                node.put("codigo", carrera.getCodigo());
                node.put("facultad", carrera.getFacultad().getNombre());
                node.put("modalidad", carrera.getModalidadEstudio().getNombre());
                node.put("tipo", carrera.getTipo());
                node.put("tipoEnum", !"".equals(this.getTipoEstudio(carrera.getTipo())) ? carrera.getTipoEnum().getValue() : "");
                ArrayNode arrayOriCarrera = new ArrayNode(JsonNodeFactory.instance);
                for (OrientacionCarrera oriCarrera : carrera.getOrientacionCarrera()) {
                    ObjectNode node2 = new ObjectNode(JsonNodeFactory.instance);
                    node2.put("nombre", oriCarrera.getNombre());
                    arrayOriCarrera.add(node2);
                }
                node.set("oriCarreras", arrayOriCarrera);
                node.put("estado", carrera.getEstado());
                node.put("estadoEnum", carrera.getEstadoEnum().getValue());
                node.put("motivo", carrera.getMotivoAnulacion());
                node.put("estadoAdmision", carrera.getEstadoAdmision());
                node.put("estadoAdmisionEnum", carrera.getEstadoAdmisionEnum().getValue());
                node.put("areaPosgrado", (String) ObjectUtil.getParentTree(carrera, "areaPosgrado.nombre"));

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

    public String getTipoEstudio(String tipo) {
        if (tipo.equals(TipoCarreraEnum.SEM.name()) || tipo.equals(TipoCarreraEnum.PMA.name())) {
            return "";
        }
        return tipo;
    }

    @ResponseBody
    @RequestMapping("cambiarEstadoCarrera")
    public JsonResponse cambiarEstadoCarrera(Carrera carrera) {
        JsonResponse response = new JsonResponse();
        response.setSuccess(false);

        try {
            service.cambiarEstadoCarrera(carrera);

            response.setMessage("Se cambio de estado satisfactoriamente.");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("cambiarEstadoAdmision")
    public JsonResponse cambiarEstadoAdmision(@RequestParam("carreraId") Long carreraId) {
        JsonResponse response = new JsonResponse();
        response.setSuccess(false);

        try {
            service.cambiarEstadoAdmision(new Carrera(carreraId));

            response.setMessage("Se cambio de estado admisión satisfactoriamente.");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @RequestMapping("nuevo")
    public String nuevoRol(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        Compania cia = ds.getCompania();

        Carrera carrera = new Carrera();
        carrera.setOrientacionCarrera(new ArrayList());

        model.addAttribute("tipos", JsonHelper.enumToJson(new Enum[]{MAE, DOC}));
        model.addAttribute("carrera", createCarreraJson(carrera));
        model.addAttribute("facultades", createAllFacultadesJson(service.allFacultades()));
        model.addAttribute("modalidades", createAllModalidadesJson(service.allPrePostgrado(cia)));
        model.addAttribute("areasPosgrado", createAllAreaPosgradoJson(service.allAreaPosgrado()));
        model.addAttribute("orientaciones", createAllOrientacionesJson(new ArrayList()));

        return "academico/carrera/carreraForm";
    }

    @ResponseBody
    @RequestMapping("saveCarrera")
    public JsonResponse saveCarrera(@RequestBody Carrera carrera, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            //TypesUtil.delay(3000);
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            boolean isSave = carrera.getId() != null;
            Carrera carreraBD = service.save(carrera, ds);

            response.setMessage("Especialidad " + (isSave ? "actualizada" : "creada") + " satisfactoriamente");
            response.setData(createCarreraJson(carreraBD));
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @RequestMapping("{id}/editar")
    public String editarCarrera(@PathVariable("id") Long idCarrera, Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        Compania cia = ds.getCompania();

        Carrera carrera = service.find(idCarrera);

        model.addAttribute("tipos", JsonHelper.enumToJson(new Enum[]{MAE, DOC}));
        model.addAttribute("carrera", createCarreraJson(carrera));
        model.addAttribute("facultades", createAllFacultadesJson(service.allFacultades()));
        model.addAttribute("modalidades", createAllModalidadesJson(service.allPrePostgrado(cia)));
        model.addAttribute("areasPosgrado", createAllAreaPosgradoJson(service.allAreaPosgrado()));
        model.addAttribute("orientaciones", createAllOrientacionesJson(carrera.getOrientacionCarrera()));
        return "academico/carrera/carreraForm";
    }

    @ResponseBody
    @RequestMapping("deleteOrientacion")
    public JsonResponse deleteOrientacion(@RequestBody OrientacionCarrera orientacion, HttpSession session) {

        JsonResponse response = new JsonResponse();
        response.setSuccess(false);

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            OrientacionCarrera eliminada = service.deleteOrientacion(orientacion, ds);

            response.setSuccess(true);
            response.setMessage("Orientación " + (eliminada == null ? "eliminada" : "inhabilitada") + " satisfactoriamente");
            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            node.put("eliminados", (eliminada == null) ? 1 : 0);
            node.set("orientacion", createOrientacionJson((eliminada == null) ? new OrientacionCarrera() : eliminada));
            response.setData(node);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("saveOrientaciones")
    public JsonResponse saveOrientaciones(@RequestBody Carrera carrera, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            List<OrientacionCarrera> orientaciones = service.saveOrientaciones(carrera, ds);

            response.setData(createAllOrientacionesJson(orientaciones));
            response.setMessage("Orientaciones registradas satisfactoriamente.");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("saveOrientacion")
    public JsonResponse saveOrientacion(@RequestBody OrientacionCarrera orientacionForm, HttpSession session) {
        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            OrientacionCarrera orientacionBD = service.editarOrientacion(orientacionForm, ds);

            response.setData(createOrientacionJson(orientacionBD));
            response.setMessage("Orientación modificada satisfactoriamente.");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("activarOrientacion")
    public JsonResponse activarOrientacion(@RequestBody OrientacionCarrera orientacion, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            OrientacionCarrera orientacionBD = service.activarOrientacion(orientacion, ds);
            response.setMessage("Se activó la Orientación satisfactoriamente.");
            response.setData(createOrientacionJson(orientacionBD));
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("allTiposCarrera")
    public JsonResponse allTiposCarrera(HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            TipoCarreraEnum[] tipos = new TipoCarreraEnum[2];
            tipos[0] = TipoCarreraEnum.MAE;
            tipos[1] = TipoCarreraEnum.DOC;

            ArrayNode jsonList = new ArrayNode(jsonFactory);

            for (int i = 0; i < tipos.length; i++) {
                ObjectNode json = new ObjectNode(jsonFactory);

                json.put("id", tipos[i].name());
                json.put("nombre", tipos[i].getValue());

                jsonList.add(json);
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

    private ObjectNode createCarreraJson(Carrera carrera) {
        ObjectNode node = JsonHelper.createJson(carrera, JsonNodeFactory.instance, true, new String[]{
            "id", "nombre", "codigo", "estadoEnum", "estado", "tipo", "tipoEnum",
            "modalidadEstudio.id",
            "modalidadEstudio.nombre",
            "modalidadEstudio.codigo",
            "facultad.id",
            "facultad.nombre",
            "facultad.codigo",
            "areaPosgrado.id",
            "areaPosgrado.nombre",
            "areaPosgrado.codigo"
        });
        return node;
    }

    private ObjectNode createOrientacionJson(OrientacionCarrera orientacion) {
        ObjectNode node = JsonHelper.createJson(orientacion, JsonNodeFactory.instance, true, new String[]{
            "id", "estado", "estadoEnum", "codigo", "nombre", "motivoAnulacion", "carrera.id"
        });
        node.put("nombre2", orientacion.getNombre());
        return node;
    }

    private ArrayNode createAllFacultadesJson(List<Facultad> facultades) {
        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
        for (Facultad fac : facultades) {
            ObjectNode node = JsonHelper.createJson(fac, JsonNodeFactory.instance, true, new String[]{
                "id", "codigo", "nombre"
            });
            arrayNode.add(node);
        }
        return arrayNode;
    }

    private ArrayNode createAllModalidadesJson(List<ModalidadEstudio> modalidades) {
        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
        for (ModalidadEstudio modalidad : modalidades) {
            ObjectNode node = JsonHelper.createJson(modalidad, JsonNodeFactory.instance, true, new String[]{
                "id", "codigo", "nombre"
            });
            arrayNode.add(node);
        }
        return arrayNode;
    }

    private ArrayNode createAllAreaPosgradoJson(List<AreaPosgrado> areasPosgrado) {
        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
        for (AreaPosgrado area : areasPosgrado) {
            ObjectNode node = JsonHelper.createJson(area, JsonNodeFactory.instance, true, new String[]{
                "id", "codigo", "nombre"
            });
            arrayNode.add(node);
        }
        return arrayNode;
    }

    private ArrayNode createAllOrientacionesJson(List<OrientacionCarrera> orientaciones) {
        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
        for (OrientacionCarrera orientacion : orientaciones) {
            ObjectNode node = createOrientacionJson(orientacion);
            arrayNode.add(node);
        }
        return arrayNode;
    }

}
