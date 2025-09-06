public class Effect extends Attack{
    private int number;
    private boolean classUsed;
    public Effect(String name, boolean playerFired, Person whoAttacked){
        super(name,playerFired,whoAttacked);
    }
    public Effect(){
        super();
    }
    public void multiplySize(int m){
        super.multiplySize(m);
        System.out.println("Width: " + getWidth() + " " + "Height:" + getHeight());
    }
    public boolean equals(Object other){
        return ((Effect)other).getWidth() == getWidth() && ((Effect)other).getHeight() == getHeight();
    }
    public String toString(){
        return super.toString() + "Number: " + number;
    }
    public boolean compareTo(Attack other){
        return this.getType().equals(other.getType());
    }
}
