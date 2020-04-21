package pe.edu.lamolina.pivot.controller.programacionhorarios.tramiteaula;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.bienestar.AulaReservada;
import pe.edu.lamolina.model.bienestar.ReservaAula;
import pe.edu.lamolina.model.enums.TipoSolicitanteEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.general.Empresa;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.constant.Messages;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("tramite/aula")
public class TramiteAulaController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TramiteAulaService service;

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
        return "programacion/tramiteaula/tramiteaula";
    }

    @RequestMapping("nuevo")
    public String nuevo(Model model, HttpSession session) {
        JsonNodeFactory jFactory = JsonNodeFactory.instance;
        ArrayNode array = new ArrayNode(jFactory);
        for (TipoSolicitanteEnum aula : TipoSolicitanteEnum.values()) {
            if (!TipoSolicitanteEnum.PER.name().equals(aula.name())) {
                ObjectNode node = new ObjectNode(jFactory);
                node.put("id", aula.name());
                node.put("nombre", aula.getValue());
                array.add(node);
            }
        }
        model.addAttribute("tiposSolicitante", array.toString());
        return "programacion/tramiteaula/tramiteaulaform";
    }

    @ResponseBody
    @RequestMapping("save")
    public JsonResponse save(@RequestBody ReservaAula reservaAula, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            if (reservaAula.getId() != null) {
                service.update(reservaAula, ds);
                response.setMessage(GlobalMessages.UPDATED);
            } else {
                service.save(reservaAula, ds);
                response.setMessage(GlobalMessages.CREATED);
            }

            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @RequestMapping("{idtramite}/update")
    public String update(@PathVariable Long idtramite, Model model, HttpSession session) {

        JsonNodeFactory jFactory = JsonNodeFactory.instance;
        ArrayNode array = new ArrayNode(jFactory);
        for (TipoSolicitanteEnum aula : TipoSolicitanteEnum.values()) {
            if (!TipoSolicitanteEnum.PER.name().equals(aula.name())) {
                ObjectNode node = new ObjectNode(jFactory);
                node.put("id", aula.name());
                node.put("nombre", aula.getValue());
                array.add(node);
            }
        }
        model.addAttribute("tiposSolicitante", array.toString());
        ReservaAula reservaAula = service.findReservaAula(idtramite);
        ObjectNode reservaAulaNode = JsonHelper.createJson(reservaAula, jFactory, true, new String[]{
            "*",
            "tramite.id",
            "tramite.tipoSolicitante",
            "tramite.oficina.*",
            "tramite.alumno.id",
            "tramite.docente.id",
            "tramite.empresa.id",
            "tramite.empresa.razonSocial",
            "tramite.alumno.persona.id",
            "tramite.alumno.persona.nombreCompleto",
            "tramite.docente.persona.nombreCompleto",
            "tramite.tipoSolicitanteEnum.*",
            "reservados.*",});
        model.addAttribute("reservaAula", reservaAulaNode.toString());
        return "programacion/tramiteaula/tramiteaulaform";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {

        DynatableResponse json = new DynatableResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            List<ReservaAula> reservaAulas = service.allDynatableFilter(filter);
            JsonNodeFactory jFactory = JsonNodeFactory.instance;
            ArrayNode array = new ArrayNode(jFactory);
            for (ReservaAula aula : reservaAulas) {
                ObjectNode node = JsonHelper.createJson(aula, jFactory, true, new String[]{
                    "*",
                    "visibleHorario",
                    "tramite.*",
                    "tramite.alumno.*",
                    "tramite.docente.*",
                    "tramite.empresa.*",
                    "tramite.oficina.*",
                    "tramite.alumno.persona.*",
                    "tramite.docente.persona.*",
                    "reservados.id",
                    "reservados.nombrePublico"
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
    @RequestMapping("filteraula")
    public DynatableResponse filteraula(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            List<Aula> aulas = service.allByDynatableFilterAula(filter, ds);
            JsonNodeFactory jFactory = JsonNodeFactory.instance;

            ArrayNode array = new ArrayNode(jFactory);

            for (Aula aula : aulas) {

                ObjectNode node = JsonHelper.createJson(aula, jFactory, true, new String[]{
                    "*",
                    "aulaSuperior.*",
                    "tipoAula.*",});

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
    @RequestMapping("saveInstitucion")
    public JsonResponse saveInstitucion(Empresa insticion, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            Empresa institucionBD = service.saveInstitucion(insticion);
            ObjectNode node = JsonHelper.createJson(institucionBD, JsonNodeFactory.instance, true,
                    new String[]{
                        "id",
                        "razonSocial"
                    });
            response.setData(node);
            response.setSuccess(true);
            response.setMessage(GlobalMessages.CREATED);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("allAlumno")
    public JsonResponse allAlumno(@RequestParam("nombre") String nombre, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
            ArrayNode jsonList = new ArrayNode(jsonFactory);
            List<Alumno> alumnos = service.allAlumnoByName(nombre);

            for (Alumno alumno : alumnos) {
                ObjectNode json = JsonHelper.createJson(alumno, JsonNodeFactory.instance, true,
                        new String[]{
                            "id", "codigo",
                            "persona.nombreCompleto",});
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
    @RequestMapping("allDocente")
    public JsonResponse allDocente(@RequestParam("nombre") String nombre, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            JsonNodeFactory factory = JsonNodeFactory.instance;
            List<Docente> docentes = service.allDocenteByName(nombre);
            ArrayNode jsonList = new ArrayNode(factory);

            for (Docente profe : docentes) {
                ObjectNode json = JsonHelper.createJson(profe, factory, true, new String[]{
                    "id", "codigo",
                    "persona.nombreCompleto",
                    "persona.apellidosNombres",
                    "departamentoAcademico.codigo",
                    "departamentoAcademico.nombre"
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
    @RequestMapping("aceptartramite")
    public JsonResponse aceptartramite(@RequestBody ReservaAula reservaAula, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.aceptartramite(reservaAula);
            response.setMessage(GlobalMessages.UPDATED);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("rechazartramite")
    public JsonResponse rechazartramite(@RequestBody ReservaAula reservaAula, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.rechazartramite(reservaAula);
            response.setMessage(GlobalMessages.UPDATED);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("allAulaModulo")
    public JsonResponse allAulaModulo(@RequestParam("nombre") String nombre, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
            ArrayNode jsonList = new ArrayNode(jsonFactory);
            List<Aula> aulas = service.allAulaModuloByName(nombre, ds);

            for (Aula aula : aulas) {
                ObjectNode json = JsonHelper.createJson(aula, JsonNodeFactory.instance, true, new String[]{"*"});
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
    @RequestMapping("allAulaModulos")
    public JsonResponse allAulaModulos(HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
            ArrayNode jsonList = new ArrayNode(jsonFactory);
            List<Aula> aulas = service.allAulaModuloByOficina(ds.getOficinaMain());

            for (Aula aula : aulas) {
                ObjectNode json = JsonHelper.createJson(aula, JsonNodeFactory.instance, true, new String[]{"*"});
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
    @RequestMapping("allOficina")
    public JsonResponse allOficina(@RequestParam("nombre") String nombre, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
            ArrayNode jsonList = new ArrayNode(jsonFactory);
            List<Oficina> oficinas = service.allOficinaByName(nombre, ds);

            for (Oficina oficina : oficinas) {
                ObjectNode json = JsonHelper.createJson(oficina, JsonNodeFactory.instance, true, new String[]{"*"});
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
    @RequestMapping("loadHorario")
    public JsonResponse loadHorario(HttpSession session, Model model) {
        JsonResponse response = new JsonResponse();
        try {

            JsonNodeFactory factory = JsonNodeFactory.instance;

            List<Dia> dias = service.allDia();

            ObjectNode data = new ObjectNode(factory);

            ArrayNode diasJson = new ArrayNode(factory);
            for (Dia dia : dias) {
                ObjectNode json = JsonHelper.createJson(dia, factory, true, new String[]{"*"});
                diasJson.add(json);
            }

            List<Hora> horas = service.allHora();

            for (Hora hora : horas) {
                hora.setDias(dias);
            }

            ArrayNode horasJson = new ArrayNode(factory);
            for (Hora hora : horas) {
                ObjectNode json = JsonHelper.createJson(hora, factory, true, new String[]{"*", "dias.*"});
                horasJson.add(json);
            }

            data.set("dias", diasJson);

            data.set("horas", horasJson);

            response.setData(data);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("resumen")
    public JsonResponse resumen(ReservaAula reservaAulaForm, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {

            JsonNodeFactory jFactory = JsonNodeFactory.instance;
            ObjectNode data = new ObjectNode(jFactory);
            ReservaAula reservaAula = service.findReservaAula(reservaAulaForm.getId());
            ObjectNode reservaAulaNode = JsonHelper.createJson(reservaAula, jFactory, true, new String[]{
                "*",
                "tramite.id",
                "tramite.tipoSolicitante",
                "tramite.alumno.id",
                "tramite.docente.id",
                "tramite.empresa.id",
                "tramite.empresa.razonSocial",
                "tramite.alumno.persona.id",
                "tramite.alumno.persona.nombreCompleto",
                "tramite.docente.persona.nombreCompleto",
                "reservados.*"});

            List<Dia> dias = service.allDia();
            ArrayNode diasJson = new ArrayNode(jFactory);
            for (Dia dia : dias) {
                ObjectNode json = JsonHelper.createJson(dia, jFactory, true, new String[]{"*"});
                diasJson.add(json);
            }

            List<AulaReservada> aulaReservadas = service.allAulaReservada(reservaAula);

            Map<Long, Hora> horass = aulaReservadas.stream().collect(Collectors.toMap(x -> x.getHora().getId(), x -> x.getHora(), (f, s) -> s));

            Map<String, AulaReservada> diasHoras = aulaReservadas.stream().collect(Collectors.toMap(x -> x.getHora().getId() + "-" + x.getDia().getId(), x -> x, (f, s) -> s));

            List<Hora> horas = horass.values().stream().collect(Collectors.toList());

            Collections.sort(horas, (p1, p2) -> p1.getNumero().compareTo(p2.getNumero()));

            for (Hora hora : horas) {
                for (Dia dia : dias) {
                    dia.setSelecionado(null);
                    String key = hora.getId() + "-" + dia.getId();
                    AulaReservada reserva = diasHoras.get(key);
                    if (reserva != null) {
                        dia.setSelecionado("1");
                    }
                }
                hora.setDias(dias);
            }

            ArrayNode horasJson = new ArrayNode(jFactory);

            for (Hora hora : horas) {
                ObjectNode json = JsonHelper.createJson(hora, jFactory, true, new String[]{"*", "dias.*"});
                horasJson.add(json);
            }

            data.set("dias", diasJson);
            data.set("horas", horasJson);
            data.set("reservaAula", reservaAulaNode);

            response.setData(data);
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("cambiarVisible")
    public JsonResponse cambiarVisible(@RequestBody ReservaAula reservaAulaForm, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {

            JsonNodeFactory jFactory = JsonNodeFactory.instance;
            ObjectNode data = new ObjectNode(jFactory);
            service.cambiarVisibilidadReserva(reservaAulaForm);
            response.setMessage("Visibilidad cambiada correctamente.");
            response.setData(data);
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

}
