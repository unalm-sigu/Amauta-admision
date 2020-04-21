package pe.edu.lamolina.amauta.dao.horario.hibernate;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.horario.GrupoHoras;
import pe.edu.lamolina.model.horario.GrupoHorasExcluido;
import pe.edu.lamolina.model.horario.TipoGrupoHoras;
import pe.edu.lamolina.amauta.dao.horario.GrupoHorasExcluidoDAO;

@Repository
public class GrupoHorasExcluidoDAOH extends AbstractEasyDAO<GrupoHorasExcluido> implements GrupoHorasExcluidoDAO {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public GrupoHorasExcluidoDAOH() {
        super();
        setClazz(GrupoHorasExcluido.class);
    }

    @Override
    public GrupoHorasExcluido findByGpoCiclo(GrupoHoras gpoHoras, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(GrupoHorasExcluido.class, "ghe")
                .join("cicloAcademico cic", "grupoHoras gh")
                .filter("cic.id", ciclo)
                .filter("gh.id", gpoHoras);

        return find(sql);
    }

    @Override
    public List<GrupoHorasExcluido> allByTipoGpoCiclo(TipoGrupoHoras tipoGpo, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(GrupoHorasExcluido.class, "ghe")
                .join("cicloAcademico cic", "grupoHoras gh", "gh.tipoGrupoHoras tg")
                .filter("cic.id", ciclo)
                .filter("tg.id", tipoGpo);

        return all(sql);
    }

    @Override
    public List<GrupoHorasExcluido> allByGpoHorasCiclo(List<GrupoHoras> gpos, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(GrupoHorasExcluido.class, "ghe")
                .join("cicloAcademico cic", "grupoHoras gh", "gh.tipoGrupoHoras tg")
                .filter("cic.id", ciclo)
                .in("gh.id", gpos);

        return all(sql);
    }

}
