package pe.edu.lamolina.pivot.controller.general.responsableaula;

import java.util.ArrayList;
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
import pe.edu.lamolina.model.general.TurnoAtencionAula;
import pe.edu.lamolina.pivot.dao.general.AulaDAO;
import pe.edu.lamolina.pivot.dao.general.PersonaDAO;
import pe.edu.lamolina.pivot.dao.general.ResponsableAulaDAO;
import pe.edu.lamolina.pivot.dao.general.TurnoAtencionAaulaDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = false)
public class ResponsableAulaServiceImp implements ResponsableAulaService {

    @Autowired
    ResponsableAulaDAO responsableAulaDAO;

    @Autowired
    PersonaDAO personaDAO;

    @Autowired
    TurnoAtencionAaulaDAO turnoAtencionAaulaDAO;

    @Autowired
    AulaDAO aulaDAO;

    @Override
    public List<Persona> allResponsablesByRaptor(DynatableFilter filter, CicloAcademico cicloAcademico) {
        List<Persona> personasResponsables = personaDAO.allResponsableAulas(filter, EstadoEnum.ACT);
        List<ResponsableAula> responsableAulas = responsableAulaDAO.allByPersona(personasResponsables, EstadoEnum.ACT);
        List<TurnoAtencionAula> turnoAtencionAulas = turnoAtencionAaulaDAO.all();

        Map<Long, List<ResponsableAula>> responsablesByPersona = TypesUtil.convertListToMapList("persona.id", responsableAulas);

        for (Persona personasResponsable : personasResponsables) {
            List<ResponsableAula> aulas = responsablesByPersona.get(personasResponsable.getId());
            List<TurnoAtencionAula> turnosClone = new ArrayList<>(turnoAtencionAulas);
            personasResponsable.setTurnosAtencionAulas(new ArrayList<>());
            for (TurnoAtencionAula turno : turnosClone) {
                TurnoAtencionAula turnoClone = turno.clone();
                List<ResponsableAula> aulaByTurno = aulas.stream().filter(x -> x.getTurnoAtencionAula().equals(turno)).collect(Collectors.toList());
                aulaByTurno = aulaByTurno == null ? new ArrayList<>() : aulaByTurno;
                turnoClone.setAulas(aulaByTurno.stream().map(x -> x.getAula()).collect(Collectors.toList()));
                personasResponsable.getTurnosAtencionAulas().add(turnoClone);
            }
        }

        return personasResponsables;
    }

    @Override
    public List<Persona> allPersonasByName(String nombre) {
        List<Persona> personas = personaDAO.allPersonaColaboradorByNombre(nombre, OficinaEnum.PAULA);
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
    public void saveResponsableAula(Persona personaResponsable, DataSessionPivot ds) {
        List<Aula> aulasForm = personaResponsable.getTurnosAtencionAulas().stream()
                .map(per -> per.getAulas())
                .flatMap(aulas -> aulas.stream())
                .collect(Collectors.toList());

        List<ResponsableAula> responsablesByAulasBD = responsableAulaDAO.allByAulas(aulasForm, EstadoEnum.ACT);
        List<String> errors = this.validarResponsablesAulas(personaResponsable, responsablesByAulasBD);
        Assert.isTrue(errors.isEmpty(), String.join("</br>", errors));

        List<ResponsableAula> responsableAulasForm = new ArrayList<>();
        for (TurnoAtencionAula turnosAtencionAula : personaResponsable.getTurnosAtencionAulas()) {
            for (Aula aula : turnosAtencionAula.getAulas()) {
                ResponsableAula responsableAula = new ResponsableAula();
                responsableAula.setAula(aula);
                responsableAula.setEstadoEnum(EstadoEnum.ACT);
                responsableAula.setFechaActualizacion(ds.getFechaAccionAudit());
                responsableAula.setFechaRegistro(ds.getFechaAccionAudit());
                responsableAula.setPersona(personaResponsable);
                responsableAula.setTurnoAtencionAula(turnosAtencionAula);
                responsableAula.setUserActualizacion(ds.getUsuario());
                responsableAula.setUserRegistro(ds.getUsuario());
                responsableAulasForm.add(responsableAula);
            }
        }

        List<ResponsableAula> responsableByPersonaBD = responsableAulaDAO.allByPersona(personaResponsable, EstadoEnum.ACT);
        ListsInspector inspector = TypesUtil.analizeLists(responsableByPersonaBD, responsableAulasForm, "perAulTur");
        List<ResponsableAula> muertos = inspector.getDeadList();
        List<ResponsableAula> nuevos = inspector.getNewList();

        for (ResponsableAula responsableAula : muertos) {
            responsableAulaDAO.delete(responsableAula);
        }
        for (ResponsableAula responsableAula : nuevos) {
            responsableAulaDAO.save(responsableAula);
        }
    }

    public List<String> validarResponsablesAulas(Persona personaResponsable, List<ResponsableAula> responsablesAulasBD) {
        List<String> errors = new ArrayList<>();
        responsablesAulasBD.removeIf(x -> x.getPersona().equals(personaResponsable));

        for (TurnoAtencionAula turnosAtencionAula : personaResponsable.getTurnosAtencionAulas()) {
            List<ResponsableAula> responsablesAulasByTurnoBD = responsablesAulasBD.stream()
                    .filter(x -> x.getTurnoAtencionAula().equals(turnosAtencionAula))
                    .collect(Collectors.toList());
            for (Aula aula : turnosAtencionAula.getAulas()) {
                ResponsableAula responsableAula = responsablesAulasByTurnoBD.stream().filter(x -> x.getAula().equals(aula)).findFirst().orElse(null);
                if (responsableAula != null) {
                    String error = String.format("El responsable del aula %s en el turno %s es %s",
                            aula.getCodigo(), turnosAtencionAula.getDescripcion(), responsableAula.getPersona().getApellidosNombres());
                    errors.add(error);
                }
            }
        }
        return errors;
    }

    @Override
    public Persona findResponsableAula(Persona personaResponsable, DataSessionPivot ds) {
        personaResponsable = personaDAO.find(personaResponsable.getId());
        List<TurnoAtencionAula> turnoAtencionAulas = turnoAtencionAaulaDAO.all();
        List<ResponsableAula> responsableAulas = responsableAulaDAO.allByPersona(personaResponsable, EstadoEnum.ACT);
        for (TurnoAtencionAula turnoAtencionAula : turnoAtencionAulas) {
            List<ResponsableAula> responsables = responsableAulas.stream()
                    .filter(x -> x.getTurnoAtencionAula().equals(turnoAtencionAula))
                    .collect(Collectors.toList());
            responsables = responsables == null ? new ArrayList<>() : responsables;
            turnoAtencionAula.setAulas(responsables.stream().map(x -> x.getAula()).collect(Collectors.toList()));
        }
        personaResponsable.setTurnosAtencionAulas(turnoAtencionAulas);
        return personaResponsable;
    }

}
