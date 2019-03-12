package pe.edu.lamolina.pivot.dao.aporte.hibernate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.hibernate.Query;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.aporte.ResumenAporteAlumno;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.MAT;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.NMAT;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.EPG;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.ESP;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.PRE;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.VIS;
import pe.edu.lamolina.model.enums.RecorridoIngresanteEstadoEnum;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_8;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_9;
import pe.edu.lamolina.model.enums.TipoActividadIngresanteEnum;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.dao.aporte.ResumenAporteAlumnoDAO;

@Repository
public class ResumenAporteAlumnoDAOH extends AbstractEasyDAO<ResumenAporteAlumno> implements ResumenAporteAlumnoDAO {

    public ResumenAporteAlumnoDAOH() {
        super();
        setClazz(ResumenAporteAlumno.class);
    }

    @Override
    public ResumenAporteAlumno findByAlumnoCicloAcademico(Alumno alumno, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(ResumenAporteAlumno.class, "raa")
                .join("matriculaResumen mr", "mr.alumno alu", "mr.cicloAcademico ca", "alu.situacionAcademica")
                .filter("alu.id", alumno)
                .filter("ca.id", cicloAcademico)
                .filter("mr.estado", EstadoMatriculaEnum.NMAT);
        return find(sql);
    }

    @Override
    public ResumenAporteAlumno findByAlumnoCiclo(AlumnoCiclo alumnoCiclo) {
        Octavia sql = Octavia.query()
                .from(ResumenAporteAlumno.class, "raa")
                .filter("alumnoCiclo", alumnoCiclo);

        return (ResumenAporteAlumno) sql.find(getCurrentSession());
    }

    @Override
    public List<ResumenAporteAlumno> allByAlumno(Alumno alumno) {
        Octavia sql = Octavia.query()
                .from(ResumenAporteAlumno.class, "raa")
                .join("matriculaResumen ac", "ac.alumno alu", "ac.cicloAcademico ca", "alu.situacionAcademica")
                .filter("alu.id", alumno)
                .orderBy("ca.year desc");
        return all(sql);
    }

    @Override
    public List<ResumenAporteAlumno> allByCicloAcademico(CicloAcademico cicloAcademico) {

        Octavia sql = Octavia.query()
                .from(ResumenAporteAlumno.class, "raa")
                .join("matriculaResumen ac")
                .filter("ac.cicloAcademico", cicloAcademico);

        return all(sql);
    }

    @Override
    public List<ResumenAporteAlumno> allByDynatableCicloAcademico(ModalidadEstudio estudio, DynatableFilter filter, CicloAcademico cicloAcademico) {
        DynatableSql sql = new DynatableSql(filter)
                .from(ResumenAporteAlumno.class, "raa")
                .join("matriculaResumen mr", "mr.alumno alu", "alu.persona per", "alu.carrera ca", "alu.situacionAcademica sa", "alu.modalidadEstudio me")
                .searchFields("alu.codigo", "ca.nombre", "per.numeroDocIdentidad", "sa.nombre", "mr.estado")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .filter("mr.cicloAcademico", cicloAcademico)
                .orderBy("raa.id desc");
        sql.beginRelativeFilters();
        setCondicionModalidad(filter, sql, estudio);
        return all(sql);
    }

    private void setCondicionModalidad(DynatableFilter filter, DynatableSql sql, ModalidadEstudio estudio) {
        Map<String, Object> queries = filter.getQueries();
        if (queries == null) {
            return;
        }

        for (String key : queries.keySet()) {
            String values = (String) queries.get(key);
            if (values.toLowerCase().equals("pregrado")) {
                sql.filter("me.codigo", PRE);
            } else if (values.toLowerCase().equals("posgrado")) {
                sql.filter("me.codigo", EPG);
            } else if (values.toLowerCase().equals("visitante")) {
                sql.filter("me.codigo", VIS);
            } else if (values.toLowerCase().equals("especial")) {
                sql.filter("me.codigo", ESP);
            }
        }
    }

    @Override
    public ResumenAporteAlumno findByMatriculaResumen(MatriculaResumen matriculaResumen) {
        Octavia sql = Octavia.query()
                .from(ResumenAporteAlumno.class, "raa")
                .leftJoin("matriculaResumen mr")
                .filter("matriculaResumen", matriculaResumen);

        return (ResumenAporteAlumno) sql.find(getCurrentSession());
    }

    @Override
    public void inicializarByCodigoCicloModalidad(String codigoCiclo, ModalidadEstudio modalidad, Usuario usuario) {

        StringBuilder sql = new StringBuilder();

        sql.append("insert into ResumenAporteAlumno "
                + " ( matriculaResumen, fechaRegistro, userRegistro, montoTotal, montoInicial, montoFraccionado, "
                + "   montoPendiente, montoCancelado, montoExonerado, montoAFavor "
                + " ) "
                + " select MR, sysdate(), :USUARIO ,:CERO ,:CERO ,:CERO ,:CERO ,:CERO ,:CERO ,:CERO "
                + "   from MatriculaResumen MR "
                + "   join MR.alumno AL "
                + "   join AL.situacionAcademica SA with SA.codigo not in :SIT_INGRESANTES "
                + "   join AL.modalidadEstudio ME with ME.id = :MODALIDAD "
                + "   join MR.cicloAcademico CA with CA.codigo = :CODIGO_CICLO "
                + "  where MR.estado in :ESTADOS   ");

        Query query = getCurrentSession().createQuery(sql.toString());

        query.setParameter("CODIGO_CICLO", codigoCiclo);
        query.setParameter("MODALIDAD", modalidad.getId());
        query.setParameter("USUARIO", usuario);
        query.setParameter("CERO", BigDecimal.ZERO);
        query.setParameterList("ESTADOS", Arrays.asList(NMAT.name(), MAT.name()));
        query.setParameterList("SIT_INGRESANTES", Arrays.asList(S_8.getValue(), S_9.getValue()));

        query.executeUpdate();
    }

    @Override
    public void inicializarIngresantesByCiclo(CicloAcademico cicloAcademico, Usuario usuario) {

        StringBuilder sql = new StringBuilder();

        sql.append("insert into ResumenAporteAlumno "
                + " ( matriculaResumen, fechaRegistro, userRegistro, montoTotal, montoInicial, montoFraccionado, "
                + "   montoPendiente, montoCancelado, montoExonerado, montoAFavor "
                + " ) "
                + " select MR, sysdate(), :USUARIO, :CERO, :CERO, :CERO, :CERO, :CERO, :CERO, :CERO "
                + "   from MatriculaResumen MR "
                + "   join MR.alumno AL "
                + "   join AL.situacionAcademica SA with SA.codigo in :SIT_INGRESANTES "
                + "   join MR.cicloAcademico CA with CA.id = :ID_CICLO "
                + "  where MR.estado in :ESTADOS   "
                + "    and exists (  "
                + "         select AI.id  "
                + "           from ActividadIngresante AI  "
                + "           join AI.recorridoIngresante RI  "
                + "           join AI.tipoActividadIngresante TAI  "
                + "          where RI.alumno = AL  "
                + "            and RI.cicloAcademico = CA  "
                + "            and AI.estado = :ESTADO_ACTIVIDAD  "
                + "            and TAI.codigo = :CODIGO_ACTIVIDAD  "
                + "    )  "
                + "       ");

        Query query = getCurrentSession().createQuery(sql.toString());

        query.setParameter("ID_CICLO", cicloAcademico.getId());
        query.setParameter("CODIGO_ACTIVIDAD", TipoActividadIngresanteEnum.ENTREV.name());
        query.setParameter("ESTADO_ACTIVIDAD", RecorridoIngresanteEstadoEnum.ACT.name());
        query.setParameter("USUARIO", usuario);
        query.setParameter("CERO", BigDecimal.ZERO);
        query.setParameterList("ESTADOS", Arrays.asList(NMAT.name(), MAT.name()));
        query.setParameterList("SIT_INGRESANTES", Arrays.asList(S_8.getValue(), S_9.getValue()));

        query.executeUpdate();
    }

    @Override
    public void consolidarAportesByCiclo(CicloAcademico cicloAcademico) {

        StringBuilder sql = new StringBuilder();

        sql.append(" update apo_resumen_aporte_alumno as RAA ");
        sql.append(" inner join  ");
        sql.append(" 	(	 ");
        sql.append("      select AAC.id_resumen_aporte_alumno,  ");
        sql.append(" 		 sum(AAC.monto) as monto ");
        sql.append("        from apo_aporte_alumno_ciclo as AAC  ");
        sql.append("       inner join apo_aporte_ciclo as AC on AAC.id_aporte_ciclo = AC.id ");
        sql.append("       where AC.id_ciclo_academico = :CICLO_ACADEMICO ");
        sql.append("       group by AAC.id_resumen_aporte_alumno ");
        sql.append("     ) as AAC ");
        sql.append("     on RAA.id = AAC.id_resumen_aporte_alumno ");

        sql.append(" set ");
        sql.append("     RAA.monto_total = AAC.monto, ");
        sql.append("     RAA.monto_inicial = AAC.monto, ");
        sql.append("     RAA.monto_pendiente = AAC.monto, ");
        sql.append("     RAA.monto_fraccionado = 0, ");
        sql.append("     RAA.monto_cancelado = 0, ");
        sql.append("     RAA.monto_afavor = 0, ");
        sql.append("     RAA.monto_exonerado = 0; ");

        Query query = getCurrentSession().createSQLQuery(sql.toString());
        query.setParameter("CICLO_ACADEMICO", cicloAcademico.getId());
        query.executeUpdate();
    }

    @Override
    public boolean yaGenerados(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(ResumenAporteAlumno.class, "raa")
                .leftJoin("raa.matriculaResumen ac")
                .filter("ac.cicloAcademico", cicloAcademico)
                .limit(1);

        return !all(sql).isEmpty();
    }

    @Override
    public void deleteByCicloAcademico(CicloAcademico cicloAcademico) {
        StringBuilder sql = new StringBuilder();

        sql.append("delete RAA "
                + "   from apo_resumen_aporte_alumno RAA "
                + "  inner join aca_matricula_resumen AC on RAA.id_matricula_resumen = AC.id "
                + "  where AC.id_ciclo_academico = :CICLO_ACADEMICO ");

        Query query = getCurrentSession().createSQLQuery(sql.toString());
        query.setParameter("CICLO_ACADEMICO", cicloAcademico.getId());
        query.executeUpdate();
    }

    @Override
    public List<ResumenAporteAlumno> allByCodigoCicloModalidad(String codigo, ModalidadEstudio modalidad) {
        Octavia sql = Octavia.query()
                .from(ResumenAporteAlumno.class, "raa")
                .join("raa.matriculaResumen mr", "mr.alumno alu", "alu.modalidadEstudio me", "mr.cicloAcademico ca")
                .filter("ca.codigo", codigo)
                .filter("me.id", modalidad);

        return all(sql);
    }

    @Override
    public ResumenAporteAlumno find(ResumenAporteAlumno aporteAlumno) {
        Octavia sql = new Octavia()
                .from(ResumenAporteAlumno.class, "raa")
                .filter("raa.id", aporteAlumno);
        return find(sql);

    }

}
