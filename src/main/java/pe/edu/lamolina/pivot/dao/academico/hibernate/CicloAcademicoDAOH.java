package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.Arrays;
import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import static pe.edu.lamolina.pivot.zelper.enums.CicloAcademicoEstadoEnum.ACT;
import static pe.edu.lamolina.pivot.zelper.enums.CicloAcademicoEstadoEnum.CER;
import static pe.edu.lamolina.pivot.zelper.enums.CicloAcademicoEstadoEnum.PEND;

@Repository
public class CicloAcademicoDAOH extends AbstractDAO<CicloAcademico> implements CicloAcademicoDAO {

    public CicloAcademicoDAOH() {
        super();
        setClazz(CicloAcademico.class);
    }

    @Override
    public CicloAcademico find(long cicloAcademico) {

        Octavia sql = Octavia.query()
                .from(CicloAcademico.class, "ca")
                .filter("id", cicloAcademico);
        return (CicloAcademico) sql.find(getCurrentSession());
    }

    @Override
    public CicloAcademico findActivo() {

        Octavia sql = Octavia.query()
                .from(CicloAcademico.class, "ca")
                .filter("estado", ACT);
        return (CicloAcademico) sql.find(getCurrentSession());
    }

    @Override
    public List<CicloAcademico> allForChanges(Integer maxResultado) {

        Octavia sql = Octavia.query()
                .from(CicloAcademico.class, "ca")
                .in("estado", Arrays.asList(ACT, CER, PEND))
                .orderBy("year desc", "numeroCiclo desc")
                .limit(maxResultado);

        return sql.all(getCurrentSession());
    }

    @Override
    public CicloAcademico findAnteriorRegular(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(CicloAcademico.class, "ca")
                .filter("tipo", "REG")
                .filter("codigo", "<", ciclo.getCodigo())
                .orderBy("ca.year DESC", "ca.numeroCiclo DESC")
                .limit(1);

        return (CicloAcademico) sql.find(getCurrentSession());
    }

    @Override
    public List<CicloAcademico> allUltimos(Integer cantidadCiclos) {
        Octavia sql = Octavia.query()
                .from(CicloAcademico.class, "ca")
                .in("estado", Arrays.asList(ACT, CER))
                .filter("tipo", "REG")
                .orderBy("year desc", "numeroCiclo desc")
                .limit(cantidadCiclos);

        return sql.all(getCurrentSession());
    }

}
