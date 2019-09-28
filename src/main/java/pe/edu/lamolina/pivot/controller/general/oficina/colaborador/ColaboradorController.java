package pe.edu.lamolina.pivot.controller.general.oficina.colaborador;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.enums.ColaboradorEstadoEnum;
import pe.edu.lamolina.model.enums.SexoEnum;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.FuncionColaborador;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.PerfilCompania;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.controller.seguridad.verificador.VerificadorService;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("general/oficina")
public class ColaboradorController {

    @Autowired
    ColaboradorService service;
    @Autowired
    VerificadorService verificadorService;

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

    @RequestMapping("{idOficina}/updateColaborador/{idColaborador}")
    public String updateColaborador(
            @RequestParam(value = "origen", required = false) String origen,
            @PathVariable("idOficina") Long idOficina,
            @PathVariable("idColaborador") Long idColaborador, Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        boolean puedeVerOficina = verificadorService.puedeVerOficina(new Oficina(idOficina), ds);
        if (!puedeVerOficina) {
            return "redirect:" + verificadorService.getOrigen(origen, "/");
        }

        Colaborador colaborador = service.findColaborador(new Colaborador(idColaborador));
        List<PerfilCompania> misfunciones = service.allFuncionByColaborador(colaborador);

        model.addAttribute("colaborador", createColaboradorJson(colaborador));
        model.addAttribute("misfunciones", createPerfilesJson(misfunciones));
        loadAttibutesColaboradorForm(idOficina, origen, ds, model);

        return "general/oficina/colaborador/colaboradorForm";
    }

    @RequestMapping("{idOficina}/nuevoColaborador")
    public String nuevoColaborador(
            @PathVariable("idOficina") Long idOficina,
            @RequestParam(value = "origen", required = false) String origen, Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        boolean puedeVerOficina = verificadorService.puedeVerOficina(new Oficina(idOficina), ds);
        if (!puedeVerOficina) {
            return "redirect:" + verificadorService.getOrigen(origen, "/");
        }

        model.addAttribute("colaborador", createColaboradorJson(new Colaborador()));
        model.addAttribute("misfunciones", createPerfilesJson(new ArrayList()));
        loadAttibutesColaboradorForm(idOficina, origen, ds, model);

        return "general/oficina/colaborador/colaboradorForm";
    }

    private void loadAttibutesColaboradorForm(Long idOficina, String origen, DataSessionPivot ds, Model model) {
        Oficina oficina = service.findOficina(new Oficina(idOficina));
        List<TipoDocIdentidad> tiposDocumentos = service.allDocumentosIdentidad();
        List<Oficina> areas = service.allAreasByOficinaMain(oficina);
        List<PerfilCompania> cargos = service.allCargoByOficinaAltoNivel(oficina, ds);
        List<PerfilCompania> funciones = service.allFuncionByOficinaAltoNivel(oficina, ds);

        model.addAttribute("oficina", createOficinaJson(oficina));
        model.addAttribute("tipoDocumento", createTipoDocumentoJson(tiposDocumentos));
        model.addAttribute("sexo", SexoEnum.values());
        model.addAttribute("areas", createOficinasJson(areas));
        model.addAttribute("cargos", createPerfilesJson(cargos));
        model.addAttribute("funciones", createPerfilesJson(funciones));

        model.addAttribute("origen", verificadorService.getOrigen(origen, "/"));
        model.addAttribute("puedeEditarOficinas", verificadorService.puedeEditarOficinas(ds));
    }

    @ResponseBody
    @RequestMapping("updateColaborador")
    public JsonResponse saveUpdateColaborador(@RequestBody ColaboradorBean colaboradorBean, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {

            Colaborador colaborador = colaboradorBean.getColaborador();
            List<PerfilCompania> perfilCompanias = TypesUtil.getListNotNull(colaboradorBean.getPerfilCompanias());
            List<FuncionColaborador> funciones = new ArrayList();
            for (PerfilCompania perfilCompania : perfilCompanias) {
                FuncionColaborador funcionColaborador = new FuncionColaborador();
                funcionColaborador.setFuncion(perfilCompania);
                funciones.add(funcionColaborador);
            }
            colaborador.setFuncionColaborador(funciones);
            service.updateColaborador(colaborador, colaboradorBean.getOficinaMean(), ds);

            response.setMessage("Se actualizó el colaborador satisfactoriamente");
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("saveColaborador")
    public JsonResponse saveColaborador(@RequestBody ColaboradorBean colaboradorBean, HttpSession session) {
        JsonResponse response = new JsonResponse();
        response.setSuccess(false);
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            ArrayList<FuncionColaborador> funciones = new ArrayList();
            Colaborador colaborador = colaboradorBean.getColaborador();
            if (colaboradorBean.getPerfilCompanias() != null) {
                for (PerfilCompania perfilCompania : colaboradorBean.getPerfilCompanias()) {
                    FuncionColaborador funcionColaborador = new FuncionColaborador();
                    funcionColaborador.setFuncion(perfilCompania);
                    funciones.add(funcionColaborador);
                }
            }
            colaborador.setFuncionColaborador(funciones);
            if (colaborador.getPersona().getId() == null) {
                service.saveColaborador(colaborador, colaboradorBean.getOficinaMean(), ds.getCompania(), ds);
                response.setSuccess(true);
            } else {
                Boolean success = service.saveColaboradorExistente(colaborador, colaboradorBean.getOficinaMean(), ds.getCompania(), ds);
                response.setSuccess(true);
                response.setSuccess(success);
            }
            response.setMessage("Se agregó el colaborador satisfactoriamente");
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("validarEmail")
    public JsonResponse validarEmail(@RequestBody Persona persona, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            Usuario usu = service.verificarEmail(persona);
            if (usu == null) {
                response.setSuccess(true);
            } else {
                response.setSuccess(false);
            }
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("validarDoc")
    public JsonResponse validarDoc(@RequestBody Persona persona) {

        JsonResponse response = new JsonResponse();

        try {

            Persona personaDb = service.verificarDocumento(persona);
            if (personaDb == null) {
                response.setSuccess(true);
            } else {
                response.setSuccess(false);
            }

            ObjectNode node = JsonHelper.createJson(personaDb, JsonNodeFactory.instance, true, new String[]{"*", "tipoDocumento.*"});
            response.setData(node);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @RequestMapping("{oficina}/colaboradores")
    public String colaboradores(
            @PathVariable("oficina") Long idOficina,
            @RequestParam(value = "origen", required = false) String origen,
            Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        boolean puedeVerOficina = verificadorService.puedeVerOficina(new Oficina(idOficina), ds);
        if (!puedeVerOficina) {
            return "redirect:" + verificadorService.getOrigen(origen, "/");
        }

        Oficina oficina = service.findOficina(new Oficina(idOficina));
        List<Oficina> areas = service.allAreasByOficinaMain(new Oficina(idOficina));
        List<PerfilCompania> cargos = service.allCargoByOficinaAltoNivel(new Oficina(idOficina), ds);

        model.addAttribute("oficina", oficina);
        model.addAttribute("areas", createOficinasJson(areas));
        model.addAttribute("cargos", createPerfilesJson(cargos));
        model.addAttribute("origen", verificadorService.getOrigen(origen, "/"));
        model.addAttribute("estadosEmp", JsonHelper.enumToJson(ColaboradorEstadoEnum.values()));
        model.addAttribute("puedeEditarOficinas", verificadorService.puedeEditarOficinas(ds));

        return "general/oficina/colaborador/colaborador";
    }

    @ResponseBody
    @RequestMapping("{oficina}/resumen")
    public JsonResponse resumen(
            @PathVariable("oficina") Long idOficina, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            Oficina oficina = service.findOficina(new Oficina(idOficina));
            ResumenColaborador resumen = service.getResumenColoboradores(oficina);
            response.setData(JsonHelper.createJson(resumen, JsonNodeFactory.instance, true, new String[]{"*"}));
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("{oficina}/updateEstado")
    public JsonResponse updateEstado(
            @PathVariable("oficina") Long idOficina,
            @RequestBody Colaborador colaborador, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.updateEstado(colaborador, new Oficina(idOficina), ds);

            response.setMessage("Se cambio satisfactoriamente el estado del colaborador");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("{idOficina}/listColaboradores")
    public DynatableResponse listColaboradores(
            @PathVariable("idOficina") Long idOficina,
            DynatableFilter filter, HttpSession session) {

        DynatableResponse response = new DynatableResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            List<Colaborador> colaboradores = new ArrayList();
            boolean puedeVerOficina = verificadorService.puedeVerOficina(new Oficina(idOficina), ds);
            if (puedeVerOficina) {
                colaboradores = service.getColaboradores(filter, new Oficina(idOficina));
            }
            response.setData(createColaboradoresJson(colaboradores));
            response.setTotal(filter.getTotal());
            response.setFiltered(filter.getFiltered());

        } catch (Exception e) {
            e.printStackTrace();
            response.setTotal(0);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("searchPersona")
    public JsonResponse searchPersona(@RequestParam("nombre") String buscar) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();
        logger.debug("buscar {}", buscar);
        try {
            ArrayNode node = new ArrayNode(jsonFactory);
            List<Persona> personas = service.allPersonasByNombre(buscar);
            for (Persona persona : personas) {
                node.add(JsonHelper.createJson(persona, jsonFactory, new String[]{
                    "*",}));
            }
            response.setData(node);
            response.setSuccess(true);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("{idOficina}/allFuncionesColaborador")
    public JsonResponse allFuncionesColaborador(
            @PathVariable("idOficina") Long idOficina,
            @RequestBody Colaborador colaborador, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            List<PerfilCompania> perfiles = service.allFuncionByColaborador(colaborador);
            response.setData(createPerfilesJson(perfiles));
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("{idOficina}/allCargosOficina")
    public JsonResponse allCargosOficina(
            @PathVariable("idOficina") Long idOficina,
            Colaborador colaborador, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            List<PerfilCompania> perfiles = service.allCargoByOficina(new Oficina(idOficina), ds);
            response.setData(createPerfilesJson(perfiles));
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("{idOficina}/allFuncionesOficina")
    public JsonResponse allFuncionesOficina(
            @PathVariable("idOficina") Long idOficina,
            Colaborador colaborador, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            List<PerfilCompania> perfiles = service.allFuncionByOficina(new Oficina(idOficina), ds);
            response.setData(createPerfilesJson(perfiles));
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("{idOficina}/savefuncion")
    public JsonResponse saveFuncion(
            @PathVariable("idOficina") Long idOficina,
            @RequestBody PerfilCompania perfilCompania, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            service.addFuncion(perfilCompania, new Oficina(idOficina), ds);
            response.setMessage("Función creada satisfactoriamente");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("{idOficina}/savecargo")
    public JsonResponse saveCargo(
            @PathVariable("idOficina") Long idOficina,
            @RequestBody PerfilCompania perfilCompania, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            service.addCargo(perfilCompania, new Oficina(idOficina), ds);
            response.setMessage("Función creada satisfactoriamente");
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
            "fechaInicioJefatura", "fechaEncargatura",
            "tipoOficina.nivelEnum",
            "tipoOficina.codigoEnum",
            "oficinaSuperior.id",
            "oficinaSuperior.nombre",
            "cargoJefe.nombre",
            "jefeEncargado.id",
            "jefeEncargado.nombreConTitulo",
            "personaJefe.id",
            "personaJefe.nombreConTitulo"
        });
        return node;
    }

    private ObjectNode createColaboradorJson(Colaborador colaborador) {
        List<ColaboradorEstadoEnum> estadosTrabajando = Arrays.asList(
                ColaboradorEstadoEnum.ACT, ColaboradorEstadoEnum.DSC, ColaboradorEstadoEnum.PER, ColaboradorEstadoEnum.VAC
        );

        ObjectNode node = JsonHelper.createJson(colaborador, JsonNodeFactory.instance, true, new String[]{
            "*",
            "persona.id",
            "persona.tipoDocumento.*",
            "persona.numeroDocIdentidad",
            "persona.tituloAcademico",
            "persona.nombreCompleto",
            "persona.emailCompania",
            "persona.tipoFoto",
            "persona.rutaFoto",
            "oficina.id",
            "oficina.codigo",
            "oficina.nombre",
            "cargo.id",
            "cargo.codigo",
            "cargo.nombre"
        });

        node.put("estaTrabajando", estadosTrabajando.contains(colaborador.getEstadoEnum()));

        return node;
    }

    private ArrayNode createTipoDocumentoJson(List<TipoDocIdentidad> tiposDocs) {
        ArrayNode arrayArea = new ArrayNode(JsonNodeFactory.instance);
        for (TipoDocIdentidad tdoc : tiposDocs) {
            ObjectNode node = JsonHelper.createJson(tdoc, JsonNodeFactory.instance, true, new String[]{"*"});
            arrayArea.add(node);
        }
        return arrayArea;
    }

    private ArrayNode createOficinasJson(List<Oficina> oficinas) {
        ArrayNode arrayArea = new ArrayNode(JsonNodeFactory.instance);
        for (Oficina oficina : oficinas) {
            ObjectNode node = createOficinaJson(oficina);
            arrayArea.add(node);
        }
        return arrayArea;
    }

    private ArrayNode createPerfilesJson(List<PerfilCompania> perfiles) {
        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
        for (PerfilCompania perfil : perfiles) {
            ObjectNode node = JsonHelper.createJson(perfil, JsonNodeFactory.instance, new String[]{"*"});
            arrayNode.add(node);
        }
        return arrayNode;
    }

    private ArrayNode createColaboradoresJson(List<Colaborador> colaboradores) {
        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
        List<FuncionColaborador> funcionesColaboradores = service.allFuncionesByColaboradores(colaboradores);
        Map<Long, List<FuncionColaborador>> mapFunciones = TypesUtil.convertListToMapList("colaborador.id", funcionesColaboradores);
        List<ColaboradorEstadoEnum> estadosTrabajando = Arrays.asList(
                ColaboradorEstadoEnum.ACT, ColaboradorEstadoEnum.DSC, ColaboradorEstadoEnum.PER, ColaboradorEstadoEnum.VAC
        );

        for (Colaborador colaborador : colaboradores) {
            ObjectNode node = JsonHelper.createJson(colaborador, JsonNodeFactory.instance, new String[]{
                "id", "estado", "estadoEnum", "codigo",
                "oficina.nombre",
                "cargo.nombre",
                "persona.tipoDocumento.simbolo",
                "persona.id", "persona.nombreCompleto", "persona.numeroDocIdentidad", "persona.emailCompania"
            });
            node.put("estaTrabajando", estadosTrabajando.contains(colaborador.getEstadoEnum()));
            node.put("funciones", TypesUtil.getListNotNull(mapFunciones.get(colaborador.getId())).size());
            arrayNode.add(node);
        }

        return arrayNode;
    }

}
