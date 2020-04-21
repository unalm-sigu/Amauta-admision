package pe.edu.lamolina.amauta.dao.aporte.hibernate;

import java.util.List;
import java.util.stream.Collectors;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.edu.lamolina.model.aporte.Aporte;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.aporte.AporteCiclo;
import pe.edu.lamolina.model.enums.AportesEnum;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.amauta.dao.aporte.AporteDAO;

@Repository
public class AporteDAOH extends AbstractEasyDAO<Aporte> implements AporteDAO {

    public AporteDAOH() {
        super();
        setClazz(Aporte.class);
    }

    @Override
    public List<Aporte> allAporte() {
        Octavia sql = Octavia.query()
                .from(Aporte.class, "apo")
                .join("modalidadEstudio me")
                .orderBy("apo.nombre asc");
        return all(sql);
    }

    @Override
    public List<Aporte> allActivoByModalidadEstudio(ModalidadEstudio modalidadEstudio) {
        Octavia sql = Octavia.query()
                .from(Aporte.class, "apo")
                .join("modalidadEstudio me")
                .filter("modalidadEstudio", modalidadEstudio)
                .filter("estado", EstadoEnum.ACT.name());

        return all(sql);
    }

    @Override
    public List<Aporte> allByNombre(List<String> aporteName) {
        Octavia sql = Octavia.query()
                .from(Aporte.class, "apo")
                .join("modalidadEstudio me")
                .in("apo.nombre", aporteName)
                .orderBy("apo.nombre asc");
        return all(sql);
    }

    @Override
    public Aporte findByNombre(String aporteName) {
        Octavia sql = Octavia.query()
                .from(Aporte.class, "apo")
                .join("modalidadEstudio me")
                .filter("apo.nombre", aporteName);
        return find(sql);
    }

    @Override
    public List<Aporte> allByDynatable(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(Aporte.class, "apo")
                .join("modalidadEstudio mo")
                .searchFields("apo.nombre", "mo.nombre")
                .orderBy("mo.id", "apo.nombre asc");
        return all(sql);
    }

    @Override
    public List<Aporte> allByCodigoCicloAcademico(String codigoCicloAcademico) {
        Octavia sql = Octavia.query()
                .selectDistinct("ac.aporte")
                .from(AporteCiclo.class, "ac")
                .join("cicloAcademico ca", "aporte ap", "cuentaBancaria cb", "ap.modalidadEstudio")
                .filter("ca.codigo", codigoCicloAcademico)
                .orderBy("ap.nombre asc");

        return all(sql);

    }

    @Override
    public Aporte findMaximoCodigo() {
        Octavia sql = Octavia.query()
                .from(Aporte.class, "apo")
                .orderBy("apo.codigo desc")
                .limit(1);
        return find(sql);
    }

    @Override
    public Aporte findByCode(AportesEnum codeEnum) {
        Octavia sql = Octavia.query()
                .from(Aporte.class, "apo")
                .filter("apo.codigo", codeEnum.name().substring(1));
        return find(sql);
    }

    @Override
    public List<Aporte> allByCodesEnum(List<AportesEnum> codesEnum) {
        List<String> codes = codesEnum.stream().map(x -> x.name().substring(1)).collect(Collectors.toList());
        Octavia sql = Octavia.query()
                .from(Aporte.class, "apo")
                .in("apo.codigo", codes);

        return all(sql);
    }

}
