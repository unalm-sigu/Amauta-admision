package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ControlMeritoEgresado;
import pe.edu.lamolina.model.academico.Egresado;
import pe.edu.lamolina.pivot.dao.academico.EgresadoDAO;

@Repository
public class EgresadoDAOH extends AbstractEasyDAO<Egresado> implements EgresadoDAO {

    public EgresadoDAOH() {
        super();
        setClazz(Egresado.class);
    }

    @Override
    public Egresado findByAlumno(Alumno alumno) {
        Octavia sql = Octavia.query()
                .from(Egresado.class, "e")
                .join("alumno alu")
                .filter("alu.id", alumno);

        return find(sql);
    }
    
    @Override
    public List<Egresado> allByCicloAcademico(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(Egresado.class, "ac")
                .join("cicloAcademico ca", "alumno alu")
                .join("alu.persona per", "carrera car", "car.facultad fac")
                .filter("cicloAcademico", ciclo);

        return sql.all(getCurrentSession());
    }

    @Override
    public List<Egresado> allByControlesOrdenMerito(List<ControlMeritoEgresado> coms) {
        Octavia sql = Octavia.query()
                .from(Egresado.class, "ac")
                .join("cicloAcademico ca", "alumno alu")
                .join("alu.persona per", "carrera car", "car.facultad fac")
                .left("controlMeritoCiclo cmc", "controlMeritoFacultad cmf", "controlMeritoCarrera cmca")
                .isNotNull("promedioAcumulado")
                .beginBlock()
                .__().in("cmc.id", coms)
                .__().in("cmf.id", coms)
                .__().in("cmca.id", coms)
                .endBlock();

        return sql.all(getCurrentSession());
    }

    @Override
    public void deleteInfoOrdenMeritoByCicloAcademico(CicloAcademico cicloAcademico) {
        StringBuilder sql = new StringBuilder();

        sql.append("update Egresado set ");

        sql.append("controlMeritoCarrera = null, ");
        sql.append("controlMeritoCiclo = null, ");
        sql.append("controlMeritoFacultad = null, ");

        sql.append("ordenMeritoCarrera = null, ");
        sql.append("ordenMeritoCiclo = null, ");
        sql.append("ordenMeritoFacultad = null, ");

        sql.append("cuadroHonorCarrera = null, ");
        sql.append("cuadroHonorCiclo = null, ");
        sql.append("cuadroHonorFacultad = null, ");

        sql.append("quintoSuperiorCarrera = null, ");
        sql.append("quintoSuperiorCiclo = null, ");
        sql.append("quintoSuperiorFacultad = null, ");

        sql.append("tercioSuperiorCarrera = null, ");
        sql.append("tercioSuperiorCiclo = null, ");
        sql.append("tercioSuperiorFacultad = null ");

        sql.append("where cicloAcademico.id = :CICLO");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setParameter("CICLO", cicloAcademico.getId());
        query.executeUpdate();
    }

    @Override
    public List<Egresado> allByControlMeritoCiclo(DynatableFilter filter, ControlMeritoEgresado controlBD) {
        DynatableSql sql = new DynatableSql(filter)
                .from(Egresado.class, "ac")
                .join("alumno alu", "alu.persona per", "carrera car", "car.facultad fac")
                .join("cicloAcademico ca")
                .join("controlMeritoCiclo control")
                .filter("control.id", controlBD)
                .orderBy("ac.ordenMeritoCiclo");

        return all(sql);
    }

    @Override
    public List<Egresado> allByControlMeritoCarrera(DynatableFilter filter, ControlMeritoEgresado controlBD) {
        DynatableSql sql = new DynatableSql(filter)
                .from(Egresado.class, "ac")
                .join("alumno alu", "alu.persona per", "carrera car", "car.facultad fac")
                .join("cicloAcademico ca")
                .join("controlMeritoCarrera control")
                .filter("control.id", controlBD)
                .orderBy("ac.ordenMeritoCarrera");

        return all(sql);
    }

    @Override
    public List<Egresado> allByControlMeritoFacultad(DynatableFilter filter, ControlMeritoEgresado controlBD) {
        DynatableSql sql = new DynatableSql(filter)
                .from(Egresado.class, "ac")
                .join("alumno alu", "alu.persona per", "carrera car", "car.facultad fac")
                .join("cicloAcademico ca")
                .join("controlMeritoFacultad control")
                .filter("control.id", controlBD)
                .orderBy("ac.ordenMeritoFacultad");

        return all(sql);
    }

}
