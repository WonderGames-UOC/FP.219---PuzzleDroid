package gameMechanics;

public class Selector {

    private int selBlockA, selBlockB;

    public Selector() {
        this.selBlockA = -1;
        this.selBlockB = -1;
    }

    /**
     * Allows to swap pieces like a memory game
     * @param pos
     * @return -1 cancell selection; 0 one block selected; 1 two blocks selected.
     */
    public int memoryLikeSelector(int pos){
        //If the same imageView is touched twice, selection is canceled.
        if(pos == this.selBlockA){
            this.selBlockA = -1;
            return -1;
        }
        //If the touched imageView is first one.
        if(this.selBlockA < 0){
            this.selBlockA = pos;
            return 0;
        }
        //If the touched imageView is the second.
        this.selBlockB = pos;
        return 1;
    }
    public void resetSelection(){
        this.selBlockA = this.selBlockB = -1;
    }

    public int getSelBlockA() {
        return selBlockA;
    }

    public void setSelBlockA(int selBlockA) {
        this.selBlockA = selBlockA;
    }

    public int getSelBlockB() {
        return selBlockB;
    }

    public void setSelBlockB(int selBlockB) {
        this.selBlockB = selBlockB;
    }
}
