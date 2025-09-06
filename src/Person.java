import java.util.ArrayList;

public class Person
{
    private boolean canMove, inAttack, healing,burned,stunned,bleeding,invincible,gambit,gambitSucceed,dead,aggro,pathFinding,berserk;
    private int xPos, yPos, mapX, mapY, hpStat, physicalStat, magicStat, statPoints, sword, magic, defense,
            xp, level, xpNeeded, reserveHpCount, reserveHp, maxHp, cuts, energy, lineNum, optionCount, optionNum, detectionRange, burnCounter, burnEnd,
            stunCounter,stunEnd, bleedCounter, bleedEnd, attackCombo, attackCount, baseSpeed, bossAttackNum, statBonus, meter, attackMove;
    private boolean shovable = true;
    private double hp;
    private int chatIndex;
    private int mode;
    private int burnDamage;
    private final int respawnableTimes; //-1 means infinite
    private double moodLevel;
    private String direction = "right";
    private String lastDirection = "left";
    private String type, displayName, specialRole;
    private int[] cooldowns = new int[4];
    private int[] energyCosts = new int[4];
    private int[] length = new int[4];
    private boolean[] held = new boolean[4];
    private float[] currentCooldowns = {0,0,0,0};
    private boolean[] cooldownDone = {true,true,true,true};
    private boolean[] skill = new boolean[4];
    private String[] skillSet;
    private String[] skillsKnown; //skills that are unknown won't show up in skill menu and will be replaced by question mark
    private String[] passivesEquipped;
    private String[] passivesKnown;
    private String[] lines;
    private String[] options;
    private int[] accessories;
    /**How much lineNum increases by for each option in options*/
    private int[] optionActions;
    /** 1st index is the index where it states "select"
     * 2nd index is number of options
     * 3rd index is index at which the options are at*/
    private int[][] optionStuff;
    private int[] switchLocations;
    private String[] switchNames;
    /**indexes of lines that the line where you start at change*/
    private int[] indexesChange;
    /**indexes of the lines that you start at*/
    private int[] newIndexes;
    /**indexes where you get exp and skills and money and stuff*/
    private int[] indexesGain;
    /**string telling code what player gains*/
    private String[] gainWhat;
    /**indexes where you are required to have something*/
    private int[] indexesWithRequirements;
    /**string stating what requirement is*/
    private String[] requirements;
    /**when you don't meet the requirement, the number is added to lineNum*/
    private int[] requirementAdds;
    private int[] questIndexes;
    private Quest[] quests;
    private int[] destinationX;
    private int[] destinationY;
    private int destination;
    private int[] wait;
    private int waitTick;
    private Item announcerItem;
    private int attackBooster = 1;
    private int speed;
    private int deadCounter = 255;
    private static Game game;
    private Person enemy;
    private Person connectedPerson;
    private boolean tookDamage;
    private ArrayList<String> combo = new ArrayList<>();
    private int comboCount;
    private String desc;
    private String[] pathFind;
    private String[] currentPathFind = new String[]{""};
    public Person(int x, int y){ //player constructor
        type = "player";
        xPos = x;
        yPos = y;
        level = 1;
        xpNeeded = 50;
        hp = 100;
        deadCounter = 0;
        dead = false;
        maxHp = 100;
        energy=100;
        speed = 5;
        baseSpeed = 5;
        canMove = true;
        skillSet = new String[]{game.skillList()[3],game.skillList()[8],"",""};
        skillsKnown = new String[]{game.skillList()[3],game.skillList()[8]};
        passivesEquipped = new String[]{game.passiveList()[0],""};
        passivesKnown = new String[]{game.passiveList()[0]};
        respawnableTimes=-1;
    }
    public void updatePlayer(int x, int y, int health, int physical, int magic, int lvl, double mood, int exp, int statB){ //loaded player
        type = "player";
        xPos = x;
        yPos = y;
        level = lvl;
        xpNeeded = 50;
        for(int i = 1; i < lvl; i++){
            xpNeeded*=1.1;
        }
        xp = exp;
        hpStat = health;
        physicalStat = physical;
        magicStat = magic;
        statPoints = ((level-1) * 3) - hpStat - physicalStat - magicStat;
        if(statPoints < 0){
            statPoints = 0;
            //report that something went wrong
        }
        setStats();
        hp = maxHp;
        energy=100;
        speed = 5;
        baseSpeed = 5;
        canMove = true;
        moodLevel = mood;
        statBonus = statB;
    }
    public void updateFighter(int health, int physical, int magic){
        hpStat = health;
        physicalStat = physical;
        magicStat = magic;
        setStats();
        hp = maxHp;
        canMove = true;
    }
    public Person(String name, int x, int y, int x2, int y2){ //constructor for npcs
        type = name;
        xPos = x;
        yPos = y;
        mapX = x2;
        mapY = y2;
        respawnableTimes=0;
        setLines();
    }
    public Person(String name, int x, int y, int health, int detect, int respawn, int physical, int magical, int xMap, int yMap){ //constructor for fighters
        if(name.contains("Enemy")){
            game.getGameScreen().getEnemyNums().add(-1);
            enemy = game.getPlayer();
        }
        if(name.contains("Boss")){
            shovable = false;
        }
        speed = 5;
        baseSpeed = 5;
        respawnableTimes = respawn;
        type = name;
        xPos = x;
        yPos = y;
        mapX = xMap;
        mapY = yMap;
        detectionRange=detect;
        canMove = true;
        hpStat = health;
        physicalStat = physical;
        magicStat = magical;
        setStats();
        hp=maxHp;
    }
    public int[] getAccessories(){
        if(type.equals("IK") && quests[0].deleteQuest() && accessories==null){
            accessories = new int[]{2};
            destinationX = new int[]{450};
            destination = 0;
        }
        return accessories;
    }
    public void connect(Person other){
        connectedPerson = other;
    }
    public void hold(int skillNum){
        if(!inSkill(0) && !inSkill(1) && !inSkill(2) && !inSkill(3)) {
            if (game.getHoldable()[game.indexOfSkill(skillNum)]) {
                if (!held()) {
                    held[skillNum] = true;
                }
                if (held[skillNum]) {
                    length[skillNum]++;
                    if (length[skillNum] < 56) {
                        game.getAttacks().add(new Attack("particle", true, this));
                    }
                }
            }
        }
    }
    public int getLengthHeld(int skillNum){return length[skillNum];}
    public boolean getHeld(int skillNum){return held[skillNum];}
    public void letGo(int skillNum){
        held[skillNum] = false;
        length[skillNum] = 0;
    }
    public boolean shovable(){
        return shovable && !inAttack && !inSkill(0) && !inSkill(1) && !inSkill(2) && !inSkill(3) && !held();
    }
    public Person clone(){
        Person clone = new Person(type, xPos, yPos, hpStat, detectionRange, respawnableTimes, physicalStat, magicStat,mapX,mapY);
        clone.setDesc(displayName,desc);
        return clone;
    }
    public String toString(){
        return "Skills: " + skillSet + " " + "Name: " + displayName + "type" + type + "\n";
    }
    public boolean equals(Object other){
        return other==this;
    }
    public void getCut(Person attacker){
        if(attacker.passiveEquipped(2)){
            cuts++;
            startBleeding(2);
        }
        cuts++;
    }
    public boolean needPath(){
        if(enemy==null){
            return false;
        }
        if(enemy==game.getPlayer()){
            if(xPos/1375 + mapX!=game.getGameScreen().getMapNumX() || yPos/775 + mapY!=game.getGameScreen().getMapNumY()){
                return true;
            }
        }
        else{
            if(xPos/1375 + mapX!=enemy.xPos/1375 + enemy.mapX || yPos/775 + mapY!=enemy.yPos/775 + enemy.mapY){
                return true;
            }
        }
        return false;
    }
    public void pathFind(){
        pathFind = new String[0];
        int originalX = xPos;
        int originalY = yPos;
        boolean xPath = true;
        GameScreen gs = game.getGameScreen();
        while (getxPos() > enemy.getxPos()+25 || getxPos()+25 < enemy.getxPos() || getyPos() > enemy.getyPos()+25 || getyPos()+25 < enemy.getyPos() && pathFind.length<700){
            System.out.println(getxPos() - enemy.getxPos() + " " + (getyPos() - enemy.getyPos()));
            if((getxPos() > enemy.getxPos()+25 || getxPos()+25 < enemy.getxPos()) && xPath){
                int move;
                if(getxPos() > enemy.getxPos()+25){
                    move = -1;
                }
                else{
                    move = 1;
                }
                int tempX = xPos;
                gs.movePerson(move,0,this);
                if (tempX == xPos) {
                    int tempY = yPos;
                    int count1 = 0;
                    boolean canMoveX = false;
                    boolean exit1 = false;
                    int count2 = 0;
                    boolean exit2 = false;
                    //exit2 = lastDirection.equals("up");
                    while (!canMoveX && !exit1 && count1 < 700) {
                        int tempY2 = yPos;
                        gs.movePerson(0, -1, this);
                        if (tempY2 != yPos) {
                            tempX = xPos;
                            gs.movePerson(move, 0, this);
                            if (tempX != xPos) {
                                canMoveX = true;
                                gs.movePerson(-move, 0, this);
                            }
                        } else {
                            exit1 = true;
                        }
                        count1++;
                    }
                    yPos = tempY;
                    canMoveX = false;
                    while (!canMoveX && !exit2 && count2 < 700) {
                        int tempY2 = yPos;
                        gs.movePerson(0, 1, this);
                        if (tempY2 != yPos) {
                            tempX = xPos;
                            gs.movePerson(move, 0, this);
                            if (tempX != xPos) {
                                canMoveX = true;
                                gs.movePerson(-move, 0, this);
                            }
                        } else {
                            exit2 = true;
                        }
                        count2++;
                    }
                    yPos = tempY;
                    if (exit1 || (count1 > count2 && !exit2) && (pathFind.length==0 || !pathFind[pathFind.length-1].equals("up"))) {
                        for (int i = 0; i < count2; i++) {
                            pathFind = add(pathFind,"down");
                            gs.movePerson(0, 1, this);
                        }
                    } else {
                        for (int i = 0; i < count1; i++) {
                            pathFind = add(pathFind,"up");
                            gs.movePerson(0, -1, this);
                        }
                    }
                    gs.movePerson(move, 0, this);
                }
                if(move < 0) {
                    pathFind = add(pathFind,"left");
                }
                else{
                    pathFind = add(pathFind,"right");
                }
            }
            else if(getyPos() > enemy.getyPos()+25 || getyPos()+25 < enemy.getyPos()){
                xPath = false;
                int move;
                if(getyPos() > enemy.getyPos()+25){
                    move = -1;
                }
                else{
                    move = 1;
                }
                int tempY = yPos;
                gs.movePerson(0,move,this);
                if (tempY == yPos) {
                    int tempX = xPos;
                    int count1 = 0;
                    boolean canMoveY = false;
                    boolean exit1 = false;
                    int count2 = 0;
                    boolean exit2 = false;
                    //exit2 = lastDirection.equals("right");
                    while (!canMoveY && !exit1 && count1 < 700) {
                        int tempX2 = xPos;
                        gs.movePerson(1, 0, this);
                        if (tempX2 != xPos) {
                            tempY = yPos;
                            gs.movePerson(0, move, this);
                            if (tempY != yPos) {
                                canMoveY = true;
                                gs.movePerson(0, -move, this);
                            }
                        } else {
                            exit1 = true;
                        }
                        count1++;
                    }
                    xPos = tempX;
                    canMoveY = false;
                    while (!canMoveY && !exit2 && count2 < 700) {
                        int tempX2 = xPos;
                        gs.movePerson(-1, 0, this);
                        if (tempX2 != xPos) {
                            tempY = yPos;
                            gs.movePerson(0, move, this);
                            if (tempY != yPos) {
                                canMoveY = true;
                                gs.movePerson(0, -move, this);
                            }
                        } else {
                            exit2 = true;
                        }
                        count2++;
                    }
                    xPos = tempX;
                    if (exit1 || (count1 > count2 && !exit2) && (pathFind.length==0 || !pathFind[pathFind.length-1].equals("right"))) {
                        for (int i = 0; i < count2; i++) {
                            pathFind = add(pathFind,"left");
                            gs.movePerson(-1, 0, this);
                        }
                    } else {
                        for (int i = 0; i < count1; i++) {
                            pathFind = add(pathFind,"right");
                            gs.movePerson(1, 0, this);
                        }
                    }
                    gs.movePerson(0, move, this);
                }
                if(move < 0) {
                    pathFind = add(pathFind,"up");
                }
                else{
                    pathFind = add(pathFind,"down");
                }
            }
            else{
                xPath = true;
            }
        }
        xPos = originalX;
        yPos = originalY;
//        System.out.println("pathFinding");
        if(currentPathFind.length==0) {
            String[] pf = pathFind(0, 0, 0);
            currentPathFind = pf;
        }
        if(currentPathFind.length >= pathFind.length){
            currentPathFind = pathFind;
        }
    }
    public String[] add(String[] arr, String s){
        String[] result = new String[arr.length+1];
        for(int i = 0; i < arr.length; i++){
            result[i] = arr[i];
        }
        result[result.length-1] = s;
        return result;
    }
    public String[] pathFind(int num, int length, int times){
        //int originalNum = num;
        if(num>0){
            //System.out.println(getxPos() + " " + getyPos());
        }
        String[] pathFind = new String[0];
        int originalX = xPos;
        int originalY = yPos;
        boolean xPath = true;
        GameScreen gs = game.getGameScreen();
        boolean moved = true;
        while ((getxPos() > enemy.getxPos()+25 || getxPos()+25 < enemy.getxPos() || getyPos() > enemy.getyPos()+25 || getyPos()+25 < enemy.getyPos()) && length<700 && pathFind.length<700 && times < 3){
            int xRecord = xPos;
            int yRecord = yPos;
            //System.out.println(getxPos() - enemy.getxPos() + " " + (getyPos() - enemy.getyPos()));
            if((getxPos() > enemy.getxPos()+25 || getxPos()+25 < enemy.getxPos()) && xPath && num < 3){
                int move;
                if(getxPos() > enemy.getxPos()+25){
                    move = -1;
                }
                else{
                    move = 1;
                }
                int tempX = xPos;
                gs.movePerson(move,0,this);
                if (tempX == xPos) {
                    int tempY = yPos;
                    int count1 = 0;
                    boolean canMoveX = false;
                    boolean exit1 = false;
                    int count2 = 0;
                    boolean exit2 = false;
                    //exit2 = lastDirection.equals("up");
                    if(num==1) {
                        while (!canMoveX && !exit1 && count1 < 700) {
                            int tempY2 = yPos;
                            gs.movePerson(0, -1, this);
                            if (tempY2 != yPos) {
                                tempX = xPos;
                                gs.movePerson(move, 0, this);
                                if (tempX != xPos) {
                                    canMoveX = true;
                                    gs.movePerson(-move, 0, this);
                                }
                            } else {
                                exit1 = true;
                            }
                            count1++;
                        }
                        yPos = tempY;
                    }
                    canMoveX = false;
                    if(num==2) {
                        while (!canMoveX && !exit2 && count2 < 700) {
                            int tempY2 = yPos;
                            gs.movePerson(0, 1, this);
                            if (tempY2 != yPos) {
                                tempX = xPos;
                                gs.movePerson(move, 0, this);
                                if (tempX != xPos) {
                                    canMoveX = true;
                                    gs.movePerson(-move, 0, this);
                                }
                            } else {
                                exit2 = true;
                            }
                            count2++;
                        }
                        yPos = tempY;
                    }
                    if(num==0){
                        if(!moved){
                            xPos = originalX;
                            yPos = originalY;
                            pathFind = add(pathFind,"");
                            return pathFind;
                        }
                        boolean up = lastDirection.equals("up");
                        boolean down = lastDirection.equals("down");
                        String[] temp1;
                        String[] temp2;
                        //if(!down) {
                        temp1 = pathFind(1, length + pathFind.length,times+1);
                        //}
                        //if(!up) {
                        temp2 = pathFind(2, length + pathFind.length,times+1);
                        //}
                        String[] chosen;
                        //if(!up && !down) {
                        if (temp1.length > temp2.length) {
                            chosen = temp2;
                        } else {
                            chosen = temp1;
                        }
                        if(contains(temp1,"")){
                            chosen = temp2;
                        }
                        if(contains(temp2,"")){
                            chosen = temp1;
                        }
                        //}
//                        else if(!down){
//                            chosen = temp1;
//                        }
//                        else {
//                            chosen = temp2;
//                        }
                        for(String path : chosen){
                            pathFind = add(pathFind,path);
                            if(path.equals("left")){
                                gs.movePerson(-1,0,this);
                            }
                            else if(path.equals("right")){
                                gs.movePerson(1,0,this);
                            }
                            else if(path.equals("up")){
                                gs.movePerson(0,-1,this);
                            }
                            else{
                                gs.movePerson(0,1,this);
                            }
                        }
                    }
                    else if (num==2) {
                        for (int i = 0; i < count2; i++) {
                            pathFind = add(pathFind,"down");
                            gs.movePerson(0, 1, this);
                        }
                        num = 0;
                    } else if(num==1){
                        for (int i = 0; i < count1; i++) {
                            pathFind = add(pathFind,"up");
                            gs.movePerson(0, -1, this);
                        }
                        num = 0;
                    }
                    gs.movePerson(move, 0, this);
                }
                if(move < 0) {
                    pathFind = add(pathFind,"left");
                }
                else{
                    pathFind = add(pathFind,"right");
                }
            }
            else if(getyPos() > enemy.getyPos()+25 || getyPos()+25 < enemy.getyPos()){
                xPath = false;
                int move;
                if(getyPos() > enemy.getyPos()+25){
                    move = -1;
                }
                else{
                    move = 1;
                }
                int tempY = yPos;
                gs.movePerson(0,move,this);
                if (tempY == yPos) {
                    int tempX = xPos;
                    int count1 = 0;
                    boolean canMoveY = false;
                    boolean exit1 = false;
                    int count2 = 0;
                    boolean exit2 = false;
                    //exit2 = lastDirection.equals("right");
                    if(num==3) {
                        while (!canMoveY && !exit1 && count1 < 700) {
                            int tempX2 = xPos;
                            gs.movePerson(1, 0, this);
                            if (tempX2 != xPos) {
                                tempY = yPos;
                                gs.movePerson(0, move, this);
                                if (tempY != yPos) {
                                    canMoveY = true;
                                    gs.movePerson(0, -move, this);
                                }
                            } else {
                                exit1 = true;
                            }
                            count1++;
                        }
                        xPos = tempX;
                    }
                    canMoveY = false;
                    if(num==4) {
                        while (!canMoveY && !exit2 && count2 < 700) {
                            int tempX2 = xPos;
                            gs.movePerson(-1, 0, this);
                            if (tempX2 != xPos) {
                                tempY = yPos;
                                gs.movePerson(0, move, this);
                                if (tempY != yPos) {
                                    canMoveY = true;
                                    gs.movePerson(0, -move, this);
                                }
                            } else {
                                exit2 = true;
                            }
                            count2++;
                        }
                        xPos = tempX;
                    }
                    if(num==0){
                        if(!moved){
                            xPos = originalX;
                            yPos = originalY;
                            pathFind = add(pathFind,"");
                            return pathFind;
                        }
                        boolean right = lastDirection.equals("right");
                        boolean left = lastDirection.equals("left");
                        String[] temp1;
                        String[] temp2;
                        //if(!left) {
                        temp1 = pathFind(3, length + pathFind.length,times+1);
                        //}
                        //if(!right) {
                        temp2 = pathFind(4, length + pathFind.length,times+1);
                        //}
                        String[] chosen;
                        //if(!right && !left) {
                        if (temp1.length > temp2.length) {
                            chosen = temp2;
                        } else {
                            chosen = temp1;
                        }
                        if(contains(temp1,"")){
                            chosen = temp2;
                        }
                        if(contains(temp2,"")){
                            chosen = temp1;
                        }
                        //}
//                        else if(!left){
//                            chosen = temp1;
//                        }
//                        else {
//                            chosen = temp2;
//                        }
                        for(String path : chosen){
                            pathFind = add(pathFind,path);
                            if(path.equals("left")){
                                gs.movePerson(-1,0,this);
                            }
                            else if(path.equals("right")){
                                gs.movePerson(1,0,this);
                            }
                            else if(path.equals("up")){
                                gs.movePerson(0,-1,this);
                            }
                            else{
                                gs.movePerson(0,1,this);
                            }
                        }
                    }
                    else if (num==4) {
                        for (int i = 0; i < count2; i++) {
                            pathFind = add(pathFind,"left");
                            gs.movePerson(-1, 0, this);
                        }
                        num = 0;
                    } else if(num==3){
                        for (int i = 0; i < count1; i++) {
                            pathFind = add(pathFind,"right");
                            gs.movePerson(1, 0, this);
                        }
                        num = 0;
                    }
                    gs.movePerson(0, move, this);
                }
                if(move < 0) {
                    pathFind = add(pathFind,"up");
                }
                else{
                    pathFind = add(pathFind,"down");
                }
            }
            else{
                xPath = true;
            }
            moved = xRecord!=xPos || yRecord!=yPos;
        }
        xPos = originalX;
        yPos = originalY;
        return pathFind;
    }
    public boolean contains(String[] arr, String target){
        for(String str : arr){
            if(str.equals(target)){
                return true;
            }
        }
        return false;
    }
    public boolean pathNeeded(){return pathFind.length==0 && (getxPos() > enemy.getxPos()+25 || getxPos()+25 < enemy.getxPos() || getyPos() > enemy.getyPos()+25 || getyPos()+25 < enemy.getyPos());}
    public String pathFound(int index){
        if(index >= currentPathFind.length){
            return "";
        }
        else{
            return remove();
        }
    }
    public String remove(){
        String[] result = new String[currentPathFind.length-1];
        for(int i = 1; i < currentPathFind.length; i++){
            result[i-1] = currentPathFind[i];
        }
        String str = currentPathFind[0];
        currentPathFind = result;
        return str;
    }
    public void setPathFinding(boolean p){pathFinding = p;}
    public boolean isPathFinding(){return pathFinding;}
    public int respawnableTimes(){return respawnableTimes;}
    public Item getItem(){
        return announcerItem;
    }
    public Person getEnemy(){
        return enemy;
    }
    public static void setGame(Game g){
        game = g;
    }
    public int getAttackMove(){return attackMove;}
    public void setAttackMove(int a){attackMove = a;}
    public void setMode(int m){
        mode = m;
        if(mode!=0){
            meter = 1000;
        }
    }
    public boolean biggerSword(){
        return mode==1;
    }
    public String getDesc(){return desc;}
    public void setDesc(String n,String d){
        displayName = n;
        desc = d;
    }
    public void addSkills(String skill, int cooldown){
        if(skillSet==null){
            skillSet = new String[]{skill};
            cooldowns = new int[]{cooldown};
        }
        else {
            String[] tempSkillSet = new String[skillSet.length + 1];
            for (int i = 0; i < skillSet.length; i++) {
                tempSkillSet[i] = skillSet[i];
            }
            tempSkillSet[tempSkillSet.length - 1] = skill;
            skillSet = tempSkillSet;
            int[] tempCooldowns = new int[cooldowns.length + 1];
            for (int i = 0; i < cooldowns.length; i++) {
                tempCooldowns[i] = cooldowns[i];
            }
            tempCooldowns[tempCooldowns.length - 1] = cooldown;
            cooldowns = tempCooldowns;
        }
    }
    public void addPassives(String passive){
        if(passivesEquipped==null){
            passivesEquipped = new String[]{passive};
        }
        else {
            String[] tempPassivesEquipped = new String[passivesEquipped.length + 1];
            for (int i = 0; i < passivesEquipped.length; i++) {
                tempPassivesEquipped[i] = passivesEquipped[i];
            }
            tempPassivesEquipped[tempPassivesEquipped.length - 1] = passive;
            passivesEquipped = tempPassivesEquipped;
        }
    }
    public void comboCount(){
        comboCount++;
        if(comboCount > 120){
            combo.clear();
        }
    }
    public void addToCombo(String attack){
        combo.add(attack);
        comboCount = 0;
    }
    public boolean combo(String[] comboArray){
        if(comboArray.length > combo.size()){
            return false;
        }
        for(int i = combo.size() - comboArray.length; i < combo.size(); i++){
            if(!comboArray[i - (combo.size() - comboArray.length)].equals(combo.get(i))){
                return false;
            }
        }
        return true;
    }
    public void deadCount(){
        if(type.equals("player")){
            if(dead){
                deadCounter+=6;
                if(deadCounter > 255){
                    deadCounter = 255;
                    dead = false;
                    hp = maxHp;
                    xPos = 25;
                    yPos = 400;
                    energy = 100;
                    meter = 0;
                    invincible = false;
                    canMove = true;
                    inAttack = false;
                    game.getGameScreen().goLastSpawnPoint();
                    game.playerDied();
                    for(int i = 0; i < 4; i++){
                        currentCooldowns[i] = 0;
                    }
                }
            }
            else{
                deadCounter -= 6;
                if (deadCounter < 0) {
                    deadCounter = 0;
                }
            }
        }
        else {
            deadCounter -= 13;
            if (deadCounter < 0) {
                deadCounter = 0;
            }
        }
    }
    /**indexOfPerson is the index of the person, num is skill number (0-3)*/
    public void useSkill(int indexOfPerson,int num){
        game.getGameScreen().getEnemyNums().set(indexOfPerson, num);
        game.setTimeKeep("skill");
        TimeKeeper.timerStart();
    }
    public void check(){
        if(connectedPerson!=null){
            connectedPerson.goToPerson(this,0,0);
        }
        specialRoleCheck();
    }
    public void setEnemy(Person person){
        enemy = person;
    }
    public int getDeadCounter(){
        return deadCounter;
    }
    public int getSpeed(){
        return speed;
    }
    public void setSpeed(int spd){
        speed = spd;
    }
    public void adjustSpeed(int spd){
        speed+=spd;
    }
    public void assignCooldowns(String[] skills, int[] cooldownList, int[] energyList){
        for(int i = 0; i < skills.length; i++){
            if(skillSet[0].equals(skills[i])){
                cooldowns[0] = cooldownList[i];
                energyCosts[0] = energyList[i];
            }
            if(skillSet[1].equals(skills[i])){
                cooldowns[1] = cooldownList[i];
                energyCosts[1] = energyList[i];
            }
            if(skillSet[2].equals(skills[i])){
                cooldowns[2] = cooldownList[i];
                energyCosts[2] = energyList[i];
            }
            if(skillSet[3].equals(skills[i])){
                cooldowns[3] = cooldownList[i];
                energyCosts[3] = energyList[i];
            }
        }
    }
    public void setSpecialRole(String role){
        specialRole = role;
    }
    public void addToAttackCount(){
        attackCount++;
        if(attackCount==60){
            attackCombo=0;
        }
    }
    public void increaseAttackCombo(){
        attackCount=0;
        attackCombo++;
        attackCombo%=4;
    }
    public int getAttackCombo(){return attackCombo;}
    public void bossAttack(){
        int rand = 0;
        if(type.contains("Boss1")){
            rand = game.randomNum(3,1);
        }
        else if(type.contains("Boss2")){
            if(getHpPercent()<=50) {
                int[] nums = {4, 5, 6};
                rand = nums[game.randomNum(3, 0)];
            }
            else{
                int[] nums = {2, 4, 5};
                rand = nums[game.randomNum(3, 0)];
            }
        }
        bossAttackNum = rand;
        inAttack = true;
        attackCount = 0;
    }
    public void personAttacking(){
        attackCount++;
        if(bossAttackNum == 0) {
            if (attackCount > 14) {
                inAttack = false;
                attackCount = 0;
            }
        }
        else{
            int x = mapX * 1375 + game.getGameScreen().getScrollX();
            int y = mapY * 775 + game.getGameScreen().getScrollY();
            if(bossAttackNum==1){
                if(attackCount < 181){
                    game.getAttacks().add(new Attack(x + (int)(Math.random()*1400),y + (int)(Math.random()*800),0,"magma",this));
                    //game.getAttacks().add(new Attack((int)(Math.random()*1400),(int)(Math.random()*800),0,"lightning",this));
                }
                if (attackCount > 210) {
                    inAttack = false;
                    attackCount = 0;
                }
            }
            else if(bossAttackNum==2){
                if(attackCount == 1){
                    if(direction.equals("left")){
                        for(int i = 0; i < 400; i += 64){
                            game.getAttacks().add(new Attack(x + 1500,y + i,25,"slash",this));
                        }
                    }
                    else if(direction.equals("right")){
                        for(int i = 0; i < 400; i += 64){
                            game.getAttacks().add(new Attack(x - 100,y + i,25,"slash",this));
                        }
                    }
                    else if(direction.equals("up")){
                        for(int i = 0; i < 700; i += 64){
                            game.getAttacks().add(new Attack(x + i,y + 900,25,"slash",this));
                        }
                    }
                    else if(direction.equals("down")){
                        for(int i = 0; i < 700; i += 64){
                            game.getAttacks().add(new Attack(x + i,y - 100,25,"slash",this));
                        }
                    }
                }
                if(attackCount == 46){
                    if(direction.equals("left")){
                        for(int i = 400; i < 800; i += 64){
                            game.getAttacks().add(new Attack(x + 1500,y + i,25,"slash",this));
                        }
                    }
                    else if(direction.equals("right")){
                        for(int i = 400; i < 800; i += 64){
                            game.getAttacks().add(new Attack(x - 100,y + i,25,"slash",this));
                        }
                    }
                    else if(direction.equals("up")){
                        for(int i = 700; i < 1400; i += 64){
                            game.getAttacks().add(new Attack(x + i,y + 900,25,"slash",this));
                        }
                    }
                    else if(direction.equals("down")){
                        for(int i = 700; i < 1400; i += 64){
                            game.getAttacks().add(new Attack(x + i,y - 100,25,"slash",this));
                        }
                    }
                }
                if (attackCount > 120) {
                    inAttack = false;
                    attackCount = 0;
                }
            }
            else if(bossAttackNum==3){
                if(attackCount > 60 && attackCount < 151){
                    game.getAttacks().add(new Attack(getxPos() + game.randomNum(attackCount * 4,-attackCount * 2),getyPos()+ game.randomNum(attackCount * 4,-attackCount * 2),0,"magma",this));
                    //game.getAttacks().add(new Attack((int)(Math.random()*1400),(int)(Math.random()*800),0,"lightning",this));
                }
                if (attackCount > 180) {
                    inAttack = false;
                    attackCount = 0;
                }
            }
            else if(bossAttackNum==4){
                if(attackCount==1){
                    int theY = 0;
                    for(int i = 0; i < 1400; i+=78){
                        while(theY<800){
                            game.getAttacks().add(new Attack(x+i,y+theY,0,"magma",this));
                            theY+=120;
                        }
                        theY+=60;
                        theY%=120;
                    }
                }
                if(attackCount==61){
                    int theY = 60;
                    for(int i = 0; i < 1400; i+=78){
                        while(theY<800){
                            game.getAttacks().add(new Attack(x+i,y+theY,0,"magma",this));
                            theY+=120;
                        }
                        theY+=60;
                        theY%=120;
                    }
                }
                if(attackCount>120){
                    inAttack = false;
                    attackCount = 0;
                }
            }
            else if(bossAttackNum==5){
                if(attackCount==1){
                    for(int i = 0; i < 1400; i+=78){
                        int theY = 0;
                        while(theY<800){
                            game.getAttacks().add(new Attack(x+i,y+theY,0,"magma",this));
                            theY+=120;
                        }
                    }
                }
                if(attackCount==61){
                    for(int i = 0; i < 1400; i+=78){
                        int theY = 60;
                        while(theY<800){
                            game.getAttacks().add(new Attack(x+i,y+theY,0,"magma",this));
                            theY+=120;
                        }
                    }
                }
                if(attackCount>120){
                    inAttack = false;
                    attackCount = 0;
                }
            }
            else if(bossAttackNum==6){
                if(attackCount<51 && attackCount%4==0) {
                    int theY = 0;
                    for(int i = attackCount; i < attackCount+4; i++){
                        theY = 0;
                        while (theY < 400) {
                            game.getAttacks().add(new Attack(x + i * 28, y + theY, 0, "magma", this));
                            theY += 60;
                        }
                    }
                    for(int i = attackCount; i < attackCount+4; i++) {
                        theY = 400;
                        while (theY < 800) {
                            game.getAttacks().add(new Attack(x + 1400 - i * 28, y + theY, 0, "magma", this));
                            theY += 60;
                        }
                    }
                }
                if(attackCount==90){
                    inAttack = false;
                    attackCount = 0;
                }
            }
        }
    }
    public int getAttackCount(){return attackCount;}
    public void incrementCount(){attackCount++;}
    public void personAttack(int index){
        inAttack = true;
        int skill = -1;
        if(skillSet!=null){
            for(int i = 0; i < skillSet.length; i++){
                if(cooldownDone[i]){
                    skill = i;
                }
            }
        }
        if(skill>-1) {
            if(type.contains("Enemy")) {
                game.setTimeKeep("enemySkill");
                game.getGameScreen().getEnemyNums().remove(index);
                game.getGameScreen().getEnemyNums().add(index, skill);
                TimeKeeper.timerStart();
            }
            else{
                game.setTimeKeep("allySkill" + game.getAllyList().indexOf(this));
                TimeKeeper.timerStart();
            }
        }
        else {
            if (attackCount < 59) {
                increaseAttackCombo();
            }
            if (type.contains("melee")) {
                if (attackCombo == 0 || attackCombo == 2) {
                    game.getAttacks().add(new Attack(getxPos()-25, getyPos() - 35, 0, "attack", this));
                } else if (attackCombo == 1) {
                    game.getAttacks().add(new Attack(getxPos()-25, getyPos() - 35, 0, "attack2", this));
                } else if (attackCombo == 3) {
                    game.getAttacks().add(new Attack(getxPos()-25, getyPos() - 40, 0, "attack3", this));
                }
            } else if (type.contains("ranged")) {
                Attack bullet = new Attack(getxPos(), getyPos(), 10, "magicBullet", this);
                bullet.redirectTrajectory();
                game.getAttacks().add(bullet);
            }
        }
        attackCount = 0;
    }

    public void equipSkill(int skillNum, int num, String[] skills){
        skillSet[num] = skills[skillNum];
    }
    public void equipPassive(int passiveNum, int num, String[] passives){
        passivesEquipped[num] = passives[passiveNum];
    }
    private void setLines(){
        if(type.equals("guide")){
            displayName = "Guide";
            lines = new String[]{"Welcome, Press X to continue.","Use WASD or arrow keys to move.","Press Space to Attack",
                    "Do you understand?","select","Are you sure?","select","Good.","","Interact with me again.","","Good.","",
                    "There is no maybe.","Choose.","select","Much better.","","Just testing some stuff out :)", ""};
            options = new String[]{"Yes","No","Yes","No","Maybe","Yes"};
            optionActions = new int[]{3,1,3,5,7,1};
            optionStuff = new int[][]{{4,6,15},{2,3,1},{0,2,5}};
            indexesChange = new int[]{8};
            newIndexes = new int[]{18};

        } else if(type.equals("storyteller")){
            displayName = "You";
            lines = new String[]{":You:Where am I?","I don't remember anything...","This is so strange.","I guess I should get up and look around.","Help me!","What was that? I should go check it out.","", "What's wrong?",
                    "I'm being attacked!", "Wait...", "Are you the legendary orange square?","...Who?","The legends foretold the arrival of a orange square. It is said that he would save the world! You must be him! Save me please!",
                    "I don't know... I don't remember anything. Well, talking comes later, I'll try to help you first.","Thank you so much!","","Thank you, you saved me!","Can I ask what was going on here?",
                    "You really don't remember anything huh... Well, that was a Red Square, and Red Squares are trying to take over the world. Recently they starting attacking towns with civilians who can't fight in order to make a better attempt.","I see...",
                    "It is real unfortunate that you forgot your memories, maybe you should look around town. That might help you a bit. Also, here, I'll compensate you for the trouble you had to go through.","Thanks.","",
                    "I've reached another town huh...","That building over there looks important, I should check it out. Might hold clues to my history.","","Hello, what's the RSS?","It stands for Red square Suppression Squad. Would you like to join?","Uhh... Sure.",
                    "Nice! Welcome to the squad!","That over there is your team. Go and say hi.","","Hello my name is ⬛⬛⬛⬛⬛","My name is Kuria.","I'm Lloyd.","Let's get along.","Alright.","Okay.","Our first mission to is check on Toothpaste Town to the east. We'll go when you are ready.","",
                    "We've arrived.","Yikes, this place looks like a mess.","Guess we gotta wipe them all out. That is our mission, after all.","Alright, let's go.","","Good, we are done here. We gotta go south now, it has been requested that we head there by headquarters.","",
                    "This place seems to be all cleaned up. There is no sight of any Red Squares here...","Let's go ask that guy over there. Wait a minute... That guy over there is John!","John?","Yeah. He's a special member of the Rss. Special members don't need to do tasks assigned by headquarters, because they want the special members to stay in the Rss.",
                    "Oh...I see. Is he really strong?","I believe so, I believe he also has the special ability to send somebody's body back in time for a short duration. Nothing they do in the past changes the present, though.","I see.","","What happened here?","Oh, so you are the group tasked with this area. I already took care of them, so no need to worry.",":Lloyd:Let's go then, we got a new mission from headquarters.","",
                    ":Lloyd:We've arrived at the next town. Let's go look for the guy who was said to have not only defended a Red Square, but also killed that Red Square in front of the RSS. Pretty fitting to find them in The Forsaken.",":You:The Forsaken?",":Lloyd:Yeah, this place is filled with unsavory people. That's where it got its name.",
                    ":You:I see.","",":Lloyd:My instincts tell me that that square over there is the culprit.",":Kuria:Let's go there, then.",":You:Alright.","", ":Square:What is it?", ":Lloyd:Did you defend a Red Square?",":Square:...Oh great. They sent more people after me. I haven't even done anything wrong. Just let me live my life in peace, man.",":Lloyd:Answer my question.",
                    ":Square:...","Fine. Yes, I did, and I killed him afterwards. What does it matter to you guys? You didn't even know him.",":Lloyd:I see, then there is no choice but to take you into custody for what you have done.",":Square:Custody huh... What, you gonna ask why I did it? Well, let me tell you why I did it right now. Then there's no need to take me into custody.",
                    ":Lloyd:You have to be punished for what you've done. Red Squares are evil, defending him makes you just as evil.",":Square:That's not true, they aren't evil. They are just forced to take actions that they don't want to.",":Lloyd:Quit yapping, you aren't fooling anybody. They CHOSE to be Red Squares. They CHOSE to commit evil deeds. Guys, get ready to fight.",
                    ":Kuria:Wait, I think we should hear him out first.",":Lloyd:No way, are you really gonna listen to the gibberish this guy spouts?",":Kuria:Yeah. There's no harm in doing so, after all.",":Lloyd:Fine. Go ahead, spill everything.",":Square:Alright, so I had this prodigious friend, right? He went to challenge the leader of the Red Squares, and you know what happened? He came back as a Red Square!",
                    ":Lloyd:So your friend betrayed you huh...",":Square:That's not true. When I saw him again, he seemed to be suffering. He told me \"Sorry, I couldn't win. Please kill me.\" At first I was shocked, but I quickly came to realize that after losing, he was forced to become a Red Square, and that his mind was slowly being taken over. He was clinging on to the last bit of his consciousness, seeking me to stop him before he did something irreversible.",
                    ":Kuria:So you killed him?",":Square:Yeah, we were interrupted while talking, so I beat up the people trying to fight him. I then did by his request, laying him to eternal sleep.","...If only there was a way to reverse the Red Squarification.",":Lloyd:NONSENSE! ALL RED SQUARES ARE EVIL, AND CHOSE TO BE THIS WAY. _THERE'S NO WAY YOUR STORY IS TRUE.                                  _...and you've just wasted our time. I'm bringing you into custody.","",
                    ":Square:Wow, you guys sure are strong. Not strong enough to beat the boss of the Red Squares, though. What a waste of talent, to be using it on me.",":Lloyd:Shut it, we're going to deliver you to the RSS leaders to lock you up.",":You:...",":Kuria:Is something wrong?",":You:No, nothing.",":Lloyd:Let's go then. We go north through a big maze then east.","",
                    ":Lloyd:We've arrived at our destination.",":Kuria:Is this where the teleport pad is?",":You:Teleport pad?",":Kuria:Yeah, I heard that there are teleport pads scattered around the world to teleport to a specific city.",":Lloyd:Yeah. We are going there.","",":Lloyd:We're here. Let's go turn this guy in.",":Kuria:Wait...", "Isn't this the city that the Illusionist uses as their base of operations?",
                    ":You:Illusionist?",":Kuria:Yeah, nobody knows who they are. You know how we all look the same? Apparently we have a sense that allows us to recognize people, and the Illusionist can mess with it, making them able to change identities, basically.",":You:Wow, that sounds really impressive.",":Kuria:It is impressive, I'd like to meet them and ask how they can do that.","",
                    ":???:!","",":???:You are...",":You:...?","Do I know you?",":???:...","No, its nothing, its just that I noticed that you were the rumored hero.", ":You:Oh..." + "You feel familiar though, are you sure we've never met before?",":???:Maybe you saw me in another town before?",":You:Maybe, but my I have no memories, so I was wondering if I met you somewhere before. This way, maybe I could recover some of my memories.",
                    ":???:...No memories?",":You:Yeah, I don't know what happened before a certain point. You know anything?",":???:...No, nothing.",":You:Oh...","Well, whatever. Thanks, though. Could you tell me your name?",":???:Sorry, I can't. Well, I gotta go, good luck saving the world again.", ":You:Alright, See you around.",":???:See ya.","",":You:...","Good luck saving the world again?","",
                    ":Lloyd:Where were you? We already turned him in.",":You:Sorry about that, I got a little distracted. I've been meaning to ask, but you've been teleporting the Red Squares that we fought before, so why didn't you do the same with him?",":Lloyd:Well...","The RSS gave me a magic item that can only teleport Red Squares, in order to prevent us from abusing this power.",":You:Ohh, So that's how you do it. I thought you teleported them using magic of your own.",
                    ":Kuria:No, teleporting magic is really hard to learn.",":You:Oh, I see. What even happens to them after they get teleported?",":Lloyd:Is this about that guy again? You really shouldn't believe what he says. He just wants to manipulate your emotions because you are so strong.",":You:No, I mean...","Well, I mean it kinda is about him. I just wanted to know.",":Lloyd:There's really no reason to know. We just need to keep fighting. It's a waste of sentiment to worry about Red Squares.",
                    ":Kuria:They are put in confinement where they are studied so we can find out how Red Squarification works. The research might yield groundbreaking discoveries that could give us an upper edge over them.",":Lloyd:...",":Kuria:They research how they think, because their thought process seems to differ greatly from the normal square. They also research the magic used to make them Red Squares, because every Red Square was turned into a Red Square by the leader. At least that's what I heard.",
                    ":Kuria:It is also believed that the magic involved may have relation to the phenomenon known as \"Going Berserk\" and figuring out how the magic works may allow us to prevent the phenomenon from happening.",":You:\"Going Berserk\"? What does that mean?",":Lloyd:It means exactly what it sounds like. A square that has gone berserk loses control of themself, and rampages, attempting to destroy everything. It occurs when a square's emotions go too far in the negatives.",
                    ":You:Won't they just calm down after a while?",":Lloyd:It is different from regular fits of anger. They consume their life energy to power themselves up. That is what makes it so dangerous. Even the average square can destroy whole towns if the town is filled with average squares.",":You:Consumes their life energy? Does that mean...",":Lloyd:Yeah, they die after a while.",":You:!",":You:Is there any way to stop it?",":Lloyd:There is no way to save them. They are good as dead, at least from what I've heard",
                    ":Kuria:No. There is a way.",":Lloyd:There is? I never heard of one.",":Kuria:I did some research on this topic, and talked to researchers about it. There is a way to save them.",":Kuria:You must make them feel like life is worth living.",":You:That's it?",":Kuria:It is not as easy as it may seem. Even if you are the closest square to them, it still won't be easy. Even if you comfort them with soft words, they will remain impaled by the harsh reality. If it were that easy, they wouldn't have went berserk in the first place.",
                    ":You:Oh...",":Lloyd:Forgive me if this seems rude, but why did you research it?",":Kuria:Didn't I tell you guys before?",":Lloyd:No, all you said was that you joined the RSS because you lost everything and that joining the RSS made you feel fulfilled. You never elaborated.",":Kuria:Oh. Sorry about that.","Where do I start...","Alright then...",":Kuria:Everybody in my family had this condition that made them die early. Everybody except for me and my little sister. When our last family member passed away, she starting about how everybody dies. She cried, asking why it happens to us.",
                    ":Kuria:I tried comforting her, but it didn't help. She only got worse and worse. And then she...",":Kuria:She went berserk.", ":Kuria:No matter how much I tried to calm her down, she wouldn't return to normal.",":Kuria:She died shortly after.",":You:...",":Lloyd:...",":Lloyd:That must've been painful. Sorry for bringing that topic up.",":Kuria:It's fine, It was bound to happen sooner or later.",":Kuria:Let's keep going. What is our orders this time?",":Lloyd:Another city nearby was attacked by a mass amount of Red Squares. We've been tasked with taking care of it.",":Kuria:I see. Let's go then.",
                    "",":John:This place looks pretty free from Red Squares, I wonder what happened here.",":You:Hey, it's John!",":John:!","",":John:What are you guys doing here?",":Lloyd:We were tasked with checking out the invasion of Red Squares here but...",":Lloyd:Seems it's been taken care of already.",":Kuria:Did you take care of it, John?",":John:No, I just arrived. Maybe there is still fighting happening in other parts of this city. We should verify if this place is all safe first.",":Lloyd:Alright, how about this? Me and Kuria will go down south, and you guys go up north. We will report back here after we are done.",
                    ":John:I have no objections. What about you?",":You:I have none.",":John:Let's go then.","",":???:*Sniff* Why?",":John:! There's crying coming from over there, let's go over there!","",":John:What's wrong?",":Crying Square:They're gone...",":John:Who?",":Crying Square:I was just ten seconds too late...",":John:...Who died?",":Crying Square:It doesn't concern you. Go away!",":John:...",":You:John is just trying to help you. Can you please tell us what happened here?",":Crying Square:Of course...","Not! Why do you care? You weren't affected by what happened here!"
                    ,"Leave me alone!",":John:We'll leave you alone when you tell us what happened. Go on, you can rant to us.",":Crying Square:...","The Red Squares got my parents...",":John:I see...",":Crying Square:That should be enough, right? Go away now!",":John:Sorry, but we can't do that yet. It's not good to leave someone alone after they've suffered a loss. We'll stay until you feel better.",":Crying Square:Stay until I feel better? What can you guys possibly do to cheer me up? HUH?",":You:Well...",":Crying Square:SHUT UP! YOU DON'T UNDERSTAND HOW I FEEL!","YOU'RE THE HERO! YOU HAVE IT EASY!"
                    ,"EVERYTHING GOES THE WAY YOU WANT IT! I DON'T WANT SOMEBODY LIKE THAT TRYING TO COMFORT ME!",":You:...",":John:...","It's okay, I understand what you're going through. You'll make it past this, believe me. Take some deep breaths and calm yourself down.",":Crying Square:SHUT UP! NONE OF YOU ACTUALLY GET IT!",":John:You're right, I don't know exactly how you feel. But-",":Crying Square:Shut up, shut up, SHUT UP! LEAVE ME ALONE!","",":John:!",":You:What happened?",":John:He's gone berserk...",":John:Run for it!",":You:But...","",":You:Ow...",":John:See what I mean? He's too much for you."
                    ,"He was really strong as a normal square so he's insanely powerful now. I can't fight him while defending you.",":You:But there's a way to save him!",":John:I already know! But it's not gonna work, we can't make it work! We're just some strangers to him! Now run!",":You:But I could provide support! Didn't you say he was insanely powerful?",":John:It'll be fine. I won't lose to him. Besides, he's going to go after everything in his vicinity, meaning you won't be safe here.",":John:If you appear back here and I'm gone, then I'm dead. Forgive me for doing this but...",":You:?","",
                    ":You:Ugh... What was that? My head hurts.","Wait, Where am I?","Maybe I should go ask those Squares over there...","",":You:Hey, do you know where this is?",":Square 2:Where this is? First you left us waiting, then you start blabbering nonsense!",":You:Left you waiting? I wasn't here before. I don't even know you guys. What are you talking about?",":You:WAIT! WHERE IS JOHN?! He was with me just a moment ago...","No, I was with him just a moment ago..",":Square 2:Dunno who John is, but how do you not remember us? How do you forget your own girlfriend?",":Square 1:Exactly, how could you forget her?",
                    ":Square 3:And what about us, who've been with you for so long?",":You:???","Sorry, I really don't know you guys.","You do feel familiar though...",":Square 2:Come on, stop joking around.",
                    ":You:No, I had no memory for a while now.",":Square 2:Huh? Yesterday you were giving a whole speech about how you won't let anybody go through what we did ever again. Come on, nobody would fall for that.",":Square 3:Yeah, seriously, man.",":You:No???",":You:(What's going on? What did John do to me?)",":Square 2:Don't tell me you actually forgot everything...","Do you still remember the day when your brother died?",":You:I had a brother?",":Square 2:!",":Square 1:What do we do? He's in no fighting condition!",":Square 2:No choice but to hold back on that for now.",
                    "Do you seriously know nothing?",":You:Yeah, nothing.",":Square 2:...",":Square 1:Really? What about the first time you used magic to help others? The time when you sparked a revolution by being the first person to use magic?",":You:No...",":Square 1:You seriously forgot such important events?!",":Square 2:Guess we'll stay here a while longer. I'm going to try to see what's wrong with you.",":You:Alright, pl-","" ,":Lloyd:There you are! We've been looking for you everywhere! Where were you?",":You:...!","WHERE IS JOHN?!",":Kuria:Calm down for a moment. We don't know where he is, either. I thought he was with you.",":You:!",":Lloyd:What happened here?",
                    ":You:Hes...","Johns...","Johns dead.","We found this square who was crying, so we talked to him.","But he went berserk.",":Kuria:He went what?",":Lloyd:What happened after?",":You:He was insanely powerful...","I don't know what John did, but the next moment I was in a weird place. After a while, I appeared back here...",":Lloyd:And that's when we found you?",":You:Yeah.","...","Is there a way to resurrect the dead?",":Lloyd:A way to resurrect the dead? Don't tell me you are trying to resurrect John?",":Kuria:I heard of magic that can reconstruct the soul, but nobody seems to know how to perform it.",
                    ":You:That's fine. Thank you for telling me.",":Kuria:Sorry for bursting your bubble, but John is probably long gone now. It is basically impossible to reconstruct his soul now.",":You:But there's a chance.",":Kuria:There really isn't, please don't waste your time by focusing on trying to do it. There have been no records anybody ever being resurrected.",":Lloyd:Yeah. Let's keep going east. If we do, we can eventually reach the front lines where the RSS fights off the Red Squares.","",
                    ":Lloyd:We've arrived at the front lines. Get ready to fight.",":You:Alright.",":Lloyd:Come to think of it, you feel stronger than before, when you we saw you in Parallel City. Do you know why?",":You:I don't know.",":Kuria:Must've been John's special ability, then.",":Lloyd:Yeah, you're probably right...","",":Commander:I'm guessing you guys are the new reinforcements? Don't let us down.",":Lloyd:No worries, we're pretty strong.","",":Commander:Finally, we've won. It has been 90 days since we've last gained ground in this war.",
                    "This battle marks our turning point! Let's finish this war once and for all! The other armies will be overjoyed to hear of this victory!","West we go! Follow me!","",":Lloyd:Let's follow him","",":Commander:We're here. Let's take over this city.","",":Red Square:So it's true. Sorry, but you can't go past this point. I have something I need to do, and you guys won't stop me from doing it.", ":Commander:We have something we need to do too, and YOU aren't going to stop us either.","",":Red Square:Ugh... I can't lose here...","I can't lose it all!","",":Commander:Are you kidding me? This is too much for us to handle, we gotta run for it!",
                    ":You:No, I think we might be able to beat him. After all, he's seems way weaker than the one who was there with John.",":Lloyd:You really think so? Because we likely can't escape unless somebody stays and stalls him.",":Kuria:I think we should attempt to fight, and run as a last resort.",":Lloyd:Alright, let's try it then.",":Commander:You're right, I don't think it'll be possible to just run away. We most likely need to at least weaken him.","","We just can't win...Run for it! I'll try to hold him back as long as possible!",":Kuria:Wait, you can't do that. You're not strong enough to hold it back. I'll do it.",
                    ":You:No.","I'm not running away. I can't leave someone behind again.",":Lloyd:We have no other choice! She's stronger than us physically, we would only be holding her back by staying behind. We gotta go.",":You:But...",":Kuria:It's fine, I just need to keep dodging his attacks. it's something only I can do. Run away while you still can.",":You:...",":Lloyd:There's nothing we can anyways. Let's go.","",":Commander:Reinforcements are here!",":Lloyd:This way.","",":Commander:There's nobody here!",":Lloyd:...",":Commander:It seems reinforcements were too late. My condolences.",":You:I knew we shouldn't have ran. We definitely should've stayed behind."
                    ,":Lloyd:No, we couldn't have done anything. Nothing would've changed.","Let's go. We can't stay here moping around forever.",":Commander:Let's stay here for a while. We need more soldiers to come before we advance.",":Lloyd:The other Red Squares haven't heard of the defeat here yet. We should hurry in my opinion.",":Commander:We don't know what lies ahead. It would be smart to wait a bit. If you want to go ahead, feel free to do so.",":Lloyd:Alright then.","Let's go.",":You:...Alright.","",":Red Guard:Hey! Wait a minute!","","You're not allowed here, only Red Square are. Sorry, but you gotta go.",
                    ":Lloyd:We're not leaving. We will advance forward, and anyone who stands in our way will get no mercy.",":Red Guard:Looks like there is no other choice then.","",":Red Soldier:It's not over yet! There will be more soldiers to stop your advance!","",":Red Soldier:You have no place here! Scram!","",":Red Soldier:Tch, so the legend will come true after all.",":Lloyd:Legend? About how the legendary orange square hero will save the world?",":Red Square:WHAT?! Don't make me laugh! What kind of legend is that?","The legend states that the orange square will destroy the world! And look who came to our lands! The legendary orange square!",
                    "Not only that, they're quite strong. This signifies a future in which the world is destroyed! Do you really support such a dangerous square?!",":Lloyd:Dangerous square? This so called dangerous square is one of the reasons why I'm getting closer to my goal! Why wouldn't I support such square?",":Red Soldier:You'd prioritize your own goals over the world? How selfish can you get?!",":Lloyd:A Red Square like you shouldn't be talking about selfishness. Wasn't the whole point of becoming a Red Square to gain power for yourself?",":Red Soldier:This choice isn't putting anybody but myself in danger. You have no right to criticize this action!",
                    ":Lloyd:Only putting yourself in danger? Don't joke with me! You work for an evil dictator, who brings danger to all! Your claim is baseless!","As such, I have every right to criticize this choice.","And besides, I am not prioritizing my goal over the world. This Orange Square here has high morals, you see. I can achieve both my goal and take steps towards world peace.",":Red Soldier:You're being deceived! World peace? The world is going to get destroyed! There can't be world peace without a world!",":You:I won't destroy the world. I promise that.", ":Red Soldier:You can't be a hundred percent sure. Nobody knows their own future."
                    ,":Lloyd:I think that's enough from you. Time to complete the capture.",":You:!","Wait!",":Lloyd:What, still have something to say?",":You:You don't have to kill them...","They were just doing their jobs like how any Blue Square does it. They don't seem to be bad people, and we can reason with them.",":Lloyd:All Red Square are evil. None are innocent, and they are just barbaric beings who live to hurt others.",":You:I don't think that's true. Come to think of it, these Red Squares can be reasoned with, can't they? If they can be reasoned with, I believe that they aren't all evil.",
                    ":Lloyd:Just because they can talk doesn't make it any better.","Fine, if you insist so, we'll leave them alone. But if any of them attacks us, I'll take them all into custody.","Let's go south.",":You:South? Isn't the leader of the Red Squares to the east?",":Lloyd:Well, to be precise, we're going southeast. It's a little detour, but there's something I need to do there.",":You:...","Your hometown?",":Lloyd:So you noticed, huh. Yeah, it's my hometown. I just have to see what it is like now. My memories from that time are also a bit hazy, so looking at the objects that remain there might bring some memories back.",":You:Alright.","",
                    ":Lloyd:What a wasteland...","Guess they didn't care enough about a small town like this to fix it.",":You:This is your hometown?",":Lloyd:Yeah, it used to look better, though. It used to be a beautiful town. Until those Red Squares attacked.",":You:I see.",":Lloyd:Let's look around. If you find anything interesting, please take it to me.",":You:Alright.",""};
            options = new String[]{};
            optionActions = new int[]{};
            optionStuff = new int[][]{};
            switchLocations = new int[]{4,5,8,11,12,13,14,17,18,19,20,21,27,28,29,32,33,34,35,36,37,41,42,43,48,49,50,51,52,53,55,56};
            switchNames = new String[]{"???","You","Square","You","Square","You","Square","You","Square","You","Square","You","Rss Leader","You","Rss Leader","You","Kuria","Lloyd","You","Kuria","Lloyd","Kuria","Lloyd","Kuria","Lloyd","You","Lloyd","You","Lloyd","You","Lloyd","John"};
            indexesChange = new int[]{6,15,22,25,31,39,44,46,54,58,63,67,90,97,103,111,113,130,133,183,187,197,200,229,235,245,249,281,308,315,318,322,324,326,329,332,339,349,352,365,367,371,373,375,408};
            newIndexes    = new int[]{7,16,23,26,32,40,45,47,55,59,64,68,91,98,104,112,114,131,134,184,188,198,201,230,236,246,250,282,309,316,319,323,325,327,330,333,340,350,353,366,368,372,374,376,409};
            checkLines();
        }
        else if(type.equals("Square")){
            displayName = "Square";
            lines = new String[]{"Help me!","","I hope you manage to find something that will help you regain your memories.",""};
            newIndexes = new int[]{2};
        }
        else if(type.equals("swordMaster")){
            accessories = new int[]{0};
            displayName = "Retired Fighter";
            lines = new String[]{"Hello there!","Would you like to learn how to concentrate your energy into flying slashes? I'll teach you for just 10 coins!",
                    "select","Alright.","","You learned the skill \"" + game.skillList()[0] + "\"!","","You don't have enough coins.","","Would you like to learn how to concentrate your energy into a shield? I'll teach you for another 10 coins!",
                    "select","Alright.", "","You learned the skill \"" + game.skillList()[4] + "\"!","","You don't have enough coins.","",
                    "I have nothing more to teach you :)",""};
            options = new String[]{"Yes","No"};
            optionActions = new int[]{3,1};
            optionStuff = new int[][]{{2,10},{2,2},{0,0}};
            requirementAdds = new int[]{2,2};
            requirements = new String[]{"haveCoins10","haveCoins10"};
//            switchLocations = new int[]{5,9,13,17};
//            switchNames = new String[]{"","Retired Fighter","","Retired Fighter"};
            indexesWithRequirements = new int[]{5,13};
            indexesChange = new int[]{6,14};
            newIndexes = new int[]{9,17};
            indexesGain = new int[]{5,13};
            gainWhat = new String[]{"skill0","skill4"};
            destinationX = new int[]{200,325};
            destinationY = new int[]{175,175};
            wait = new int[]{120,120};
        }else if(type.equals("rssLeader1")){
            accessories = new int[]{0};
            displayName = "Rss Leader";
            lines = new String[]{"Hello There!","Now that you've joined, I'd like to take a mission for me.",
                    "select","Alright.","","Okay, well first you must demonstrate your ability. Defeat five Red Squares, then report back to me.","","You did it? Alright, here is your reward.","",
                    "You have yet to defeat five Red Squares.","","You seem to know how to fight, at the least. Would you like to learn how to break your limits, boosting power, but lowering defense?",
                    "select","Okay.","","You learned skill \"" + game.skillList()[6] + "\" !","","In battle, sometimes there are risks you need to take. I'll teach you a skill to help you in these situations after you have defeated 10 more Red Squares","",
                    "Good job.","You learned new skill \"" + game.skillList()[5] + "\"!","","You have yet to defeat ten Red Squares","","Good luck on your travels!",""};
            options = new String[]{"Yes","Not now","Yes","No"};
            optionActions = new int[]{3,1,3,1};
            optionStuff = new int[][]{{2,12},{2,2},{0,2}};
            requirementAdds = new int[]{2,3};
            requirements = new String[]{"quest0","quest1"};
            indexesWithRequirements = new int[]{7,19};
            indexesChange = new int[]{6,8,16,18,21};
            newIndexes = new int[]{7,11,17,19,24};
            indexesGain = new int[]{15};
            gainWhat = new String[]{"skill6","skill5"};
            questIndexes = new int[]{6,18};
            quests = new Quest[]{new Quest("Rss test"), new Quest("An errand")};
            switchLocations = new int[]{15,17,20,24};
            switchNames = new String[]{"","Rss Leader","","Rss Leader"};
        } else if(type.equals("IK")){
            displayName = "Square of Fire";
            lines = new String[]{"Do you desire the power of fire? I don't need it anymore...",
                    "select","Alright.","","Okay then, I'll give it to you if you could do something for me. I left my ribbon lying around somewhere. If you could find it for me, I'll give you the power of fire",
                    "","Thank you so much for finding my ribbon!","Here is your reward.","","Please find my ribbon, it means a lot to me.","",":Square Of Fire:Thank you so much for finding my ribbon!",""};
            options = new String[]{"Yes","No"};
            optionActions = new int[]{3,1};
            optionStuff = new int[][]{{1},{2},{0}};
            requirementAdds = new int[]{3};
            requirements = new String[]{"quest0"};
            indexesWithRequirements = new int[]{6};
            indexesChange = new int[]{5,8};
            newIndexes = new int[]{6,11};
            indexesGain = new int[]{7,7};
            gainWhat = new String[]{"skill1","passive1"};
            questIndexes = new int[]{5};
            quests = new Quest[]{new Quest("Find a ribbon")};
            destinationX = new int[]{450,575};
            destinationY = new int[]{625,625};
            wait = new int[]{1,1};
        }
        else if(type.equals("announcer")){
            displayName = "";
            lines = new String[]{"This is a box.","select","You left it alone.","","You broke it open.","",
                    "You found a ribbon on the floor.","","You found a piece of bread on the floor.","",
                    "This is a box.","select","You left it alone.","","You found a coin inside of it. Was it worth destroying somebody's property?","",
                    "This is a teleport pad. Activate?","select","Verifying Access...","Action authorized.","","You chose not to activate.","","",
                    "This is a teleport pad. Activate?","select","Verifying Access...","Action unauthorized. If you are a member of the Red Square Suppression Squad, get the leader of your squad to help.","","You chose not to activate.","","",
                    "This is broken box.","","","This is a ripped up piece of paper. Read it?","select","You make out the following:","Today... so fun! I got... straw hat from... I'll treasure... always... Never forget... Lloyd.","","You decided to leave it alone.","",
                    "This is a ripped paper. Read it?","select","It appears to be part of a newspaper. The headline is barely legible. You read:","\"President's son John figured out how to use magic! With this, the barbaric servant revolt can be put down!\"", "",
                    "You decided to ignore the paper.","","","This is a ripped paper. Read it?","select","It appears to be a part of a newspaper. A big chunk of the text is legible. You read:",
                    "\"Ever since that day, our servants have been fighting back using magic. At first it was thought that they weren't actually human. However, after appropriate research was done, we've realized that exposure to magic can enable us to use magic, too.\"","",
                    "You decided to ignore the paper.",""};
            options = new String[]{"Break it open","Leave it alone","Yes","No"};
            optionActions = new int[]{3,1,1,4};
            optionStuff = new int[][]{{1,11,17,25,36,43,51},{2,2,2,2,2,2,2},{0,0,2,2,2,2,2}};
            newIndexes = new int[]{0,6,8,10,16,24,32,35,42,50};
            indexesGain = new int[]{15,20};
            gainWhat = new String[]{"coins1","teleportMX50MY35XX36YY375"};
            System.out.println();
            for(int i = 0; i < lines.length; i++){
                if(lines[i].equals("select")){
                    System.out.print(i + " ");
                }
            }
            System.out.println();
            checkLines();
        }
        else if(type.equals("kuria")){
            accessories = new int[]{3};
            displayName = "Kuria";
            lines = new String[]{"I heard that you lost all your memories. I hope you will find them again.","select","No problem.","",
                    "Well, theres nothing else for me to do. Don't got family or friends, no passions or anything. Helping people is the only way I've found satisfaction in my life.","",
                    "Not bad for our first mission.","","John beat us to the punch, huh...","","Don't worry about me. I'm fine.","","We're here for you if you need it.",""};
            options = new String[]{"What made you join the RSS?","Thank you."};
            optionActions = new int[]{3,1};
            optionStuff = new int[][]{{1},{2},{0}};
            newIndexes = new int[]{6,8,10,12};
        }
        else if(type.equals("lloyd")){
            accessories = new int[]{4};
            displayName = "Lloyd";
            lines = new String[]{"Are you ready to go?","select","Alright, take your time.","","I lost my family to a bunch of Red Squares, if I remember correctly. I have to get my revenge on them.","",
                    "Alright, let's go then. It is to the east.","","You are quite good at fighting!","","Some of my memories are also hazy... I wonder if John could help me with that.","","...","",
                    "We'll go when you are ready.",""};
            options = new String[]{"What made you join the RSS?","Yes","Not yet"};
            optionActions = new int[]{3,5,1};
            optionStuff = new int[][]{{1},{3},{0}};
            indexesGain = new int[]{7};
            gainWhat = new String[]{"npcLeave0"};
            newIndexes = new int[]{8,10,12,14};
        }
        else if(type.equals("noviceMage")){
            accessories = new int[]{1};
            displayName = "Novice Mage";
            lines = new String[]{"Bro, I just learned how to call down lightning strikes from the sky! Wanna learn how? You just need 5 stat points into magic!","select","Okay.","",
                    "Here ya go.","","You don't have the magical aptitude to learn this.","","You are welcome.",""};
            options = new String[]{"Yes","No"};
            optionActions = new int[]{3,1};
            optionStuff = new int[][]{{1},{2},{0}};
            requirementAdds = new int[]{2};
            requirements = new String[]{"haveMagic5"};
            indexesWithRequirements = new int[]{4};
            indexesChange = new int[]{5};
            newIndexes = new int[]{8};
            indexesGain = new int[]{5};
            gainWhat = new String[]{"skill2"};
        }
        else if(type.equals("Y")){
            displayName = "Vampire";
            lines = new String[]{"Do you wish to learn how to condense your lightning magic into a thunder dragon?","select",
                    "Okay.","","Here you go.","","...","","Have fun using it.",""};
            options = new String[]{"Yes","No"};
            optionActions = new int[]{3,1};
            optionStuff = new int[][]{{1},{2},{0}};
            requirementAdds = new int[]{6,6};
            requirements = new String[]{"haveMagic10","haveSkill2"};
            indexesWithRequirements = new int[]{0,0};
            indexesChange = new int[]{5};
            newIndexes = new int[]{8};
            indexesGain = new int[]{4};
            gainWhat = new String[]{"skill7"};
//            switchNames = new String[]{"","Vampire"};
//            switchLocations = new int[]{4,8};
        }
        else if(type.equals("john")){
            displayName = "John";
            lines = new String[]{"I know you are not going to believe me, but we weren't squares before. I swear.","Everybody thinks that I'm crazy. But, I have traveled back in time before.","You know what I saw?","Depictions of us not as squares, but as something called \"Humans\".","We were fighting a war at the time.",
                    "I was on the wrong side.","The whole reason why I joined the Rss was to make up for my wrongdoings.","Please trust me.",""};
        }
        else if(type.equals("fillerGuy1")){
            displayName = "Square";
            lines = new String[]{"You should open some boxes.","select","You can find stuff inside.","","\uD83D\uDE32",""};
            options = new String[]{"I already do.","..."};
            optionActions = new int[]{3,1};
            optionStuff = new int[][]{{1},{2},{0}};
        }
        else if(type.equals("fillerGuy2")){
            displayName = "Square";
            lines = new String[]{"Would you be now?","select","Nah, I'd code.","","\uD83D\uDC4D",""};
            options = new String[]{"Yes","???"};
            optionActions = new int[]{3,1};
            optionStuff = new int[][]{{1},{2},{0}};
        }
        else if(type.equals("tipGuy1")){
            displayName = "Square";
            lines = new String[]{"You should totally move right, press the space bar 3 times, then use the dash skill. Trust.","","I got a tip for you, but you don't have the slash ability yet :(", ""};
            requirementAdds = new int[]{2};
            requirements = new String[]{"haveSkill0"};
            indexesWithRequirements = new int[]{0};
        }
        else if(type.equals("tipGuy2")){
            displayName = "Square";
            lines = new String[]{"Learning some moves unlocks new combos! So you should learn everything!!!",""};
        }
        else if(type.equals("unfortunateSquare1")){
            displayName = "Square";
            lines = new String[]{"What a waste...",""};
        }
        else if(type.equals("gf")){
            displayName = "???";
            switchNames = new String[]{"???","Square 2"};
            //lines = new String[]{"",""};
        }
        else if(type.equals("swordProfessional")){
            displayName = "Old Square";
            lines = new String[]{"I haven't seen another square in years...","Do you want some training, young one? I bet I can still be of use to you.","select","Here you go.","","Alright","","Good luck on your travels!",""};
            options = new String[]{"Yes","No"};
            optionActions = new int[]{1,3};
            optionStuff = new int[][]{{2},{2},{0}};
            indexesChange = new int[]{4};
            newIndexes = new int[]{7};
            indexesGain = new int[]{4};
            gainWhat = new String[]{"passive2"};
        }
        else if(type.equals("duck")){
            displayName = "Duck";
            lines = new String[]{"Quack.","I'm cravin' bread. Could you get some for me?","select","Alright :(","","Thank you very much!","", "Thanks for the bread! Here is your reward.","","Bread...",""};
            options = new String[]{"Yes","No"};
            optionActions = new int[]{3,1};
            optionStuff = new int[][]{{2},{2},{0}};
            indexesChange = new int[]{6};
            newIndexes = new int[]{7};
            indexesGain = new int[]{8};
            gainWhat = new String[]{"skill11"};
            requirementAdds = new int[]{2};
            requirements = new String[]{"quest0"};
            indexesWithRequirements = new int[]{7};
            questIndexes = new int[]{5};
            quests = new Quest[]{new Quest("Find some bread")};
        }
        else if(type.equals("johnFan1")){
            displayName = "Square";
            lines = new String[]{"Did you hear? I heard that John was seen nearby, I gotta find him!",""};
        }
        else if(type.equals("johnFan2")){
            displayName = "Square";
            lines = new String[]{"I just have to ask John for an autograph!",""};
        }
        else if(type.equals("unfortunateSquare2")){
            displayName = "Crying Square";
            lines = new String[]{"Me when I have no lines:",""};
        }
        else if(type.equals("friend1")){
            displayName = "Square 1";
        }
        else if(type.equals("friend2")){
            displayName = "Square 3";
        }
        else if(type.equals("sorcerer")){
            displayName = "Sorcerer";
            lines = new String[]{"Ah yes, the offscreening technique I haven't used since the human era.",""};
        }
        else if(type.equals("seller")){
            displayName = "Square";
            lines = new String[]{"Man, there is so much empty space here. I know what this place needs! A SkillMart!","Please visit me when I build it up! Here is a skill for you for now.","","How do I go about this?",""};
            indexesGain = new int[]{2};
            gainWhat = new String[]{"skill10"};
            indexesChange = new int[]{2};
            newIndexes = new int[]{3};
        }
        else if(type.equals("soldier1")){
            displayName = "Commander";
            lines = new String[]{"We must put an end to this war. We must crush those loathsome Red Squares and restore peace to the world.",""};
        }
        else if(type.equals("you")){
            displayName = "You";
            lines = new String[]{"Ow...What was that? A prophecy?","Better not follow it.","","I just had a very vivid nightmare...",""};
            indexesGain = new int[]{0};
            gainWhat = new String[]{"random03"};
        }
        else if(type.equals("redSquare1")){
            displayName = "Red Square";
            lines = new String[]{};
        }
        else if(type.equals("redGuard1")){
            displayName = "Red Guard";
            lines = new String[]{"This isn't your city. Get out.","","I sure hope I'm wrong...",""};
        }
    }
    public void switchName(int index){
        displayName = switchNames[index];
    }
    public void setAnnouncerItem(Item item){announcerItem = item;}
    public void checkLines(){ //not used for gameplay
        for(int i = 0; i < lines.length; i++){
            if(lines[i].equals("")){
                System.out.print(i + " ");
            }
        }
    }
    public void switchIndexes(int index){
        chatIndex = newIndexes[index];
    }
    public void setCurrentCooldown(int skillNum, float cooldown){currentCooldowns[skillNum]=cooldown;}
    public int getCurrentCooldown(int skillNum){return (int)currentCooldowns[skillNum];}
    public float getRealCurrentCooldown(int skillNum){return currentCooldowns[skillNum];}
    public int getCooldown(int skillNum){return cooldowns[skillNum];}
    public void skill(int skillNum){
        skill[skillNum]=true;
    }
    public void skillDone(int skillNum){
        skill[skillNum]=false;
    }
    public boolean inSkill(int skillNum){
        return skill[skillNum];
    }
    public int cooldown(int num){return cooldowns[num];}
    public int getEnergyCost(int num){return energyCosts[num];}
    public int getEnergy(){return energy;}
    public boolean cooldownDone(int num){return cooldownDone[num];}
    public void cooldowned(int num){cooldownDone[num] = true;}
    public void onCooldown(int num){cooldownDone[num] = false;}
    public void useEnergy(int cost){energy-=cost;}
    public void useUpEnergy(){energy=0;}
    public void recoverEnergy(){
        if(!skill[0] && !skill[1] && !skill[2] && !skill[3]){
            energy+=5;
            if (energy > 100) {
                energy=100;
            }
        }
    }
    public void recoverEnergy(int amount){
        energy+=amount;
        if (energy > 100) {
            energy=100;
        }
    }
    public String getSkill(int num){
        if(skillSet!=null) return skillSet[num];
        return "";}
    public String getPassive(int num){return passivesEquipped[num];}
    public boolean inAttack(){return inAttack;}
    public void attack(){inAttack=true;}
    public void attackDone(){inAttack=false;}
    public void getBurned(int duration, int damage, Person attacker){
        if(!invincible) {
            if (!burned) {
                burned = true;
                burnCounter = 0;
                burnEnd = duration * 100;
            } else if (burnCounter > 100) {
                burnCounter %= 100;
            }
            burnDamage = damage;
            if(attacker.passiveEquipped(1)){
                burnDamage*=2;
            }
        }
    }
    public boolean isBurned(){
        return burned;
    }
    public boolean isStunned(){return stunned;}
    public boolean isBleeding(){return bleeding;}
    public void burn(){
        if(burned){
            if(burnCounter%100 == 0 && burnCounter!=0){
                takeDamage(burnDamage,null);
            }
            burnCounter++;
            if(burnCounter>burnEnd){
                burned = false;
            }
        }
    }
    public void getStunned(double duration){
        if(!invincible && (!type.contains("Boss"))) {
            stunned = true;
            stunCounter = 0;
            stunEnd = (int)(duration * 100);
            if(type.equals("player")){
                stunEnd/=2;
            }
            if(this!=game.getPlayer()){
                inAttack = false;
            }
        }
    }
    public void stun(){
        if(stunned){
            stunCounter++;
            if(stunCounter>stunEnd){
                boolean b = false;
                for(int i = 0; i < 4; i++){
                    if(inSkill(i)){
                        b = true;
                    }
                }
                if(!inAttack && !b) {
                    stunned = false;
                }
            }
        }
    }
    public void startBleeding(int duration){
        if(!invincible) {
            bleeding = true;
            bleedCounter = 0;
            bleedEnd = duration * 100;
        }
    }
    public void bleed(){
        if(bleeding){
            bleedCounter++;
            speed = 4;
            if(bleedCounter>bleedEnd){
                bleeding = false;
                speed = baseSpeed;
            }
        }
    }
    public int getxPos(){
        if(this== game.getPlayer()){
            return xPos;
        }
        return xPos + mapX * 1375 + game.getGameScreen().getScrollX();
    }
    public int getyPos(){
        if(this== game.getPlayer()){
            return yPos;
        }
        return yPos + mapY * 775 + game.getGameScreen().getScrollY();
    }
    public int xPosRelativeToMapX(int x){
        return -(x * 1375) - game.getGameScreen().getScrollX() + getxPos();
    }
    public int yPosRelativeToMapY(int y){
        return -(y * 775) - game.getGameScreen().getScrollY() + getyPos();
    }
    public int truexPos(){
        return xPos%1375;
    }
    public int trueyPos(){
        return yPos%775;
    }
    public int trueMapX(){return mapX + xPos/1375;}
    public int trueMapY(){return mapY + yPos/775;}
    public int getMapX(){return mapX;}
    public int getMapY(){return mapY;}
    public int getDetectionRange(){return detectionRange;}
    public int getHpPercent(){return (int)((100*hp)/maxHp);}
    public double getRawHpPercent(){return (100.0*hp)/maxHp;}
    public int getHp(){return (int)hp;}
    public int getHpStat(){return hpStat;}
    public int getPhysicalStat(){return physicalStat;}
    public int getMagicStat(){return magicStat;}
    public int getStatPoints(){return statPoints;}
    public int getMaxHpBefore(){return 100+hpStat*50+physicalStat*5;}
    public int getSwordBefore(){return 5+physicalStat*2+magicStat;}
    public int getMagicBefore(){return 5+magicStat*2;}
    public int getDefenseBefore(){return physicalStat/5 + hpStat/3;}
    public int getSword(){return sword;}
    public int getMagic(){return magic;}
    public int getDefense(){return defense;}
    public int getMaxHp(){return maxHp;}
    public int getLevel(){return level;}
    public int getXpPercent(){return (100*xp)/xpNeeded;}
    public int getXp(){return xp;}
    public int getXpNeeded(){return xpNeeded;}
    public double getMoodLevel(){return moodLevel;}
    public boolean movable(){
        return canMove && !stunned && !game.isInStory();}
    //public boolean attackAble(){return canMove && !stunned && !game.isInStory();}
    public void test(){
        System.out.println(canMove);
        System.out.println(stunned);
        System.out.println(game.isInStory());
    }
    public boolean held(){
        for(boolean b : held){
            if(b){
                return true;
            }
        }
        return false;
    }
    public void cantMove(){canMove=false;}
    //System.out.println("can't move");}
    public void canMove(){canMove=true;}
    //System.out.println("can move");}
    public void moveX(int x){xPos += x;}
    public void moveY(int y){yPos += y;}
    public void changeMood(double mood){moodLevel+=mood;}
    public void heal(){
        if(passiveEquipped(0)){
            hp += (double) maxHp / 100;
        }
        if(passiveEquipped(1)){
            if(reserveHp<200){
                reserveHp+=(int)Math.pow(2, (double) reserveHpCount /4);
            }
            reserveHpCount++;
            if(reserveHpCount > 20){
                reserveHpCount = 20;
            }
        }
        if(mode==2){
            reserveHp+=200;
            if(reserveHp>200){
                reserveHp=200;
            }
        }
        if(hp>maxHp){
            hp=maxHp;
            healing = false;
        }
        if(hp==maxHp){
            healing=false;
        }
    }
    public void heal(int h){
        hp += (double) maxHp * h / 100;
        if(hp>maxHp){
            hp=maxHp;
            healing = false;
        }
        if(hp==maxHp){
            healing=false;
        }
    }
    public void reserveHeal(){
        if(hp < maxHp){
            healing = true;
        }
        if(reserveHp > 3 && hp < maxHp && healing){
            reserveHp-=4;
            hp+=(double)maxHp/100;
        }
    }
    public void gainHp(double amt){
        hp+=amt;
        if(hp > maxHp){
            hp = maxHp;
        }
    }
    public void setDirection(String dir){
        if(dir.equals("left") || dir.equals("right")){
            if(direction.equals("up") || direction.equals("down")){
                lastDirection = direction;
            }
        }
        if(dir.equals("up") || dir.equals("down")){
            if(direction.equals("left") || direction.equals("right")){
                lastDirection = direction;
            }
        }
        direction = dir;
    }
    public void setType(String s){
        type=s;
    }
    public String getLine(int num){
        if (!(indexesChange == null)) {
            for(int i = 0; i < indexesChange.length; i++){
                //System.out.println("one");
                if(indexesChange[i]==num){
                    chatIndex = newIndexes[i];
                }
            }
        }
        lineNum = num;
        if(requirements!=null){
            for(int i = 0; i < requirements.length; i++){
                //System.out.println("two");
                if(indexesWithRequirements[i]==lineNum){
                    if(requirements[i].contains("haveCoins")){
                        if(game.checkPlayerMoney() < Integer.parseInt(requirements[i].substring(9))){
                            add(optionNum, requirementAdds[i]);
                        }
                        else{
                            game.spendMoney(Integer.parseInt(requirements[i].substring(9)));
                            indexesWithRequirements[i] = -1;
                        }
                    }
                    else if(requirements[i].contains("haveMagic")){
                        if(game.getPlayer().getMagicStat() < Integer.parseInt(requirements[i].substring(9))){
                            add(optionNum, requirementAdds[i]);
                        }
                        else{
                            indexesWithRequirements[i] = -1;
                        }
                    }
                    else if(requirements[i].contains("quest")){
                        quests[i].checkQuest();
                        if(!quests[Integer.parseInt(requirements[i].substring(5))].deleteQuest()){
                            add(optionNum, requirementAdds[i]);
                        }
                    }
                    else if(requirements[i].contains("haveSkill")){
                        if(!game.getPlayer().skillsContain(Integer.parseInt(requirements[i].substring(9)))){
                            add(optionNum, requirementAdds[i]);
                        }
                        else{
                            indexesWithRequirements[i] = -1;
                        }
                    }
                }
            }
        }
        if(!(switchLocations == null)){
            for(int i = 0; i < switchLocations.length; i++){
                //System.out.println("three");
                if(switchLocations[i] == lineNum){
                    displayName = switchNames[i];
                }
            }
        }
        if(indexesGain!=null){
            for(int i = 0; i < indexesGain.length; i++){
                //System.out.println("four");
                if(indexesGain[i] == lineNum){
                    if(gainWhat[i].contains("skill")){
                        indexesGain[i] = -1;
                        game.getPlayer().obtainSkill(Integer.parseInt(gainWhat[i].substring(5)));
                    }
                    if(gainWhat[i].contains("passive")){
                        indexesGain[i] = -1;
                        game.getPlayer().obtainPassive(Integer.parseInt(gainWhat[i].substring(7)));
                    }
                    if(gainWhat[i].contains("npcLeave")){
                        game.npcLeave(Integer.parseInt(gainWhat[i].substring(8)));
                    }
                    if(gainWhat[i].contains("coins")){
                        indexesGain[i] = -1;
                        game.earnMoney(Integer.parseInt(gainWhat[i].substring(5)));
                    }
                    if(gainWhat[i].contains("teleport")){
                        String tele = gainWhat[i];
                        game.getGameScreen().goToMapXY(Integer.parseInt(tele.substring(10,tele.indexOf("MY"))),Integer.parseInt(tele.substring(tele.indexOf("MY")+2,tele.indexOf("XX"))));
                        game.getGameScreen().setScrollXY(Integer.parseInt(tele.substring(10,tele.indexOf("MY"))),Integer.parseInt(tele.substring(tele.indexOf("MY")+2,tele.indexOf("XX"))),true);
                        game.getPlayer().goToXY(Integer.parseInt(tele.substring(tele.indexOf("XX")+2,tele.indexOf("YY"))),Integer.parseInt(tele.substring(tele.indexOf("YY")+2)));
                        game.getGameScreen().mapChange();
                    }
                    if(gainWhat[i].contains("random")){
                        String rand = gainWhat[i].substring(6);
                        int[] nums = new int[rand.length()];
                        int count = 0;
                        while(rand.length()>0){
                            //System.out.println("five");
                            nums[count] = Integer.parseInt(rand.substring(0,1));
                            rand = rand.substring(1);
                            count++;
                        }
                        int random = (int)(Math.random() * nums.length);
                        lineNum = nums[random];
                    }
                }
            }
        }
        if(questIndexes!=null){
            for(int i = 0; i < questIndexes.length; i++){
                //System.out.println("six");
                if(questIndexes[i] == lineNum) {
                    questIndexes[i] = -1;
                    game.getQuests().add(quests[i]);
                }
            }
        }
        if(type.equals("announcer")){
            for(int i = 0; i < newIndexes.length; i++){
                //System.out.println("seven");
                if(lineNum == newIndexes[i]-1){
                    announcerItem.remove();
                }
            }
        }
        String theLine = lines[lineNum];
        if(theLine.length()>1) {
            if (theLine.substring(0, 1).equals(":")) {
                displayName = "";
                for (int i = 1; !theLine.substring(i, i + 1).equals(":"); i++) {
                    //System.out.println("eight");
                    displayName += theLine.substring(i, i + 1);
                }
                theLine = theLine.substring(displayName.length() + 2);
            }
        }
        return theLine;
    }
    public String getOptionLine(int num) {
        return options[num];
    }
    public int getOptions(){
        return optionNum;
    }
    public int getOptions(int num){
        for(int i = 0; i < optionStuff[0].length; i++){
            //System.out.println("nine");
            setOptions(num,optionStuff[0][i],optionStuff[1][i],optionStuff[2][i]);
        }
        return optionCount;
    }
    private void setOptions(int num, int i, int options, int option){
        if(num==i){
            optionCount=options;
            optionNum=option;
        }
    }
    private void add(int i, int add){
        if(optionNum==i){
            lineNum+=add;
            game.getGameScreen().changeTextPart(lineNum);
        }
    }
    public int select(int num){
        optionNum+=num;
        for(int i = 0; i < optionActions.length; i++){
            //System.out.println("ten");
            add(i,optionActions[i]);
        }
        return lineNum;
    }
    public int getLineNum(){return lineNum;}
    public String getDirection(){
        return direction;
    }
    public String getType(){return type; }
    public String getDisplayName(){return displayName;}
    public void takeDamage(int damage, Person attacker){
        if((attacker==game.getPlayer() || attacker==null) &&game.isInStory()){

        }
        else {
            aggro = true;
            if (!invincible) {
                game.getAttacks().add(new Attack("hitEffect", false, this));
                tookDamage = true;
                double damageDone = (damage * (cuts / 100.0 + 1));
                if (attacker != null) {
                    if (attacker.passiveEquipped(3)) {
                        damageDone *= 1.5;
                    }
                    if (attacker.passiveEquipped(4)) {
                        if (damageDone > defense) {
//                        if((damageDone-defense)/100 < 1){
//                            attacker.gainHp(1);
//                        }
//                        else {
                            attacker.gainHp((damageDone - defense) / 100);
//                        }
                        }
                    }
                }
                damageDone -= defense;
                if (damageDone < 0) {
                    damageDone = 0;
                    tookDamage = false;
                }
                if (tookDamage) {
                    reserveHpCount *= 0.8;
                    if (reserveHpCount < 1) {
                        reserveHpCount = 1;
                    }
                }
                hp -= damageDone;
                checkDead();
            }
        }
    }
    public void recoilDamage(int damage){
        hp-=damage;
        checkDead();
    }
    private void checkDead(){
        if(hp <= 0){
            if(specialRole!=null) {
                if (specialRole.equals("unkillable")) {
                    hp = 1;
                    reserveHp = 1000;
                    if(passivesEquipped==null) {
                        addPassives(game.passiveList()[4]);
                    }
                    else{
                        equipPassive(4,0,game.passiveList());
                    }
                }
            }
        }
        if (hp <= 0) {
            if(gambit && !gambitSucceed){
                hp = maxHp/5;
                gambitSucceed=true;
            }
            else if(passiveEquipped(1) && energy>=75 && !game.isInStory()){
                energy-=75;
                hp = 5;
                reserveHpCount = 20;
                reserveHp += 135;
            }
            else if(passiveEquipped(1) && reserveHp > Math.abs(hp)/((double) maxHp /100) * 4){
                reserveHp-= (Math.abs(hp)/((double) maxHp /100) * 4);
                hp = 1;
            }
            else{
                if(type.equals("player")) {
                    burned = false;
                    stunned = false;
                    bleeding = false;
                    canMove = false;
                    if(game.isInStory()){
                        hp = 1;
                        canMove = true;
                    }
                    else if(game.getStoryLocation()==32){
                        hp = 1;
                        canMove = true;
                        game.advanceStory();
                        game.enterStory();
                    }
                    else {
                        invincible = true;
                        deadCounter = 1;
                        dead = true;
                    }
                } else if (!type.contains("dead")) {
                    type = "dead";
                    for(int i = 0; i < game.getQuests().size(); i++){
                        //System.out.println("eleven");
                        game.getQuests().get(i).enemyDefeated();
                    }
                }
            }
        }
    }
    public void losePercentHp(double percent){
        if(hp > maxHp * percent /100){
            hp-= maxHp * percent /100;
        }
    }
    public boolean tookDamage(){
        boolean temp = tookDamage;
        tookDamage = false;
        return temp;
    }
    public boolean canGetHurt(){return !invincible;}
    public boolean didGambitWork(){
        return gambitSucceed;
    }
    public void toggleGambit(){
        gambit=!gambit;
        gambitSucceed=false;
    }
    private void specialRoleCheck(){
        if(specialRole!=null) {
            if (specialRole.contains("advanceStory")) {
                game.forceExit();
                game.advanceStory();
                game.enterStory();
            }
            if (specialRole.contains("swap")) {
                String s = specialRole.substring(specialRole.indexOf("swap")+4);
                int index = Integer.parseInt(s);
                game.getNpcList().get(index).goToPerson(this,0,0);
            }
            if(specialRole.contains("darkScreen")){
                game.getGameScreen().darkScreen();
            }
        }
    }
    public void getXp(int exp){
        xp+=exp;
        game.getEffects().add(new Attack("gainExp" + exp,false,this));
        if (xp>=xpNeeded){
            level++;
            xp=0;
            xpNeeded*=1.1;
            statPoints+=3;
            game.getEffects().add(new Attack("levelUp",true,this));
        }
    }
    public void increaseStat(String str){
        if (statPoints>0) {
            if (str.equals("physical")) {
                physicalStat++;
                statPoints--;
            }
            else if (str.equals("hp")) {
                hpStat++;
                statPoints--;
            }
            else if (str.equals("magic")) {
                magicStat++;
                statPoints--;
            }
        }
    }
    /**Postcondition: Stats have multipliers applied to them*/
    public void setStats(){
        int physicalStat = this.physicalStat;
        int magicStat = this.magicStat;
        int hpStat = this.hpStat;
        if(passiveEquipped(7)){
            physicalStat+=magicStat/2;
            hpStat+=magicStat/2;
            if(magicStat%2==1){
                physicalStat++;
            }
        }
        maxHp=100+hpStat*50+physicalStat*5;
        sword=(int)((5+physicalStat*2+(magicStat*(1+moodLevel/20)))*attackBooster);
        magic=(int)((5+(magicStat*2*(1+moodLevel/20)))*attackBooster);
        defense=(physicalStat/5 + hpStat/3) /attackBooster;
        if(passiveEquipped(3)){
            sword*=1.5;
            magic*=1.5;
        }
        if(passiveEquipped(6)){
            maxHp*=3;
            defense*=3;
        }
        maxHp+=statBonus*maxHp;
        sword+=statBonus*sword;
        magic+=statBonus*magic;
        defense+=statBonus*defense;
        if(berserk()){
            maxHp *= 10;
            sword *= 10;
            magic *= 10;
            defense *= 10;
        }
        if(mode==1){
            sword*=2;
        }
        if(mode==2){
            sword*=1.25;
            magic*=1.25;
        }
    }
    public void setAttackBooster(int booster){
        attackBooster = booster;
    }
    public int getChatIndex(){return chatIndex;}

    public String getLastDirection() {
        return lastDirection;
    }

    public void setLastDirection(String lastDir) {
        lastDirection = lastDir;
    }
    public void turnInvincible(){
        invincible = true;
    }
    public void notInvincible(){
        invincible = false;
    }
    /**Precondition: skillsKnown must not be null*/
    public boolean skillsContain(int skillNum){
        for (String skill : skillsKnown) {
            if (skill.equals(game.skillList()[skillNum])) {
                return true;
            }
        }
        return false;
    }
    public boolean passivesContain(int passiveNum){
        for (String passive : passivesKnown) {
            if (passive.equals(game.passiveList()[passiveNum])) {
                return true;
            }
        }
        return false;
    }
    public boolean passiveEquipped(int passiveNum){
        if(passivesEquipped!=null) {
            for (String passive : passivesEquipped) {
                if (passive.equals(game.passiveList()[passiveNum])) {
                    return true;
                }
            }
        }
        return false;
    }
    public boolean skillSetContain(int skillNum){
        if(skillSet!=null) {
            for (String skill : skillSet) {
                if (skill.equals(game.skillList()[skillNum])) {
                    return true;
                }
            }
        }
        return false;
    }
    public int indexOfSkill(int skillNum){
        if(skillSet!=null){
            if(skillSetContain(skillNum)) {
                for (int i = 0; i < skillSet.length; i++) {
                    if (skillSet[i].equals(game.skillList()[skillNum])) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }
    public int index(){
        for(int i = 0; i < game.getEnemyList().size();i++){
            if(this==game.getEnemyList().get(i)){
                return i;
            }
        }
        return -1;
    }
    public void aggroOn(){aggro = true;}
    public void checkSkill(int skillNum, int range){
        if(Math.abs(getxPos() - enemy.getxPos()) < range && Math.abs(getyPos() - enemy.getyPos()) < range && aggro) {
            if (skillSetContain(skillNum)) {
                if(currentCooldowns[indexOfSkill(skillNum)] == 0 && cooldownDone[indexOfSkill(skillNum)]){
                    personAttack(index());
                }
            }
        }
    }
    public void obtainSkill(int skillNum){
        String[] tempSkills = new String[skillsKnown.length + 1];
        for(int i = 0; i < skillsKnown.length; i++){
            tempSkills[i] = skillsKnown[i];
        }
        tempSkills[tempSkills.length-1] = game.skillList()[skillNum];
        skillsKnown = tempSkills;
        game.obtainSkill(skillNum);
    }
    public void obtainPassive(int passiveNum){
        String[] tempPassives = new String[passivesKnown.length + 1];
        for(int i = 0; i < passivesKnown.length; i++){
            tempPassives[i] = passivesKnown[i];
        }
        tempPassives[tempPassives.length-1] = game.passiveList()[passiveNum];
        passivesKnown = tempPassives;
        game.obtainPassive(passiveNum);
    }
    public void moveMap(int x, int y){
        mapX = x;
        mapY = y;
    }
    public int theX(){return xPos;}
    public int theY(){return yPos;}
    public void goToXY(int x, int y){
        xPos = x;
        yPos = y;
    }
    public boolean moveCloserTo(Person person, int xd, int yd){
        int x = getxPos();
        int y = getyPos();
        moveTowards(person,xd,yd);
        return x!=getxPos() || y!=getyPos();
    }
    public void moveTowards(Person person, int xd, int yd){
        for(int i = 0; i < 5; i++) {
            if (getxPos() > person.getxPos() + xd) {
                xPos -= 1;
            }
            if (getxPos() < person.getxPos() - xd) {
                xPos += 1;
            }
            if (getyPos() > person.getyPos() + yd) {
                yPos -= 1;
            }
            if (getyPos() < person.getyPos() - yd) {
                yPos += 1;
            }
        }
    }
    public int xpGain(){
        setStats();
        return 25 + magicStat*5 + physicalStat*5 + hpStat*5;
    }
    public int questsLength(){
        if(quests==null){
            return 0;
        }
        return quests.length;
    }
    public void setChatIndex(int index){
        chatIndex = index;
    }
    public boolean questDone(int index){
        return quests[index].deleteQuest();
    }
    public boolean berserk(){return berserk;}
    public void goBerserk(){
        berserk=true;
        Attack anAttack = new Attack(getxPos()-23,getyPos()-23,0,"berserk",this);
        game.getAttacks().add(anAttack);
    }
    public void goToPerson(Person person, int xd, int yd){
        moveX(person.getxPos()+xd-getxPos());
        moveY(person.getyPos()+yd-getyPos());
    }
    public int getMode(){
        return  mode;
    }
    public int getMeter(){return meter;}
    public void decrementMeter(){meter--;}
    public int getStatBonus(){return statBonus;}
    public void resetStats(){
        hpStat = 0;
        physicalStat = 0;
        magicStat = 0;
        statPoints = (level-1) * 3;
    }
    public void cheat(){
        level*=10;
        physicalStat*=10;
        magicStat*=10;
        hpStat*=10;
    }
    public Quest[] getQuests(){
        return quests;
    }
    public void move(){
        if(destinationX!=null){
            if(!(game.getGameScreen().checkInChat() && game.getNpcList().get(game.getGameScreen().getNpcTouched())==this)) {
                if (truexPos() != destinationX[destination]) {
                    if (truexPos() > destinationX[destination]) {
                        game.getGameScreen().movePerson(-1, 0, this);
                    } else {
                        game.getGameScreen().movePerson(1, 0, this);
                    }
                } else if (trueyPos() != destinationY[destination]) {
                    if (trueyPos() > destinationY[destination]) {
                        game.getGameScreen().movePerson(0, -1, this);
                    } else {
                        game.getGameScreen().movePerson(0, 1, this);
                    }
                } else {
                    waitTick++;
                    if (waitTick > wait[destination]) {
                        waitTick = 0;
                        destination++;
                        if (destination >= destinationX.length) {
                            destination = 0;
                        }
                    }
                }
            }
        }
    }
}
