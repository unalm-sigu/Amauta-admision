package pe.edu.lamolina.amauta.controller.matricula.tutorsolicitud;

import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.dao.consejeria.AlumnoConsejeroDAO;
import pe.edu.lamolina.amauta.dao.consejeria.TutorSolicitudDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.model.consejeria.TutorSolicitud;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.TutorSolicitudEnum;
import pe.edu.lamolina.model.seguridad.Usuario;

@Service
@Transactional(readOnly = true)
public class TutorSolicitudServiceImp implements TutorSolicitudService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TutorSolicitudDAO tutorSolicitudDAO;

    @Autowired
    AlumnoConsejeroDAO alumnoConsejeroDAO;

    @Override
    public List<TutorSolicitud> allTutorSolicitudByFilter(DynatableFilter filter, CicloAcademico ciclo) {
        return tutorSolicitudDAO.allTutorSolicitudByFilter(filter, ciclo);
    }

    @Override
    @Transactional
    public void updateEstado(Long idAlumnoConsejero, String estado, Usuario usuario) {
        TutorSolicitud tutorSolicitud = tutorSolicitudDAO.find(idAlumnoConsejero);
        tutorSolicitud.setEstado(estado);
        tutorSolicitud.setUsuarioVerifica(usuario);
        tutorSolicitud.setFechaVerifica(new Date());
        tutorSolicitudDAO.update(tutorSolicitud);

    }

    @Override
    @Transactional
    public void solicitudBeneficio(AlumnoConsejero alumnoConsejero, DataSessionPivot ds) {

        TutorSolicitud solicitud = new TutorSolicitud();
        solicitud.setAlumnoConsejero(alumnoConsejero);
        solicitud.setUsuarioRegistra(ds.getUsuario());
        solicitud.setFechaRegistro(new Date());
        solicitud.setEstadoEnum(TutorSolicitudEnum.PEN);
        solicitud.setTipoSolicitud("BULT");//BULT(Beneficio de Ultimo ciclo)por si necesitan otra solicitud se crearia el enum
        tutorSolicitudDAO.save(solicitud);

        AlumnoConsejero aluConsejero = alumnoConsejeroDAO.find(alumnoConsejero.getId());
        aluConsejero.setBeneficioUtlimoCiclo(1);
        alumnoConsejeroDAO.update(aluConsejero);

    }

}
