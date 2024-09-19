package pe.edu.lamolina.amauta.controller.academico.pronabec;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("academico/becaspronabec")
public class BecasPronabecController {
    @Autowired
    BecasPronabecService service;
}
