import java.net.UnknownHostException;
import java.net.Socket;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.Color;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.Graphics;
import java.awt.Dimension;
import javax.swing.JOptionPane;

public class Cliente extends JFrame {
    public boolean continua;
    public DataOutputStream Saida;
    public DataInputStream Entrada;
    public Socket Conexao;
    public Interacao Teclado;
    public Janela Tela;
    public int X, Y, Mapa[][][], Vida[] = new int[2], Item[] = new int[2], Pontos[] = new int[2];
    public EntradaServidor Recebimento;
    String Sprites[] = {"Sprites\\Chao_Selecionado.png",
                        "Sprites\\1-Parado_Frente.png",
                        "Sprites\\1-Parado_Tras.png",
                        "Sprites\\2-Parado_Frente.png",
                        "Sprites\\2-Parado_Tras.png",
                        "Sprites\\Alavanca_Ativada.png",
                        "Sprites\\Alavanca_Desativada.png",
                        "Sprites\\Bau_Aberto.png",
                        "Sprites\\Bau_Fechado.png",
                        "Sprites\\Chao.png",
                        "Sprites\\Chave.png",
                        "Sprites\\Espada.png",
                        "Sprites\\Parede.png",
                        "Sprites\\Pocao1.png",
                        "Sprites\\Pocao2.png",
                        "Sprites\\Porta_Aberta.png",
                        "Sprites\\Porta_Fechada.png",
                        "Sprites\\Portao_Fechado.png",
                        "Sprites\\Saida.png",
                        "Sprites\\Slime_Parado.png",};

    public static void main (String[] args) {
        Cliente Jogador = new Cliente();
    }
    Cliente() {
        super("Mata! MATA O SLIME PARA VER O QUE QUE TE ACONTECE!");
        try {
            Conexao = new Socket("127.0.0.1", 80);
            Saida = new DataOutputStream(Conexao.getOutputStream());
            Entrada = new DataInputStream(Conexao.getInputStream());
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host.");
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to host");
            System.exit(1);
        }
        Vida[0] = 3;
        Vida[1] = 3;
        Item[0] = 20;
        Item[1] = 20;
        Pontos[0] = 0;
        Pontos[1] = 0;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        continua = true;
        Teclado = new Interacao();
        addKeyListener(Teclado);
        Recebimento = new EntradaServidor(Entrada);
        new Thread(Recebimento).start();
        Tela = new Janela();
        add(Tela);
        pack();
        setVisible(true);
    }

    class Interacao extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            int Tecla = e.getKeyCode();
            if (Tecla != 27 && Tecla != 65 && Tecla != 68 && Tecla != 69 && Tecla != 81 && Tecla != 83 && Tecla != 87) return;
            try {
                Saida.writeInt(Tecla);

            } catch (IOException erro) {
                System.out.println("Ocorreu um erro ao enviar o comando para o servidor.");
            }
        }
    }
    public void AlteraJogo(int entrada[]) {
        if (entrada[0] == -1 && entrada[1] == -1) {
            if (entrada[2] == -1 && entrada[3] == -1) {
                if (entrada[4] == -1) {
                    System.out.println("Jogo foi finalizado pelo servidor."); 
                    System.exit(0);
                } else if (entrada[4] == 0) {
                    Tela.repaint();
                } else if (entrada[4] == 1) {
                    Tela.FimDeJogo();
                }
            }
            if (entrada[2] == 0) {
                Vida[entrada[4]-1] = entrada[3];
            }
            else if (entrada[2] == 1) Item[entrada[4]] = entrada[3];
            else if (entrada[2] == 2) Pontos[entrada[4]] = entrada[3];
        } else {
            Mapa[entrada[0]][entrada[1]][entrada[2]] = entrada[3];    
        }
    }
    class EntradaServidor extends Thread {
        DataInputStream Entrada;
        EntradaServidor(DataInputStream e) {
            Entrada = e;
            start();
        }
        public void start() {
            try {
                X = Entrada.readInt();
                Y = Entrada.readInt();
                Mapa = new int[X][Y][5];
                for (int x = 0; x < X; x++) {
                    for (int y = 0; y < Y; y++) {
                        for (int z = 0; z < 5; z++) {
                            Mapa[x][y][z] = Entrada.readInt();
                        }
                    }
                }
                System.out.println("...");
            } catch (IOException erro) {
                System.out.println("Ocorreu um erro ao receber informacoes do servidor.");
                System.exit(1);
            }
        }
        public void run() {
            System.out.println("Metodo run");
            int entrada[] = new int[5];
            while (continua) {
                try {
                    for (int i = 0; i < 5; i++) entrada[i] = Entrada.readInt();
                    AlteraJogo(entrada);
                } catch (IOException erro) {
                    System.out.println("Ocorreu um erro ao enviar dados ao servidor.");
                }
            }
        }
    }
    class Janela extends JPanel {
        Janela() {
            setPreferredSize(new Dimension(X*48, Y*48+100));
        }
        public void paintComponent(Graphics g) {
            g.setColor(Color.BLACK);
            g.fillRect(0, Y*48, getWidth(), 100);
            if (Vida[0] > 0) {
                g.setColor(Color.RED);
                g.fillRect(X*24-55, Y*48+25, 50, 50);
                for (int i = 0; i < Vida[0]; i++)
                    g.fillRect(X*24-55-(i+1)*70, Y*48+45, 60, 30);
                if (Item[0] != 20) {
                    try {
                        g.drawImage(ImageIO.read(new File(Sprites[Item[0]])), X*24-55, Y*48+27, 48, 48, null);
                    } catch (IOException erro) {
                        System.out.println("Ocorreu um erro ao exibir o inventario do jogador 1");
                    }
                }
                g.setColor(Color.white);
                g.drawString(Integer.toString(Pontos[0]), X*24-93, Y*48+40);
            }
            if (Vida[1] > 0) {
                g.setColor(Color.blue);
                g.fillRect(X*24+5, Y*48+25, 50, 50);
                for (int i = 0; i < Vida[1]; i++)
                    g.fillRect(X*24+65+i*70, Y*48+45, 60, 30);
                if (Item[1] != 20) {
                    try {
                        g.drawImage(ImageIO.read(new File(Sprites[Item[1]])), X*24+5, Y*48+27, 48, 48, null);
                    } catch (IOException erro) {
                        System.out.println("Ocorreu um erro ao exibir o inventario do jogador 2");
                    }
                }
                g.setColor(Color.white);
                g.drawString(Integer.toString(Pontos[1]), X*24+65, Y*48+40);
            }
            for (int auxx = 0; auxx < X; auxx++)
              for (int auxy = 0; auxy < Y; auxy++)
                for (int auxz = 0; auxz < 5; auxz++) {
                    try {
                        draw(auxx, auxy, auxz, g);
                    } catch (IOException e) {
                        System.out.println("Ocorreu o erro " + e + " na impressao da imagem " + Mapa[auxx][auxy][auxz]);
                    }
                }
        }
        private void draw(int PosX, int PosY, int PosZ, Graphics g) throws IOException {
            int Sprite = Mapa[PosX][PosY][PosZ];
            if (Sprite == 20) return;
            if (Mapa[PosX][PosY][PosZ] < 0) g.drawImage(ImageIO.read(new File(Sprites[-Mapa[PosX][PosY][PosZ]])), PosX*48+48, PosY*48, -48, 48, null);
            else g.drawImage(ImageIO.read(new File(Sprites[Mapa[PosX][PosY][PosZ]])), PosX*48, PosY*48, 48, 48, null);
        }
        public void FimDeJogo() {
            Tela.repaint();
            String Vencedor;
            if (Pontos[0] == Pontos[1]) JOptionPane.showMessageDialog(new JFrame(), "Demonstracao completa. Os dois trabalharam igualmente bem, ambos sao vencedores.");
            else {
                if (Pontos[0] > Pontos[1]) Vencedor = "vermelho";
                else Vencedor = "azul";
                JOptionPane.showMessageDialog(new JFrame(), "Demonstracao completa. O vencedor e o jogador " + Vencedor +  ".");
            }
            System.exit(0);
        }
    }
}