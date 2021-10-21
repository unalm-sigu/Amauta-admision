package pe.edu.lamolina.amauta.dao.bienestar.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.bienestar.AlumnoViajeCursoDAO;
import pe.edu.lamolina.model.bienestar.AlumnoViajeCurso;
import pe.edu.lamolina.model.bienestar.ViajeCurso;

@Repository
public class AlumnoViajeCursoDAOH extends AbstractEasyDAO<AlumnoViajeCurso> implements AlumnoViajeCursoDAO {

    public AlumnoViajeCursoDAOH() {
        super();
        setClazz(AlumnoViajeCurso.class);
    }
    
    @Override
    public List<AlumnoViajeCurso> allByViajeCurso(ViajeCurso viaje) {
        Octavia sql = Octavia.query()
                .from(AlumnoViajeCurso.class, "avc")
                .join("alumno alu", "viajeCurso vc", "alu.persona per")
                .join("vc.curso", "vc.seccion", "vc.cicloAcademico")
                .leftJoin("per.tipoDocumento")
                .filter("vc.id", viaje)
                .orderBy("per.paterno", "per.materno", "per.nombres");

        return all(sql);
    }
}
