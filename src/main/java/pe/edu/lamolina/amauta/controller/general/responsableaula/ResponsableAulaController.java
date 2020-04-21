package pe.edu.lamolina.amauta.controller.general.responsableaula;

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
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.TipoResponsableEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.ResponsableAula;
import pe.edu.lamolina.model.general.TurnoAtencionAula;
import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("general/responsableaula")
public class ResponsableAulaController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ResponsableAulaService service;

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
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();
        model.addAttribute("ciclo", ciclo);

        ArrayNode jTiposResponsables = this.tiposResponsables();
        model.addAttribute("jTiposResponsables", jTiposResponsables.toString());
        return "general/responsableaula/responsableaula";
    }

    public ArrayNode tiposResponsables() {
        ArrayNode tiposResponsables = new ArrayNode(JsonNodeFactory.instance);

        ObjectNode jTipoResp = new ObjectNode(JsonNodeFactory.instance);
        jTipoResp.put("name", TipoResponsableEnum.RES.name());
        jTipoResp.put("value", TipoResponsableEnum.RES.getValue());
        tiposResponsables.add(jTipoResp);

        jTipoResp = new ObjectNode(JsonNodeFactory.instance);
        jTipoResp.put("name", TipoResponsableEnum.SOP.name());
        jTipoResp.put("value", TipoResponsableEnum.SOP.getValue());
        tiposResponsables.add(jTipoResp);

        jTipoResp = new ObjectNode(JsonNodeFactory.instance);
        jTipoResp.put("name", TipoResponsableEnum.SUP.name());
        jTipoResp.put("value", TipoResponsableEnum.SUP.getValue());
        tiposResponsables.add(jTipoResp);

        return tiposResponsables;
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

            List<ResponsableAula> responsables = service.allResponsablesByRaptor(filter, ds.getCicloAcademico());
            JsonNodeFactory jFactory = JsonNodeFactory.instance;

            ArrayNode array = new ArrayNode(jFactory);

            //    ArrayNode turnosCabecera = new ArrayNode(jFactory);
            for (ResponsableAula responsableAula : responsables) {
                ObjectNode jResponsableAula = this.castPersonaResponsable(responsableAula);
                array.add(jResponsableAula);
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

    public ObjectNode castPersonaResponsable(ResponsableAula responsableAula) {
        JsonNodeFactory jFactory = JsonNodeFactory.instance;
        ObjectNode jResponsableAula = JsonHelper.createJson(responsableAula, jFactory, true, new String[]{
            "*",
            "tipoEnum.*",
            "userRegistro.*",
            "userActualizacion.*",
            "persona.*",
            "persona.apellidosNombres"
        });
        ArrayNode jTurnosAtencionAulas = new ArrayNode(jFactory);
        for (TurnoAtencionAula turnosAtencionAula : responsableAula.getTurnosAtencionAulas()) {
            ObjectNode turno = JsonHelper.createJson(turnosAtencionAula, jFactory, true, new String[]{
                "id",
                "descripcion",
                "horaInicio.*",
                "horaFin.*",
                "aulas.id",
                "aulas.codigo",
                "aulas.nombre",
                "aulas.descripcionConcat",
                "aulas.tipoAula.tipoAulaMOD",
                "aulas.tipoAula.tipoAulaAUL"
            });
            jTurnosAtencionAulas.add(turno);
        }
        jResponsableAula.set("turnosAtencionAulas", jTurnosAtencionAulas);
        return jResponsableAula;
    }

    @ResponseBody
    @RequestMapping("allPersonas")
    public JsonResponse allPersonas(@RequestParam("nombre") String nombre, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {

            JsonNodeFactory jFactory = JsonNodeFactory.instance;

            ArrayNode jsonList = new ArrayNode(jFactory);
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

            CicloAcademico cicloAcademico = ds.getCicloAcademico();
            List<Persona> personas = service.allPersonasByName(nombre);

            for (Persona persona : personas) {
                ObjectNode json = JsonHelper.createJson(persona, jFactory, true,
                        new String[]{
                            "id",
                            "apellidosNombres",
                            "nombres",
                            "paterno",
                            "materno",
                            "numeroDocIdentidad",
                            "telefono",
                            "celular",
                            "emailCompania"
                        });
                jsonList.add(json);
            }

            response.setData(jsonList);
            response.setTotal(jsonList.size());
            response.setSuccess(true);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("allAulas")
    public JsonResponse allAulas(@RequestParam("nombre") String nombre, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {

            JsonNodeFactory jFactory = JsonNodeFactory.instance;

            ArrayNode jsonList = new ArrayNode(jFactory);
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

            List<Aula> aulas = service.allAulasByName(nombre);

            for (Aula aula : aulas) {
                ObjectNode json = JsonHelper.createJson(aula, jFactory, true,
                        new String[]{
                            "id",
                            "codigo",
                            "nombre"
                        });
                jsonList.add(json);
            }

            response.setData(jsonList);
            response.setTotal(jsonList.size());
            response.setSuccess(true);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("changePersonaResponsable")
    public JsonResponse changePersonaResponsable(@RequestBody ResponsableAula responsableAulaForm, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            JsonNodeFactory jFactory = JsonNodeFactory.instance;

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

            List<TurnoAtencionAula> turnosAtencionAulas = service.allTurnoAtenconAula();
            turnosAtencionAulas.forEach(x -> x.setAulas(new ArrayList<>()));

            ResponsableAula responsableAula = service.findResponsableAula(responsableAulaForm, ds);
            responsableAula.setTipo(responsableAulaForm.getTipo());
            responsableAula.setPersona(responsableAulaForm.getPersona());
            ObjectNode jResponsableAula = this.castPersonaResponsable(responsableAula);
            turnosAtencionAulas = responsableAula.getTurnosAtencionAulas();

//            ArrayNode jTurnosAtencionAulas = new ArrayNode(jFactory);
//            for (TurnoAtencionAula turnosAtencionAula : turnosAtencionAulas) {
//                jTurnosAtencionAulas.add(JsonHelper.createJson(turnosAtencionAula, jFactory, false,
//                        new String[]{
//                            "*",
//                            "horaInicio.*",
//                            "horaFin.*",
//                            "aulas.*"
//                        }));
//            }
            response.setData(jResponsableAula);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("saveResponsableAula")
    public JsonResponse saveResponsableAula(@RequestBody ResponsableAula responsableAula, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {

            JsonNodeFactory jFactory = JsonNodeFactory.instance;
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            ds.setFechaAccionAudit(new Date());
            service.saveResponsableAula(responsableAula, ds);
            response.setSuccess(true);
            response.setMessage("Responsable grabado");
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("editarResponsableAula")
    public JsonResponse editarResponsableAula(@RequestBody ResponsableAula responsableAula, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {

            JsonNodeFactory jFactory = JsonNodeFactory.instance;
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            ds.setFechaAccionAudit(new Date());
            responsableAula = service.findResponsableAula(responsableAula, ds);

            ObjectNode jPersona = this.castPersonaResponsable(responsableAula);
            response.setData(jPersona);
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

}
