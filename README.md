*Misicu Laura-Teodora 323CA*
# GwentStone


GwentStone is a card game that combines gameplay elements from **Hearthstone** and **Gwent**.

## 📌 Features and Implementation

The project is structured around several **abstract classes** that define the core mechanics of the game:

-   `Card`
-   `Minion`
-   `SpecialMinion`
-   `Hero`
-   `Environment`

These classes are extended by specialized subclasses representing different cards. Method overriding is used to implement abilities for **heroes**, **minions**, and **environment cards**.

### 🏆 Hero Abilities

Each hero class overrides the `ability` method to define their unique effect:

-   **Lord Royce** – Freezes a minion ❄️
-   **Empress Thorina** – Destroys a minion 💥
-   **King Mudface** – Increases the health of certain minions ❤️
-   **General Kocioraw** – Increases the attack power of certain minions ⚔️

### 🃏 Deck Management

The **Deck** class handles:

-   Storing the deck’s cards
-   Keeping track of the number of cards
-   Loading deck configurations from the provided input file using the `setupDeck` method

### 🎮 Game State Management

The **GameInfo** class maintains all essential game details, such as:

-   Players' heroes
-   Cards in hand
-   Current round number

The methods `setHero` and `setupGame` ensure a smooth game initialization.

### ⚔️ Gameplay Execution

The **GamePlay** class manages all game actions through its `executeActions` method, processing commands like:

-   Placing cards (`placeCard`)
-   Using environment cards (`useEnvironment`)
-   Activating hero abilities (`heroAbility`)

### 🏁 Game Control and Results

The **Solver** class is responsible for:

-   Managing multiple games sequentially
-   Tracking the number of games played and won
-   Displaying the results of each action

## 🔧 Areas for Improvement

-   **Optimize Board Structure** – Changing the board representation to `ArrayList<ArrayList<Minion>>` would reduce frequent casting to `Minion`.
-   **Improve Code Readability** – Implementing a dedicated class for error handling would result in a cleaner and more structured codebase.
