package pe.edu.lamolina.amauta.controller.academico.alumnosdocente;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.amauta.zelper.pdf.PdfService;

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
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            CicloAcademico ciclo = ds.getCicloAcademico();

            Seccion seccion = service.findSeccion(idSeccion);

            List<MatriculaSeccion> matriculados = service.allMatriculadosBySeccion(seccion, ciclo);
            List<AlumnoConsejero> aconsejados = service.allAconsejadosByMatriculados(matriculados, ciclo);
            Map<Long, AlumnoConsejero> mapAconsejado = TypesUtil.convertListToMap("alumno.id", aconsejados);
            List<Oficina> consejerias = service.allConsejerias();
            Map<Long, Oficina> mapConsejeria = TypesUtil.convertListToMap("instanciaOficina", consejerias);

            for (MatriculaSeccion matSecc : matriculados) {
                ObjectNode node = JaneHelper.createJson()
                        .from(matSecc, "id")
                        .putJoin("alumno", "matriculaResumen.alumno", "codigo")
                        .putJoin("modalidad", "matriculaResumen.alumno.modalidadEstudio", "codigo")
                        .putJoin("carrera", "matriculaResumen.alumno.carrera", "nombre,tipo,tipoEnum")
                        .putJoin("facultad", "matriculaResumen.alumno.carrera.facultad", "nombre")
                        .putJoin("persona", "matriculaResumen.alumno.persona",
                                "tipoFoto,rutaFoto,apellidosNombres,numeroDocIdentidad,emailCompania,tipoDocumento.simbolo")
                        .getNode();

                Alumno alumno = matSecc.getMatriculaResumen().getAlumno();
                AlumnoConsejero aconsejado = mapAconsejado.get(alumno.getId());
                node.set("consejero", createConsejeroJson(aconsejado));

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
    public String alumnos(
            @RequestParam(value = "origen", required = false) String origen,
            @PathVariable("seccion") Long idSeccion, Model model, HttpSession session) {
        logger.debug("la seccion es {}", idSeccion);

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        Seccion seccion = service.findSeccion(idSeccion);

        logger.debug("El docente es {}", ds.getDocente().getId());
        logger.debug("Consultara notas por seccion");

        model.addAttribute("seccion", createSeccionJson(seccion));
        model.addAttribute("origen", getOrigen(origen));

        return "academico/docente/alumnos/alumnosDocente";
    }

    private String getOrigen(String origen) {
        if (StringUtils.isEmpty(origen)) {
            return "/academico/alumno";
        }
        byte[] decoded = Base64.getMimeDecoder().decode(origen);
        String output = new String(decoded);
        return output;
    }

    private ObjectNode createConsejeriaJson(Oficina consejeria) {
        ObjectNode node = JaneHelper.createJson()
                .from(consejeria, "id")
                .putJoin("persona", "personaJefe", "apellidosNombres,emailCompania")
                .getNode();

        return node;
    }

    private ObjectNode createConsejeroJson(AlumnoConsejero aconsejado) {
        ObjectNode node = JaneHelper.createJson()
                .from(aconsejado, "id")
                .putJoin("persona", "consejero.colaborador.persona", "apellidosNombres,emailCompania")
                .getNode();
        return node;
    }

    private ObjectNode createSeccionJson(Seccion seccion) {
        ObjectNode node = JaneHelper.createJson()
                .from(seccion, "id,codigo2,tipoSeccionEnum")
                .join("grupoHoras", "codigo")
                .join("aula", "codigo,nombre")
                .putJoin("curso", "grupoSeccion.curso", "tpc,codigo,nombre")
                .getNode();

        return node;
    }

}
