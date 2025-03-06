package pe.edu.lamolina.amauta.controller.academico.ciclo;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import javax.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.amauta.config.DespliegueConfig;
import pe.edu.lamolina.amauta.controller.academico.avancecurricular.AvanceCurricularService;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.enums.NumeroCicloAcademicoEnum;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.amauta.controller.ingresante.muestraslab.MuestrasLabService;
import pe.edu.lamolina.amauta.controller.seguridad.verificarurl.VerificarUrlControServiceImp;
import pe.edu.lamolina.amauta.dao.seguridad.MenuDAO;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Alumno;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.PRE;
import pe.edu.lamolina.model.enums.TipoCicloEnum;

@Slf4j
@Controller
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@RequestMapping("academico/cicloacademico")
public class CicloAcademicoController extends VerificarUrlControServiceImp {

    private final CicloAcademicoService service;
    private final MuestrasLabService muestrasLabService;
    private final AvanceCurricularService avanceCurricularService;

    private final String rutaModulo = this.getClass().getAnnotation(RequestMapping.class).value()[0];
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    DespliegueConfig despliegueConfig;

    @Autowired
    MenuDAO menuDAO;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        Compania cia = ds.getCompania();
        List<ModalidadEstudio> modalidades = service.allPrePostgrado(cia);
        ModalidadEstudio pregrado = modalidades.stream().filter(mod -> mod.getCodigoEnum() == PRE).findFirst().get();

        DateTime today = new DateTime();
        List<MargenYear> margenes = service.allMargenesByYearModalidad(today.getYear(), pregrado);
        ArrayNode margenesJson = JaneHelper.from(margenes).array();

        if (!this.accesoSessionUrl(ds, rutaModulo)) {
            return "redirect:/logout";
        }
        model.addAttribute("modalidades", modalidades);
        model.addAttribute("margenes", margenesJson);
        model.addAttribute("numeros", NumeroCicloAcademicoEnum.values());
        model.addAttribute("rutaModulo", rutaModulo);

        return "academico/cicloacademico/cicloAcademico";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {

        DynatableResponse json = new DynatableResponse();

        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        List<CicloAcademico> ciclos = service.allByDynatable(filter);
        for (CicloAcademico ciclo : ciclos) {
            ObjectNode cicloJson = JsonHelper.createJson(ciclo, JsonNodeFactory.instance, true,
                    new String[]{
                        "*", "modalidadEstudio.*"
                    });
            array.add(cicloJson);
        }
        json.setData(array);
        json.setTotal(filter.getTotal());
        json.setFiltered(filter.getFiltered());

        return json;
    }

    @ResponseBody
    @RequestMapping("allMargenes")
    public JsonResponse allMargenes(@RequestBody CicloAcademico cicloForm) {
        ModalidadEstudio modalidad = service.findModalidadEstudio(cicloForm.getModalidadEstudio());
        List<MargenYear> margenes = service.allMargenesByYearModalidad(cicloForm.getYear(), modalidad);
        ArrayNode margenesJson = JaneHelper.from(margenes).array();

        JsonResponse response = new JsonResponse();
        response.setData(margenesJson);
        response.setSuccess(Boolean.TRUE);

        return response;
    }

    @ResponseBody
    @RequestMapping("update")
    public JsonResponse update(CicloAcademico cicloAcademico) {
        JsonResponse response = new JsonResponse();

        CicloAcademico cicloAcademicoDB = service.findCicloAcademico(cicloAcademico);
        ObjectNode cicloJson = JsonHelper.createJson(cicloAcademicoDB, JsonNodeFactory.instance, true,
                new String[]{
                    "*", "modalidadEstudio.*"
                });
        response.setData(cicloJson);
        response.setSuccess(Boolean.TRUE);

        return response;
    }

    @ResponseBody
    @RequestMapping("save")
    public JsonResponse save(CicloAcademico cicloAcademico, HttpSession session, RedirectAttributes redirectAttr) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        Usuario usuario = ds.getUsuario();

        JsonResponse response = new JsonResponse();
        if (cicloAcademico.getId() == null) {
            service.save(cicloAcademico, ds);
            response.setMessage("Ciclo académico creado satisfactoriamente");
        } else {
            service.update(cicloAcademico, ds);
            response.setMessage("Ciclo académico modificado satisfactoriamente");
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("delete")
    public JsonResponse delete(CicloAcademico cicloAcademico) {
        JsonResponse response = new JsonResponse();
        try {
            service.delete(cicloAcademico);
            response.setMessage("Ciclo académico eliminado satisfactoriamente");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("activar")
    public JsonResponse activar(CicloAcademico cicloAcademico, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.activar(cicloAcademico, ds);
            CicloAcademico caBD = service.findCicloAcademico(cicloAcademico);

            muestrasLabService.inicializarVisor();

            if (caBD.getTipoEnum().equals(TipoCicloEnum.REG)) {
                List<Alumno> alumnos = service.ejecutarTramiteAcademicos(cicloAcademico, ds);
                avanceCurricularService.generarAvanceCurricularByAlumnosPregrados(alumnos, ds, null);
            }

            response.setMessage("Ciclo académico activado satisfactoriamente");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("desactivar")
    public JsonResponse desactivar(CicloAcademico cicloAcademico) {
        JsonResponse response = new JsonResponse();
        try {
            service.desactivar(cicloAcademico);
            response.setMessage("Ciclo académico desactivado satisfactoriamente");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("anular")
    public JsonResponse anular(CicloAcademico cicloAcademico) {
        JsonResponse response = new JsonResponse();
        try {
            service.anular(cicloAcademico);
            response.setMessage("Ciclo académico anulado satisfactoriamente");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("cerrar")
    public JsonResponse cerrar(CicloAcademico cicloAcademico) {
        JsonResponse response = new JsonResponse();
        try {
            service.cerrar(cicloAcademico);
            response.setMessage("Ciclo académico cerrado satisfactoriamente");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("pendiente")
    public JsonResponse pendiente(CicloAcademico cicloAcademico) {
        JsonResponse response = new JsonResponse();
        try {
            service.pendiente(cicloAcademico);
            response.setMessage("Ciclo académico pasado a pendiente satisfactoriamente");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("configurar")
    public JsonResponse configurar(CicloAcademico cicloAcademico) {
        JsonResponse response = new JsonResponse();
        try {
            service.configurar(cicloAcademico);
            response.setMessage("Se ha iniciado la configuración del ciclo académico satisfactoriamente");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("changeVisiblelogin")
    public JsonResponse changeVisiblelogin(@RequestBody CicloAcademico cicloAcademico) {
        JsonResponse response = new JsonResponse();
        response.setSuccess(Boolean.FALSE);
        try {
            service.changeVisiblelogin(cicloAcademico);
            response.setMessage("Se actualizó el ciclo satisfactoriamente.");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

}
