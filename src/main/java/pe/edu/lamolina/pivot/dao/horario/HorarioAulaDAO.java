package pe.edu.lamolina.pivot.dao.horario;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.Seccion;
import pe.edu.lamolina.pivot.model.general.Aula;
import pe.edu.lamolina.pivot.model.general.Dia;
import pe.edu.lamolina.pivot.model.horario.Hora;
import pe.edu.lamolina.pivot.model.horario.HorarioAula;

public interface HorarioAulaDAO extends Crud<HorarioAula> {

    public List<HorarioAula> allHorarioAula();

    List<HorarioAula> allByAula(Aula aula, CicloAcademico cicloAcademico);

    List<HorarioAula> allBySeccionAula(Seccion seccion, Aula aula);

    List<HorarioAula> allByAulaCicloDiasHoras(Aula aula, CicloAcademico cicloAcademico, List<Dia> dias, List<Hora> horas);

    void deleteBySeccionAula(Seccion seccion, Aula aula);

    List<HorarioAula> allByAulaCiclo(Aula aula, CicloAcademico cicloAcademico);

}
