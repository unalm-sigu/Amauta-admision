package pe.edu.lamolina.pivot.dao.encuesta.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaDocenteModalidad;
import pe.edu.lamolina.pivot.dao.encuesta.EncuestaDocenteModalidadDAO;

@Repository
public class EncuestaDocenteModalidadDAOH extends AbstractEasyDAO<EncuestaDocenteModalidad> implements EncuestaDocenteModalidadDAO {

    public EncuestaDocenteModalidadDAOH() {
        super();
        setClazz(EncuestaDocenteModalidad.class);
    }

    @Override
    public List<EncuestaDocenteModalidad> allByCiclo(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(EncuestaDocenteModalidad.class, "edm")
                .join("docente d", "modalidadEstudio me", "cicloAcademico ca", "d.persona per", "d.departamentoAcademico da", "da.facultad f")
                //.filter("me.codigo", ModalidadEstudioEnum.PRE)
                .filter("ca.id", cicloAcademico);

        return all(sql);
    }

    @Override
    public List<EncuestaDocenteModalidad> allConEncuestadosByCiclo(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(EncuestaDocenteModalidad.class, "edm")
                .join("docente d", "modalidadEstudio me", "cicloAcademico ca", "d.persona per", "d.departamentoAcademico da", "da.facultad f")
                //.filter("me.codigo", ModalidadEstudioEnum.PRE)
                .filter("edm.alumnosEncuestados", ">", 0)
                .filter("ca.id", cicloAcademico);

        return all(sql);
    }

    @Override
    public List<EncuestaDocenteModalidad> allByDynatableCicloAcademico(DynatableFilter filter, CicloAcademico ciclo) {
        DynatableSql sql = new DynatableSql(filter)
                .from(EncuestaDocenteModalidad.class, "edm")
                .join("docente d", "modalidadEstudio me", "cicloAcademico ca", "d.persona per", "per.tipoDocumento tdoc")
                .join("d.departamentoAcademico da", "da.facultad fa")
                .searchFields("da.nombre", "fa.nombre", "d.codigo")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .filter("ca.id", ciclo)
                //.filter("me.codigo", ModalidadEstudioEnum.PRE)
                .orderBy("edm.id desc");

        return all(sql);
    }

    @Override
    public List<EncuestaDocenteModalidad> allByDynatableCicloAcademicoDocente(DynatableFilter filter, CicloAcademico ciclo, Docente docente) {
        DynatableSql sql = new DynatableSql(filter)
                .from(EncuestaDocenteModalidad.class, "edm")
                .join("docente d", "modalidadEstudio me", "cicloAcademico ca", "d.persona per", "per.tipoDocumento tdoc")
                .join("d.departamentoAcademico da", "da.facultad fa")
                .searchFields("per.numeroDocIdentidad", "per.telefono", "per.celular", "per.emailCompania", "tdoc.simbolo")
                .searchFields("da.nombre", "fa.nombre")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .filter("ca.id", ciclo)
                .filter("d.id", docente)
                .orderBy("edm.id desc");

        return all(sql);
    }

}
