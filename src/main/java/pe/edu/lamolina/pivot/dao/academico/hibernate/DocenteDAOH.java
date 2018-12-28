package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import pe.edu.lamolina.pivot.dao.academico.DocenteDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.general.Persona;

@Repository
public class DocenteDAOH extends AbstractEasyDAO<Docente> implements DocenteDAO {

    public DocenteDAOH() {
        super();
        setClazz(Docente.class);
    }

    @Override
    public List<Docente> allByFacultadesDyantable(DynatableFilter filter, List<DepartamentoAcademico> departamento) {
        DynatableSql sql = new DynatableSql(filter)
                .from(Docente.class, "doc")
                .join("persona per", "departamentoAcademico da", "da.facultad fa")
                .leftJoin("per.tipoDocumento tdoc")
                .in("da.id", departamento)
                .searchFields("per.numeroDocIdentidad", "per.telefono", "per.celular", "per.emailCompania", "tdoc.simbolo")
                .searchFields("da.nombre", "fa.nombre")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("doc.id desc");

        return all(sql);
    }

    @Override
    public Docente find(long id) {
        Octavia sql = Octavia.query()
                .from(Docente.class, "doc")
                .join("persona per")
                .filter("doc.id", id);

        return find(sql);
    }

    @Override
    public Docente findByCode(String codigo) {
        Octavia sql = Octavia.query()
                .from(Docente.class, "doc")
                .leftJoin("persona per", "modalidadEstudio", "departamentoAcademico")
                .filter("doc.codigo", codigo);

        return find(sql);
    }

    @Override
    public List<Docente> allByPersona(Persona persona) {
        Octavia sql = Octavia.query()
                .from(Docente.class, "doc")
                .join("persona per")
                .leftJoin("modalidadEstudio", "departamentoAcademico")
                .filter("per.id", persona);

        return all(sql);
    }

    @Override
    public List<Docente> allActivos(ModalidadEstudio modalidad) {
        Octavia sql = Octavia.query()
                .from(Docente.class, "doc")
                .join("persona per", "modalidadEstudio me")
                .leftJoin("departamentoAcademico")
                .filter("doc.estado", EstadoEnum.ACT)
                .filter("me.id", modalidad);

        return all(sql);
    }

    @Override
    public List<Docente> allByFilter(DynatableFilter filter, List<DepartamentoAcademico> dptos) {
        DynatableSql sql = new DynatableSql(filter)
                .from(Docente.class, "doc")
                .join("persona per", "departamentoAcademico da", "da.facultad fa")
                .leftJoin("per.tipoDocumento tdoc")
                .in("da.id", dptos)
                .searchFields("per.numeroDocIdentidad", "per.telefono", "per.celular", "per.emailCompania", "tdoc.simbolo")
                .searchFields("da.nombre", "fa.nombre")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("doc.id desc");

        return all(sql);
    }

    @Override
    public Docente findByDocente(Docente docente) {
        Octavia sql = Octavia.query()
                .from(Docente.class, "doc")
                .join("persona per")
                .leftJoin("modalidadEstudio me", "departamentoAcademico", "per.paisDomicilio")
                .filter("doc.id", docente);

        return find(sql);
    }

    @Override
    public List<Docente> allCoordinadoresByIdDptoName(Long idDpto, String nombre) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        Octavia sql = Octavia.query()
                .from(Docente.class, "do")
                .join("persona per", "departamentoAcademico dpto")
                .filter("dpto.id", idDpto)
                .__().complexFilter("concat(per.paterno,' ',per.materno,' ',per.nombres)", "like", nombre)
                .__().complexFilter("concat(per.nombres,' ',per.paterno,' ',per.materno)", "like", nombre)
                .limit(15);
        return sql.all(getCurrentSession());
    }

    @Override
    public List<Docente> allByNombreFilter(String nombre, Integer limit, String codigoDep) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        Octavia sql = Octavia.query()
                .from(Docente.class, "doc")
                .join("persona per", "departamentoAcademico da")
                .beginBlock()
                .__().complexFilter("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))", "like", nombre)
                .__().complexFilter("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))", "like", nombre)
                .__().filter("doc.codigo", "like", nombre)
                .__().filter("da.nombre", "like", nombre)
                .endBlock();
        if (codigoDep != null) {
            sql.filter("da.codigo", codigoDep);
        }

        return sql.all(getCurrentSession());
    }

    @Override
    public List<Docente> allByDptoEstado(Long idDpto, String estado) {
        Octavia sql = Octavia.query()
                .from(Docente.class, "doc")
                .join("persona per", "departamentoAcademico dpto")
                .filter("dpto.id", idDpto)
                .filter("doc.estado", estado);
        return all(sql);
    }

    @Override
    public List<Docente> allByNombreDepartamento(String nombre, DepartamentoAcademico departamento, int limit) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        Octavia sql = Octavia.query()
                .from(Docente.class, "doc")
                .join("persona per", "departamentoAcademico da")
                .leftJoin("per.tipoDocumento")
                .filter("da.id", departamento)
                .beginBlock()
                .__().complexFilter("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))", "like", nombre)
                .__().complexFilter("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))", "like", nombre)
                .__().filter("doc.codigo", "like", nombre)
                .endBlock()
                .limit(limit);

        return sql.all(getCurrentSession());
    }

}
