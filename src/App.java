import java.util.ArrayList;

public class App {
    public static void main(String[] args) throws Exception {

        ASTParser astParser = new ASTParser();
        ASTNode astNode = astParser.parse("a*b");
        BottomUpNFABuilder nfaBuilder = new BottomUpNFABuilder();
        nfaBuilder.build(astNode);
        BottomUpNFATransformer nfaTransformer = new BottomUpNFATransformer(nfaBuilder.predictStateTable,
                nfaBuilder.startStatesStack.getFirst(), nfaBuilder.endStatesQuene.getFirst(),
                new char[] { 'a', 'b' });

        ArrayList<DFAState> dfaStates = nfaTransformer.subsetConstruct();
        System.out.println(dfaStates);

    }
}
