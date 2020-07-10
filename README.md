# Hearthstone
java version:
 
    14

extra lib:

    javafx
    log4j
    gson
    
sources:

    stackOverflow
    oracle
    blizzard sites
    and ...


explain structure:

    using factory and singleton design patterns.
    using configs and json files to load and save data.
    using gradle.
    using hash for saving passwords.
    saving logs with LogCenter Class.
    design graphics with fxml and css files and control them with controller classes.
    saving all data, even game settings like volume.
    tried to be based on clean code and solid principles.
    can simply set alert box and confirmation box just by calling setAlert and setConfirmation methods. 
    having music, sound effects and animations.
    using exceptions for alerting and logic.
    being full screen.
    design some pics by my own.
    design and render all cards, heroes, decks and ... programmatically in GraphicsRender Class.
    for playing, graphics part send some infoPacks with ActionRequest to Game and Game performs actions with Action class
    and Action class find and invoke methods for the infoPacks and if needs selection or it was an invalid move, Action class
    throws proper exception and graphics part get the problem.
    ActionRequest records the actions and graphics part reads the records and show the actions with animations or ... .
    
negative point:

    graphics part has the game class for getting information. It would be better if graphics part gets the game state with
    an interface. but we handle logic with an interface (ActionRequest). 
    
before compiling, please install the game's font form the following address:

    src/main/resources/Font
    
git link for full version (branch: faze-3):

    https://github.com/MatinAsp/Hearthstone.git