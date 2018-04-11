package pe.edu.lamolina.pivot.controller.tramite;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.general.Idioma;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.PlantillaDocumentoAcademico;
import pe.edu.lamolina.pivot.dao.general.IdiomaDAO;
import pe.edu.lamolina.pivot.dao.tramite.PlantillaConstanciaDAO;

@Service
@Transactional(readOnly = true)
public class PlantillaConstanciaServiceImpl implements PlantillaConstanciaService {

    @Autowired
    PlantillaConstanciaDAO plantillaConstanciaDAO;
    
    @Autowired
    IdiomaDAO idiomaDAO ;

    @Override
    @Transactional
    public void update(PlantillaDocumentoAcademico plantillaDocumentoAcademico, Usuario usuario) {
        plantillaConstanciaDAO.update(plantillaDocumentoAcademico);
    }

    @Override
    @Transactional
    public void save(PlantillaDocumentoAcademico plantillaDocumentoAcademico, Usuario usuario) {
        plantillaDocumentoAcademico.setFechaRegistro(new Date());
        plantillaDocumentoAcademico.setIdUserRegistro(usuario.getId());
        plantillaDocumentoAcademico.setContenido("Constancia");
        plantillaConstanciaDAO.save(plantillaDocumentoAcademico);
    }

    @Override
    public PlantillaDocumentoAcademico findById(PlantillaDocumentoAcademico plantillaDocumentoAcademico) {
        return plantillaConstanciaDAO.find(plantillaDocumentoAcademico.getId());
    }

    @Override
    public List<PlantillaDocumentoAcademico> all(DynatableFilter filter) {
        return plantillaConstanciaDAO.allDynatable(filter);
    }
    
    @Override
    public List<Idioma> allIdioma() {
        return idiomaDAO.all();
    }
}
