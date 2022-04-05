package pe.edu.lamolina.amauta.dao.inscripcion.hibernate;

import java.util.Arrays;
import static java.util.Arrays.asList;
import java.util.List;
import pe.edu.lamolina.amauta.dao.inscripcion.PostulanteDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.PostulanteEstadoEnum;
import static pe.edu.lamolina.model.enums.PostulanteEstadoEnum.ANU;
import static pe.edu.lamolina.model.enums.PostulanteEstadoEnum.REN;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.Postulante;

@Repository
public class PostulanteDAOH extends AbstractEasyDAO<Postulante> implements PostulanteDAO {

    public PostulanteDAOH() {
        super();
        setClazz(Postulante.class);
    }

    @Override
    public List<Postulante> allByPersona(Persona persona) {
        Octavia sql = Octavia.query()
                .from(Postulante.class, "pos")
                .join("persona per")
                .filter("per.id", persona);

        return all(sql);
    }

    @Override
    public Postulante findByCodigoCiclo(String codigo, CicloPostula ciclo) {
        Octavia sql = Octavia.query()
                .from(Postulante.class, "po")
                .join("cicloPostula cip", "cip.cicloAcademico ca", "persona per", "per.tipoDocumento td")
                .leftJoin("modalidadIngreso mod", "colegioProcedencia col", "col.ubicacion uc", "col.gestion", "universidadProcedencia uni", "uni.pais")
                .filter("po.estado", "<>", ANU.name())
                .filter("po.codigo", codigo)
                .filter("cip.id", ciclo);

        return (Postulante) sql.find(getCurrentSession());
    }

    @Override
    public Postulante findByDNICiclo(String dni, CicloPostula ciclo) {
        Octavia sql = Octavia.query()
                .from(Postulante.class, "po")
                .join("modalidadIngreso mod", "cicloPostula cip", "cip.cicloAcademico ca", "persona per", "per.tipoDocumento td")
                .leftJoin("colegioProcedencia col", "col.ubicacion uc", "universidadProcedencia uni")
                .notIn("po.estado", Arrays.asList(ANU.name(), REN.name()))
                .filter("per.numeroDocIdentidad", dni)
                .filter("cip.id", ciclo);

        return find(sql);
    }

    @Override
    public Postulante findByDocIdentidadCiclo(TipoDocIdentidad tipoDoc, String nroDoc, CicloPostula ciclo) {
        Octavia sql = Octavia.query()
                .from(Postulante.class, "po")
                .join("modalidadIngreso mod", "cicloPostula cip", "cip.cicloAcademico ca", "persona per", "per.tipoDocumento td")
                .leftJoin("colegioProcedencia col", "col.ubicacion uc", "universidadProcedencia uni")
                .filter("po.estado", "<>", ANU.name())
                .filter("td.id", tipoDoc)
                .filter("per.numeroDocIdentidad", nroDoc)
                .filter("cip.id", ciclo);

        return find(sql);
    }

    @Override
    public Postulante findByPersonaCicloAcademico(Persona persona, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(Postulante.class, "po")
                .join("cicloPostula cip", "cip.cicloAcademico ca", "persona per", "per.tipoDocumento td")
                .leftJoin("modalidadIngreso mod", "colegioProcedencia col", "col.ubicacion uc", "col.gestion", "universidadProcedencia uni", "uni.pais")
                .filter("po.estado", "<>", ANU.name())
                .filter("per.id", persona)
                .filter("ca.id", cicloAcademico)
                .limit(1);

        return find(sql);
    }

    @Override
    public List<Postulante> allByDynatableRenuncia(DynatableFilter filter) {

        DynatableSql sql = new DynatableSql(filter)
                .from(Postulante.class, "po")
                .join("persona per", "per.tipoDocumento tdoc", "cicloPostula cip", "cip.cicloAcademico ca")
                .leftJoin("modalidadIngreso mod", "interesado inte", "aulaExamen ae", "ae.aula au", "inte.carreraInteres cai")
                .in("po.estado", asList(PostulanteEstadoEnum.REN, PostulanteEstadoEnum.REND))
                .searchFields("po.fechaRegistro", "mod.nombre", "po.estado", "po.codigo", "po.importePagar", "po.importeAbonado", "po.importeDescuento")
                .searchFields("tdoc.simbolo", "per.numeroDocIdentidad", "po.numeroAsiento", "au.codigo", "inte.id")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .searchComplexField("concat(coalesce(inte.paterno,''),' ',coalesce(inte.materno,''),' ',coalesce(inte.nombres,''))")
                .searchComplexField("concat(coalesce(inte.nombres,''),' ',coalesce(inte.paterno,''),' ',coalesce(inte.materno,''))")
                .orderBy("inte.id desc", "po.id desc");
        sql.beginRelativeFilters();
        return all(sql);
    }

    @Override
    public Postulante findById(Long id) {
        Octavia sql = Octavia.query()
                .from(Postulante.class, "po")
                .join("modalidadIngreso mod", "cicloPostula cip", "cip.cicloAcademico ca", "persona per", "per.tipoDocumento td")
                .leftJoin("colegioProcedencia col", "col.ubicacion uc", "universidadProcedencia uni")
                .filter("po.id", id);
        return find(sql);
    }
}
