public class ASTParser {

  private char token;
  private String source;
  private int index = 0;

  public ASTNode parse(String source) {
    this.source = source.trim();
    if (this.source.length() == 0) {
      throw new Error("空字符串");
    }
    this.token = this.source.charAt(index);
    return this.parseS();
  }

  private void getNextToken() {
    if (index == this.source.length() - 1) {
      this.token = '\u0000';
    } else {
      index++;
      this.token = this.source.charAt(index);
    }
  }

  /**
   * Regex BNF
   * S -> S '|' S | A
   * A -> B'·'B | B
   * B -> C'*' | C
   * C -> '('S')' | char | ε
   */
  private ASTNode parseS() {
    ASTNode res = this.parseA();
    while (this.token == '|') {
      this.getNextToken();
      res = new ASTNode(ASTNodeType.SELECT, res, this.parseS());
    }
    return res;
  }

  private ASTNode parseA() {
    ASTNode res = this.parseB();
    while (this.token != '|' && this.token != '\u0000' && this.token != ')') {
      res = new ASTNode(ASTNodeType.CONCAT, res, this.parseB());
    }
    return res;
  }

  private ASTNode parseB() {
    ASTNode res = this.parseC();
    if (this.token == '*') {
      res = new ASTNode(ASTNodeType.CLOSURE, this.token, res);
      this.getNextToken();
    }
    return res;
  }

  private ASTNode parseC() {
    ASTNode res;
    if (this.token == '(') {
      this.getNextToken();
      res = this.parseS();
      this.getNextToken();
    } else if (this.token == ')') {
      this.getNextToken();
      res = this.parseS();
    } else {
      if (this.token == '\u0000') {
        res = new ASTNode(ASTNodeType.EPLISON, this.token);
      } else {
        res = new ASTNode(ASTNodeType.CHAR, this.token);
        this.getNextToken();
      }
    }

    return res;
  }

}
