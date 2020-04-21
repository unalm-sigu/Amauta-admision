package pe.edu.lamolina.amauta.dao.tramite.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import static pe.edu.lamolina.model.enums.ResolucionEstadoEnum.ACT;
import pe.edu.lamolina.model.tramite.CambioNotaMasBaja;
import pe.edu.lamolina.model.tramite.Reincorporacion;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.model.tramite.RetiroCiclo;
import pe.edu.lamolina.model.tramite.RetiroCurso;
import pe.edu.lamolina.amauta.dao.tramite.ResolucionDAO;

@Repository
public class ResolucionDAOH extends AbstractEasyDAO<Resolucion> implements ResolucionDAO {

    public ResolucionDAOH() {
        super();
        setClazz(Resolucion.class);
    }

    @Override
    public Resolucion find() {
        Octavia sql = new Octavia()
                .from(this.getClass())
                .join("oficina ofi", "tipoResolucion tr", "userRegistro ur")
                .left("reunionConsejo re", "userActualizacion ua")
                .left("ur.persona per", "ua.persona per 2");
        return this.find(sql);
    }

    @Override
    public List<Resolucion> allByDyna(DynatableFilter filter) {

//        Octavia sqlSubRetCicl = new Octavia()
//                .from(RetiroCiclo.class, "rci")
//                .join("resolucion re2")
//                .left("alumno alu2", "alu2.persona per3");
//
//        Octavia sqlSuNota = new Octavia()
//                .from(CambioNotaMasBaja.class, "nmb")
//                .join("resolucion re1")
//                .left("alumno alu1", "alu1.persona per2");
        Octavia sqlSubRein = new Octavia()
                .from(Reincorporacion.class, "rein")
                .join("resolucion re")
                .left("alumno alu", "alu.persona per1");

        DynatableSql sql = new DynatableSql(filter)
                .from(Resolucion.class, "res")
                .join("tipoResolucion", "oficina")
                .leftJoin("userRegistro ur", "ur.persona per")
                .searchFields("serie", "numero")
                .__().__()
                .searchSubquery(sqlSubRein)
                .__().__().searchSubqueryComplexField("concat(coalesce(per1.paterno,''),' ',coalesce(per1.materno,''),' ',coalesce(per1.nombres,''))")
                .__().__().searchSubqueryComplexField("concat(coalesce(per1.nombres,''),' ',coalesce(per1.paterno,''),' ',coalesce(per1.materno,''))")
                .searchSubqueryFields("alu.codigo", "per1.numeroDocIdentidad")
                .subqueryLinkedBy("res.id", "re.id")
                //                .__().__()
                //                .searchSubquery(sqlSuNota)
                //                .__().__().searchSubqueryComplexField("concat(coalesce(per2.paterno,''),' ',coalesce(per2.materno,''),' ',coalesce(per2.nombres,''))")
                //                .__().__().searchSubqueryComplexField("concat(coalesce(per2.nombres,''),' ',coalesce(per2.paterno,''),' ',coalesce(per2.materno,''))")
                //                .searchSubqueryFields("alu1.codigo", "per2.numeroDocIdentidad")
                //                .subqueryLinkedBy("res.id", "re1.id")
                //                .__().__()
                //                .searchSubquery(sqlSubRetCicl)
                //                .__().__().searchSubqueryComplexField("concat(coalesce(per3.paterno,''),' ',coalesce(per3.materno,''),' ',coalesce(per3.nombres,''))")
                //                .__().__().searchSubqueryComplexField("concat(coalesce(per3.nombres,''),' ',coalesce(per3.paterno,''),' ',coalesce(per3.materno,''))")
                //                .searchSubqueryFields("alu2.codigo", "per3.numeroDocIdentidad")
                //                .subqueryLinkedBy("res.id", "re2.id")
                .orderBy("res.id desc");
        return this.all(sql);
    }

    @Override
    public void updateResolucion(Resolucion resolucion) {
        Octavia octavia = Octavia.update(Resolucion.class);
        octavia.set(resolucion, "fecha");
        octavia.set(resolucion, "serie");
        octavia.set(resolucion, "numero");
        octavia.set(resolucion, "userActualizacion");
        octavia.set(resolucion, "fechaActualizacion");
        this.update(octavia);
    }

    @Override
    public void updateResolucionFile(Resolucion resolucion) {
        Octavia octavia = Octavia.update(Resolucion.class);
        octavia.set(resolucion, "rutaUrl");
        octavia.set(resolucion, "userActualizacion");
        octavia.set(resolucion, "fechaActualizacion");
        octavia.set(resolucion, "estado");
        this.update(octavia);
    }

    @Override
    public void updateEstado(Resolucion resolucion) {
        Octavia octavia = Octavia.update(Resolucion.class);
        octavia.set(resolucion, "userActualizacion");
        octavia.set(resolucion, "fechaActualizacion");
        octavia.set(resolucion, "estado");
        this.update(octavia);
    }

    @Override
    public void updateEstadoCicloRei(Resolucion resolucion) {
        Octavia octavia = Octavia.update(Resolucion.class);
        octavia.set(resolucion, "userActualizacion");
        octavia.set(resolucion, "fechaActualizacion");
        octavia.set(resolucion, "estado");
        octavia.set(resolucion, "cicloReincorporacion");
        this.update(octavia);
    }

    @Override
    public Resolucion findById(Long resolucion) {
        Octavia sql = new Octavia()
                .from(Resolucion.class)
                .join("oficina ofi", "tipoResolucion tr", "userRegistro ur", "ur.persona")
                .left("reunionConsejo re")
                .filter("id", resolucion);
        return this.find(sql);
    }

    @Override
    public List<Resolucion> allByNombre(String nombre) {
        Octavia sql = Octavia.query()
                .from(Resolucion.class, "r")
                .join("oficina o")
                .filter("r.estado", ACT)
                .beginBlock()
                .__().filter("r.serie", "like", nombre)
                .__().filter("r.numero", "like", nombre)
                .endBlock();
        return all(sql);
    }

}
