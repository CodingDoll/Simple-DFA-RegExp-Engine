import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

class DFAState {

  public boolean tagged = false;

  /**
   * NFA状态集合
   */
  public int[] states;

  /**
   * 是否接受状态
   */
  public boolean end;

  public HashMap<Character, DFAState> nextStates = new HashMap<Character, DFAState>();

  public DFAState() {
  }

  public DFAState(int[] states) {
    this.states = states;
  }

  public static boolean equals(DFAState a1, DFAState a2) {
    return Arrays.equals(a1.states, a2.states);
  }
}

public class BottomUpNFATransformer {

  private int start;
  private int end;

  private char[] alphabet;
  private HashMap<Integer, HashMap<Character, int[]>> predictStateTable = new HashMap<Integer, HashMap<Character, int[]>>();

  /**
   * 能够从NFA的状态s开始只通过e转换到达的NFA状态集合
   * 
   * @param state
   * @return
   */
  public int[] eplisonClosure(int state) {
    if (this.end == state)
      return new int[] { state };
    LinkedList<Integer> stack = new LinkedList<Integer>();
    ArrayList<Integer> tempRes = new ArrayList<Integer>();

    stack.push(state);
    while (!stack.isEmpty()) {
      int t = stack.pop();
      if (!tempRes.contains(t)) {
        tempRes.add(t);
      }
      HashMap<Character, int[]> next = this.predictStateTable.get(state);
      if (next.containsKey('\u0000')) {
        int[] nextStates = next.get('\u0000');
        for (int i : nextStates) {
          if (!tempRes.contains(i)) {

            stack.push(i);
          }
        }
      }
    }

    int[] res = new int[tempRes.size()];
    for (int i = 0; i < res.length; i++) {
      res[i] = tempRes.get(i);
    }
    return res;

  }

  /**
   * 能够从states中的某个状态s开始只通过e转换到达的NFA状态集合
   * 
   * @param states
   * @return
   */
  public int[] eplisonClosure(int[] states) {
    HashSet<Integer> resSet = new HashSet<Integer>();

    for (int i : states) {
      int[] closurei = this.eplisonClosure(i);
      for (int j : closurei) {
        resSet.add(j);
      }
    }

    int[] res = resSet.stream().mapToInt(Integer::intValue).toArray();
    return res;
  }

  /**
   * 能够从T中某个状态s出发通过标号为c的转换到达的NFA状态集合
   * 
   * @param states
   * @param c
   * @return
   */
  public int[] move(int[] states, char c) {
    HashSet<Integer> resSet = new HashSet<Integer>();
    for (int i : states) {
      if (!this.predictStateTable.containsKey(i))
        continue;
      HashMap<Character, int[]> nfaNext = this.predictStateTable.get(i);
      if (!nfaNext.containsKey(c))
        continue;

      int[] nextStates = nfaNext.get(c);
      for (int j : nextStates) {
        resSet.add(j);
      }
    }
    int[] res = resSet.stream().mapToInt(Integer::intValue).toArray();
    return res;
  }

  /**
   * 子集构造法
   * 
   * @return
   */
  public ArrayList<DFAState> subsetConstruct() {

    ArrayList<DFAState> dfaStates = new ArrayList<DFAState>();

    int[] startT = this.eplisonClosure(this.start);
    // 为了数组可匹配
    Arrays.sort(startT);
    DFAState startDFAState = new DFAState(startT);
    dfaStates.add(startDFAState);
    DFAState toTagState = startDFAState;
    // 当有一个未标记状态
    while (toTagState != null) {
      // 对t加上标记
      toTagState.tagged = true;
      // 如果包含接受状态则这个状态是接受状态
      if (Arrays.binarySearch(toTagState.states, this.end) >= 0) {
        toTagState.end = true;
      }
      ;
      // for 每个输入符号c
      for (char c : this.alphabet) {
        int[] newNFAStates = this.eplisonClosure(this.move(toTagState.states, c));
        Arrays.sort(newNFAStates);
        DFAState newUntaggedState = new DFAState(newNFAStates);

        // u不在dstate中
        boolean isInDstates = false;
        for (DFAState s : dfaStates) {
          if (DFAState.equals(s, newUntaggedState)) {
            isInDstates = true;
            newUntaggedState = s;
            break;
          }
        }
        // 将u加入dstate且不标记
        if (!isInDstates) {

          dfaStates.add(newUntaggedState);
        }
        // dtran[T, c] = U
        toTagState.nextStates.put(c, newUntaggedState);
      }
      for (DFAState s : dfaStates) {
        if (!s.tagged) {
          toTagState = s;
          break;
        }
        toTagState = null;
        ;
      }
    }
    return dfaStates;
  }

  public BottomUpNFATransformer(HashMap<Integer, HashMap<Character, int[]>> predictStateTable, int start, int end,
      char[] alphabet) {
    this.predictStateTable = predictStateTable;
    this.start = start;
    this.end = end;
    this.alphabet = alphabet;
  }

}
