package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.hibernate.Query;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.MAT;
import pe.edu.lamolina.model.enums.TipoSeccionEnum;

@Repository
public class MatriculaSeccionDAOH extends AbstractEasyDAO<MatriculaSeccion> implements MatriculaSeccionDAO {
    
    public MatriculaSeccionDAOH() {
        super();
        setClazz(MatriculaSeccion.class);
    }
    
    @Override
    public List<MatriculaSeccion> allMatriculadosBySeccion(Seccion seccion) {
        return this.allMatriculadosBySeccion(Arrays.asList(seccion), EstadoMatriculaEnum.MAT);
    }
    
    @Override
    public List<MatriculaSeccion> allMatriculadosBySecciones(List<Seccion> secciones) {
        return this.allMatriculadosBySeccion(secciones, EstadoMatriculaEnum.MAT);
    }
    
    @Override
    public List<MatriculaSeccion> allMatriculadosBySeccion(List<Seccion> secciones, EstadoMatriculaEnum... estados) {
        Octavia sql = Octavia.query()
                .from(MatriculaSeccion.class, "ms")
                .join("matriculaResumen mr", "seccion sec", "mr.alumno alu", "sec.grupoSeccion gs")
                .join("gs.curso cur", "alu.persona per", "alu.carrera carr", "carr.facultad fac")
                .join("alu.modalidadEstudio")
                .leftJoin("per.tipoDocumento tdoc")
                .in("ms.estado", Arrays.asList(estados))
                .in("sec.id", secciones)
                .orderBy("per.paterno", "per.materno", "per.nombres");
        return all(sql);
    }
    
    @Override
    public List<MatriculaSeccion> allMatriculadosByMatriculaSeccion(List<MatriculaResumen> matriculasResumen, EstadoMatriculaEnum... estados) {
        Octavia sql = Octavia.query()
                .from(MatriculaSeccion.class, "ms")
                .join("matriculaResumen mr", "seccion sec", "mr.alumno alu", "sec.grupoSeccion gs")
                .join("gs.curso cur", "alu.persona per", "alu.carrera carr", "carr.facultad fac")
                .join("alu.modalidadEstudio")
                .leftJoin("per.tipoDocumento tdoc")
                .in("ms.estado", Arrays.asList(estados))
                .in("mr.estado", Arrays.asList(estados))
                .in("mr.id", matriculasResumen)
                .orderBy("per.paterno", "per.materno", "per.nombres");
        return all(sql);
    }
    
    @Override
    public List<MatriculaSeccion> allBySeccion(Seccion seccion) {
        Octavia sql = Octavia.query()
                .from(MatriculaSeccion.class, "ms")
                .join("matriculaResumen mr", "seccion sec", "mr.alumno alu", "sec.grupoSeccion gs")
                .join("gs.curso cur", "alu.persona per", "alu.carrera carr", "carr.facultad fac")
                .join("alu.cicloActivo ca", "alu.cicloIngreso caing")
                .left("ms.userRegistro ureg", "ureg.persona urper", "alu.situacionAcademica sa")
                .leftJoin("per.tipoDocumento tdoc", "alu.modalidadEstudio mest")
                .filter("sec.id", seccion)
                .orderBy("per.paterno", "per.materno", "per.nombres");
        
        return all(sql);
    }
    
    @Override
    public List<MatriculaSeccion> allBySeccionLite(Seccion seccion) {
        Octavia sql = Octavia.query()
                .from(MatriculaSeccion.class, "ms")
                .join("matriculaResumen mr", "seccion sec", "mr.alumno alu", "sec.grupoSeccion gs")
                .join("gs.curso cur", "alu.persona per", "alu.carrera carr", "carr.facultad fac")
                .left("alu.cicloActivo ca", "alu.cicloIngreso caing")
                .left("ms.userRegistro ureg", "ureg.persona urper", "alu.situacionAcademica sa")
                .leftJoin("per.tipoDocumento tdoc", "alu.modalidadEstudio mest")
                .filter("sec.id", seccion)
                .orderBy("per.paterno", "per.materno", "per.nombres");
        
        return all(sql);
    }
    
    @Override
    public List<MatriculaSeccion> allByGrupoSeccion(List<GrupoSeccion> gruposSeccion, EstadoMatriculaEnum... estadosMatriculaSeccion) {
        Octavia sql = Octavia.query()
                .from(MatriculaSeccion.class, "ms")
                .join("matriculaResumen mr", "seccion sec", "mr.alumno alu", "sec.grupoSeccion gs")
                .join("gs.curso cur", "alu.persona per", "alu.carrera carr", "carr.facultad fac")
                .join("alu.cicloActivo ca", "alu.cicloIngreso caing")
                .left("ms.userRegistro ureg", "ureg.persona urper", "alu.situacionAcademica sa")
                .leftJoin("per.tipoDocumento tdoc", "alu.modalidadEstudio mest")
                .in("gs.id", gruposSeccion);
        if (estadosMatriculaSeccion != null) {
            List<EstadoMatriculaEnum> lEstadosMatriculaSeccion = Arrays.asList(estadosMatriculaSeccion);
            sql.in("ms.estado", lEstadosMatriculaSeccion);
        }
        sql.orderBy("per.paterno", "per.materno", "per.nombres");
        
        return all(sql);
    }
    
    @Override
    public MatriculaSeccion find(long id) {
        Octavia sql = Octavia.query()
                .from(MatriculaSeccion.class, "ms")
                .join("matriculaResumen mr", "seccion sec", "mr.alumno alu", "sec.grupoSeccion gs")
                .join("gs.curso cur", "alu.persona per", "per.tipoDocumento tdoc")
                .filter("ms.id", id);
        
        return find(sql);
    }
    
    @Override
    public List<MatriculaSeccion> allMatriculadosByGpoSeccion(GrupoSeccion grupoSeccion) {
        Octavia sql = Octavia.query()
                .from(MatriculaSeccion.class, "ms")
                .join("matriculaResumen mr", "seccion sec", "mr.alumno alu", "sec.grupoSeccion gs", "gs.cicloAcademico ca")
                .join("gs.curso cur", "alu.persona per", "per.tipoDocumento tdoc")
                .filter("ms.estado", MAT)
                .filter("gs.id", grupoSeccion);
        //  .filter("ca.id", ciclo);

        return all(sql);
    }
    
    @Override
    public List<MatriculaSeccion> allMatriculadosByCiclo(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(MatriculaSeccion.class, "ms")
                .join("matriculaResumen mr", "seccion sec", "mr.alumno alu", "sec.grupoSeccion gs", "gs.cicloAcademico ca")
                .join("gs.curso cur", "alu.persona per", "per.tipoDocumento tdoc")
                .leftJoin("gs.planCalificacion")
                .filter("ms.estado", MAT)
                .filter("ca.id", ciclo);
        
        return all(sql);
    }
    
    @Override
    public List<MatriculaSeccion> allByCiclo(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(MatriculaSeccion.class, "ms")
                .join("matriculaResumen mr", "seccion sec", "mr.alumno alu", "sec.grupoSeccion gs", "gs.cicloAcademico ca")
                .join("gs.curso cur", "alu.persona per")
                .leftJoin("gs.planCalificacion", "per.tipoDocumento tdoc")
                .filter("ca.id", ciclo);
        
        return all(sql);
    }
    
    @Override
    public List<MatriculaSeccion> allMatriculadosByAlumnoCiclo(Alumno alumno, CicloAcademico ciclo) {
        Octavia sqlUtil = Octavia.query()
                .from(MatriculaSeccion.class, "ms")
                .join("matriculaResumen mr", "mr.alumno alu", "mr.cicloAcademico ca")
                .join("seccion sec", "sec.grupoSeccion gs", "gs.curso")
                .left("sec.aula", "sec.grupoHoras")
                .filter("ms.estado", EstadoMatriculaEnum.MAT)
                .filter("mr.estado", EstadoMatriculaEnum.MAT)
                .filter("ca.id", ciclo)
                .filter("alu.id", alumno);
        return all(sqlUtil);
    }
    
    @Override
    public List<MatriculaSeccion> allMatriculadosByAlumnosCiclo(List<Alumno> alumnos, CicloAcademico ciclo) {
        Octavia sqlUtil = Octavia.query()
                .from(MatriculaSeccion.class, "ms")
                .join("matriculaResumen mr", "mr.alumno alu", "mr.cicloAcademico ca")
                .join("seccion sec", "sec.grupoSeccion gs", "gs.curso")
                .leftJoin("sec.seccionSuperior")
                .leftJoin("sec.aula", "sec.grupoHoras")
                .filter("ms.estado", EstadoMatriculaEnum.MAT)
                .filter("mr.estado", EstadoMatriculaEnum.MAT)
                .filter("ca.id", ciclo)
                .in("alu.id", alumnos);
        return all(sqlUtil);
    }
    
    @Override
    public List<MatriculaSeccion> allActivesByMatriculaResumen(List<MatriculaResumen> matriculaResumen) {
        Octavia sqlUtil = Octavia.query()
                .from(MatriculaSeccion.class, "ms")
                .join("matriculaResumen mr", "mr.alumno alu", "mr.cicloAcademico ca", "alu.carrera car")
                .join("seccion sec", "sec.grupoSeccion gs", "gs.curso")
                .leftJoin("sec.seccionSuperior")
                .leftJoin("sec.aula", "sec.grupoHoras")
                .filter("ms.estado", EstadoMatriculaEnum.MAT)
                .filter("mr.estado", EstadoMatriculaEnum.MAT)
                .in("mr.id", matriculaResumen);
        return all(sqlUtil);
    }
    
    @Override
    public List<MatriculaSeccion> allByMatriculaResumenes(List<MatriculaResumen> matriculaResumen) {
        Octavia sqlUtil = Octavia.query()
                .from(MatriculaSeccion.class, "ms")
                .join("matriculaResumen mr", "mr.alumno alu", "mr.cicloAcademico ca", "alu.carrera car")
                .join("seccion sec", "sec.grupoSeccion gs", "gs.curso")
                .leftJoin("sec.seccionSuperior")
                .leftJoin("sec.aula", "sec.grupoHoras")
                .in("mr.id", matriculaResumen);
        return all(sqlUtil);
    }
    
    @Override
    public List<MatriculaSeccion> allByMatriculaResumen(MatriculaResumen matResumen) {
        Octavia sqlUtil = Octavia.query()
                .from(MatriculaSeccion.class, "ms")
                .join("matriculaResumen mr", "mr.alumno alu", "mr.cicloAcademico ca")
                .join("seccion sec", "sec.grupoSeccion gs", "gs.curso")
                .leftJoin("sec.seccionSuperior")
                .leftJoin("sec.aula", "sec.grupoHoras")
                .filter("mr.id", matResumen);
        return all(sqlUtil);
    }
    
    @Override
    public List<MatriculaSeccion> allByAlumnoCicloEstados(Alumno alumno, CicloAcademico academico, List<String> estados) {
        Octavia sql = Octavia.query()
                .from(MatriculaSeccion.class, "ms")
                .join("matriculaResumen mr", "mr.alumno alu", "mr.cicloAcademico ca")
                .join("seccion s", "s.grupoSeccion gs", "gs.curso")
                .leftJoin("s.aula")
                .filter("ca.id", academico)
                .filter("alu.id", alumno)
                .in("ms.estado", estados);
        return sql.all(getCurrentSession());
    }
    
    @Override
    public Long countAllSeccionPrematriculado(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .selectCountDistinct("ms.id")
                .from(MatriculaSeccion.class, "ms")
                .join("matriculaResumen mr", "mr.alumno alu", "mr.cicloAcademico ca")
                .join("seccion s", "s.grupoSeccion gs", "gs.curso")
                .left("s.aula")
                .filter("ca.id", cicloAcademico)
                .in("ms.estado", Arrays.asList(EstadoMatriculaEnum.PMAT.name(), EstadoMatriculaEnum.NMAT.name()));
        return (Long) sql.find(getCurrentSession());
    }
    
    @Override
    public List<MatriculaSeccion> allPrematriculadoByMatriculaResumen(List<MatriculaResumen> matriculaResumens) {
        Octavia sql = Octavia.query()
                .from(MatriculaSeccion.class, "ms")
                .join("matriculaResumen mr", "seccion sec", "mr.alumno alu", "sec.grupoSeccion gs", "gs.cicloAcademico ca")
                .join("gs.curso cur", "alu.persona per", "per.tipoDocumento tdoc")
                .in("mr.id", matriculaResumens)
                .filter("ms.estado", EstadoMatriculaEnum.PMAT);
        return all(sql);
    }
    
    @Override
    public List<MatriculaSeccion> allMatriculadosByAlumnosSecciones(List<Alumno> alumnos, List<Seccion> secciones) {
        Octavia sql = Octavia.query()
                .from(MatriculaSeccion.class, "ms")
                .join("seccion sec", "sec.grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ci")
                .join("matriculaResumen mr", "mr.alumno alu", "mr.cicloAcademico ca")
                .filter("ms.estado", EstadoMatriculaEnum.MAT)
                .in("sec.id", secciones)
                .in("alu.id", alumnos);
        return all(sql);
    }
    
    @Override
    public MatriculaSeccion findByMatriculaMatSeccion(MatriculaResumen matriculaResumen, Seccion seccion) {
        Octavia sqlUtil = Octavia.query()
                .from(MatriculaSeccion.class, "ms")
                .join("matriculaResumen mr", "mr.alumno alu", "mr.cicloAcademico ca")
                .join("seccion sec", "sec.grupoSeccion gs", "gs.curso")
                .left("sec.aula", "sec.grupoHoras")
                .filter("mr.id", matriculaResumen)
                .filter("ms.estado", EstadoMatriculaEnum.MAT)
                .filter("sec.id", seccion);
        return find(sqlUtil);
    }
    
    @Override
    public MatriculaSeccion findByMatriculaMatSeccion(MatriculaResumen matriculaResumen, Seccion seccion, EstadoMatriculaEnum... estado) {
        Octavia sqlUtil = Octavia.query()
                .from(MatriculaSeccion.class, "ms")
                .join("matriculaResumen mr", "mr.alumno alu", "mr.cicloAcademico ca")
                .join("seccion sec", "sec.grupoSeccion gs", "gs.curso")
                .left("sec.aula", "sec.grupoHoras")
                .filter("mr.id", matriculaResumen)
                .in("ms.estado", estado)
                .filter("sec.id", seccion);
        return find(sqlUtil);
    }
    
    @Override
    public MatriculaSeccion findByMatriculaMatSeccionAndNoEstado(MatriculaResumen matriculaResumen, Seccion seccion, EstadoMatriculaEnum... estado) {
        List<EstadoMatriculaEnum> estadosEnum = Arrays.asList(estado);
        Octavia sqlUtil = Octavia.query()
                .from(MatriculaSeccion.class, "ms")
                .join("matriculaResumen mr", "mr.alumno alu", "mr.cicloAcademico ca")
                .join("seccion sec", "sec.grupoSeccion gs", "gs.curso")
                .left("sec.aula", "sec.grupoHoras")
                .filter("mr.id", matriculaResumen)
                .notIn("ms.estado", estadosEnum.stream().map(x -> x.name()).collect(Collectors.toList()))
                .filter("sec.id", seccion);
        return find(sqlUtil);
    }
    
    @Override
    public List<MatriculaSeccion> allByMatriculaMatSeccion(List<MatriculaResumen> matriculasResumen, Seccion seccion) {
        Octavia sqlUtil = Octavia.query()
                .from(MatriculaSeccion.class, "ms")
                .join("matriculaResumen mr", "mr.alumno alu", "mr.cicloAcademico ca")
                .join("seccion sec", "sec.grupoSeccion gs", "gs.curso")
                .left("sec.aula", "sec.grupoHoras")
                .in("mr.id", matriculasResumen)
                .filter("ms.estado", EstadoMatriculaEnum.MAT)
                .filter("sec.id", seccion);
        return all(sqlUtil);
    }
    
    @Override
    public List<MatriculaSeccion> allByMatriculaResumenes(List<MatriculaResumen> resumenes, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(MatriculaSeccion.class, "ms")
                .join("matriculaResumen mr", "seccion sec", "mr.alumno alu", "sec.grupoSeccion gs", "gs.cicloAcademico ca")
                .join("gs.curso cur", "alu.persona per")
                .left("per.tipoDocumento tdoc", "sec.aula", "sec.grupoHoras")
                .filter("ms.estado", EstadoMatriculaEnum.PMAT)
                .filter("ca.id", cicloAcademico)
                .in("mr.id", resumenes);
        return all(sql);
    }
    
    @Override
    public void updateEstado(List<MatriculaSeccion> matriculaSeccionMatTemp, EstadoMatriculaEnum eme) {
        List<Long> longs = matriculaSeccionMatTemp.stream().map(x -> x.getId()).collect(Collectors.toList());
        StringBuilder sql = new StringBuilder();
        sql.append("  update  ").append(MatriculaSeccion.class.getName()).append(" ms ");
        sql.append("  set estado = :estado ");
        sql.append("  where ms.id in (:ids )");
        
        Query query = getCurrentSession().createQuery(sql.toString());
        query.setParameterList("ids", longs);
        query.setString("estado", eme.name());
        query.executeUpdate();
    }
    
    @Override
    public void updateColumns(MatriculaSeccion matriculaSeccion, String... columns) {
        Octavia sql = Octavia.update(MatriculaSeccion.class, "se");
        for (String column : columns) {
            sql.set(matriculaSeccion, column);
        }
        this.update(sql);
    }
    
    @Override
    public MatriculaSeccion findByMatResumenAndTipoSecAndEstado(MatriculaResumen matriculaResumen, TipoSeccionEnum tipoSeccionEnum, EstadoMatriculaEnum... estadoMatriculaEnum) {
        Octavia sqlUtil = Octavia.query()
                .from(MatriculaSeccion.class, "ms")
                .join("matriculaResumen mr", "mr.alumno alu", "mr.cicloAcademico ca")
                .join("seccion sec", "sec.grupoSeccion gs", "gs.curso")
                .left("sec.aula", "sec.grupoHoras")
                .filter("mr.id", matriculaResumen)
                .in("ms.estado", estadoMatriculaEnum)
                .filter("sec.tipoSeccion", tipoSeccionEnum);
        return find(sqlUtil);
    }
    
    @Override
    public MatriculaSeccion findByMatResumenAndTipoSecAndNoEstado(MatriculaResumen matriculaResumen, TipoSeccionEnum tipoSeccionEnum, EstadoMatriculaEnum... estadoMatriculaEnum) {
        Octavia sqlUtil = Octavia.query()
                .from(MatriculaSeccion.class, "ms")
                .join("matriculaResumen mr", "mr.alumno alu", "mr.cicloAcademico ca")
                .join("seccion sec", "sec.grupoSeccion gs", "gs.curso")
                .left("sec.aula", "sec.grupoHoras")
                .filter("mr.id", matriculaResumen)
                .notIn("ms.estado", Arrays.asList(estadoMatriculaEnum).stream().map(x -> x.name()).collect(Collectors.toList()))
                .filter("sec.tipoSeccion", tipoSeccionEnum);
        return find(sqlUtil);
    }
    
    @Override
    public void deleteAllByCiclo(CicloAcademico ciclo) {
        StringBuilder sql = new StringBuilder();
        sql.append(" DELETE ").append(MatriculaSeccion.class.getName()).append(" ms ")
                .append(" WHERE EXISTS ( ")
                .append("   SELECT 1 FROM ").append(Seccion.class.getName()).append(" sec ")
                .append("     JOIN sec.grupoSeccion gs ")
                .append("     JOIN gs.cicloAcademico ci ")
                .append("    WHERE ci.id = :CICLO ")
                .append("      AND ms.seccion.id = sec.id ")
                .append(" ) ");
        
        Query query = getCurrentSession().createQuery(sql.toString());
        query.setLong("CICLO", ciclo.getId());
        query.executeUpdate();
        
    }
    
    @Override
    public List<MatriculaSeccion> allMatriculadosBySeccion(String seccion, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(MatriculaSeccion.class, "ms")
                .join("matriculaResumen mr", "mr.alumno alu", "mr.cicloAcademico ca")
                .join("seccion s", "s.grupoSeccion gs", "gs.curso", "gs.cicloAcademico gca")
                .left("s.aula")
                .filter("s.codigo2", seccion)
                .filter("gca.id", cicloAcademico)
                .in("ms.estado", Arrays.asList(EstadoMatriculaEnum.MAT.name()))
                .orderBy("mr.prioridad asc");
        return all(sql);
    }
    
    @Override
    public MatriculaSeccion findByAlumnoSeccion(String codigo, String seccion) {
        Octavia sqlUtil = Octavia.query()
                .from(MatriculaSeccion.class, "ms")
                .join("matriculaResumen mr", "mr.alumno alu", "mr.cicloAcademico ca")
                .join("seccion sec", "sec.grupoSeccion gs", "gs.curso")
                .left("sec.aula", "sec.grupoHoras")
                .filter("alu.codigo", codigo)
                .filter("sec.codigo2", seccion)
                .limit(1);
        return find(sqlUtil);
    }
}
