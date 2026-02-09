package pe.edu.lamolina.amauta.controller.nivelacioneegg.alumnado;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.json.JaneHelper;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.alumnado.reporte.ExcelMatriculadosNivelacion;
import pe.edu.lamolina.amauta.controller.seguridad.verificador.VerificadorService;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.nivelacioneegg.CursoNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.NotaAlumnoNivelacion;

@Slf4j
@Controller
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@RequestMapping("nivelacioneegg/carganivelacion")
public class AlumnadoController {

    public final String rutaModulo = this.getClass().getAnnotation(RequestMapping.class).value()[0];

    private final AlumnadoService service;
    private final VerificadorService verificadorService;
    private final ExcelMatriculadosNivelacion excelMatriculadosNivelacion;

    @RequestMapping("{seccion}/alumnado")
    public String alumnado(
            @PathVariable("seccion") Long idSeccion,
            @RequestParam("origen") String origen,
            Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();
        Docente docente = ds.getDocente();

        CursoNivelacion seccion = service.findSeccion(new CursoNivelacion(idSeccion), docente, ciclo);

        model.addAttribute("seccionJson", this.createSeccionJson(seccion));
        model.addAttribute("cicloJson", this.createCicloJson(ciclo));
        model.addAttribute("rutaModulo", rutaModulo);
        model.addAttribute("origen", verificadorService.getOrigen(origen, "/nivelacioneegg/carganivelacion"));

        return "nivelacioneegg/alumnado/alumnado";
    }

    @ResponseBody
    @RequestMapping("{seccion}/listMatriculados")
    public DynatableResponse listMatriculados(
            @PathVariable("seccion") Long idSeccion,
            DynatableFilter filter, HttpSession session, HttpServletRequest request) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();
        Docente docente = ds.getDocente();

        CursoNivelacion seccion = service.findSeccion(new CursoNivelacion(idSeccion), docente, ciclo);
        List<NotaAlumnoNivelacion> alumnos = service.allMatriculadosDynatable(filter, seccion);
        ArrayNode array = this.createAlumnosJson(alumnos);

        DynatableResponse json = new DynatableResponse();
        json.setData(array);
        json.setTotal(filter.getTotal());
        json.setFiltered(filter.getFiltered());

        return json;
    }

    @RequestMapping("{seccion}/reporteAlumnos")
    public ModelAndView reporteRecargasComedor(
            @PathVariable("seccion") Long idSeccion,
            HttpSession session, Model model) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();
        Docente docente = ds.getDocente();

        CursoNivelacion seccion = service.findSeccion(new CursoNivelacion(idSeccion), docente, ciclo);
        List<NotaAlumnoNivelacion> alumnado = service.allAlumnadoBySeccion(seccion);

        model.addAttribute("alumnado", alumnado);
        model.addAttribute("seccion", seccion);
        model.addAttribute("ciclo", ciclo);

        return new ModelAndView(excelMatriculadosNivelacion);
    }

    private ObjectNode createCicloJson(CicloAcademico ciclo) {
        return JaneHelper
                .from(ciclo)
                .only("id,descripcion,descripcion2")
                .json();
    }

    private ObjectNode createSeccionJson(CursoNivelacion cursoNiv) {
        ObjectNode node = JaneHelper
                .from(cursoNiv)
                .only("id,codigo,matriculados")
                .join("docente", "id,codigo")
                .join("docente.persona", "id,apellidosNombres,numeroDocIdentidad,tipoFoto,rutaFoto")
                .join("aula", "id,codigo,nombre,capacidadAula,aforo")
                .join("plantilla", "id,codigo")
                .join("cursoCiclo", "id,horasCiclo")
                .join("cursoCiclo.curso", "id,codigo,nombre,horasCiclo")
                .json();

        return node;
    }

    private ArrayNode createAlumnosJson(List<NotaAlumnoNivelacion> alumnos) {
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        for (NotaAlumnoNivelacion nota : alumnos) {
            ObjectNode node = JaneHelper
                    .from(nota)
                    .join("cursoNivelacion", "id,codigo")
                    .join("alumnoNivelacion", "id,estado,estadoEnum")
                    .join("alumnoNivelacion.alumno", "id,codigo")
                    .join("alumnoNivelacion.alumno.modalidadEstudio", "id,codigo,nombre")
                    .join("alumnoNivelacion.alumno.carrera", "id,codigo,nombre,tipo,tipoEnum")
                    .join("alumnoNivelacion.alumno.carrera.facultad", "id,codigo,nombre")
                    .join("alumnoNivelacion.alumno.persona", "id,apellidosNombres,numeroDocIdentidad,tipoFoto,rutaFoto")
                    .join("alumnoNivelacion.alumno.persona.tipoDocumento", "simbolo")
                    .join("alumnoNivelacion.alumno.postulantePregrado.modalidadIngreso", "id,nombre")
                    .join("alumnoNivelacion.alumno.postulantePregrado.cicloPostula.cicloAcademico", "id,descripcion")
                    .json();

            array.add(node);
        }
        return array;
    }

}
