package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.matricula.AlumnoCursoCurricula;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCursoCurriculaDAO;

@Repository
public class AlumnoCursoCurriculaDAOH extends AbstractEasyDAO<AlumnoCursoCurricula> implements AlumnoCursoCurriculaDAO {
    
    public AlumnoCursoCurriculaDAOH() {
        super();
        setClazz(AlumnoCursoCurricula.class);
    }
    
    @Override
    public List<AlumnoCursoCurricula> allNoOpcionalByAlumno(Alumno alumno) {
        Octavia sql = Octavia.query()
                .from(AlumnoCursoCurricula.class, "acc")
                .isNull("cursoOpcional")
                .join("alumno alu")
                .filter("alumno", alumno)
                .orderBy("acc.numeroCiclo");
        
        return all(sql);
    }
    
    @Override
    public List<AlumnoCursoCurricula> allByAlumno(Alumno alumno, Long numeroCiclo) {
        Octavia sql = Octavia.query()
                .from(AlumnoCursoCurricula.class, "acc")
                .join("curso","cursoCurricula cc")
                .join("cc.tipoCursoCurricula")
                .leftJoin("acc.cicloAprobado")
                .filter("acc.alumno", alumno)
                .filter("acc.numeroCiclo", numeroCiclo);
        return all(sql);
    }
    
    @Override
    public List<AlumnoCursoCurricula> allCiclosAlumno(Alumno alumno) {
        Octavia sql = Octavia.query()
                
                .from(AlumnoCursoCurricula.class, "acc")                
                .filter("acc.alumno", alumno)
                .orderBy("acc.numeroCiclo");
        return all(sql);
    }
}
