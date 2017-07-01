package tutorialfollower.myfirstmassisproject;

import java.awt.Color;
import java.awt.Graphics2D;
import com.massisframework.gui.DrawableLayer;
import com.massisframework.massis.displays.floormap.layers.DrawableFloor;
import com.massisframework.massis.model.agents.DefaultAgent;
import com.massisframework.massis.model.building.Floor;
import straightedge.geom.KPolygon;

public class DisasterSimulationLayer extends DrawableLayer<DrawableFloor> {
    private static Color STATIC_AGENT_COLOR = new Color(165,42,42);
    private static Color UNKNOWN_DISASTER = Color.WHITE;
    private static Color EXPERT_INDIRECT = Color.BLUE;
    private static Color INEXPERT_FOLLOWER = Color.GREEN;
    private static Color EXPERT_DIRECT = Color.BLACK;
    private static Color INEXPERT_PE = Color.PINK;
    private int steps = -1;
    private int inexpertPe = 0;
    private int expertDirect = 0;
    private int expertIndirect = 0;
    private int inexpertFollower = 0;
    private int unknown = 0;

    public DisasterSimulationLayer(boolean enabled) {
        super(enabled);
    }

    @Override
    public void draw(DrawableFloor dfloor, Graphics2D g)
    {
    	
    	if (steps%2 == 0){
    		System.out.print("\n\n\n\n\n");
    		System.out.println("Estadísticas:\n Expertos que conocen el desastre de primera mano: " + expertDirect);
    		System.out.println(" Expertos que conocen el desastre por otros: " + expertIndirect + "\n Fugados: " + inexpertPe + "\n Seguidores: " + inexpertFollower);
    		System.out.println(" Desconocen el desastre: " + unknown);
    	}
    	
    	steps++;
    	unknown = 0;
    	
        final Floor f = dfloor.getFloor();
        for ( DefaultAgent p : f.getAgents() ) {
            if ( p.isDynamic() )
            {
            	
                KPolygon poly = KPolygon.
               createRegularPolygon(3, p.getPolygon().getRadius());
                poly.scale(1, 0.6); 
                poly.rotate(p.getAngle());
                poly.translateTo(p.getX(), p.getY());
                g.setColor(UNKNOWN_DISASTER);
                g.draw(poly);
                if ("true".equals(p.getProperty("KNOWDISASTER"))) {
                    if ("false".equals(p.getProperty("STATE")) && "true".equals(p.getProperty("EXPERIENCE"))) {
                        //g.setColor(DEFAULT_PERSON_FILL_COLOR);
                    	g.setColor(EXPERT_DIRECT);
                    	if (steps%2 == 0){
                    		expertDirect++;
                    	}
                    	if (steps%2 == 1){
                    		expertDirect = 0;
                    	}
                    } 
                    else if ("true".equals(p.getProperty("STATE")) && "true".equals(p.getProperty("EXPERIENCE")))  { // untagged agents
                        //g.setColor(DEFAULT_PERSON_DRAW_COLOR);
                    	g.setColor(EXPERT_INDIRECT);
                    	if (steps%2 == 0){
                    		expertIndirect++;
                    	}
                    	if (steps%2 == 1){
                    		expertIndirect = 0;
                    	}
                    } 
                    else if ("true".equals(p.getProperty("STATE")) && "false".equals(p.getProperty("EXPERIENCE")))  { // untagged agents
                        //g.setColor(DEFAULT_PERSON_DRAW_COLOR);
                    	g.setColor(INEXPERT_FOLLOWER);
                    	if (steps%2 == 0){
                    		inexpertFollower++;
                    	}
                    	if (steps%2 == 1){
                    		inexpertFollower = 0;
                    	}
                    } 
                    else if ("false".equals(p.getProperty("STATE")) && "false".equals(p.getProperty("EXPERIENCE")))  { // untagged agents
                        //g.setColor(DEFAULT_PERSON_DRAW_COLOR);
                    	g.setColor(INEXPERT_PE);
                    	if (steps%2 == 0){
                    		if (("true".equals(p.getProperty("KNOWPLACE")) && "false".equals(p.getProperty("METHODKNOWLEDGED"))) || ("false".equals(p.getProperty("KNOWPLACE")) && "true".equals(p.getProperty("METHODKNOWLEDGED")))){
                    			inexpertPe++;
                    		}
                    	}
                    	if (steps%2 == 1){
                    		inexpertPe = 0;
                    	}
                    }
                } else {
                    g.setColor(UNKNOWN_DISASTER);
                    unknown++;
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
    return "DISASTER SIMULATION AGENTS";
    }
}
