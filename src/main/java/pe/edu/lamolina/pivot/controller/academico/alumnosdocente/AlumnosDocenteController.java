package pe.edu.lamolina.pivot.controller.academico.alumnosdocente;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;
import pe.edu.lamolina.pivot.zelper.pdf.PdfService;

@Controller
@RequestMapping("academico/docente/alumnosDocente")
public class AlumnosDocenteController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AlumnosDocenteService service;

    @Autowired
    PdfService pdfService;

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
        return "redirect:/academico/docente/cargaacademica";
    }

    @ResponseBody
    @RequestMapping("{seccion}/list")
    public JsonResponse list(@PathVariable("seccion") Long idSeccion, HttpSession session) {

        JsonResponse json = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            CicloAcademico ciclo = ds.getCicloAcademico();

            Seccion seccion = service.findSeccion(idSeccion);
            //FotoHelper helper = new FotoHelper();

            List<MatriculaSeccion> matriculados = service.allMatriculadosBySeccion(seccion, ciclo);
            List<AlumnoConsejero> aconsejados = service.allAconsejadosByMatriculados(matriculados, ciclo);
            Map<Long, AlumnoConsejero> mapAconsejado = TypesUtil.convertListToMap("alumno.id", aconsejados);
            List<Oficina> consejerias = service.allConsejerias();
            Map<Long, Oficina> mapConsejeria = TypesUtil.convertListToMap("instanciaOficina", consejerias);

            for (MatriculaSeccion matSecc : matriculados) {
                ObjectNode node = JsonHelper.createJson(matSecc, JsonNodeFactory.instance, new String[]{
                    "id",
                    "matriculaResumen.alumno.codigo",
                    "matriculaResumen.alumno.modalidadEstudio.codigo",
                    "matriculaResumen.alumno.carrera.nombre",
                    "matriculaResumen.alumno.carrera.tipo",
                    "matriculaResumen.alumno.carrera.tipoEnum",
                    "matriculaResumen.alumno.carrera.facultad.nombre",
                    "matriculaResumen.alumno.persona.tipoFoto",
                    "matriculaResumen.alumno.persona.rutaFoto",
                    "matriculaResumen.alumno.persona.apellidosNombres",
                    "matriculaResumen.alumno.persona.numeroDocIdentidad",
                    "matriculaResumen.alumno.persona.emailCompania",
                    "matriculaResumen.alumno.persona.tipoDocumento.simbolo"
                });

                Alumno alumno = matSecc.getMatriculaResumen().getAlumno();
                AlumnoConsejero aconsejado = mapAconsejado.get(alumno.getId());
                node.set("aconsejado", createAconsejadoJson(aconsejado));

                Oficina consejeria = mapConsejeria.get(alumno.getCarrera().getId());
                node.set("consejeria", createConsejeriaJson(consejeria));

                array.add(node);
            }

            json.setData(array);
            json.setSuccess(Boolean.TRUE);

        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }
        return json;
    }

    @RequestMapping("{seccion}/alumnosDocente")
    public String alumnos(@PathVariable("seccion") Long idSeccion, Model model, HttpSession session) {
        logger.debug("la seccion es {}", idSeccion);

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        Seccion seccion = service.findSeccion(idSeccion);

        logger.debug("El docente es {}", ds.getDocente().getId());
        logger.debug("Consultara notas por seccion");

        model.addAttribute("seccion", createSeccionJson(seccion));
        return "academico/docente/alumnos/alumnosDocente";
    }

    private ObjectNode createConsejeriaJson(Oficina consejeria) {
        ObjectNode node = JsonHelper.createJson(consejeria, JsonNodeFactory.instance, new String[]{
            "id",
            "personaJefe.apellidosNombres",
            "personaJefe.emailCompania"
        });
        return node;
    }

    private ObjectNode createAconsejadoJson(AlumnoConsejero aconsejado) {
        ObjectNode node = JsonHelper.createJson(aconsejado, JsonNodeFactory.instance, new String[]{
            "id",
            "consejero.colaborador.persona.apellidosNombres",
            "consejero.colaborador.persona.emailCompania"
        });
        return node;
    }

    private ObjectNode createSeccionJson(Seccion seccion) {
        ObjectNode node = JsonHelper.createJson(seccion, JsonNodeFactory.instance, new String[]{
            "id", "codigo2", "tipoSeccionEnum",
            "grupoHoras.codigo",
            "aula.codigo",
            "aula.nombre",
            "grupoSeccion.curso.tpc",
            "grupoSeccion.curso.codigo",
            "grupoSeccion.curso.nombre"
        });
        return node;
    }

}
