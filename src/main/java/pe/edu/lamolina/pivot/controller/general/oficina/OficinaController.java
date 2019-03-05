package pe.edu.lamolina.pivot.controller.general.oficina;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.enums.SexoEnum;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import pe.edu.lamolina.model.general.AusenciaJefe;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.FuncionColaborador;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.PerfilCompania;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.general.TipoOficina;
import pe.edu.lamolina.model.seguridad.Usuario;
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
            List<Colaborador> colaboradoresTodos = service.allColaborador(oficinas);
            Map<Long, List<Colaborador>> colaboradoresMap = TypesUtil.convertListToMapList("oficina.id", colaboradoresTodos);
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (Oficina oficina : oficinas) {

                List<Colaborador> colaboradores = colaboradoresMap.get(oficina.getId());
                if (colaboradores == null) {
                    colaboradores = new ArrayList();
                }

                ObjectNode node = createOficinaJson(oficina);

                node.put("colaboradores", colaboradores.size());
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

    @RequestMapping("{idColaborador}/updateColaborador/{idOficina}")
    public String updateColaborador(@PathVariable("idOficina") Long idOficina, @PathVariable("idColaborador") Long idColaborador, Model model) {

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
            "oficina.id",
            "oficina.codigo",
            "oficina.nombre",
            "cargo.id",
            "cargo.codigo",
            "cargo.nombre",});

        List<TipoDocIdentidad> tipoDoc = service.allDocumentosIdentidad();
        List<Oficina> oficinas = service.allOficinasByOficinaMain(new Oficina(idOficina));

        List<PerfilCompania> cargos = service.allCargoByOficina(new Oficina(idOficina));
        List<PerfilCompania> funciones = service.allFuncionByOficina(new Oficina(idOficina));

        List<PerfilCompania> misfunciones = service.allFuncionByColaborador(colaborador);

        ArrayNode arrayOficinas = new ArrayNode(JsonNodeFactory.instance);
        for (Oficina oficina : oficinas) {
            ObjectNode node = createOficinaJson(oficina);
            arrayOficinas.add(node);
        }

        ArrayNode arrayCargos = new ArrayNode(jFactory);
        for (PerfilCompania cargo : cargos) {
            ObjectNode node = JsonHelper.createJson(cargo, jFactory, true, new String[]{"*"});
            arrayCargos.add(node);
        }

        ArrayNode arrayFunsiones = new ArrayNode(jFactory);
        for (PerfilCompania funcion : funciones) {
            ObjectNode node = JsonHelper.createJson(funcion, jFactory, true, new String[]{"*"});
            arrayFunsiones.add(node);
        }

        ArrayNode arrayMisFunsiones = new ArrayNode(jFactory);
        for (PerfilCompania mifuncion : misfunciones) {
            ObjectNode node = JsonHelper.createJson(mifuncion, jFactory, true, new String[]{"*"});
            arrayMisFunsiones.add(node);
        }

        model.addAttribute("colaborador", jsonColaborador);
        model.addAttribute("oficina", idOficina);
        model.addAttribute("tipoDocumento", new TipoDocIdentidad().toJsonArray(tipoDoc));
        model.addAttribute("sexo", SexoEnum.values());
        model.addAttribute("area", arrayOficinas);

        model.addAttribute("funciones", arrayFunsiones);
        model.addAttribute("cargos", arrayCargos);
        model.addAttribute("misfunciones", arrayMisFunsiones);

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
    public String nuevoColaborador(@PathVariable("idOficina") Long idOficina, Model model) {

        List<TipoDocIdentidad> tipoDoc = service.allDocumentosIdentidad();
        List<Oficina> oficinas = service.allOficinasByOficinaMain(new Oficina(idOficina));
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

        model.addAttribute("oficina", idOficina);
        model.addAttribute("colaborador", colaboradorJson);
        model.addAttribute("tipoDocumento", new TipoDocIdentidad().toJsonArray(tipoDoc));
        model.addAttribute("sexo", SexoEnum.values());
        model.addAttribute("area", arrayOficinas);
        model.addAttribute("cargos", new PerfilCompania().toJsonArray(cargos));
        model.addAttribute("funciones", new PerfilCompania().toJsonArray(funciones));
        model.addAttribute("misfunciones", arrayMisFunciones);
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
    public String colaboradores(@PathVariable("oficina") Long idOficina, Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        List<Oficina> oficinas = service.allOficina(ds.getPersona());
        Colaboradores colaboradors = service.countColaborador(new Oficina(idOficina));
        List<PerfilCompania> perfilCompania = service.allCargosByOficina(new Oficina(idOficina));

        ArrayNode arrayOficinas = new ArrayNode(JsonNodeFactory.instance);
        for (Oficina oficina : oficinas) {
            ObjectNode node = createOficinaJson(oficina);
            arrayOficinas.add(node);
        }

        model.addAttribute("oficinas", arrayOficinas);
        model.addAttribute("oficina", idOficina);
        model.addAttribute("resumen", colaboradors);
        model.addAttribute("cargos", new PerfilCompania().toJsonArray(perfilCompania));
        return "general/oficina/colaborador/colaborador";
    }

    @ResponseBody
    @RequestMapping("updateEstado")
    public JsonResponse updateEstado(@RequestBody Colaborador colaborador, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
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

    @ResponseBody
    @RequestMapping("{idOficina}/listColaboradores")
    public DynatableResponse listColaboradores(@PathVariable("idOficina") Long idOficina, DynatableFilter filter, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        DynatableResponse response = new DynatableResponse();
        try {

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

    @RequestMapping("nuevo")
    public String nuevo(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        Compania compania = ds.getCompania();
        List<TipoOficina> tipoOficina = service.allTipoOficina();
        ArrayNode node = new ArrayNode(JsonNodeFactory.instance);
        for (TipoOficina tipoOficina1 : tipoOficina) {
            node.add(JsonHelper.createJson(tipoOficina1, JsonNodeFactory.instance, new String[]{
                "*"
            }));
        }
        ObjectNode objectNode = JsonHelper.createJson(new Oficina(), JsonNodeFactory.instance, new String[]{
            "*"});
        model.addAttribute("tipos", node.toString());
        model.addAttribute("oficina", objectNode.toString());
        return "general/oficina/oficinaForm";
    }

    @RequestMapping("{oficina}/update")
    public String update(@PathVariable("oficina") Long idOficina, Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        Compania compania = ds.getCompania();
        Oficina oficina = service.find(new Oficina(idOficina));
        service.fillReferencia(oficina);

        List<TipoOficina> tipoOficina = service.allTipoOficina();

        ArrayNode node = new ArrayNode(JsonNodeFactory.instance);
        for (TipoOficina tipoOficina1 : tipoOficina) {
            node.add(JsonHelper.createJson(tipoOficina1, JsonNodeFactory.instance, new String[]{
                "*"
            }));
        }

        ObjectNode objectNode = JsonHelper.createJson(oficina, JsonNodeFactory.instance, new String[]{
            "*", "tipoOficina.*", "cargoJefe.*", "oficinaSuperior.*"});

        model.addAttribute("tipos", node.toString());
        model.addAttribute("oficina", objectNode.toString());
        return "general/oficina/oficinaForm";
    }

//    @RequestMapping("save")
//    public String save(Oficina oficina, HttpSession session, RedirectAttributes redirectAttr) {
//        try {
//            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
//            Compania compania = ds.getCompania();
//            oficina.setCompania(compania);
//
//            if (oficina.getId() != null) {
//                service.update(oficina, ds);
//                Notificaciones.crearMsg("Oficina Actualizado", redirectAttr);
//            } else {
//                service.save(oficina, ds);
//                Notificaciones.crearMsg("Oficina Creada", redirectAttr);
//            }
//        } catch (PhobosException ex) {
//            ExceptionHandler.handleException(ex, redirectAttr);
//        } catch (Exception e) {
//            ExceptionHandler.handleException(e, redirectAttr);
//        }
//        return "redirect:/general/oficina";
//    }
    @ResponseBody
    @RequestMapping("save")
    public JsonResponse save(@RequestBody Oficina oficina, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
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

//    @ResponseBody
//    @RequestMapping("delete")
//    public JsonResponse delete(Oficina oficina) {
//        JsonResponse response = new JsonResponse();
//        try {
//            service.delete(oficina);
//            response.setMessage("Oficina eliminada satisfactoriamente");
//            response.setSuccess(Boolean.TRUE);
//        } catch (PhobosException e) {
//            ExceptionHandler.handlePhobosEx(e, response);
//        } catch (Exception e) {
//            ExceptionHandler.handleException(e, response);
//        }
//        return response;
//    }
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

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            Compania compania = ds.getCompania();
            ArrayNode array = new ArrayNode(jsonFactory);
            TipoOficina oficina = service.findTipoById(tipo);
            if (TipoOficinaEnum.DPTO.name().equalsIgnoreCase(oficina.getCodigo())) {
                List<DepartamentoAcademico> departamentos = service.allDepartamento(compania);
                for (DepartamentoAcademico departamento : departamentos) {
                    ObjectNode a = new ObjectNode(jsonFactory);
                    a.put("id", departamento.getId());
                    a.put("codigo", departamento.getCodigo());
                    a.put("nombre", departamento.getNombre());
                    array.add(a);
                }
            }
            if (TipoOficinaEnum.ESP.name().equalsIgnoreCase(oficina.getCodigo())) {
                List<Carrera> carreras = service.allCarrera(compania);
                for (Carrera carrera : carreras) {
                    ObjectNode a = new ObjectNode(jsonFactory);
                    a.put("id", carrera.getId());
                    a.put("codigo", carrera.getCodigo());
                    a.put("nombre", carrera.getNombre());
                    array.add(a);
                }
            }
            if (TipoOficinaEnum.FAC.name().equalsIgnoreCase(oficina.getCodigo())) {
                List<Facultad> facultades = service.allFacultad(compania);
                for (Facultad facultad : facultades) {
                    ObjectNode a = new ObjectNode(jsonFactory);
                    a.put("id", facultad.getId());
                    a.put("codigo", facultad.getCodigo());
                    a.put("nombre", facultad.getNombre());
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

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {

            List<Persona> personas = service.allPersona(nombre);
            ArrayNode array = new ArrayNode(jsonFactory);

            for (Persona persona : personas) {
                ObjectNode per = new ObjectNode(jsonFactory);
                per.put("id", persona.getId());
                per.put("nombre", persona.getNombreConTitulo());
                per.put("dni", persona.getNumeroDocIdentidad());
                per.put("tipo", (String) ObjectUtil.getParentTree(persona, "tipoDocumento.simbolo"));
                per.put("titulo", persona.getTituloAcademico());
                per.put("hastitulo", Strings.isNullOrEmpty(persona.getTituloAcademico()) ? 0 : 1);
                array.add(per);
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
    public JsonResponse asignarJefe(Oficina oficina, HttpSession session) {
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
    @RequestMapping("asignarEncargado")
    public JsonResponse asignarEncargado(Oficina oficina, HttpSession session) {
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
    public JsonResponse retirarEncargado(AusenciaJefe ausencia, HttpSession session) {
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
                ObjectNode node = JsonHelper.createJson(perfile, jFactory, true,
                        new String[]{
                            "*"
                        });
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
                ObjectNode node = JsonHelper.createJson(perfile, jFactory, true,
                        new String[]{
                            "*"
                        });
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
    @RequestMapping("verfuncionColaborador")
    public JsonResponse verfuncionColaborador(Colaborador colaborador, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            JsonNodeFactory jFactory = JsonNodeFactory.instance;
            List<PerfilCompania> perfilCompanias = service.allFuncionByColaborador(colaborador);
            ArrayNode array = new ArrayNode(jFactory);
            for (PerfilCompania perfile : perfilCompanias) {
                ObjectNode node = JsonHelper.createJson(perfile, jFactory, true,
                        new String[]{
                            "*"
                        });
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

}
