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
    private static Color UNKNOWK_DISASTER = Color.WHITE;
    private static Color BEHAVIOUR_2 = Color.BLUE;
    private static Color BEHAVIOUR_3 = Color.RED;
    private static Color BEHAVIOUR_4 = Color.GREEN;
    private static Color BEHAVIOUR_5 = Color.BLACK;
    private static Color BEHAVIOUR_6 = Color.DARK_GRAY;
    private static Color BEHAVIOUR_7 = Color.PINK;
    private static Color BEHAVIOUR_8 = Color.YELLOW;
    private int steps = -1;
    private int fugados = 0;
    private int expertosPM = 0;
    private int expertosSM = 0;
    private int seguidores = 0;
    private int unknow = 0;

    public TagGameLayer(boolean enabled) {
        super(enabled);
    }

    @Override
    public void draw(DrawableFloor dfloor, Graphics2D g)
    {
    	
    	if (steps%2 == 0){
    		System.out.print("\n\n\n\n\n");
    		System.out.println("Estadísticas:\n Expertos que conocen el desastre de primera mano: " + expertosPM);
    		System.out.println(" Expertos que conocen el desastre por otros: " + expertosSM + "\n Fugados: " + fugados + "\n Seguidores: " + seguidores);
    		System.out.println(" Desconocen el desastre: " + (unknow-expertosPM-expertosSM-fugados-seguidores));
    	}
    	
    	steps++;
    	
        final Floor f = dfloor.getFloor();
        for ( DefaultAgent p : f.getAgents() ) {
            if ( p.isDynamic() )
            {
            	if (steps%2 == 0){
            		unknow++;
            	}
                if (steps%2 == 1){
            		unknow = 0;
            	}
            	
                KPolygon poly = KPolygon.
               createRegularPolygon(3, p.getPolygon().getRadius());
                poly.scale(1, 0.6); 
                poly.rotate(p.getAngle());
                poly.translateTo(p.getX(), p.getY());
                g.setColor(UNKNOWK_DISASTER);
                g.draw(poly);
                if ("true".equals(p.getProperty("KNOWDISASTER"))) {
                    if ("false".equals(p.getProperty("STATE")) && "true".equals(p.getProperty("EXPERIENCE"))) {
                        //g.setColor(DEFAULT_PERSON_FILL_COLOR);
                    	g.setColor(BEHAVIOUR_5);
                    	if (steps%2 == 0){
                    		expertosPM++;
                    	}
                    	if (steps%2 == 1){
                    		expertosPM = 0;
                    	}
                    } 
                    if ("true".equals(p.getProperty("STATE")) && "true".equals(p.getProperty("EXPERIENCE")))  { // untagged agents
                        //g.setColor(DEFAULT_PERSON_DRAW_COLOR);
                    	g.setColor(BEHAVIOUR_2);
                    	if (steps%2 == 0){
                    		expertosSM++;
                    	}
                    	if (steps%2 == 1){
                    		expertosSM = 0;
                    	}
                    } 
                    if ("true".equals(p.getProperty("STATE")) && "false".equals(p.getProperty("EXPERIENCE")))  { // untagged agents
                        //g.setColor(DEFAULT_PERSON_DRAW_COLOR);
                    	g.setColor(BEHAVIOUR_4);
                    	if (steps%2 == 0){
                    		seguidores++;
                    	}
                    	if (steps%2 == 1){
                    		seguidores = 0;
                    	}
                    } 
                    if ("false".equals(p.getProperty("STATE")) && "false".equals(p.getProperty("EXPERIENCE")))  { // untagged agents
                        //g.setColor(DEFAULT_PERSON_DRAW_COLOR);
                    	g.setColor(BEHAVIOUR_7);
                    	if (steps%2 == 0){
                    		if (("true".equals(p.getProperty("KNOWPLACE")) && "false".equals(p.getProperty("METHODKNOWLEDGED"))) || ("false".equals(p.getProperty("KNOWPLACE")) && "true".equals(p.getProperty("METHODKNOWLEDGED")))){
                    			fugados++;
                    		}
                    	}
                    	if (steps%2 == 1){
                    		fugados = 0;
                    	}
                    }
                } else {
                    g.setColor(UNKNOWK_DISASTER);
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
