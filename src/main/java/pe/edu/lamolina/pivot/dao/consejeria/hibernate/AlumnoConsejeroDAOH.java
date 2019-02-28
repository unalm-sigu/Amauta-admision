package pe.edu.lamolina.pivot.dao.consejeria.hibernate;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.hibernate.Query;
import org.springframework.stereotype.Service;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.model.enums.EstadoEnum;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.MAT;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.NMAT;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.RCI;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.dao.consejeria.AlumnoConsejeroDAO;
import static pe.edu.lamolina.pivot.zelper.constant.Constantine.ID_CONSEJERO_NN;

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

    @Override
    public List<AlumnoConsejero> allByCarrera(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(AlumnoConsejero.class, "ac")
                .join("alumno al", "consejero con")
                .join("al.persona per", "al.carrera car")
                .join("per.tipoDocumento", "al.situacionAcademica ")
                .left("al.cicloIngreso", "con.colaborador col", "col.persona perc", "perc.tipoDocumento")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .searchFields("al.codigo")
                .filter("estado", EstadoEnum.ACT)
                .orderBy("al.id desc");
        sql.beginRelativeFilters();
        setCondicion(filter, sql);
        return all(sql);
    }

    private void setCondicion(DynatableFilter filter, DynatableSql sql) {
        Map<String, Object> queries = filter.getQueries();
        if (queries == null) {
            return;
        }
        for (String key : queries.keySet()) {
            if (key.equals("search")) {
                continue;
            }
            String values = (String) queries.get(key);
            switch (key) {
                case "carrera":
                    sql.filter("car.id", values);
                    break;
                case "activo":
                    sql.filter("con.id", "<>", ID_CONSEJERO_NN);
                    break;
                case "sinconsejero":
                    sql.filter("con.id", ID_CONSEJERO_NN);
                    break;
            }

        }
    }

    @Override
    public List<AlumnoConsejero> allByPersona(DynatableFilter filter, CicloAcademico cicloAcademico, Persona persona) {
        Octavia sqlSub = new Octavia()
                .from(MatriculaResumen.class, "mr")
                .join("alumno al1", "cicloAcademico ca1")
                .filter("ca1.id", cicloAcademico);
        setCondicionEstadoMatricula(filter, sqlSub);

        DynatableSql sql = new DynatableSql(filter)
                .from(AlumnoConsejero.class, "ac")
                .join("alumno al", "consejero con")
                .join("al.persona per", "al.carrera car")
                .join("per.tipoDocumento", "al.situacionAcademica ", "cicloAcademico ca")
                .left("al.cicloIngreso", "con.colaborador col", "col.persona perc", "perc.tipoDocumento")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .searchFields("al.codigo")
                .exists(sqlSub)
                .linkedBy("al.id", "al1.id")
                .filter("estado", EstadoEnum.ACT)
                .filter("perc.id", persona)
                .filter("ca.id", cicloAcademico)
                .orderBy("al.id desc");
        return all(sql);
    }

    private void setCondicionEstadoMatricula(DynatableFilter filter, Octavia sql) {
        Map<String, Object> queries = filter.getQueries();
        if (queries == null) {
            return; 
        }
        for (String key : queries.keySet()) {
            if (key.equals("estado")) {
                String value = (String) queries.get(key);
                switch (value) {
                    case "matriculado":
                        sql.filter("mr.estado", MAT);
                        break;
                    case "noMatriculado":
                        sql.filter("mr.estado", NMAT);
                        break;
                    case "retirado":
                        sql.filter("mr.estado", RCI);
                        break;
                }
            }

        }
    }

    @Override
    public List<AlumnoConsejero> findAlumnoConsejeroByIdConsejero(Consejero consejero) {
        Octavia sql = Octavia.query()
                .from(AlumnoConsejero.class, "alcon")
                .filter("alcon.consejero", consejero);
        return all(sql);
    }
}
