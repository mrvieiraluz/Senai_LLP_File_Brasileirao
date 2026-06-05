import java.io.*;
import java.util.*;

public class Main {

    static class Time {
        String nome;
        int pontos = 0;
        int golsFeitos = 0;
        int golsSofridos = 0;
        int vitorias = 0;
        int empates = 0;
        int derrotas = 0;
        int jogos = 0;

        Time(String nome) {
            this.nome = nome;
        }

        int saldoDeGols() {
            return golsFeitos - golsSofridos;
        }
    }

    public static void main(String[] args) {
        Map<String, Time> tabela = new LinkedHashMap<>();

        String caminhoArquivo = "jogos.txt";

        try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo))) {
            String linha;
            int numLinha = 0;

            while ((linha = br.readLine()) != null) {
                numLinha++;
                linha = linha.trim();
                if (linha.isEmpty()) continue;

                String[] partes = linha.split(",");
                if (partes.length < 4) {
                    System.err.println("Linha " + numLinha + " ignorada (formato inválido): " + linha);
                    continue;
                }

                String nomeA = partes[1].trim().replace("_", " ");
                String nomeB = partes[2].trim().replace("_", " ");
                String resultado = partes[3].trim();

                if (!resultado.matches("\\d+x\\d+")) {
                    System.err.println("Linha " + numLinha + " ignorada (placar inválido/ADI): " + linha);
                    continue;
                }

                String[] placares = resultado.split("x");
                int golsA = Integer.parseInt(placares[0]);
                int golsB = Integer.parseInt(placares[1]);

                tabela.putIfAbsent(nomeA, new Time(nomeA));
                tabela.putIfAbsent(nomeB, new Time(nomeB));

                Time timeA = tabela.get(nomeA);
                Time timeB = tabela.get(nomeB);

                timeA.jogos++;
                timeB.jogos++;
                timeA.golsFeitos += golsA;
                timeA.golsSofridos += golsB;
                timeB.golsFeitos += golsB;
                timeB.golsSofridos += golsA;

                if (golsA > golsB) {
                    timeA.pontos += 3;
                    timeA.vitorias++;
                    timeB.derrotas++;
                } else if (golsB > golsA) {
                    timeB.pontos += 3;
                    timeB.vitorias++;
                    timeA.derrotas++;
                } else {
                    timeA.pontos += 1;
                    timeB.pontos += 1;
                    timeA.empates++;
                    timeB.empates++;
                }
            }

        } catch (FileNotFoundException e) {
            System.err.println("Arquivo não encontrado: " + caminhoArquivo);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
            System.exit(1);
        }

        List<Time> classificacao = new ArrayList<>(tabela.values());
        classificacao.sort((a, b) -> {
            if (b.pontos != a.pontos) return b.pontos - a.pontos;
            if (b.saldoDeGols() != a.saldoDeGols()) return b.saldoDeGols() - a.saldoDeGols();
            return b.golsFeitos - a.golsFeitos;
        });

        String sep = "+------+-------------------------+----+----+----+----+--------+-------+";
        System.out.println(sep);
        System.out.printf("| %-4s | %-23s | %-2s | %-2s | %-2s | %-2s | %-6s | %-5s |%n",
                "Pos", "Time", "J", "V", "E", "D", "Pontos", "Saldo");
        System.out.println(sep);

        for (int i = 0; i < classificacao.size(); i++) {
            Time t = classificacao.get(i);
            String pos = (i + 1) + "º";
            String saldo = (t.saldoDeGols() >= 0 ? "+" : "") + t.saldoDeGols();
            System.out.printf("| %-4s | %-23s | %-2d | %-2d | %-2d | %-2d | %-6d | %-5s |%n",
                    pos, t.nome, t.jogos, t.vitorias, t.empates, t.derrotas, t.pontos, saldo);
        }

        System.out.println(sep);
    }
}
