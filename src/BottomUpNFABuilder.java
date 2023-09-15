import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * if CHAR
 * s0 -> e1
 * if SELECT : a = (s0 -> e1) b = (s2 -> e3)
 * 4 -> 0 -> 1 -> 5
 * 4 -> 2 -> 3 -> 5
 * if CONCAT : a = (s0 -> e1) b = (s2 -> e3)
 * 0 -> 12 -> 3
 */

/**
 * 状态预测表
 * HashMap<Integer, HashMap<Character, int[]>>
 */

/**
 * from bottom to top build NFA
 */
public class BottomUpNFABuilder {
  public int newState = 0;

  public Deque<Integer> startStatesStack = new LinkedList<Integer>();
  public Deque<Integer> endStatesQuene = new LinkedList<Integer>();

  public HashMap<Integer, HashMap<Character, int[]>> predictStateTable = new HashMap<Integer, HashMap<Character, int[]>>();

  private void handleChar(char c) {
    int startState = this.newState;
    this.newState++;
    int endState = this.newState;

    this.startStatesStack.push(startState);

    HashMap<Character, int[]> nextStates = new HashMap<Character, int[]>();
    nextStates.put(c, new int[] { endState });
    this.predictStateTable.put(startState, nextStates);

    this.endStatesQuene.offer(endState);
    this.newState++;
  }

  private void handleConcat() {

    /**
     * left endState shift
     * right startState pop
     * predicStateTable[leftEndState, rightChar] =
     * predictStateTable[rightStateState, rightChar]
     * predictStateTable.remove([rightStartState, rightChar])
     */
    int leftEndState = this.endStatesQuene.poll();
    int rightStartState = this.startStatesStack.pop();

    HashMap<Character, int[]> rightStartStateTable = this.predictStateTable.get(rightStartState);

    this.predictStateTable.put(leftEndState, rightStartStateTable);
    this.predictStateTable.remove(rightStartState);

  }

  private void handleSelect() {
    /**
     * left start end state out
     * right start end state out
     * new start state in
     * point to left right start state
     * new end state in
     * left rignt end state point to new end state
     */
    int rightStartState = this.startStatesStack.pop();
    int leftStartState = this.startStatesStack.pop();

    int leftEndState = this.endStatesQuene.poll();
    int rightEndState = this.endStatesQuene.poll();

    int newStartState = this.newState;
    this.newState++;
    int newEndState = this.newState;

    HashMap<Character, int[]> newNextState = new HashMap<Character, int[]>();
    newNextState.put('\u0000', new int[] { leftStartState, rightStartState });
    this.predictStateTable.put(newStartState, newNextState);

    HashMap<Character, int[]> rightEndStateNext = new HashMap<Character, int[]>();
    rightEndStateNext.put('\u0000', new int[] { newEndState });
    this.predictStateTable.put(rightEndState, rightEndStateNext);

    this.predictStateTable.get(rightEndState).put('\u0000', new int[] { newEndState });

    HashMap<Character, int[]> leftEndStateNext = new HashMap<Character, int[]>();
    leftEndStateNext.put('\u0000', new int[] { newEndState });
    this.predictStateTable.put(leftEndState, leftEndStateNext);

    this.startStatesStack.push(newStartState);
    this.endStatesQuene.offer(newEndState);

    this.newState++;
  }

  private void handelClosure() {
    /**
     * old start end state out
     * old end point to new end | old end point to old start
     * new start state point to old start | new start state point to new end
     */
    int oldStartState = this.startStatesStack.poll();
    int oldEndState = this.endStatesQuene.pop();
    int newStartState = this.newState;
    this.newState++;
    int newEndState = this.newState;

    HashMap<Character, int[]> oldEndStateNext = new HashMap<Character, int[]>();
    oldEndStateNext.put('\u0000', new int[] { oldStartState, newEndState });

    this.predictStateTable.put(oldEndState, oldEndStateNext);

    HashMap<Character, int[]> newStartStateNext = new HashMap<Character, int[]>();
    newStartStateNext.put('\u0000', new int[] { oldStartState, newEndState });
    this.predictStateTable.put(newStartState, newStartStateNext);

    this.startStatesStack.push(newStartState);
    this.endStatesQuene.offer(newEndState);
    this.newState++;
  }

  public void build(ASTNode tree) {
    /**
     * left search
     * right search
     * build
     */

    if (tree.left != null) {
      build(tree.left);
    }
    if (tree.right != null) {
      build(tree.right);
    }

    switch (tree.type) {
      case CHAR:
        handleChar(tree.value);
        break;
      case CONCAT:
        handleConcat();
        break;
      case SELECT:
        handleSelect();
        break;
      case CLOSURE:
        handelClosure();
        break;
      default:
        System.out.println("error");
        break;
    }
  }
}
