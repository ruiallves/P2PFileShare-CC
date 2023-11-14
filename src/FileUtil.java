package P2PFileShare_CC.src;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.javatuples.Pair;

public class FileUtil {
    public static List<Pair<String, Integer>> obterListaDeArquivosNaPasta(String pasta, String nodeId) {
        List<Pair<String, Integer>> listaArquivos = new ArrayList<>();

        File diretorio = new File(pasta);
        if (diretorio.exists() && diretorio.isDirectory()) {
            File[] arquivos = diretorio.listFiles();

            if (arquivos != null) {
                for (File arquivo : arquivos) {
                    if (arquivo.isFile()) {
                        String nomeArquivo = arquivo.getName();
                        int numeroChunk = determinarNumeroDoChunk(nomeArquivo);

                        if (numeroChunk >= 0) {
                            listaArquivos.add(new Pair<>(nodeId, numeroChunk));
                        }
                    }
                }
            }
        }

        return listaArquivos;
    }


    public static List<String> getFilesInDirectory(String directoryPath) {
        List<String> fileList = new ArrayList<>();

        File directory = new File(directoryPath);
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        fileList.add(file.getName());
                    }
                }
            }
        }

        return fileList;
    }

    private static int determinarNumeroDoChunk(String nomeArquivo) {

        int tamanhoMaximoPorChunk = 256;

        File arquivo = new File(nomeArquivo);
        long tamanhoArquivo = arquivo.length();

        int numeroChunk = (int) (tamanhoArquivo / tamanhoMaximoPorChunk);

        if (tamanhoArquivo % tamanhoMaximoPorChunk != 0) {
            numeroChunk++;
        }

        return numeroChunk;
    }



}
