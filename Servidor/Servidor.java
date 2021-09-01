import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    ServerSocket serverSocket;
    int PORT;

    Servidor(int port) {
        PORT = port;
        serverSocket = null;
        try {
            serverSocket = new ServerSocket(PORT);
        } catch(IOException erro) {
            System.out.println("Nao foi possivel criar o servidor no PORT " + PORT);
            System.exit(1);
        }
        while (true) {
            Jogo JogoAtual = new Jogo(2);
            int numMaximoJogadores = JogoAtual.numMaximoJogadores();
            for (int i = 0; i < numMaximoJogadores; i++) {
                Socket clientSocket = null;
                try {
                    System.out.println("Esperando conexao de um jogador.");
                    clientSocket = serverSocket.accept();
                } catch (IOException e) {
                    System.out.println("Accept falhou: " + PORT + ".\n" + e);
                    System.exit(1);
                }
                System.out.println("Accept Funcionou!");
                JogoAtual.adicionaJogador(clientSocket);
            }
            JogoAtual.iniciaLogica(new Logica(JogoAtual, numMaximoJogadores));
            JogoAtual.inicia();
            System.out.println("O jogo foi iniciado!");
        }
    }
    public static void main(String[] args) {
        Servidor a = new Servidor(80);
    }
}