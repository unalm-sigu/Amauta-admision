package pe.edu.lamolina.pivot.controller.programacionhorarios.tramiteaula;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.almacen.ResumenInventario;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Empresa;
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
        model.addAttribute("tiposSolicitante", TipoSolicitanteEnum.values());
        return "programacion/tramiteaula/tramiteaulaform";
    }

    @ResponseBody
    @RequestMapping("filteraula")
    public DynatableResponse filteraula(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            List<Aula> aulas = service.allByDynatableFilterAula(filter);
            JsonNodeFactory jFactory = JsonNodeFactory.instance;

            ArrayNode array = new ArrayNode(jFactory);

            for (Aula aula : aulas) {
                ObjectNode node = new ObjectNode(jFactory);

                node.put("id", aula.getId());
                node.put("codigo", aula.getCodigo());
                node.put("nombre", aula.getNombre());
                node.put("tipoAmbienteEnum", aula.getTipoAmbienteEnum().getValue());
                node.put("tipoAmbiente", aula.getTipoAmbiente());
                node.put("piso", aula.getPiso());
                node.put("pisos", aula.getPisos());
                node.put("aforo", aula.getAforo());
                node.put("pabellon", (String) ObjectUtil.getParentTree(aula, "aulaSuperior.nombre"));
                node.put("capacidad", aula.getCapacidadAula());
                node.put("sede", aula.getSede() != null ? aula.getSede().getNombre() : "");
                node.put("tipoAula", aula.getTipoAula() != null ? aula.getTipoAula().getNombre() : "");
                node.put("gestor", aula.getOficinaSupervisora() != null ? aula.getOficinaSupervisora().getNombre() : "");
                node.put("estado", aula.getEstado());
                node.put("estadoEnum", aula.getEstadoEnum().getValue());
                node.put("motivo", aula.getMotivoAnulacion());
                node.put("aulasContenido", aula.getAulasContenido().size());

                ArrayNode arrayHijas = new ArrayNode(jFactory);
                List<Aula> aulasHijas = aula.getAulasContenido();
                for (Aula aulaHija : aulasHijas) {
                    ObjectNode nodeHija = new ObjectNode(jFactory);
                    nodeHija.put("codigo", aulaHija.getCodigo());
                    nodeHija.put("nombre", aulaHija.getNombre());
                    arrayHijas.add(nodeHija);
                }
                node.set("aulasHijas", arrayHijas);

                ArrayNode inventariosHijas = new ArrayNode(jFactory);
                List<ResumenInventario> inventarios = aula.getInventario();
                logger.debug("aula {} items {}", aula.getId(), inventarios != null ? inventarios.size() : 0);
                if (inventarios != null) {
                    for (ResumenInventario inventario : inventarios) {
                        ObjectNode jinventario = JsonHelper.createJson(inventario, jFactory, new String[]{"*", "producto.*"});
                        inventariosHijas.add(jinventario);
                    }
                }
                node.set("inventarios", inventariosHijas);
                node.put("cantidadinventarios", inventariosHijas.size());

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
                        "*"
                    });
            response.setData(node);
            response.setSuccess(true);
            response.setMessage(Messages.CREATED);

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
                            "persona.nombreCompleto",
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

}
