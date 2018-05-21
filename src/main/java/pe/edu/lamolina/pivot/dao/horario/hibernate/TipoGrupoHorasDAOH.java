package pe.edu.lamolina.pivot.dao.horario.hibernate;

import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.TipoCicloEnum;
import pe.edu.lamolina.model.enums.TipoGrupoHorasEnum;
import pe.edu.lamolina.model.horario.TipoGrupoHoras;
import pe.edu.lamolina.pivot.dao.horario.TipoGrupoHorasDAO;

@Repository
public class TipoGrupoHorasDAOH extends AbstractEasyDAO<TipoGrupoHoras> implements TipoGrupoHorasDAO {

    public TipoGrupoHorasDAOH() {
        super();
        setClazz(TipoGrupoHoras.class);
    }

    @Override
    public List<TipoGrupoHoras> allTipoGrupoHoras(DynatableFilter filter) {

        DynatableSql sql = new DynatableSql(filter)
                .from(TipoGrupoHoras.class, "tgh")
                .searchFields("codigo", "tipo", "estadoGrupos", "estado")
                .orderBy("tgh.id desc");

        return all(sql);
    }

    @Override
    public TipoGrupoHoras findByCode(String codigo) {
        Octavia sql = Octavia.query()
                .from(TipoGrupoHoras.class, "tipo")
                .filter("codigo", codigo);

        return find(sql);
    }

    @Override
    public TipoGrupoHoras findByTipo(TipoGrupoHorasEnum tipoGrupoHorasEnum) {
        Octavia sql = Octavia.query()
                .from(TipoGrupoHoras.class, "tg")
                .filter("tipo", tipoGrupoHorasEnum.getValue())
                .filter("estado", EstadoEnum.ACT.name());

        return find(sql);
    }

    @Override
    public TipoGrupoHoras findByTipoCiclo(TipoGrupoHorasEnum tipoGrupoHorasEnum, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(TipoGrupoHoras.class, "tg")
                .filter("tipo", tipoGrupoHorasEnum)
                .filter("estado", EstadoEnum.ACT)
                .in("tipoCiclo", Arrays.asList(cicloAcademico.getTipo(), TipoCicloEnum.AMB.name()));

        return find(sql);
    }

    @Override
    public List<TipoGrupoHoras> allActiveByTipoCiclo(CicloAcademico cicloAcademico, TipoGrupoHorasEnum tipoGrupoHorasEnum) {
        Octavia sql = Octavia.query()
                .from(TipoGrupoHoras.class, "tipoGH")
                .filter("estado", EstadoEnum.ACT.name())
                .filter("tipoCiclo", cicloAcademico.getTipo())
                .filter("tipo", tipoGrupoHorasEnum.name());

        return all(sql);
    }

}
