package pe.edu.lamolina.amauta.controller.academico.encuestaestudiantil.docentemodalidad;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import static com.helger.commons.io.stream.StreamHelper.close;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.context.Context;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.amauta.controller.seguridad.verificador.VerificadorService;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaDocenteModalidad;
import pe.edu.lamolina.model.encuestaestudiantil.PuntajeEncuestaDocenteModalidad;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.amauta.zelper.pdf.PdfHtml;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;

@Controller
@RequestMapping("academico/encuestaestudiantil/docentemodalidad")
public class EncuestaDocenteModalidadController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    EncuestaDocenteModalidadService service;

    @Autowired
    VerificadorService verificadorService;

    @Autowired
    PdfHtml pdfHtml;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session, HttpServletRequest request) {
        String codeRequest = verificadorService.generateCodeRequest();

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();
        List<DepartamentoAcademico> departamentos = verificadorService.allInstanciasByMenuRol(TipoOficinaEnum.DPTO, request, ds, codeRequest);
        List<Facultad> facultades = departamentos.stream().map(x -> x.getFacultad()).distinct().collect(Collectors.toList());
        ArrayNode jFacultades = JaneHelper.from(facultades).array();
        ArrayNode jDepartamentos = JaneHelper.from(departamentos).join("facultad", "id").array();

        List<CicloAcademico> ciclos = service.allCicloAcademico();
        model.addAttribute("jFacultades", jFacultades.toString());
        model.addAttribute("jDepartamentos", jDepartamentos.toString());
        model.addAttribute("cicloAcademico", JaneHelper.from(ciclo).json().toString());
        model.addAttribute("isDocente", ds.getDocente() != null);
        model.addAttribute("ciclos", JaneHelper.from(ciclos).array().toString());
        return "academico/encuestaestudiantil/docentemodalidad/encuestadocentemodalidad";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session, HttpServletRequest request) {
        DynatableResponse json = new DynatableResponse();
        String codeRequest = verificadorService.generateCodeRequest();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            CicloAcademico ciclo = ds.getCicloAcademico();

            List<Facultad> facultades = service.allAccesoFacultades(ds, request, codeRequest);
            List<DepartamentoAcademico> departamentos = service.allAccesoDepartamentos(ds, facultades, ciclo, request, codeRequest);
            List<EncuestaDocenteModalidad> encuestas = service.allByDynatableCicloAcademico(filter, ciclo, departamentos, ds);
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (EncuestaDocenteModalidad encu : encuestas) {

                ObjectNode node = JsonHelper.createJson(encu, JsonNodeFactory.instance, true,
                        new String[]{
                            "*",
                            "docente.codigo",
                            "docente.departamentoAcademico.nombre",
                            "docente.departamentoAcademico.facultad.nombre",
                            "docente.persona.apellidosNombres",
                            "docente.persona.tipoDocumento.simbolo",
                            "docente.persona.numeroDocIdentidad",
                            "modalidadEstudio.nombre"
                        });
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
    @RequestMapping(value = "{id}/resumen/temas", method = RequestMethod.GET)
    public JsonResponse resumenTemas(@PathVariable Long id) {
        JsonResponse response = new JsonResponse();
        try {
            List<PuntajeEncuestaDocenteModalidad> lista = service.resumenTemas(new EncuestaDocenteModalidad(id));
            ArrayNode arr = new ArrayNode(JsonNodeFactory.instance);

            for (PuntajeEncuestaDocenteModalidad item : lista) {
                arr.add(JsonHelper.createJson(item, JsonNodeFactory.instance, new String[]{
                    "puntaje",
                    "desviacionStandar",
                    "temaEncuesta.nombre"
                }));
            }
            response.setData(arr);
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @RequestMapping("{id}/reporte")
    public ModelAndView reporte(@PathVariable Long id, Model model, HttpSession session, HttpServletResponse response) {

        Context ctx = service.reporte(new EncuestaDocenteModalidad(id));
        model.addAllAttributes(ctx.getVariables());
        return new ModelAndView(pdfHtml);

    }

    @RequestMapping("reporte/todos")
    public ModelAndView reporteTodos(@RequestParam(value = "departamento", required = false) Long departamentoId,
            @RequestParam("tipoGrado") String tipoGrado,
            @RequestParam(value = "cicloAcademico", required = false) Long cicloAcademicoId,
            @RequestParam(value = "facultad", required = false) Long facultadId,
            Model model, HttpSession session, HttpServletResponse response, HttpServletRequest request) {

        String codeRequest = verificadorService.generateCodeRequest();

        ModalidadEstudioEnum modalidadEstudioEnum = ModalidadEstudioEnum.valueOf(tipoGrado);

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        List<DepartamentoAcademico> departamentos = verificadorService.allInstanciasByMenuRol(TipoOficinaEnum.DPTO, request, ds, codeRequest);

        if (facultadId != null) {

            List<DepartamentoAcademico> departamentosXfacutad = departamentos
                    .stream()
                    .filter(x -> x.getFacultad().getId() == facultadId)
                    .collect(Collectors.toList());

            if (!departamentosXfacutad.isEmpty()) {
                departamentos = departamentosXfacutad;
            }

        }

        if (departamentoId != null) {
            departamentos.removeIf(x -> !x.equals(new DepartamentoAcademico(departamentoId)));
        }

        CicloAcademico ciclo = cicloAcademicoId != null ? new CicloAcademico(cicloAcademicoId) : ds.getCicloAcademico();

        List<Context> mulitpleContext = service.reporteTodos(ciclo, modalidadEstudioEnum, departamentos);
        model.addAttribute("multipleContext", mulitpleContext);
        model.addAttribute("templatePdf", "resultadoencuesta");
        model.addAttribute("nombrePdf", System.currentTimeMillis() + "_ResultadoEncuesta");

        return new ModelAndView(pdfHtml);

    }

}
