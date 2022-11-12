package gameMechanics;

public class Counter {
    private int movements;

    public Counter() {
        this.movements = 0;
    }
    public void add(){
        movements++;
    }
    public void add(int qty){
        movements+=qty;
    }
    public void reset(){
        movements = 0;
    }

    public int getMovements() {
        return movements;
    }
    public void setMovements(int movements) {
        this.movements = movements;
    }
}
