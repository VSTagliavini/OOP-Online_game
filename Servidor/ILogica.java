import java.io.DataOutputStream;
import java.io.DataInputStream;

interface ILogica {
  void executa();
  void adicionaJog(int i, DataInputStream in, DataOutputStream out);
}