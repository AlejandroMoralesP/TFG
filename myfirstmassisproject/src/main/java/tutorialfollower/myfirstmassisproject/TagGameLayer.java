package tutorialfollower.myfirstmassisproject;

import java.awt.Color;
import java.awt.Graphics2D;
import com.massisframework.gui.DrawableLayer;
import com.massisframework.massis.displays.floormap.layers.DrawableFloor;
import com.massisframework.massis.model.agents.DefaultAgent;
import com.massisframework.massis.model.building.Floor;
import straightedge.geom.KPolygon;

public class TagGameLayer extends DrawableLayer<DrawableFloor> {
    private static Color STATIC_AGENT_COLOR = new Color(165,42,42);
    private static Color DEFAULT_PERSON_FILL_COLOR = Color.WHITE;
    private static Color DEFAULT_PERSON_DRAW_COLOR = Color.BLUE;
    private static Color TAGGED_PERSON_DRAW_COLOR = Color.RED;
    private static Color UNTAGGED_PERSON_DRAW_COLOR = Color.GREEN;

    public TagGameLayer(boolean enabled) {
        super(enabled);
    }

    @Override
    public void draw(DrawableFloor dfloor, Graphics2D g)
    {
        final Floor f = dfloor.getFloor();
        for ( DefaultAgent p : f.getAgents() ) {
            if ( p.isDynamic() )
            {
                KPolygon poly = KPolygon.
               createRegularPolygon(3, p.getPolygon().getRadius());
                poly.scale(1, 0.6); 
                poly.rotate(p.getAngle());
                poly.translateTo(p.getX(), p.getY());
                g.setColor(DEFAULT_PERSON_DRAW_COLOR);
                g.draw(poly);
                if (p.hasProperty("TAGGED")) {
                    if ("true".equals(p.getProperty("TAGGED"))) {
                        g.setColor(TAGGED_PERSON_DRAW_COLOR);
                    } else { // untagged agents
                        g.setColor(UNTAGGED_PERSON_DRAW_COLOR);
                    }
                } else {
                    g.setColor(DEFAULT_PERSON_FILL_COLOR);
                }
                g.fill(poly);

            }
            else
            {
                g.setColor(STATIC_AGENT_COLOR);
                g.fill(p.getPolygon());
            }
        }
    }

    @Override
    public String getName() {
    return "TAGGED AGENTS";
    }
}
