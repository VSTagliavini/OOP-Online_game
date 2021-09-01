public class Alavanca extends CelulaMundo{
    boolean Ativacao, Pontuacao;
    int x, y;
    Portao PortaoAtrelado;
    Alavanca(Portao pa, int x, int y) {
        Pontuacao = Permeavel = true;
        Ativacao = false;
        ID = 6;
        this.x = x;
        this.y = y;
        PortaoAtrelado = pa;
    }
    public void Interacao(Jogador Personagem) {
        Ativacao = !Ativacao;
        if (Pontuacao) {
            Pontuacao = false;
            Personagem.MudaPontuacao(10);
        }
        if (Ativacao) ID = 5;
        else ID = 6;
        if (PortaoAtrelado != null) PortaoAtrelado.Abre_Fecha();
    }
}