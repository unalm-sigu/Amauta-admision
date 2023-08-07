package pe.edu.lamolina.amauta.dao.consejeria;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.tutoria.CitaConsejeroAlumno;
import pe.edu.lamolina.model.tutoria.ObjetivoCitaConsejero;
import pe.edu.lamolina.model.tutoria.PlanTutorial;

public interface ObjetivoCitaConsejeroDAO extends EasyDAO<ObjetivoCitaConsejero> {

    List<ObjetivoCitaConsejero> allByPlanTutorial(PlanTutorial plan);

    List<ObjetivoCitaConsejero> allByCitas(List<CitaConsejeroAlumno> citas);

    List<ObjetivoCitaConsejero> allByCita(CitaConsejeroAlumno cita);

}
