package pe.edu.lamolina.amauta.controller.tramite.incrustacion;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.enums.TipoPlantillaDocumentoEnum;
import pe.edu.lamolina.model.general.Idioma;
import pe.edu.lamolina.model.tramite.PlantillaDocumentoAcademico;
import pe.edu.lamolina.amauta.dao.general.IdiomaDAO;
import pe.edu.lamolina.amauta.dao.tramite.PlantillaDocumentoAcademicoDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class PlantillaIncrustacionServiceImpl implements PlantillaIncrustacionService {

    @Autowired
    PlantillaDocumentoAcademicoDAO documentoAcademicoDAO;

    @Autowired
    IdiomaDAO idiomaDAO;

    @Override
    public List<PlantillaDocumentoAcademico> all(DynatableFilter filter) {
        return documentoAcademicoDAO.allDynatableIncrustacion(filter);
    }

    @Override
    public List<Idioma> allIdioma() {
        return idiomaDAO.all();
    }

    @Override
    @Transactional
    public void update(PlantillaDocumentoAcademico plantillaDocumentoForm, DataSessionPivot ds) {
        PlantillaDocumentoAcademico documentoAcademico = documentoAcademicoDAO.find(plantillaDocumentoForm.getId());
        documentoAcademico.setNombre(plantillaDocumentoForm.getNombre());
        documentoAcademico.setIdioma(plantillaDocumentoForm.getIdioma());
        documentoAcademicoDAO.update(documentoAcademico);
    }

    @Override
    @Transactional
    public void save(PlantillaDocumentoAcademico documentoAcademico, DataSessionPivot ds) {

        documentoAcademico.setTipoEnum(TipoPlantillaDocumentoEnum.PARR);
        documentoAcademico.setFechaRegistro(new Date());
        documentoAcademico.setUserRegistro(ds.getUsuario());
        documentoAcademico.setContenido("");
        documentoAcademicoDAO.save(documentoAcademico);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        documentoAcademicoDAO.delete(id);
    }

}
