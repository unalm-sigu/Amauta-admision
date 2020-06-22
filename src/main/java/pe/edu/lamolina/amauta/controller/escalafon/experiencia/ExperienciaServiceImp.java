package pe.edu.lamolina.amauta.controller.escalafon.experiencia;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.amauta.controller.escalafon.EscalafonService;
import pe.edu.lamolina.amauta.dao.escalafon.ExperienciaEscalafonDAO;
import pe.edu.lamolina.model.escalafon.Escalafon;
import pe.edu.lamolina.model.escalafon.ExperienciaEscalafon;
import pe.edu.lamolina.model.escalafon.enums.TipoExperienciaEnum;

@Service
@Transactional
public class ExperienciaServiceImp implements ExperienciaService {

    @Autowired
    ExperienciaEscalafonDAO experienciaEscalafonDAO;

    @Autowired
    EscalafonService escalafonService;

    @Override
    public List<ExperienciaEscalafon> allExperienciaByEscalafon(Escalafon escalafon) {
        return experienciaEscalafonDAO.allByEscalafon(escalafon);
    }

    @Override
    public void save(ExperienciaEscalafon experienciaEscalafonForm) {
        escalafonService.verificarFecha(experienciaEscalafonForm.getFechaInicio(), experienciaEscalafonForm.getFechaFin());
        if (experienciaEscalafonForm.getId() != null) {
            ExperienciaEscalafon experienciaEscalafonBD = experienciaEscalafonDAO.find(experienciaEscalafonForm.getId());
            if (experienciaEscalafonForm.getTipoExperienciaEnum().equals(TipoExperienciaEnum.DOCENTE)) {
                experienciaEscalafonBD.setUniversidad(experienciaEscalafonForm.getUniversidad());
                experienciaEscalafonBD.setTipoDocente(experienciaEscalafonForm.getTipoDocente());
                experienciaEscalafonBD.setInstitucion(null);
            }

            if (experienciaEscalafonForm.getTipoExperienciaEnum().equals(TipoExperienciaEnum.EXPERIENCIA)) {
                experienciaEscalafonBD.setInstitucion(experienciaEscalafonForm.getInstitucion());
                experienciaEscalafonBD.setTipoDocente(null);
                experienciaEscalafonBD.setUniversidad(null);
            }

            experienciaEscalafonBD.setTipo(experienciaEscalafonForm.getTipo());
            experienciaEscalafonBD.setFechaInicio(experienciaEscalafonForm.getFechaInicio());
            experienciaEscalafonBD.setFechaFin(experienciaEscalafonForm.getFechaFin());
            experienciaEscalafonBD.setCargo(experienciaEscalafonForm.getCargo());
            experienciaEscalafonDAO.update(experienciaEscalafonBD);
        } else {
            experienciaEscalafonDAO.save(experienciaEscalafonForm);
        }
    }

    @Override
    public void eliminar(ExperienciaEscalafon experienciaEscalafon) {
        experienciaEscalafonDAO.delete(experienciaEscalafon);
    }

}
