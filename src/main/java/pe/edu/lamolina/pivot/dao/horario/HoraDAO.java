package pe.edu.lamolina.pivot.dao.horario;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.horario.Hora;

public interface HoraDAO extends Crud<Hora> {

    public List<Hora> allHora();

}

