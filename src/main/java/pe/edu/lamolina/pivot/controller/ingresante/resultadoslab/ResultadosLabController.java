package pe.edu.lamolina.pivot.controller.ingresante.resultadoslab;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.RecorridoIngresante;
import pe.edu.lamolina.model.constantines.GlobalMessages;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.inscripcion.TurnoEntrevistaObuae;
import pe.edu.lamolina.model.medico.DiarioLaboratorio;
import pe.edu.lamolina.model.medico.HistoriaClinica;
import pe.edu.lamolina.model.medico.HistoriaLaboratorio;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("ingresante/resultadoslab")
public class ResultadosLabController {

    @Autowired
    ResultadosLabService service;

    @RequestMapping(method = RequestMethod.GET)
    public String postulante(Model model, HttpSession session) {

        return "ingresante/resultadoslab/resultadoslab";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse listIngresantes(DynatableFilter filter, HttpSession session) {
//        DynatableResponse json = new DynatableResponse();
//        try {
//            
//            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
//            
//            List<RecorridoIngresante> lista = service.ingresantesDynatableCiclo(filter, ds.getCicloAcademico());
//            
//            List<Alumno> alumnos = lista.stream()
//                    .map(RecorridoIngresante::getAlumno)
//                    .collect(Collectors.toList());
//            
//            List<Persona> personas = alumnos.stream()
//                    .map(Alumno::getPersona)
//                    .collect(Collectors.toList());
//            
//            List<HistoriaLaboratorio> laboratorios = service.allLabByPersonas(personas);
//            
//            List<HistoriaClinica> historias = service.allHistoriaByPersonas(personas);
//            
//            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
//            
//            for (RecorridoIngresante reco : lista) {
//                //busqueda historia clinica
//                List<HistoriaClinica> resultHistoria = historias.stream()
//                        .filter(item -> item.getPaciente().getPersona().getId().equals(reco.getAlumno().getPersona().getId()))
//                        .collect(Collectors.toList());
//                
//                HistoriaLaboratorio laboratorio = new HistoriaLaboratorio();
//                
//                if (resultHistoria.size() > 0) {
//                    HistoriaClinica historiaCli = resultHistoria.get(0);
//                    //busqueda laboratorio
//                    List<HistoriaLaboratorio> resultLaboratorio = laboratorios.stream()
//                            .filter(item -> item.getHistoriaClinica().getId().equals(historiaCli.getId()))
//                            .collect(Collectors.toList());
//                    if (resultLaboratorio.size() > 0) {
//                        laboratorio = resultLaboratorio.get(0);
//                    } else {
//                        laboratorio.setHistoriaClinica(historiaCli);
//                    }
//                } else {
//                    //buscar paciente
//                    //si no existe, crearlo
//                    //crear historia clinica                    
//                    //poner historia creada en laboratorio
//                }
//                
//                reco.setLaboratorio(laboratorio);
//                
//                ObjectNode node = JsonHelper.createJson(reco, JsonNodeFactory.instance, true,
//                        new String[]{
//                            "*",
//                            "alumno.*",
//                            "alumno.carrera.nombre",
//                            "alumno.persona.*",
//                            "alumno.persona.tipoDocumento.simbolo",
//                            "turnoEntrevistaObuae.*",
//                            "laboratorio.id",
//                            "laboratorio.valorMuestra",
//                            "laboratorio.numeroMuestra",
//                            "laboratorio.historiaClinica.id"
//                        });
//                array.add(node);
//            }
//            
//            json.setData(array);
//            json.setTotal(filter.getTotal());
//            json.setFiltered(filter.getFiltered());
//            
//        } catch (Exception e) {
//            e.printStackTrace();
//            json.setTotal(0);
//        }
//        return json;
        DynatableResponse json = new DynatableResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

//            List<RecorridoIngresante> lista = service.ingresantesDynatable(filter,  ds.getCicloAcademico());
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
                                "laboratorio.fechaRegistro",
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
}
