package pe.edu.lamolina.pivot.controller.ingresante.resultadoslab;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.RecorridoIngresante;
import pe.edu.lamolina.model.constantines.GlobalMessages;
import pe.edu.lamolina.model.enums.FactorRhEnum;
import pe.edu.lamolina.model.enums.TipoSangreEnum;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.medico.DiarioLaboratorio;
import pe.edu.lamolina.model.medico.HistoriaLaboratorio;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;
import pe.edu.lamolina.pivot.zelper.pdf.TipoPdfEnum;
import pe.edu.lamolina.pivot.zelper.pdf.pdfHtml.PDFFormatoEnum;
import pe.edu.lamolina.pivot.zelper.pdf.pdfHtml.PdfHtmlView;

@Controller
@RequestMapping("ingresante/resultadoslab")
public class ResultadosLabController {

    @Autowired
    ResultadosLabService service;

    @Autowired
    PdfHtmlView pdfHtmlView;

    @RequestMapping(method = RequestMethod.GET)
    public String postulante(Model model, HttpSession session) {

        return "ingresante/resultadoslab/resultadoslab";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse listIngresantes(DynatableFilter filter, HttpSession session) {

        DynatableResponse json = new DynatableResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            List<RecorridoIngresante> lista = service.ingresantesCiclo(ds.getCicloAcademico());

            List<Alumno> alumnos = lista.stream()
                    .map(RecorridoIngresante::getAlumno)
                    .collect(Collectors.toList());

            List<Persona> personas = alumnos.stream()
                    .map(Alumno::getPersona)
                    .collect(Collectors.toList());

            List<HistoriaLaboratorio> laboratorios = service.allLabByPersonas(personas);

            List<Persona> personasFiltro = new ArrayList();
            for (RecorridoIngresante reco : lista) {
                List<HistoriaLaboratorio> resultLab = laboratorios.stream()
                        .filter(item -> item.getHistoriaClinica().getPaciente().getPersona().getId().equals(reco.getAlumno().getPersona().getId()))
                        .collect(Collectors.toList());
                if (resultLab.size() > 0) {
                    personasFiltro.add(reco.getAlumno().getPersona());
                }
            }

            List<RecorridoIngresante> listaFiltrada = service.allIngresantesDynatableByPersona(filter, personasFiltro);

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            for (RecorridoIngresante reco : listaFiltrada) {
                List<HistoriaLaboratorio> resultLab = laboratorios.stream()
                        .filter(item -> item.getHistoriaClinica().getPaciente().getPersona().getId().equals(reco.getAlumno().getPersona().getId()))
                        .collect(Collectors.toList());
                if (resultLab.size() > 0) {
                    reco.setLaboratorio(resultLab.get(0));
                    ObjectNode node = JsonHelper.createJson(reco, JsonNodeFactory.instance, true,
                            new String[]{
                                "*",
                                "alumno.*",
                                "alumno.carrera.nombre",
                                "alumno.persona.*",
                                "alumno.persona.tipoDocumento.simbolo",
                                "turnoEntrevistaObuae.*",
                                "laboratorio.id",
                                "laboratorio.numeroMuestra",
                                "laboratorio.valorMuestra",
                                "laboratorio.tipoSangreEnum",
                                "laboratorio.factorRHEnum",
                                "laboratorio.estandar",
                                "laboratorio.hemoglobina",
                                "laboratorio.observaciones",
                                "laboratorio.historiaClinica.id"
                            });
                    array.add(node);
                }
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
    @RequestMapping("saveLaboratorio")
    public JsonResponse saveLaboratorio(@RequestBody HistoriaLaboratorio laboratorio, HttpSession session) {

        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            DiarioLaboratorio diario = service.getDiarioLabActual();
            laboratorio.setDiarioLaboratorio(diario);
            laboratorio.setFechaMuestra(new Date());

            service.saveLaboratorio(laboratorio);

            ObjectNode json = JsonHelper.createJson(laboratorio, JsonNodeFactory.instance, new String[]{
                "*",});

            response.setData(json);
            response.setMessage(GlobalMessages.CREATED);
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("tipoSangreList")
    public JsonResponse tipoAtencionExternaList(HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            ArrayNode json = JsonHelper.enumToJson(TipoSangreEnum.values());
            response.setData(json);
            response.setTotal(json.size());
            response.setSuccess(true);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("factorRhList")
    public JsonResponse factorRhList(HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            ArrayNode json = JsonHelper.enumToJson(FactorRhEnum.values());
            response.setData(json);
            response.setTotal(json.size());
            response.setSuccess(true);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @RequestMapping("reporte")
    public ModelAndView reporte(HttpSession session, HttpServletResponse respons, RedirectAttributes redirectAttr, Model model) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        List<RecorridoIngresante> lista = service.ingresantesCiclo(ds.getCicloAcademico());

        List<Alumno> alumnos = lista.stream()
                .map(RecorridoIngresante::getAlumno)
                .collect(Collectors.toList());

        List<Persona> personas = alumnos.stream()
                .map(Alumno::getPersona)
                .collect(Collectors.toList());

        List<HistoriaLaboratorio> laboratorios = service.allLabByPersonas(personas);

        List<Persona> personasFiltro = new ArrayList();
        for (RecorridoIngresante reco : lista) {
            List<HistoriaLaboratorio> resultLab = laboratorios.stream()
                    .filter(item -> item.getHistoriaClinica().getPaciente().getPersona().getId().equals(reco.getAlumno().getPersona().getId()))
                    .collect(Collectors.toList());
            if (resultLab.size() > 0) {
                personasFiltro.add(reco.getAlumno().getPersona());
            }
        }

        List<RecorridoIngresante> listaFiltrada = service.allIngresantesByPersona(personasFiltro);

        model.addAttribute("formatoEnum", PDFFormatoEnum.RESULTADOS_LAB);
        model.addAttribute("nombrePdf", String.format("Resultados de Laboratorio - %s", ds.getCicloAcademico().getDescripcion2()));
        model.addAttribute("ingresantes", listaFiltrada);
        model.addAttribute("ciclo", ds.getCicloAcademico());

        return new ModelAndView(pdfHtmlView);
    }
}
