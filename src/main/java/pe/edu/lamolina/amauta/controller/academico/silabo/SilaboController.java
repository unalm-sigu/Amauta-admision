package pe.edu.lamolina.amauta.controller.academico.silabo;

import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.json.JaneHelper;
import pe.edu.lamolina.model.academico.SilaboCurso;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.constantines.GlobalMessages;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.general.Compania;

@Slf4j
@Controller
@RequestMapping("academico/silabo")
public class SilaboController {

    @Autowired
    SilaboService service;

    @RequestMapping(method = RequestMethod.GET)
    public String index() {
        return "academico/silabo/silabo";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse allByDynatable(DynatableFilter filter, HttpSession session) {

        DynatableResponse json = new DynatableResponse();
        json.setTotal(0);

        List<SilaboCurso> silabos = service.allSilabo(filter);

        ArrayNode array = JaneHelper.from(silabos)
                .join("curso", "id,codigo,nombre")
                .join("cicloVigenciaInicio", "id,codigo,descripcion")
                .join("curso.modalidadEstudio", "id,codigo,nombre")
                .join("curso.departamentoAcademico", "id,codigo,nombre,nombreLargo")
                .join("curso.departamentoAcademico.facultad", "id,codigo,nombre")
                .join("departamentoAcademico", "id,codigo,nombre")
                .array();

        json.setData(array);
        json.setTotal(filter.getTotal());
        json.setFiltered(filter.getFiltered());
        return json;

    }

    @ResponseBody
    @RequestMapping("save")
    public String save(@RequestBody SilaboCurso silabo, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        silabo.setUserRegistro(ds.getUsuario());
        service.save(silabo);

        if (silabo.getId() == null) {
            return GlobalMessages.CREATED;
        } else {
            return GlobalMessages.UPDATED;
        }
    }

    @ResponseBody
    @RequestMapping("delete")
    public String delete(@RequestBody SilaboCurso silabo, HttpSession session) {

        service.delete(silabo);
        return GlobalMessages.DELETED;
    }

    @ResponseBody
    @RequestMapping("revision")
    public String revision(@RequestBody SilaboCurso silabo, HttpSession session) {

        return service.revision(silabo);
    }

    @ResponseBody
    @RequestMapping("allCursoMod")
    public ArrayNode allCursoMod(@RequestParam("nombre") String nombre, HttpSession session) {

        List<Curso> cursos = service.allCursoByModalidadEstudioNombre(nombre, ModalidadEstudioEnum.PRE);
        return JaneHelper.from(cursos).only("id,codigo,nombre,tpc")
                .join("departamentoAcademico", "nombre")
                .array();
    }

    @ResponseBody
    @RequestMapping("allDepartamentoMod")
    public ArrayNode allDepartamentoMod(@RequestParam("nombre") String nombre, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        Compania compania = ds.getCompania();
        List<DepartamentoAcademico> departamentoAcademicos = service.allDepartamentoMod(nombre, compania);
        return JaneHelper.from(departamentoAcademicos).only("id,codigo,nombre")
                .array();
    }

    @ResponseBody
    @RequestMapping("allCiclo")
    public ArrayNode allCiclo(HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        List<CicloAcademico> ciclos = service.allCiclo(ds);
        return JaneHelper.from(ciclos).only("id,codigo,descripcion")
                .array();
    }

    @RequestMapping("descargar")
    public void descargar(@RequestParam("silabus") ArrayList<Long> silabus,
            HttpServletResponse response) {
        
        service.downloadZip(silabus,response);
        
    }
}
