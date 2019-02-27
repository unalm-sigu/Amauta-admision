package pe.edu.lamolina.pivot.controller.ingresante.muestraslab;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.RecorridoIngresante;
import pe.edu.lamolina.model.constantines.GlobalMessages;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.inscripcion.TurnoEntrevistaObuae;
import pe.edu.lamolina.model.medico.HistoriaClinica;
import pe.edu.lamolina.model.medico.HistoriaLaboratorio;
import pe.edu.lamolina.pivot.controller.reporte.view.IngresanteMuestraLabView;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("ingresante/muestraslab")
public class MuestrasLabController {

    @Autowired
    MuestrasLabService service;

    @Autowired
    VisorMuestrasLab visorMuestrasLab;    
    
    @Autowired
    IngresanteMuestraLabView ingresanteMuestraLabView;

    @RequestMapping(method = RequestMethod.GET)
    public String postulante(Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        ObjectNode jsonLab = new ObjectNode(JsonNodeFactory.instance);
        jsonLab.put("numero", visorMuestrasLab.getNumeroLab());

        model.addAttribute("laboratorioActual", jsonLab);

        return "ingresante/muestraslab/muestraslab";
    }

    @ResponseBody
    @RequestMapping("list/{idTurno}")
    public DynatableResponse listIngresantes(@PathVariable Long idTurno, DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        try {

            TurnoEntrevistaObuae turno = new TurnoEntrevistaObuae(idTurno);

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            List<RecorridoIngresante> lista = service.ingresantesDynatableTurno(filter, turno, ds.getCicloAcademico());

            List<Alumno> alumnos = lista.stream()
                    .map(RecorridoIngresante::getAlumno)
                    .collect(Collectors.toList());

            List<Persona> personas = alumnos.stream()
                    .map(Alumno::getPersona)
                    .collect(Collectors.toList());

            List<HistoriaLaboratorio> laboratorios = service.allLabByPersonas(personas);

            List<HistoriaClinica> historias = service.allHistoriaByPersonas(personas);

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (RecorridoIngresante reco : lista) {
                //busqueda historia clinica
                List<HistoriaClinica> resultHistoria = historias.stream()
                        .filter(item -> item.getPaciente().getPersona().getId().equals(reco.getAlumno().getPersona().getId()))
                        .collect(Collectors.toList());

                HistoriaLaboratorio laboratorio = new HistoriaLaboratorio();

                if (resultHistoria.size() > 0) {
                    HistoriaClinica historiaCli = resultHistoria.get(0);
                    //riesgo alumno
                    Boolean riesgo = service.findRiesgoAlumno(historiaCli);                    
                    reco.setTieneRiesgo(riesgo);
                    
                    //busqueda laboratorio
                    List<HistoriaLaboratorio> resultLaboratorio = laboratorios.stream()
                            .filter(item -> item.getHistoriaClinica().getId().equals(historiaCli.getId()))
                            .collect(Collectors.toList());
                    if (resultLaboratorio.size() > 0) {
                        laboratorio = resultLaboratorio.get(0);
                    } else {
                        laboratorio.setHistoriaClinica(historiaCli);
                    }
                } else {
                    //buscar paciente
                    //si no existe, crearlo
                    //crear historia clinica                    
                    //poner historia creada en laboratorio
                }

                reco.setLaboratorio(laboratorio);

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
                            "laboratorio.fechaRegistro",
                            "laboratorio.historiaClinica.id",
                            "tieneRiesgo"
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
    @RequestMapping("list/atendidos/{idTurno}")
    public DynatableResponse listIngresantesAtendidos(@PathVariable Long idTurno, DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        try {

            TurnoEntrevistaObuae turno = service.findTurno(idTurno);

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

//            List<RecorridoIngresante> lista = service.ingresantesDynatable(filter,  ds.getCicloAcademico());
            List<RecorridoIngresante> lista = service.allIngresantesCiclo(ds.getCicloAcademico());

            List<Alumno> alumnos = lista.stream()
                    .map(RecorridoIngresante::getAlumno)
                    .collect(Collectors.toList());

            List<Persona> personas = alumnos.stream()
                    .map(Alumno::getPersona)
                    .collect(Collectors.toList());

            List<HistoriaLaboratorio> laboratorios = service.allLabByPersonasFilterFecha(personas, turno.getFecha());

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
    @RequestMapping("turnos")
    public JsonResponse turnos(HttpSession session
    ) {
        JsonResponse json = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            List<TurnoEntrevistaObuae> turnos = service.allTurnos(ds.getCicloAcademico());
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (TurnoEntrevistaObuae elem : turnos) {

                ObjectNode node = JsonHelper.createJson(elem, JsonNodeFactory.instance, true,
                        new String[]{
                            "*",});
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

    @ResponseBody
    @RequestMapping("saveLaboratorio")
    public JsonResponse saveLaboratorio(@RequestBody HistoriaLaboratorio laboratorio, HttpSession session) {

        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {

            laboratorio.setNumeroMuestra(visorMuestrasLab.getNumeroLab());
            laboratorio.setFechaRegistro(new Date());
            laboratorio.setUserRegistro(ds.getUsuario());
            service.saveLaboratorio(laboratorio);
            visorMuestrasLab.incrementaNumLab();

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
    @RequestMapping("borrarLaboratorio")
    public JsonResponse borrarLaboratorio(@RequestBody HistoriaLaboratorio laboratorio, HttpSession session) {

        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {

            service.deleteLaboratorio(laboratorio);

            ObjectNode json = JsonHelper.createJson(laboratorio, JsonNodeFactory.instance, new String[]{
                "*",});

            response.setData(json);
            response.setMessage(GlobalMessages.DELETED);
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }
    
    @RequestMapping("listaExcel")
    public ModelAndView listaExcel(Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        
        List<RecorridoIngresante> ingresantes = service.allIngresantesConTurno(ds.getCicloAcademico());

        InputStream formato = this.getClass().getResourceAsStream("/templates/excel/formatoIngresanteMuestraLab.xlsx");

        model.addAttribute("formato", formato);
        model.addAttribute("ingresantes", ingresantes);
        
        return new ModelAndView(ingresanteMuestraLabView);
    }

}
