package pe.edu.lamolina.pivot.controller.general.oficina;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
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
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import pe.edu.lamolina.model.general.AusenciaJefe;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.PerfilCompania;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.TipoOficina;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.constant.Messages;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("general/oficina")
public class OficinaController {

    @Autowired
    OficinaService service;

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

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model) {
        return "general/oficina/oficina";
    }

    @ResponseBody
    @RequestMapping("all")
    public DynatableResponse all(DynatableFilter filter, HttpSession session) {

        DynatableResponse json = new DynatableResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            Compania compania = ds.getCompania();

            List<Oficina> oficinas = service.allByDynatable(filter, compania);
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (Oficina oficina : oficinas) {
                List<AusenciaJefe> ausencias = oficina.getAusenciasJefe();
                AusenciaJefe ausenciaJefe = ausencias.isEmpty() ? new AusenciaJefe() : ausencias.get(0);
                ObjectNode node = createOficinaJson(oficina);
                node.put("colaboradores", oficina.getColaborador().size());
                node.set("ausenciaJefe", createAusenciaJson(ausenciaJefe));
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
    @RequestMapping("cargo")
    public JsonResponse cargo(@RequestBody PerfilCompania perfilCompania, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            service.addCargo(perfilCompania, ds);
            response.setMessage("Se agregó el cargo satisfactoriamente");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @RequestMapping("nuevo")
    public String nuevo(
            @RequestParam(value = "origen", required = false) String origen,
            Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        List<TipoOficina> tipoOficina = service.allTipoOficina();
        ArrayNode tiposOficinaJson = new ArrayNode(JsonNodeFactory.instance);
        for (TipoOficina tipoOficina1 : tipoOficina) {
            tiposOficinaJson.add(JsonHelper.createJson(tipoOficina1, JsonNodeFactory.instance, new String[]{"*"}));
        }

        ObjectNode oficinaJson = JsonHelper.createJson(new Oficina(), JsonNodeFactory.instance, new String[]{
            "*", "tipoOficina.*", "cargoJefe.*", "oficinaSuperior.*"});
        oficinaJson.put("instanciaReferencia", "");

        model.addAttribute("tiposOficina", tiposOficinaJson.toString());
        model.addAttribute("oficina", oficinaJson.toString());
        model.addAttribute("origen", getOrigen(origen));
        return "general/oficina/oficinaForm";
    }

    @RequestMapping("{oficina}/update")
    public String update(
            @PathVariable("oficina") Long idOficina,
            @RequestParam(value = "origen", required = false) String origen,
            Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        Oficina oficina = service.find(new Oficina(idOficina));
        service.fillReferencia(oficina);

        List<TipoOficina> tipoOficina = service.allTipoOficina();
        ArrayNode tiposOficinaJson = new ArrayNode(JsonNodeFactory.instance);
        for (TipoOficina tipoOficina1 : tipoOficina) {
            tiposOficinaJson.add(JsonHelper.createJson(tipoOficina1, JsonNodeFactory.instance, new String[]{"*"}));
        }

        ObjectNode oficinaJson = createOficinaJson(oficina);
        oficinaJson.put("instanciaReferencia", "");

        model.addAttribute("tiposOficina", tiposOficinaJson.toString());
        model.addAttribute("oficina", oficinaJson.toString());
        model.addAttribute("origen", getOrigen(origen));
        return "general/oficina/oficinaForm";
    }

    @ResponseBody
    @RequestMapping("save")
    public JsonResponse save(@RequestBody Oficina oficina, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            Compania compania = ds.getCompania();
            oficina.setCompania(compania);

            if (oficina.getId() != null) {
                service.update(oficina, ds);
                response.setMessage("Oficina actualizada satisfactoriamente");
            } else {
                service.save(oficina, ds);
                response.setMessage("Oficina creada satisfactoriamente");
            }

            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("allUnidadSuperior")
    public JsonResponse allUnidadSuperior(@RequestParam("nombre") String nombre, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            Compania compania = ds.getCompania();

            List<Oficina> oficinas = service.allUnidadSuperior(nombre, compania);
            ArrayNode array = new ArrayNode(jsonFactory);
            for (Oficina oficina : oficinas) {
                ObjectNode a = new ObjectNode(jsonFactory);
                a.put("id", oficina.getId());
                a.put("codigo", oficina.getCodigo());
                a.put("nombre", oficina.getNombre());
                array.add(a);
            }

            response.setData(array);
            response.setTotal(array.size());
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("allReferencia")
    public JsonResponse allReferencia(@RequestParam("tipo") String tipo, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {

            String columnas = "id,codigo,nombre,estado,estadoEnum";

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            Compania compania = ds.getCompania();
            ArrayNode array = new ArrayNode(jsonFactory);
            TipoOficina oficina = service.findTipoById(tipo);
            if (TipoOficinaEnum.DPTO.name().equalsIgnoreCase(oficina.getCodigo())) {
                List<DepartamentoAcademico> departamentos = service.allDepartamento(compania);
                for (DepartamentoAcademico departamento : departamentos) {
                    ObjectNode a = JsonHelper.createJson(departamento, jsonFactory, columnas.split(","));
                    a.put("descripcion", departamento.getCodigo() + " - " + departamento.getNombre());
                    a.put("modalidad", "");
                    array.add(a);
                }
            }
            if (TipoOficinaEnum.ESP.name().equalsIgnoreCase(oficina.getCodigo())) {
                List<Carrera> carreras = service.allCarrera(compania);
                for (Carrera carrera : carreras) {

                    columnas += ",tipo,tipoEnum";
                    ObjectNode a = JsonHelper.createJson(carrera, jsonFactory, columnas.split(","));

                    ModalidadEstudio modalidad = carrera.getModalidadEstudio();
                    a.put("modalidad", modalidad.getCodigo());

                    String descripcion = carrera.getCodigo() + " - ";
                    descripcion += modalidad.isPostgrado() ? carrera.getTipoEnum().getValue() + " " : "";
                    descripcion += carrera.getNombre();
                    a.put("descripcion", descripcion);

                    array.add(a);
                }
            }
            if (TipoOficinaEnum.FAC.name().equalsIgnoreCase(oficina.getCodigo())) {
                List<Facultad> facultades = service.allFacultad(compania);
                for (Facultad facultad : facultades) {
                    ObjectNode a = JsonHelper.createJson(facultad, jsonFactory, columnas.split(","));
                    a.put("descripcion", facultad.getCodigo() + " - " + facultad.getNombre());
                    a.put("modalidad", "");
                    array.add(a);
                }
            }

            response.setData(array);
            response.setTotal(array.size());
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("cambiarEstado/{accion}")
    public JsonResponse cambiarEstado(
            @RequestBody Oficina oficina,
            @PathVariable("accion") String accion) {

        JsonResponse response = new JsonResponse();

        try {
            service.cambiarEstado(oficina, accion);
            response.setMessage("Oficina actualizada satisfactoriamente");
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, "El registro de esta oficina se encuentra relacionado a otros elementos del sistema");
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("allPersona")
    public JsonResponse allPersona(@RequestParam("nombre") String nombre, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {

            List<Persona> personas = service.allPersona(nombre);
            response.setData(createAllPersonasJson(personas));
            response.setTotal(personas.size());
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("allColaborador")
    public JsonResponse allColaborador(Oficina oficina, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {

            List<Colaborador> colaboradores = service.allColaboradorByOficina(oficina);
            ArrayNode array = new ArrayNode(jsonFactory);
            for (Colaborador colaborador : colaboradores) {
                ObjectNode a = JsonHelper.createJson(colaborador, jsonFactory, true, new String[]{
                    "id", "persona.nombreCompleto", "cargo.nombre"
                });
                array.add(a);
            }

            response.setData(array);
            response.setTotal(array.size());
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("allCargo")
    public JsonResponse allCargo(@RequestParam("nombre") String nombre, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {

            List<PerfilCompania> cargos = service.allCargo(nombre);
            ArrayNode array = new ArrayNode(jsonFactory);
            for (PerfilCompania cargo : cargos) {
                ObjectNode a = new ObjectNode(jsonFactory);
                a.put("id", cargo.getId());
                a.put("nombre", cargo.getNombreDocumento());
                array.add(a);
            }
            response.setData(array);
            response.setTotal(array.size());
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("asignarJefe")
    public JsonResponse asignarJefe(@RequestBody Oficina oficina, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.asignarJefe(oficina, ds);
            response.setMessage("Jefe asignado satisfactoriamente.");
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("actualizarJefe")
    public JsonResponse actualizarJefe(@RequestBody Oficina oficina, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.actualizarJefe(oficina, ds);
            response.setMessage("Jefe asignado satisfactoriamente.");
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("asignarEncargado")
    public JsonResponse asignarEncargado(@RequestBody Oficina oficina, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            service.asignarEncargado(oficina, ds);
            response.setMessage("Encargado asignado satisfactoriamente.");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("actualizarEncargado")
    public JsonResponse actualizarEncargado(@RequestBody Oficina oficina, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.actualizarEncargado(oficina, ds);
            response.setMessage("Jefe asignado satisfactoriamente.");
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("retirarJefe")
    public JsonResponse retirarJefe(Oficina oficina, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            service.retirarJefe(oficina, ds);
            response.setMessage("Jefe retirado satisfactoriamente.");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("retirarEncargado")
    public JsonResponse retirarEncargado(@RequestBody AusenciaJefe ausencia, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            service.retirarEncargado(ausencia, ds);
            response.setMessage("Encargado retirado satisfactoriamente.");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("savefuncion")
    public JsonResponse saveFuncion(PerfilCompania perfilCompania, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            service.addFuncion(perfilCompania, ds);
            response.setMessage(Messages.CREATED);
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("vercargo")
    public JsonResponse verCargo(Oficina oficina, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            JsonNodeFactory jFactory = JsonNodeFactory.instance;
            List<PerfilCompania> perfilCompanias = service.allCargoByOficina(oficina);
            ArrayNode array = new ArrayNode(jFactory);
            for (PerfilCompania perfile : perfilCompanias) {
                ObjectNode node = JsonHelper.createJson(perfile, jFactory, true, new String[]{"*"});
                array.add(node);
            }
            response.setData(array);
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("verfuncion")
    public JsonResponse verFuncion(Oficina oficina, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            JsonNodeFactory jFactory = JsonNodeFactory.instance;
            List<PerfilCompania> perfilCompanias = service.allFuncionByOficina(oficina);
            ArrayNode array = new ArrayNode(jFactory);
            for (PerfilCompania perfile : perfilCompanias) {
                ObjectNode node = JsonHelper.createJson(perfile, jFactory, true, new String[]{"*"});
                array.add(node);
            }
            response.setData(array);
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    private ObjectNode createOficinaJson(Oficina oficina) {
        ObjectNode node = JsonHelper.createJson(oficina, JsonNodeFactory.instance, true, new String[]{
            "id", "nombre", "codigo", "estadoEnum", "estado", "motivoAusenciaJefe",
            "fechaInicioJefatura", "fechaEncargatura", "telefonos", "anexos", "email",
            "instanciaOficina",
            "instanciaOficinaCodigo",
            "instanciaOficinaNombre",
            "compania.telefonos",
            "tipoOficina.id",
            "tipoOficina.codigo",
            "tipoOficina.nombre",
            "tipoOficina.nivelEnum",
            "tipoOficina.codigoEnum",
            "oficinaSuperior.id",
            "oficinaSuperior.nombre",
            "cargoJefe.id",
            "cargoJefe.nombre",
            "jefeEncargado.id",
            "jefeEncargado.nombreConTitulo",
            "jefeEncargado.nombreCompleto",
            "jefeEncargado.tituloAcademico",
            "personaJefe.id",
            "personaJefe.nombreConTitulo",
            "personaJefe.nombreCompleto",
            "personaJefe.tituloAcademico"
        });
        return node;
    }

    private ObjectNode createPersonaJson(Persona persona) {
        ObjectNode node = JsonHelper.createJson(persona, JsonNodeFactory.instance, true, new String[]{
            "id", "nombreCompleto", "tituloAcademico", "numeroDocIdentidad", "tipoDocumento.simbolo"
        });
        return node;
    }

    private ObjectNode createAusenciaJson(AusenciaJefe ausencia) {
        ObjectNode node = JsonHelper.createJson(ausencia, JsonNodeFactory.instance, true, new String[]{
            "id", "fechaInicioEncargatura", "oficina.id", "oficina.nombre", "jefe.id", "encargado.id", "encargado.nombreConTitulo"
        });
        return node;
    }

    private ArrayNode createAllPersonasJson(List<Persona> personas) {
        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
        for (Persona persona : personas) {
            ObjectNode node = createPersonaJson(persona);
            arrayNode.add(node);
        }
        return arrayNode;
    }

    private String getOrigen(String origen) {
        if (StringUtils.isEmpty(origen)) {
            return "/general/oficina";
        }
        byte[] decoded = Base64.getMimeDecoder().decode(origen);
        String output = new String(decoded);
        return output;
    }

}
