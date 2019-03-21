package pe.edu.lamolina.pivot.controller.academico.facultad;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.enums.EnteAcademicoEstadoEnum;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.pivot.dao.academico.FacultadDAO;

@Service
@Transactional(readOnly = true)
public class FacultadServiceImp implements FacultadService {

    @Autowired
    FacultadDAO facultadDAO;

    @Override
    public List<Facultad> allFacultad(DynatableFilter filter, List<Facultad> facultads) {
        return facultadDAO.allDynatable(filter, facultads);
    }

    @Override
    public Facultad findFacultad(Long idFacultad) {
        return facultadDAO.find(idFacultad);
    }

    @Override
    public void save(Facultad facultad) {
        facultad.setCompania(new Compania(1));
        facultad.setFechaRegistro(new Date());
        facultad.setEstado(EnteAcademicoEstadoEnum.CRE);
        facultadDAO.save(facultad);
    }

    @Override
    @Transactional
    public void update(Facultad facultad) {
        Facultad facultadDb = facultadDAO.find(facultad.getId());
        facultadDb.setCodigo(facultad.getCodigo());
        facultadDb.setNombre(facultad.getNombre());
        facultadDb.setSimbolo(facultad.getSimbolo());
        facultadDAO.update(facultadDb);
    }

    @Override
    @Transactional
    public void delete(Facultad facultad) {
        facultadDAO.delete(facultad);
    }

    @Override
    @Transactional
    public void estado(Facultad facultad) {
        Facultad facultadBD = facultadDAO.find(facultad.getId());
        if (EnteAcademicoEstadoEnum.CRE.name().equalsIgnoreCase(facultadBD.getEstado())) {
            facultadBD.setEstado(EnteAcademicoEstadoEnum.ACT);
        } else if (EnteAcademicoEstadoEnum.ACT.name().equalsIgnoreCase(facultadBD.getEstado())) {
            facultadBD.setEstado(EnteAcademicoEstadoEnum.INA);
            facultadBD.setMotivoDesactivacion(facultad.getMotivoDesactivacion());
            facultadBD.setFechaDesactivacion(new Date());
        } else {
            facultadBD.setEstado(EnteAcademicoEstadoEnum.ACT);
        }
        facultadDAO.update(facultadBD);
    }

    @Override
    @Transactional
    public void updateConsejeroRequerido(Facultad facultadForm) {
        Facultad facultadDB = facultadDAO.find(facultadForm.getId());
        facultadDB.setConsejeriaRequerida(facultadForm.getConsejeriaRequerida());
        facultadDAO.update(facultadDB);
    }

}
