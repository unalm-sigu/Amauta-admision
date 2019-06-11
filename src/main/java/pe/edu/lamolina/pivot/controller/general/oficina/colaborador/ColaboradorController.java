package pe.edu.lamolina.pivot.controller.general.oficina.colaborador;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.enums.ColaboradorEstadoEnum;
import pe.edu.lamolina.model.enums.SexoEnum;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.FuncionColaborador;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.PerfilCompania;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("general/oficina")
public class ColaboradorController {

    @Autowired
    ColaboradorService service;

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
            @PathVariable("idColaborador") Long idColaborador, Model model) {

        JsonNodeFactory jFactory = JsonNodeFactory.instance;

        Colaborador colaborador = service.findColarador(new Colaborador(idColaborador));
        ObjectNode jsonColaborador = JsonHelper.createJson(colaborador, jFactory, true, new String[]{
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
            "cargo.nombre",});

        List<TipoDocIdentidad> tiposDocumentos = service.allDocumentosIdentidad();
        List<Oficina> oficinas = service.allAreasByOficinaMain(new Oficina(idOficina));

        List<PerfilCompania> cargos = service.allCargoByOficina(new Oficina(idOficina));
        List<PerfilCompania> funciones = service.allFuncionByOficina(new Oficina(idOficina));

        List<PerfilCompania> misfunciones = service.allFuncionByColaborador(colaborador);

        ArrayNode arrayArea = new ArrayNode(JsonNodeFactory.instance);
        for (Oficina oficina : oficinas) {
            ObjectNode node = createOficinaJson(oficina);
            arrayArea.add(node);
        }

        ArrayNode arrayCargos = new ArrayNode(jFactory);
        for (PerfilCompania cargo : cargos) {
            ObjectNode node = JsonHelper.createJson(cargo, jFactory, true, new String[]{"*"});
            arrayCargos.add(node);
        }

        ArrayNode arrayFunciones = new ArrayNode(jFactory);
        for (PerfilCompania funcion : funciones) {
            ObjectNode node = JsonHelper.createJson(funcion, jFactory, true, new String[]{"*"});
            arrayFunciones.add(node);
        }

        ArrayNode arrayMisFunciones = new ArrayNode(jFactory);
        for (PerfilCompania mifuncion : misfunciones) {
            ObjectNode node = JsonHelper.createJson(mifuncion, jFactory, true, new String[]{"*"});
            arrayMisFunciones.add(node);
        }

        ArrayNode arrayTiposDocumentos = new ArrayNode(jFactory);
        for (TipoDocIdentidad tipoDoc : tiposDocumentos) {
            ObjectNode node = JsonHelper.createJson(tipoDoc, JsonNodeFactory.instance, true, new String[]{"*"});
            arrayTiposDocumentos.add(node);
        }

        model.addAttribute("colaborador", jsonColaborador);
        model.addAttribute("oficina", idOficina);
        model.addAttribute("tipoDocumento", arrayTiposDocumentos);
        model.addAttribute("sexo", SexoEnum.values());
        model.addAttribute("areas", arrayArea);

        model.addAttribute("funciones", arrayFunciones);
        model.addAttribute("cargos", arrayCargos);
        model.addAttribute("misfunciones", arrayMisFunciones);
        model.addAttribute("origen", getOrigen(origen));

        return "general/oficina/colaborador/colaboradorForm";
    }

    @ResponseBody
    @RequestMapping("updateColaborador")
    public JsonResponse saveUpdateColaborador(@RequestBody ColaboradorBean colaboradorBean, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {

            Colaborador colaborador = colaboradorBean.getColaborador();
            List<PerfilCompania> perfilCompanias = colaboradorBean.getPerfilCompanias();
            List<FuncionColaborador> funciones = new ArrayList();
            if (perfilCompanias != null) {
                for (PerfilCompania perfilCompania : perfilCompanias) {
                    FuncionColaborador funcionColaborador = new FuncionColaborador();
                    funcionColaborador.setFuncion(perfilCompania);
                    funciones.add(funcionColaborador);
                }
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

    @RequestMapping("{idOficina}/nuevoColaborador")
    public String nuevoColaborador(
            @PathVariable("idOficina") Long idOficina,
            @RequestParam(value = "origen", required = false) String origen, Model model) {

        List<TipoDocIdentidad> tiposDocumentos = service.allDocumentosIdentidad();
        List<Oficina> oficinas = service.allAreasByOficinaMain(new Oficina(idOficina));
        List<PerfilCompania> cargos = service.allCargoByOficina(new Oficina(idOficina));
        List<PerfilCompania> funciones = service.allFuncionByOficina(new Oficina(idOficina));

        ObjectNode colaboradorJson = JsonHelper.createJson(new Colaborador(), JsonNodeFactory.instance, true, new String[]{
            "*",
            "persona.id",
            "persona.tipoDocumento.id",
            "persona.numeroDocIdentidad",
            "persona.sexo",
            "oficina.id",
            "cargo.id",
            "funcionColaborador.id"
        });

        JsonNodeFactory jFactory = JsonNodeFactory.instance;
        ArrayNode arrayMisFunciones = new ArrayNode(jFactory);

        ArrayNode arrayOficinas = new ArrayNode(JsonNodeFactory.instance);
        for (Oficina oficina : oficinas) {
            ObjectNode node = createOficinaJson(oficina);
            arrayOficinas.add(node);
        }

        ArrayNode arrayTiposDocumentos = new ArrayNode(jFactory);
        for (TipoDocIdentidad tipoDoc : tiposDocumentos) {
            ObjectNode node = JsonHelper.createJson(tipoDoc, JsonNodeFactory.instance, true, new String[]{"*"});
            arrayTiposDocumentos.add(node);
        }

        model.addAttribute("oficina", idOficina);
        model.addAttribute("colaborador", colaboradorJson);
        model.addAttribute("tipoDocumento", arrayTiposDocumentos);
        model.addAttribute("sexo", SexoEnum.values());
        model.addAttribute("areas", arrayOficinas);
        model.addAttribute("cargos", createPerfilesJson(cargos));
        model.addAttribute("funciones", createPerfilesJson(funciones));
        model.addAttribute("misfunciones", arrayMisFunciones);
        model.addAttribute("origen", getOrigen(origen));
        model.addAttribute("idOficina", idOficina);
        return "general/oficina/colaborador/colaboradorForm";
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
                service.saveColaborador(colaborador, colaboradorBean.getOficinaMean(), ds.getUsuario(), ds.getCompania());
                response.setSuccess(true);
            } else {
                Boolean success = service.saveColaboradorExistente(colaborador, colaboradorBean.getOficinaMean(), ds.getUsuario(), ds.getCompania());
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
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {

            Usuario usu = service.verifiEmail(persona);
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

            Persona personaDb = service.verifiDocumento(persona);
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
        Oficina oficina = service.find(new Oficina(idOficina));
        List<PerfilCompania> perfiles = service.allCargosByOficina(oficina);

        model.addAttribute("oficina", oficina);
        model.addAttribute("idOficina", oficina.getId());
        model.addAttribute("cargos", createPerfilesJson(perfiles));
        model.addAttribute("origen", getOrigen(origen));
        model.addAttribute("estadosEmp", JsonHelper.enumToJson(ColaboradorEstadoEnum.values()));
        return "general/oficina/colaborador/colaborador";
    }

    @ResponseBody
    @RequestMapping("{oficina}/resumen")
    public JsonResponse resumen(
            @PathVariable("oficina") Long idOficina, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            Oficina oficina = service.find(new Oficina(idOficina));
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
            service.updateEstado(colaborador, ds);
            response.setMessage("Se cambio de estado al colaborador satisfactoriamente");
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
            ArrayNode colaboradores = service.getColaboradoresJson(filter, new Oficina(idOficina));
            response.setData(colaboradores);
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
            Colaborador colaborador, HttpSession session) {

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

    private ArrayNode createPerfilesJson(List<PerfilCompania> perfiles) {
        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
        for (PerfilCompania perfil : perfiles) {
            ObjectNode node = JsonHelper.createJson(perfil, JsonNodeFactory.instance, true, new String[]{"*"});
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
