package P2PFileShare_CC.src.files;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileFolder {
    private List<FileInfo> folder;

    public FileFolder(String path) {
        List<FileInfo> listaDeArquivos = new ArrayList<>();

        File pasta = new File(path);

        if (pasta.exists() && pasta.isDirectory()) {

            File[] arquivos = pasta.listFiles();

            for (File arquivo : arquivos) {
                if (arquivo.isFile()) {
                    String nomeDoArquivo = arquivo.getName();
                    long comprimentoDoArquivo = arquivo.length();
                    String caminhoDoArquivo = arquivo.getAbsolutePath();
                    FileInfo file = new FileInfo(nomeDoArquivo, comprimentoDoArquivo,caminhoDoArquivo);
                    listaDeArquivos.add(file);
                }
            }
        } else {
            System.out.println("O diretório não existe.");
        }

        this.folder = listaDeArquivos;
    }

    public List<FileInfo> getFolder(){
        return this.folder;
    }
}
