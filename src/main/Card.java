package main;

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

class Hero extends Card {

    public Hero(int mana, String description, ArrayList<String> colors, String name) {
        super(mana, description, colors, name);
    }
    private int health = 30;
    private boolean alive = true;
    public void ability() {

    }

    public int getHealth() {
        return health;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
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

    public Minion(int mana, String description, ArrayList<String> colors, String name, int attackDamage, int health) {
        super(mana, description, colors, name);
        this.attackDamage = attackDamage;
        this.health = health;
    }
}

class StandardMinion extends Minion{
    public StandardMinion(int mana, String description, ArrayList<String> colors, String name, int attackDamage, int health) {
        super(mana, description, colors, name, attackDamage, health);
    }
}

class TheRipper extends Minion {
    public TheRipper(int mana, String description, ArrayList<String> colors, String name, int attackDamage, int health) {
        super(mana, description, colors, name, attackDamage, health);
    }
    public void ability() {

    }
}

class Miraj extends Minion {
    public Miraj(int mana, String description, ArrayList<String> colors, String name, int attackDamage, int health) {
        super(mana, description, colors, name, attackDamage, health);
    }
    public void ability() {

    }
}

class TheCursedOne extends Minion {
    public TheCursedOne(int mana, String description, ArrayList<String> colors, String name, int attackDamage, int health) {
        super(mana, description, colors, name, attackDamage, health);
    }
    public void ability() {

    }
}

class Disciple extends  Minion {
    public Disciple(int mana, String description, ArrayList<String> colors, String name, int attackDamage, int health) {
        super(mana, description, colors, name, attackDamage, health);
    }
    public void ability() {

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


