import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

import java.net.Socket;

public class Jogo implements IJogo{
    int MaximoJogadores, JogadoresAtuais = 0;
    DataInputStream Entradas[];
    DataOutputStream Saidas[];
    Logica logica;

    Jogo(int maximoJogadores) {
        MaximoJogadores = maximoJogadores;
        Entradas = new DataInputStream[MaximoJogadores];
        Saidas = new DataOutputStream[MaximoJogadores];
    }
    public int numMaximoJogadores() {
        return MaximoJogadores;
    }
    public void adicionaJogador(Socket clientSocket) {
        try {
            Entradas[JogadoresAtuais] = new DataInputStream(clientSocket.getInputStream());
            Saidas[JogadoresAtuais] = new DataOutputStream(clientSocket.getOutputStream());
        } catch(IOException erro) {
            System.out.println("Nao foi possivel conectar com o jogador " + JogadoresAtuais+1);
            System.exit(1);
        }
        JogadoresAtuais++;
    }
    public void iniciaLogica(ILogica logica) {
        this.logica = (Logica) logica;
        for (int i = 0; i < JogadoresAtuais; i++) {
            this.logica.adicionaJog(i, Entradas[i], Saidas[i]);
        }
    }
    public void inicia() {
        logica.executa();
    }
}