package pe.edu.lamolina.amauta.controller.general.oficina.util;

import java.util.List;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.Persona;

public interface OficinaService {

    List<Oficina> allOficinasByOficinaMain(Oficina oficina);

    List<Oficina> allOficinasMainByPersona(Persona persona);

    Oficina findOficinaHija(Persona persona, Oficina oficinaMain);

    List<Oficina> allOficinasOrganizadas();

    Oficina findOficinaMain(Oficina oficinaHija);

    Oficina findOficinaMain(Oficina oficinaHija, List<Oficina> oficinasTodas);

}
