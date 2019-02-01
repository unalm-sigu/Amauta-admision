package pe.edu.lamolina.pivot.dao.general.hibernate;

import java.util.Arrays;
import java.util.List;
import pe.edu.lamolina.pivot.dao.general.ColaboradorDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import static pe.edu.lamolina.model.enums.ColaboradorEstadoEnum.ACT;
import static pe.edu.lamolina.model.enums.ColaboradorEstadoEnum.DSC;
import static pe.edu.lamolina.model.enums.ColaboradorEstadoEnum.PER;
import static pe.edu.lamolina.model.enums.ColaboradorEstadoEnum.VAC;
import pe.edu.lamolina.model.enums.PerfilColaboradorEnum;
import pe.edu.lamolina.model.enums.PersonaEstadoEnum;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
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
    public List<Colaborador> allByOficinas(List<Oficina> oficinas) {
        Octavia sql = Octavia.query()
                .from(Colaborador.class, "co")
                .join("oficina ofi", "cargo car", "persona per")
                .in("ofi.id", oficinas);

        return all(sql);
    }

    @Override
    public List<Colaborador> allByOficina(Oficina oficina) {
        Octavia sql = Octavia.query()
                .from(Colaborador.class, "co")
                .join("oficina ofi", "cargo car", "persona per")
                .filter("ofi.id", oficina);

        return all(sql);
    }

    @Override
    public List<Colaborador> allActivosByPersona(Persona persona) {
        Octavia sql = Octavia.query()
                .from(Colaborador.class, "co")
                .join("persona per")
                .filter("per.id", persona)
                .in("co.estado", Arrays.asList(ACT, VAC, DSC, PER));

        return all(sql);
    }

    @Override
    public List<Colaborador> allActivosByOficina(Oficina oficina) {
        Octavia sql = Octavia.query()
                .from(Colaborador.class, "co")
                .join("oficina ofi", "cargo car", "persona per")
                .filter("ofi.id", oficina)
                .in("co.estado", Arrays.asList(ACT, VAC, DSC, PER));

        return all(sql);
    }

    @Override
    public Colaborador findActivoByPersonaOficina(Oficina oficina, Persona persona) {
        Octavia sql = Octavia.query()
                .from(Colaborador.class, "co")
                .join("persona per", "oficina ofi")
                .filter("per.id", persona)
                .filter("ofi.id", oficina)
                .in("co.estado", Arrays.asList(ACT, VAC, DSC, PER));

        return find(sql);
    }

    @Override
    public Colaboradores countByOficinas(List<Oficina> oficinas) {
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
                .in("ofi.id", oficinas);

        return (Colaboradores) sql.find(getCurrentSession());
    }

    @Override
    public List<Colaborador> allDynatableByOficina(DynatableFilter filter, List<Oficina> oficinas) {
        DynatableSql sql = new DynatableSql(filter)
                .from(Colaborador.class, "co")
                .join("persona per", "oficina ofi")
                .in("ofi.id", oficinas)
                .searchFields("ofi.nombre", "co.estado")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("co.id desc");
        return all(sql);
    }

    @Override
    public Colaborador findMaxCodigo() {
        Octavia sql = Octavia.query()
                .from(Colaborador.class, "co")
                .orderBy("co.codigo desc")
                .limit(1);

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

    @Override
    public List<Colaborador> allByName(String nombre) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        Octavia sql = Octavia.query()
                .from(Colaborador.class, "cola")
                .join("persona per", "oficina ofi", "cargo car")
                .leftJoin("per.tipoDocumento td")
                .filter("per.estado", PersonaEstadoEnum.ACT)
                .beginBlock()
                .__().complexFilter("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))", "like", nombre)
                .__().complexFilter("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))", "like", nombre)
                .__().filter("per.numeroDocIdentidad", "like", nombre)
                .__().filter("cola.codigo", "like", nombre)
                .endBlock()
                .limit(15);
        return sql.all(getCurrentSession());
    }

    @Override
    public Colaborador findByPersonaAndEstado(Persona persona) {
        Octavia sql = Octavia.query()
                .from(Colaborador.class, "co")
                .join("persona per")
                .filter("per.id", persona)
                .in("co.estado", Arrays.asList(ACT, PER, VAC, DSC));
        return find(sql);
    }

    @Override
    public Colaborador findColaboradorByIdPersona(Long idPersona) {
        Octavia sql = Octavia.query()
                .from(Colaborador.class, "co")
                .join("persona per", "oficina ofi", "ofi.tipoOficina tip", "cargo carg")
                .filter("carg.codigo", PerfilColaboradorEnum.DOC) //Docente
                .filter("tip.codigo", TipoOficinaEnum.DPTO) //departamentoAcdemico
                .filter("per.id", idPersona);

        return find(sql);
    }

}
