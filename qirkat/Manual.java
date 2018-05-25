package qirkat;

import static qirkat.PieceColor.*;
import static qirkat.Command.Type.*;

/** A Player that receives its moves from its Game's getMoveCmnd method.
 *  @author cs61b
 */
class Manual extends Player {

    /** A Player that will play MYCOLOR on GAME, taking its moves from
     *  GAME. */
    Manual(Game game, PieceColor myColor) {
        super(game, myColor);
        _prompt = myColor + ": ";
    }

    @Override
    Move myMove() {
        Command movecommand = game().getMoveCmnd(_prompt);
        if (movecommand == null) {
            return null;
        }
        String[] movestring = movecommand.operands();
        Move move = Move.parseMove(movestring[0]);

        return move;
    }

    /** Identifies the player serving as a source of input commands. */
    private String _prompt;
}

