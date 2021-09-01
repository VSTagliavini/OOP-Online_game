import java.io.IOException;
import java.awt.Graphics;
import java.awt.Color;

public class Slime extends Entidade {
    Slime() {
        Vida = 2;
        ID = 19;
    }
    public void Interacao(Jogador Personagem) {
        if (Personagem.Inventario != null)
            if (Personagem.Inventario.ID == 11) {
                Vida--;
                Personagem.MudaPontuacao(5);
                if (Vida <= 0) Personagem.MudaPontuacao(10);
            }
    }
}