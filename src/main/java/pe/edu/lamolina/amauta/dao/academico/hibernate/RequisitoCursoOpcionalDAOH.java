package pe.edu.lamolina.amauta.dao.academico.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CursoCurricula;
import pe.edu.lamolina.model.academico.CursoOpcionalCurricula;
import pe.edu.lamolina.model.academico.RequisitoCursoOpcional;
import static pe.edu.lamolina.model.enums.EstadoEnum.ACT;
import pe.edu.lamolina.amauta.dao.academico.RequisitoCursoOpcionalDAO;

@Repository
public class RequisitoCursoOpcionalDAOH extends AbstractEasyDAO<RequisitoCursoOpcional> implements RequisitoCursoOpcionalDAO {

    public RequisitoCursoOpcionalDAOH() {
        super();
        setClazz(RequisitoCursoOpcional.class);
    }

    @Override
    public List<RequisitoCursoOpcional> allPostRequisitosByCursosCurricula(List<CursoCurricula> cursosCurricula) {
        Octavia sql = Octavia.query()
                .from(RequisitoCursoOpcional.class, "rcc")
                .join("cursoOpcional cop", "cursoRequisitoCurricula crc", "cop.curso", "cop.tipoCursoCurricula")
                .filter("estado", ACT)
                .in("crc.id", cursosCurricula);

        return all(sql);
    }

    @Override
    public List<RequisitoCursoOpcional> allRequisitoOpcionalDe(CursoCurricula cursosCurricula) {
        Octavia sql = Octavia.query()
                .from(RequisitoCursoOpcional.class, "rcc")
                .join("cursoOpcional cop", "cursoRequisitoCurricula crc", "cop.curso", "cop.tipoCursoCurricula")
                .filter("estado", ACT)
                .filter("crc.id", cursosCurricula);

        return all(sql);
    }

    @Override
    public List<RequisitoCursoOpcional> allRequisitosByCursosElectivos(List<CursoOpcionalCurricula> cursosElectivos) {
        Octavia sql = Octavia.query()
                .from(RequisitoCursoOpcional.class, "rcc")
                .join("cursoOpcional cop")
                .leftJoin("cursoRequisitoCurricula crc", "crc.curso", "crc.tipoCursoCurricula")
                .leftJoin("cursoRequisitoOpcional cro", "cro.curso", "cro.tipoCursoCurricula")
                .filter("estado", ACT)
                .in("cop.id", cursosElectivos);

        return all(sql);
    }

    @Override
    public List<RequisitoCursoOpcional> allPostRequisitosByCursosElectivo(List<CursoOpcionalCurricula> cursosElectivos) {
        Octavia sql = Octavia.query()
                .from(RequisitoCursoOpcional.class, "rcc")
                .join("cursoOpcional cop", "cursoRequisitoOpcional cro", "cop.curso", "cop.tipoCursoCurricula")
                .filter("estado", ACT)
                .in("cro.id", cursosElectivos);

        return all(sql);
    }

    @Override
    public List<RequisitoCursoOpcional> allByCursoElectivo(CursoOpcionalCurricula cursoElectivo) {
        Octavia sql = Octavia.query()
                .from(RequisitoCursoOpcional.class, "rcc")
                .join("cursoOpcional cop")
                .leftJoin("cursoRequisitoCurricula crc", "crc.curso", "crc.tipoCursoCurricula")
                .leftJoin("cursoRequisitoOpcional cro", "cro.curso", "cro.tipoCursoCurricula")
                .filter("estado", ACT)
                .filter("cop.id", cursoElectivo);

        return all(sql);
    }

    @Override
    public List<RequisitoCursoOpcional> allPostRequisitosByCursoElectivo(CursoOpcionalCurricula cursoElectivo) {
        Octavia sql = Octavia.query()
                .from(RequisitoCursoOpcional.class, "rcc")
                .join("cursoOpcional cop", "cursoRequisitoCurricula crc", "cop.curso", "cop.tipoCursoCurricula")
                .filter("estado", ACT)
                .filter("crc.id", cursoElectivo);

        return all(sql);
    }

}
