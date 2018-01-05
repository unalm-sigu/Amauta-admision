package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.Arrays;
import java.util.List;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import static pe.edu.lamolina.model.enums.CicloAcademicoEstadoEnum.CFG;
import static pe.edu.lamolina.model.enums.CicloAcademicoEstadoEnum.PEND;
import static pe.edu.lamolina.model.enums.CicloAcademicoEstadoEnum.ACT;
import static pe.edu.lamolina.model.enums.CicloAcademicoEstadoEnum.CER;

@Repository
public class CicloAcademicoDAOH extends AbstractEasyDAO<CicloAcademico> implements CicloAcademicoDAO {

    public CicloAcademicoDAOH() {
        super();
        setClazz(CicloAcademico.class);
    }

    @Override
    public CicloAcademico find(long cicloAcademico) {

        Octavia sql = Octavia.query()
                .from(CicloAcademico.class, "ca")
                .filter("id", cicloAcademico);

        return find(sql);
    }

    @Override
    public CicloAcademico findActivo() {

        Octavia sql = Octavia.query()
                .from(CicloAcademico.class, "ca")
                .filter("estado", ACT);

        return find(sql);
    }

    @Override
    public List<CicloAcademico> allForChanges(Integer maxResultado) {

        Octavia sql = Octavia.query()
                .from(CicloAcademico.class, "ca")
                .in("estado", Arrays.asList(ACT, CER, PEND))
                .orderBy("year desc", "numeroCiclo desc")
                .limit(maxResultado);

        return all(sql);
    }

    @Override
    public CicloAcademico findAnteriorRegular(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(CicloAcademico.class, "ca")
                .filter("tipo", "REG")
                .filter("codigo", "<", ciclo.getCodigo())
                .orderBy("ca.year DESC", "ca.numeroCiclo DESC")
                .limit(1);

        return find(sql);
    }

    @Override
    public List<CicloAcademico> allUltimos(Integer cantidadCiclos) {
        Octavia sql = Octavia.query()
                .from(CicloAcademico.class, "ca")
                .in("estado", Arrays.asList(ACT, CER, PEND, CFG))
                .filter("tipo", "REG")
                .orderBy("year desc", "numeroCiclo desc")
                .limit(cantidadCiclos);

        return all(sql);
    }

    @Override
    public List<CicloAcademico> allCicloAcademicoByRange(int yearinit, int yearend) {
        Octavia sql = Octavia.query()
                .from(CicloAcademico.class, "ca")
                .filter("tipo", "REG")
                .filter("year", ">", yearinit)
                .filter("year", "<", yearend)
                .orderBy("ca.year DESC", "ca.numeroCiclo DESC");

        return all(sql);
    }

}
