package pe.edu.lamolina.pivot.controller.permisoprogramahorario;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.AnexoBoletin;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.bean.ColaboradorAnexoBean;
import static pe.edu.lamolina.model.enums.PermisoProgramacionNivelEnum.CURSO;
import static pe.edu.lamolina.model.enums.PermisoProgramacionNivelEnum.DOCENTE;
import static pe.edu.lamolina.model.enums.PermisoProgramacionNivelEnum.GPOSECC;
import static pe.edu.lamolina.model.enums.PermisoProgramacionNivelEnum.SECCION;
import pe.edu.lamolina.model.permisoprogramacion.PermisoProgramacion;
import pe.edu.lamolina.model.permisoprogramacion.PermisosProgramacionHorarios;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("permisoprograma/colaborador")
public class PermisoProgramacionController {

    @Autowired
    PermisoProgramacionService service;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        CicloAcademico cicloAcademico = ds.getCicloAcademico();
        List<PermisoProgramacion> programacions = service.allPermisosPrograma();
        Map<String, List<PermisoProgramacion>> map = TypesUtil.convertListToMapList("nivel", programacions);

        List<AnexoBoletin> anexoBoletin = service.allAnexoBoletin();
        ArrayNode arrayEvento = new ArrayNode(JsonNodeFactory.instance);
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

        for (AnexoBoletin anexo : anexoBoletin) {
            ObjectNode node = JsonHelper.createJson(anexo, JsonNodeFactory.instance, new String[]{
                "*",
                "anexoSuperior.*"
            });
            array.add(node);
        }
        ArrayNode arrayProgramaCurso = new ArrayNode(JsonNodeFactory.instance);
        for (PermisoProgramacion item : map.get(CURSO.name())) {
            ObjectNode node = JsonHelper.createJson(item, JsonNodeFactory.instance, new String[]{
                "*"
            });
            arrayProgramaCurso.add(node);
        }
        ArrayNode arrayProgramaSecc = new ArrayNode(JsonNodeFactory.instance);
        for (PermisoProgramacion item : map.get(SECCION.name())) {
            ObjectNode node = JsonHelper.createJson(item, JsonNodeFactory.instance, new String[]{
                "*"
            });
            arrayProgramaSecc.add(node);
        }
        ArrayNode arrayProgramaGpoSecc = new ArrayNode(JsonNodeFactory.instance);
        for (PermisoProgramacion item : map.get(GPOSECC.name())) {
            ObjectNode node = JsonHelper.createJson(item, JsonNodeFactory.instance, new String[]{
                "*"
            });
            arrayProgramaGpoSecc.add(node);
        }
        ArrayNode arrayProgramadoc = new ArrayNode(JsonNodeFactory.instance);
        for (PermisoProgramacion item : map.get(DOCENTE.name())) {
            ObjectNode node = JsonHelper.createJson(item, JsonNodeFactory.instance, new String[]{
                "*"
            });
            arrayProgramadoc.add(node);
        }
        arrayEvento.addAll(arrayProgramaCurso);
        arrayEvento.addAll(arrayProgramaSecc);
        arrayEvento.addAll(arrayProgramaGpoSecc);
        arrayEvento.addAll(arrayProgramadoc);
        model.addAttribute("anexoBoletin", array);
        model.addAttribute("programaCurso", arrayProgramaCurso);
        model.addAttribute("programaSeccion", arrayProgramaSecc);
        model.addAttribute("programaGpoSeccion", arrayProgramaGpoSecc);
        model.addAttribute("programaDocente", arrayProgramadoc);
        model.addAttribute("eventos", arrayEvento);

        model.addAttribute("cicloacademico", cicloAcademico.getDescripcion());

        return "permisoprograma/permisoprograma";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {

        DynatableResponse json = new DynatableResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            Map<Long, String> mapFichados = new LinkedHashMap();

            List<ColaboradorAnexoBean> colaboradorAnexoBeans = service.allPermisos(filter);
            Map<Long, List<AnexoBoletin>> mapColaborAnexo = TypesUtil.convertListToMapList("colaborador.id", colaboradorAnexoBeans);
            ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
            for (ColaboradorAnexoBean colaboradorAnexo : colaboradorAnexoBeans) {
                String fichado = mapFichados.get(colaboradorAnexo.getColaborador().getId());
                List<AnexoBoletin> colaboradorAnexos = mapColaborAnexo.get(colaboradorAnexo.getColaborador().getId());
                ObjectNode node = JsonHelper.createJson(colaboradorAnexo, JsonNodeFactory.instance, new String[]{
                    "id",
                    "colaborador.*",
                    "colaborador.cargo.*",
                    "colaborador.persona.*",
                    "anexoBoletin.*",
                    "anexoBoletin.*"});
                ArrayNode arrayPermisoCurso = new ArrayNode(JsonNodeFactory.instance);
                for (PermisosProgramacionHorarios item : colaboradorAnexo.getPermisosCurso()) {
                    ObjectNode permisos = JsonHelper.createJson(item, JsonNodeFactory.instance, new String[]{
                        "puedeAgregar",
                        "puedeEliminar",
                        "puedeModificar",});
                    permisos.put("idPermiso", item.getId());
                    permisos.put("id", item.getPermisoProgramacion().getId());
                    permisos.put("nombre", item.getPermisoProgramacion().getNombre());
                    arrayPermisoCurso.add(permisos);
                }

                ArrayNode arrayPermisoSeccion = new ArrayNode(JsonNodeFactory.instance);
                for (PermisosProgramacionHorarios item : colaboradorAnexo.getPermisosSecc()) {
                    ObjectNode permisos = JsonHelper.createJson(item, JsonNodeFactory.instance, new String[]{
                        "puedeAgregar",
                        "puedeEliminar",
                        "puedeModificar",});
                    permisos.put("idPermiso", item.getId());
                    permisos.put("id", item.getPermisoProgramacion().getId());
                    permisos.put("nombre", item.getPermisoProgramacion().getNombre());
                    arrayPermisoSeccion.add(permisos);
                }

                ArrayNode arrayPermisoGsec = new ArrayNode(JsonNodeFactory.instance);
                for (PermisosProgramacionHorarios item : colaboradorAnexo.getPermisosGpoSec()) {
                    ObjectNode permisos = JsonHelper.createJson(item, JsonNodeFactory.instance, new String[]{
                        "puedeAgregar",
                        "puedeEliminar",
                        "puedeModificar",});
                    permisos.put("idPermiso", item.getId());
                    permisos.put("id", item.getPermisoProgramacion().getId());
                    permisos.put("nombre", item.getPermisoProgramacion().getNombre());
                    arrayPermisoGsec.add(permisos);
                }

                ArrayNode arrayPermisoDoc = new ArrayNode(JsonNodeFactory.instance);
                for (PermisosProgramacionHorarios item : colaboradorAnexo.getPermisosDocente()) {

                    ObjectNode permisos = JsonHelper.createJson(item, JsonNodeFactory.instance, new String[]{
                        "puedeAgregar",
                        "puedeEliminar",
                        "puedeModificar",});
                    permisos.put("idPermiso", item.getId());
                    permisos.put("id", item.getPermisoProgramacion().getId());
                    permisos.put("nombre", item.getPermisoProgramacion().getNombre());
                    arrayPermisoDoc.add(permisos);
                }

                node.set("permisosGpoSec", arrayPermisoGsec);
                node.set("permisosSecc", arrayPermisoSeccion);
                node.set("permisosCurso", arrayPermisoCurso);
                node.set("permisosDocente", arrayPermisoDoc);

                if (fichado == null) {
                    node.put("rows", colaboradorAnexos.size());
                    mapFichados.put(colaboradorAnexo.getColaborador().getId(), "OK");
                } else {
                    node.put("rows", 0);
                }
                arrayNode.add(node);
            }
            json.setData(arrayNode);
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
    public JsonResponse save(@RequestBody ColaboradorAnexoBean colaboradorAnexo, HttpSession session) {

        JsonResponse response = new JsonResponse();
        response.setSuccess(Boolean.FALSE);
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.save(colaboradorAnexo, ds);

            response.setMessage("Se registró satisfactoriamente");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }
    @ResponseBody
    @RequestMapping("savepermiso")
    public JsonResponse savepermiso(@RequestBody ColaboradorAnexoBean colaboradorAnexo, HttpSession session) {

        JsonResponse response = new JsonResponse();
        response.setSuccess(Boolean.FALSE);
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.savepermiso(colaboradorAnexo, ds);

            response.setMessage("Se registró satisfactoriamente");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("update")
    public JsonResponse update(@RequestBody ColaboradorAnexoBean colaboradorAnexo, HttpSession session) {

        JsonResponse response = new JsonResponse();
        response.setSuccess(Boolean.FALSE);
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.update(colaboradorAnexo, ds);

            response.setMessage("Se actualizó satisfactoriamente");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }
}
