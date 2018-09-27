package pe.edu.lamolina.pivot.controller.academico.ciclo;

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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.enums.NumeroCicloAcademicoEnum;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/cicloacademico")
public class CicloAcademicoController {

    @Autowired
    CicloAcademicoService service;

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
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        Compania cia = ds.getCompania();
        List<Integer> margen = service.allYear();
        List<ModalidadEstudio> modalidades = service.allPrePostgrado(cia);
        model.addAttribute("modalidades", modalidades);
        model.addAttribute("margen", margen);
        model.addAttribute("numeros", NumeroCicloAcademicoEnum.values());
        return "academico/cicloacademico/cicloAcademico";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {

        DynatableResponse json = new DynatableResponse();
        try {

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

        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }
        return json;
    }

    @ResponseBody
    @RequestMapping("update")
    public JsonResponse update(CicloAcademico cicloAcademico) {
        JsonResponse response = new JsonResponse();
        try {
            CicloAcademico cicloAcademicoDB = service.findCicloAcademico(cicloAcademico);
            ObjectNode cicloJson = JsonHelper.createJson(cicloAcademicoDB, JsonNodeFactory.instance, true,
                    new String[]{
                        "*", "modalidadEstudio.*"
                    });
            response.setData(cicloJson);
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("save")
    public JsonResponse save(CicloAcademico cicloAcademico, HttpSession session, RedirectAttributes redirectAttr) {

        JsonResponse response = new JsonResponse();
        ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            Usuario usuario = ds.getUsuario();

            if (cicloAcademico.getId() == null) {
                service.save(cicloAcademico, usuario);
                response.setMessage("Ciclo académico creado satisfactoriamente");
            } else {
                service.update(cicloAcademico, usuario);
                response.setMessage("Ciclo académico modificado satisfactoriamente");
            }

            response.setSuccess(true);
            response.setData(node);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
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
    public JsonResponse activar(CicloAcademico cicloAcademico) {
        JsonResponse response = new JsonResponse();
        try {
            service.activar(cicloAcademico);
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

}
