
/**This class creates new threads so that multiple lines of code can run at once.
 * By doing so, I can create things like cooldowns for attacks.
 */
public class TimeKeeper extends Thread{ //Extends Thread allows me to have concurrency, wait block can be executed while other code is running
    private Person person;
    private static Game game;
    public String toString(){
        return person.toString();
    }
    public boolean equals(Object other){
        return ((TimeKeeper)other).person==person;
    }

    /**This method contains the code for the new thread that is created, the actions each thread does differs through the use of static variables.
     */
    public void run(){
        Game game = Main.getGame();
        //System.out.println(game.timeKeepContains("1")); //to see if timeKeep changes on time(it does)
        if (game.timeKeepEquals("enemySkill")) {
            int skill = 0;
            for (int i = 0; i < game.getGameScreen().getEnemyNums().size(); i++){
                if (game.getGameScreen().getEnemyNums().get(i) > -1){
                    skill = game.getGameScreen().getEnemyNums().get(i);
                    try {
                        person = game.getEnemyList().get(i);
                    }
                    catch(ArrayIndexOutOfBoundsException e){
                        break;
                    }
                    game.getGameScreen().getEnemyNums().set(i,-1);
                    i+= game.getGameScreen().getEnemyNums().size()*1000000;
                }
            }
            //System.out.println(person); //to see if person changes (it does)
            if(person!=null) {
                game.executeSkill(skill, person);
            }
        }
        else if (game.timeKeepContains("allySkill")) {
            if(game.timeKeepNumber()>-1) {
                //System.out.println("allySkillOpen");
                int skill = 0;
                person = game.getAllyList().get(game.timeKeepNumber());
                game.executeSkill(skill, person);
                //System.out.println("allySkillClose");
            }

        }
        else if (game.timeKeepEquals("playerHeal")) {
            game.setInitializer(1);
            while (game.isGamePlaying()) {
                //System.out.println("playerHealOpen");
                waitSec(1);
                //if(!game.getGameScreen().checkMenu()) {
                    game.healPlayer();
                //}
                //System.out.println("playerHealClose");
            }
        }
        else if (game.timeKeepEquals("attackManager")) {
            game.setInitializer(2);
            while (game.isGamePlaying()) {
                //System.out.println("attackOpen");
                System.out.print("");
                game.attack();
                //System.out.println("attackClose");
            }
        }
        else if (game.timeKeepEquals("skill1Manager")) {
            game.setInitializer(3);
            while (game.isGamePlaying()) {
                //System.out.println("skill1Open");
                System.out.print("");
                game.executeSkill(0);
                //System.out.println("skill1Close");
            }
        }
        else if (game.timeKeepEquals("skill2Manager")) {
            game.setInitializer(4);
            while (game.isGamePlaying()) {
                //System.out.println("skill2Open");
                System.out.print("");
                game.executeSkill(1);
                //System.out.println("skill2Close");
            }
        }
        else if (game.timeKeepEquals("skill3Manager")) {
            game.setInitializer(5);
            while (game.isGamePlaying()) {
                //System.out.println("skill3Open");
                System.out.print("");
                game.executeSkill(2);
                //System.out.println("skill3Close");
            }
        }
        else if (game.timeKeepEquals("skill4Manager")) {
            game.setInitializer(6);
            while (game.isGamePlaying()) {
                //System.out.println("skill4Open");
                System.out.print("");
                game.executeSkill(3);
                //System.out.println("skill4Close");
            }
        }
        else if (game.timeKeepEquals("statusEffectManager")){
            game.setInitializer(7);
            while(game.isGamePlaying()){
                waitSec(0.01);
                //System.out.println("statusOpen");
//                if(!game.getGameScreen().checkMenu()) {
                for (int i = 0; i < game.getEnemyList().size(); i++) {
                    game.getEnemyList().get(i).burn();
                    game.getEnemyList().get(i).stun();
                    game.getEnemyList().get(i).bleed();
                }
                for (int i = 0; i < game.getAllyList().size(); i++) {
                    game.getAllyList().get(i).burn();
                    game.getAllyList().get(i).stun();
                    game.getAllyList().get(i).bleed();
                }
                game.getPlayer().stun();
                game.getPlayer().burn();
                game.getPlayer().bleed();
                if (game.getPlayer().getMeter() > 0) {
                    game.getPlayer().decrementMeter();
                } else {
                    game.getPlayer().setMode(0);
                }
                //System.out.println("statusClose");
            }
//            }
        }
    }

    /**The method that creates the new thread.
     */
    public static void timerStart() {
        (new TimeKeeper()).start();
    }

    /** Method that makes the thread wait, allows the code to keep time.
     * @param sec, it is a double so I can put numbers like 0.5 (half a second).
     */
    public static void waitSec(double sec){
        try {
            Thread.sleep((int)(sec*1000));
        } catch (InterruptedException e) {
            System.out.println("Interrupted");
        }
    }
    public static void setGame(Game g){
        game = g;
    }
}
