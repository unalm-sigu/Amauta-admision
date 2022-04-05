package pe.edu.lamolina.amauta.controller.academico.historico;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
import pe.edu.lamolina.amauta.controller.seguridad.verificador.VerificadorServiceImp;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.SituacionAcademica;
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

    @RequestMapping(value = "{idAlumno}/update", method = RequestMethod.GET)
    public String update(@PathVariable("idAlumno") Long idAlumno, Model model) {
        model.addAttribute("idAlumno", idAlumno);
        return "academico/historico/alumnohistoricoregistro";
    }

    @ResponseBody
    @RequestMapping("{idAlumno}/find")
    public ObjectNode find(@PathVariable("idAlumno") Long idAlumno) {

        Alumno alumno = service.findAlumno(idAlumno);

        ObjectNode jAlumno = JaneHelper.from(alumno)
                .join("carrera")
                .join("situacionAcademica")
                .join("modalidadEstudio")
                .join("cicloIngreso")
                .join("persona")
                .join("persona.tipoDocumento")
                .join("persona.nacionalidad")
                .join("persona.paisNacer")
                .join("persona.paisDomicilio")
                .join("persona.ubicacionDomicilio")
                .join("persona.ubicacionNacer")
                .join("persona.paisNacer")
                .json();

        return jAlumno;
    }

    @ResponseBody
    @RequestMapping("{idAlumno}/historial")
    public ArrayNode historial(@PathVariable("idAlumno") Long idAlumno) {

        List<AlumnoCiclo> alumnoCiclos = service.allAlumnoCiclo(idAlumno);
        return JaneHelper.from(alumnoCiclos)
                .join("carrera")
                .join("situacionInicio")
                .join("alumno")
                .join("alumno.carrera")
                .join("alumno.modalidadEstudio")
                .join("alumno.situacionAcademica")
                .join("alumno.cicloIngreso")
                .join("cicloAcademico")
                .array();

    }

    @ResponseBody
    @RequestMapping("{idAlumnoCiclo}/alumnoCicloCurso")
    public ArrayNode alumnoCicloCurso(@PathVariable("idAlumnoCiclo") Long idAlumnoCiclo) {

        List<AlumnoCicloCurso> alumnoCicloCursos = service.allAlumnoCicloCurso(idAlumnoCiclo);
        return JaneHelper.from(alumnoCicloCursos)
                .join("curso")
                .join("alumnoCiclo")
                .array();

    }

    @ResponseBody
    @RequestMapping("save")
    public Map save(@RequestBody Alumno alumno, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.save(alumno, ds);
        Map response = new LinkedHashMap();
        response.put("message", GlobalMessages.CREATED);
        response.put("id", alumno.getId());
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public String update(@RequestBody Alumno alumno, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.update(alumno, ds);
        return GlobalMessages.UPDATED;
    }

    @ResponseBody
    @RequestMapping("{idAlumno}/delete")
    public String delete(@PathVariable("idAlumno") Long idAlumno) {

        service.deleteAlumnoHistorico(idAlumno);
        return GlobalMessages.DELETED;
    }

    @ResponseBody
    @RequestMapping("{idAlumnoCiclo}/deleteAlumnoCiclo")
    public String deleteAlumnoCiclo(@PathVariable("idAlumnoCiclo") Long idAlumnoCiclo) {

        service.deleteAlumnoCiclo(idAlumnoCiclo);
        return GlobalMessages.DELETED;
    }

    @ResponseBody
    @RequestMapping("{idAlumnoCicloCurso}/deleteAlumnoCicloCurso")
    public String deleteAlumnoCicloCurso(@PathVariable("idAlumnoCicloCurso") Long idAlumnoCicloCurso) {

        service.deleteAlumnoCicloCurso(idAlumnoCicloCurso);
        return GlobalMessages.DELETED;
    }

    @ResponseBody
    @RequestMapping("existealumno")
    public ObjectNode existealumno(@RequestBody Persona personaDocumento) {

        Persona persona = service.validarAlumnoDocumento(personaDocumento);
        return JaneHelper.from(persona)
                .join("tipoDocumento")
                .join("nacionalidad")
                .join("paisNacer")
                .join("paisDomicilio")
                .join("ubicacionDomicilio")
                .join("ubicacionNacer")
                .join("paisNacer")
                .json();

    }

    @ResponseBody
    @RequestMapping("datos")
    public ObjectNode datos() {

        List<CicloAcademico> ciclos = service.allCicloAcademico();
        List<TipoDocIdentidad> tiposDocumentos = service.allTiposDocIdentidad();
        List<ModalidadEstudio> modalidades = service.allModalidad();
        List<SituacionAcademica> situaciones = service.allSituacionAcademica();

        ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
        node.set("tiposDocumentos", JaneHelper.from(tiposDocumentos).only("id,simbolo,nombre").array());
        node.set("ciclos", JaneHelper.from(ciclos).only("id,descripcion").join("modalidadEstudio", "id,nombre").array());
        node.set("modalidades", JaneHelper.from(modalidades).only("id,nombre").array());
        node.set("situaciones", JaneHelper.from(situaciones).only("id,nombre").array());
        return node;

    }

    @ResponseBody
    @RequestMapping("saveCicloAlumno")
    public String saveCicloAlumno(@RequestBody AlumnoCiclo alumnoCiclo, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.saveCicloAlumno(alumnoCiclo, ds);
        return GlobalMessages.CREATED;
    }

    @ResponseBody
    @RequestMapping("saveCicloAlumnoCurso")
    public String saveCicloAlumnoCurso(@RequestBody AlumnoCiclo alumnoCiclo, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.saveCicloAlumnoCurso(alumnoCiclo, ds);
        return GlobalMessages.UPDATED;
    }

    @ResponseBody
    @RequestMapping("calcularpromedio")
    public String calcularpromedio(@RequestBody Alumno alumno, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.calcularPromedio(alumno, ds);
        return GlobalMessages.UPDATED;
    }

}
