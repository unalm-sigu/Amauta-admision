package pe.edu.lamolina.amauta.controller.matricula.nivelacion;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import static pe.edu.lamolina.model.constantines.GlobalMessages.UPDATED;

@Slf4j
@Controller
@RequestMapping("academico/matriculable/nivelacion")
public class MatriculableNivelacionController {

    @Autowired
    MatriculableNivelacionService service;

    @ResponseBody
    @RequestMapping("clonar")
    public String clonarNivelacion(HttpSession session, @RequestBody @Valid ClonarNivelacionDTO clonarNivelacionDTO) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.ClonarNivelacionDTO(ds, clonarNivelacionDTO);
        service.generarPrioridad(clonarNivelacionDTO.getCicloDestino());
        return UPDATED;

    }

}
