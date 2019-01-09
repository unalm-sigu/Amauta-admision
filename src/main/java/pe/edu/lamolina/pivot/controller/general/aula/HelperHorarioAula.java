package pe.edu.lamolina.pivot.controller.general.aula;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioAula;
import pe.edu.lamolina.pivot.dao.horario.HorarioAulaDAO;

public class HelperHorarioAula {

    @Autowired
    HorarioAulaDAO horarioAulaDAO;

    public String getCodigoSeccion(long idDia, long idHora, List<HorarioAula> horario) {
        for (HorarioAula horarioAula : horario) {
            Dia dia = horarioAula.getDia();
            Hora hora = horarioAula.getHora();

            if (!(idDia == dia.getId() && idHora == hora.getId())) {
                continue;
            }
            return horarioAula.getSeccion().getCodigo2();
        }
        return null;
    }

    public String getCodigoGrupoHora(long idDia, long idHora, List<HorarioAula> horario) {
        for (HorarioAula horarioAula : horario) {
            Dia dia = horarioAula.getDia();
            Hora hora = horarioAula.getHora();

            if (!(idDia == dia.getId() && idHora == hora.getId())) {
                continue;
            }
            Seccion seccion = horarioAula.getSeccion();
            if (seccion.getGrupoHoras() == null) {
                return null;
            }
            return horarioAula.getSeccion().getGrupoHoras().getCodigo();
        }
        
        return null;
    }
    
    
    public Seccion getSeccion(long idDia, long idHora, List<HorarioAula> horario) {
        for (HorarioAula horarioAula : horario) {
            Dia dia = horarioAula.getDia();
            Hora hora = horarioAula.getHora();

            if (!(idDia == dia.getId() && idHora == hora.getId())) {
                continue;
            }
            return horarioAula.getSeccion();
        }
        return null;
    }

}
