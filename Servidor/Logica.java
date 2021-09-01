import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

import java.lang.Thread;

public class Logica implements ILogica {
    boolean continua;
    int X, Y, numJogadores;
    Atomo Mapa[][][];
    Movimentacao Entradas[];
    DataOutputStream Saidas[];
    Jogador Jogadores[];
    Jogo JogoAtual;

    Logica(Jogo jogo, int numJogadores) {
        this.numJogadores = numJogadores;
        Jogadores = new Jogador[numJogadores];
        Entradas = new Movimentacao[numJogadores];
        Saidas = new DataOutputStream[numJogadores];
        JogoAtual = jogo;
        continua = true;

        X = 16;
        Y = 9;
        Mapa = new Atomo[X][Y][5];
        for (int x = 0; x < X; x++) {
            Mapa[x][0][0] = new Parede();
            Mapa[x][Y-1][0] = new Parede();
        }
        for (int y = 1; y < Y-1; y++) {
            Mapa[0][y][0] = new Parede();
            Mapa[X-1][y][0] = new Parede();
        } 
        for (int x = 1; x < X-1; x++) {
            for (int y = 1; y < Y-1; y++) {
                Mapa[x][y][0] = new Chao();
            }
        }

        Jogadores[0] = new Jogador(1, 14, 6);
        Mapa[14][6][1] = Jogadores[0];
        Mapa[14][6][4] = new AtomoMarcado();
        Jogadores[1] = new Jogador(2, 14, 7);
        Mapa[14][7][1] = Jogadores[1];
        Mapa[14][7][4] = new AtomoMarcado();


        Mapa[10][7][2] = new Pocao(1);
        Mapa[10][6][2] = new Pocao(2);
        for (int i = 13; i < 16; i++) Mapa[i][5][0] = new Parede();
        for (int i = 8; i > 4; i--) Mapa[11][i][0] = new Parede();
        Mapa[12][5][3] = new Porta(10);
        Mapa[12][6][2] = new Chave(10);
        for (int i = 0; i < 6; i++) Mapa[7][i][0] = new Parede();
        Mapa[8][5][0] = new Parede();
        Mapa[10][5][0] = new Parede();
        Mapa[9][5][3] = new Portao();
        Mapa[11][1][3] = new Alavanca((Portao) Mapa[9][5][3], 9, 5);
        Mapa[11][3][2] = new Chave(11);
        Mapa[1][7][3] = new Bau(11, (Item) new Espada());
        Mapa[1][1][3] = new Saida();
        Mapa[5][2][1] = new Slime();
    }
    public void executa() {
        for (int i = 0; i < numJogadores; i++) {
            Entradas[i].start();
        }
    }
    public void adicionaJog(int i, DataInputStream in, DataOutputStream out) {
        Saidas[i] = out;
        Entradas[i] = new Movimentacao(i, in);
        try {
            Saidas[i].writeInt(X);

            System.out.println(X);
            Saidas[i].writeInt(Y);
            System.out.println(Y);
            for (int x = 0; x < X; x++) {
                for (int y = 0; y < Y; y++) {
                    for (int z = 0; z < 5; z++) {
                        if (Mapa[x][y][z] == null) Saidas[i].writeInt(20);
                        else Saidas[i].writeInt(Mapa[x][y][z].ID);
                    }
                }
            }
        } catch (IOException erro) {
            System.out.println("Nao foi possivel trocar informacoes com o jogador " + i);
        }
    }
    public void AtualizaJogadores(int x, int y, int a, int b, int c) {
        for (int i = 0; i < numJogadores; i++) {
            try {
                Saidas[i].writeInt(x);
                Saidas[i].writeInt(y);
                Saidas[i].writeInt(a);
                Saidas[i].writeInt(b);
                Saidas[i].writeInt(c);
            } catch (IOException erro) {
                System.out.println("Ocorreu um erro ao envia dados ao jogador " + i);
            }
        }
    }

    class Movimentacao extends Thread {
        int jogador;
        DataInputStream Conexao;

        Movimentacao(int i, DataInputStream in) {
            jogador = i;
            Conexao = in;
        }
        public void run() {
            int acao;
            while (continua) {
                try {
                    acao = Conexao.readInt();
                } catch (IOException erro) {
                    System.out.println("Nao foi possivel executar os comandos do jogador " + jogador);
                    continua = false;
                    acao = 27;
                }
                /*W-87 | S-83 | A-65 | D-68 | Q-81 | E-69 | ESC-27*/
                if (acao == 27) {
                    continua = false;
                    AtualizaJogadores(-1, -1, -1, -1, -1);
                }
                if (acao == 69) {
                    //Interacao
                    int mX = Jogadores[jogador].mX, mY = Jogadores[jogador].mY;
                    if (Mapa[mX][mY][1] != null) {
                        //Interação com entidades
                        Mapa[mX][mY][1].Interacao(Jogadores[jogador]);
                        if (Mapa[mX][mY][1].ID != 0 && (Mapa[mX][mY][1].ID > -5 && Mapa[mX][mY][1].ID < 5)) AtualizaJogadores(-1, -1, 0, ((Jogador) Mapa[mX][mY][1]).Vida, ((Jogador) Mapa[mX][mY][1]).Ident);
                        if (((Entidade) Mapa[mX][mY][1]).Vida <= 0) {
                            if (Mapa[mX][mY][1].ID != 19) {
                                AtualizaJogadores(((Jogador) Mapa[mX][mY][1]).mX, ((Jogador) Mapa[mX][mY][1]).mY, 4, 20, 0);
                                AtualizaJogadores(-1, -1, 0, ((Jogador) Mapa[mX][mY][1]).Vida, ((Jogador) Mapa[mX][mY][1]).Ident);
                            }
                            Mapa[mX][mY][1] = null;
                            Jogadores[1-jogador] = null;
                            AtualizaJogadores(mX, mY, 1, 20, 0);
                        }
                        if (Jogadores[jogador].Inventario != null) AtualizaJogadores(-1, -1, 1, Jogadores[jogador].Inventario.ID, Jogadores[jogador].Ident-1);
                        else AtualizaJogadores(-1, -1, 1, 20, Jogadores[jogador].Ident-1);
                        AtualizaJogadores(-1, -1, 2, Jogadores[jogador].Pontuacao, Jogadores[jogador].Ident-1);
                        AtualizaJogadores(-1, -1, -1, -1, 0);
                    }
                    if (Mapa[mX][mY][2] != null) {
                        //Interação com items
                        int pX =  Jogadores[jogador].X, pY = Jogadores[jogador].Y;
                        if (Jogadores[jogador].Inventario != null) {
                            Mapa[pX][pY][2] = Jogadores[jogador].Inventario;
                            AtualizaJogadores(pX, pY, 2, Mapa[pX][pY][2].ID, 0);
                        }
                        Jogadores[jogador].Inventario = (Item) Mapa[mX][mY][2];
                        AtualizaJogadores(-1, -1, 1, Jogadores[jogador].Inventario.ID, Jogadores[jogador].Ident-1);
                        Mapa[mX][mY][2] = null;
                        AtualizaJogadores(mX, mY, 2, 20, 0);
                        AtualizaJogadores(-1, -1, -1, -1, 0);
                    }
                    if (Mapa[mX][mY][3] != null) {
                        //Interação com partes do mapa
                        Mapa[mX][mY][3].Interacao(Jogadores[jogador]);
                        AtualizaJogadores(mX, mY, 3, Mapa[mX][mY][3].ID, 0);
                        if (Jogadores[jogador].Inventario != null) AtualizaJogadores(-1, -1, 1, Jogadores[jogador].Inventario.ID, Jogadores[jogador].Ident-1);
                        else AtualizaJogadores(-1, -1, 1, 20, Jogadores[jogador].Ident-1);
                        if (Mapa[mX][mY][3].ID == 6 || Mapa[mX][mY][3].ID == 5) AtualizaJogadores(((Alavanca) Mapa[mX][mY][3]).x, ((Alavanca) Mapa[mX][mY][3]).y, 3, Mapa[((Alavanca) Mapa[mX][mY][3]).x][((Alavanca) Mapa[mX][mY][3]).y][3].ID, 0);
                        if (Mapa[mX][mY][3].ID == 18) {
                            System.out.println("Fim do jogo.");
                            AtualizaJogadores(-1, -1, -1, -1, 1);
                            System.exit(0);
                        }
                        AtualizaJogadores(-1, -1, 2, Jogadores[jogador].Pontuacao, Jogadores[jogador].Ident-1);
                        AtualizaJogadores(-1, -1, -1, -1, 0);
                    }
                }  
                else if (acao == 81) {
                    //Dropar
                    int pX =  Jogadores[jogador].X, pY = Jogadores[jogador].Y;
                    if (Jogadores[jogador].Inventario != null && Mapa[pX][pY][2] == null) {
                        Mapa[pX][pY][2] = Jogadores[jogador].Inventario;
                        AtualizaJogadores(pX, pY, 2, Jogadores[jogador].Inventario.ID, 0);
                        Jogadores[jogador].Inventario = null;
                        AtualizaJogadores(-1, -1, 1, 20, Jogadores[jogador].Ident-1);
                        AtualizaJogadores(-1, -1, -1, -1, 0);
                    }
                }
                else {
                    int pX = Jogadores[jogador].X, pY = Jogadores[jogador].Y, nX = 0, nY = 0, mX = Jogadores[jogador].mX, mY = Jogadores[jogador].mY;
                    switch (acao) {
                        case 87: nX = 0; nY = -1; break;
                        case 83: nX = 0; nY = 1; break;
                        case 65: nX = -1; nY = 0; break;
                        case 68: nX = 1; nY = 0; break;
                    }
                    if (pX + nX < 0 || pX + nX > X-1 || pY + nY < 0 || pY + nY > Y-1) continue;
                    if (!Mapa[pX+nX][pY+nY][0].Permeavel || Mapa[pX+nX][pY+nY][1] != null) continue;
                    if (Mapa[pX+nX][pY+nY][3] != null && !Mapa[pX+nX][pY+nY][3].Permeavel) continue;

                    Jogadores[jogador].Movimentacao(nX, nY, X, Y);
                    

                    Mapa[pX+nX][pY+nY][1] = Jogadores[jogador];
                    Mapa[pX][pY][1] = null;
                    AtualizaJogadores(pX+nX, pY+nY, 1, Jogadores[jogador].ID, 0);
                    AtualizaJogadores(pX, pY, 1, 20, 0);
                    Mapa[Jogadores[jogador].mX][Jogadores[jogador].mY][4] = Mapa[mX][mY][4];
                    Mapa[mX][mY][4] = null;
                    AtualizaJogadores(Jogadores[jogador].mX, Jogadores[jogador].mY, 4, 0, 0);
                    AtualizaJogadores(mX, mY, 4, 20, 0);

                    AtualizaJogadores(-1, -1, -1, -1, 0);
                }
            }
        }
    }
}