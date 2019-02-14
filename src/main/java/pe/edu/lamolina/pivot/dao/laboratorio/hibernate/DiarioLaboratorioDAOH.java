package pe.edu.lamolina.pivot.dao.laboratorio.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.medico.DiarioLaboratorio;
import pe.edu.lamolina.pivot.dao.laboratorio.DiarioLaboratorioDAO;

@Repository
public class DiarioLaboratorioDAOH extends AbstractEasyDAO<DiarioLaboratorio> implements DiarioLaboratorioDAO {

    public DiarioLaboratorioDAOH() {
        super();
        setClazz(DiarioLaboratorio.class);
    }

    @Override
    public List<DiarioLaboratorio> allFechaDesc() {
        Octavia sql = Octavia.query()
                .from(DiarioLaboratorio.class, "dl")
                .orderBy("dl.fecha desc");
        return all(sql);    
    }

}
