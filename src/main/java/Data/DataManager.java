package Data;

import Models.*;
import Models.Cards.*;
import Models.Character;
import com.google.gson.Gson;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

import java.io.*;
import java.util.*;

public class DataManager {
    static private DataManager dataManager = null;
    private SessionFactory sessionFactory;
    private HashMap<Class, ArrayList> dataMap;
//    private String playersPath;
    private String generalPath;
    private Gson gson;
    private Object lock = new Object();

    private DataManager(){
        GameConstants gameConstants = GameConstants.getInstance();
        sessionFactory = buildSessionFactory();
      //  playersPath = gameConstants.getString("playerPath");
        generalPath = gameConstants.getString("generalPath");
        gson = new Gson();
        dataMap = new HashMap<>();
        initialize();
    }

    private void initialize() {
        try {
            GameConstants gameConstants = GameConstants.getInstance();
            Scanner scanner = new Scanner(new File(gameConstants.getString("classToLoadPath")));
            while (scanner.hasNext()){
                String className = scanner.next();
                dataMap.put(
                    Class.forName(className),
                    //loadData(Class.forName(className),gameConstants.getString(Class.forName(className).getSimpleName().toLowerCase()+"Path"))
                    loadData(Class.forName(className))
                );
            }
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    private SessionFactory buildSessionFactory() {
        final ServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
        SessionFactory sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        return sessionFactory;
    }

    private synchronized <T> ArrayList<T> loadData(Class<T> tClass) {
       // System.out.println(address);
//        ArrayList<T> arr = new ArrayList<>();
//        File[] dir = (new File(address)).listFiles();
//        for(File file: dir){
//            if(file.isDirectory()){
//                try {
//                    arr.addAll((ArrayList<T>) loadData(Class.forName(GameConstants.getInstance().getString(file.getName()+"Class")), file.getAbsolutePath()));
//                } catch (ClassNotFoundException e) {
//                    e.printStackTrace();
//                }
//            }
//                else{
//                FileReader fileReader = null;
//                try {
//                    fileReader = new FileReader(file);
//                    T t = gson.fromJson(fileReader,tClass);
//                    Session session = sessionFactory.openSession();
//                    session.beginTransaction();
//                  //  System.out.println(t.getClass() + " " + t.getClass());
//                    session.saveOrUpdate(t);
//                    session.getTransaction().commit();
//                    session.close();
//                    arr.add(t);
//                    fileReader.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return arr;
        Session session = sessionFactory.openSession();
        List<T> list = session.createQuery("from " + tClass.getName(), tClass).getResultList();
        session.close();
        return new ArrayList<>(list);
    }

    public synchronized static DataManager getInstance() {
        if(dataManager == null){
            dataManager = new DataManager();
        }
        return dataManager;
    }

    public  synchronized <T extends Character> ArrayList<T> getAllCharacter(Class<T> tClass){
        ArrayList<T> arr = new ArrayList<>();
        for(T t: (ArrayList<T>) dataMap.get(tClass)){
            arr.add((T) t.newOne());
        }
        return arr;
    }

    public synchronized <T extends Character> T getObject(Class<T> tClass, String name){
        for(T obj: (ArrayList<T>) dataMap.get(tClass)){
            if((obj.getName().trim()).equalsIgnoreCase(name))
                return (T) obj.newOne();
        }
        return null;
    }

    public synchronized ArrayList<Player> getAllPlayers() {
        synchronized (lock){
            return loadData(Player.class);
        }
    }

    public synchronized void deletePlayer(int id) {
        synchronized (lock){
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            session.delete(String.valueOf(id), Player.class);
            session.getTransaction().commit();
            session.close();
            //(new File(playersPath+File.separator+username)).delete();
        }
    }

    public synchronized void savePlayer(Player player) {
        synchronized (lock) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            session.saveOrUpdate(player);
            session.getTransaction().commit();
            session.close();
//            FileWriter fileWriter = null;
//            try {
//                fileWriter = new FileWriter(playersPath + File.separator + player.getUsername());
//                gson.toJson(player, fileWriter);
//                fileWriter.flush();
//                fileWriter.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }

    public synchronized ArrayList<Card> getDefaultCards() {
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(generalPath+File.separator+"Default Cards"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ArrayList<Card> defaultCards = new ArrayList<>();
        while (scanner.hasNext()){
            String str=scanner.nextLine();
            defaultCards.add(getObject(Card.class, str));
        }
        return defaultCards;
    }

    public synchronized ArrayList<Hero> getDefaultHeroes() {
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(generalPath+File.separator+"Default Heroes"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ArrayList<Hero> defaultHeroes = new ArrayList<>();
        while (scanner.hasNext()){
            String str=scanner.nextLine();
            defaultHeroes.add(getObject(Hero.class, str));
        }
        return defaultHeroes;
    }

    public synchronized Deck getBot(){
        Random random = new Random();
        ArrayList<Hero> heroes = getAllCharacter(Hero.class);
        Deck deck = new Deck("", heroes.get(random.nextInt(heroes.size())));
        ArrayList<Card> cards = getAllCharacter(Card.class);
        int size = cards.size();
        int cnt = 0;
        for(int i = 0; i < size; i++, cnt++){
            Card card = cards.get(cnt);
            if(card.getHeroClass().equalsIgnoreCase(deck.getHero().getName())){
                try {
                    deck.addCard(card);
                    cards.remove(card);
                    cnt--;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                if(!card.getHeroClass().equals("Neutral")){
                    cards.remove(card);
                    cnt--;
                }
            }
        }
        size = cards.size();
        int deckSize = deck.getCards().size();
        for(int i = 0; i < Math.min(deck.getHero().getDeckMax() - deckSize, size); i++){
            Card card = cards.get(random.nextInt(cards.size()));
            try {
                deck.addCard(card);
                cards.remove(card);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return deck;
    }

    public synchronized ArrayList<Deck> getDeckReaderDecks(){
        FileReader fileReader = null;
        DeckReader deckReader = null;
        try {
            fileReader = new FileReader(new File(GameConstants.getInstance().getString("DeckReaderAddress")));
            deckReader = gson.fromJson(fileReader, DeckReader.class);
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Deck friend = new Deck("", getObject(Hero.class, "Mage"), true);
        Deck enemy = new Deck("", getObject(Hero.class, "Mage"), true);
        for(String cardName:deckReader.getFriend()){
            friend.addCardWithCheat(getObject(Card.class, cardName));
        }
        for(String cardName:deckReader.getEnemy()){
            enemy.addCardWithCheat(getObject(Card.class, cardName));
        }
        ArrayList<Deck> decks = new ArrayList<>();
        decks.add(friend);
        decks.add(enemy);
        return decks;
    }
}
