package pe.edu.lamolina.pivot.controller.rolexamen.components.cambiaraulaexamen;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoEspecial;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoRegular;
import pe.edu.lamolina.pivot.controller.rolexamen.components.CambiarAula;
import pe.edu.lamolina.model.enums.TipoGrupoRolExamenesEnum;
import pe.edu.lamolina.pivot.controller.rolexamen.util.RolExamenesLogger;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("rolexamen/cambiaraulaexamen")
public class CambiarAulaExamenController {

    @Autowired
    CambiarAulaExamenService cambiarAulaExamenService;

    @Autowired
    RolExamenesLogger rolExamenesLogger;

    @ResponseBody
    @RequestMapping(value = "loadComponent", method = RequestMethod.GET)
    public JsonResponse loadComponent(HttpSession session, HttpServletRequest request,
            @RequestParam("seccion") Long seccionId,
            @RequestParam("tipoOrigen") String tipoOrigen,
            @RequestParam("rolExamenes") Long rolExamenesId) {

        JsonResponse response = new JsonResponse();
        response.setSuccess(Boolean.TRUE);
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            Seccion seccion = new Seccion(seccionId);
            List<Aula> aulasOeras = cambiarAulaExamenService.allActivesAulasOeraForSeccion(seccion);

            RolExamenes rol = new RolExamenes(rolExamenesId);
            TipoGrupoRolExamenesEnum tipoGrupoRolExamenesEnumDefault = TipoGrupoRolExamenesEnum.GRU_REG;
            JsonNodeFactory jc = JsonNodeFactory.instance;
            ObjectNode data = new ObjectNode(jc);
            if (TipoGrupoRolExamenesEnum.GRU_ESP.name().equals(tipoOrigen)) {
                SeccionGrupoEspecial seccionGrupoEspecial = cambiarAulaExamenService.findSeccionGrupoEspecialBySeccionRolExamenes(seccion, rol);
                ObjectNode jSeccionRolExamenes = JsonHelper.createJson(seccionGrupoEspecial, jc, false, new String[]{
                    "*",
                    "seccion.*",
                    "aula.*",
                    "grupoHorasExamen.*",
                    "grupoHorasExamen.grupoHoras.*",
                    "grupoHorasExamen.horaInicio.*",
                    "grupoHorasExamen.horaFin.*"
                });
                data.set("seccionRolExamenes", jSeccionRolExamenes);
            } else if (TipoGrupoRolExamenesEnum.GRU_REG.name().equals(tipoOrigen)) {
                SeccionGrupoRegular seccionGrupoRegular = cambiarAulaExamenService.findSeccionGrupoRegularBySeccionRolExamenes(seccion, rol);
                ObjectNode jSeccionRolExamenes = JsonHelper.createJson(seccionGrupoRegular, jc, false, new String[]{
                    "*",
                    "seccion.*",
                    "aula.*",
                    "letraGrupoRegular.*",
                    "letraGrupoRegular.grupoHorasExamen.*"
                });
                jSeccionRolExamenes.set("grupoHorasExamen", JsonHelper.createJson(seccionGrupoRegular.getLetraGrupoRegular().getGrupoHorasExamen(), jc, false, new String[]{
                    "*",
                    "grupoHoras.*",
                    "horaInicio.*",
                    "horaFin.*"
                }));
                data.set("seccionRolExamenes", jSeccionRolExamenes);
            }
            if (tipoGrupoRolExamenesEnumDefault != null) {
                data.set("tipoGrupoRolExamenesEnumDefault", JsonHelper.createJson(tipoGrupoRolExamenesEnumDefault, jc, false, new String[]{
                    "*"
                }));
            }
            ArrayNode jAulasOeras = new ArrayNode(jc);
            aulasOeras.forEach(x -> {
                jAulasOeras.add(JsonHelper.createJson(x, jc, false, new String[]{
                    "*"
                }));
            });
            data.set("jAulasOeras", jAulasOeras);
            response.setData(data);
        } catch (PhobosException e) {
            e.printStackTrace();
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "cambiarAulaExamenSeccion", method = RequestMethod.POST)
    public JsonResponse cambiarAulaExamenSeccion(HttpSession session, HttpServletRequest request,
            @RequestBody CambiarAula cambiarAula) {
        JsonResponse response = new JsonResponse();
        response.setSuccess(Boolean.TRUE);
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        ds.setFechaAccionAudit(new Date());
        try {
            JsonNodeFactory jc = JsonNodeFactory.instance;
            cambiarAulaExamenService.cambiarAulaExamen(cambiarAula, ds);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        } finally {
            rolExamenesLogger.finalizeLog();
        }
        return response;
    }

}
