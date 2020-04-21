package pe.edu.lamolina.pivot.controller.programacionhorarios.loadprogramacion;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.pivot.controller.academico.plancalificacurso.PlanCalificaCursoService;
import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/loadprogramacion")
public class LoadProgramacionController {

    @Autowired
    LoadProgramacionService service;
    @Autowired
    EvaluacionExpandidaService evaluacionExpandidaService;
    @Autowired
    PlanCalificaCursoService planCalificaCursoService;
    @Autowired
    VisorLoadProgramacion visor;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
        model.addAttribute("ciclo", ds.getCicloAcademico());
        model.addAttribute("visor", visor);
        return "academico/loadprogramacion/loadProgramacion";
    }

    @RequestMapping(value = "alumnos", method = RequestMethod.GET)
    public String indexAlumno(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        model.addAttribute("ciclo", ds.getCicloAcademico());
        model.addAttribute("visor", visor);
        return "academico/loadprogramacion/loadProgramacionAlumnos";
    }

    @RequestMapping("colgados")
    public String colgados(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        model.addAttribute("ciclo", ds.getCicloAcademico());
        model.addAttribute("alumnos", visor.getAlumnos());
        return "academico/loadprogramacion/colgados";
    }

    @ResponseBody
    @RequestMapping("uploadFiles")
    public JsonResponse uploadFiles(@RequestParam("file") MultipartFile[] files, Model model, HttpSession session) {
        JsonResponse json = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            Map<String, String> rutas = service.loadArchivosHorario(files);
            service.inicioProcesarArchivos(rutas, ds.getCicloAcademico(), ds);

            json.setSuccess(true);
            json.setMessage("Archivos enviados satisfactoriamente");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, json);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, json);

        } finally {
            return json;
        }
    }

    @ResponseBody
    @RequestMapping("uploadFileAlumnos")
    public JsonResponse uploadFileAlumnos(@RequestParam("file") MultipartFile[] file, Model model, HttpSession session) {
        JsonResponse json = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            Map<String, String> rutas = service.loadArchivosAlumnos(file);
            service.inicioProcesarArchivoAlumno(rutas, ds.getCicloAcademico(), ds);

            json.setSuccess(true);
            json.setMessage("Archivos enviados satisfactoriamente");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, json);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, json);

        } finally {
            return json;
        }
    }

    @ResponseBody
    @RequestMapping("analizarLogCarga")
    public JsonResponse analizarLogCarga(Model model, HttpSession session) {
        JsonResponse json = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            evaluacionExpandidaService.analizarLogCarga();

            json.setSuccess(true);
            json.setMessage("Analisis Satisfactorio");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, json);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, json);

        } finally {
            return json;
        }

    }

    @ResponseBody
    @RequestMapping("recalcularNivel")
    public JsonResponse recalcularNivel(Model model, HttpSession session) {
        JsonResponse json = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            evaluacionExpandidaService.recalcularNivel(ds.getCicloAcademico(), ds);

            json.setSuccess(true);
            json.setMessage("carga satisfactoria");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, json);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, json);

        } finally {
            return json;
        }

    }

    @ResponseBody
    @RequestMapping("reasignarPlan")
    public JsonResponse reasignarPlan(Model model, HttpSession session) {
        JsonResponse json = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            planCalificaCursoService.reasignarPlanDocenteCurso(ds.getCicloAcademico(), ds);

            json.setSuccess(true);
            json.setMessage("carga satisfactoria");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, json);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, json);

        } finally {
            return json;
        }

    }

    @ResponseBody
    @RequestMapping("logVisor")
    public JsonResponse logVisor(Model model, HttpSession session) {
        JsonResponse json = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            List<String> logs = visor.reporte();
            for (String log : logs) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                String[] data = log.split("::::");

                if (data.length > 1) {
                    node.put("tipo", data[0]);
                    node.put("info", data[1]);
                } else {
                    node.put("tipo", "info");
                    node.put("info", log);
                }
                array.add(node);

            }

            json.setData(array);
            json.setSuccess(true);
            json.setMessage("carga satisfactoria");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, json);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, json);

        } finally {
            return json;
        }

    }

    @ResponseBody
    @RequestMapping("detenerCarga")
    public JsonResponse detenerCarga(Model model, HttpSession session) {
        JsonResponse json = new JsonResponse();

        try {
            visor.setStop(true);
            json.setSuccess(true);
            json.setMessage("Orden de interrupción fue ejecutada");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, json);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, json);

        } finally {
            return json;
        }

    }

}
