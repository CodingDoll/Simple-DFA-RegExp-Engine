import java.util.HashMap;

class NFANode {

  public HashMap<Character, NFANode[]> next = new HashMap<Character, NFANode[]>();
}

public class TopDownNFABuilder {

  private NFANode handleChar(NFANode start, NFANode end, ASTNode tree) {

    start.next.put(tree.value, new NFANode[] { end });
    return start;
  }

  private NFANode handleConcat(NFANode start, NFANode end, ASTNode tree) {
    NFANode transit = new NFANode();
    build(start, transit, tree.left);
    build(transit, end, tree.right);
    return start;
  }

  private NFANode handleSelect(NFANode start, NFANode end, ASTNode tree) {
    build(start, end, tree.left);
    build(start, end, tree.right);
    return start;
  }

  private NFANode handleClosure(NFANode start, NFANode end, ASTNode tree) {
    build(start, start, tree.left);
    if (start.next.containsKey('\u0000')) {
      NFANode[] oldNfaNodes = start.next.get('\u0000');
      NFANode[] newNfaNodes = java.util.Arrays.copyOf(oldNfaNodes, oldNfaNodes.length + 1);
      newNfaNodes[oldNfaNodes.length] = end;
      start.next.replace('\u0000', newNfaNodes);
    } else {

      start.next.put('\u0000', new NFANode[] { end });
    }
    return start;
  }

  public NFANode build(ASTNode tree) {
    NFANode start = new NFANode();
    NFANode end = new NFANode();

    return build(start, end, tree);
  }

  private NFANode build(NFANode start, NFANode end, ASTNode tree) {
    switch (tree.type) {
      case CHAR:
        return handleChar(start, end, tree);
      case CONCAT:
        return handleConcat(start, end, tree);
      case SELECT:
        return handleSelect(start, end, tree);
      case CLOSURE:
        return handleClosure(start, end, tree);
      default:
        return start;
    }
  }
}
