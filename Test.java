// proof of concept for the Skill ActivationCondition interface
// ActivationCondition instances will be stored in the fields of Skill objects

// output:

// [root@waterloo.student.cs Test]# java Test
// Does 49 HP pass a 50 HP check? false
// Is currentHPCondition a COMBAT_STAT check? false
// Is currentHPCondition a CURRENT_HP check? true

// Does "infantry" pass the check for "cavalry"? false
// Does "cavalry" pass the check for "cavalry"? true
// Is movementTypeCondition a MOVEMENT_TYPE check? true

public class Test {
  public static void main(String[] args) {
    CurrentHPActivationCondition currentHPCondition = (Integer HP) -> HP > 50;
    MovementTypeActivationCondition movementTypeCondition = (String mvt) -> mvt.equals("cavalry");

    System.out.println("Does 49 HP pass a 50 HP check? " +  currentHPCondition.checkCondition(49));

    System.out.println();

    System.out.println("Does \"infantry\" pass the check for \"cavalry\"? " + movementTypeCondition.checkCondition("infantry"));
    System.out.println("Does \"cavalry\" pass the check for \"cavalry\"? " + movementTypeCondition.checkCondition("cavalry"));
    System.out.println("Is movementTypeCondition a MOVEMENT_TYPE check? " + (movementTypeCondition.type == ConditionType.MOVEMENT_TYPE));
  }
}

enum ConditionType {
  CURRENT_HP,
  COMBAT_STAT,
  VISIBLE_STAT,
  MOVEMENT_TYPE
}

interface ActivationCondition<T> {
  public boolean checkCondition(T input);
}

interface CurrentHPActivationCondition extends ActivationCondition<Integer> {
  ConditionType type = ConditionType.CURRENT_HP;

  @Override
  public boolean checkCondition(Integer x);
}

interface MovementTypeActivationCondition extends ActivationCondition<String> {
  ConditionType type = ConditionType.MOVEMENT_TYPE;

  @Override
  public boolean checkCondition(String s);
}
