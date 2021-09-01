public class Porta extends CelulaMundo {
    int chave;
    Porta(int chave) {
        Permeavel = false;
        this.chave = chave;
        ID = 16;
    }
    public void Interacao(Jogador Personagem) {
        if (Personagem.Inventario == null) return;
        if (chave == Personagem.Inventario.chave) {
            if (!Permeavel) Personagem.MudaPontuacao(10);
            Permeavel = true;
            ID = 15;
            Personagem.Inventario = null;
        }
    }
}