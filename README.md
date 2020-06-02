# FEH Simulator Project Outline

Here you will find plans of class organization and rudimentary outlines for how to implement game functionalities. There are plans to use Swing & WebSwing in the future in order to build this simulator into a full fledged app (perhaps even hosted on the web), but that will come once the code is working.

TODO NEXT: figure out how to actually identify the drives / in-combat effects a unit is under when CombatInteractionHandler simulates a round of combat.

## Core Classes

### MatchMaster
 
This provides the highest-level control of a game. The MatchMaster contains the main method, and takes in arguments when called in the command line that determine match parameters such as game type (PvP? PvE?), map type, team size, etc..
    
Upon starting a game, the MatchMaster takes information from the user(s) in order to initialize the Unit objects of each team. It then does various preparations including, but not limited to, initializing the Tile\[]\[] map, or setting up the Lists of UnitLocationPairs.
    
The MatchMaster tracks the turn counter and ends the match upon the turn limit being reached (if there is one), or a certain amount of unit loss has occurred depending on mode (GHBs end with 1 user unit loss, whereas Arena matches do not end until all units on a team are lost).
    
Once the MatchMaster starts a phase (there are 2 phases a turn), it passes all relevant state information of the map and units to TurnPhaseHandler, which is now responsible for taking in player input and executing the appropriate actions.

### TurnPhaseHandler

Technically speaking, TurnPhaseHandler handles "half" a FEH turn each time - it counts a player phase as one turn, and an enemy phase as another turn. A new TurnPhaseHandler is created for each phase and is useless once a phase terminates.

The TurnPhaseHandler is responsible for applying start-of-turn effects, expiring buffs, and other turn-related operations. It then calls a method that loops and repeatedly calls a takeAction() method in the Player class to allow player input until a player is out of actions, at which point it ends the turn and returns.
    
Every time a player input is taken in, the Player class processes the input into a command, and passes it to TurnPhaseHandler, which calls the appropriate InteractionHandler to process and carry it out. The InteractionHandler is responsible for matters such as expiring debuffs that only last through a unit's next action.

### InteractionHandler
  
There are 3 types of InteractionHandlers: MovementInteractionHandler, CombatInteractionHandler, and AssistInteractionHandler, whose names should be fairly intuitive. InteractionHandlers are always given commands that specify the type of interaction the initiator of the interaction, the recipient of the interaction (none for a movement command), and details of how to carry out the command (e.g. which tile to attack from). InteractionHandlers will also receive the Tile\[]\[] map and both players' lists of UnitLocationPairs.
    
InteractionHandlers perform calculations of combat results, read the skills of units in order to determine whether they are active and what effects they have if they are active, and also invoke those effects. Functions to check skill activation conditions and invoke skill effects are stored inside Skill objects as fields, which are called by InteractionHandler.
    
For more information on how Skills work with functional interfaces, see the Skill class section.
  
### Player
  
A Player is either a User or an AI. The Player class contains mostly functionality, such as methods that take in, process, and return user input as commands. The AI class extending Player will have <SHIT TONS OF DECISION MAKING CODE THAT'S SURE TO BE FUN>. At the core, however, a User object and an AI object will interact with TurnPhaseHandler through the same methods - only what goes on in the background to determine what commands are output by the Player object is different.
    

## Game Mechanics Classes

### UnitLocationPair
  
Stores a Unit object and it's location as an int\[2] array in the form of {column, row}.
  
### Tile
  
A Tile is the same as a tile on the FEH map and contains the same information: terrain information, structure type, and break information (how many hits to break, if breakable). A Tile contains NO unit-related information at all. That is tracked through UnitLocationPairs.
  
### Unit
  
A Unit is, at it's simplest, is just a container for information and has no real functionality. It is other methods that are accessing a unit's information, and doing things with that. A Unit object stores stat-related information in arrays where indices have predefined meaning (e.g. 0 = hp, 1 = atk, 2 = spd, etc.), skill information as an array of Skill objects, and state information.

State information is slightly more complicated. Buffs must be stored together with their remaining duration, whereas debuffs expire after any action, and so do not need to store duration information. Special charge can simply be stored as an int.

Units also need to know who their support partner is. Support info is stored in the form of {int[2] identifier, String rank} pairs. A unit's identifier has the format of {owner, slot} (for example, {1, 4} would indicate the 4th unit of player 1). For simplicity, this pair will just be an integer array of length 4: {player, slot, row, column}. Summoner support rank is taken into account of BEFORE the match starts, and the stat buffs are loaded into the unit's base stats.
  
### Skill

Functionality of skills is modularized by way of Skill objects having a Condition activationCondition field and an Effect activationEffect field.

Here, Condition and Effect are functional interfaces instantiated through lambdas:
  
```Java
// if you have an interface containing only one method
interface Function {
  public void doSomething();
}
// you will be able to instantiate this interface like so
Function printsInput = (int x) -> { System.out.println(x); };
printsInput.doSomething(5); // prints 5
```
  
In the case of our skills, Condition and Effect functions will take in tuples containing variable amounts of things as their parameters. If a skill activation condition involves checking a unit's stats only, for instance, the the activationCondition function would take in a tuple containing only the Unit object to check its stats. If it were a Solo skill, however, activationCondition's tuple would have a List<UnitLocationPair>. Effect functions work similarly.

Skills will need to contain some type of identification to let InteractionHandlers know what to give it's Condition and Effect functions.
  
Other details about skills are ezpz so those can be figured out later.

## Auxiliary Classes
  
### SkillFactory
  
### DistanceMapper
