package pe.edu.lamolina.amauta.controller.consejeria.administracion;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.amauta.controller.consejeria.consejeros.Aconsejado;
import pe.edu.lamolina.amauta.controller.consejeria.consejeros.ConsejeroEstado;
import pe.edu.lamolina.amauta.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.consejeria.AlumnoConsejeroDAO;
import pe.edu.lamolina.amauta.dao.consejeria.ConsejeriaHistorialDAO;
import pe.edu.lamolina.amauta.dao.consejeria.ConsejeriaResumenDAO;
import pe.edu.lamolina.amauta.dao.consejeria.ConsejeroDAO;
import pe.edu.lamolina.amauta.dao.consejeria.ReunionAlumnoConsejeroDAO;
import pe.edu.lamolina.amauta.dao.consejeria.TutorSolicitudDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.model.consejeria.ConsejeriaHistorial;
import pe.edu.lamolina.model.consejeria.ConsejeriaHistorial.ConsejeriaHistorialEstado;
import pe.edu.lamolina.model.consejeria.ConsejeriaResumen;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import static pe.edu.lamolina.model.enums.EstadoEnum.ACT;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class AdministracionConsejeriaServiceImp implements AdministracionConsejeriaService {

    private final ConsejeriaHistorialDAO consejeriaHistorialDAO;
    private final CicloAcademicoDAO cicloAcademicoDAO;
    private final AlumnoConsejeroDAO alumnoConsejeroDAO;
    private final ConsejeriaResumenDAO consejeriaResumenDAO;
    private final ConsejeroDAO consejeroDAO;
    private final TutorSolicitudDAO tutorSolicitudDAO;
    private final ReunionAlumnoConsejeroDAO reunionAlumnoConsejeroDAO;

    @Override
    public List<ConsejeriaHistorial> allConsejeriaHistorialByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico) {

        return consejeriaHistorialDAO.allByDynatable(filter, cicloAcademico);
    }

    @Override
    public List<CicloAcademico> allCiclo() {
        return cicloAcademicoDAO.allPregradoByRangeCode(201510, 220000);
    }

    @Override
    @Transactional
    public void clonar(ClonarConsejerosDTO clonarDTO, DataSessionPivot ds) {
        int modeloCodigo = Integer.parseInt(clonarDTO.getModelo().getCodigo());
        int destinoCodigo = Integer.parseInt(clonarDTO.getDestino().getCodigo());
        log.debug("modeloCodigo {}", modeloCodigo);
        log.debug("destinoCodigo {}", destinoCodigo);
        if (modeloCodigo >= destinoCodigo) {
            throw new PhobosException("El ciclo modelo no puede ser mayor o igual al ciclo destino");
        }

        List<ConsejeriaResumen> resumenes = consejeriaResumenDAO.allByCiclo(clonarDTO.getModelo());

        consejeriaResumenDAO.deleteByCiclo(clonarDTO.getDestino());

        for (ConsejeriaResumen resumen : resumenes) {

            ConsejeriaResumen consejeriaResumen = new ConsejeriaResumen();
            consejeriaResumen.setCarrera(resumen.getCarrera());
            consejeriaResumen.setCicloAcademico(clonarDTO.getDestino());
            consejeriaResumen.setFechaActualizacion(new Date());
            consejeriaResumen.setFechaActualizacion(new Date());
            consejeriaResumenDAO.save(consejeriaResumen);

            List<AlumnoConsejero> alumnoConsejerosModelo = alumnoConsejeroDAO.allByCarreraCiclo(resumen.getCarrera(), clonarDTO.getModelo());
            List<AlumnoConsejero> alumnoConsejerosDestino = alumnoConsejeroDAO.allByCarreraCiclo(resumen.getCarrera(), clonarDTO.getDestino());

            Map<Long, AlumnoConsejero> alumnoConsejerosModeloMap = alumnoConsejerosModelo.stream().
                    collect(Collectors.toMap(x -> x.getAlumno().getId(), y -> y, (f, s) -> f));

            Map<Long, AlumnoConsejero> alumnoConsejerosDestinoMap = alumnoConsejerosDestino.stream().
                    collect(Collectors.toMap(x -> x.getAlumno().getId(), y -> y, (f, s) -> f));

            for (AlumnoConsejero alumnoConsejeroModelo : alumnoConsejerosModeloMap.values()) {

                AlumnoConsejero alumnoTutor = alumnoConsejerosDestinoMap.getOrDefault(alumnoConsejeroModelo.getAlumno().getId(), new AlumnoConsejero());

                if (alumnoTutor.getId() != null) {

                    if (alumnoTutor.getConsejero() == null) {
                        if (alumnoConsejeroModelo.getConsejero() != null) {
                            alumnoTutor.setConsejero(alumnoConsejeroModelo.getConsejero());
                        } else {
                            alumnoTutor.setConsejero(new Consejero(GlobalConstantine.ID_CONSEJERO_NN));
                        }
                    }

                    if (alumnoTutor.getConsejero().getId().longValue() == GlobalConstantine.ID_CONSEJERO_NN) {
                        if (alumnoConsejeroModelo.getConsejero() != null) {
                            alumnoTutor.setConsejero(alumnoConsejeroModelo.getConsejero());
                        }
                    }

                    alumnoTutor.setEstadoEnum(ACT);
                    alumnoTutor.setFechaAsigna(new Date());
                    alumnoTutor.setUserAsigna(ds.getUsuario());
                    alumnoConsejeroDAO.update(alumnoTutor);

                } else {

                    if (alumnoConsejeroModelo.getConsejero() != null) {
                        alumnoTutor.setConsejero(alumnoConsejeroModelo.getConsejero());
                    } else {
                        alumnoTutor.setConsejero(new Consejero(GlobalConstantine.ID_CONSEJERO_NN));
                    }

                    alumnoTutor.setAlumno(alumnoConsejeroModelo.getAlumno());
                    alumnoTutor.setCicloAcademico(clonarDTO.getDestino());
                    alumnoTutor.setEstadoEnum(ACT);
                    alumnoTutor.setFechaAsigna(new Date());
                    alumnoTutor.setUserAsigna(ds.getUsuario());
                    alumnoConsejeroDAO.save(alumnoTutor);
                }
            }

            Aconsejado aconsejadoMtbles = alumnoConsejeroDAO.countAconsejadosMatriculables(resumen.getCarrera(), clonarDTO.getDestino());
            aconsejadoMtbles = (aconsejadoMtbles == null) ? new Aconsejado() : aconsejadoMtbles;

            consejeriaResumen.setAconsejadosActivos(aconsejadoMtbles.getMatriculadosConConsejeros().intValue());
            consejeriaResumen.setAconsejadosInactivos(aconsejadoMtbles.getNoMatriculadosConConsejeros().intValue());
            consejeriaResumen.setSinconsejeroActivos(aconsejadoMtbles.getMatriculadosSinConsejeros().intValue());
            consejeriaResumen.setSinconsejeroInactivos(aconsejadoMtbles.getNoMatriculadosSinConsejeros().intValue());

            Aconsejado aconsejadoNoMtbles = alumnoConsejeroDAO.countAconsejadosNoMatriculables(resumen.getCarrera(), clonarDTO.getDestino());
            aconsejadoNoMtbles = (aconsejadoNoMtbles == null) ? new Aconsejado() : aconsejadoNoMtbles;
            consejeriaResumen.setInhabilitados(aconsejadoNoMtbles.getInhabilitados().intValue());

            ConsejeroEstado cont = consejeroDAO.countConsejerosByCarrera(resumen.getCarrera());
            cont = (cont == null) ? new ConsejeroEstado() : cont;
            resumen.setConsejerosActivos(cont.getActivos().intValue());
            consejeriaResumen.setConsejerosInactivos(cont.getInactivos().intValue());

            consejeriaResumenDAO.update(consejeriaResumen);

        }

        ConsejeriaHistorial consejeriaHistorial = new ConsejeriaHistorial();
        consejeriaHistorial.setCicloAcademico(clonarDTO.getDestino());
        consejeriaHistorial.setEstadoEnum(ConsejeriaHistorialEstado.ACTIVO);
        consejeriaHistorial.setFechaCreacion(new Date());
        consejeriaHistorial.setFechaActualizacion(new Date());
        consejeriaHistorialDAO.save(consejeriaHistorial);

    }

    @Override
    @Transactional
    public void eliminar(Long idConsejeriaHistorial, DataSessionPivot ds) {

        ConsejeriaHistorial consejeriaHistorial = consejeriaHistorialDAO.find(idConsejeriaHistorial);
        consejeriaHistorial.setEstadoEnum(ConsejeriaHistorialEstado.ELIMINADO);
        consejeriaHistorial.setFechaActualizacion(new Date());
        consejeriaHistorialDAO.update(consejeriaHistorial);
        
        reunionAlumnoConsejeroDAO.deleteByCiclo(consejeriaHistorial.getCicloAcademico());
        tutorSolicitudDAO.deleteByCiclo(consejeriaHistorial.getCicloAcademico());
        consejeriaResumenDAO.deleteByCiclo(consejeriaHistorial.getCicloAcademico());
        alumnoConsejeroDAO.deleteByCiclo(consejeriaHistorial.getCicloAcademico());
    }

}
