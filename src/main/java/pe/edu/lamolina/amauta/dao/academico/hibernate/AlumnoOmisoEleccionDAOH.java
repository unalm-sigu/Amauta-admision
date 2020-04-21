package pe.edu.lamolina.amauta.dao.academico.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoOmisoEleccion;
import pe.edu.lamolina.model.academico.CicloAcademico;
import static pe.edu.lamolina.model.enums.DeudaEstadoEnum.DEU;
import pe.edu.lamolina.amauta.dao.academico.AlumnoOmisoEleccionDAO;

@Repository
public class AlumnoOmisoEleccionDAOH extends AbstractEasyDAO<AlumnoOmisoEleccion> implements AlumnoOmisoEleccionDAO {

    public AlumnoOmisoEleccionDAOH() {
        super();
        setClazz(AlumnoOmisoEleccion.class);
    }

    @Override
    public List<AlumnoOmisoEleccion> allOrder(DynatableFilter filter) {

        DynatableSql sql = new DynatableSql(filter)
                .from(AlumnoOmisoEleccion.class, "aoe")
                .join("alumno al", "cicloAcademico ca")
                .join("al.carrera car", "car.facultad", "al.modalidadEstudio")
                .join("al.persona per", "per.tipoDocumento")
                .searchFields("al.codigo", "per.numeroDocIdentidad")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("aoe.id", "al.id");

        return all(sql);
    }

    @Override
    public void updateAnulacion(AlumnoOmisoEleccion alumnoOmisoEleccion) {
        Octavia sql = Octavia
                .update(AlumnoOmisoEleccion.class)
                .set(alumnoOmisoEleccion, "estado")
                .set(alumnoOmisoEleccion, "motivoAnulacion")
                .set(alumnoOmisoEleccion, "fechaAnulacion")
                .set(alumnoOmisoEleccion, "userAnulacion");
        this.update(sql);
    }

    @Override
    public List<AlumnoOmisoEleccion> allByCiclo(List<CicloAcademico> cicloAcademicos) {
        Octavia sql = new Octavia()
                .from(AlumnoOmisoEleccion.class, "aoe")
                .join("alumno al", "cicloAcademico ca")
                .in("ca.id", cicloAcademicos);
        return all(sql);
    }

    @Override
    public AlumnoOmisoEleccion findByAlumnoCicloMotivo(AlumnoOmisoEleccion omisoEleccion) {
        Octavia sql = new Octavia()
                .from(AlumnoOmisoEleccion.class, "aoe")
                .join("alumno al", "cicloAcademico ca")
                .filter("motivo", omisoEleccion.getMotivo())
                .filter("al.id", omisoEleccion.getAlumno())
                .filter("ca.id", omisoEleccion.getCicloAcademico())
                .filter("estado", DEU.name());
        return find(sql);
    }

    @Override
    public List<AlumnoOmisoEleccion> allDeudasByAlumno(Alumno alumno) {
        Octavia sql = new Octavia()
                .from(AlumnoOmisoEleccion.class, "aoe")
                .join("alumno al", "cicloAcademico ca")
                .filter("estado", DEU.name())
                .filter("al.id", alumno);
        return all(sql);
    }

}
