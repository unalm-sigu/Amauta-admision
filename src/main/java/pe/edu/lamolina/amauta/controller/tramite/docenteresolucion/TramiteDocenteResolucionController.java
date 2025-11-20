package pe.edu.lamolina.amauta.controller.tramite.docenteresolucion;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.amauta.controller.tramite.suspendidodisciplina.CicloAcademicoDTO;
import pe.edu.lamolina.amauta.controller.tramite.suspendidodisciplina.SancionDTO;
import pe.edu.lamolina.amauta.controller.tramite.suspendidodisciplina.TramiteSancionDisciplinaService;
import pe.edu.lamolina.amauta.dao.tramite.DocenteResolucionDAO;
import pe.edu.lamolina.amauta.dao.tramite.SancionDisciplinaCicloDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.constantines.GlobalMessages;
import pe.edu.lamolina.model.tramite.DocenteResolucion;
import pe.edu.lamolina.model.tramite.SancionDisciplina;
import pe.edu.lamolina.model.tramite.SancionDisciplinaCiclo;

import javax.servlet.http.HttpSession;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@Controller
@RequestMapping("academico/tramiteacademico/docenteResolucion")
public class TramiteDocenteResolucionController {

    @Autowired
    TramiteDocenteResolucionService service;

    @Autowired
    DocenteResolucionDAO docenteResolucionDAO;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        List<CicloAcademico> cicloAcademicos = service.getCiclos();

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        ArrayNode arrayCiclos = new ArrayNode(jsonFactory);

        for (CicloAcademico ciclo : cicloAcademicos) {
            ObjectNode cicloNode = arrayCiclos.addObject();
            cicloNode.put("id", ciclo.getId());
            cicloNode.put("descripcion", ciclo.getDescripcion());

            if (ciclo.getModalidadEstudio() != null) {
                ObjectNode modalidadNode = cicloNode.putObject("modalidadEstudio");
                modalidadNode.put("id", ciclo.getModalidadEstudio().getId());
                modalidadNode.put("codigo", ciclo.getModalidadEstudio().getCodigo());
            }
        }

        model.addAttribute("ciclos", arrayCiclos.toString());
        return "academico/tramitescademicos/docenteresolucion/docenteResolucion";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse listTramites(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        try {
            List<DocenteResolucion> docenteRes = service.allTramiteByFilter(filter);
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for(DocenteResolucion docente : docenteRes) {
                // Crear objeto tramite
                ObjectNode tramite = new ObjectNode(JsonNodeFactory.instance);
                tramite.put("id", docente.getId());
                tramite.put("motivo", docente.getMotivo());
                tramite.put("serie", docente.getTramite().getSerie());
                tramite.put("numero", docente.getTramite().getNumero());
                tramite.put("estadoConsejo", docente.getEstadoConsejo());
                tramite.put("estadoFacultad", docente.getEstadoFacultad());
                tramite.put("estadoTramite", docente.getTramite().getEstado());
                tramite.putObject("estadoConsejoEnum").put("value", docente.getEstadoConsejoEnum().getValue());
                tramite.putObject("estadoFacultadEnum").put("value", docente.getEstadoFacultadEnum().getValue());
                tramite.putObject("estadoTramiteEnum").put("value", docente.getTramite().getEstadoEnum().getValue());

                // Agregar información de la persona (del docente)
                ObjectNode persona = tramite.putObject("persona");
                persona.put("apellidosNombres", docente.getDocente().getPersona().getApellidosNombres());

                // Agregar información del docente (en lugar de alumno)
                ObjectNode docenteNode = tramite.putObject("alumno"); // Mantener "alumno" para no cambiar el frontend
                docenteNode.put("codigo", docente.getDocente().getCodigo());


                // Agregar ciclo académico (como array de 1 elemento para mantener compatibilidad)
                ArrayNode ciclosArray = tramite.putArray("ciclos");
                if (docente.getCiclo() != null) {
                    ObjectNode cicloNode = new ObjectNode(JsonNodeFactory.instance);
                    cicloNode.put("id", docente.getCiclo().getId());
                    cicloNode.put("descripcion", docente.getCiclo().getDescripcion());

                    if (docente.getCiclo().getModalidadEstudio() != null) {
                        ObjectNode modalidadNode = cicloNode.putObject("modalidadEstudio");
                        modalidadNode.put("codigo", docente.getCiclo().getModalidadEstudio().getCodigo());
                    }

                    ciclosArray.add(cicloNode);
                }

                // Agregar información de docenteResolucion
                ObjectNode docenteResolucionNode = new ObjectNode(JsonNodeFactory.instance);
                docenteResolucionNode.put("resolucionConsejo", docente.getResolucionConsejo() != null ? docente.getResolucionConsejo().getDescripcion() : "NO");
                docenteResolucionNode.put("resolucionFacultad", docente.getResolucionFacultad() != null ? docente.getResolucionFacultad().getDescripcion() : "NO");

                // Envolver en el objeto wrapper
                ObjectNode wrapper = new ObjectNode(JsonNodeFactory.instance);
                wrapper.set("tramite", tramite);
                wrapper.set("docenteResolucion", docenteResolucionNode);

                array.add(wrapper);
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
    public JsonResponse save(@RequestBody DocenteResolucion docenteForm, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        JsonResponse json = new JsonResponse();
        json.setSuccess(Boolean.FALSE);

        try {

//            List<CicloAcademicoDTO> listCiclos = docenteForm.getCicloAcademico();

            String MSG = service.saveDocenteResolucion(docenteForm,ds);
            json.setSuccess(MSG.equalsIgnoreCase("OK") ? Boolean.TRUE : Boolean.FALSE);
            json.setMessage(MSG.equalsIgnoreCase("OK") ? GlobalMessages.CREATED : MSG);
        } catch (PhobosException e) {
            json.setSuccess(Boolean.FALSE);
            json.setMessage(e.getMessage());
            e.printStackTrace();

        } catch (Exception e) {
            json.setSuccess(Boolean.FALSE);
            json.setMessage(e.getMessage());
            e.printStackTrace();
        }
        return json;
    }

//    @ResponseBody
//    @RequestMapping("update")
//    public JsonResponse update(@RequestBody SancionDTO sancionForm, HttpSession session) {
//        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
//        JsonResponse json = new JsonResponse();
//        json.setSuccess(Boolean.FALSE);
//
//        try {
//            List<CicloAcademicoDTO> listCiclos = sancionForm.getCicloAcademico();
//
//            String MSG = service.updateSancionByCiclos(sancionForm, ds, listCiclos);
//            json.setSuccess(MSG.equalsIgnoreCase("OK") ? Boolean.TRUE : Boolean.FALSE);
//            json.setMessage(MSG.equalsIgnoreCase("OK") ? GlobalMessages.UPDATED : MSG);
//        } catch (PhobosException e) {
//            e.printStackTrace();
//            json.setMessage("Error al actualizar la sanción: " + e.getMessage());
//        }
//        return json;
//    }
//
//    @ResponseBody
//    @RequestMapping("get/{id}")
//    public JsonResponse getSancionById(@PathVariable Long id, HttpSession session) {
//        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
//        JsonResponse json = new JsonResponse();
//        json.setSuccess(Boolean.FALSE);
//
//        try {
//            SancionDTO sancionDTO = service.getSancionDTOById(id);
//            if (sancionDTO != null) {
//                json.setData(sancionDTO);
//                json.setSuccess(Boolean.TRUE);
//            } else {
//                json.setMessage("Sanción no encontrada");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            json.setMessage("Error al obtener la sanción: " + e.getMessage());
//        }
//        return json;
//    }

    @ResponseBody
    @RequestMapping(value = "anular/{idDocente}", method = RequestMethod.GET)
    public ResponseEntity anular(@PathVariable Long idDocente, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.anular(idDocente, ds.getUsuario());
        return new ResponseEntity(GlobalMessages.ANNULL, OK);

    }

    @ResponseBody
    @RequestMapping("allDocenteByNombre")
    public JsonResponse allAlumnoByNombre(@RequestParam("nombre") String nombre, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        try {

            JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
            List<Docente> lista = service.allDocenteByNombre(nombre, ds);
            ArrayNode jsonList = new ArrayNode(jsonFactory);

            for (Docente alum : lista) {
                jsonList.add(JsonHelper.createJson(alum, jsonFactory, true,
                        new String[]{
                                "id",
                                "codigo",
                                "persona.numeroDocIdentidad",
                                "persona.apellidosNombres",
                                "persona.nombreCompleto",
                                "persona.rutaFoto",
                                "persona.tipoDocumento.*"}));
            }

            response.setData(jsonList);
            response.setTotal(jsonList.size());
            response.setSuccess(true);
        } catch (PhobosException e) {
            pe.albatross.zelpers.miscelanea.ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

}
