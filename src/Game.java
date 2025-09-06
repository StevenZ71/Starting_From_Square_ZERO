import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.*;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.io.FileWriter;
//Keycode 48 through 57 is 0 to 9
public class Game extends JFrame implements KeyListener {
    private boolean up, down, left, right, one, two, three, four;
    private String code;
    private boolean save, start;
    private static int initializer;
    private int pressedX;
    private boolean testing;

    private GameScreen theGameScreen;
    private OtherScreen otherScreen;
    /**0 is storyteller, 1 is announcer(for items), 2 is Kuria, 3 is Lloyd, 14 is gf, 16 is duck*/
    private ArrayList<Person> npcList= new ArrayList<>();
    private ArrayList<Person> enemyList = new ArrayList<>();
    private ArrayList<Person> allyList = new ArrayList<>();
    private final ArrayList<Attack> attacks = new ArrayList<>();
    private final ArrayList<Attack> effects = new ArrayList<>();
    private String timeKeep = "";
    private final String[] skillList =
            {"Energy Slashes", "The Great Fire", "Thunder","Dash","Shield","Gambit","Break","Thunder Dragon","Magic Release","Recovery","Detonation","Bread","Revolving Blades","Singularity","Flashing Strikes","Benevolent Blade of the Hero","placeholder",""};
    private final boolean[] specialMove =
            {false, true, false, false, false, false, false, true, false, false, false, true, false, true, true, true, false, false, false};
    private final boolean[] holdable =
            {true, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false};
    private final String[] passiveList = {"Adventurer","Square Of Fire","Sword Master","Maniac","Vampire","Speedster","Juggernaut","Magic Augmentation","placeholder","placeholder","placeholder","placeholder"};
    //square of fire gives double burn damage and chance to burn enemies with basic attack and consumes energy to prevent fatal attacks every few seconds, sword master gives double cut multiplier and makes enemies bleed, which slows enemies, maniac makes player lose hp with each attack but attacks do more dmg, vampire gives lifesteal, speedster gives player more speed and cooldown go down every kill, heavyweight gives more hp and defense but less speed
    private ArrayList<String> inventory = new ArrayList<>();
    private Person player;
    private final int[] skillCooldowns = {3,20,5,1,1,10,10,20,3,15,10,20,3,20,20,20,5,0};
    private final int[] energyCosts = {10,100,20,5,5,5,15,100,25,5,20,100,15,100,100,10,10,0};
    private int fps;
    private final int intendedFps = 60; //can be increased to 60 if the tiles are made into squares and not images
    private int theFps;
    private int storyLocation;
    private int farthest = -1;
    private int playerMoney;
    private boolean inStory;
    private boolean gamePlaying = true;
    public boolean[] getSpecialMove(){return specialMove;}
    public boolean[] getHoldable(){return holdable;}
    private ArrayList<Quest> quests = new ArrayList<>();
    private ArrayList<Item> items = new ArrayList<>();
    public Person getPlayer() {
        return player;
    }
    public ArrayList<Person> getNpcList(){
        return npcList;
    }
    public ArrayList<Person> getEnemyList(){
        return enemyList;
    }
    public ArrayList<String> getInventory(){return inventory;}
    public GameScreen getGameScreen(){return theGameScreen;}
    public ArrayList<Quest> getQuests(){return quests;}
    public ArrayList<Item> getItems(){return items;}
    public ArrayList<Person> getAllyList(){return allyList;}
    public ArrayList<Attack> getAttacks(){return attacks;}
    public ArrayList<Attack> getEffects(){return effects;}
    public int[] getSkillCooldowns(){return skillCooldowns;}
    public int[] getEnergyCosts(){return energyCosts;}
    public boolean isGamePlaying(){return gamePlaying;}
    private int framesPlayed;
    private String saveCode;
    /**36 is 1, 10 is a*/
    private String characters = "          abcdefghijklmnopqrstuvwxyz1234567890,-!@#%^&*().ABCDEFGHIJKLMNOPQRSTUVWXYZ_";
    private Person[] bosses = new Person[2];
    private ArrayList everything = new ArrayList<>();

    public String toString() {
        return "Hello World!";
    }
    public boolean equals(Object other){
        return storyLocation==((Game)other).storyLocation;
    }
    public boolean hasCode(){return !code.equals("");}

    public void runGame(){
        Scanner scanner;
        try {
            scanner = new Scanner(new File("saveCode.txt"));
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        }
        try {
            code = scanner.nextLine();
        }catch (NoSuchElementException e){
            code = "";
        }
        MusicPlayer music = new MusicPlayer();
        music.playMusic();
        storyLocation = -1;
        Person.setGame(this);
        Attack.setGame(this);
        TimeKeeper.setGame(this);
        Quest.setGame(this);
        Item.setGame(this);
        player = new Person(25,375);
        player.cantMove();
        theGameScreen = new GameScreen(this);
        player.assignCooldowns(skillList,skillCooldowns,energyCosts);
        setFocusable(true);
        setTitle("Hidden Easter Egg");
        add(theGameScreen);
        addKeyListener(this);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(970,750));
        setResizable(false);
        pack();
        setVisible(true);
        setLocationRelativeTo(null);
        double drawInterval = 1000000000/ intendedFps;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        while(gamePlaying){
            currentTime = System.nanoTime();
            delta+=(currentTime-lastTime)/drawInterval;
            timer+=(currentTime-lastTime);
            lastTime=currentTime;

            if(delta>=1){
                //if (theGameScreen.needImage()) {repaint();}
                //if(testing){
                //    System.out.print("test");
                //    theGameScreen.drawMap(getGraphics());
                //}
                //else {
                //System.out.print("hm");
                //theGameScreen.checkIn();
                if(start) {
                    remove(theGameScreen);
                    otherScreen = new OtherScreen();
                    add(otherScreen);
                    pack();
                    repaint();
                    initialize();
                    remove(otherScreen);
                    add(theGameScreen);
                    pack();
                    theGameScreen.doneWithSetUp();
                    start = false;
                    if (save) {
                        forceExit();
                        inStory = false;
                        try {
                            decodeSaveCode(code);
                        } catch (NumberFormatException exc) {
                            System.out.println("Invalid Save Code. Try Again");
                            exitGame();
                        }
                        theGameScreen.moreSetUp();
                        theGameScreen.mapChange();
                    }
                    effects.clear();
                }
                repaint();
                //}
                storyStuff();
                questCheck();
                framesPlayed++;
                delta--;
            }
            if(timer>1000000000){
                theFps = fps;
                fps=0;
                timer=0;
            }
        }
    }
    public void setInitializer(int i){
        initializer = i;
    }
    public String[] passiveList(){return passiveList;}
    public String[] skillList(){return skillList;}
    public int getCurrentFps(){return theFps;}
    public void increaseFps(){fps++;}
    public boolean timeKeepEquals(String str){
        return timeKeep.equals(str);
    }
    public void setTimeKeep(String str){
        timeKeep = str;
    }
    public void initialize(){
        storyLocation = 0;
        initializeNpcs();
        initializeTimeKeepers();
        initializeItems();
        Attack initializer = new Attack();
        inventory.add("sword");
        playerMoney = 0;
        theGameScreen.setUp();
        theGameScreen.goToMapXY(0,1);
        theGameScreen.mapChange();
        player.canMove();
        startGame();
    }
    private void initializeNpcs(){
        /*0*/npcList.add(new Person("storyteller", -100,-100,0,0));
        /*1*/npcList.add(new Person("announcer", -100,-100,0,0));
        /*2*/npcList.add(new Person("kuria",500,275,3,1));
        /*3*/npcList.add(new Person("lloyd",550,275,3,1));
        /*4*/npcList.add(new Person("swordMaster",200,175,0,1));
        /*5*/npcList.add(new Person("rssLeader1",300,275,3,1));
        /*6*/npcList.add(new Person("IK",450,625,3,1));
        /*7*/npcList.add(new Person("noviceMage",450,725,3,0));
        /*8*/npcList.add(new Person("Y",625,475,3,2));
        /*9*/npcList.add(new Person("fillerGuy1",600,675,0,1));
        /*10*/npcList.add(new Person("fillerGuy2",300,675,0,1));
        /*11*/npcList.add(new Person("john",725,425,8,5));
        /*12*/npcList.add(new Person("Square",1050,400,0,1));
        /*13*/npcList.add(new Person("unfortunateSquare1",300,275,2,5));
        /*14*/npcList.add(new Person("gf",50,250,50,35));
        /*15*/npcList.add(new Person("swordProfessional",625,475,1,4));
        /*16*/npcList.add(new Person("duck",950,425,6,3));
        /*17*/npcList.add(new Person("johnFan1",675,500,51,35));
        /*18*/npcList.add(new Person("johnFan2",675,525,51,35));
        /*19*/npcList.add(new Person("unfortunateSquare2",675,275,55,33));
        /*20*/npcList.add(new Person("friend1",1225,375,-100,-100));
        /*21*/npcList.add(new Person("friend2",1225,425,-100,-100));
        /*22*/npcList.add(new Person("sorcerer",1025,600,55,33));
        /*23*/npcList.add(new Person("seller",1025,425,5,5));
        /*24*/npcList.add(new Person("soldier1",50,200,60,35));
        /*25*/npcList.add(new Person("you",-100,-100,-100,-1000));
        /*26*/npcList.add(new Person("redSquare1",500,300,64,35));
        /*27*/npcList.add(new Person("redGuard1",500,300,67,35));
    }
    private void initializeTimeKeepers(){
        timeKeep = "playerHeal";
        TimeKeeper.timerStart();
        waitUntilInitializer(1);
        timeKeep = "attackManager";
        TimeKeeper.timerStart();
        waitUntilInitializer(2);
        timeKeep = "skill1Manager";
        TimeKeeper.timerStart();
        waitUntilInitializer(3);
        timeKeep = "skill2Manager";
        TimeKeeper.timerStart();
        waitUntilInitializer(4);
        timeKeep = "skill3Manager";
        TimeKeeper.timerStart();
        waitUntilInitializer(5);
        timeKeep = "skill4Manager";
        TimeKeeper.timerStart();
        waitUntilInitializer(6);
        timeKeep = "statusEffectManager";
        TimeKeeper.timerStart();
    }
    private void waitUntilInitializer(int i){
        while(initializer!=i){
            System.out.print("");
        }
    }
    private void initializeItems(){
        Item item = new Item();
        items.add(new Item("ribbon",625,230,3,2));
        items.add(new Item("bread",1300,600,6,1));
        items.add(new Item("boxCoin",150,150,0,1));
        items.add(new Item("box",150,25,6,3));
        items.add(new Item("teleportPad",1287,362,6,3));
        items.add(new Item("unusableTeleportPad",25,362,50,35));
        items.add(new Item("unusableTeleportPad",35,262,50,35));
        items.add(new Item("unusableTeleportPad",35,462,50,35));
        items.add(new Item("unusableTeleportPad",25,260,50,35));
        items.add(new Item("unusableTeleportPad",25,464,50,35));
        createItem("rippedPaper1",105,460,72,37,15);
        createItem("mossRock",70,45,71,37,25,25);
        createItem("mossRock",90,45,71,37,25,25);
        createItem("mossRock",80,55,71,37,25,25);
        createItem("mossRock",410,355,71,37,25,25);
        createItem("mossRock",430,355,71,37,25,25);
        createItem("mossRock",420,365,71,37,25,25);
        createItem("mossRock",230,650,71,37,25,25);
        createItem("mossRock",250,650,71,37,25,25);
        createItem("mossRock",240,660,71,37,25,25);
        createItem("cutMossPillar",25,45,71,37,15);
        createItem("cutMossPillar",200,150,71,37,0);
        createItem("cutMossPillar",100,305,71,37,5);
        createItem("cutMossPillar",400,423,71,37,10);
        createItem("cutMossPillar",300,65,71,37,20);
        createItem("cutMossPillar",700,450,71,37,25);
        createItem("cutMossPillar",1000,690,71,37,-5);
        createItem("cutMossPillar",1345,215,71,37,-5);
        createItem("cutMossPillar",130,500,71,37,-1);
        createItem("cutMossPillar",170,650,71,37,-10);
        createItem("cutMossPillar",1200,490,71,37,20);
        createItem("cutMossPillar",800,235,71,37,-7);
        createItem("cutMossPillar",550,100,71,37,-5);
        createItem("cutMossPillar",970,300,71,37,-5);
        createItem("cutMossPillar",25,45,72,37,15);
        createItem("cutMossPillar",200,150,72,37,0);
        createItem("cutMossPillar",100,305,72,37,5);
        createItem("cutMossPillar",400,423,72,37,10);
        createItem("cutMossPillar",300,65,72,37,20);
        createItem("cutMossPillar",700,450,72,37,25);
        createItem("cutMossPillar",1000,690,72,37,-5);
        createItem("cutMossPillar",1345,215,72,37,-5);
        createItem("cutMossPillar",130,500,72,37,-1);
        createItem("cutMossPillar",170,650,72,37,-10);
        createItem("cutMossPillar",1200,490,72,37,20);
        createItem("cutMossPillar",800,235,72,37,-7);
        createItem("cutMossPillar",550,100,72,37,-5);
        createItem("cutMossPillar",970,300,72,37,-5);
        createItem("cutMossPillar",25,45,71,38,15);
        createItem("cutMossPillar",200,150,71,38,0);
        createItem("cutMossPillar",100,305,71,38,5);
        createItem("cutMossPillar",400,423,71,38,10);
        createItem("cutMossPillar",300,65,71,38,20);
        createItem("cutMossPillar",700,450,71,38,25);
        createItem("cutMossPillar",1000,690,71,38,-5);
        createItem("cutMossPillar",1345,215,71,38,-5);
        createItem("cutMossPillar",130,500,71,38,-1);
        createItem("cutMossPillar",170,650,71,38,-10);
        createItem("cutMossPillar",1200,490,71,38,20);
        createItem("cutMossPillar",800,235,71,38,-7);
        createItem("cutMossPillar",550,100,71,38,-5);
        createItem("cutMossPillar",970,300,71,38,-5);
        createItem("mossLog",1350,475,71,37,50,50,15);
        createItem("mossLog",1340,460,71,37,50,50,15);
        createItem("rippedPaper2",300,300,-100,-100,20);
        createItem("rippedPaper3",200,500,-100,-100,-15);
    }
    private void createItem(String name, int x, int y, int mX, int mY){
        Item item = new Item(name,x,y,mX,mY);
        items.add(item);
    }
    private void createItem(String name, int x, int y, int mX, int mY, int width, int height){
        Item item = new Item(name,x,y,mX,mY);
        item.alterDimension(width, height);
        items.add(item);
    }
    private void createItem(String name, int x, int y, int mX, int mY, int degrees){
        Item item = new Item(name,x,y,mX,mY);
        item.rotate(degrees);
        items.add(item);
    }
    private void createItem(String name, int x, int y, int mX, int mY, int width, int height, int degrees){
        Item item = new Item(name,x,y,mX,mY);
        item.alterDimension(width, height);
        item.rotate(degrees);
        items.add(item);
    }
    private void startGame(){
        if(!save) {
            interactWith(0);
            enemyList.clear();
            enemyList.add(new Person("meleeEnemy", 1425, 400, 0, 150, 0, 0, 0, 0, 1));
            enemyList.get(0).setSpecialRole("advanceStorydarkScreen");
            inStory = true;
        }
    }
    private void interactWith(int indexOfNpc){
        //player.canMove();
        theGameScreen.touchNpc(indexOfNpc);
        theGameScreen.storyInteract();
    }

    @Override
    public void keyReleased(KeyEvent e) {
//        System.out.println(player.movable());
        int keyCode = e.getKeyCode();
        //System.out.println(keyCode + "opening");
        if (keyCode == 88) {
            pressedX = 0;
            if (theGameScreen.checkInChat()) {
                theGameScreen.advanceText(1,true);
            } else if (theGameScreen.checkMenu()) {
                theGameScreen.pressButton();
            }
            else if(storyLocation==-1){
                if(theGameScreen.mainMenuSelect()==0 || (theGameScreen.mainMenuSelect()==1 && hasCode()) && !theGameScreen.inInstructions()) {
                    if (theGameScreen.mainMenuSelect() == 1) {
//                        setVisible(false);
//                        Scanner scanner = new Scanner(System.in);
//                        System.out.println("Input Save Code Below.");
//                        code = scanner.nextLine();
//                        checkCode(code);
//                        setVisible(true);
                    }
                    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    setPreferredSize(new Dimension((theGameScreen.columns() * theGameScreen.squareSize() + 15), (theGameScreen.rows() * theGameScreen.squareSize() + 35)));
                    setResizable(false);
                    pack();
                    setLocationRelativeTo(null);
                    start = true;
//                setFocusable(true);
//                setTitle("GameScreen");
//                add(theGameScreen);
//                addKeyListener(this);
                    if(theGameScreen.mainMenuSelect()==1) {
                        save = true;
                    }
                }
                else if(theGameScreen.mainMenuSelect()==2){
                    theGameScreen.toggleHowToPlay();
                }
            }
            else if(theGameScreen.checkCanInteract() && !theGameScreen.checkMenu()) {
                theGameScreen.interact();
            } else if(!theGameScreen.checkMenu()){
                interactWithItem();
            }
        } else if (keyCode == 90) {
            if (theGameScreen.checkInChat()) {
                theGameScreen.advanceText(1, false);
            }
        } else if (keyCode == 32){
            if(theGameScreen.checkInChat()){
                skipChat();
            }
            else if(player.movable() && !player.inAttack() && !theGameScreen.checkInChat() && !theGameScreen.checkMenu()) {
                player.attack();
            }
        } else if (keyCode == 49) {
            if( ((player.movable() && player.cooldownDone(0)  && player.getEnergy()>=player.getEnergyCost(0) && !theGameScreen.checkInChat() && !theGameScreen.checkMenu()) || player.getHeld(0))) {
                player.skill(0);
            }
            else{
                player.letGo(0);
            }
            one = false;
        } else if (keyCode == 50){
            if((player.movable() && player.cooldownDone(1)  && player.getEnergy()>=player.getEnergyCost(1) && !theGameScreen.checkInChat() && !theGameScreen.checkMenu()) || player.getHeld(1))
            {
                player.skill(1);
            }
            else{
                player.letGo(1);
            }
            two = false;
        } else if (keyCode == 51){
            if((player.movable() && player.cooldownDone(2)  && player.getEnergy()>=player.getEnergyCost(2) && !theGameScreen.checkInChat() && !theGameScreen.checkMenu()) || player.getHeld(2))
            {
                player.skill(2);
            }
            else{
                player.letGo(2);
            }
            three = false;
        } else if (keyCode == 52){
            if((player.movable() && player.cooldownDone(3)  && player.getEnergy()>=player.getEnergyCost(3) && !theGameScreen.checkInChat() && !theGameScreen.checkMenu()) || player.getHeld(3))
            {
                player.skill(3);
            }
            else{
                player.letGo(3);
            }
            four = false;
        } else if (keyCode == 77) {
            if (theGameScreen.checkMenu()) {
                theGameScreen.menuClose();
            } else if (!theGameScreen.checkInChat() && !inStory) {
                theGameScreen.menuOpen();
            }
        } else if (theGameScreen.checkMenu() || storyLocation==-1) {
            if (keyCode == 40 || keyCode == 83) {
                theGameScreen.moveMenu(0,1);
            } else if (keyCode == 38 || keyCode == 87) {
                theGameScreen.moveMenu(0,-1);
            }
            else if (keyCode == 39 || keyCode == 68) {
                theGameScreen.moveMenu(1,0);
            }
            else if (keyCode == 37 || keyCode == 65) {
                theGameScreen.moveMenu(-1,0);
            }
        } else if (theGameScreen.checkSelect()) {
            if (keyCode == 40 || keyCode == 83) {
                theGameScreen.moveMenu(0,1);
            } else if (keyCode == 38 || keyCode == 87) {
                theGameScreen.moveMenu(0,-1);
            }
        }
        else if (keyCode==72 && player.movable()){
            if(!player.biggerSword()) {
                if(player.getEnergy()>=10) {
                    player.setMode(1);
                    attacks.add(new Attack(player.getxPos() - 32, player.getyPos() - 64, 30, "bigSlash", player));
                }
            }
        }else if(keyCode == 73){
            //System.out.println(generateSaveCode());
            //decodeSaveCode("4040$1393645$236$345$445$545$645$736$845574536$93740$136$0454646463846464646464346464646464646$1384643463645463746$245464646464646464646464646$3454646$43738535336455353455353455353445353455315102128141510212814534553151021281453455353455353455353455353455353375353$536$61510212814531510212814532927301453$7");

//            for(int i = 0 ; i < 100; i++){
            player.cheat();
            //System.out.println(theGameScreen.getScrollX() + " " + theGameScreen.getScrollY());
            //printStuff();
//                player.increaseStat("physical");
//                player.increaseStat("hp");
//                player.increaseStat("magic");
//                healPlayer();
//////                player.setSpeed(25);
//            }
            player.setSpeed(25);
            //testing = true;
            for(int i = 0; i < skillList.length; i++){
                if(!player.skillsContain(i)) {
                    player.obtainSkill(i);
                }
            }
            for(int i = 0; i < passiveList.length; i++){
                if(!player.passivesContain(i)) {
                    player.obtainPassive(i);
                }
            }
        }
//        else if(keyCode == 27){
//            gamePlaying=false;
//            System.out.println("You have manually exited the game.");
//            setVisible(false);
//            removeKeyListener(this);
//        }
        if (keyCode == 40 || keyCode == 83) {
            down = false;
        }
        else if (keyCode == 39 || keyCode == 68) {
            right = false;
        }
        else if (keyCode == 38 || keyCode == 87) {
            up = false;
        } else if (keyCode == 37 || keyCode == 65) {
            left = false;
        }
        //System.out.println(keyCode + "closed");
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //System.out.println("type open");
        //System.out.println("type closed");
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();// can be changed into make booleans true, make booleans false in the loop
        //System.out.println("pressed open");
        if (keyCode == 40 || keyCode == 83) {
            down = true;
            //theGameScreen.movePlayer(0, player.getSpeed());
        }
        else if (keyCode == 39 || keyCode == 68) {
            right = true;
            //theGameScreen.movePlayer(player.getSpeed(), 0);
        }
        else if (keyCode == 38 || keyCode == 87) {
            up = true;
            //theGameScreen.movePlayer(0, -player.getSpeed());
        } else if (keyCode == 37 || keyCode == 65) {
            left = true;
            //theGameScreen.movePlayer(-player.getSpeed(), 0);
        }
        else if(keyCode==88){
            pressedX++;
            if(pressedX>20 && theGameScreen.checkMenu()){
                theGameScreen.specialPress();
                pressedX-=2;
            }
        }
        else if (keyCode == 49 && (player.movable() || player.getHeld(0)) && player.cooldownDone(0)  && player.getEnergy()>=player.getEnergyCost(0) && !theGameScreen.checkInChat() && !theGameScreen.checkMenu()){
            one = true;
        } else if (keyCode == 50 && (player.movable() || player.getHeld(1)) && player.cooldownDone(1)  && player.getEnergy()>=player.getEnergyCost(1) && !theGameScreen.checkInChat() && !theGameScreen.checkMenu()) {
            two = true;
        } else if (keyCode == 51 && (player.movable() || player.getHeld(2)) && player.cooldownDone(2)  && player.getEnergy()>=player.getEnergyCost(2) && !theGameScreen.checkInChat() && !theGameScreen.checkMenu()) {
            three= true;
        } else if (keyCode == 52 && (player.movable() || player.getHeld(2)) && player.cooldownDone(3)  && player.getEnergy()>=player.getEnergyCost(3) && !theGameScreen.checkInChat() && !theGameScreen.checkMenu()) {
            four = true;
        }
        //System.out.println("press close");
    }
    public boolean play(){return start;}

    public void wait(int milliSec) {
        try {
            TimeUnit.MILLISECONDS.sleep(milliSec);
        } catch (InterruptedException e) {
            System.out.println(":(");
        }
    }
    private void waitUntilAttackDone(Attack attack){
        while(!attack.attackDeletable() && attacks.contains(attack)){
            wait(1);
            //System.out.print("attacking");
        }
    }
    public void loop(){
        checkStuff();
        if(inStory || theGameScreen.checkMenu() || theGameScreen.checkInChat()){
            player.letGo(0);
            player.letGo(1);
            player.letGo(2);
            player.letGo(3);
            one = false;
            two = false;
            three = false;
            four = false;
        }
        if(one){
            player.hold(0);
        }
        if(two){
            player.hold(1);
        }
        if(three){
            player.hold(2);
        }
        if(four){
            player.hold(3);
        }
        if (player.movable()) {
            player.setSpeed(5);
            if(player.passiveEquipped(5)) {
                player.adjustSpeed(1);
            }
            if(player.passiveEquipped(6)) {
                player.adjustSpeed(-1);
            }
            if(player.held()){
                player.setSpeed(player.getSpeed()/2);
            }
            for(int i = 0; i < player.getSpeed(); i++) {
                if (right) {
                    theGameScreen.movePlayer(1, 0);
                }
                if (left) {
                    theGameScreen.movePlayer(-1, 0);
                }
                if (up) {
                    theGameScreen.movePlayer(0, -1);
                }
                if (down) {
                    theGameScreen.movePlayer(0, 1);
                }
            }
//            if(player.getxPos()>1375){
//                if(storyLocation==27){
//                    player.goToXY(1375,player.getyPos());
//                }
//            }
//            if(storyLocation==27){
//                while(player.getxPos()<700 && 700-player.getxPos()+theGameScreen.getScrollX() > -81825){
//                    player.moveX(1);
//                }
//            }
            for (int i = 0; i < items.size(); i++) {
                Item item = items.get(i);
                if(item.interactable()) {
                    int xPos = item.getxPos();
                    int yPos = item.getyPos();
                    if (xPos + item.width() * item.size() > player.getxPos() && xPos < player.getxPos() + theGameScreen.squareSize() && yPos + item.height() * item.size() > player.getyPos() && yPos < player.getyPos() + theGameScreen.squareSize() && !theGameScreen.checkInChat() && theGameScreen.itemOnMap(item) && !inStory && !item.isRemovable()) {
                        npcList.get(1).switchIndexes(item.index());
                        npcList.get(1).setAnnouncerItem(item);
                        theGameScreen.touchNpc(1);
                    }
                }
            }
        }
        else if(!inStory && !theGameScreen.checkInChat() && player.getAttackMove()!=0){
            for(int i = 0; i < player.getAttackMove(); i++) {
                if (right) {
                    theGameScreen.movePerson(1, 0,player);
                }
                if (left) {
                    theGameScreen.movePerson(-1, 0,player);
                }
                if (up) {
                    theGameScreen.movePerson(0, -1,player);
                }
                if (down) {
                    theGameScreen.movePerson(0, 1,player);
                }
            }
        }
        player.reserveHeal();
        for(Person person : enemyList){
            person.reserveHeal();
            person.setSpeed(5);
            if(person.getType().contains("melee")){
                person.adjustSpeed(1);
            }
            if(person.passiveEquipped(5)) {
                person.adjustSpeed(1);
            }
            if(person.passiveEquipped(6)) {
                person.adjustSpeed(-1);
            }
        }
        for(Person person : allyList){
            person.reserveHeal();
            person.setSpeed(5);
            if(person.getType().contains("melee")){
                person.adjustSpeed(1);
            }
            if(person.passiveEquipped(5)) {
                person.adjustSpeed(1);
            }
            if(person.passiveEquipped(6)) {
                person.adjustSpeed(-1);
            }
        }
    }
    private void checkStuff(){
        if(storyLocation < 10 && Math.abs(player.getxPos() - npcList.get(13).getxPos()) < 400 && Math.abs(player.getyPos() - npcList.get(13).getyPos()) < 400 && !inStory){
            forceExit();
            enterStory();
        }
        if(storyLocation<17 && Math.abs(player.getxPos() - npcList.get(2).getxPos()) < 100 && Math.abs(player.getyPos() - npcList.get(2).getyPos()) < 100 && !inStory && theGameScreen.getMapNumX()==51 && theGameScreen.getMapNumY()==35){
            advanceStory();
            forceExit();
            enterStory();
        }
        if(storyLocation==24 && Math.abs(player.getxPos() - npcList.get(14).getxPos()) < 100 && Math.abs(player.getxPos() - npcList.get(14).getxPos()) < 100 && !inStory){
            forceExit();
            enterStory();
        }
    }

    public int checkCollision(int xPos, int yPos, int width, int height, int x, int y){
        return theGameScreen.collisionCheck(xPos, yPos, width, height, x, y);
    }
    public void movePerson(int x, int y,Person person){
        theGameScreen.movePerson(x,y,person);
    }

    public void attack(){
//        if(player.biggerSword() && player.getEnergy()<5){
//            player.setMode(0);
//        }
        System.out.print("");
        boolean canAttack = /*(!player.biggerSword() || (player.biggerSword() && player.getEnergy()>=5) && */player.movable();
        if (player.inAttack() && canAttack) {
            if(player.passiveEquipped(3)){
                player.losePercentHp(0.5);
            }
            player.cantMove();
//            if(player.biggerSword()){
//                player.useEnergy(5);
//            }
            if(player.getAttackCombo()==0){
                Attack slash = new Attack(player.getxPos()-25,player.getyPos()-35,0, "attack",player);
                attacks.add(slash);
                player.increaseAttackCombo();
                player.addToCombo("attack1");
                waitUntilAttackDone(slash);
            }
            else if(player.getAttackCombo()==1){
                Attack slash = new Attack(player.getxPos()-25,player.getyPos()-35,0, "attack2",player);
                attacks.add(slash);
                player.increaseAttackCombo();
                player.addToCombo("attack2");
                waitUntilAttackDone(slash);
            }
            else if(player.getAttackCombo()==2){
                Attack slash = new Attack(player.getxPos()-25,player.getyPos()-35,0, "attack",player);
                attacks.add(slash);
                player.increaseAttackCombo();
                player.addToCombo("attack1");
                waitUntilAttackDone(slash);
            }
            else if(player.getAttackCombo()==3){
                Attack slash = new Attack(player.getxPos()-25,player.getyPos()-40,0, "attack3",player);
                attacks.add(slash);
                player.increaseAttackCombo();
                player.addToCombo("attack3");
                waitUntilAttackDone(slash);
            }
            if(player.passiveEquipped(1)){
                if(Math.random()>0.9){
                    attacks.add(new Attack(player.getxPos()-70,player.getyPos()-85,0,"searingSlash",player));
                }
            }
            player.canMove();
        }
        player.attackDone();
    }

    public void healPlayer() {
        player.heal();
        player.recoverEnergy();
        for (Person person : enemyList) {
            person.heal();
            person.recoverEnergy();
        }
    }
    private void skill(int skillNum, Person person){
        person.setAttackMove(0);
        if(person.passiveEquipped(3)){
            person.losePercentHp(3);
        }
        String skill = person.getSkill(skillNum);
        if(person.getLengthHeld(skillNum)>59 && person.getHeld(skillNum)){
            if(skill.equals(skillList[0])){
                person.setAttackMove(1);
                for(int i = 10; i < 20; i+=3) {
                    Attack slash = new Attack(person.getxPos() - 10, person.getyPos() - 10, 25, "slash", person);
                    slash.rotate(-i);
                    slash.rotateImage(-i);
                    attacks.add(slash);
                    slash = new Attack(person.getxPos() - 10, person.getyPos() - 10, 25, "slash", person);
                    slash.rotate(i);
                    slash.rotateImage(i);
                    attacks.add(slash);
                    slash = new Attack(person.getxPos() - 10, person.getyPos() - 10, 25, "slash", person);
                    attacks.add(slash);
                    wait(200);
                }
                for(int i = 20; i > 10; i-=3) {
                    Attack slash = new Attack(person.getxPos() - 10, person.getyPos() - 10, 25, "slash", person);
                    slash.rotate(-i);
                    slash.rotateImage(-i);
                    attacks.add(slash);
                    slash = new Attack(person.getxPos() - 10, person.getyPos() - 10, 25, "slash", person);
                    slash.rotate(i);
                    slash.rotateImage(i);
                    attacks.add(slash);
                    slash = new Attack(person.getxPos() - 10, person.getyPos() - 10, 25, "slash", person);
                    attacks.add(slash);
                    wait(200);
                }
            }
            else if(skill.equals(skillList[2])){
                for(int magnitude = 20; magnitude < 220; magnitude+=25) {
                    for (int i = 0; i < 360; i += 45 - magnitude/20) {
                        int cos = (int)(Math.cos(Math.toRadians(i)) * magnitude);
                        int sin = (int)(Math.sin(Math.toRadians(i)) * magnitude);
                        attacks.add(new Attack(person.getxPos() + cos - 27, person.getyPos() + sin - 29, 0, "lightning", person));
                    }
                    wait(100);
                }
            }
            else if(skill.equals(skillList[16])){

            }
        }
        else {
            if (skill.equals(skillList[0])) {
                Attack slash = new Attack(person.getxPos() - 10, person.getyPos() - 10, 25, "slash", person);
                slash.rotate(-15);
                slash.rotateImage(-15);
                attacks.add(slash);
                slash = new Attack(person.getxPos() - 10, person.getyPos() - 10, 25, "slash", person);
                slash.rotate(15);
                slash.rotateImage(15);
                attacks.add(slash);
                slash = new Attack(person.getxPos() - 10, person.getyPos() - 10, 25, "slash", person);
                attacks.add(slash);
                slash = new Attack(person.getxPos() - 10, person.getyPos() - 10, 25, "slash", person);
                slash.rotate(-30);
                slash.rotateImage(-30);
                attacks.add(slash);
                slash = new Attack(person.getxPos() - 10, person.getyPos() - 10, 25, "slash", person);
                slash.rotate(30);
                slash.rotateImage(30);
                attacks.add(slash);
                wait(100);
            } else if (skill.equals(skillList[1])) {
                Attack wisp = new Attack(person.getxPos() - 50, person.getyPos() - 90, 0, "pillarManager", person);
                attacks.add(wisp);
                person.turnInvincible();
                while (!wisp.attackDeletable() && attacks.contains(wisp)) {
                    wait(1);
                }
                person.notInvincible();
                Attack whiteFlash = new Attack("whiteFlash", person == player, person);
                effects.add(whiteFlash);
                for (int i = 0; i < 100; i++) {
                    //attacks.add(new Attack(i*64,745,0, "fire",person));
                    attacks.add(new Attack((int) (Math.random() * 1415) + 25, (int) (Math.random() * 765) + 25, 0, "burn", person));
                }
                person.setMode(2);
                wait(1000);
            } else if (skill.equals(skillList[2])) {
                person.setAttackMove(person.getSpeed()/2);
                for(int i = 1; i < 31; i+=5){
                    int x = (int)(Math.cos(i) * 3 * i);
                    int y = (int)(Math.sin(i) * 2 * i);
                    attacks.add(new Attack(person.getxPos() + x, person.getyPos() + y, 0, "lightning", person));
                    wait(50);
                }
//                attacks.add(new Attack(person.getxPos(), person.getyPos(), 0, "lightning", person));
//                wait(50);
//                attacks.add(new Attack(person.getxPos() + 25, person.getyPos() + 25, 0, "lightning", person));
//                wait(50);
//                attacks.add(new Attack(person.getxPos() + 25, person.getyPos() - 25, 0, "lightning", person));
//                wait(50);
//                attacks.add(new Attack(person.getxPos() - 25, person.getyPos() - 25, 0, "lightning", person));
//                wait(50);
//                attacks.add(new Attack(person.getxPos() - 25, person.getyPos() + 25, 0, "lightning", person));
//                wait(50);
//                attacks.add(new Attack(person.getxPos() + 50, person.getyPos(), 0, "lightning", person));
//                wait(50);
//                attacks.add(new Attack(person.getxPos() - 50, person.getyPos(), 0, "lightning", person));
//                wait(50);
//                attacks.add(new Attack(person.getxPos(), person.getyPos() - 50, 0, "lightning", person));
//                wait(50);
//                attacks.add(new Attack(person.getxPos(), person.getyPos() + 50, 0, "lightning", person));
//                wait(350);
//                attacks.add(new Attack(person.getxPos() - 50, person.getyPos() - 50, 0, "lightningBig", person));
//                wait(100);
            } else if (skill.equals(skillList[3])) {
                boolean combo = person.combo(new String[]{"attack1", "attack2", "attack1"}) && person.getAttackCombo() == 3 && person.skillsContain(0) && !person.getDirection().equals("up") && !person.getDirection().equals("down");
                for (int i = 0; i < 25; i++) {
                    wait(5);
                    if (person.getDirection().equals("left")) {
                        theGameScreen.movePerson(-5, 0, person);
                    } else if (person.getDirection().equals("right")) {
                        theGameScreen.movePerson(5, 0, person);
                    } else if (person.getDirection().equals("down")) {
                        theGameScreen.movePerson(0, 5, person);
                    } else if (person.getDirection().equals("up")) {
                        theGameScreen.movePerson(0, -5, person);
                    }
                    if (combo) {
                        attacks.add(new Attack(person.getxPos(), person.getyPos(), 0, "dashHit", person));
                    }
                }
                if (combo) {
                    attacks.add(new Attack(person.getxPos() - 10, person.getyPos() - 10, 25, "slash", person));
                }
            } else if (skill.equals(skillList[4])) {
                boolean combo = person.combo(new String[]{"attack1", "attack2", "attack1", "Dash"}) && person.passiveEquipped(1);
                person.turnInvincible();
                attacks.add(new Attack(person.getxPos() - 13, person.getyPos() - 13, 0, "shield", person));
                wait(200);
                if (combo) {
                    Attack slash1 = new Attack(person.getxPos() - 70, person.getyPos() - 85, 0, "searingSlash", person);
                    Attack slash2 = new Attack(person.getxPos() - 50, person.getyPos() - 85, 0, "searingSlash", person);
                    if (slash1.getDirection().equals("right")) {
                        slash1.flip();
                    } else {
                        slash2.flip();
                    }
                    attacks.add(slash1);
                    attacks.add(slash2);
                }
                person.notInvincible();
            } else if (skill.equals(skillList[5])) {
                person.toggleGambit();
                attacks.add(new Attack(person.getxPos() - 3, person.getyPos() - 32, 0, "gambit", person));
                wait(500);
                if (!person.didGambitWork()) {
                    person.useUpEnergy();
                } else {
                    if (person.passiveEquipped(1)) {
                        Attack slash1 = new Attack(person.getxPos() - 70, person.getyPos() - 85, 0, "searingSlash", person);
                        Attack slash2 = new Attack(person.getxPos() - 70, person.getyPos() - 85, 0, "searingSlash", person);
                        slash1.flip();
                        attacks.add(slash1);
                        attacks.add(slash2);
                    }
                }
                person.toggleGambit();
            } else if (skill.equals(skillList[6])) {
                person.setAttackBooster(2);
                effects.add(new Attack("break", person == player, person));
            } else if (skill.equals(skillList[7])) {
                Attack dragon = new Attack(0, 0, 0, "thunderDragon", person);
                attacks.add(dragon);
                person.turnInvincible();
                while (!dragon.attackDeletable()) {
                    wait(1);
                }
                person.notInvincible();
            } else if (skill.equals(skillList[8])) {
                String dir = person.getDirection();
                person.setDirection("right");
                attacks.add(new Attack(person.getxPos(), person.getyPos(), 10, "magicBullet", person));
                person.setDirection("left");
                attacks.add(new Attack(person.getxPos(), person.getyPos(), 10, "magicBullet", person));
                person.setDirection("up");
                attacks.add(new Attack(person.getxPos(), person.getyPos(), 10, "magicBullet", person));
                person.setDirection("down");
                attacks.add(new Attack(person.getxPos(), person.getyPos(), 10, "magicBullet", person));
                person.setDirection(dir);
            } else if (skill.equals(skillList[9])) {
                person.setAttackMove(1);
                for (int i = 0; i < 15; i++) {
                    person.heal(3);
                    person.recoverEnergy(2);
                    attacks.add(new Attack("heal", person == player, person));
                    wait(50);
                }
            } else if (skill.equals(skillList[10])) {
                ArrayList<Person> list;
                if(enemyList.contains(person)){
                    list = allyList;
                }
                else{
                    list = enemyList;
                }
                for(int j = 0; j < 10; j++) {
                    for (int i = 0; i < list.size(); i++) {
                        Person enemy = list.get(i);
                        Attack explosion = new Attack(enemy.getxPos(), enemy.getyPos(), 0, "explosion", person);
                        explosion.multiplySize(2);
                        attacks.add(explosion);
                    }
                    wait(25);
                }
            } else if (skill.equals(skillList[11])) {
                int rand = randomNum(3, 0);
                person.setAttackMove(person.getSpeed()/2 + 1);
                if (rand == 0) {
                    for (int i = 0; i < 30; i++) {
//                        if (person != player) {
//                            if (person.getDirection().equals("left")) {
//                                theGameScreen.movePerson(-5, 0, person);
//                            } else if (person.getDirection().equals("right")) {
//                                theGameScreen.movePerson(5, 0, person);
//                            } else if (person.getDirection().equals("down")) {
//                                theGameScreen.movePerson(0, 5, person);
//                            } else if (person.getDirection().equals("up")) {
//                                theGameScreen.movePerson(0, -5, person);
//                            }
//                        } else {
//                            if (up) {
//                                theGameScreen.movePerson(0, -5, person);
//                            }
//                            if (down) {
//                                theGameScreen.movePerson(0, 5, person);
//                            }
//                            if (left) {
//                                theGameScreen.movePerson(-5, 0, person);
//                            }
//                            if (right) {
//                                theGameScreen.movePerson(5, 0, person);
//                            }
//                        }
                        attacks.add(new Attack(person.getxPos() - 51, person.getyPos() - 29, 0, "breadSlashes", person));
                        wait(50);
                    }
                } else if (rand == 1) {
                    for (int i = 0; i < 100; i++) {
                        attacks.add(new Attack((int) (Math.random() * this.getWidth() - 400), 0, 10, "breadMeteor", person));
                        attacks.add(new Attack((int) (Math.random() * this.getWidth() - 400), 0, 10, "breadMeteor", person));
                        wait(50);
                    }
                } else if (rand == 2) {
                    for (int i = 0; i < 15; i++) {
                        attacks.add(new Attack(person.getxPos() - 51, person.getyPos() - 29, 0, "breadSlashes", person));
                        attacks.add(new Attack(person.getxPos(), person.getyPos() + 10, 20, "breadMissile", person));
                        Attack missile = new Attack(person.getxPos(), person.getyPos() + 10, 20, "breadMissile", person);
                        missile.flip();
                        attacks.add(missile);
                        attacks.add(new Attack(person.getxPos(), person.getyPos() - 40, 20, "breadMissile", person));
                        Attack missile2 = new Attack(person.getxPos(), person.getyPos() - 40, 20, "breadMissile", person);
                        missile2.flip();
                        attacks.add(missile2);
                        attacks.add(new Attack(person.getxPos(), person.getyPos() - 15, 20, "breadMissile", person));
                        Attack missile3 = new Attack(person.getxPos(), person.getyPos() - 15, 20, "breadMissile", person);
                        missile3.flip();
                        attacks.add(missile3);
                        wait(100);
                    }
                }
            } else if (skill.equals(skillList[12])) {
                attacks.add(new Attack(person.getxPos(), person.getyPos(), 10, "revolvingBlade", person));
                Attack blade = new Attack(person.getxPos(), person.getyPos(), 10, "revolvingBlade", person);
                Attack blade2 = new Attack(person.getxPos(), person.getyPos(), 10, "revolvingBlade", person);
                blade.rotate(45);
                blade2.rotate(-45);
                attacks.add(blade);
                attacks.add(blade2);
                if (person.getDirection().equals("right")) {
                    theGameScreen.shovePerson(-20, 0, person);
                } else if (person.getDirection().equals("left")) {
                    theGameScreen.shovePerson(20, 0, person);
                } else if (person.getDirection().equals("up")) {
                    theGameScreen.shovePerson(0, 20, person);
                } else if (person.getDirection().equals("down")) {
                    theGameScreen.shovePerson(0, -20, person);
                }
                wait(100);
            } else if (skill.equals(skillList[13])) {
                attacks.add(new Attack(0, person.getyPos(), 0, "singularity", person));
            } else if (skill.equals(skillList[14])) {
                for (int rep = 0; rep < 50; rep++) {
                    int mapX = theGameScreen.getMapNumX();
                    int mapY = theGameScreen.getMapNumY();
                    if (!theGameScreen.checkInChat() && !inStory) {
                        int dX = 0;
                        int dY = 0;
                        int tempX = person.getxPos();
                        int tempY = person.getyPos();
                        for (int i = 0; i < 10; i++) {
                            if (person != player) {
                                if (person.getDirection().equals("left")) {
                                    theGameScreen.movePerson(-20, 0, person);
                                } else if (person.getDirection().equals("right")) {
                                    theGameScreen.movePerson(20, 0, person);
                                } else if (person.getDirection().equals("down")) {
                                    theGameScreen.movePerson(0, 20, person);
                                } else if (person.getDirection().equals("up")) {
                                    theGameScreen.movePerson(0, -20, person);
                                }
                            } else {
                                if (up) {
                                    theGameScreen.movePerson(0, -20, person);
                                }
                                if (down) {
                                    theGameScreen.movePerson(0, 20, person);
                                }
                                if (left) {
                                    theGameScreen.movePerson(-20, 0, person);
                                }
                                if (right) {
                                    theGameScreen.movePerson(20, 0, person);
                                }
                                //wait(100);
//                            tempX += person.getxPos() - tempX;
//                            tempY += person.getyPos() - tempY;
//                            theGameScreen.scroll();
                            }
                        }
                        if (tempX != person.getxPos()) {
                            dX = tempX - person.getxPos();
                        }
                        if (tempY != person.getyPos()) {
                            dY = tempY - person.getyPos();
                        }
                        if (dX != 0 || dY != 0) {
                            Attack flashingStrike = new Attack(person.getxPos(), person.getyPos(), 0, "flashingStrike", person);
                            int w = 0;
                            int h = 0;
                            if (dX != 0) {
                                w = dX;
                            } else {
                                w = 25;
                            }
                            if (dY != 0) {
                                h = dY;
                            } else {
                                h = 25;
                            }
                            if (dX != 0 && dY != 0) {
                                flashingStrike.setDegrees(dX, dY);
                            }
                            flashingStrike.setDimensions(w, h);
                            attacks.add(flashingStrike);
                        }
                    }
//                if(mapX!=theGameScreen.getMapNumX() || mapY!=theGameScreen.getMapNumY()){
//                    break;
//                }
                    if (person == player) {
                        theGameScreen.scroll();
                    }
                    wait(25);
                }
            } else if (skill.equals(skillList[15])) {
                person.turnInvincible();
                Attack blade = new Attack(person.getxPos(), person.getyPos(), 0, "bBOTH", person);
                attacks.add(blade);
                waitUntilAttackDone(blade);
                person.notInvincible();
                person.setMode(1);
            }
            else if(skill.equals(skillList[16])){
                for(int i = 25; i < 775; i+=75){
                    attacks.add(new Attack(i + person.getxPos()/2-61,i-61,0,"magma",person));
                    attacks.add(new Attack(775-i + person.getxPos()/2-61,775-i-61,0,"magma",person));
                    attacks.add(new Attack(i + person.getxPos()/2-61,775-i-61,0,"magma",person));
                    attacks.add(new Attack(775-i + person.getxPos()/2-61,i-61,0,"magma",person));
                    wait(50);
                }
            }
        }
        person.addToCombo(skill);
        person.letGo(skillNum);
        person.setAttackMove(0);
    }
    public void executeSkill(int num){
        if(player.inSkill(num)){// && player.attackAble()){
            player.cantMove();
            player.useEnergy(player.getEnergyCost(num));
            player.onCooldown(num);
            skill(num,player);
            player.canMove();
            player.skillDone(num);
            tickingWait(num,player.cooldown(num),player);
            player.cooldowned(num);
        }
    }
    public void executeSkill(int num, Person person){
        if(person!=null) {
            person.cantMove();
            person.useEnergy(person.getEnergyCost(num));
            person.onCooldown(num);
            skill(num, person);
            person.canMove();
            person.skillDone(num);
            tickingWait(num, person.cooldown(num), person);
            person.cooldowned(num);
        }
    }
    private void tickingWait(int skillNum,int sec,Person person){
        person.setCurrentCooldown(skillNum,sec);
        for(int i = 0; i < sec*60; i++){
            if(person.getRealCurrentCooldown(skillNum)<=0)break;
            else {
                person.setCurrentCooldown(skillNum, (float)sec - i/60F);
                wait(1000/60);
            }
        }
        person.setCurrentCooldown(skillNum,0);
    }

    public void equipSkill(int skillNum, int num){
        if(player.skillsContain(skillNum)){
            boolean equipped = false;
            for(int playerSkillNum = 0; playerSkillNum < 4; playerSkillNum++){
                if(player.getSkill(playerSkillNum).equals(skillList[skillNum])){
                    equipped = true;
                }
            }
            if(specialMove[skillNum]) {
                boolean special = false;
                for (int i = 0; i < skillList.length; i++) {
                    for(int playerSkillNum = 0; playerSkillNum < 4; playerSkillNum++){
                        if(player.getSkill(playerSkillNum).equals(skillList[i])){
                            if (specialMove[i]){
                                special = true;
                            }
                        }
                    }
                    if (player.getSkill(num).equals(skillList[i])) {
                        if (specialMove[i]){
                            special = false;
                        }
                    }
                }
                if(special){
                    equipped = true;
                }
            }
            if(!equipped){
                player.equipSkill(skillNum,num,skillList);
                player.assignCooldowns(skillList,skillCooldowns,energyCosts);
            }
        }
    }
    public void equipPassive(int skillNum, int num){
        if(player.passivesContain(skillNum)){
            boolean equipped = false;
            for(int playerSkillNum = 0; playerSkillNum < 2; playerSkillNum++){
                if(player.getPassive(playerSkillNum).equals(passiveList[skillNum])){
                    equipped = true;
                }
            }
            if(!equipped){
                player.equipPassive(skillNum,num,passiveList);
            }
        }
    }
    public void unequipPassives(){
        player.equipPassive(0,0,new String[]{""});
        player.equipPassive(0,1,new String[]{""});
    }

    public int indexOfSkill(int skillNum){
        for(int i = 0; i < skillList.length; i++) {
            if (player.getSkill(skillNum).equals(skillList[i])) return i;
        }
        return -1;
    }
    public int indexOfPassive(int passiveNum){
        for(int i = 0; i < passiveList.length; i++) {
            if (player.getPassive(passiveNum).equals(passiveList[i])) return i;
        }
        return -1;
    }
    public void playerDied(){
        forceExit();
        interactWith(25);
        enemyList.clear();
        allyList.clear();
        if(storyLocation> farthest){
            farthest = storyLocation;
        }
        if(storyLocation==0){
            npcList.get(0).setChatIndex(0);
            enemyList.add(new Person("meleeEnemy",1425,400,0,150,0,0,0,0,1));
            enemyList.get(0).setSpecialRole("advanceStorydarkScreen");
            interactWith(0);
            inStory = true;
        }
        if(storyLocation==5){
            theGameScreen.unbarricadeMap(8,1);
            storyLocation--;
            npcList.get(0).setChatIndex(40);
        }
        if(storyLocation==10){
            storyLocation--;
            npcList.get(0).setChatIndex(64);
            npcList.get(13).moveMap(2,5);
            npcList.get(13).goToXY(300,275);
        }
        if(storyLocation==27){
            storyLocation-=2;
            npcList.get(0).setChatIndex(307);
            npcList.get(24).goToXY(50,200);
        }
        if(storyLocation==30){
            theGameScreen.unbarricadeMap(64,35);
            storyLocation=28;
            npcList.get(0).setChatIndex(323);
            npcList.get(26).moveMap(64,35);
            npcList.get(26).goToXY(500,300);
        }
        if(storyLocation==37 || storyLocation==38){
            storyLocation = 36;
            npcList.get(0).setChatIndex(364);
        }
    }
    public boolean checkSkip(){
        if(farthest ==0 && storyLocation==0){
            return true;
        }
        if (farthest == 5 && storyLocation == 4) {
            return true;
        }
        if (farthest == 10 && (storyLocation==9 || storyLocation==10)) {
            return true;
        }
        if (farthest == 27 && (storyLocation==26||storyLocation==27)) {
            return true;
        }
        return false;
    }
    public void skipChat(){
        if(inStory) {
            if(farthest ==0 && storyLocation==0){
                //player.canMove();
                inStory = false;
                theGameScreen.barricadeMap(0,1);
                enemyList.get(0).moveX(npcList.get(12).getxPos()+25 - enemyList.get(0).getxPos());
                player.goToXY(npcList.get(12).getxPos()-25,npcList.get(12).getyPos());
                npcList.get(0).setChatIndex(16);
                forceExit();
            }
            if (farthest == 5 && storyLocation == 4) {
                theGameScreen.barricadeMap(8, 1);
                storyLocation++;
                npcList.get(0).setChatIndex(45);
                //player.canMove();
                inStory = false;
                npcList.get(2).moveMap(3,1);
                npcList.get(3).moveMap(3,1);
                allyList.add(new Person("meleeAlly", 25,350,1000,1000,0,player.getPhysicalStat()*2,0,8,1));
                allyList.get(0).addSkills(skillList[6],skillCooldowns[6]);
                allyList.get(0).setDesc("Kuria","");
                allyList.add(new Person("rangedAlly", 25,450,1000,1000,0,player.getPhysicalStat()/2,player.getMagicStat()*2,8,1));
                allyList.get(1).addSkills(skillList[2],skillCooldowns[2]);
                allyList.get(1).setDesc("Lloyd","");
                forceExit();
            }
            if (farthest == 10 && (storyLocation==9 || storyLocation==10)) {
                storyLocation = 10;
                npcList.get(0).setChatIndex(91);
                player.goToPerson(npcList.get(13),25,0);
                npcList.get(2).goToPerson(npcList.get(13),25,-25);
                npcList.get(3).goToPerson(npcList.get(13),25,25);
                if(bosses[0]==null){
                    bosses[0] = new Person("meleeBoss1", 300, 275, player.getLevel()*16, 1000, 0, player.getLevel()*2, player.getLevel(),2,5);
                    bosses[0].setDesc("Square","Sympathizer of Red Squares");
                }

                Person boss = bosses[0].clone();
                enemyList.add(boss);
                boss.setSpecialRole("advanceStory");
                npcList.get(13).moveMap(-100,0);
//                allyList.add(new Person("meleeAlly", npcList.get(2).getxPos(),npcList.get(2).getyPos(),player.getLevel()*2,1000,0,player.getLevel()+player.getPhysicalStat(),0,2,5));
//                allyList.get(0).addSkills(skillList[6],skillCooldowns[6]);
//                allyList.get(0).setDesc("Kuria","");
//                allyList.add(new Person("rangedAlly", npcList.get(3).getxPos(),npcList.get(3).getyPos(),player.getLevel(),1000,0,player.getPhysicalStat()/2,player.getLevel()+player.getMagicStat(),2,5));
//                allyList.get(1).addSkills(skillList[2],skillCooldowns[2]);
//                allyList.get(1).setDesc("Lloyd","");
//                npcList.get(2).moveMap(-100,1);
//                npcList.get(3).moveMap(-100,1);
                swapAllies(2,5);
                //player.canMove();
                inStory = false;
                theGameScreen.barricadeMap(2,5);
                forceExit();
            }
            if (farthest == 27 && (storyLocation==26||storyLocation==27)) {
                storyLocation = 27;
                npcList.get(0).setChatIndex(317);
                int tempY = theGameScreen.getScrollY();
                theGameScreen.setScrollXY(theGameScreen.getScrollX(),-27125,false);
                player.goToXY(player.getxPos(),player.getyPos()+theGameScreen.getScrollY()-tempY);
                player.moveX(25);
                inStory = false;
                //player.canMove();
                theGameScreen.playerBarricadeMap(60,35);
                forceExit();
            }
//            if (storyLocation == 30) {
//                theGameScreen.unbarricadeMap(64, 35);
//                storyLocation = 28;
//                npcList.get(0).setChatIndex(323);
//            }
        }
    }
    private void storyStuff(){
        if(inStory){
            //player.cantMove();
            if(!theGameScreen.isDarkScreen()) {
                if (storyLocation == -1) {

                }
                if (storyLocation == 0) {
                    if (theGameScreen.checkInChat()) {
                        if (npcList.get(0).getLineNum() >= 3 && enemyList.get(0).getxPos() > 1075) {
                            enemyList.get(0).moveX(-25);
                        }
                    } else {
                        if (player.getxPos() < npcList.get(12).getxPos() - 25) {
                            player.moveX(10);
                        } else if (npcList.get(0).getChatIndex() > 15) {
                            //player.canMove();
                            inStory = false;
                            theGameScreen.barricadeMap(0, 1);
                        } else {
                            interactWith(0);
                        }
                    }
                } else if (storyLocation == 1) {
                    if (npcList.get(0).getChatIndex() > 22) {
                        //player.canMove();
                        inStory = false;
                        npcList.get(12).switchIndexes(0);
                        theGameScreen.unbarricadeMap(0, 1);
                        earnMoney(10);
                    } else if (!theGameScreen.checkInChat()) {
                        interactWith(0);
                        player.goToPerson(npcList.get(12),-25,0);
                    }
                } else if (storyLocation == 2) {
                    interactWith(0);
                    storyLocation++;
                } else if (storyLocation == 3) {
                    if (npcList.get(0).getChatIndex() > 31) {
                        storyLocation++;
                    } else if (!theGameScreen.checkInChat()) {
                        if (player.getxPos() + 24 < npcList.get(5).getxPos()) {
                            player.moveX(5);
                        } else if (player.getyPos() > npcList.get(5).getyPos()) {
                            movePerson(0,-5,player);
                        } else {
                            interactWith(0);
                        }
                    }
                } else if (storyLocation == 4) {
                    if (npcList.get(0).getChatIndex() > 39) {
                        //player.canMove();
                        inStory = false;
                    } else if (!theGameScreen.checkInChat()) {
                        if (player.getxPos() + 25 < npcList.get(2).getxPos()) {
                            player.moveX(5);
                        } else {
                            interactWith(0);
                        }
                    }
                } else if (storyLocation == 5) {
                    if (npcList.get(0).getChatIndex() > 44) {
                        //player.canMove();
                        inStory = false;
                        theGameScreen.barricadeMap(8, 1);
                        swapAllies(8,1);
                    } else if (!theGameScreen.checkInChat()) {
                        player.moveX(25);
                        lineUp();
                        theGameScreen.respawnEnemy(1,8,8,1);
                        interactWith(0);
                    }
                } else if (storyLocation == 6) {
                    if (npcList.get(0).getChatIndex() > 46) {
                        //player.canMove();
                        inStory = false;
                    } else if (!theGameScreen.checkInChat()) {
                        interactWith(0);
                        npcGoTo(2, allyList.get(0).getxPos(), allyList.get(0).getyPos());
                        npcGoTo(3, allyList.get(1).getxPos(), allyList.get(1).getyPos());
                        allyList.clear();
                        npcList.get(2).switchIndexes(0);
                        npcList.get(3).switchIndexes(0);
                        theGameScreen.unbarricadeMap(8, 1);
                    }
                } else if (storyLocation == 7) {
                    if (npcList.get(0).getChatIndex() > 54) {
                        if (player.getyPos() + 25 < npcList.get(11).getyPos()) {
                            player.moveY(5);
                            npcGoTo(2, player.getxPos() + 25, player.getyPos());
                            npcGoTo(3, player.getxPos() - 25, player.getyPos());
                        } else {
                            if (npcList.get(0).getChatIndex() < 57 && !theGameScreen.checkInChat()) {
                                interactWith(0);
                            } else {
                                //player.canMove();
                                inStory = false;
                                storyLocation++;
                                npcList.get(2).switchIndexes(1);
                                npcList.get(3).switchIndexes(1);
                            }
                        }
                    } else if (!theGameScreen.checkInChat()) {
                        npcGoTo(2, 700, 25);
                        npcGoTo(3, 750, 25);
                        interactWith(0);
                    }
                } else if (storyLocation == 8) {
                    if (npcList.get(0).getChatIndex() > 63) {
                        //player.canMove();
                        inStory = false;
                        storyLocation++;
                    } else if (!theGameScreen.checkInChat()) {
                        interactWith(0);
                    }
                } else if (storyLocation == 9) {
                    if (npcList.get(0).getChatIndex() > 67) {
                        if (player.moveCloserTo(npcList.get(13), 0, 25)) {
                            npcList.get(2).moveTowards(npcList.get(13), 25, 25);
                            npcList.get(3).moveTowards(npcList.get(13), 25, 0);
//                        if(npcList.get(2).getxPos()>npcList.get(13).getxPos()-25){
//                            npcList.get(2).moveX(-5);
//                        }
//                        if(npcList.get(3).getxPos()>npcList.get(13).getxPos()+25){
//                            npcList.get(3).moveX(-5);
//                        }
                        } else if (npcList.get(2).moveCloserTo(npcList.get(13), 25, 25)) {
                            npcList.get(3).moveTowards(npcList.get(13), 25, 0);
                        } else if (npcList.get(3).moveCloserTo(npcList.get(13), 25, 0)) {

                        }
//                    else if(player.getyPos()>npcList.get(13).getyPos()+25){
//                        player.moveY(-5);
//                        if(npcList.get(2).getxPos()>npcList.get(13).getxPos()-25){
//                            npcList.get(2).moveX(-5);
//                        }
//                        if (npcList.get(2).getyPos()>npcList.get(13).getyPos()+25) {
//                            npcList.get(2).moveY(-5);
//                        }
//                        if(npcList.get(3).getyPos()>npcList.get(13).getyPos()+25){
//                            npcList.get(3).moveY(-5);
//                        }
//                    }
//                    else if (npcList.get(2).getyPos()>npcList.get(13).getyPos()+25) {
//                        npcList.get(2).moveY(-5);
//                        if(npcList.get(3).getyPos()>npcList.get(13).getyPos()+25){
//                            npcList.get(3).moveY(-5);
//                        }
//                    }
//                    else if(npcList.get(3).getyPos()>npcList.get(13).getyPos()+25){
//                        npcList.get(3).moveY(-5);
//                    }
                        else {
                            storyLocation++;
                        }
                    } else if (!theGameScreen.checkInChat()) {
                        npcList.get(2).moveMap(2, 5);
                        npcList.get(3).moveMap(2, 5);
                        npcGoTo(2, player.getxPos(), player.getyPos() - 25);
                        npcGoTo(3, player.getxPos(), player.getyPos() + 25);
                        interactWith(0);
                    }
                } else if (storyLocation == 10) {
                    if (npcList.get(0).getChatIndex() > 90) {
                        if (bosses[0] == null) {
                            bosses[0] = new Person("meleeBoss1", 300, 275, player.getLevel() * 24, 1000, 0, player.getLevel() * 2, player.getLevel(), 2, 5);
                            bosses[0].setDesc("Square", "Sympathizer of Red Squares");
                        }

                        Person boss = bosses[0].clone();
                        enemyList.add(boss);
                        boss.setSpecialRole("advanceStory");
                        npcList.get(13).moveMap(-100, 0);
//                        allyList.add(new Person("meleeAlly", npcList.get(2).getxPos(), npcList.get(2).getyPos(), player.getLevel() * 2, 1000, 0, player.getLevel() + player.getPhysicalStat(), 0, 2, 5));
//                        allyList.get(0).addSkills(skillList[6], skillCooldowns[6]);
//                        allyList.get(0).setDesc("Kuria", "");
//                        allyList.add(new Person("rangedAlly", npcList.get(3).getxPos(), npcList.get(3).getyPos(), player.getLevel(), 1000, 0, player.getPhysicalStat() / 2, player.getLevel() + player.getMagicStat(), 2, 5));
//                        allyList.get(1).addSkills(skillList[2], skillCooldowns[2]);
//                        allyList.get(1).setDesc("Lloyd", "");
//                        npcList.get(2).moveMap(-100, 1);
//                        npcList.get(3).moveMap(-100, 1);
                        //player.canMove();
                        swapAllies(2,5);
                        inStory = false;
                        theGameScreen.barricadeMap(2, 5);
                    } else if (!theGameScreen.checkInChat()) {
                        interactWith(0);
                    }
                } else if (storyLocation == 11) {
                    if (npcList.get(0).getChatIndex() > 97) {
                        //player.canMove();
                        inStory = false;
                        theGameScreen.unbarricadeMap(2, 5);
                    } else if (!theGameScreen.checkInChat()) {
                        interactWith(0);
                        swapNpcs(2,5);
                    }
                } else if (storyLocation == 12) {
                    if (npcList.get(0).getChatIndex() > 103) {
                        //player.canMove();
                        inStory = false;
                    } else if (!theGameScreen.checkInChat()) {
                        interactWith(0);
                    }
                } else if (storyLocation == 13) {
                    npcList.get(2).goToXY(50, 375);
                    npcList.get(2).moveMap(50, 35);
                    npcList.get(3).goToXY(50, 400);
                    npcList.get(3).moveMap(50, 35);
                    if (npcList.get(0).getChatIndex() > 111) {
                        storyLocation++;
                    } else if (!theGameScreen.checkInChat()) {
                        interactWith(0);
                    }
                } else if (storyLocation == 14) {
                    if (npcList.get(2).getxPos() < 1500) {
                        npcList.get(2).moveX(10);
                        npcList.get(3).moveX(10);
                    }
                    if (npcList.get(2).getyPos() > 300) {
                        npcList.get(2).moveY(-5);
                        npcList.get(3).moveY(-5);
                    }
                    if (npcList.get(0).getChatIndex() > 113) {
                        storyLocation++;
                    } else if (!theGameScreen.checkInChat()) {
                        interactWith(0);
                    }
                } else if (storyLocation == 15) {
                    if (npcList.get(2).getxPos() < 1500) {
                        npcList.get(2).moveX(10);
                        npcList.get(3).moveX(10);
                    }
                    if (npcList.get(2).getyPos() > 300) {
                        npcList.get(2).moveY(-5);
                        npcList.get(3).moveY(-5);
                    }
                    if (npcList.get(14).getxPos() > player.getxPos()) {
                        npcList.get(14).moveX(-5);
                    } else if (npcList.get(14).getyPos() < player.getyPos() - 25) {
                        npcList.get(14).moveY(5);
                    } else if (npcList.get(0).getChatIndex() > 130) {
                        storyLocation++;
                    } else if (!theGameScreen.checkInChat()) {
                        interactWith(0);
                    }
                } else if (storyLocation == 16) {
                    if (npcList.get(14).getyPos() > -25) {
                        npcList.get(14).moveY(-5);
                    } else {
                        if (npcList.get(0).getChatIndex() > 133) {
                            inStory = false;
                            //player.canMove();
                            npcList.get(2).moveMap(51, 35);
                            npcList.get(3).moveMap(51, 35);
                            npcList.get(2).goToXY(400, 650);
                            npcList.get(3).goToXY(425, 650);
                        } else if (!theGameScreen.checkInChat()) {
                            interactWith(0);
                            npcList.get(14).moveMap(-100,-100);
                        }
                    }
                } else if (storyLocation == 17) {
//                    boolean b1 = npcList.get(2).getxPos() > player.getxPos() + 25;
//                    boolean b2 = npcList.get(3).getxPos() > player.getxPos() + 25;
//                    boolean b3 = npcList.get(2).getyPos() > player.getyPos() + 25;
//                    boolean b4 = npcList.get(3).getyPos() > player.getyPos();
//                    boolean b5 = npcList.get(2).getyPos() < player.getyPos() + 25;
//                    boolean b6 = npcList.get(3).getyPos() < player.getyPos();
//                    boolean b7 = !(npcList.get(2).getxPos() > player.getxPos() + 25) && npcList.get(2).getxPos() < player.getxPos() - 25;
//                    boolean b8 = !(npcList.get(3).getxPos() > player.getxPos() + 25) && npcList.get(3).getxPos() < player.getxPos() - 25;
//                    if (b1) {
//                        npcList.get(2).moveX(-5);
//                    }
//                    if (b2) {
//                        npcList.get(3).moveX(-5);
//                    }
//                    if (b3) {
//                        npcList.get(2).moveY(-5);
//                    }
//                    if (b4) {
//                        npcList.get(3).moveY(-5);
//                    }
//                    if (b5) {
//                        npcList.get(2).moveY(5);
//                    }
//                    if (b6) {
//                        npcList.get(3).moveY(5);
//                    }
//                    if (b7) {
//                        npcList.get(2).moveX(5);
//                    }
//                    if (b8) {
//                        npcList.get(3).moveX(5);
//                    }
//                    if (!(b1 || b2 || b3 || b4 || b5 || b6 || b7 || b8)) {
                    if(!npcList.get(2).moveCloserTo(player,25,25) && !npcList.get(3).moveCloserTo(player,25,0)){
                        if (npcList.get(0).getChatIndex() > 183) {
                            inStory = false;
                            //player.canMove();
                            npcList.get(2).switchIndexes(2);
                            npcList.get(3).switchIndexes(2);
                        } else if (!theGameScreen.checkInChat()) {
                            interactWith(0);
                        }
                    }
                } else if (storyLocation == 18) {
                    npcList.get(11).moveMap(55, 35);
                    npcList.get(11).goToXY(75, 350);
                    npcList.get(2).moveMap(55, 35);
                    npcList.get(2).goToXY(0, 400);
                    npcList.get(3).moveMap(55, 35);
                    npcList.get(3).goToXY(0, 425);
                    if (npcList.get(0).getChatIndex() > 187) {
                        storyLocation++;
                    } else if (!theGameScreen.checkInChat()) {
                        interactWith(0);
                    }
                } else if (storyLocation == 19) {
                    if (npcList.get(11).getxPos() > player.getxPos() + 25) {
                        npcList.get(11).moveX(-5);
                    } else if (npcList.get(11).getyPos() < player.getyPos()) {
                        npcList.get(11).moveY(5);
                    } else if (npcList.get(0).getChatIndex() > 197) {
                        if(theGameScreen.inDarkScreen()){
                            npcList.get(2).moveMap(-100, 33);
                            npcList.get(2).goToXY(-100, 400);
                            npcList.get(3).moveMap(-100, 33);
                            npcList.get(3).goToXY(-100, 475);
                            npcList.get(11).moveMap(-100, 33);
                            npcList.get(11).goToXY(-100, 475);
                            inStory = false;
                        }
                        else {
                            theGameScreen.darkScreen();
                        }
                        //player.canMove();
                    } else if (!theGameScreen.checkInChat()) {
                        interactWith(0);
                    }
                } else if (storyLocation == 20) {
                    if (npcList.get(0).getChatIndex() > 200) {
                        storyLocation++;
                    } else if (!theGameScreen.checkInChat()) {
                        if (player.xPosRelativeToMapX(55) > 700) {
                            npcGoTo(11, player.getxPos() - 25, player.getyPos());
                        } else {
                            npcGoTo(11, player.getxPos() + 25, player.getyPos());
                        }
                        interactWith(0);
                    }
                } else if (storyLocation == 21) {
                    if (player.getyPos() < npcList.get(19).getyPos() + 25) {
                        movePerson(0, 5, player);
                        movePerson(0, 5, npcList.get(11));
                        movePerson(-5, 0, player);
                        movePerson(-5, 0, npcList.get(11));
                    } else if (player.getxPos() < npcList.get(19).getxPos() - 5) {
                        int tempX = player.getxPos();
                        movePerson(5, 0, player);
                        movePerson(5, 0, npcList.get(11));
                        if(player.getxPos() == tempX){
                            movePerson(0, -5, player);
                            movePerson(0, -5, npcList.get(11));
                        }
                    } else if (player.getxPos() > npcList.get(19).getxPos() + 5) {
                        movePerson(-5, 0, player);
                        movePerson(-5, 0, npcList.get(11));
                    } else if (player.getyPos() > npcList.get(19).getyPos() + 30) {
                        movePerson(0, -5, player);
                        movePerson(0, -5, npcList.get(11));
                    } else if (npcList.get(0).getChatIndex() > 229) {
                        Attack anAttack = new Attack(npcList.get(19).getxPos() - 23, npcList.get(19).getyPos() - 23, 0, "berserk", player);
                        attacks.add(anAttack);
                        npcList.get(19).goBerserk();

                        storyLocation++;
                    } else if (!theGameScreen.checkInChat()) {
                        interactWith(0);
                    }
                } else if (storyLocation == 22) {
                    if (npcList.get(0).getChatIndex() > 235) {
                        Person person = new Person("meleeBoss1", 300, 275, player.getLevel() * 24, 1000, 0, player.getLevel() * 1000, player.getLevel(), 55, 33);
                        person.setDirection("down");
                        Attack anAttack = new Attack(npcList.get(19).getxPos(), npcList.get(19).getyPos(), 25, "slash", person);
                        anAttack.multiplySize(4);
                        attacks.add(anAttack);
                        storyLocation++;
                    } else if (!theGameScreen.checkInChat()) {
                        interactWith(0);
                    }
                } else if (storyLocation == 23) {
                    if (npcList.get(0).getChatIndex() > 245) {
                        storyLocation++;
                        goToMapXY(-100, -100);
                        theGameScreen.mapChange();
                        effects.add(new Attack("static", false, player));
                        player.goToXY(500, 400);
                        npcList.get(14).moveMap(-100, -100);
                        npcList.get(14).goToXY(1225, 400);
                        npcList.get(14).switchName(1);
                        inStory = false;
                        interactWith(0);
                    } else if (!theGameScreen.checkInChat()) {
                        interactWith(0);
                    }
                } else if (storyLocation == 24) {
                    if(player.moveCloserTo(npcList.get(14),25,25)){

                    }
                    else if (npcList.get(0).getChatIndex() > 281) {
                        storyLocation++;
                        goToMapXY(55, 33);
                        theGameScreen.mapChange();
                        effects.add(new Attack("static", false, player));
                        player.goToXY(npcList.get(19).getxPos(), npcList.get(19).getyPos() + 25);
                        npcGoTo(3, npcList.get(19).getxPos() - 300, npcList.get(19).getyPos() + 25);
                        npcGoTo(2, npcList.get(19).getxPos() - 300, npcList.get(19).getyPos() + 50);
                        npcList.get(11).moveMap(-100, -100);
                        npcList.get(19).moveMap(-100, -100);
                    } else if (!theGameScreen.checkInChat()) {
                        interactWith(0);
                    }
                } else if (storyLocation == 25) {
                    if (npcList.get(2).getxPos() + 25 < player.getxPos()) {
                        npcList.get(2).moveX(5);
                        npcList.get(3).moveX(5);
                    } else if (npcList.get(0).getChatIndex() > 308) {
                        //player.canMove();
                        inStory = false;
                        npcList.get(2).switchIndexes(3);
                        npcList.get(3).switchIndexes(3);
                    } else if (!theGameScreen.checkInChat()) {
                        interactWith(0);
                    }
                } else if (storyLocation == 26) {
                    if (npcList.get(0).getChatIndex() > 315) {
                        storyLocation++;
                    } else if (!theGameScreen.checkInChat()) {
                        interactWith(0);
                        npcGoTo(2, player.getxPos(), player.getyPos() - 24);
                        int tempX = npcList.get(2).getxPos();
                        movePerson(-1, 0, npcList.get(2));
                        if (tempX != npcList.get(2).getxPos()) {
                            npcGoTo(3, player.getxPos(), player.getyPos() + 24);
                            tempX = npcList.get(3).getxPos();
                            movePerson(-1, 0, npcList.get(3));
                            if (tempX == npcList.get(3).getxPos()) {
                                npcGoTo(3, player.getxPos(), player.getyPos() - 50);
                            }
                        } else {
                            npcGoTo(2, player.getxPos(), player.getyPos() + 25);
                            npcGoTo(3, player.getxPos(), player.getyPos() + 50);
                        }
                        swapAllies(60, 35);
                        allyList.get(0).losePercentHp(1);
                        allyList.get(1).losePercentHp(1);
                        for (int i = 0; i < 14; i++) {
                            Person ally = new Person("meleeAlly", 25, randomNum(749, 25), player.getLevel(), 1400, 0, player.getLevel(), 0, 60, 35);
                            //ally.setDesc("Soldier","");
                            allyList.add(ally);
                        }
                    }
                } else if (storyLocation == 27) {
                    if (npcList.get(24).getyPos() < player.getyPos()) {
                        npcList.get(24).moveY(5);
                    } else if (npcList.get(0).getChatIndex() > 318) {
                        int tempY = theGameScreen.getScrollY();
                        theGameScreen.setScrollXY(theGameScreen.getScrollX(), -27125, false);
                        player.goToXY(player.getxPos(), player.getyPos() + theGameScreen.getScrollY() - tempY);
                        player.moveX(25);
                        inStory = false;
                        //player.canMove();
                        theGameScreen.playerBarricadeMap(60,35);
                    } else if (!theGameScreen.checkInChat()) {
                        interactWith(0);
                    }
                } else if (storyLocation == 28) {
                    if (npcList.get(0).getChatIndex() > 322) {
                        if (npcList.get(24).getxPos() < 1500) {
                            boolean a = false;
                            for (Person p : allyList) {
                                if (p.getDisplayName() == null) {
                                    p.moveX(5);
                                    if (p.getxPos() < 1500) {
                                        a = true;
                                    }
                                }
                            }
                            if (!a || npcList.get(24).getxPos() < 1490) {
                                npcList.get(24).moveX(5);
                            }
                        } else {
                            if (npcList.get(0).getChatIndex() > 324) {
                                theGameScreen.unbarricadeMap(60,34);
                                theGameScreen.unbarricadeMap(60,35);
                                theGameScreen.unbarricadeMap(60,35);
                                for (int i = allyList.size() - 1; i > -1; i--) {
                                    if (allyList.get(i).getDisplayName() == null) {
                                        allyList.remove(i);
                                    }
                                }
                                npcList.get(24).moveMap(64, 35);
                                inStory = false;
                                //player.canMove();
                            } else if (!theGameScreen.checkInChat()) {
                                interactWith(0);
                            }
                        }
                    } else if (!theGameScreen.checkInChat()) {
                        interactWith(0);
                        swapNpcs(60, 35);
                    }
                } else if (storyLocation == 29) {
                    if (npcList.get(0).getChatIndex() > 326) {
                        storyLocation++;
                    } else if (!theGameScreen.checkInChat()) {
                        interactWith(0);
                        lineUp();
                        player.moveX(25);
                        npcList.get(2).moveX(25);
                        npcList.get(3).moveX(25);
                        swapAllies(64, 35);
                        for (int i = 0; i < 15; i++) {
                            Person ally = new Person("meleeAlly", 25, randomNum(749, 25), player.getLevel(), 1400, 0, player.getLevel(), 0, 64, 35);
                            allyList.add(ally);
                        }
                        allyList.get(allyList.size() - 1).setDesc("Soldier", "");
                    }
                } else if (storyLocation == 30) {
                    if (npcList.get(26).moveCloserTo(player, 25, 0)) {

                    } else if (npcList.get(0).getChatIndex() > 329) {
                        if (bosses[1] == null) {
                            bosses[1] = new Person("meleeEnemyBoss2", 300, 275, player.getLevel() * 48, 1000, 0, player.getLevel() * 4, player.getLevel() * 2, 64, 35);
                            bosses[1].setDesc("Red Square", "The Dreamer");
                        }

                        Person boss = bosses[1].clone();
                        boss.goToPerson(npcList.get(26), 0, 0);
                        npcList.get(26).moveMap(-1000, -1000);
                        enemyList.add(boss);
                        boss.setSpecialRole("advanceStoryswap26");
                        //player.canMove();
                        inStory = false;
                        theGameScreen.barricadeMap(64, 35);
                    } else if (!theGameScreen.checkInChat()) {
                        interactWith(0);
                    }
                } else if (storyLocation == 31) {
                    if (npcList.get(0).getChatIndex() > 332) {
                        Person boss = bosses[1].clone();
                        boss.goToPerson(npcList.get(26), 0, 0);
                        npcList.get(26).moveMap(npcList.get(26).getMapX() - 1000, -1000);
                        boss.goBerserk();
                        boss.updateFighter(player.getLevel() * 48, player.getLevel() * 4, player.getLevel() * 2);
                        boss.setSpecialRole("unkillable");
                        enemyList.add(boss);
                        lineUp();
                        storyLocation++;
                    } else if (!theGameScreen.checkInChat()) {
                        interactWith(0);
                    }
                } else if (storyLocation == 32) {
                    if (npcList.get(0).getChatIndex() > 339) {
                        swapAllies(64, 35);
                        //player.canMove();
                        inStory = false;
                    } else if (!theGameScreen.checkInChat()) {
                        interactWith(0);
                    }
                } else if (storyLocation == 33) {
                    if (npcList.get(0).getChatIndex() > 349) {
                        storyLocation++;
                        swapNpcs(64, 35);
                        theGameScreen.unbarricadeMap(64, 35);
                    } else if (!theGameScreen.checkInChat()) {
                        interactWith(0);
                    }
                } else if (storyLocation == 34) {
                    if(npcList.get(3).getxPos()<-1400 || npcList.get(3).getxPos()>2800) {
                        npcList.get(3).goToPerson(player,25,0);
                    }
                    if (theGameScreen.getMapNumX() > 61) {
                        movePerson(-5, 0, player);
                        int sY = theGameScreen.getScrollY();
                        if (sY < -27225) {
                            movePerson(0, -5, player);
                        } else if(sY > -27125){
                            movePerson(0, 5, player);
                        }
                        for (Person p : allyList) {
                            p.moveTowards(player, 25, 25);
                        }
                        npcList.get(3).moveTowards(player, 25, 0);
                    } else {
                        storyLocation++;
                        theGameScreen.darkScreen();
                    }
                }
                else if(storyLocation==35) {
                    if (npcList.get(0).getChatIndex() > 352) {
                        if (theGameScreen.getMapNumX() < 64) {
                            movePerson(5, 0, player);
                            int sY = theGameScreen.getScrollY();
                            if (sY < -27225) {
                                movePerson(0, -5, player);
                            } else if(sY > -27125){
                                movePerson(0, 5, player);
                            }
                            for (Person p : allyList) {
                                p.moveTowards(player, 25, 25);
                            }
                            npcList.get(3).moveTowards(player, 25, 0);
                            npcList.get(24).moveTowards(player,25,25);
                        } else {
                            if(npcList.get(0).getChatIndex() > 365){
                                storyLocation++;
                                theGameScreen.darkScreen();
                                player.test();
                            }
                            else if(!theGameScreen.checkInChat()){
                                interactWith(0);
                            }
                        }
                    }
                    else if(!theGameScreen.checkInChat()){
                        npcList.get(24).moveMap(60, 35);
                        npcList.get(24).goToPerson(player, -30, 0);
                        for(int i = 0; i < 14; i++){
                            allyList.add(new Person("meleeAlly", player.getxPos()-800, randomNum(749, 25), player.getLevel(), 1400, 0, player.getLevel(), 0, 62, 35));
                        }
                        npcList.get(2).goToXY(-100,-100);
                        npcList.get(2).moveMap(-1000,-1000);
                        enemyList.clear();
                        interactWith(0);
                    }
                }
                else if(storyLocation==36){
                    inStory = false;
                    allyList.clear();
                    player.test();
                    npcList.get(3).goToPerson(player,25,0);
                }
                else if(storyLocation==37){
                    if(npcList.get(0).getChatIndex() > 367){
                        if(npcList.get(27).moveCloserTo(player,25,0)){

                        }
                        else if(npcList.get(0).getChatIndex() > 371){
                            inStory = false;
                            swapPerson(npcList.get(27),player.getLevel()*6,1400,player.getLevel(),player.getLevel(),false);
                            enemyList.get(0).setSpecialRole("advanceStory");
                            theGameScreen.playerBarricadeMap(67,35);
                            player.moveX(25);
                            npcList.get(3).moveX(25);
                            int tempY = player.getyPos();
                            theGameScreen.movePerson(25,-25,player);
                            theGameScreen.movePerson(0,25,player);
                            npcList.get(3).moveY(player.getyPos()-tempY);
                            swapAllies(67,35);
                        }
                        else if(!theGameScreen.checkInChat()){
                            interactWith(0);
                        }
                    }
                    else if(!theGameScreen.checkInChat()){
                        interactWith(0);
                        lineUp(3);
                    }
                }
                else if(storyLocation==38){
                    if(npcList.get(0).getChatIndex() > 373) {
                        if(npcList.get(27).getxPos()<1450 && enemyList.isEmpty()) {
                            npcList.get(27).moveX(5);
                        }
                        else if(enemyList.isEmpty()) {
                            for (int i = 0; i < 11; i++) {
                                Person enemy = new Person("meleeEnemy", 1400, 0, player.getLevel() * 6, 1400, 0, player.getLevel(), player.getLevel(), 67, 35);
                                enemy.setDesc("Red Soldier", "");
                                enemyList.add(enemy);
                            }
                        }
                        else if(npcList.get(27).getxPos()>1445){
                            int y = 40;
                            for(int i = 0; i < 11; i++){
                                enemyList.get(i).goToPerson(npcList.get(27),0,0);
                                enemyList.get(i).goToXY(enemyList.get(i).getxPos(),y);
                                y+=60;
                            }
                            npcList.get(27).moveX(-5);
                        }
                        else if(enemyList.get(0).getxPos()>player.getxPos()+400){
                            for(int i = 0; i < 11; i++){
                                enemyList.get(i).moveTowards(player,400,400);
                            }
                        }
                        else if(npcList.get(0).getChatIndex() > 375){
                            inStory = false;
                        }
                        else if(!theGameScreen.checkInChat()){
                            interactWith(0);
                        }
                    }
                    else if(!theGameScreen.checkInChat()){
                        interactWith(0);
                    }
                }
                else if(storyLocation==39){
                    if(npcList.get(0).getChatIndex() > 408) {
                        inStory = false;
                        unBarricadeMap(67,35);
                    }
                    else if(!theGameScreen.checkInChat()){
                        interactWith(0);
                    }
                }
            }
        }
        //end of story stuff
    }
    public void barricadeMap(int mapX, int mapY){
        theGameScreen.barricadeMap(mapX,mapY);
    }
    public void unBarricadeMap(int mapX, int mapY){
        theGameScreen.unbarricadeMap(mapX, mapY);
    }
    public Person swapPerson(Person person, int health, int detect, int physical, int magical, boolean ally){
        Person result;
        if(ally){
            if(physical*2 + magical > magical * 2){
                result = new Person("meleeAlly",person.truexPos(),person.trueyPos(),health,detect,0,physical,magical,person.trueMapX(),person.trueMapY());
            }
            else{
                result = new Person("rangedAlly",person.truexPos(),person.trueyPos(),health,detect,0,physical,magical,person.trueMapX(),person.trueMapY());
            }
            allyList.add(result);
        }
        else{
            if(physical*2 + magical > magical * 2){
                result = new Person("meleeEnemy",person.truexPos(),person.trueyPos(),health,detect,0,physical,magical,person.trueMapX(),person.trueMapY());
            }
            else{
                result = new Person("rangedEnemy",person.truexPos(),person.trueyPos(),health,detect,0,physical,magical,person.trueMapX(),person.trueMapY());
            }
            enemyList.add(result);
        }
        result.connect(person);
        person.goToPerson(npcList.get(0),0,0);
        return result;
    }
    public void lineUp(){
        npcGoTo(2,player.getxPos(),player.getyPos()-24);
        int tempX = npcList.get(2).getxPos();
        movePerson(-1,0,npcList.get(2));
        if(tempX!=npcList.get(2).getxPos()){
            npcGoTo(3,player.getxPos(),player.getyPos()+24);
            tempX = npcList.get(3).getxPos();
            movePerson(-1,0,npcList.get(3));
            if(tempX==npcList.get(3).getxPos()){
                npcGoTo(3,player.getxPos(),player.getyPos()-50);
            }
        }
        else{
            npcGoTo(2,player.getxPos(),player.getyPos()+25);
            npcGoTo(3,player.getxPos(),player.getyPos()+50);
        }
        for(int i = allyList.size()-1; i > -1; i--){
            if(allyList.get(i).getDisplayName()!=null) {
                if (allyList.get(i).getDisplayName().equals("Kuria") || allyList.get(i).getDisplayName().equals("Lloyd")) {
                    allyList.remove(i);
                }
            }
        }
    }
    private void lineUp(int num){
        npcGoTo(num,player.getxPos(),player.getyPos()-24);
        int tempX = npcList.get(num).getxPos();
        movePerson(-1,0,npcList.get(num));
        if(tempX==npcList.get(num).getxPos()){
            npcList.get(num).goToPerson(player,0,25);
        }
    }
    private void swapAllies(int xMap, int yMap) {
        if(storyLocation<36) {
            int xPos1 = npcList.get(2).truexPos();
            int yPos1 = npcList.get(2).trueyPos();
            Person kuria = new Person("meleeAlly", xPos1, yPos1, player.getLevel() * 4, 1000, 0, player.getLevel() + player.getPhysicalStat(), 0, xMap, yMap);
            kuria.addSkills(skillList[6], skillCooldowns[6]);
            kuria.addPassives(passiveList[5]);
            kuria.setDesc("Kuria", "");
            allyList.add(kuria);
            npcList.get(2).goToPerson(npcList.get(0), -100000, -100000);
        }
        int xPos2 = npcList.get(3).truexPos();
        int yPos2 = npcList.get(3).trueyPos();
        Person lloyd = new Person("rangedAlly", xPos2, yPos2, player.getLevel() * 2, 1000, 0, player.getPhysicalStat() / 2, player.getLevel() + player.getMagicStat(), xMap, yMap);
        lloyd.addSkills(skillList[2], skillCooldowns[2]);
        lloyd.setDesc("Lloyd", "");
        allyList.add(lloyd);
        npcList.get(3).goToPerson(npcList.get(0), -100000, -100000);
    }
    private void swapNpcs(int xMap, int yMap){
        boolean k = false;
        boolean l = false;
        for(int i = allyList.size()-1; i > -1; i--){
            if(allyList.get(i).getDisplayName()!=null){
                if(allyList.get(i).getDisplayName().equals("Kuria")){
                    npcList.get(2).goToPerson(allyList.get(i),0,0);
                    allyList.remove(i);
                    k = true;
                }
                else if(allyList.get(i).getDisplayName().equals("Lloyd")){
                    npcList.get(3).goToPerson(allyList.get(i),0,0);
                    allyList.remove(i);
                    l = true;
                }
            }
        }
        if(!k || !l){
            if(!k && !l){
                lineUp();
            }
            else {
                if (!k) {
                    lineUp(2);
                }
                if (!l) {
                    lineUp(3);
                }
            }
        }
    }
    private void goToMapXY(int x, int y){
        theGameScreen.goToMapXY(x,y);
        theGameScreen.setScrollXY(x,y,true);
        theGameScreen.mapChange();
    }
    public boolean isInStory(){
        return inStory;
    }
    public void advanceStory(){
        storyLocation++;
    }
    public void enterStory(){
        System.out.println("Story Entered.");
        inStory = true;}
    public int getStoryLocation(){return storyLocation;}
    public void forceExit(){
        theGameScreen.forceExitChat();
        theGameScreen.menuClose();
    }
    public void earnMoney(int money){
        effects.add(new Attack("obtained",true,player));
        effects.add(new Attack("receiveMoney" + money,true,player));
        playerMoney+=money;
    }
    public void spendMoney(int money){
        playerMoney-=money;
        effects.add(new Attack("gainMoney" + -money,true,player));
    }
    public int checkPlayerMoney(){
        return playerMoney;
    }
    private void questCheck(){
        for(int i = quests.size() - 1; i > -1; i--){
            if (quests.get(i).deleteQuest()){
                quests.remove(i);
            }
        }
    }
    private void interactWithItem(){
        if(player.movable()) {
            for (int i = 0; i < items.size(); i++) {
                Item item = items.get(i);
                if(item.interactable()) {
                    int xPos = item.getxPos();
                    int yPos = item.getyPos();
                    if (xPos + item.width() * item.size() > player.getxPos() && xPos < player.getxPos() + theGameScreen.squareSize() && yPos + item.height() * item.size() > player.getyPos() && yPos < player.getyPos() + theGameScreen.squareSize() && !theGameScreen.checkInChat() && theGameScreen.itemOnMap(item) && !inStory && !item.isRemovable()) {
                        npcList.get(1).switchIndexes(item.index());
                        npcList.get(1).setAnnouncerItem(item);
                        interactWith(1);
                    }
                }
            }
        }
    }
    public void npcLeave(int num){
        if(num==0){
            npcList.get(2).goToPerson(npcList.get(0),0,0);
            npcList.get(3).goToPerson(npcList.get(0),0,0);
        }
        if(num==1){
            npcList.get(2).moveMap(8,5);
            npcList.get(3).moveMap(8,5);
        }
    }
    public void npcGoTo(int index, int x, int y){
        npcList.get(index).moveX(x-npcList.get(index).getxPos());
        npcList.get(index).moveY(y-npcList.get(index).getyPos());
    }
    public int timeKeepNumber(){
        return Integer.parseInt(timeKeep.substring(9));
    }
    public boolean timeKeepContains(String str){
        return timeKeep.contains(str);
    }
    public void generateSaveCode(){
        saveCode = "";
        addToSaveCode("" + player.getxPos());
        saveCode += "$1";
        addToSaveCode("" + player.getyPos());
        saveCode += "$2";
        addToSaveCode("" + theGameScreen.getScrollX());
        saveCode += "$3";
        addToSaveCode("" + theGameScreen.getScrollY());
        saveCode += "$4";
        addToSaveCode("" + player.getHpStat());
        saveCode += "$5";
        addToSaveCode("" + player.getPhysicalStat());
        saveCode += "$6";
        addToSaveCode("" + player.getMagicStat());
        saveCode += "$7";
        addToSaveCode("" + player.getLevel());
        saveCode += "$8";
        addToSaveCode("" + player.getMoodLevel());
        saveCode += "$9";
        addToSaveCode("" + player.getXp());
        saveCode += "$1";
        addToSaveCode("" + playerMoney);
        saveCode += "$0";
        for(int i = 0; i < skillList.length; i++){
            if(player.skillsContain(i)){
                addToSaveCode("" + i);
            }
            saveCode += 46;
        }
        saveCode += "$1";
        for(int i = 0; i < 4; i++){
            for(int index = 0; index < skillList.length; index++){
                if(player.getSkill(i).equals(skillList[index])){
                    addToSaveCode("" + index);
                }
            }
            saveCode += 46;
        }
        saveCode += "$2";
        for(int i = 0; i < passiveList.length; i++){
            if(player.passivesContain(i)){
                addToSaveCode("" + i);
            }
            saveCode += 46;
        }
        saveCode += "$3";
        for(int i = 0; i < 2; i++){
            for(int index = 0; index < passiveList.length; index++){
                if(player.getPassive(i).equals(passiveList[index])){
                    addToSaveCode("" + index);
                }
            }
            saveCode += 46;
        }
        saveCode += "$4";
        for(int i = 0; i < npcList.size(); i++){
            Person npc = npcList.get(i);
            addToSaveCode("" + npc.getChatIndex());
            saveCode += 53;
            for(int quest = 0; quest < npc.questsLength(); quest++){
                addToSaveCode("" + npc.questDone(quest));
            }
            saveCode += 53;
            addToSaveCode("" + npc.theX());
            saveCode += 53;
            addToSaveCode("" + npc.theY());
            saveCode += 53;
            addToSaveCode("" + npc.getMapX());
            saveCode += 53;
            addToSaveCode("" + npc.getMapY());
            saveCode += 53;
        }
        saveCode+= "$5";
        addToSaveCode("" + storyLocation);
        saveCode+= "$6";
        for(int i = 0; i < items.size(); i++){
            Item item = items.get(i);
            if(item.interactable()) {
                addToSaveCode("" + item.isRemovable());
                saveCode += 53;
            }
        }
        saveCode+= "$7";
        addToSaveCode("" + theGameScreen.getLastX());
        saveCode+= "$8";
        addToSaveCode("" + theGameScreen.getLastY());
        saveCode+= "$9";
        for(int i = 0; i < quests.size(); i++){
            Quest quest = quests.get(i);
            String name = quest.getName();
            while(name.contains(" ")){
                name = name.substring(0,name.indexOf(" ")) + "_" + name.substring(name.indexOf(" ") + 1);
            }
            addToSaveCode(name);
            saveCode+=53;
        }
        saveCode+= "$0";
        addToSaveCode("" + player.getStatBonus());
        saveCode+="$1";
        addToSaveCode("" + theGameScreen.getPointX());
        saveCode+="$2";
        addToSaveCode("" + theGameScreen.getPointY());
        saveCode+="$3";
        try {
            FileWriter writer = new FileWriter("saveCode.txt");
            writer.write(saveCode);
            writer.close();
        }
        catch(IOException ioe){}
        System.out.println("Saved.");
//        System.out.println("Copy the Save Code below:");
        System.out.println(saveCode);
//        exitGame();
    }
    private void addToSaveCode(String str){
        for(int i = 0; i < str.length(); i++){
            saveCode += characters.indexOf(str.substring(i,i+1));
        }
    }
    public void decodeSaveCode(String code){
        int place = 0;
        int index = 0;
        int num = 0;
        String addWhat = "";
        int x = 0;
        int y = 0;
        int mapX = 0;
        int mapY = 0;
        int lastX = 0;
        int lastY = 0;
        int pointX = 0;
        int pointY = 0;
        int hp = 0;
        int physical = 0;
        int magic = 0;
        int level = 0;
        double mood = 0;
        int exp = 0;
        for(int i = 0; i < code.length(); i+=2){
            System.out.println("saveCode");
            if(code.substring(i,i+2).contains("$")){
                if(place==0){
                    x = Integer.parseInt(addWhat);
                }
                else if(place==1){
                    y = Integer.parseInt(addWhat);
                }
                else if(place==2){
                    mapX = Integer.parseInt(addWhat);
                }
                else if(place==3){
                    mapY = Integer.parseInt(addWhat);
                }
                else if(place==4){
                    hp = Integer.parseInt(addWhat);
                }
                else if(place==5){
                    physical = Integer.parseInt(addWhat);
                }
                else if(place==6){
                    magic = Integer.parseInt(addWhat);
                }
                else if(place==7){
                    level = Integer.parseInt(addWhat);
                }
                else if(place==8){
                    mood = Double.parseDouble(addWhat);
                }
                else if(place==9){
                    exp = Integer.parseInt(addWhat);
                }
                else if(place==10){
                    playerMoney = Integer.parseInt(addWhat);
                }
                else if(place==16){
                    storyLocation = Integer.parseInt(addWhat);
                    //theGameScreen.goToMapXY(-mapX/1375,-mapY/775);
                    //theGameScreen.setScrollXY(mapX,mapY,true);
                    theGameScreen.setScrollXY(mapX,mapY,false);
                    theGameScreen.checkMap();
                }
                else if(place==18){
                    lastX = Integer.parseInt(addWhat);
                }
                else if(place==19){
                    lastY = Integer.parseInt(addWhat);
                }
                else if(place==21){
                    int statB = Integer.parseInt(addWhat);
                    player.updatePlayer(x,y,hp,physical,magic,level,mood,exp,statB);
                    player.assignCooldowns(skillList,skillCooldowns,energyCosts);
                }
                else if(place==22){
                    pointX = Integer.parseInt(addWhat);
                }
                else if(place==23){
                    pointY = Integer.parseInt(addWhat);
                    theGameScreen.setLast(lastX,lastY,pointX,pointY);
                }
                //System.out.println(addWhat);//marker
                addWhat = "";
                place++;
                index = 0;
                num = 0;
            }
            else{
                int number = Integer.parseInt(code.substring(i, i + 2));
                if(number == 46){
                    if(place==11 && !addWhat.isEmpty()){
                        player.obtainSkill(Integer.parseInt(addWhat));
                    }
                    if(place==12 && !addWhat.isEmpty()){
                        player.equipSkill(Integer.parseInt(addWhat),index,skillList);
                    }
                    if(place==13 && !addWhat.isEmpty()){
                        player.obtainPassive(Integer.parseInt(addWhat));
                    }
                    if(place==14 && !addWhat.isEmpty()){
                        player.equipPassive(Integer.parseInt(addWhat),index,passiveList);
                    }
                    num = 0;
                    index++;
                    //System.out.println(addWhat);//marker
                    addWhat = "";
                }
                else if (number == 53){
                    if(place==15){
                        Person npc = npcList.get(index);
                        if(num==0){
                            npc.setChatIndex(Integer.parseInt(addWhat));
                            //System.out.print(Integer.parseInt(addWhat) + " ");
                            num++;
                        }
                        else if(num==1){
                            int num2 = 0;
                            while(addWhat.length()>0){
                                if(addWhat.indexOf("true")==0){
                                    npc.questDone(num2);
                                    addWhat = addWhat.substring(4);
                                }
                                if(addWhat.indexOf("false")==0){
                                    addWhat = addWhat.substring(5);
                                }
                                num2++;
                            }
                            num++;
                        }
                        else if(num==2){
                            npc.goToXY(Integer.parseInt(addWhat),npc.theY());
                            num++;
                        }
                        else if(num==3){
                            npc.goToXY(npc.theX(),Integer.parseInt(addWhat));
                            num++;
                        }
                        else if(num==4){
                            npc.moveMap(Integer.parseInt(addWhat),npc.getMapY());
                            num++;
                        }
                        else if(num==5){
                            npc.moveMap(npc.getMapX(),Integer.parseInt(addWhat));
                            num = 0;
                            index++;
                        }
                    }
                    if(place==17){
                        Item item = items.get(index);
                        while(!item.interactable()){
                            index++;
                            item = items.get(index);
                        }
                        if(addWhat.indexOf("true")==0){
                            item.remove();
                        }
                        index++;
                    }
                    if(place==20){
                        String name = addWhat;
                        while(name.contains("_")){
                            name = name.substring(0,name.indexOf("_")) + " " + name.substring(name.indexOf("_") + 1);
                        }
                        Quest quest = new Quest(name);
                        for(Person person: npcList){
                            if(person.getQuests()!=null) {
                                for (int q = 0; q < person.getQuests().length; q++) {
                                    if (person.getQuests()[q].getName().equals(quest.getName())) {
                                        person.getQuests()[q] = quest;
                                    }
                                }
                            }
                        }
                        quests.add(quest);
                    }
                    //System.out.print(addWhat + " ");//marker
                    addWhat = "";
                }
                else {
                    addWhat += characters.substring(number, number + 1);
                }
            }
        }
    }
    public int randomNum(int possibleNumberCount, int minimum){
        return (int)(Math.random() * possibleNumberCount) + minimum;
    }
    public void obtainSkill(int skillNum){
        effects.add(new Attack("skill" + skillNum,false,null));
    }
    public void obtainPassive(int passiveNum){
        effects.add(new Attack("passive" + passiveNum,false,null));
    }
    private void checkCode(String code){
        boolean validity = true;
        if(code.length()%2!=0){
            validity = false;
        }
        else{
            String temp = code;
            int count = 0;
            while(temp.length()>0){
                System.out.println("checking validity");
                if(temp.contains("$")){
                    if(temp.length()>2) temp = temp.substring(temp.indexOf("$")+2);
                    else{
                        temp = "";
                    }
                    count++;
                }
                else{
                    validity = false;
                    break;
                }
            }
            if(count < 17){
                validity = false;
            }
        }
        if(!validity){
            System.out.println("Invalid Save Code. Try again.");
            exitGame();
        }
    }
    private void exitGame(){
        dispatchEvent(new WindowEvent(this,WindowEvent.WINDOW_CLOSING));
    }
    //    public void printStuff(){
//        everything.clear();
//        System.out.println(everything.add(player));
//        everything.add(0,enemyList);
//        System.out.println(everything.set(1,npcList));
//        everything.add(allyList);
//        everything.add(skillList);
//        everything.add(passiveList);
//        System.out.println(everything.remove(0));
//        System.out.println(everything);
//        theGameScreen.printMap();
//        Attack[][] attacks2 = new Attack[2][attacks.size()];
//        Attack effect = new Effect();
//        Effect a = new Effect();
//        Effect b =  new Effect();
//        for(int i = 0; i < attacks.size(); i++){
//            attacks2[0][i] = attacks.get(i);
//        }
//        attacks2[0][1] = effect;
//        attacks2[0][2] = a;
//        attacks2[0][3] = b;
//        System.out.println(attacks2);
//        System.out.println(effect);
//        System.out.println(effect.compareTo(effect));
//        effects.add(effect);
//        effects.add(a);
//        effects.add(b);
//        int[] stats = new int[]{enemyList.size()};
//        for(int i = 0; i < enemyList.size(); i++){
//            stats[i] = enemyList.get(i).getHpStat();
//        }
//        int[] stats2 = new int[]{allyList.size()};
//        for(int i = 0; i < allyList.size(); i++){
//            stats2[i] = allyList.get(i).getHpStat();
//        }
//        selectionSort(stats);
//        insertionSort(stats2);
//    }
    public int findMinIndex(int index, int[] arr)
    {
        int minUpdate = 0;
        int min = arr[index];
        for(int i = index; i < arr.length; i++)
        {
            if(arr[i] < min)
            {
                min = arr[i];
                index = i;
                minUpdate++;
            }
        }
        System.out.println("Minimum Updates: " + minUpdate);
        return index;
    }
    public void swap(int[] arr, int i, int j)
    {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
    public void selectionSort(int[] arr)
    {
        for(int i = 0; i < arr.length; i++)
        {
            int minIndex = findMinIndex(i, arr);
            if(minIndex != i)
            {
                swap(arr, i, minIndex);
            }
            print(arr);
        }
    }
    public void insertionSort(int[] arr)
    {
        for(int i = 1; i < arr.length; i++)
        {
            int temp = arr[i];
            int index = i;
            while(index > 0 && temp < arr[index - 1])
            {
                swap(arr, index, index - 1);
                index--;
            }
            print(arr);
        }
    }
    public void print(int[] arr){
        for(int i : arr){
            System.out.print(i + " ");
        }
        System.out.println();
    }
}
