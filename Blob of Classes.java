// ~~Java FEH~~
// It's FEH, but in Java!
// Finished product will support both UservUser and UservComputer
// Programming the computer AI is going to be hard >.<

// barebones outline of how a match will go:
// pre: MatchMaster takes input from players and sets up units as well as the map
// 1. MatchMaster handles the turn alternation and calling of PhaseHandler on Player objects
//    all state information such as Map and the UnitLocationPair list is passed to PhaseHandler
//    MatchMaster will apply start of turn buffs / effects, expire buffs at the end of a turn cycle, etc.
// 2. PhaseHandler takes input from the players, either moving units or calling InteractionHandler when needed
//    all state information is passed down to InteractionHandler as well if invoked
// 3. InteractionHandler calculates combat results, applies external-affecting skill effects, etc.
//    Depending on the type of Skill class involved, InteractionHandler will pass relevant info to the Skill to apply effects

// ~~MatchMaster~~
// this is the class that runs the match
// functions:
// - know what type of match it is (AR? Arena? etc.)
// - hold the map on which a the game takes place
//   the map contains all geographical information at any given time in a match in the form of tiles
// - hold a list containing the units / their locations of each player stored as {Unit unit, int[2] location} pairs
// - know what's going on with debuffs and drives
// - execute the turns actions of each player, invoking the AI class if the match is PlayervComputer
//   does this by delegating to PhaseHandler
public class MatchMaster {
  private List<UnitLocationPair> p1UnitInfo;
  private List<UnitLocationPair> p2UnitInfo;
  private Tile[][] map;
}

// ~~PhaseHandler~~
// not sure if this will be used yet; depends on how ridiculously crowded MatchMaster gets
public class PhaseHandler {
  
}

// just a simple helper class to implement unit-location pairs
// location is in the form of (row, column) coordinates
public class UnitLocationPair {
  public Unit unit;
  public int[2] location;
  
  public UnitLocationPair(Unit _unit, int[2] _location) {
    unit = _unit
    location = _location;
  }
  
  public void updateLocation(int row, int column) {
    location[0] = row;
    location[1] = column;
  }
}

// ~~Tile~~
// a Tile knows things about itself, and that's about it
// functions:
// - know if it's a mountain, lake, box, forest, etc.
// - know if it's a structure
// - know if it's breakable and how many hits are needed to break it
public class Tile {
  private String terrainType;
  private String structureType;
  private int hitsToBreak;
  
  public Tile(String _terrainType, String _structureType, int _hitsToBreak) {
    terrainType = _terrainType;
    structureType = _structureType;
    hitsToBreak = _hitsToBreak;
  }
  
  public String getTerrainType() {
    return terrainType;
  }
  public String getStructureType() {
    return structureType;
  }
  public int getHitsToBreak() {
    return hitsToBreak;
  }
  
  public void setTerrainType(String input) {
    terrainType = input;
  }
  public void setStructureType(String input) {
    structureType = input;
  }
  public void setHitsToBreak(int input) {
    hitsToBreak = input;
  }
  public void decrementHitsToBreak() {
    hitsToBreak = hitsToBreak - 1;
  }
}

// ~~Unit~~
// Unit class contains all information specific to a Unit (you could look at this information without Map context and make sense of it)
// functions:
// - know what a unit's stats are
// - know what a unit's skills are
// - know what special charge a Unit is at
// - know what buffs / debuffs / status effects are on a Unit
// - know who a unit's support partner is and what support rank they are
//   support info is stored in the form of {int[2] identifier, String rank} pairs
//   a unit's identifier has the format of {owner, slot} (for example, {1, 4} would indicate the 4th unit of player 1)
//   for simplicity, this pair will just be an integer array of length 4: {player, slot, row, column}
// - for details on how skill activation conditions and effects will be implemented, see README
public class Unit {
  private String unitName;
  private int[5] baseStats;
  private Skill[6] unitSkills;
}

// ~~Skill~~
// a skill in general
// details:
// - instantiable skills will have types and can implement ExternalAffecting if it has mapwide affects (e.g. buffs in column)
// - passive skills are classed based on activation condition
//   InteractionHandler will pass information into a Skill's isActive() based on the skill's type, given by instanceof
public class Skill {

}

// ~~DistanceMapper~~
// takes a unit's location as an origin and maps distances to every other point on the map
// (will undoubtedly be more functionality added here in the future, for example finding shortest possible paths from a to b)
// functions:
// - determine whether a player's movement input is valid when MatchMaster asks it to
// - help the AI make targetting / movement decisions
// - be able to distinguish between mapping literal distance (disregarding obstacles) and movement distance
//   literal distance will be used to calculate no. of allies in drive range
public class DistanceMapper {
  
}

// ~~InteractionHandler~~
// takes two units, one the initiator of an interaction and one the received of it, and handles the interaction 
public abstract class InteractionHandler {
  private Unit initiatingUnit;
  private Unit receivingUnit;
  private List<UnitLocationPair> initiatingTeamInfo;
  private List<UnitLocationPair> receivingTeamInfo;
  private Tile[][] map;
  
  public InteractionHandler() {
    
  }
  
  public void executeInteraction();
}

// ~~CombatInteractionHandler~~
// handles combats
public class CombatInteractionHandler extends InteractionHandler {
  
}

// ~~AssistInteractionHandler~~
// handles assists
public class AssistInteractionHandler extends InteractionHandler {
  
}

// ~~Player~~
// either a user or an AI
public abstract class Player {
  
}

// ~~User~~
// You, probably, or your friend if you're playing against them
public class User extends Player {
  
}

// ~~AI~~
// The computer, who you're playing against
// I guess we could include the option for ComputervComputer just for lols
public class AI extends Player {
  
}

// ~~SkillFactory~~
// will be able to take strings as input, then instantiate and return the corresponding Skill objects to be used in the code
// functions:
// - take String input and invoke corresponding method to return a particular Skill object
// - ONLY responsible for "making" skills - skill restrictions are handled by MatchMaster
// - split up into subclasses only because one massive SkillFactory would suck
public class SkillFactory {
  public SkillFactory() {
    
  }
}

// ~~WeaponSkillFactory~~
// makes weapon skills
public class WeaponSkillFactory extends SkillFactory {
  public WeaponSkillFactory() {
    super();
  }
}

// ~~AssistSkillFactory~~
// makes assist skills
public class AssistSkillFactory extends SkillFactory {
  public AssistSkillFactory() {
    super();
  }
}

// ~~SpecialSkillFactory~~
// makes special skills
public class SpecialSkillFactory extends SkillFactory {
  public SpecialSkillFactory() {
    super();
  }
}

// ~~PassiveSkillFactory~~
// makes passive skills (includes seals, but needs to know when a skill can't be a seal)
// will be huge, unfortunately
public class PassiveSkillFactory extends SkillFactory {
  public PassiveSkillFactory() {
    super();
  }
}
