package pe.edu.lamolina.pivot.dao.rolexamen.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.enums.AlumnoRolExamenEstadoEnum;
import pe.edu.lamolina.model.rolexamen.AlumnoCursoMasivo;
import pe.edu.lamolina.model.rolexamen.CursoMasivoExamen;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.pivot.dao.rolexamen.AlumnoCursoMasivoDAO;

@Repository
public class AlumnoCursoMasivoDAOH extends AbstractEasyDAO<AlumnoCursoMasivo> implements AlumnoCursoMasivoDAO {

    public AlumnoCursoMasivoDAOH() {
        super();
        setClazz(AlumnoCursoMasivo.class);
    }

    @Override
    public List<AlumnoCursoMasivo> allAlumnoByCursoMasivo(CursoMasivoExamen cursoMasivo) {
        Octavia sql = Octavia.query()
                .from(AlumnoCursoMasivo.class, "acm")
                .join("cursoMasivoExamen cme", "userRegistro ur")
                .filter("cme.id", cursoMasivo);
        return all(sql);
    }

    @Override
    public List<AlumnoCursoMasivo> allByCursoMasivo(CursoMasivoExamen cursoMasivo, AlumnoRolExamenEstadoEnum... estados) {
        Octavia sql = Octavia.query()
                .from(AlumnoCursoMasivo.class, "acm")
                .join("cursoMasivoExamen cme", "userRegistro ur")
                .filter("cme.id", cursoMasivo)
                .in("acm.estado", estados);
        return all(sql);
    }

    @Override
    public List<AlumnoCursoMasivo> allAlumnoByRolExamenes(RolExamenes rolExamenes, AlumnoRolExamenEstadoEnum... estados) {
        Octavia sql = Octavia.query()
                .from(AlumnoCursoMasivo.class, "acm")
                .join("cursoMasivoExamen cme", "userRegistro ur")
                .join("cme.rolExamenes rex")
                .filter("rex.id", rolExamenes)
                .in("acm.estado", estados);
        return all(sql);
    }

    @Override
    public List<AlumnoCursoMasivo> allByCursosMasivos(List<CursoMasivoExamen> cursosMasivoExamenes, AlumnoRolExamenEstadoEnum... estados) {
        Octavia sql = Octavia.query()
                .from(AlumnoCursoMasivo.class, "acm")
                .join("cursoMasivoExamen cme", "userRegistro ur", "alumno alu", "alu.persona aluper", "cme.rolExamenes rex")
                .in("cme.id", cursosMasivoExamenes)
                .in("acm.estado", estados);
        return all(sql);
    }
}
