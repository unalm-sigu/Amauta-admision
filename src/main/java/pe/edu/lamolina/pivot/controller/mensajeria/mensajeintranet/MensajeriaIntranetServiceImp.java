package pe.edu.lamolina.pivot.controller.mensajeria.mensajeintranet;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.GrupoAlumno;
import pe.edu.lamolina.model.academico.MensajeIntranet;
import pe.edu.lamolina.model.academico.TipoMensajeIntranet;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.dao.mensajeria.GrupoAlumnoDAO;
import pe.edu.lamolina.pivot.dao.mensajeria.MensajeIntranetDAO;
import pe.edu.lamolina.pivot.dao.mensajeria.TipoMensajeIntranetDAO;

@Service
@Transactional(readOnly = true)
public class MensajeriaIntranetServiceImp implements MensajeriaIntranetService {

    @Autowired
    GrupoAlumnoDAO grupoAlumnoDAO;
    @Autowired
    TipoMensajeIntranetDAO tipoMensajeIntranetDAO;
    @Autowired
    MensajeIntranetDAO mensajeIntranetDAO;

    @Override
    public List<GrupoAlumno> allGruposAlumnos() {
        return grupoAlumnoDAO.all();
    }

    @Override
    public List<TipoMensajeIntranet> allTiposMensajes() {
        return tipoMensajeIntranetDAO.all();
    }

    @Override
    public List<MensajeIntranet> allByDynatble(DynatableFilter filter) {
        return mensajeIntranetDAO.allByDynatble(filter);
    }

    @Override
    @Transactional
    public void saveMensajeria(MensajeIntranet mensajeria, CicloAcademico cicloAcademico, Usuario usuario) {
        mensajeria.setCicloAcademico(cicloAcademico);
        mensajeria.setUserRegistro(usuario);
        mensajeria.setFechaRegistro(new Date());
        mensajeIntranetDAO.save(mensajeria);

    }

    @Override
    @Transactional
    public void updateMensajeria(MensajeIntranet mensajeriaForm, CicloAcademico cicloAcademico, Usuario usuario) {
        MensajeIntranet mensajeria = mensajeIntranetDAO.find(mensajeriaForm);
        if (mensajeria == null) {
            throw new PhobosException("La mensajería que intenta editar no es correcta");
        }
        mensajeriaForm.setFechaRegistro(mensajeria.getFechaRegistro());
        mensajeriaForm.setUserRegistro(usuario);
        mensajeriaForm.setCicloAcademico(mensajeria.getCicloAcademico());
        mensajeIntranetDAO.update(mensajeriaForm);
    }

    @Override
    @Transactional
    public void eliminar(MensajeIntranet mensajeria) {
        mensajeIntranetDAO.delete(mensajeria);
    }

    @Override
    public MensajeIntranet findMensajeria(Long id) {
        return mensajeIntranetDAO.find(id);
    }

}
