package pe.edu.lamolina.pivot.dao.academico;

import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.academico.AlumnoVisitante;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.general.Persona;

public interface AlumnoVisitanteDAO extends Crud<AlumnoVisitante> {

    public AlumnoVisitante findByPersona(Persona persona);

    public AlumnoVisitante findByPersona(Persona persona, CicloAcademico cicloAcademico);

}
