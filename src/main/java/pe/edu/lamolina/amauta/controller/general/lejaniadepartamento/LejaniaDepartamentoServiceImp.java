package pe.edu.lamolina.amauta.controller.general.lejaniadepartamento;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.DistanciaPabellon;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.amauta.dao.academico.DepartamentoAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.DistanciaPabellonDAO;
import pe.edu.lamolina.amauta.dao.general.AulaDAO;
import pe.edu.lamolina.amauta.dao.general.OficinaDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class LejaniaDepartamentoServiceImp implements LejaniaDepartamentoService {

    @Autowired
    DistanciaPabellonDAO distanciaPabellonDAO;

    @Autowired
    DepartamentoAcademicoDAO departamentoAcademicoDAO;

    @Autowired
    AulaDAO aulaDAO;

    @Autowired
    OficinaDAO oficinaDAO;

    @Override
    public List<DepartamentoAcademico> allDepartamentos() {
        List<DepartamentoAcademico> departamentosAcademicos = departamentoAcademicoDAO.allActivos();
        return departamentosAcademicos;
    }

    @Override
    public List<DistanciaPabellon> allFactorDistanciaByDepartamento(DepartamentoAcademico departamentoAcademico) {
        List<DistanciaPabellon> distanciaPabellon = distanciaPabellonDAO.allFactorDistanciaByDepartamento(departamentoAcademico);
        return distanciaPabellon;
    }

    @Override
    public Oficina findOficinaOera() {
        return oficinaDAO.findByCode("OERA");
    }

    @Override
    public List<Aula> allPabellonesByOficina(Oficina oficinaOERA) {
        return aulaDAO.allPabellonesByOficina(oficinaOERA);
    }

    @Override
    @Transactional
    public void save(List<DistanciaPabellon> distanciaPabellon, DataSessionPivot ds) {
        for (DistanciaPabellon distancia : distanciaPabellon) {

            if (distancia.getId() == null) {
                if (distancia.getDistancia() != null && distancia.getDistancia() > 0) {
                    distanciaPabellonDAO.save(distancia);
                }
            } else if (distancia.getDistancia() != null && distancia.getDistancia() > 0) {
                distanciaPabellonDAO.update(distancia);
            } else {
                distanciaPabellonDAO.delete(distancia);
            }
        }

    }

    @Override
    public List<DistanciaPabellon> allDistanciaPabellon(DynatableFilter filter) {
       return distanciaPabellonDAO.allByDynatable(filter);
    }
}
