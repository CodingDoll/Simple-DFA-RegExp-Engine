public class ASTNode {
  public ASTNodeType type;
  public char value;
  public ASTNode left;
  public ASTNode right;

  /**
   * concat select
   */
  public ASTNode(ASTNodeType type, ASTNode left, ASTNode right) {
    this.type = type;
    this.left = left;
    this.right = right;
  }

  /**
   * closure
   * example closure '*' left
   */
  public ASTNode(ASTNodeType type, char value, ASTNode left) {
    this.type = type;
    this.left = left;
  }

  /**
   * char
   */
  public ASTNode(ASTNodeType type, char value) {
    this.type = type;
    this.value = value;
  }

}
