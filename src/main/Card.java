package main;

import fileio.ActionsInput;

import java.util.ArrayList;

abstract class Card {
    private int mana;
    private String description;
    private ArrayList<String> colors;
    private String name;

    Card(final int mana, final String description, final ArrayList<String> colors,
         final String name) {
        this.mana = mana;
        this.description = description;
        this.colors = colors;
        this.name = name;
    }

    public int getMana() {
        return mana;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<String> getColors() {
        return colors;
    }

    public String getName() {
        return name;
    }

    public void setMana(final int mana) {
        this.mana = mana;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public void setColors(final ArrayList<String> colors) {
        this.colors = colors;
    }

    public void setName(final String name) {
        this.name = name;
    }
}

abstract class Hero extends Card {

    Hero(final int mana, final String description, final ArrayList<String> colors,
         final String name) {
        super(mana, description, colors, name);
    }
    private int health = 30;
    private boolean hasAttacked = false;
    public abstract void ability(ArrayList<Card> row);
    public int getHealth() {
        return health;
    }
    public void setHealth(final int health) {
        this.health = health;
    }
    public boolean isHasAttacked() {
        return hasAttacked;
    }

    public void setHasAttacked(final boolean hasAttacked) {
        this.hasAttacked = hasAttacked;
    }
}
class LordRoyce extends Hero {
    LordRoyce(final int mana, final String description, final ArrayList<String> colors,
                     final String name) {
        super(mana, description, colors, name);
    }
    // freezes the enemy minion with the most attack damage
    public void ability(final ArrayList<Card> row) {
        int max = 0;
        Card freeze = row.get(0);
        for (Card card : row) {
            if (((Minion) card).getAttackDamage() > max) {
                max = ((Minion) card).getAttackDamage();
                freeze = card;
            }
        }
        ((Minion) freeze).setFrozen(true);
    }
}
class EmpressThorina extends Hero {
    EmpressThorina(final int mana, final String description,
                          final ArrayList<String> colors, final String name) {
        super(mana, description, colors, name);
    }
    // destroys the enemy minion with the most health
    public void ability(final ArrayList<Card> row) {
        int max = 0;
        Card destroy = row.get(0);
        for (Card card : row) {
            if (((Minion) card).getHealth() > max) {
                max = ((Minion) card).getHealth();
                destroy = card;
            }
        }
        row.remove(destroy);
    }
}
class KingMudface extends Hero {
    KingMudface(final int mana, final String description, final ArrayList<String> colors,
                       final String name) {
        super(mana, description, colors, name);
    }
    // gives 1 health to each allied minion on the row
    public void ability(final ArrayList<Card> row) {
        for (Card card : row) {
            ((Minion) card).setHealth(((Minion) card).getHealth() + 1);
        }
    }
}
class GeneralKocioraw extends Hero {
    GeneralKocioraw(final int mana, final String description,
                           final ArrayList<String> colors, final String name) {
        super(mana, description, colors, name);
    }
    // gives 1 attack damage to each allied minion on the row
    public void ability(final ArrayList<Card> row) {
        for (Card card : row) {
            ((Minion) card).setAttackDamage(((Minion) card).getAttackDamage() + 1);
        }
    }
}
abstract class Minion extends Card {
    private int attackDamage;
    private int health;
    private boolean frozen = false;
    private boolean hasAttacked;
    private boolean tank;

    public int getAttackDamage() {
        return attackDamage;
    }

    public void setAttackDamage(final int attackDamage) {
        this.attackDamage = attackDamage;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(final int health) {
        this.health = health;
    }

    public boolean isFrozen() {
        return frozen;
    }

    public void setFrozen(final boolean frozen) {
        this.frozen = frozen;
    }

    public boolean isTank() {
        return tank;
    }

    public void setTank(final boolean tank) {
        this.tank = tank;
    }

    public boolean hasAttacked() {
        return hasAttacked;
    }

    public void setHasAttacked(final boolean hasAttacked) {
        this.hasAttacked = hasAttacked;
    }

    Minion(final int mana, final String description, final ArrayList<String> colors,
           final String name, final int attackDamage, final int health) {
        super(mana, description, colors, name);
        this.attackDamage = attackDamage;
        this.health = health;
    }
}
// Minions that don't have abilities
class StandardMinion extends Minion {
    StandardMinion(final int mana, final String description, final ArrayList<String> colors,
                   final String name, final int attackDamage, final int health) {
        super(mana, description, colors, name, attackDamage, health);
    }
}
// Minions that have special abilities
abstract class SpecialMinion extends Minion {
    SpecialMinion(final int mana, final String description, final ArrayList<String> colors,
                  final String name, final int attackDamage, final int health) {
        super(mana, description, colors, name, attackDamage, health);
    }
    abstract void ability(GameInfo gameInfo, ActionsInput act);
}

class TheRipper extends SpecialMinion {
    TheRipper(final int mana, final String description, final ArrayList<String> colors,
              final String name, final int attackDamage, final int health) {
        super(mana, description, colors, name, attackDamage, health);
    }
    // reduces the target's attack damage by 2
    public void ability(final GameInfo gameInfo, final ActionsInput act) {
        Minion target = (Minion) gameInfo.getBoard().get(act.getCardAttacked().getX())
                .get(act.getCardAttacked().getY());
        if (target.getAttackDamage() > 2) {
            target.setAttackDamage(target.getAttackDamage() - 2);
        } else {
            target.setAttackDamage(0);
        }
    }
}

class Miraj extends SpecialMinion {
    Miraj(final int mana, final String description, final ArrayList<String> colors,
          final String name, final int attackDamage, final int health) {
        super(mana, description, colors, name, attackDamage, health);
    }
    // swaps his health and the health of an enemy minion
    public void ability(final GameInfo gameInfo, final ActionsInput act) {
        Minion target = (Minion) gameInfo.getBoard().get(act.getCardAttacked().getX())
                .get(act.getCardAttacked().getY());
        Minion receiver = (Minion) gameInfo.getBoard().get(act.getCardAttacker().getX())
                .get(act.getCardAttacker().getY());
        int newHealth = target.getHealth();
        target.setHealth(receiver.getHealth());
        receiver.setHealth(newHealth);
    }
}

class TheCursedOne extends SpecialMinion {
    TheCursedOne(final int mana, final String description, final ArrayList<String> colors,
                 final String name, final int attackDamage, final int health) {
        super(mana, description, colors, name, attackDamage, health);
    }
    // swaps the attack damage and health of an enemy minion
    public void ability(final GameInfo gameInfo, final ActionsInput act) {
        Minion target = (Minion) gameInfo.getBoard().get(act.getCardAttacked().getX())
                .get(act.getCardAttacked().getY());
        int newValue = target.getHealth();
        target.setHealth(target.getAttackDamage());
        target.setAttackDamage(newValue);
        // if the target's health is now 0 it will die and be removed from the game
        if (target.getHealth() == 0) {
            gameInfo.getBoard().get(act.getCardAttacked().getX()).remove(act.getCardAttacked()
                    .getY());
        }
    }
}

class Disciple extends SpecialMinion {
    Disciple(final int mana, final String description, final ArrayList<String> colors,
             final String name, final int attackDamage, final int health) {
        super(mana, description, colors, name, attackDamage, health);
    }
    // gives +2 health to an allied minion
    public void ability(final GameInfo gameInfo, final ActionsInput act) {
        Minion target = (Minion) gameInfo.getBoard().get(act.getCardAttacked().getX()).
                get(act.getCardAttacked().getY());
        target.setHealth(target.getHealth() + 2);
    }
}

abstract class Environment extends Card {
    Environment(final int mana, final String description, final ArrayList<String> colors,
                final String name) {
        super(mana, description, colors, name);
    }
    public abstract void effect(ArrayList<ArrayList<Card>> board, int affectedRow);
}

class Firestorm extends Environment {
    Firestorm(final int mana, final String description, final ArrayList<String> colors,
              final String name) {
        super(mana, description, colors, name);
    }
    @Override
    public void effect(final ArrayList<ArrayList<Card>> board, final int affectedRow) {
        // deals 1 damage to all minions on row, removing units with 0 health
        ArrayList<Card> cardRow = board.get(affectedRow);
        for (Card card : cardRow) {
            ((Minion) card).setHealth(((Minion) card).getHealth() - 1);
        }
        cardRow.removeIf((Card card) -> ((Minion) card).getHealth() == 0);
    }
}
class Winterfell extends Environment {
    Winterfell(final int mana, final String description, final ArrayList<String> colors,
               final String name) {
        super(mana, description, colors, name);
    }
    @Override
    public void effect(final ArrayList<ArrayList<Card>> board, final int affectedRow) {
        // freezes all minions on the row
        ArrayList<Card> cardRow = board.get(affectedRow);
        for (Card card : cardRow) {
            ((Minion) card).setFrozen(true);
        }
    }
}
class HeartHound extends Environment {
    HeartHound(final int mana, final String description, final ArrayList<String> colors,
               final String name) {
        super(mana, description, colors, name);
    }
    @Override
    public void effect(final ArrayList<ArrayList<Card>> board, final int affectedRow) {
        // steals the enemy's minion that has the most health from the selected row
        ArrayList<Card> cardRow = board.get(affectedRow);
        int maxHealth = 0;
        Card stolen = cardRow.get(0);
        for (Card card : cardRow) {
            if (((Minion) card).getHealth() > maxHealth) {
                maxHealth = ((Minion) card).getHealth();
                stolen = card;
            }
        }
        board.get(board.size() - affectedRow - 1).add(stolen);
        cardRow.remove(stolen);
    }
}


