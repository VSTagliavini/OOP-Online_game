public class Portao extends CelulaMundo{
    Portao() {
        Permeavel = false;
        ID = 17;
    }
    public void Abre_Fecha() {
        Permeavel = !Permeavel;
        if (Permeavel) ID = 15;
        else if (!Permeavel) ID = 17;
    }
}