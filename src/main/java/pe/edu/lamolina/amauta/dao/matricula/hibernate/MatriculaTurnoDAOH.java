package pe.edu.lamolina.amauta.dao.matricula.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.matricula.MatriculaTurno;
import pe.edu.lamolina.amauta.dao.matricula.MatriculaTurnoDAO;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.TurnoAtencion;

@Repository
public class MatriculaTurnoDAOH extends AbstractEasyDAO<MatriculaTurno> implements MatriculaTurnoDAO {

    public MatriculaTurnoDAOH() {
        this.setClazz(MatriculaTurno.class);
    }
    
    @Override
    public List<MatriculaTurno> findAllMatriculaTurnoByCiclo(DynatableFilter filter, CicloAcademico cicloAcademico) {
                DynatableSql sql = new DynatableSql(filter)
                .from(MatriculaTurno.class, "mt")
                .join("turnoAtencion ta")
                .join("matriculaResumen mres")
                .join("eventoAcademico ea")
                .join("mres.cicloAcademico ca")
                .join("mres.alumno al")
                .join("al.persona per")
                .filter("ca.id", cicloAcademico.getId())
                .searchFields("al.codigo", "per.numeroDocIdentidad")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("mt.id");
        return all(sql);
    }

    @Override
    public MatriculaTurno findMatriculaTurnoByTurnoAtencion(TurnoAtencion turnoAtencion) {
        Octavia sql = Octavia.query()
                .from(MatriculaTurno.class, "mt")
                .filter("mt.turnoAtencion", turnoAtencion);
        return find(sql);
    }        

    @Override
    public List<MatriculaTurno> findMatriculaTurnoByMatriculaResumen(MatriculaResumen matriculaResumen) {
        Octavia sql = Octavia.query()
                .from(MatriculaTurno.class, "mt")
                .filter("mt.matriculaResumen", matriculaResumen);
        return all(sql);
    }
    
}
