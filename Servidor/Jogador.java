public class Jogador extends Entidade {
    int Ident, Pontuacao, X, Y, mX, mY, Direcao;
    Item Inventario;
    Jogador(int ident, int x, int y) {
        Direcao = 1;
        X = x;
        Y = y;
        mX = x;
        mY = y;
        Vida = 3;
        Ident = ident;
        ID = ident*2-1;
    }
    public void MudaPontuacao(int i) {
        Pontuacao += i;
    }
    public void Interacao(Jogador Personagem) {
        if (Personagem.Inventario == null) return;
        if (Personagem.Inventario.ID == 11) {
            Vida--;
            Personagem.MudaPontuacao(-25);
        } else if (Personagem.Inventario.chave == Ident) {
            Personagem.Inventario = null;
            Personagem.MudaPontuacao((3-Vida)*25);
            Vida = 3;
        }
    }
    public void Movimentacao(int NovoX, int NovoY, int LX, int LY) {
        if (NovoX < 0) Direcao = -1;
        else if (NovoX > 0) Direcao = 1;
        
        if (NovoY < 0) ID = Ident*2*Direcao;
        else if (NovoY > 0) ID = (Ident*2-1)*Direcao;

        X += NovoX;
        Y += NovoY;
        
        if (X + NovoX > LX-1 || X + NovoX < 0 || Y + NovoY > LY-1 || Y + NovoY < 0) {
          mX = X;
          mY = Y;
        } else {
          mX = X + NovoX;
          mY = Y + NovoY;
        }
    }
}
