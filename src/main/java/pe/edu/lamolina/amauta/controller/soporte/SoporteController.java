package pe.edu.lamolina.amauta.controller.soporte;

import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.json.JaneHelper;
import pe.edu.lamolina.model.academico.Soporte;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.constantines.GlobalMessages;

@Controller
@RequestMapping("academico/soporte")
public class SoporteController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SoporteService service;

    @Autowired
    SoporteExcelView soporteExcelView;

    @RequestMapping(method = RequestMethod.GET)
    public String index() {
        return "soporte/soporte";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {

        DynatableResponse json = new DynatableResponse();
        json.setTotal(0);

        List<Soporte> soportes = service.allDyanatable(filter);

        ArrayNode soportesNode = JaneHelper.from(soportes)
                .join("alumno", "id,codigo,estado,estadoEnum,promedioAcumulado,creditosCursados,creditosAprobados")
                .join("alumno.persona", "id,apellidosNombres,rutaFoto,tipoFoto,numeroDocIdentidad,telefono,celular,email,emailCompania")
                .join("alumno.persona.tipoDocumento", "simbolo")
                .join("alumno.carrera", "nombre,codigo,tipoEnum,tipo")
                .join("alumno.carrera.facultad", "codigo,nombre")
                .join("alumno.modalidadEstudio", "codigo,nombre")
                .join("alumno.situacionAcademica", "codigo,nombre")
                .join("alumno.cicloIngreso", "descripcion")
                .join("alumno.cicloActivo", "descripcion")
                .join("userAtencion.persona", "nombreCompleto")
                .array();

        json.setData(soportesNode);
        json.setFiltered(filter.getFiltered());
        json.setTotal(filter.getTotal());
        return json;

    }

    @ResponseBody
    @RequestMapping("responder")
    public String save(@RequestBody Soporte soporte, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.responder(soporte, ds);
        return GlobalMessages.UPDATED;

    }

    @RequestMapping("reporte")
    public ModelAndView reporte(Model model) {

        model.addAttribute("soportes", service.allSoporte());
        return new ModelAndView(soporteExcelView);

    }

}
