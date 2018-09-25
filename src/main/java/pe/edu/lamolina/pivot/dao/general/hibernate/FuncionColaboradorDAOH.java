package pe.edu.lamolina.pivot.dao.general.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.enums.EstadoEnum;
import static pe.edu.lamolina.model.enums.EstadoEnum.ACT;
import static pe.edu.lamolina.model.enums.PerfilColaboradorEnum.EDITPROG;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.FuncionColaborador;
import pe.edu.lamolina.model.permisoprogramacion.ColaboradorAnexo;
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

    @Override
    public List<FuncionColaborador> allByColaborador(Colaborador colaborador) {
        Octavia sql = Octavia.query()
                .from(FuncionColaborador.class, "fc")
                .join("colaborador col", "funcion fun")
                .filter("col.id", colaborador)
                .filter("estado", EstadoEnum.ACT)
                .orderBy("fc.id");

        return all(sql);
    }

    @Override
    public List<FuncionColaborador> allColaboradorEditor(DynatableFilter filter) {
        Octavia subquery = new Octavia()
                .from(ColaboradorAnexo.class, "ca")
                .join("colaborador cl", "anexoBoletin ab")
                .filter("estado", ACT);

        DynatableSql sql = new DynatableSql(filter)
                .from(FuncionColaborador.class, "fc")
                .join("colaborador col", "funcion fun", "col.persona per", "per.tipoDocumento")
                .searchFields("per.numeroDocIdentidad", "alum.codigo")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .searchSubquery(subquery)
                .subqueryLinkedBy("col.id", "cl.id")
                .searchSubqueryFields("ab.nombre")
                .filter("estado", EstadoEnum.ACT)
                .filter("fun.codigo", EDITPROG)
                .orderBy("per.paterno");

        return all(sql);
    }

}
