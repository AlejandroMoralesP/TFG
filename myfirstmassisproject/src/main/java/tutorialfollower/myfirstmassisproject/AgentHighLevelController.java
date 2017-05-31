package tutorialfollower.myfirstmassisproject;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.massisframework.massis.model.agents.HighLevelController;
import com.massisframework.massis.model.agents.LowLevelAgent;
import com.massisframework.massis.model.building.SimRoom;
import com.massisframework.massis.model.location.Location;
import com.massisframework.massis.model.managers.movement.ApproachCallback;
import com.massisframework.massis.pathfinding.straightedge.FindPathResult.PathFinderErrorReason;

public class AgentHighLevelController extends HighLevelController {

	private static final long serialVersionUID = 1L;
	private static final double search_range = 300;
	private Location currentTarget;
	private int byExpert=0;
	private Location disasterLocation = null;
	public int numberOfFollowers = 0;
	private boolean stop = false;
	
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
	
	public boolean state() {
	    return "true".equals(this.agent.getProperty("STATE"));
	}
	public void setState(boolean state) {
	    this.agent.setProperty("STATE", String.valueOf(state));
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
					final AgentHighLevelController agent = (AgentHighLevelController) otherAgent.getHighLevelData();
					setDisasterLocation(agent.getdisasterLocation());
				}
			}
		}
		return isbyExpert();
	}
	
	private void followMeUnexperienced(double range){
		for (LowLevelAgent otherAgent : this.agent.getAgentsInRange(range)) {
			if (numberOfFollowers < 4 && otherAgent.getID() != 57){
				final AgentHighLevelController follower = (AgentHighLevelController) otherAgent.getHighLevelData();
				if ("false".equals(otherAgent.getProperty("EXPERIENCE")) && follower.isFollowingSomeone() == null && follower.getdisasterLocation() != null){
					follower.setKnowDisaster(true);
					follower.setFollowTarget(this.agent);
					follower.setDisasterLocation(null);
					numberOfFollowers++;
					//System.out.println("Sigueme");
				}
			}
		}
	}
	
	private boolean sigueloael(double range){
		for (LowLevelAgent otherAgent : this.agent.getAgentsInRange(range)) {
			if (otherAgent.getID() != 57){
				final AgentHighLevelController leader = (AgentHighLevelController) otherAgent.getHighLevelData();
				if ("true".equals(otherAgent.getProperty("KNOWDISASTER")) && leader.numberOfFollowers < 3){
					if ("true".equals(otherAgent.getProperty("EXPERIENCE")) && this.isFollowingSomeone() == null && leader.getdisasterLocation() == null){
						setFollowTarget(leader.agent);
						leader.numberOfFollowers++;
						setState(true);
						//System.out.println("Sigo al valiente");
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	private Location destination = null;
	
	void fuga(){
		int aux = this.agent.getRoom().getRoomsOrderedByDistance().size()/2;
		int last = this.agent.getRoom().getRoomsOrderedByDistance().size()-1;
		
		if(destination == null){
			for(int i = 0; i < last+1 ; i++){
				if(this.agent.getRoom() == this.agent.getRoom().getRoomsOrderedByDistance().get(i)){
					if(i<=aux){
						destination = this.agent.getRoom().getRoomsOrderedByDistance().get(last).getRandomLoc();
					}
					else{
						destination = this.agent.getRoom().getRoomsOrderedByDistance().get(0).getRandomLoc();
					}
				}
			}
		}
		//Location destination = this.agent.getLocation().getFloor().getDoors().get(0).getLocation();

		ApproachCallback callback = new ApproachCallback() {
			@Override
			public void onTargetReached(LowLevelAgent agent) {
				// Target has been reached.
				stop = true;
			}

			@Override
			public void onSucess(LowLevelAgent agent) {
				// Everything ok. The agent has moved a little bit.
			}

			@Override
			public void onPathFinderError(PathFinderErrorReason reason) {
				// Error!
				Logger.getLogger(AgentHighLevelController.class.getName())
           			.log(Level.SEVERE,
           					"Error when approaching to {0} Reason: {1}",
           					new Object[] { destination, reason });
			}
		};
		/* 3 */ this.agent.approachTo(destination, callback);

	}
	
	private SimRoom Room;
	
	void escaparse(){
		if (destination == null){
			Room = this.agent.getRandomRoom();
			
			while (Room == this.agent.getRoom()){
				Room = this.agent.getRandomRoom();
			}
			
			destination = Room.getRandomLoc();
		}
		
		ApproachCallback callback = new ApproachCallback() {
			@Override
			public void onTargetReached(LowLevelAgent agent) {
				// Target has been reached.
				stop = true;
			}

			@Override
			public void onSucess(LowLevelAgent agent) {
				// Everything ok. The agent has moved a little bit.
			}

			@Override
			public void onPathFinderError(PathFinderErrorReason reason) {
				// Error!
				Logger.getLogger(AgentHighLevelController.class.getName())
           			.log(Level.SEVERE,
           					"Error when approaching to {0} Reason: {1}",
           					new Object[] { destination, reason });
			}
		};
		/* 3 */ this.agent.approachTo(destination, callback);
		
	}
	
	void UnexperiencedBehaviour(){
			if(isKnowPlace()){
				if(isMethodKnowledged()){
					if(isbyExpert() == 2){
						//Lo sigue		
						follow(isFollowingSomeone());
						sigueloael(search_range);
						
					}
					else if(isbyExpert() == 1){
						//Huye y alerta a demas
						setDisasterLocation(null);
						setState(false);
						fuga();
					}
				}
				else{
					//Huye y alerta a demas
					//System.out.println(this.agent.getLocation().getFloor().getDoors().get(1).getLocation());
					setDisasterLocation(null);
					setState(false);
					fuga();
				}
			}
			else {
				if(isMethodKnowledged()){
					//sigue a experto
					if(isbyExpert() == 2){
						//Lo sigue
						follow(isFollowingSomeone());
						sigueloael(search_range);
						
						
						
					}
					else if(isbyExpert() == 1){
						//Huye y alerta a demas
						setDisasterLocation(null);
						setState(false);
						escaparse();
					}

				}
				else{
					//Pánico y paralizado
					sigueloael(search_range);
					follow(isFollowingSomeone());
				}
			}
	}
	
	void ExperiencedBehaviour(){
			if(isMethodKnowledged()){
				setState(true);
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
					setState(false);
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
	
	void moveRandomly (){
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
				if(DisasterRange(search_range)){
					//System.out.println("Veo el desastre");
					setKnowDisaster(true);
					setMethodKnowledged(false);
				}
				
				else if(ByExpertRange(search_range) == 2 || ByExpertRange(search_range) == 1){
					//System.out.println("Me entero del desastre por otro");
					setKnowDisaster(true);
					setMethodKnowledged(true);
				}
			}

			@Override
			public void onPathFinderError(PathFinderErrorReason reason) {
				// Error!
				Logger.getLogger(AgentHighLevelController.class.getName())
            		.log(Level.SEVERE,
            				"Error when approaching to {0} Reason: {1}",
            				new Object[] { currentTarget, reason });
			}
		};
   		
		/* 3 */ this.agent.approachTo(this.currentTarget, callback);
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
				if(DisasterRange(search_range)){
					//System.out.println("LOO VEEOOO343434");
					setDisasterLocation(null);
				}
			}

			@Override
			public void onPathFinderError(PathFinderErrorReason reason) {
				// Error!
				Logger.getLogger(AgentHighLevelController.class.getName())
            		.log(Level.SEVERE,
            				"Error when approaching to {0} Reason: {1}",
            				new Object[] { currentTarget, reason });
			}
		};
   		
		/* 3 */ this.agent.approachTo(target, callback);
	}
	
	private Location randomTarget = null;
	
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
	        		followMeUnexperienced(search_range);
	        	}

	        	@Override
	        	public void onPathFinderError(PathFinderErrorReason reason) {
	            	// Error!
	        		Logger.getLogger(AgentHighLevelController.class.getName())
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
			setState(true);	
			
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
		            Logger.getLogger(AgentHighLevelController.class.getName()).log(
		              Level.SEVERE,
		              "An error occurred when approaching to {0}. Reason: {1}",
		              new Object[] { followTarget.getLocation(), reason });

		        }
		      });
		}		
	}
	
	public AgentHighLevelController(LowLevelAgent agent, Map<String, String> metadata, String resourcesFolder) {
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
	    
	    if (taggedStr == null || !"true".equals(metadata.get("STATE"))) {
	        this.setState(false);
	    } else {
	    	this.setState(true);
	    }
	    
	}

	@Override
	public void stop() {
		/*
		 * Clean resources, threads...etc
		 */
	}

	@Override
	public void step() {
		if(!stop){
			if(!this.isKnowDisaster()){
				moveRandomly();
			}
			else {
				if (this.isExperienced()) {
					ExperiencedBehaviour();
				} else {
					UnexperiencedBehaviour();
				}
			}
		}
	}
	
	private void prueba(){
		System.out.println(this.agent.getRoom().getRoomsOrderedByDistance());
	}
}