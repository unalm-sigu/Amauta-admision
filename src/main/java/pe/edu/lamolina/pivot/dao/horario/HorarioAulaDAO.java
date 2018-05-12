package pe.edu.lamolina.pivot.dao.horario;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioAula;

public interface HorarioAulaDAO extends EasyDAO<HorarioAula> {

    List<HorarioAula> allHorarioAula();

    List<HorarioAula> allByAula(Aula aula, CicloAcademico cicloAcademico);

    List<HorarioAula> allBySeccionAula(Seccion seccion, Aula aula);

    List<HorarioAula> allBySeccionCiclo(Seccion seccion, CicloAcademico cicloAcademico);

    List<HorarioAula> allByAulaCicloDiasHoras(Aula aula, CicloAcademico cicloAcademico, List<Dia> dias, List<Hora> horas);

    void deleteBySeccionAula(Seccion seccion, Aula aula);

    void deleteBySeccionDiaHoraAula(Seccion seccion, Dia dia, Hora hora, Aula aula);

    List<HorarioAula> allByAulaCiclo(Aula aula, CicloAcademico cicloAcademico);

    List<HorarioAula> allByCiclo(CicloAcademico cicloAcademico);

    void deleteAllInList(List<HorarioAula> muertos);

}
