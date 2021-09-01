public class Bau extends CelulaMundo {
    int chave;
    Item Inventario;
    Bau(int chave, Item Inventario) {
        Permeavel = false;
        this.chave = chave;
        this.Inventario = Inventario;
        ID = 8;
    }
    public void Interacao(Jogador Personagem) {
        if (Personagem.Inventario == null) return;
        if (chave == Personagem.Inventario.chave) {
            if (Inventario != null) Personagem.MudaPontuacao(10);
            ID = 7;
            Personagem.Inventario = Inventario;
            Inventario = null;
        }
    }
}