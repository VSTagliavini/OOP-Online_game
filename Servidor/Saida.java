public class Saida extends CelulaMundo {
    Saida() {
        Permeavel = false;
        ID = 18;
    }
    public void Interacao(Jogador Personagem) {
        Personagem.MudaPontuacao(10);
    }
}