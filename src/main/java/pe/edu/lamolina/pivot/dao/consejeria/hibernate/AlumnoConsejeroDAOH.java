package pe.edu.lamolina.pivot.dao.consejeria.hibernate;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.hibernate.Query;
import org.springframework.stereotype.Service;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.model.consejeria.Consejero;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.MAT;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.NMAT;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.dao.consejeria.AlumnoConsejeroDAO;

@Service
public class AlumnoConsejeroDAOH extends AbstractEasyDAO<AlumnoConsejero> implements AlumnoConsejeroDAO {

    public AlumnoConsejeroDAOH() {
        super();
        setClazz(AlumnoConsejero.class);
    }

    @Override
    public void insertAlumnoConsejero(Consejero consejero, CicloAcademico cicloAcademico, Usuario usuario, Carrera carrera, List<Alumno> alumnos) {
        List<Long> ids = alumnos.stream().map(Alumno::getId).collect(Collectors.toList());
        StringBuilder strb = new StringBuilder("");
        strb.append("insert into ");
        strb.append("AlumnoConsejero ");
        strb.append("( ");
        strb.append("estado, ");
        strb.append("fechaAsigna, ");
        strb.append("alumno, ");
        strb.append("consejero, ");
        strb.append("cicloAcademico, ");
        strb.append("userAsigna ");
        strb.append(") ");
        strb.append(" select ");
        strb.append("'ACT', ");
        strb.append(":hoy, ");
        strb.append("alum, ");
        strb.append(":consejero, ");
        strb.append(":ciclo, ");
        strb.append(":usuario ");
        strb.append("from MatriculaResumen mat ");
        strb.append("inner join mat.alumno alum ");
        strb.append("inner join alum.carrera car ");
        strb.append("inner join mat.cicloAcademico cic ");
        strb.append("where ");
        strb.append("car.id = :carrera ");
        strb.append("and cic.id = :ciclo ");
        strb.append("and mat.estado in (:estado)");
        strb.append("and alum.id in (:idAlumno)");

        Query query = getCurrentSession().createQuery(strb.toString());
        query.setParameter("carrera", carrera.getId());
        query.setParameter("ciclo", cicloAcademico);
        query.setParameter("consejero", consejero);
        query.setParameter("usuario", usuario);
        query.setParameter("hoy", new Date());
        query.setParameterList("estado", Arrays.asList(MAT.name(), NMAT.name()));
        query.setParameterList("idAlumno", ids);

        query.executeUpdate();
    }

    @Override
    public void desasignarAlumnosConsejero(List<Consejero> consejeros, Usuario usuario) {
        List<Long> ids = consejeros.stream().map(Consejero::getId).collect(Collectors.toList());
        StringBuilder strb = new StringBuilder("");

        strb.append("delete ");
        strb.append(" from AlumnoConsejero ");
        strb.append("where ");
        strb.append("consejero.id in (:consejeros)");

        Query query = getCurrentSession().createQuery(strb.toString());
        query.setParameterList("consejeros", ids);

        query.executeUpdate();

    }

}
