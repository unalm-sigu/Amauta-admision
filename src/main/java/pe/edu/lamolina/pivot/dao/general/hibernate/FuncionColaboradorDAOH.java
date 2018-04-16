package pe.edu.lamolina.pivot.dao.general.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.FuncionColaborador;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.pivot.dao.general.FuncionColaboradorDAO;

@Repository
public class FuncionColaboradorDAOH extends AbstractEasyDAO<FuncionColaborador> implements FuncionColaboradorDAO {

    public FuncionColaboradorDAOH() {
        super();
        setClazz(FuncionColaborador.class);
    }

    @Override
    public List<FuncionColaborador> findFuncionByColaborador() {
        Octavia sql = Octavia.query()
                .from(FuncionColaborador.class, "co")
                .join("colaborador col")
                .filter("estado", EstadoEnum.ACT)
                .orderBy("col.id");

        return all(sql);
    }

    @Override
    public List<FuncionColaborador> findFuncionByColaborador(Colaborador colaborador) {
        Octavia sql = Octavia.query()
                .from(FuncionColaborador.class, "fc")
                .join("colaborador col", "funcion fun")
                .filter("col.id", colaborador)
                .filter("estado", EstadoEnum.ACT)
                .orderBy("fc.id");

        return all(sql);
    }

 

}
