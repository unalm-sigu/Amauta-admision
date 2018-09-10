package pe.edu.lamolina.pivot.zelper;

import java.awt.BasicStroke;
import java.awt.Paint;
import java.awt.Stroke;
import org.jfree.chart.renderer.category.BarRenderer3D;

public class CustomRenderer extends BarRenderer3D {

    Stroke soild = new BasicStroke(2.0f);
    Stroke dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{10.0f}, 0.0f);

    private Paint[] colors;

    public CustomRenderer(final Paint[] colors) {
        this.colors = colors;
    }

    @Override
    public Paint getItemPaint(final int row, final int column) {
        return this.colors[column % this.colors.length];
    }

}
