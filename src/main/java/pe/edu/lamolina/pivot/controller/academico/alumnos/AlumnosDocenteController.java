package pe.edu.lamolina.pivot.controller.academico.alumnos;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.albatross.zelpers.dynatable.DynatableResponse;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.pivot.controller.general.foto.FotoHelper;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;
import pe.edu.lamolina.pivot.zelper.pdf.PdfService;

@Controller
@RequestMapping("academico/docente/alumnosDocente")
public class AlumnosDocenteController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AlumnosDocenteService alumnosDocenteService;

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
    public DynatableResponse list(@PathVariable("seccion") Long idSeccion, DynatableFilter filter, HttpSession session) {

        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        try {

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            CicloAcademico ciclo = ds.getCicloAcademico();

            Seccion seccion = alumnosDocenteService.findSeccion(idSeccion);
            FotoHelper helper = new FotoHelper();

            List<MatriculaSeccion> matriculasSeccionByFilter = alumnosDocenteService.allMatriculaSeccionBySeccion(seccion);
            for (MatriculaSeccion matSecc : matriculasSeccionByFilter) {
                Alumno alumno = matSecc.getMatriculaResumen().getAlumno();
                Persona persona = alumno.getPersona();
                Carrera carrera = alumno.getCarrera();
                Facultad facultad = carrera.getFacultad();

                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                node.put("id", matSecc.getId());
                node.put("nombre", persona.getApellidosNombres());
                node.put("codigo", alumno.getCodigo());
                node.put("rutaFoto", helper.getRutaFoto(persona.getFoto(), persona.getSexo()));
                node.put("simbolo", persona.getTipoDocumento().getSimbolo());
                node.put("documento", persona.getNumeroDocIdentidad());
                node.put("carrera", carrera.getNombre());
                node.put("facultad", facultad.getNombre());

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

    @RequestMapping("{seccion}/alumnosDocente")
    public String alumnos(@PathVariable("seccion") Long idSeccion, Model model, HttpSession session) {
        logger.debug("la seccion es {}", idSeccion);

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        Seccion seccion = alumnosDocenteService.findSeccion(idSeccion);
        GrupoSeccion grupoSeccion = seccion.getGrupoSeccion();
        Curso curso = grupoSeccion.getCurso();

        logger.debug("El docente es {}", ds.getDocente().getId());
        logger.debug("Consultara notas por seccion");

        //     model.addAttribute("docenteSeccion", docenteSeccion);
        model.addAttribute("seccion", seccion);
        model.addAttribute("grupoSeccion", grupoSeccion);
        model.addAttribute("curso", curso);
        //model.addAttribute("matriculasSeccion", matriculasSeccionByFilter);
        return "academico/docente/alumnos/alumnosDocente";
    }

}
