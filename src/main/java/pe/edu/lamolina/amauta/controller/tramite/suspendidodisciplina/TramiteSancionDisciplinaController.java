package pe.edu.lamolina.amauta.controller.tramite.suspendidodisciplina;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.amauta.dao.tramite.SancionDisciplinaCicloDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.constantines.GlobalMessages;
import pe.edu.lamolina.model.tramite.RetiroCiclo;
import pe.edu.lamolina.model.tramite.SancionDisciplina;
import pe.edu.lamolina.model.tramite.SancionDisciplinaCiclo;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("academico/tramiteacademico/suspendidoDisciplina")
public class TramiteSancionDisciplinaController {

    @Autowired
    TramiteSancionDisciplinaService service;

    @Autowired
    SancionDisciplinaCicloDAO sancionDisciplinaCicloDAO;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        List<CicloAcademico> cicloAcademicos = service.getCiclos(ds);
        ArrayNode arrayCiclos = JaneHelper.from(cicloAcademicos).array();
        model.addAttribute("ciclos", arrayCiclos.toString());
        return "academico/tramitescademicos/sancionDisciplina/sancion";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse listTramites(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        try {
            List<SancionDisciplina> listSancion = service.allTramitesByFilter(filter, ds);
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            for(SancionDisciplina sancion : listSancion) {
                ObjectNode tramite = new ObjectNode(JsonNodeFactory.instance);
                tramite.put("id", sancion.getId());
                tramite.put("motivo", sancion.getMotivo());
                tramite.put("serie", sancion.getTramite().getSerie());
                tramite.put("numero", sancion.getTramite().getNumero());
                tramite.put("estadoSancion", sancion.getEstado());
                tramite.put("estadoTramite", sancion.getTramite().getEstado());
                tramite.putObject("estadoEnum").put("value", sancion.getEstadoEnum().getValue());
                tramite.putObject("estadoTramiteEnum").put("value", sancion.getTramite().getEstadoEnum().getValue());

                if (sancion.getAlumno() != null && sancion.getAlumno().getPersona() != null) {
                    ObjectNode persona = new ObjectNode(JsonNodeFactory.instance);
                    persona.put("apellidosNombres", sancion.getAlumno().getPersona().getNombreCompleto());
                    tramite.set("persona", persona);
                }

                if (sancion.getAlumno() != null) {
                    ObjectNode alumno = new ObjectNode(JsonNodeFactory.instance);
                    alumno.put("codigo", sancion.getAlumno().getCodigo());

                    if (sancion.getAlumno().getCarrera() != null && sancion.getAlumno().getCarrera().getFacultad() != null) {
                        ObjectNode carrera = new ObjectNode(JsonNodeFactory.instance);
                        ObjectNode facultad = new ObjectNode(JsonNodeFactory.instance);
                        facultad.put("nombre", sancion.getAlumno().getCarrera().getFacultad().getNombre());
                        carrera.set("facultad", facultad);
                        alumno.set("carrera", carrera);
                    }

                    tramite.set("alumno", alumno);
                }

                List<SancionDisciplinaCiclo> ciclosRelacionados = sancionDisciplinaCicloDAO.findBySancionDisciplina(sancion);
                System.out.println(ciclosRelacionados.size());

                ArrayNode ciclosArray = new ArrayNode(JsonNodeFactory.instance);
                for (SancionDisciplinaCiclo rel : ciclosRelacionados) {
                    if (rel.getCiclo() != null) {
                        ObjectNode cicloNode = new ObjectNode(JsonNodeFactory.instance);
                        cicloNode.put("id", rel.getCiclo().getId());
                        cicloNode.put("descripcion", rel.getCiclo().getDescripcion());
                        ciclosArray.add(cicloNode);
                    }
                }
                tramite.set("ciclos", ciclosArray);


                ObjectNode wrapper = new ObjectNode(JsonNodeFactory.instance);
                wrapper.set("tramite", tramite);

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
    public JsonResponse save(@RequestBody SancionDTO sancionForm, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        JsonResponse json = new JsonResponse();
        json.setSuccess(Boolean.FALSE);

        ObjectUtil.printAttr(sancionForm);
        try {

            List<CicloAcademico> listCiclos = sancionForm.getCicloAcademico();

            String MSG = service.saveSancionByCiclos(sancionForm,ds,listCiclos);
            json.setSuccess(MSG.equalsIgnoreCase("OK") ? Boolean.TRUE : Boolean.FALSE);
            json.setMessage(MSG.equalsIgnoreCase("OK") ? GlobalMessages.CREATED : MSG);
        } catch (PhobosException e) {
            e.printStackTrace();
        }
        return json;
    }


}
