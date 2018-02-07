package pe.edu.lamolina.pivot.dao.general;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.general.Color;

public interface ColorDAO extends EasyDAO<Color> {

    public Color findLastColor();

}
