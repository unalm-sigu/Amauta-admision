package pe.edu.lamolina.amauta.controller.general.oficina.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.enums.NivelOficinaEnum;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.amauta.dao.general.ColaboradorDAO;
import pe.edu.lamolina.amauta.dao.general.OficinaDAO;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class OficinaServiceImp implements OficinaService {

    private final ColaboradorDAO colaboradorDAO;
    private final OficinaDAO oficinaDAO;

    private void agregarOficinasHijas(Oficina oficinaMain, List<Oficina> oficinas) {
        for (Oficina oficinasDependiente : oficinaMain.getOficinasDependientes()) {
            oficinas.add(oficinasDependiente);
            agregarOficinasHijas(oficinasDependiente, oficinas);
        }
    }

    @Override
    public List<Oficina> allOficinasByOficinaMain(Oficina oficina) {
        return allOficinasByMain(oficina);
    }

    @Override
    public List<Oficina> allOficinasMainByPersona(Persona persona) {
        List<Colaborador> colaboradores = colaboradorDAO.allActivosByPersona(persona);
        Map<Long, Oficina> mapOficinas = TypesUtil.convertListToMap("oficina.id", "oficina", colaboradores);
        List<Oficina> areasLaboraPersona = new ArrayList(mapOficinas.values());
        List<Oficina> oficinasMain = new ArrayList();

        List<Oficina> oficinasTodas = allOficinasOrganizadas();
        for (Oficina ofi : areasLaboraPersona) {
            Oficina main = findOficinaMain(ofi, oficinasTodas);
            oficinasMain.add(main);
        }
        return oficinasMain;

    }

    @Override
    public Oficina findOficinaMain(Oficina oficinaHija) {
        List<Oficina> oficinasTodas = allOficinasOrganizadas();
        Oficina oficinaHijaBD = oficinaDAO.find(oficinaHija);
        return findOficinaMain(oficinaHijaBD, oficinasTodas);
    }

    @Override
    public Oficina findOficinaMain(Oficina oficinaHija, List<Oficina> oficinasTodas) {
        Map<Long, Oficina> mapOficina = TypesUtil.convertListToMap("id", oficinasTodas);
        Oficina oficinaTempo = mapOficina.get(oficinaHija.getId());
        if (oficinaTempo.getTipoOficina().getNivelEnum() == NivelOficinaEnum.OFI) {
            return oficinaTempo;
        }
        for (;;) {
            Oficina sup = oficinaTempo.getOficinaSuperior();
            if (sup == null) {
                return null;
            }
            if (sup.getTipoOficina().getNivelEnum() == NivelOficinaEnum.OFI) {
                return sup;
            }
            oficinaTempo = sup;
        }

    }

    @Override
    public Oficina findOficinaHija(Persona persona, Oficina oficinaMain) {
        List<Colaborador> colaboradores = colaboradorDAO.allActivosByPersona(persona);
        Map<Long, Oficina> mapOficinas = TypesUtil.convertListToMap("oficina.id", "oficina", colaboradores);
        List<Oficina> oficinasHijas = new ArrayList(mapOficinas.values());

        List<Oficina> oficinasTodas = allOficinasOrganizadas();
        for (Oficina ofi : oficinasHijas) {
            Oficina main = findOficinaMain(ofi, oficinasTodas);
            if (main.getId() == oficinaMain.getId().longValue()) {
                return ofi;
            }
        }
        return null;
    }

    @Override
    public List<Oficina> allOficinasOrganizadas() {
        List<Oficina> oficinasTodas = oficinaDAO.all();
        Map<Long, Oficina> mapOficina = TypesUtil.convertListToMap("id", oficinasTodas);

        for (Oficina oficina : oficinasTodas) {
            oficina.setOficinasDependientes(new ArrayList());
        }
        for (Oficina oficina : oficinasTodas) {
            if (oficina.getOficinaSuperior() != null) {
                Oficina sup = mapOficina.get(oficina.getOficinaSuperior().getId());
                sup.getOficinasDependientes().add(oficina);
                oficina.setOficinaSuperior(sup);
            }
        }
        return oficinasTodas;
    }

    @Override
    public Oficina findByCodigo(String codigo) {
        return oficinaDAO.findByCode(codigo);
    }

    private List<Oficina> allOficinasByMain(Oficina oficinaMain) {
        List<Oficina> oficinasTodas = allOficinasOrganizadas();
        Map<Long, Oficina> mapOficina = TypesUtil.convertListToMap("id", oficinasTodas);

        Oficina oficinaBD = mapOficina.get(oficinaMain.getId());
        List<Oficina> oficinas = new ArrayList();
        oficinas.add(oficinaBD);
        agregarOficinasHijas(oficinaBD, oficinas);

        return oficinas;
    }

}
