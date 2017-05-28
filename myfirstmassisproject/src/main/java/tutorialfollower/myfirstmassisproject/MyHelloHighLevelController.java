package tutorialfollower.myfirstmassisproject;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import com.massisframework.massis.model.agents.HighLevelController;
import com.massisframework.massis.model.agents.LowLevelAgent;
import com.massisframework.massis.model.building.Floor;
import com.massisframework.massis.model.building.SimRoom;
import com.massisframework.massis.model.location.Location;
import com.massisframework.massis.model.managers.movement.ApproachCallback;
import com.massisframework.massis.pathfinding.straightedge.FindPathResult.PathFinderErrorReason;

import straightedge.geom.KPolygon;
import straightedge.geom.path.PathBlockingObstacle;
import sun.tools.tree.ThisExpression;

public class MyHelloHighLevelController extends HighLevelController {

	private static final long serialVersionUID = 1L;
	private static final double tag_max_distance = 50;
	private static final double search_range = 200;
	private Location currentTarget;
	private int byExpert=0;
	private Location disasterLocation = null;
	public int numberOfFollowers = 0;
	
	
	public boolean isTagged() {
	    return "true".equals(this.agent.getProperty("TAGGED"));
	}
	public void setTagged(boolean tagged) {
	    this.agent.setProperty("TAGGED", String.valueOf(tagged));
	}
	
	public boolean isKnowPlace() {
	    return "true".equals(this.agent.getProperty("KNOWPLACE"));
	}
	public void setKnowPlace(boolean knowplace) {
	    this.agent.setProperty("KNOWPLACE", String.valueOf(knowplace));
	}
	
	public boolean isExperienced() {
	    return "true".equals(this.agent.getProperty("EXPERIENCE"));
	}
	public void setExperienced(boolean experience) {
	    this.agent.setProperty("EXPERIENCE", String.valueOf(experience));
	}
	
	public boolean isKnowDisaster() {
	    return "true".equals(this.agent.getProperty("KNOWDISASTER"));
	}
	public void setKnowDisaster(boolean knowdisaster) {
	    this.agent.setProperty("KNOWDISASTER", String.valueOf(knowdisaster));
	}
	
	public boolean isMethodKnowledged() {
	    return "true".equals(this.agent.getProperty("METHODKNOWLEDGED"));
	}
	public void setMethodKnowledged(boolean methodknowledged) {
	    this.agent.setProperty("METHODKNOWLEDGED", String.valueOf(methodknowledged));
	}
	
	public int isbyExpert() {
	    return byExpert;
	}
	public void setbyExpert(int answer) {
	    byExpert = answer;
	}
	
	public Location getdisasterLocation() {
	    return disasterLocation;
	}
	public void setDisasterLocation(Location disasterLoc) {
	    disasterLocation = disasterLoc;
	}
	
	boolean DisasterRange(double range) {
		boolean disaster=false; 
		
		for (LowLevelAgent otherAgent : this.agent.getAgentsInRange(range)) {
			if ("true".equals(otherAgent.getProperty("DISASTER"))){
				disaster = true;
				if ("false".equals(this.agent.getProperty("EXPERIENCE"))){
					setDisasterLocation(otherAgent.getLocation());
				}
			}
		}
		return disaster;
	}
	
	private int ByExpertRange(double range) { 

		for (LowLevelAgent otherAgent : this.agent.getAgentsInRange(range)) {
			if ("true".equals(otherAgent.getProperty("KNOWDISASTER")) ){
				if ("true".equals(otherAgent.getProperty("EXPERIENCE"))){
					setbyExpert(2);
				}
				else {
					setbyExpert(1);
					final MyHelloHighLevelController agent = (MyHelloHighLevelController) otherAgent.getHighLevelData();
					setDisasterLocation(agent.getdisasterLocation());
				}
			}
		}
		return isbyExpert();
	}
	
	private void followMeUnexperienced(double range){
		for (LowLevelAgent otherAgent : this.agent.getAgentsInRange(range)) {
			if (numberOfFollowers < 4 && otherAgent.getID() != 57){
				final MyHelloHighLevelController follower = (MyHelloHighLevelController) otherAgent.getHighLevelData();
				if ("false".equals(otherAgent.getProperty("EXPERIENCE")) && follower.isFollowingSomeone() == null && follower.getdisasterLocation() != null){
					follower.setKnowDisaster(true);
					follower.setFollowTarget(this.agent);
					follower.setDisasterLocation(null);
					numberOfFollowers++;
					System.out.println("Sigueme");
				}
			}
		}
	}
	
	private boolean sigueloael(double range){
		for (LowLevelAgent otherAgent : this.agent.getAgentsInRange(range)) {
			if (otherAgent.getID() != 57){
				final MyHelloHighLevelController leader = (MyHelloHighLevelController) otherAgent.getHighLevelData();
				if ("true".equals(otherAgent.getProperty("KNOWDISASTER")) && leader.numberOfFollowers < 3){
					if ("true".equals(otherAgent.getProperty("EXPERIENCE")) && this.isFollowingSomeone() == null && leader.getdisasterLocation() == null){
						setFollowTarget(leader.agent);
						leader.numberOfFollowers++;
						System.out.println("Sigo al valiente");
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	void KeepMoving(){
		System.out.println("Como me sale de los cojones");
		if (this.currentTarget == null) {
       		/* 1 */ SimRoom currentRoom = this.agent.getRoom();
       		/* 2 */ this.currentTarget = currentRoom.getRandomLoc();
    	}

		ApproachCallback callback = new ApproachCallback() {
			@Override
			public void onTargetReached(LowLevelAgent agent) {
				// Target has been reached.
				currentTarget = null;
			}

			@Override
			public void onSucess(LowLevelAgent agent) {
				// Everything ok. The agent has moved a little bit.
			}

			@Override
			public void onPathFinderError(PathFinderErrorReason reason) {
				// Error!
				Logger.getLogger(MyHelloHighLevelController.class.getName())
            		.log(Level.SEVERE,
            				"Error when approaching to {0} Reason: {1}",
            				new Object[] { currentTarget, reason });
			}
		};
   		
		/* 3 */ this.agent.approachTo(this.currentTarget, callback);
		
	}
	
	private Location destino = null;
	
	void fuga(){
		int aux = this.agent.getRoom().getRoomsOrderedByDistance().size()/2;
		int last = this.agent.getRoom().getRoomsOrderedByDistance().size()-1;
		
		if(destino == null){
			for(int i = 0; i < last+1 ; i++){
				if(this.agent.getRoom() == this.agent.getRoom().getRoomsOrderedByDistance().get(i)){
					if(i<=aux){
						destino = this.agent.getRoom().getRoomsOrderedByDistance().get(last).getRandomLoc();
					}
					else{
						destino = this.agent.getRoom().getRoomsOrderedByDistance().get(0).getRandomLoc();
					}
				}
			}
		}
		//Location destino = this.agent.getLocation().getFloor().getDoors().get(0).getLocation();

		ApproachCallback callback = new ApproachCallback() {
			@Override
			public void onTargetReached(LowLevelAgent agent) {
				// Target has been reached.
			}

			@Override
			public void onSucess(LowLevelAgent agent) {
				// Everything ok. The agent has moved a little bit.
			}

			@Override
			public void onPathFinderError(PathFinderErrorReason reason) {
				// Error!
				Logger.getLogger(MyHelloHighLevelController.class.getName())
           			.log(Level.SEVERE,
           					"Error when approaching to {0} Reason: {1}",
           					new Object[] { destino, reason });
			}
		};
		/* 3 */ this.agent.approachTo(destino, callback);

	}
	
	private SimRoom Room;
	
	void escaparse(){
		if (destino == null){
			Room = this.agent.getRandomRoom();
			
			while (Room == this.agent.getRoom()){
				Room = this.agent.getRandomRoom();
			}
			
			destino = Room.getRandomLoc();
		}
		
		ApproachCallback callback = new ApproachCallback() {
			@Override
			public void onTargetReached(LowLevelAgent agent) {
				// Target has been reached.
			}

			@Override
			public void onSucess(LowLevelAgent agent) {
				// Everything ok. The agent has moved a little bit.
			}

			@Override
			public void onPathFinderError(PathFinderErrorReason reason) {
				// Error!
				Logger.getLogger(MyHelloHighLevelController.class.getName())
           			.log(Level.SEVERE,
           					"Error when approaching to {0} Reason: {1}",
           					new Object[] { destino, reason });
			}
		};
		/* 3 */ this.agent.approachTo(destino, callback);
		
	}
	
	void UnexperiencedBehaviour(){
		if(!isKnowDisaster()){
			if (this.currentTarget == null) {
	       		/* 1 */ SimRoom currentRoom = this.agent.getRoom();
	       		/* 2 */ this.currentTarget = currentRoom.getRandomLoc();
	    	}
			
			ApproachCallback callback = new ApproachCallback() {
				@Override
				public void onTargetReached(LowLevelAgent agent) {
					// Target has been reached.
					currentTarget = null;
				}

				@Override
				public void onSucess(LowLevelAgent agent) {
					// Everything ok. The agent has moved a little bit.
					if(DisasterRange(300)){
						System.out.println("Veo el desastre");
						setKnowDisaster(true);
						setMethodKnowledged(false);
					}
					
					else if(ByExpertRange(300) == 2 || ByExpertRange(300) == 1){
						System.out.println("Me entero del desastre por otro");
						setKnowDisaster(true);
						setMethodKnowledged(true);
					}
				}

				@Override
				public void onPathFinderError(PathFinderErrorReason reason) {
					// Error!
					Logger.getLogger(MyHelloHighLevelController.class.getName())
	            		.log(Level.SEVERE,
	            				"Error when approaching to {0} Reason: {1}",
	            				new Object[] { currentTarget, reason });
				}
			};
	   		
			/* 3 */ this.agent.approachTo(this.currentTarget, callback);
		}
		else {
			if(isKnowPlace()){
				if(isMethodKnowledged()){
					if(isbyExpert() == 2){
						//Lo sigue						
						follow(isFollowingSomeone());
						sigueloael(300);
						
					}
					else if(isbyExpert() == 1){
						//Huye y alerta a demas
						setDisasterLocation(null);
						fuga();
					}
				}
				else{
					//Huye y alerta a demas
					//System.out.println(this.agent.getLocation().getFloor().getDoors().get(1).getLocation());
					setDisasterLocation(null);
					fuga();
				}
			}
			else {
				if(isMethodKnowledged()){
					//Niega el hecho y sigue igual
					if(isbyExpert() == 2){
						//Lo sigue
						follow(isFollowingSomeone());
						sigueloael(300);
						
						
						
					}
					else if(isbyExpert() == 1){
						//Huye y alerta a demas
						setDisasterLocation(null);
						escaparse();
					}

				}
				else{
					//Pánico y paralizado
					sigueloael(300);
					follow(isFollowingSomeone());
				}
			}
		}
	}
	
	void ExperiencedBehaviour(){
		if(!isKnowDisaster()){
			if (this.currentTarget == null) {
	       		/* 1 */ SimRoom currentRoom = this.agent.getRoom();
	       		/* 2 */ this.currentTarget = currentRoom.getRandomLoc();
	    	}

			ApproachCallback callback = new ApproachCallback() {
				@Override
				public void onTargetReached(LowLevelAgent agent) {
					// Target has been reached.
					currentTarget = null;
				}

				@Override
				public void onSucess(LowLevelAgent agent) {
					// Everything ok. The agent has moved a little bit.
					if(DisasterRange(300)){
						System.out.println("Veo el desastre");
						setKnowDisaster(true);
						setMethodKnowledged(false);
					}
					
					else if(ByExpertRange(300) == 2 || ByExpertRange(300) == 1){
						System.out.println("Me entero del desastre por otro");
						setKnowDisaster(true);
						setMethodKnowledged(true);
					}
				}

				@Override
				public void onPathFinderError(PathFinderErrorReason reason) {
					// Error!
					Logger.getLogger(MyHelloHighLevelController.class.getName())
	            		.log(Level.SEVERE,
	            				"Error when approaching to {0} Reason: {1}",
	            				new Object[] { currentTarget, reason });
				}
			};
	   		
			/* 3 */ this.agent.approachTo(this.currentTarget, callback);
		}
		else {
			if(isMethodKnowledged()){
				if(isbyExpert() == 2){
					if(isKnowPlace()){
						//busca a otros agentes y va a la sala mas alejada
						followMe();
					}
					else{
						//reune a otros agentes en otra sala
						followMe();
						if(numberOfFollowers == 3) {
							escaparse();
						}
					}
				}
				else if (isbyExpert() == 1){
					//ir a ver que pasa
					if (getdisasterLocation() != null){
						whatsUp(getdisasterLocation());
					}
					if(isKnowPlace()){
						//busca a otros agentes y va a la sala mas alejada
						followMe();
						if(numberOfFollowers == 3) {
							fuga();
						}
						
					}
					else{
						//reune a otros agentes en otra sala
						followMe();
						if(numberOfFollowers == 3) {
							escaparse();
						}
					}
				}
			}
			else {
				if(isbyExpert() == 0){
					if(isKnowPlace()){
						//busca a otros agentes y va a la sala mas alejada
						followMe();
						if(numberOfFollowers == 3) {
							fuga();
						}
					}
					else{
						//reune a otros agentes en otra sala
						followMe();
						if(numberOfFollowers == 3) {
							escaparse();
						}
					}
				}
			}
		}
	}
	
	private void whatsUp (Location target){
		
		ApproachCallback callback = new ApproachCallback() {
			@Override
			public void onTargetReached(LowLevelAgent agent) {
				// Target has been reached.
			}

			@Override
			public void onSucess(LowLevelAgent agent) {
				// Everything ok. The agent has moved a little bit.
				if(DisasterRange(300)){
					System.out.println("LOO VEEOOO343434");
					setDisasterLocation(null);
				}
			}

			@Override
			public void onPathFinderError(PathFinderErrorReason reason) {
				// Error!
				Logger.getLogger(MyHelloHighLevelController.class.getName())
            		.log(Level.SEVERE,
            				"Error when approaching to {0} Reason: {1}",
            				new Object[] { currentTarget, reason });
			}
		};
   		
		/* 3 */ this.agent.approachTo(target, callback);
	}
	
	private void followMe(){
		if(numberOfFollowers < 3){
			if (this.randomTarget == null) {
       			/* 1 */ SimRoom currentRoom = this.agent.getRoom();
       			/* 2 */ this.randomTarget = currentRoom.getRandomLoc();
    		}
	    	this.agent.approachTo(this.randomTarget, new ApproachCallback() {

	        	@Override
	        	public void onTargetReached(LowLevelAgent agent) {
	        		randomTarget = null;
	        	}

	        	@Override
	        	public void onSucess(LowLevelAgent agent) {
	        		followMeUnexperienced(300);
	        	}

	        	@Override
	        	public void onPathFinderError(PathFinderErrorReason reason) {
	            	// Error!
	        		Logger.getLogger(MyHelloHighLevelController.class.getName())
	                    	.log(Level.SEVERE,
	                    		"Error when finding path Reason: {0}", reason);
	        	}
	    	});
		}
	}
	
	private LowLevelAgent followTarget = null;
	
	public void setFollowTarget(LowLevelAgent agent) {
	    followTarget = agent;
	}

	public LowLevelAgent isFollowingSomeone() {
	    return followTarget;
	}
	
	void follow(LowLevelAgent agent){
		if (isFollowingSomeone() != null) {
		      this.agent.approachTo(this.followTarget.getLocation(),
		        new ApproachCallback() {

		          @Override
		          public void onTargetReached(LowLevelAgent agent) {
		            // Nothing. We are going to follow the target forever.

		          }

		          @Override
		          public void onSucess(LowLevelAgent agent) {
		            // Continue following the target.
		          }

		          @Override
		          public void onPathFinderError(PathFinderErrorReason reason) {
		            Logger.getLogger(MyHelloHighLevelController.class.getName()).log(
		                Level.SEVERE,
		                "An error occurred when approaching to {0}. Reason: {1}",
		                new Object[] { followTarget.getLocation(), reason });

		          }
		        });
		    }		
	}
	
	void printAgentsIDsInRange(double range) {
	    StringBuilder sb = new StringBuilder();

	    for (LowLevelAgent otherAgent : this.agent.getAgentsInRange(range)) {
	    final int otherId = otherAgent.getID();
	    final Location agentLoc = this.agent.getLocation();
	    final Location otherLoc = otherAgent.getLocation();
	    final double distance = agentLoc.distance2D(otherLoc);
	    if ("true".equals(otherAgent.getProperty("DISASTER"))){
	    	System.out.println("MAAAAAAAAAAAAAMBO");
	    }
	   

	    sb.append("\tAgent #").append(otherId).append(". distance: ")
	            .append(distance).append("\n");
	    }
	    if (sb.length() > 0) {
	    System.out.println("Agent #" + this.agent.getID()+
	        " has in the range of "+range+" cm:");
	    
	    System.out.println(sb.toString());
	    }
	}
	
	public MyHelloHighLevelController(LowLevelAgent agent, Map<String, String> metadata, String resourcesFolder) {
		super(agent, metadata, resourcesFolder);
		this.agent.setHighLevelData(this);
		
		String taggedStr = metadata.get("KNOWPLACE");
	    
	    if (taggedStr == null || !"true".equals(metadata.get("KNOWPLACE"))) {
	        this.setKnowPlace(false);
	    } else {
	        this.setKnowPlace(true);
	    }
	    
	    taggedStr = metadata.get("EXPERIENCE");
	    
	    if (taggedStr == null || !"true".equals(metadata.get("EXPERIENCE"))) {
	        this.setExperienced(false);
	    } else {
	        this.setExperienced(true);
	    }
	    
	    taggedStr = metadata.get("KNOWDISASTER");
	    
	    if (taggedStr == null || !"true".equals(metadata.get("KNOWDISASTER"))) {
	        this.setKnowDisaster(false);
	    } else {
	        this.setKnowDisaster(true);
	    }
	    
	    taggedStr = metadata.get("METHODKNOWLEDGED");
	    
	    if (taggedStr == null || !"true".equals(metadata.get("METHODKNOWLEDGED"))) {
	        this.setMethodKnowledged(false);
	    } else {
	        this.setMethodKnowledged(true);
	    }
	    
	}
	
	private MyHelloHighLevelController getNearestAgent (double range, boolean tagStatus) {

		/* Set a high limit */
		double minDist = Float.MAX_VALUE;

		/* Location of this agent */
		final Location agentLoc = this.agent.getLocation();

		/* Nearest agent found */
		MyHelloHighLevelController nearest = null;
		for (LowLevelAgent otherAgent : this.agent.getAgentsInRange(range)) {
			/*
			 * Retrieve the high-level data of the other agent. It should be of
			 * the type of agent playing this game, MyHelloHighLevelController.
			 */
			final Object highLevelData = otherAgent.getHighLevelData();
			if (highLevelData instanceof MyHelloHighLevelController) {
				MyHelloHighLevelController otherCtrl =
						(MyHelloHighLevelController) highLevelData;
				/*
				 * Check the search condition
				 */
				if (otherCtrl.isTagged() == tagStatus) {
					final Location otherLoc = otherAgent.getLocation();
					final double distance = agentLoc.distance2D(otherLoc);
					/*
					 * Store if nearest.
					 */
					if (distance < minDist) {
						nearest = otherCtrl;
						minDist=distance;
					}
				}
			}
		}
		return nearest;
	}
	
	private boolean seesTaggedAgent() {
	    // true, because is tagged
	    return getNearestAgent(search_range, true) != null;
	}
	
	private boolean isDistanceLessThan50cm(LowLevelAgent a1, LowLevelAgent a2) {
		return a1.getLocation().distance2D(a2.getLocation())<50;
	}

	@Override
	public void stop() {
		/*
		 * Clean resources, threads...etc
		 */
	}

	@Override
	public void step() {
		if(this.agent.getID()==12){
			
		}
		if (this.isExperienced()) {
	        ExperiencedBehaviour();
	    } else {
			UnexperiencedBehaviour();
	    }
	}
	
	private void runAsNotTagged() {
        // sees tagged agent?
        if (seesTaggedAgent()) {
            /*
             * Retrieve the polygon associated with the current room
             */
            KPolygon polygon = this.agent.getRoom().getPolygon();
            /*
             * If the agent's target is in the same room of the agent (and in
             * the same room as the "tagged" one), select a new target in a
             * different room.
             */
            while (this.randomTarget == null
                    || polygon.contains(this.randomTarget.getXY())) {
                this.randomTarget = this.agent.getRandomRoom().getRandomLoc();
            }
        }
        this.moveRandomly();
    }
	
	private void runAsTagged() {
	    final Location agentLoc = this.agent.getLocation();
	    // Sees un-tagged agent?
	    MyHelloHighLevelController nearest = getNearestAgent(search_range, false);
	    if (nearest != null) {
	        final Location nearestLoc = nearest.agent.getLocation();
	        // Yes, and is the closest one.
	        // if distance < 0.5 m, (50 cm), tag it
	        final double distance = agentLoc.distance2D(nearestLoc);
	        if (distance < tag_max_distance) {
	            // tag it
	            nearest.setTagged(true);
	            System.out.println("COGIDO");
	            // un-tag itself
	            this.setTagged(false);
	            System.out.println("ME LIBRO");
	            // end
	        } else {
	            // chase him
	            this.agent.approachTo(nearestLoc, new ApproachCallback() {

	                @Override
	                public void onTargetReached(LowLevelAgent agent) {
	                    // Nothing this time. We are handling the logic
	                    // elsewhere.
	                }

	                @Override
	                public void onSucess(LowLevelAgent agent) {
	                }

	                @Override
	                public void onPathFinderError(
	                        PathFinderErrorReason reason) {
	                    // Error!
	                    Logger.getLogger(
	                            MyHelloHighLevelController.class.getName())
	                            .log(Level.SEVERE,
	                                    "Error when approaching to {0} Reason: {1}",
	                                    new Object[] { nearestLoc, reason });
	                }
	            });
	        }
	    } else {
	        // no target found:
	        this.moveRandomly();
	    }

	}
	
	private Location randomTarget = null;

	private void moveRandomly() {
	    if (this.randomTarget == null) {
	        Location randomLocation = this.agent.getRandomRoom().getRandomLoc();
	        this.randomTarget = randomLocation;
	    }
	    this.agent.approachTo(this.randomTarget, new ApproachCallback() {

	        @Override
	        public void onTargetReached(LowLevelAgent agent) {
	            randomTarget = null;
	        }

	        @Override
	        public void onSucess(LowLevelAgent agent) {
	        }

	        @Override
	        public void onPathFinderError(PathFinderErrorReason reason) {
	            // Error!
	            Logger.getLogger(MyHelloHighLevelController.class.getName())
	                    .log(Level.SEVERE,
	                            "Error when finding path Reason: {0}", reason);
	        }
	    });
	}
	
	private void prueba(){
		System.out.println(this.agent.getRoom().getRoomsOrderedByDistance());
	}
}