package main;

import fileio.ActionsInput;
import fileio.GameInput;

import java.util.ArrayList;

abstract class Card {
    private int mana;
    private String description;
    private ArrayList<String> colors;
    private String name;

    public Card(int mana, String description, ArrayList<String> colors, String name) {
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

    public void setMana(int mana) {
        this.mana = mana;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setColors(ArrayList<String> colors) {
        this.colors = colors;
    }

    public void setName(String name) {
        this.name = name;
    }
}

abstract class Hero extends Card {

    public Hero(int mana, String description, ArrayList<String> colors, String name) {
        super(mana, description, colors, name);
    }
    private int health = 30;
    private boolean hasAttacked = false;
    abstract public void ability(ArrayList<Card> row);
    public int getHealth() {
        return health;
    }
    public void setHealth(int health) {
        this.health = health;
    }
    public boolean isHasAttacked() {
        return hasAttacked;
    }

    public void setHasAttacked(boolean hasAttacked) {
        this.hasAttacked = hasAttacked;
    }
}
class LordRoyce extends Hero {
    public LordRoyce(int mana, String description, ArrayList<String> colors, String name) {
        super(mana, description, colors, name);
    }

    public void ability(ArrayList<Card> row) {
        int max = 0;
        Card freeze = row.get(0);
        for (Card card : row) {
            if (((Minion)card).getAttackDamage() > max) {
                max = ((Minion)card).getAttackDamage();
                freeze = card;
            }
        }
        ((Minion)freeze).setFrozen(true);
    }
}
class EmpressThorina extends Hero {
    public EmpressThorina(int mana, String description, ArrayList<String> colors, String name) {
        super(mana, description, colors, name);
    }
    public void ability(ArrayList<Card> row) {
        int max = 0;
        Card destroy = row.get(0);
        for (Card card : row) {
            if (((Minion)card).getHealth() > max) {
                max = ((Minion)card).getHealth();
                destroy = card;
            }
        }
        row.remove(destroy);
    }
}
class KingMudface extends Hero {
    public KingMudface(int mana, String description, ArrayList<String> colors, String name) {
        super(mana, description, colors, name);
    }
    public void ability(ArrayList<Card> row) {
        for (Card card : row) {
            ((Minion)card).setHealth(((Minion)card).getHealth() + 1);
        }
    }
}
class GeneralKocioraw extends Hero {
    public GeneralKocioraw(int mana, String description, ArrayList<String> colors, String name) {
        super(mana, description, colors, name);
    }
    public void ability(ArrayList<Card> row) {
        for (Card card : row) {
            ((Minion)card).setAttackDamage(((Minion)card).getAttackDamage() + 1);
        }
    }
}
abstract class Minion extends Card{
    private int attackDamage;
    private int health;
    private boolean frozen = false;
    private boolean hasAttacked;
    private boolean tank;

    public int getAttackDamage() {
        return attackDamage;
    }

    public void setAttackDamage(int attackDamage) {
        this.attackDamage = attackDamage;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public boolean isFrozen() {
        return frozen;
    }

    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }

    public boolean isTank() {
        return tank;
    }

    public void setTank(boolean tank) {
        this.tank = tank;
    }

    public boolean hasAttacked() {
        return hasAttacked;
    }

    public void setHasAttacked(boolean hasAttacked) {
        this.hasAttacked = hasAttacked;
    }

    public Minion(int mana, String description, ArrayList<String> colors, String name,
                  int attackDamage, int health) {
        super(mana, description, colors, name);
        this.attackDamage = attackDamage;
        this.health = health;
    }
}

class StandardMinion extends Minion{
    public StandardMinion(int mana, String description, ArrayList<String> colors, String name,
                          int attackDamage, int health) {
        super(mana, description, colors, name, attackDamage, health);
    }
}
abstract class SpecialMinion extends Minion{
    public SpecialMinion(int mana, String description, ArrayList<String> colors, String name,
                         int attackDamage, int health) {
        super(mana, description, colors, name, attackDamage, health);
    }
    abstract void ability(GameInfo gameInfo, ActionsInput act);
}

class TheRipper extends SpecialMinion {
    public TheRipper(int mana, String description, ArrayList<String> colors, String name,
                     int attackDamage, int health) {
        super(mana, description, colors, name, attackDamage, health);
    }
    public void ability(GameInfo gameInfo, ActionsInput act) {
        Minion target = (Minion)gameInfo.getBoard().get(act.getCardAttacked().getX())
                .get(act.getCardAttacked().getY());
        if (target.getAttackDamage() > 2)
            target.setAttackDamage(target.getAttackDamage() - 2);
        else
            target.setAttackDamage(0);
    }
}

class Miraj extends SpecialMinion {
    public Miraj(int mana, String description, ArrayList<String> colors, String name,
                 int attackDamage, int health) {
        super(mana, description, colors, name, attackDamage, health);
    }
    public void ability(GameInfo gameInfo, ActionsInput act) {
        Minion target = (Minion)gameInfo.getBoard().get(act.getCardAttacked().getX())
                .get(act.getCardAttacked().getY());
        Minion receiver = (Minion)gameInfo.getBoard().get(act.getCardAttacker().getX())
                .get(act.getCardAttacker().getY());
        int newHealth = target.getHealth();
        target.setHealth(receiver.getHealth());
        receiver.setHealth(newHealth);
    }
}

class TheCursedOne extends SpecialMinion {
    public TheCursedOne(int mana, String description, ArrayList<String> colors, String name,
                        int attackDamage, int health) {
        super(mana, description, colors, name, attackDamage, health);
    }
    public void ability(GameInfo gameInfo, ActionsInput act) {
        Minion target = (Minion)gameInfo.getBoard().get(act.getCardAttacked().getX())
                .get(act.getCardAttacked().getY());
        int newValue = target.getHealth();
        target.setHealth(target.getAttackDamage());
        target.setAttackDamage(newValue);
        if (target.getHealth() == 0)
            gameInfo.getBoard().get(act.getCardAttacked().getX()).remove(act.getCardAttacked()
                    .getY());
    }
}

class Disciple extends SpecialMinion {
    public Disciple(int mana, String description, ArrayList<String> colors, String name,
                    int attackDamage, int health) {
        super(mana, description, colors, name, attackDamage, health);
    }
    public void ability(GameInfo gameInfo, ActionsInput act) {
        Minion target = (Minion)gameInfo.getBoard().get(act.getCardAttacked().getX()).
                get(act.getCardAttacked().getY());
        target.setHealth(target.getHealth() + 2);
    }
}

abstract class Environment extends Card{
    public Environment(int mana, String description, ArrayList<String> colors, String name) {
        super(mana, description, colors, name);
    }
    public abstract void effect(ArrayList<ArrayList<Card>> board, int affectedRow);
}

class Firestorm extends Environment {
    public Firestorm(int mana, String description, ArrayList<String> colors, String name) {
        super(mana, description, colors, name);
    }
    @Override
    public void effect(ArrayList<ArrayList<Card>> board, int affectedRow) {
        ArrayList<Card> cardRow = board.get(affectedRow);
        for (Card card : cardRow)
            ((Minion)card).setHealth(((Minion)card).getHealth() - 1);
        cardRow.removeIf((Card card) -> ((Minion)card).getHealth() == 0);
    }
}
class Winterfell extends Environment {
    public Winterfell(int mana, String description, ArrayList<String> colors, String name) {
        super(mana, description, colors, name);
    }
    @Override
    public void effect(ArrayList<ArrayList<Card>> board, int affectedRow) {
        ArrayList<Card> cardRow = board.get(affectedRow);
        for (Card card : cardRow)
            ((Minion)card).setFrozen(true);
    }
}
class HeartHound extends Environment {
    public HeartHound(int mana, String description, ArrayList<String> colors, String name) {
        super(mana, description, colors, name);
    }
    @Override
    public void effect(ArrayList<ArrayList<Card>> board, int affectedRow) {
        ArrayList<Card> cardRow = board.get(affectedRow);
        int maxHealth = 0;
        Card stolen = cardRow.get(0);
        for (Card card : cardRow)
            if (((Minion)card).getHealth() > maxHealth) {
                maxHealth = ((Minion) card).getHealth();
                stolen = card;
            }
        board.get(board.size() - affectedRow - 1).add(stolen);
        cardRow.remove(stolen);
    }
}


