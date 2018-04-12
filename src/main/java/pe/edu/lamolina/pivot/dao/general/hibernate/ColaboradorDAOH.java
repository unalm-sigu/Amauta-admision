package pe.edu.lamolina.pivot.dao.general.hibernate;

import java.util.List;
import pe.edu.lamolina.pivot.dao.general.ColaboradorDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.pivot.controller.general.oficina.Colaboradores;

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

    @Override
    public List<Colaborador> allByPersona(Persona persona) {
        Octavia sql = Octavia.query()
                .from(Colaborador.class, "co")
                .join("persona per")
                .filter("per.id", persona);

        return all(sql);
    }

    @Override
    public Colaboradores countColaboradores(Oficina oficina) {
        Octavia sql = Octavia.query()
                .select("sum(case co.estado when 'ACT' then 1 else 0 end)",
                        "sum(case co.estado when 'VAC' then 1 else 0 end)",
                        "sum(case co.estado when 'RET' then 1 else 0 end)",
                        "sum(case co.estado when 'DSC' then 1 else 0 end)",
                        "sum(case co.estado when 'PER' then 1 else 0 end)",
                        "sum(case co.estado when 'DESP' then 1 else 0 end)"
                )
                .into(Colaboradores.class)
                .from(Colaborador.class, "co")
                .join("oficina ofi")
                .filter("ofi.id", oficina);

        return (Colaboradores) sql.find(getCurrentSession());
    }

    @Override
    public List<Colaborador> allByOficina(DynatableFilter filter, List<Oficina> oficinas) {
        DynatableSql sql = new DynatableSql(filter)
                .from(Colaborador.class, "co")
                .join("persona per", "oficina ofi")
                .in("ofi.id", oficinas)
                .searchFields("ofi.nombre", "co.estado")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))");
        return all(sql);
    }

    @Override
    public Colaborador findCodigo() {
        Octavia sql = Octavia.query()
                .from(Colaborador.class, "co")
                .limit(1)
                .orderBy("co.codigo desc");

        return (Colaborador) sql.find(getCurrentSession());
    }

    @Override
    public Colaborador find(Colaborador colaborador) {
        Octavia sql = Octavia.query()
                .from(Colaborador.class, "co")
                .join("persona per", "per.tipoDocumento")
                .filter("co.id", colaborador);

        return find(sql);
    }
}
