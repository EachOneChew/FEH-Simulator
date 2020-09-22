# FEH Simulator Project Outline

Here you will find plans of class organization and rudimentary outlines for how to implement game functionalities. There are plans to use Swing & WebSwing in the future in order to build this simulator into a full fledged app (perhaps even hosted on the web), but that will come once the code is working.

*Tentative idea for implementing drive effects: Have Skill objects tag themselves as having external effects. CombatInteractionHandler can then loop through the units with skills marked as having external effects, and apply those effects if their conditions are met. This SHOULD work as skills can independently carry out their effects see Skill section).*

**The classes relating to pathfinding and target prioritization have already been completed and can be found [here](https://github.com/EachOneChew/Dijkstra-s-Al gorithm-FEH).**

## Core Classes

### MatchMaster
 
This provides the highest-level control of a game. The MatchMaster contains the main method, and takes in arguments when called in the command line that determine match parameters such as game type (PvP? PvE?), map type, team size, etc..
    
Upon starting a game, the MatchMaster takes information from the user(s) in order to initialize the Unit objects of each team. It then does various preparations including, but not limited to, initializing the Tile\[]\[] map, or setting up the Lists of UnitLocationPairs.
    
The MatchMaster tracks the turn counter and ends the match upon the turn limit being reached (if there is one), or a certain amount of unit loss has occurred depending on mode (GHBs end with 1 user unit loss, whereas Arena matches do not end until all units on a team are lost).
    
Once the MatchMaster starts a phase (there are 2 phases a turn), it passes all relevant state information of the map and units to Phase, which is now responsible for taking in player input and executing the appropriate actions.

### PhaseHandler

Technically speaking, PhaseHandler handles "half" a FEH turn each time - it counts a player phase as one turn, and an enemy phase as another turn. A new PhaseHandler is created for each phase and is useless once a phase terminates.

The PhaseHandler is responsible for applying start-of-turn effects, expiring buffs, and other turn-related operations. It then calls a method that loops and repeatedly calls a takeAction() method in the Player class to allow player input until a player is out of actions, at which point it ends the turn and returns.
    
Every time a player input is taken in, the Player class processes the input into a command, and passes it to PhaseHandler, which calls the appropriate InteractionHandler to process and carry it out. The InteractionHandler is responsible for matters such as expiring debuffs that only last through a unit's next action.

### InteractionHandler
  
There are 3 types of InteractionHandlers: MovementInteractionHandler, CombatInteractionHandler, and AssistInteractionHandler, whose names should be fairly intuitive. InteractionHandlers are always given commands that specify the type of interaction the initiator of the interaction, the recipient of the interaction (none for a movement command), and details of how to carry out the command (e.g. which tile to attack from). InteractionHandlers will also receive the Tile\[]\[] map and both players' lists of UnitLocationPairs.
    
InteractionHandlers perform calculations of combat results, read the skills of units in order to determine whether they are active and what effects they have if they are active, and also invoke those effects. Functions to check skill activation conditions and invoke skill effects are stored inside Skill objects as fields, which are called by InteractionHandler.
    
For more information on how Skills work with functional interfaces, see the Skill class section.
  
### Player
  
A Player is either a User or an AI. The Player class contains mostly functionality, such as methods that take in, process, and return user input as commands. The AI class extending Player will have <SHIT TONS OF DECISION MAKING CODE THAT'S SURE TO BE FUN>. At the core, however, a User object and an AI object will interact with PhaseHandler through the same methods - only what goes on in the background to determine what commands are output by the Player object is different.
    

## Game Mechanics Classes

### UnitLocationPair
  
Stores a Unit object and it's location as an int\[2] array in the form of {column, row}.
  
### Tile
  
A Tile is the same as a tile on the FEH map and contains the same information: terrain information, structure type, and break information (how many hits to break, if breakable). A Tile contains NO unit-related information at all. That is tracked through UnitLocationPairs.
  
### Unit
  
A Unit is, at it's simplest, is just a container for information and has no real functionality. It is other methods that are accessing a unit's information, and doing things with that. A Unit object stores stat-related information in arrays where indices have predefined meaning (e.g. 0 = hp, 1 = atk, 2 = spd, etc.), skill information as an array of Skill objects, and state information.

On second thought: a unit will store its atk / spd / def / res in an array and its current HP / max HP in another. This is because they are often used for different checks. 

State information is slightly more complicated. Visible buffs must be stored together with their remaining duration (in-combat buffs are calculated for each combat by CombatInteractionHandler), whereas debuffs expire after any action, and so do not need to store duration information. Special charge can simply be stored as an int.

Units also need to know who their support partner is. Support info is stored in the form of {int[2] identifier, String rank} pairs. A unit's identifier has the format of {owner, slot} (for example, {1, 4} would indicate the 4th unit of player 1). For simplicity, this pair will just be an integer array of length 4: {player, slot, row, column}. Summoner support rank is taken into account of BEFORE the match starts, and the stat buffs are loaded into the unit's base stats.
  
### Skill

Functionality of skills is modularized by way of Skill objects having multiple instances of SkillCondition and SkillEffect.

Here, SkillCondition and SkillEffect are functional interfaces instantiated through lambdas:
  
```Java
// if you have an interface containing only one method
interface Function {
  public void doSomething();
}
// you will be able to instantiate this interface like so
Function printsInput = (int x) -> { System.out.println(x); };
printsInput.doSomething(5); // prints 5
```
  
For our skills, each skill function will extend either SkillCondition<T> or SkillEffect<T>. They will always take in one type of parameter and one type only (such as a unit's current hp, a unit's current visible buffs, or the board in order to calculate whether there's an ally within 2 tiles). Skill functions will identify their own type by a enum type field, and provide that type with a default method getType().

Example of a function extending SkillCondition<T>:

```Java
interface SpecialChargeSkillCondition extends SkillCondition<Integer> { // ... }
```
 
Note that, as various different skill conditions functions will be stored in the same SkillCondition or SkillEffect List, it is necessary for functions to provide their type through a default function that will be associated with the **instance** of each function. Otherwise, if the types were simply stored as and retrieved as fields, the type field would be associated with the interface instead of the instance. As we are storing different types of functions inside the same SkillCondition or SkillEffect list, information associated with and only with the interface would be lost as each instance is converted into their super interface when being stored in the List.

Tl;Dr have each function instance provide their type through a function linked to their specific instance so they don't forget what type they are when being converted into their super interface.
  
Skills will store their SkillEffect and SkillCondition instances inside a ConditionEffectAssociation List. ConditionEffectAssociation is a class that stores a List of SkillConditions and a List of SkillEffects. The List of SkillConditions in every ConditionEffectAssociation are the specific conditions associated with the SkillEffects stored inside the List of SkillEffects.

IMPORTANT: I can't just assume the logical operators between each SkillCondition in a SkillCondition List. Need some way to deal with nested boolean expressions such as ((A AND B) OR (C)).

More often than not, the SkillEffects list in a ConditionEffectAssociation will only be one element, but I think this is a good way to ensure any variation in the number of conditions or number of effects of a skill, and the dependency of different effects on different conditions can be implemented.

## Auxiliary Classes
  
### SkillFactory
  
### DistanceMapper
