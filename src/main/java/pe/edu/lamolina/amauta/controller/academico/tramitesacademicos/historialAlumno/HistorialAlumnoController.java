package pe.edu.lamolina.amauta.controller.academico.tramitesacademicos.historialAlumno;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.amauta.controller.academico.visitante.AlumnoHelper;

import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.Persona;

@Slf4j
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Controller
@RequestMapping("academico/tramiteacademico/historialalumno")
public class HistorialAlumnoController {

    private final HistorialAlumnoService historialAlumnoService;
    
    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        model.addAttribute("cicloAcademico", ds.getCicloAcademico());
        return "academico/tramitescademicos/historialAlumno/historialAlumno";
    }
    
    @RequestMapping("nuevo")
    public String newAlumno(Model model, HttpSession session) {
        
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        Compania compania = ds.getCompania();
        
        Carrera carrera = new Carrera();
        carrera.setFacultad(new Facultad());
        
        Alumno alumno = new Alumno();
        alumno.setPersona(new Persona());
        alumno.setCarrera(carrera);                
        
        model.addAttribute("documentos", historialAlumnoService.allDocumentos());
        model.addAttribute("modalidades", historialAlumnoService.allModalidadEstudioByCodes(Arrays.asList(ModalidadEstudioEnum.PRE, ModalidadEstudioEnum.EPG), compania));
        model.addAttribute("alumno", alumno);
        model.addAttribute("ciclos", historialAlumnoService.allCicloAcademico());
        model.addAttribute("helper", new AlumnoHelper());
        
        return "academico/tramitescademicos/historialAlumno/historialAlumnoForm";
    }
    
    @ResponseBody
    @RequestMapping("allFacultad")
    public JsonResponse allFacultad(@RequestParam("nombre") String nombre, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            Compania compania = ds.getCompania();

            List<Facultad> facultades = historialAlumnoService.allFacultad(nombre, compania);
            ArrayNode array = new ArrayNode(jsonFactory);
            for (Facultad facultad : facultades) {
                ObjectNode a = new ObjectNode(jsonFactory);
                a.put("id", facultad.getId());
                a.put("codigo", facultad.getCodigo());
                a.put("nombre", facultad.getNombre());
                array.add(a);
            }

            response.setData(array);
            response.setTotal(array.size());
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }
    
    @ResponseBody
    @RequestMapping("allCarrera")
    public JsonResponse allCarrera(@RequestParam("nombre") String nombre, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            Compania compania = ds.getCompania();

            List<Carrera> carreras = historialAlumnoService.allCarrera(nombre, compania);
            ArrayNode array = new ArrayNode(jsonFactory);
            for (Carrera carrera : carreras) {
                ObjectNode a = new ObjectNode(jsonFactory);
                a.put("id", carrera.getId());
                a.put("codigo", carrera.getCodigo());
                a.put("nombre", carrera.getNombre());
                array.add(a);
            }

            response.setData(array);
            response.setTotal(array.size());
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }
    
    @ResponseBody
    @RequestMapping("allCiclo")
    public JsonResponse allCiclo(@RequestParam("nombre") String nombre, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();
        try {
            List<CicloAcademico> ciclos = historialAlumnoService.allCiclo(nombre);
            ArrayNode array = new ArrayNode(jsonFactory);
            for (CicloAcademico ciclo : ciclos) {
                ObjectNode a = new ObjectNode(jsonFactory);
                a.put("id", ciclo.getId());
                a.put("codigo", ciclo.getCodigo());
                a.put("descripcion", ciclo.getDescripcion());
                array.add(a);
            }

            response.setData(array);
            response.setTotal(array.size());
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }
    
    @ResponseBody
    @RequestMapping("save")
    public JsonResponse save(Alumno alumno, HttpSession session, RedirectAttributes redirectAttr) {

        JsonResponse response = new JsonResponse();
        ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            Boolean success = true;
            historialAlumnoService.save(alumno, ds);
            /*if (alumno.getId() == null) {
                historialAlumnoService.save(alumno, ds);
                response.setMessage("Alumno creado satisfactoriamente");

            } else {
                Persona personaDuplicada = historialAlumnoService.update(alumno, ds);
                if (personaDuplicada == null) {
                    response.setMessage("Alumno modificado satisfactoriamente");
                } else {
                    ObjectNode objectNode = JsonHelper.createJson(personaDuplicada, JsonNodeFactory.instance, new String[]{
                        "*",
                        "tipoDocumento.*"
                    });
                    node.put("personaDuplicado", objectNode);
                    response.setMessage("DNI duplicado");
                    success = false;
                }
            }*/

            response.setSuccess(success);
            response.setData(node);

            node.put("personaId", alumno.getId());
            node.put("nombreCompleto", alumno.getPersona().getApellidosNombres());

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }
    
    @ResponseBody
    @RequestMapping("findPersonaAlumno")
    public JsonResponse findPersonaProfesor(Docente alumno, HttpSession session) {

        JsonResponse response = new JsonResponse();
        ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
        response.setSuccess(false);

        try {

            Persona persona = historialAlumnoService.findPersonaByDocIdentidad(alumno.getPersona());
            Persona personaBD = null;
            if (persona != null) {
                personaBD = historialAlumnoService.findPersona(persona);
            }
            AlumnoHelper helper = new AlumnoHelper();

            if (personaBD != null) {
                node.put("idPersona", personaBD.getId());
                node.put("foto", personaBD.getFoto());
                node.put("tipoDocumentoId", personaBD.getTipoDocumento().getId());
                node.put("numeroDoc", personaBD.getNumeroDocIdentidad());
                node.put("paterno", personaBD.getPaterno());
                node.put("materno", personaBD.getMaterno());
                node.put("nombres", personaBD.getNombres());
                node.put("emailCompania", personaBD.getEmailCompania());
                node.put("sexo", personaBD.getSexo());
                node.put("paisNacerId", personaBD.getPaisNacer() != null ? personaBD.getPaisNacer().getId() : null);
                node.put("paisNacerNombre", personaBD.getPaisNacer() != null ? personaBD.getPaisNacer().getNombre() + " | "
                        + helper.showCodigoPais(personaBD.getPaisNacer()) : null);
                node.put("ubicacionNacerId", personaBD.getUbicacionNacer() != null ? personaBD.getUbicacionNacer().getId() : null);
                node.put("ubicacionNacerNombre", personaBD.getUbicacionNacer() != null ? personaBD.getUbicacionNacer().getDistrito() : null);
                node.put("fechaNacer", personaBD.getFechaNacer() != null ? TypesUtil.getStringDate(personaBD.getFechaNacer(), "dd/MM/yyyy") : "");
                node.put("nacionalidadId", personaBD.getNacionalidad() != null ? personaBD.getNacionalidad().getId() : null);
                node.put("nacionalidadNombre", personaBD.getNacionalidad() != null ? personaBD.getNacionalidad().getNombre() : null);
                node.put("telefono", personaBD.getTelefono());
                node.put("celular", personaBD.getCelular());
                node.put("email", personaBD.getEmail());
                node.put("paisDomiciliodId", personaBD.getPaisDomicilio() != null ? personaBD.getPaisDomicilio().getId() : null);
                node.put("paisDomicilioNombre", personaBD.getPaisDomicilio() != null ? personaBD.getPaisDomicilio().getNombre() : null);
                node.put("ubicaiconDomiciliodId", personaBD.getUbicacionDomicilio() != null ? personaBD.getUbicacionDomicilio().getId() : null);
                node.put("ubicacionDomicilioNombre", personaBD.getUbicacionDomicilio() != null ? personaBD.getUbicacionDomicilio().getDistrito() : null);
                node.put("direccion", personaBD.getDireccion());
                node.put("foto", personaBD.getFoto());
                node.put("conDiscapacidad", personaBD.getConDiscapacidad());

                response.setSuccess(true);
            }

            response.setData(node);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Void> save(@RequestBody PersonaDto personaDto, HttpSession session) {        
        boolean registrado = historialAlumnoService.registrarAlumno(personaDto, session);
        if (registrado) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
}