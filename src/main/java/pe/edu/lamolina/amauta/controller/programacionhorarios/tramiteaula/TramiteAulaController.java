package pe.edu.lamolina.amauta.controller.programacionhorarios.tramiteaula;

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
import org.springframework.web.servlet.ModelAndView;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.*;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.bienestar.DiaHora;
import pe.edu.lamolina.model.tramite.AulaReservada;
import pe.edu.lamolina.model.tramite.ReservaAula;
import pe.edu.lamolina.model.enums.DocenteEstadoEnum;
import pe.edu.lamolina.model.enums.ReservaAulaEstadoEnum;
import pe.edu.lamolina.model.enums.TipoSolicitanteEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.general.Empresa;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.constantines.GlobalMessages;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.general.Pais;

@Controller
@RequestMapping("tramite/aula")
public class TramiteAulaController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TramiteAulaService service;

    @Autowired
    ReporteAulasReservadasExcelView recordFilterTipoReserva;

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
    public String index() {
        return "programacion/tramiteaula/tramiteaula";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {

        DynatableResponse json = new DynatableResponse();
        json.setTotal(0);

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

        return json;
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

        ArrayNode array2 = new ArrayNode(jFactory);
        for (TipoReservaAmbienteEnum tipo : TipoReservaAmbienteEnum.values()) {
            ObjectNode node = new ObjectNode(jFactory);
            node.put("id", tipo.name());
            node.put("nombre", tipo.getValue());
            array2.add(tipo.getValue());
        }

        model.addAttribute("tiposSolicitante", array.toString());
        model.addAttribute("tipoReservaAmbiente", array2.toString());
        return "programacion/tramiteaula/tramiteaulaform";
    }

    @ResponseBody
    @RequestMapping("save")
    public JsonResponse save(@RequestBody ReservaAula reservaAula, HttpSession session) {

        JsonResponse response = new JsonResponse();
//        ObjectUtil.printAttr(reservaAula);

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            boolean esDocente = ds.getDocente() != null;

            if (reservaAula.getId() != null) {
                service.update(reservaAula, ds);
                response.setMessage(GlobalMessages.UPDATED);
            } else {
                service.save(reservaAula, ds);
                response.setMessage(GlobalMessages.CREATED);
            }

            if (esDocente) {
                response.setData("PENDIENTE_REVISION");
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

        ArrayNode array2 = new ArrayNode(jFactory);
        for (TipoReservaAmbienteEnum tipo : TipoReservaAmbienteEnum.values()) {
            array2.add(tipo.getValue());
        }

        model.addAttribute("tiposSolicitante", array.toString());
        model.addAttribute("tipoReservaAmbiente", array2.toString());
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
            "reservados.id",
            "reservados.codigo",
            "reservados.nombre",
            "reservados.capacidadAula",
            "aulaReservada.*",
            "aulaReservada.aula.id",
            "aulaReservada.aula.codigo",
            "aulaReservada.aula.nombre",
            "aulaReservada.aula.capacidadAula",
            "aulaReservada.dia.*",
            "aulaReservada.hora.*",});
        model.addAttribute("reservaAula", reservaAulaNode.toString());
        return "programacion/tramiteaula/tramiteaulaform";
    }

    @ResponseBody
    @RequestMapping("filteraula")
    public DynatableResponse filteraula(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

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
    public JsonResponse saveInstitucion(@RequestBody Empresa insticion, HttpSession session) {


        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            Empresa institucionBD = service.saveInstitucion(insticion,ds);
            response.setData(JaneHelper.from(institucionBD).only("id,razonSocial").json());
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
    public ArrayNode allAlumno(@RequestParam("nombre") String nombre, HttpSession session) {

        List<Alumno> alumnos = service.allAlumnoByName(nombre);

        return JaneHelper.from(alumnos)
                .only("id,codigo")
                .join("persona", "id,nombreCompleto")
                .array();

    }

    @ResponseBody
    @RequestMapping("allDocente")
    public ArrayNode allDocente(@RequestParam("nombre") String nombre, HttpSession session) {

        List<Docente> docentes = service.allDocenteByName(nombre);

        return JaneHelper.from(docentes)
                .only("id,codigo")
                .join("persona", "nombreCompleto,apellidosNombres")
                .join("departamentoAcademico", "codigo,nombre")
                .array();
    }

    @ResponseBody
    @RequestMapping("allOficina")
    public ArrayNode allOficina(@RequestParam("nombre") String nombre, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        List<Oficina> oficinas = service.allOficinaByName(nombre, ds);

        return JaneHelper.from(oficinas)
                .array();
    }

    @ResponseBody
    @RequestMapping("allEmpresa")
    public ArrayNode allEmpresa(@RequestParam("nombre") String nombre) {

        List<Empresa> empresas = service.allEmpresaByName(new Pais(), nombre);

        return JaneHelper.from(empresas)
                .only("id,numeroDocIdentidad,razonSocial")
                .array();

    }

    @ResponseBody
    @RequestMapping("aceptartramite")
    public JsonResponse aceptartramite(@RequestBody ReservaAula reservaAula, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
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

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.rechazarTramite(reservaAula);
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
    @RequestMapping("regularizartramite")
    public JsonResponse regularizartramite(@RequestBody ReservaAula reservaAula, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.regularizarTramite(reservaAula);
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

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

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
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

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
    @RequestMapping("allAulaFiltro")
    public ArrayNode allAulaFiltro(@RequestParam("nombre") String nombre, HttpSession session) {

        List<Aula> aulas = service.allAulaFiltro(nombre);

        return JaneHelper.from(aulas)
                .only("id,codigo,nombre")
                .array();

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

    @RequestMapping("exportExcel")
    public ModelAndView exportExcel(HttpSession session, Model model) {
        try{
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            List<ReservaAulaBean> listReservas = service.filterByTipoReserva();
            model.addAttribute("listReservas", listReservas);
        }catch (PhobosException e){
            e.printStackTrace();
            return new ModelAndView("redirect:/");
        }catch (Exception e){
            e.printStackTrace();
            return new ModelAndView("redirect:/");
        }
        return new ModelAndView(recordFilterTipoReserva);
    }

    @RequestMapping("docente")
    public String docenteView(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        Docente docente = ds.getDocente();

        if (docente == null) {
            throw new PhobosException("Acceso denegado: Usuario no es docente");
        }

        if (!DocenteEstadoEnum.ACT.name().equals(docente.getEstado())) {
            throw new PhobosException("Docente no está activo");
        }

        model.addAttribute("docente", docente);
        return "programacion/tramiteaula/tramiteauladocente";
    }

    @RequestMapping("docente/nuevo")
    public String nuevoDocente(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        Docente docente = ds.getDocente();

        if (docente == null) {
            throw new PhobosException("Acceso denegado: Usuario no es docente");
        }

        JsonNodeFactory jFactory = JsonNodeFactory.instance;
        ObjectNode docenteNode = JsonHelper.createJson(docente, jFactory, true, new String[]{
            "id",
            "codigo",
            "persona.id",
            "persona.nombres",
            "persona.paterno",
            "persona.materno"
        });

        model.addAttribute("docente", docenteNode.toString());
        model.addAttribute("ciclo", ds.getCicloAcademico());
        return "programacion/tramiteaula/tramiteauladocenteform";
    }

    @RequestMapping("docente/{idtramite}/update")
    public String updateDocente(@PathVariable Long idtramite, Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        Docente docente = ds.getDocente();

        if (docente == null) {
            throw new PhobosException("Acceso denegado: Usuario no es docente");
        }

        ReservaAula reservaAula = service.findReservaAula(idtramite);

        if (!reservaAula.getTramite().getDocente().getId().equals(docente.getId())) {
            throw new PhobosException("No tiene permisos para editar esta reserva");
        }

        if (!ReservaAulaEstadoEnum.PEND.name().equals(reservaAula.getEstado())) {
            throw new PhobosException("Solo puede editar reservas en estado pendiente");
        }

        JsonNodeFactory jFactory = JsonNodeFactory.instance;
        ObjectNode docenteNode = JsonHelper.createJson(docente, jFactory, true, new String[]{
            "id",
            "codigo",
            "persona.id",
            "persona.nombres",
            "persona.paterno",
            "persona.materno"
        });

        ObjectNode reservaNode = JsonHelper.createJson(reservaAula, jFactory, true, new String[]{
            "*",
            "tramite.id",
            "tramite.serie",
            "tramite.numero",
            "reservados.id",
            "reservados.codigo",
            "reservados.nombre",
            "reservados.nombrePublico",
            "reservados.capacidadAula",
            "aulaReservada.id",
            "aulaReservada.dia.id",
            "aulaReservada.hora.id"
        });

        model.addAttribute("docente", docenteNode.toString());
        model.addAttribute("reserva", reservaNode.toString());
        model.addAttribute("ciclo", ds.getCicloAcademico());
        return "programacion/tramiteaula/tramiteauladocenteform";
    }

    @ResponseBody
    @RequestMapping("listDocente")
    public DynatableResponse listDocente(DynatableFilter filter, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        Docente docente = ds.getDocente();

        if (docente == null) {
            throw new PhobosException("Usuario no es un docente");
        }

        DynatableResponse json = new DynatableResponse();
        List<ReservaAula> reservaAulas = service.allByDocente(filter, docente);

        JsonNodeFactory jFactory = JsonNodeFactory.instance;
        ArrayNode array = new ArrayNode(jFactory);

        for (ReservaAula aula : reservaAulas) {
            ObjectNode node = JsonHelper.createJson(aula, jFactory, true, new String[]{
                "*",
                "tramite.id",
                "tramite.serie",
                "tramite.numero",
                "tramite.tipoSolicitante",
                "tramite.docente.id",
                "tramite.docente.persona.id",
                "tramite.docente.persona.nombreCompleto",
                "reservados.id",
                "reservados.nombrePublico"
            });
            array.add(node);
        }

        json.setData(array);
        json.setTotal(filter.getTotal());
        json.setFiltered(filter.getFiltered());

        return json;
    }

    @ResponseBody
    @RequestMapping("filtroAulas")
    public DynatableResponse filtroAulas(
            DynatableFilter filter,
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            @RequestParam(required = false) String horarios,
            @RequestParam(required = false, defaultValue = "true") Boolean soloDisponibles,
            @RequestParam(required = false, defaultValue = "false") Boolean soloOera,
            HttpSession session) {

        DynatableResponse json = new DynatableResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

            List<Map<String, Object>> aulasConDisponibilidad = service.filtrarAulasConDisponibilidad(
                filter, fechaInicio, fechaFin, horarios, soloDisponibles, soloOera, ds
            );

            JsonNodeFactory jFactory = JsonNodeFactory.instance;
            ArrayNode array = new ArrayNode(jFactory);

            for (Map<String, Object> aulaMap : aulasConDisponibilidad) {
                Aula aula = (Aula) aulaMap.get("aula");
                Boolean isDisponible = (Boolean) aulaMap.get("isDisponible");

                ObjectNode node = JsonHelper.createJson(aula, jFactory, true, new String[]{
                    "*",
                    "aulaSuperior.*",
                    "tipoAula.*"
                });
                node.put("isDisponible", isDisponible);

                array.add(node);
            }

            json.setData(array);
            json.setTotal(filter.getTotal());
            json.setFiltered(filter.getFiltered());

        } catch (Exception e) {
            logger.error("Error al filtrar aulas", e);
            json.setTotal(0);
        }

        return json;
    }

    @ResponseBody
    @RequestMapping("cursosByDocente")
    public JsonResponse cursosByDocente(HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            Docente docente = ds.getDocente();

            if (docente == null) {
                throw new PhobosException("Acceso denegado: Usuario no es docente");
            }

            List<Curso> cursos = service.allCursosByDocente(docente, ds);
            JsonNodeFactory jFactory = JsonNodeFactory.instance;
            ArrayNode array = new ArrayNode(jFactory);
            for (Curso curso : cursos) {
                ObjectNode node = JsonHelper.createJson(curso, jFactory, true, new String[]{"id", "codigo", "nombre"});
                array.add(node);
            }
            response.setData(array);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("verificarCruceAlumnos")
    public JsonResponse verificarCruceAlumnos(@RequestBody ReservaAula reservaAulaForm, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

            if (reservaAulaForm.getCurso() == null || reservaAulaForm.getCurso().getId() == null) {
                throw new PhobosException("Debe seleccionar un curso");
            }
            if (reservaAulaForm.getDiahora() == null || reservaAulaForm.getDiahora().isEmpty()) {
                throw new PhobosException("Debe seleccionar al menos un horario");
            }

            List<Map<String, Object>> alumnos = service.verificarCruceAlumnos(
                    reservaAulaForm.getCurso().getId(),
                    reservaAulaForm.getDiahora(),
                    ds);

            JsonNodeFactory jFactory = JsonNodeFactory.instance;
            ArrayNode array = new ArrayNode(jFactory);
            for (Map<String, Object> alumnoMap : alumnos) {
                ObjectNode node = new ObjectNode(jFactory);
                node.put("codigo", (String) alumnoMap.get("codigo"));
                node.put("nombre", (String) alumnoMap.get("nombre"));

                ArrayNode crucesArray = new ArrayNode(jFactory);
                List<Map<String, Object>> cruces = (List<Map<String, Object>>) alumnoMap.get("cruces");
                for (Map<String, Object> cruce : cruces) {
                    ObjectNode cruceNode = new ObjectNode(jFactory);
                    cruceNode.put("dia", (String) cruce.get("dia"));
                    cruceNode.put("hora", (String) cruce.get("hora"));
                    crucesArray.add(cruceNode);
                }
                node.set("cruces", crucesArray);
                array.add(node);
            }
            response.setData(array);
            response.setTotal(alumnos.size());
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

}
