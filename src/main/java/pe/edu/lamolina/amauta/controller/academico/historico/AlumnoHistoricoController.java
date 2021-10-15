package pe.edu.lamolina.amauta.controller.academico.historico;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.edu.lamolina.amauta.controller.seguridad.verificador.VerificadorServiceImp;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.constantines.GlobalMessages;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;

@Controller
@RequestMapping("academico/historico/alumno")
public class AlumnoHistoricoController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AlumnoHistoricoService service;

    @RequestMapping(method = RequestMethod.GET)
    public String index() {
        return "academico/historico/alumnohistorico";
    }

    @ResponseBody
    @RequestMapping("all")
    public DynatableResponse all(DynatableFilter filter, HttpSession session, HttpServletRequest request) {

        DynatableResponse json = new DynatableResponse();
        json.setTotal(0);

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        String codeRequest = service.generateCodeRequest();

        List<Carrera> carreras = new ArrayList();
        List<Alumno> alumnos = new ArrayList();
        VerificadorServiceImp.CantidadItemsEnum cantidadEnum = service.verificarCantidad(TipoOficinaEnum.ESP, request, ds);

        if (cantidadEnum == VerificadorServiceImp.CantidadItemsEnum.PARCIAL) {
            carreras = service.allInstanciasByMenuRol(TipoOficinaEnum.ESP, request, ds, codeRequest);
        }

        if (cantidadEnum != VerificadorServiceImp.CantidadItemsEnum.SIN_PERMISO) {
            alumnos = service.allAlumnosbyDynatable(filter, carreras, cantidadEnum.name());
        }

        ArrayNode array = JaneHelper.from(alumnos)
                .only("id,codigo,estado,estadoEnum,promedioAcumulado,creditosCursados,creditosAprobados")
                .join("persona", "id,apellidosNombres,rutaFoto,tipoFoto,numeroDocIdentidad,telefono,celular,email,emailCompania")
                .join("persona.tipoDocumento", "simbolo")
                .join("carrera", "nombre,codigo,tipoEnum,tipo")
                .join("carrera.facultad", "codigo,nombre")
                .join("modalidadEstudio", "codigo,nombre")
                .join("situacionAcademica", "codigo,nombre")
                .join("cicloIngreso", "descripcion")
                .join("cicloActivo", "descripcion")
                .array();

        json.setData(array);
        json.setTotal(filter.getTotal());
        json.setFiltered(filter.getFiltered());

        return json;
    }

    @RequestMapping("registro")
    public String registro() {
        return "academico/historico/alumnohistoricoregistro";
    }

    @RequestMapping("{idAlumno}/update")
    public String update(@PathVariable("idAlumno") Long idAlumno, Model model) {
        model.addAttribute("idAlumno", idAlumno);
        return "academico/historico/alumnohistoricoregistro";
    }

    @ResponseBody
    @RequestMapping("{idAlumno}/find")
    public ObjectNode find(@PathVariable("idAlumno") Long idAlumno) {

        Alumno alumno = service.findAlumno(idAlumno);

        ObjectNode jAlumno = JaneHelper.from(alumno)
                .join("persona")
                .join("persona.tipoDocumento")
                .json();

        return jAlumno;
    }

    @ResponseBody
    @RequestMapping("save")
    public String save(@RequestBody Alumno alumno, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.save(alumno, ds);
        return GlobalMessages.CREATED;
    }

    @ResponseBody
    @RequestMapping("update")
    public String update(@RequestBody Alumno alumno, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.update(alumno, ds);
        return GlobalMessages.UPDATED;
    }

    @ResponseBody
    @RequestMapping("delete")
    public String delete(Alumno alumno) {

        service.delete(alumno);
        return GlobalMessages.DELETED;
    }

    @ResponseBody
    @RequestMapping("existealumno")
    public ObjectNode existealumno(@RequestBody Persona personaDocumento) {
        
        Persona persona = service.validarAlumnoDocumento(personaDocumento);
        return JaneHelper.from(persona)
                .json();
        
    }

    @ResponseBody
    @RequestMapping("datos")
    public ObjectNode datos() {

        List<CicloAcademico> ciclos = service.allCicloAcademico();
        List<TipoDocIdentidad> tiposDocumentos = service.allTiposDocIdentidad();

        ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
        node.set("tiposDocumentos", JaneHelper.from(tiposDocumentos).only("id,simbolo,nombre").array());
        node.set("ciclos", JaneHelper.from(ciclos).only("id,descripcion").array());
        return node;

    }

}
