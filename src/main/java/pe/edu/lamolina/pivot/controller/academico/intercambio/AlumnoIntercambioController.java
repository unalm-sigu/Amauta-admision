package pe.edu.lamolina.pivot.controller.academico.intercambio;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoIntercambio;
import pe.edu.lamolina.model.academico.BecaEstudio;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.TipoGestionEnum;
import pe.edu.lamolina.model.general.Empresa;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@SessionAttributes("alumnoBecado")
@RequestMapping("academico/becado/alumno")
public class AlumnoIntercambioController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AlumnoIntercambioService service;

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

        return "academico/alumnointercambio/alumnointercambio";

    }

    @RequestMapping("nuevo")
    public String nuevo(Model model, HttpSession session) {

        List<Empresa> instituciones = service.allInstituciones();
        ArrayNode arrayInstitucion = new ArrayNode(JsonNodeFactory.instance);

        for (Empresa institucion : instituciones) {
            ObjectNode json = JsonHelper.createJson(institucion, JsonNodeFactory.instance, new String[]{
                "id", "numeroDocIdentidad", "razonSocial"
            });
            arrayInstitucion.add(json);
        }

        model.addAttribute("instituciones", arrayInstitucion);
        model.addAttribute("alumno", new Alumno());
        model.addAttribute("ciclos", service.allCicloAcademico());
        model.addAttribute("gestiones", TipoGestionEnum.values());

        return "academico/alumnointercambio/alumnointercambioform";
    }

    @RequestMapping("{alumno}/update")
    public String update(@PathVariable("alumno") Long alumno, Model model, HttpSession session) {
        List<Empresa> instituciones = service.allInstituciones();
        ArrayNode arrayInstitucion = new ArrayNode(JsonNodeFactory.instance);

        for (Empresa institucion : instituciones) {
            ObjectNode json = JsonHelper.createJson(institucion, JsonNodeFactory.instance, new String[]{
                "id", "numeroDocIdentidad", "razonSocial"
            });
            arrayInstitucion.add(json);
        }

        model.addAttribute("instituciones", arrayInstitucion);
        model.addAttribute("alumno", new Alumno(alumno));
        model.addAttribute("ciclos", service.allCicloAcademico());
        model.addAttribute("gestiones", TipoGestionEnum.values());

        return "academico/alumnointercambio/alumnointercambioform";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {

        DynatableResponse json = new DynatableResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            CicloAcademico cicloAcademico = ds.getCicloAcademico();
            logger.debug("cicloAcademico {} {}", cicloAcademico.getId(), cicloAcademico.getDescripcion());
            List<AlumnoIntercambio> becados = service.allAlumnoBecado(filter, cicloAcademico);
            JsonNodeFactory jFactory = JsonNodeFactory.instance;
            ArrayNode array = new ArrayNode(jFactory);

            for (AlumnoIntercambio becado : becados) {
                ObjectNode node = new ObjectNode(jFactory);

                Alumno alumno = becado.getAlumno();
                Persona persona = alumno.getPersona();
                Carrera carrera = alumno.getCarrera();
                CicloAcademico ciclo = becado.getCicloIntercambio();

                node.put("id", becado.getId());
                node.put("nombre", persona.getNombreCompleto());
                node.put("codigo", alumno.getCodigo());
                node.put("tipoDoc", (String) ObjectUtil.getParentTree(persona, "tipoDocumento.simbolo"));
                node.put("nroDocumento", persona.getNumeroDocIdentidad());
                node.put("carrera", carrera.getNombre());
                node.put("codigoCarrera", carrera.getCodigo());
                node.put("facultad", carrera.getFacultad().getNombre());
                node.put("codigoFacultad", carrera.getFacultad().getCodigo());
                node.put("monto", becado.getMonto());
                node.put("facultadDestino", becado.getFacultadDestino());
                node.put("universidadDestino", (String) ObjectUtil.getParentTree(becado, "universidadDestino.nombre"));
                node.put("ciclo", ciclo.getDescripcion());
                node.put("estado", becado.getEstado());
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
    @RequestMapping("save")
    public JsonResponse save(AlumnoIntercambio alumnoBecado, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            if (alumnoBecado.getId() == null) {
                Usuario user = ds.getUsuario();
                service.save(alumnoBecado, user);
                response.setMessage("Alumno de Intercambio agregado satisfactoriamente");
            } else {
                service.update(alumnoBecado);
                response.setMessage("Alumno de Intercambio  actualizado satisfactoriamente");
            }
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("delete")
    public JsonResponse delete(AlumnoIntercambio alumnoBecado) {
        JsonResponse response = new JsonResponse();
        try {
            service.delete(alumnoBecado);
            response.setMessage("Alumno de Intercambio eliminado satisfactoriamente");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("update")
    public JsonResponse update(AlumnoIntercambio alumnoBecado, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            JsonNodeFactory jFactory = JsonNodeFactory.instance;
            AlumnoIntercambio becado = service.find(alumnoBecado);
            ObjectNode jBecado = JsonHelper.createJson(becado, jFactory, true, new String[]{
                "*",
                "alumno.id",
                "alumno.persona.*",
                "becaEstudio.*",
                "paisDestino.*",
                "universidadDestino.*",
                "universidadDestino.pais.*",
                "cicloIntercambio.*"
            });
            response.setData(jBecado);
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("searchAlumno")
    public JsonResponse searchAlumno(@RequestParam("nombre") String nombre, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            JsonNodeFactory jFactory = JsonNodeFactory.instance;
            List<Alumno> alumnos = service.allAlumnoByName(nombre);
            ArrayNode jsonList = new ArrayNode(jFactory);
            for (Alumno alumno : alumnos) {
                ObjectNode jAlumno = JsonHelper.createJson(alumno, jFactory, true, new String[]{
                    "*",
                    "persona.*",
                    "persona.tipoDocumento.*",
                    "carrera.*",
                    "carrera.facultad.*"
                });
                jsonList.add(jAlumno);
            }
            response.setData(jsonList);
            response.setTotal(jsonList.size());
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("allBeca")
    public JsonResponse allBeca(@RequestParam("nombre") String nombre, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            JsonNodeFactory jFactory = JsonNodeFactory.instance;
            ArrayNode jsonList = new ArrayNode(jFactory);
            List<BecaEstudio> becas = service.allBeca(nombre);
            for (BecaEstudio beca : becas) {
                ObjectNode jBeca = JsonHelper.createJson(beca, jFactory, true, new String[]{
                    "*",
                    "institucion.*"
                });
                jsonList.add(jBeca);
            }
            response.setData(jsonList);
            response.setTotal(jsonList.size());
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("saveBeca")
    public JsonResponse saveBeca(@RequestBody BecaEstudio becaEstudio, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

            BecaEstudio beca = service.saveBeca(becaEstudio);
            response.setMessage("Beca agregada satisfactoriamente");

            response.setData(beca);
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

}
