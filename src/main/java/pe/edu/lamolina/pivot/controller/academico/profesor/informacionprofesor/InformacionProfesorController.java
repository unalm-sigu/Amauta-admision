package pe.edu.lamolina.pivot.controller.academico.profesor.informacionprofesor;

import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.pivot.controller.academico.visitante.AlumnoHelper;
import pe.edu.lamolina.pivot.controller.general.foto.FotoHelper;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/profesor")
public class InformacionProfesorController {

    @Autowired
    InformacionProfesorService service;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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

    @RequestMapping("{docente}/informacionacademica")
    public String informacionacademica(@PathVariable("docente") Long idDocente, Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        Compania compania = ds.getCompania();

        Docente docente = service.find(new Docente(idDocente));
        model.addAttribute("docente", docente);
        model.addAttribute("documentos", service.allDocumentos());
        model.addAttribute("fotoHelper", new FotoHelper());
        model.addAttribute("modalidades", service.allModalidadEstudio(compania));
        model.addAttribute("helper", new AlumnoHelper());
        return "academico/profesor/informacionacademica";

    }

    @RequestMapping("{docente}/informacionlaboral")
    public String informacionlaboral(@PathVariable("docente") Long idDocente, Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        Compania compania = ds.getCompania();

        Docente docente = service.find(new Docente(idDocente));
        model.addAttribute("docente", docente);
        model.addAttribute("documentos", service.allDocumentos());
        model.addAttribute("fotoHelper", new FotoHelper());
        model.addAttribute("modalidades", service.allModalidadEstudio(compania));
        model.addAttribute("helper", new AlumnoHelper());
        return "academico/profesor/informacionlaboral";

    }

    @RequestMapping("{docente}/informacionpersonal")
    public String informacionpersonal(@PathVariable("docente") Long idDocente, Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        Compania compania = ds.getCompania();

        Docente docente = service.find(new Docente(idDocente));
        model.addAttribute("docente", docente);
        model.addAttribute("documentos", service.allDocumentos());
        model.addAttribute("fotoHelper", new FotoHelper());
        model.addAttribute("modalidades", service.allModalidadEstudio(compania));
        model.addAttribute("helper", new AlumnoHelper());
        return "academico/profesor/informacionpersonal";

    }

}
