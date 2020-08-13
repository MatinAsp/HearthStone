package Data;

import Interfaces.*;
import Logic.ActionRequest;
import Logic.Actions;
import Logic.ActionsType.*;
import Logic.Competitor;
import Logic.Game;
import Models.*;
import Models.Cards.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.Character;
import java.lang.Cloneable;

public class GsonCenter {
    static private GsonCenter gsonCenter = null;
    private GsonBuilder gsonBuilder;
    private GsonCenter(){
        gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Game.class, new AbstractAdapter<Game>());
        gsonBuilder.registerTypeAdapter(ActionHandler.class, new AbstractAdapter<ActionHandler>());
        gsonBuilder.registerTypeAdapter(CardAction.class, new AbstractAdapter<CardAction>());
        gsonBuilder.registerTypeAdapter(Cloneable.class, new AbstractAdapter<Cloneable>());
        gsonBuilder.registerTypeAdapter(PerformActionHandler.class, new AbstractAdapter<PerformActionHandler>());
        gsonBuilder.registerTypeAdapter(PlayActionHandler.class, new AbstractAdapter<PlayActionHandler>());
        gsonBuilder.registerTypeAdapter(QuestActionHandler.class, new AbstractAdapter<QuestActionHandler>());
        gsonBuilder.registerTypeAdapter(Action.class, new AbstractAdapter<Action>());
        gsonBuilder.registerTypeAdapter(BotMove.class, new AbstractAdapter<BotMove>());
        gsonBuilder.registerTypeAdapter(DrawCard.class, new AbstractAdapter<DrawCard>());
        gsonBuilder.registerTypeAdapter(EndTurn.class, new AbstractAdapter<EndTurn>());
        gsonBuilder.registerTypeAdapter(PerformAction.class, new AbstractAdapter<PerformAction>());
        gsonBuilder.registerTypeAdapter(SummonMinion.class, new AbstractAdapter<SummonMinion>());
        gsonBuilder.registerTypeAdapter(ActionRequest.class, new AbstractAdapter<ActionRequest>());
        gsonBuilder.registerTypeAdapter(Actions.class, new AbstractAdapter<Actions>());
        gsonBuilder.registerTypeAdapter(Competitor.class, new AbstractAdapter<Competitor>());
        gsonBuilder.registerTypeAdapter(Character.class, new AbstractAdapter<Character>());
        gsonBuilder.registerTypeAdapter(Card.class, new AbstractAdapter<Card>());
        gsonBuilder.registerTypeAdapter(HeroPower.class, new AbstractAdapter<HeroPower>());
        gsonBuilder.registerTypeAdapter(Hero.class, new AbstractAdapter<Hero>());
        gsonBuilder.registerTypeAdapter(Minion.class, new AbstractAdapter<Minion>());
        gsonBuilder.registerTypeAdapter(Spell.class, new AbstractAdapter<Spell>());
        gsonBuilder.registerTypeAdapter(Quest.class, new AbstractAdapter<Quest>());
        gsonBuilder.registerTypeAdapter(Weapon.class, new AbstractAdapter<Weapon>());
        gsonBuilder.registerTypeAdapter(Deck.class, new AbstractAdapter<Deck>());
        gsonBuilder.registerTypeAdapter(InfoPack.class, new AbstractAdapter<InfoPack>());
        gsonBuilder.registerTypeAdapter(Passive.class, new AbstractAdapter<Passive>());
        gsonBuilder.registerTypeAdapter(Player.class, new AbstractAdapter<Player>());

        gsonBuilder.setPrettyPrinting();
    }

    public static GsonCenter getInstance(){
        if(gsonCenter == null){
            gsonCenter = new GsonCenter();
        }
        return gsonCenter;
    }

    public Gson getNewGson(){
        return gsonBuilder.create();
    }
}
