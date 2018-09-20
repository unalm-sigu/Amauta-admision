package pe.edu.lamolina.pivot.dao.permisoprogramacion.hibernate;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import static pe.edu.lamolina.model.enums.EstadoEnum.ACT;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.permisoprogramacion.ColaboradorAnexo;
import pe.edu.lamolina.pivot.dao.permisoprogramacion.ColaboradorAnexoDAO;

@Repository
public class ColaboradorAnexoDAOH extends AbstractEasyDAO<ColaboradorAnexo> implements ColaboradorAnexoDAO {

    public ColaboradorAnexoDAOH() {
        super();
        setClazz(ColaboradorAnexo.class);
    }

    @Override
    public List<ColaboradorAnexo> allByColaboradores(ArrayList<Colaborador> colaboradores) {
        Octavia sql = new Octavia()
                .from(ColaboradorAnexo.class,"ca")
                .join("colaborador col","anexoBoletin ab")
                .filter("estado", ACT)
                .in("col.id", colaboradores);
        return all(sql);
    }

}
