package pe.edu.lamolina.pivot.controller.academico.distanciapabellon;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.DistanciaPabellon;
import pe.edu.lamolina.model.enums.OficinaEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.pivot.dao.academico.DepartamentoAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.DistanciaPabellonDAO;
import pe.edu.lamolina.pivot.dao.general.AulaDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = false)
public class DistanciaPabellonServiceImp implements DistanciaPabellonService {

    @Autowired
    AulaDAO aulaDAO;

    @Autowired
    DistanciaPabellonDAO distanciaPabellonDAO;

    @Autowired
    DepartamentoAcademicoDAO departamentoAcademicoDAO;

    @Override
    public List<DepartamentoAcademico> allDepartamentos() {
        return departamentoAcademicoDAO.allActivos();
    }

    @Override
    public List<Aula> allModulos() {
        return aulaDAO.allPabellonesByOficina(OficinaEnum.OERA);
    }

    @Override
    public List<DepartamentoAcademico> allDynatableFilter(DynatableFilter filter, DataSessionPivot ds) {
        return departamentoAcademicoDAO.allDynatableFilter(filter);
    }

    @Override
    public List<DistanciaPabellon> allDistancia(DepartamentoAcademico departamentoAcademico) {
        List<Aula> modulos = aulaDAO.allPabellonesByOficina(OficinaEnum.OERA);
        List<DistanciaPabellon> distanciaPabellonDb = distanciaPabellonDAO.allFactorDistanciaByDepartamento(departamentoAcademico);
        Map<Long, DistanciaPabellon> distanciaPabellonMap = TypesUtil.convertListToMap("pabellon.id", distanciaPabellonDb);
        List<DistanciaPabellon> distanciaPabellonForm = new ArrayList<>();
        for (Aula modulo : modulos) {
            DistanciaPabellon distancia = distanciaPabellonMap.get(modulo.getId());
            if (distancia == null) {
                distancia = new DistanciaPabellon();
                distancia.setDepartamentoAcademico(departamentoAcademico);
                distancia.setPabellon(modulo);
            }
            distanciaPabellonForm.add(distancia);
        }
        return distanciaPabellonForm;
    }

    @Override
    @Transactional
    public void saveDistancia(DepartamentoAcademico departamentoAcademico) {
        List<DistanciaPabellon> distancias = departamentoAcademico.getDistanciaPabellon();
        for (DistanciaPabellon distancia : distancias) {
            distancia.setDepartamentoAcademico(departamentoAcademico);
            if (distancia.getId() != null) {
                if (distancia.getDistancia() != null) {
                    distanciaPabellonDAO.update(distancia);
                } else {
                    distanciaPabellonDAO.delete(distancia);
                }
            } else {
                if (distancia.getDistancia() != null) {
                    distanciaPabellonDAO.save(distancia);
                }
            }
        }
    }

}
