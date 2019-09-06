package pe.edu.lamolina.pivot.controller.general.responsableaula;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.ListsInspector;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.OficinaEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.ResponsableAula;
import pe.edu.lamolina.model.general.ResponsableAulaAsignacion;
import pe.edu.lamolina.model.general.TurnoAtencionAula;
import pe.edu.lamolina.pivot.dao.general.AulaDAO;
import pe.edu.lamolina.pivot.dao.general.PersonaDAO;
import pe.edu.lamolina.pivot.dao.general.ResponsableAulaAsignacionDAO;
import pe.edu.lamolina.pivot.dao.general.ResponsableAulaDAO;
import pe.edu.lamolina.pivot.dao.general.TurnoAtencionAaulaDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = false)
public class ResponsableAulaServiceImp implements ResponsableAulaService {

    @Autowired
    ResponsableAulaDAO responsableAulaDAO;

    @Autowired
    ResponsableAulaAsignacionDAO responsableAulaAsignacionDAO;

    @Autowired
    PersonaDAO personaDAO;

    @Autowired
    TurnoAtencionAaulaDAO turnoAtencionAaulaDAO;

    @Autowired
    AulaDAO aulaDAO;

    @Override
    public List<ResponsableAula> allResponsablesByRaptor(DynatableFilter filter, CicloAcademico cicloAcademico) {
        List<ResponsableAula> responsableAulas = responsableAulaDAO.allByResponsableAulas(filter, EstadoEnum.ACT);
        List<ResponsableAulaAsignacion> aulasAsignadas = responsableAulaAsignacionDAO.allByResponsable(responsableAulas, EstadoEnum.ACT);
        List<TurnoAtencionAula> turnoAtencionAulas = turnoAtencionAaulaDAO.all();

        Map<Long, List<ResponsableAulaAsignacion>> aulasByResponsable = TypesUtil.convertListToMapList("responsableAula.id", aulasAsignadas);

        for (ResponsableAula responsableAula : responsableAulas) {
            List<ResponsableAulaAsignacion> aulas = aulasByResponsable.get(responsableAula.getId());
            if (aulas == null) {
                aulas = new ArrayList<>();
            }
            List<TurnoAtencionAula> turnosClone = new ArrayList<>(turnoAtencionAulas);
            responsableAula.setTurnosAtencionAulas(new ArrayList<>());
            for (TurnoAtencionAula turno : turnosClone) {
                TurnoAtencionAula turnoClone = turno.clone();
                List<ResponsableAulaAsignacion> aulaByTurno = aulas.stream().filter(x -> x.getTurnoAtencionAula().equals(turno)).collect(Collectors.toList());
                aulaByTurno = aulaByTurno == null ? new ArrayList<>() : aulaByTurno;
                turnoClone.setAulas(aulaByTurno.stream().map(x -> x.getAula()).collect(Collectors.toList()));
                responsableAula.getTurnosAtencionAulas().add(turnoClone);
            }
        }

        return responsableAulas;
    }

    @Override
    public List<Persona> allPersonasByName(String nombre) {
        List<Persona> personas = personaDAO.allPersonaColaboradorByNombre(nombre, OficinaEnum.OERA, OficinaEnum.PAULA);
        return personas;
    }

    @Override
    public List<Aula> allAulasByName(String nombre) {
        List<Aula> aulas = aulaDAO.allAulasByName(nombre, 10000);
        return aulas;
    }

    @Override
    public List<TurnoAtencionAula> allTurnoAtenconAula() {
        return turnoAtencionAaulaDAO.all();
    }

    @Override
    @Transactional
    public void saveResponsableAula(ResponsableAula responsableAula, DataSessionPivot ds) {
        List<Aula> aulasForm = responsableAula.getTurnosAtencionAulas().stream()
                .map(per -> per.getAulas())
                .flatMap(aulas -> aulas.stream())
                .collect(Collectors.toList());

        List<ResponsableAulaAsignacion> responsablesAulasAsignacionBD = responsableAulaAsignacionDAO.allByAulas(aulasForm, EstadoEnum.ACT);
        responsablesAulasAsignacionBD.removeIf(x -> !x.getResponsableAula().getTipo().equals(responsableAula.getTipo()));

        List<String> errors = this.validarResponsablesAulas(responsableAula, responsablesAulasAsignacionBD);
        Assert.isTrue(errors.isEmpty(), String.join("</br>", errors));

        List<ResponsableAulaAsignacion> responsableAulaAssignsForm = new ArrayList<>();
        for (TurnoAtencionAula turnosAtencionAula : responsableAula.getTurnosAtencionAulas()) {
            for (Aula aula : turnosAtencionAula.getAulas()) {
                ResponsableAulaAsignacion responsableAulaAsign = new ResponsableAulaAsignacion();
                responsableAulaAsign.setResponsableAula(responsableAula);
                responsableAulaAsign.setAula(aula);
                responsableAulaAsign.setTurnoAtencionAula(turnosAtencionAula);
                responsableAulaAsign.setEstadoEnum(EstadoEnum.ACT);
                responsableAulaAsign.setFechaActualizacion(ds.getFechaAccionAudit());
                responsableAulaAsign.setFechaRegistro(ds.getFechaAccionAudit());
                responsableAulaAsign.setUserActualizacion(ds.getUsuario());
                responsableAulaAsign.setUserRegistro(ds.getUsuario());
                responsableAulaAssignsForm.add(responsableAulaAsign);
            }
        }

        if (responsableAula.getId() == null) {
            responsableAula.setEstadoEnum(EstadoEnum.ACT);
            responsableAula.setFechaActualizacion(ds.getFechaAccionAudit());
            responsableAula.setFechaRegistro(ds.getFechaAccionAudit());
            responsableAula.setUserActualizacion(ds.getUsuario());
            responsableAula.setUserRegistro(ds.getUsuario());
            responsableAulaDAO.save(responsableAula);
        } else {
            responsableAula.setFechaActualizacion(ds.getFechaAccionAudit());
            responsableAula.setUserActualizacion(ds.getUsuario());
            responsableAulaDAO.update(responsableAula);
        }

        List<ResponsableAulaAsignacion> responsableByPersonaBD = responsableAulaAsignacionDAO.allByResponsable(Arrays.asList(responsableAula), EstadoEnum.ACT);
        ListsInspector inspector = TypesUtil.analizeLists(responsableByPersonaBD, responsableAulaAssignsForm, "perAulTur");
        List<ResponsableAulaAsignacion> muertos = inspector.getDeadList();
        List<ResponsableAulaAsignacion> nuevos = inspector.getNewList();

        for (ResponsableAulaAsignacion responsableAulaAsignacion : muertos) {
            responsableAulaAsignacionDAO.delete(responsableAulaAsignacion);
        }
        for (ResponsableAulaAsignacion responsableAulaAsignacion : nuevos) {
            responsableAulaAsignacionDAO.save(responsableAulaAsignacion);
        }
    }

    public List<String> validarResponsablesAulas(ResponsableAula responsableAula, List<ResponsableAulaAsignacion> responsablesAulasBD) {
        List<String> errors = new ArrayList<>();
        responsablesAulasBD.removeIf(x -> x.getResponsableAula().equals(responsableAula));

        for (TurnoAtencionAula turnosAtencionAula : responsableAula.getTurnosAtencionAulas()) {
            List<ResponsableAulaAsignacion> responsablesAulasByTurnoBD = responsablesAulasBD.stream()
                    .filter(x -> x.getTurnoAtencionAula().equals(turnosAtencionAula))
                    .collect(Collectors.toList());
            for (Aula aula : turnosAtencionAula.getAulas()) {
                ResponsableAulaAsignacion responsableAulaAsignacion = responsablesAulasByTurnoBD.stream()
                        .filter(x -> x.getAula().equals(aula)).findFirst().orElse(null);
                if (responsableAulaAsignacion != null) {
                    String error = String.format("El %s del aula %s en el turno %s es %s",
                            responsableAulaAsignacion.getResponsableAula().getTipoEnum().getValue(),
                            aula.getCodigo(), turnosAtencionAula.getDescripcion(), responsableAulaAsignacion.
                            getResponsableAula().getPersona().getApellidosNombres());
                    errors.add(error);
                }
            }
        }
        return errors;
    }

    @Override
    public ResponsableAula findResponsableAula(ResponsableAula responsableAula, DataSessionPivot ds) {
        responsableAula = responsableAulaDAO.findByPersonaAndTipo(responsableAula.getPersona(), responsableAula.getTipoEnum(), EstadoEnum.ACT);
        List<TurnoAtencionAula> turnoAtencionAulas = turnoAtencionAaulaDAO.all();
        if (responsableAula != null) {
            List<ResponsableAulaAsignacion> responsablesAulasAsignacion = responsableAulaAsignacionDAO.allByResponsable(Arrays.asList(responsableAula), EstadoEnum.ACT);

            for (TurnoAtencionAula turnoAtencionAula : turnoAtencionAulas) {
                List<ResponsableAulaAsignacion> responsables = responsablesAulasAsignacion.stream()
                        .filter(x -> x.getTurnoAtencionAula().equals(turnoAtencionAula))
                        .collect(Collectors.toList());
                responsables = responsables == null ? new ArrayList<>() : responsables;
                turnoAtencionAula.setAulas(responsables.stream().map(x -> x.getAula()).collect(Collectors.toList()));
            }
        }
        responsableAula = responsableAula == null ? new ResponsableAula() : responsableAula;
        responsableAula.setTurnosAtencionAulas(turnoAtencionAulas);

        return responsableAula;
    }
}
