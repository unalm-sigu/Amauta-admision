package pe.edu.lamolina.amauta.controller.academico.encuestaestudiantil.resumen;

import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaEstudiantil;

public interface EncuestaResumenService {

    public EncuestaEstudiantil findEncuestaCursoWithResumen(CicloAcademico cicloAcademico);

}
