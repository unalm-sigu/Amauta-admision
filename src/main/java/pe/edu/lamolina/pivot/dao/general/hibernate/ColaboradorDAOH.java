package pe.edu.lamolina.pivot.dao.general.hibernate;

import java.util.List;
import pe.edu.lamolina.pivot.dao.general.ColaboradorDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.Oficina;

@Repository
public class ColaboradorDAOH extends AbstractEasyDAO<Colaborador> implements ColaboradorDAO {

    public ColaboradorDAOH() {
        super();
        setClazz(Colaborador.class);
    }

    @Override
    public List<Colaborador> allColaborador(List<Oficina> oficinas) {
        Octavia sql = Octavia.query()
                .from(Colaborador.class, "co")
                .join("oficina ofi", "cargo car", "persona per")
                .in("ofi.id", oficinas);

        return all(sql);
    }

    @Override
    public List<Colaborador> allColaboradorByOficina(Oficina oficina) {
        Octavia sql = Octavia.query()
                .from(Colaborador.class, "co")
                .join("oficina ofi", "cargo car", "persona per")
                .filter("ofi.id", oficina);

        return all(sql);
    }
}
