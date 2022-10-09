# Hearthstone
java version:
 
    14

extra lib:

    javafx
    log4j
    gson
    jackson
    hibernate
    postgresql
    
sources:

    stackOverflow
    oracle
    blizzard sites
    and ...
    ali tavasoly for jackson
    ali tavasoly and https://stackoverflow.com/questions/11016092/how-to-load-classes-at-runtime-from-a-folder-or-jar 
    for JarLoader class
    amirmohammad sadat shoukohi for database
    
database source code:

    http://GitHub.com/samssh/hamAfzayi

explain structure:

    using factory and singleton design patterns.
    using configs and json files to load and save data.
    using gradle.
    using hash for saving passwords.
    saving logs.
    design graphics with fxml and css files and control them with controller classes.
    saving all data, even game settings like volume.
    tried to be based on clean code and solid principles.
    having music, sound effects and animations.
    using exceptions for alerting and logic.
    being full screen.
    design some pics by my own.
    design and render all cards, heroes, decks and ... programmatically in GraphicsRender Class.
    for playing, client sends some infoPacks to server and server performs actions with Game class
    and Actions class find and invoke methods for the infoPacks and if needs selection or it was an invalid move, Action class
    throws proper exception and client gets the problem.
    ActionRequest records the actions and graphics part reads the records and show the actions with animations or ... .
    send and get data between server and client with json.
    clients have limit for getting data and change it in server.
    do all logic part in server.
    check the client connection.
    show ranking and status and have cup parameter.
    models and logic part are separated.
    mvc and requset-response pattern.
    have reflection part.
    using database.
    
negative point:

     using dupricated method in JacksonMapper class.
     dont have chat and game show for other people.
     have a weak algorithm for matching players.
         
before compiling, please install the game's font form the following address:

    src/main/resources/Font
    
to play with robot checkout to phase-3 branch.