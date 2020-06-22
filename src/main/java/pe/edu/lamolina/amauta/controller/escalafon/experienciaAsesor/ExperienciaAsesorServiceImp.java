package pe.edu.lamolina.amauta.controller.escalafon.experienciaAsesor;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.amauta.dao.escalafon.ExperienciaAsesorDAO;
import pe.edu.lamolina.model.escalafon.Escalafon;
import pe.edu.lamolina.model.escalafon.ExperienciaAsesor;

@Service
@Transactional
public class ExperienciaAsesorServiceImp implements ExperienciaAsesorService {

    @Autowired
    ExperienciaAsesorDAO experienciaAsesorDAO;

    @Override
    public List<ExperienciaAsesor> allExperienciaAsesorByEscalafon(Escalafon escalafon) {
        return experienciaAsesorDAO.allByEscalafon(escalafon);
    }

    @Override
    public void save(ExperienciaAsesor experienciaAsesorForm) {
        if (experienciaAsesorForm.getId() != null) {
            ExperienciaAsesor experienciaAsesorBD = experienciaAsesorDAO.find(experienciaAsesorForm.getId());
            experienciaAsesorBD.setConfirmado(experienciaAsesorForm.getConfirmado());
            experienciaAsesorBD.setFechaAceptacion(experienciaAsesorForm.getFechaAceptacion());
            experienciaAsesorBD.setNotaConfirmacion(experienciaAsesorForm.getNotaConfirmacion());
            experienciaAsesorBD.setTesista(experienciaAsesorForm.getTesista());
            experienciaAsesorBD.setTipoTesis(experienciaAsesorForm.getTipoTesis());
            experienciaAsesorBD.setUniversidad(experienciaAsesorForm.getUniversidad());
            experienciaAsesorBD.setUrlRepositorio(experienciaAsesorForm.getUrlRepositorio());
            experienciaAsesorDAO.update(experienciaAsesorBD);
        } else {
            experienciaAsesorDAO.save(experienciaAsesorForm);
        }
    }

    @Override
    public void eliminar(ExperienciaAsesor experienciaAsesor) {
        experienciaAsesorDAO.delete(experienciaAsesor);
    }

}
