package pe.edu.lamolina.amauta.controller.academico.renuncia;

import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.json.JaneHelper;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.constantines.GlobalMessages;
import pe.edu.lamolina.model.inscripcion.Postulante;

@Slf4j
@Controller
@RequestMapping("academico/renuncia/alumno")
public class AlumnoRenunciaController {

    @Autowired
    AlumnoRenunciaService service;

    @RequestMapping(method = RequestMethod.GET)
    public String index() {
        return "academico/renuncia/alumnorenuncia";
    }

    @ResponseBody
    @RequestMapping("all")
    public DynatableResponse all(DynatableFilter filter, HttpSession session, HttpServletRequest request) {

        DynatableResponse json = new DynatableResponse();
        json.setTotal(0);

        List<Postulante> alumnos = service.allAlumnosbyDynatable(filter);

        ArrayNode array = JaneHelper.from(alumnos)
                .join("persona", "id,apellidosNombres,rutaFoto,tipoFoto,numeroDocIdentidad,telefono,celular,email,emailCompania")
                .join("persona.tipoDocumento", "simbolo")
                .array();

        json.setData(array);
        json.setTotal(filter.getTotal());
        json.setFiltered(filter.getFiltered());

        return json;
    }

    @ResponseBody
    @RequestMapping(value = "apply", method = RequestMethod.POST)
    public String apply(@RequestBody Alumno alumno, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.apply(alumno, ds);
        return GlobalMessages.UPDATED;
    }

}
