package pe.edu.lamolina.pivot.dao.general.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.FuncionColaborador;
import pe.edu.lamolina.pivot.dao.general.FuncionColaboradorDAO;

@Repository
public class FuncionColaboradorDAOH extends AbstractEasyDAO<FuncionColaborador> implements FuncionColaboradorDAO {

    public FuncionColaboradorDAOH() {
        super();
        setClazz(FuncionColaborador.class);
    }

    @Override
    public List<FuncionColaborador> findFuncionByColaborador(Colaborador colaborador) {
       Octavia sql = Octavia.query()
                .from(FuncionColaborador.class, "co")
                .join("colaborador col")
                .filter("col.id", colaborador);

        return all(sql);
    }

 

}
