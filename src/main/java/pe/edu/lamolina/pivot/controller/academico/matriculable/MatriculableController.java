package pe.edu.lamolina.pivot.controller.academico.matriculable;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.edu.lamolina.pivot.controller.general.foto.FotoHelper;
import pe.edu.lamolina.pivot.model.academico.Alumno;
import pe.edu.lamolina.pivot.model.academico.Carrera;
import pe.edu.lamolina.pivot.model.academico.Facultad;
import pe.edu.lamolina.pivot.model.academico.ModalidadEstudio;
import pe.edu.lamolina.pivot.model.general.Persona;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import static pe.edu.lamolina.pivot.zelper.enums.ModalidadEstudioEnum.ESP;
import static pe.edu.lamolina.pivot.zelper.enums.ModalidadEstudioEnum.VIS;
import pe.edu.lamolina.pivot.zelper.enums.RolEnum;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/matriculable")
public class MatriculableController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    MatriculableService service;

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

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        MatriculableResumen resumen = service.findResumenByCiclo(ds.getCicloAcademico());
        model.addAttribute("resumen", resumen);
        return "/academico/matriculable/matriculable";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {

        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        logger.debug("Rol activo {}", ds.getRolActivo().getCodigo());

        List<Long> filtros = new ArrayList();

        switch (RolEnum.valueOf(ds.getRolActivo().getCodigo())) {
            case TODO:
                break;
            case MOD:
                for (ModalidadEstudio modalidad : ds.getModalidades()) {
                    filtros.add(modalidad.getId());
                }
                break;
            case FAC:
                for (Facultad fac : ds.getFacultados()) {
                    filtros.add(fac.getId());
                }
                break;
            case ESP:
                for (Carrera carrera : ds.getCarreras()) {
                    filtros.add(carrera.getId());
                }
                break;
            default:
                break;
        }

        try {

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            FotoHelper helper = new FotoHelper();
            List<Alumno> alumnos = service.allAlumnosByCicloRolDynatable(filter, ds.getCicloAcademico(), ds.getRolActivo().getCodigo(), filtros);

            for (Alumno alumn : alumnos) {
                Persona persona = alumn.getPersona();
                Carrera carrera = alumn.getCarrera();
                Facultad facultad = carrera.getFacultad();

                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                node.put("id", alumn.getId());
                node.put("nombre", persona.getApellidosNombres());
                node.put("codigo", alumn.getCodigo());
                node.put("rutaFoto", helper.getRutaFoto(persona.getFoto(), persona.getSexo()));
                node.put("simbolo", persona.getTipoDocumento().getSimbolo());
                node.put("numeroDoc", persona.getNumeroDocIdentidad());
                node.put("tipoDoc", persona.getTipoDocumento().getSimbolo());
                node.put("carrera", carrera.getNombre());
                node.put("facultad", facultad.getNombre());
                node.put("situacion", alumn.getSituacionAcademica().getNombre());
                node.put("cicloIngreso", alumn.getCicloIngreso().getDescripcion());
                node.put("cicloActivo", alumn.getCicloActivo().getDescripcion2());
                node.put("estado", alumn.getEstado());
                node.put("estadoEnum", alumn.getEstadoEnum() != null ? alumn.getEstadoEnum().getValue() : "");
                node.put("ppa", alumn.getPromedioAcumulado());
                node.put("cca", alumn.getCreditosCursados());
                node.put("capa", alumn.getCreditosAprobados());

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

    @RequestMapping("nuevo")
    public String nuevo(Model model, HttpSession session) {

        List<String> codigos = new ArrayList();
        codigos.add(ESP.name());
        codigos.add(VIS.name());

        List<ModalidadEstudio> modalidades = service.allModalidadEstudioByCodigos(codigos);
        model.addAttribute("modalidades", modalidades);

        return "/academico/matriculable/matriculableModal";
    }

    @RequestMapping("generar")
    public String generar(Model model, HttpSession session) {

        return "/academico/matriculable/generar";
    }

    @RequestMapping("estadoVisor")
    public String estadoVisor(Model model, HttpSession session) {

        return "/academico/matriculable/estadoVisor";
    }

}
